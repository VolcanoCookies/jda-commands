package net.volcano.jdacommands.client;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import net.volcano.jdacommands.constants.Reactions;
import net.volcano.jdacommands.exceptions.command.parsing.*;
import net.volcano.jdacommands.exceptions.command.run.CommandException;
import net.volcano.jdacommands.exceptions.command.run.CommandRuntimeException;
import net.volcano.jdacommands.exceptions.command.run.IncorrectSourceException;
import net.volcano.jdacommands.exceptions.command.run.MissingPermissionsException;
import net.volcano.jdacommands.interfaces.*;
import net.volcano.jdacommands.model.command.Command;
import net.volcano.jdacommands.model.command.CommandCompiler;
import net.volcano.jdacommands.model.command.CommandEvent;
import net.volcano.jdacommands.model.command.CommandNode;
import net.volcano.jdacommands.model.command.annotations.CommandController;
import net.volcano.jdacommands.model.command.arguments.ParsedData;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;
import net.volcano.jdacommands.model.command.arguments.implementation.CodecRegistryImpl;
import net.volcano.jdacommands.model.command.arguments.interfaces.CodecRegistry;
import net.volcano.jdautils.constants.Colors;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class CommandClientImpl extends ListenerAdapter implements CommandClient {
	
	private static final Pattern aliasSplitPattern = Pattern.compile("\\S+", Pattern.MULTILINE);
	private static final Pattern argumentPattern = Pattern.compile("\"[^\"]+\"|'[^']+'|\\S+", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	
	private final Set<Command> commands = new HashSet<>();
	
	private final CommandNode root = new CommandNode();
	
	private final ScheduledThreadPoolExecutor executorService;
	
	private final PermissionProvider permissionProvider;
	private final PrefixProvider prefixProvider;
	private final UserProvider userProvider;
	private final GuildProvider guildProvider;
	
	private final CommandCompiler commandCompiler;
	private final CodecRegistry codecRegistry;
	
	private final ReactionMenuClient reactionMenuClient;
	
	private final Font font;
	private final FontMetrics metrics;
	
	private final String ownerId;
	
	public CommandClientImpl(JDA jda,
	                         PermissionProvider permissionProvider,
	                         PrefixProvider prefixProvider,
	                         UserProvider userProvider,
	                         GuildProvider guildProvider,
	                         ReactionMenuClient reactionMenuClient) throws IOException, FontFormatException, ExecutionException, InterruptedException {
		
		this.permissionProvider = permissionProvider;
		this.prefixProvider = prefixProvider;
		this.userProvider = userProvider;
		this.guildProvider = guildProvider;
		this.reactionMenuClient = reactionMenuClient;
		
		executorService = new ScheduledThreadPoolExecutor(1);
		executorService.setMaximumPoolSize(10);
		
		codecRegistry = new CodecRegistryImpl();
		commandCompiler = new CommandCompiler(codecRegistry);
		
		codecRegistry.loadDefaults();
		
		InputStream stream = new FileInputStream("Montserrat-Regular.ttf");
		font = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(48f);
		metrics = new BufferedImage(1, 1, 1)
				.createGraphics()
				.getFontMetrics(font);
		
		ownerId = jda.retrieveApplicationInfo()
				.submit()
				.get()
				.getOwner()
				.getId();
		
	}
	
	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		
		// Only accept commands from regular users
		if (event.isWebhookMessage() || event.getAuthor().isBot()) {
			return;
		}
		
		// Initialize prefix
		String prefix = event.isFromGuild() ? prefixProvider.getPrefix(event.getGuild()) : prefixProvider.getDefault();
		
		// The message content
		String content = event.getMessage().getContentRaw();
		
		// Check if the message starts with the prefix
		if (!content.startsWith(prefix)) {
			return;
		}
		
		// Remove the prefix to find the command
		content = content.replaceFirst(prefix, "").trim();
		
		String finalContent = content;
		executorService.execute(() -> {
			
			try {
				
				// Check if the user is command banned
				if (userProvider.isCommandBanned(event.getAuthor())) {
					return;
				}
				
				var data = findAndParse(event, finalContent);
				
				var commandEvent = new CommandEvent(this, data);
				
				executeCommand(commandEvent);
				
				// Log
				User caller = event.getAuthor();
				log.info("{}[{}] ran {}", caller.getAsTag(), caller.getId(), event.getMessage().getContentRaw());
				
			} catch (ArgumentParsingException e) {
				
				sendError(e, event);
				
			} catch (CommandNotFoundException e) {
				
				// Add warning to unknown commands
				event.getMessage()
						.addReaction(Reactions.WARNING)
						.queue();
				
			}
			
		});
	}
	
	/**
	 * Parse the arguments for the commands with the base data,
	 * return the data that accepted the most arguments
	 * <p>
	 * If no parsing finished completely,
	 * return the error of the longest successful chain
	 *
	 * @param commands     the command list to parse
	 * @param argumentData the  data to parse
	 * @return the parsed data
	 */
	private ParsedData parseAny(List<Command> commands, ArgumentParsingData argumentData) throws ArgumentParsingException,
			CommandNotFoundException {
		
		// The latest fully parsed command,
		// where the last argument is a take-all
		int longestParsedWithError = 0;
		ArgumentParsingException exception = null;
		
		// Sort commands by longest argument chain first,
		// And take all second
		// This lets us return immediately when we parse successfully.
		commands = commands.stream()
				.sorted(Command.argumentComparator())
				.collect(Collectors.toList());
		
		for (Command command : commands) {
			
			ArgumentParsingData parsingData = argumentData.clone();
			
			try {
				
				return command.parseArguments(parsingData);
				
			} catch (MissingArgumentsException | InvalidArgumentsException | TooManyArgumentsException e) {
				if (e.getArgumentIndex() > longestParsedWithError) {
					longestParsedWithError = parsingData.currentArg;
					exception = e;
				}
			}
			
		}
		
		if (exception != null) {
			throw exception;
		} else {
			throw new CommandNotFoundException();
		}
		
	}
	
	/**
	 * Register a {@link Command}
	 *
	 * @param command the {@link Command} to register
	 * @return whether or not a change occurred to the command set
	 */
	@Override
	public boolean registerCommand(Command command) {
		boolean change = false;
		for (String path : command.getPaths()) {
			change = change || root.addCommand(command, path);
		}
		return change;
	}
	
	/**
	 * Register a {@link CommandController}, thus registering all of its commands
	 *
	 * @param controller the {@link CommandController} annotated object
	 * @return whether or not a change occurred to the command set
	 */
	@Override
	public boolean registerController(Object controller) {
		boolean change = false;
		for (Command command : commandCompiler.compile(controller)) {
			change = change || registerCommand(command);
		}
		return change;
	}
	
	/**
	 * Find what command the user tried to run.
	 * <p>
	 * Some commands can have the exact same path and only differ in their arguments.
	 * This method tries to figure out which exact command the user tried to run,
	 * and if none found,
	 * tries to find the closest one to provide the best possible error message.
	 * <p>
	 * This lets us have commands such as:
	 * <br>
	 * <b>!Help</b>
	 * <br>
	 * <b>!Help &lt page &gt</b>
	 * <br>
	 * Where both commands will be correctly resolved depending on if the user provided an argument or not.
	 *
	 * @param event   the event with the content correctly prefixed
	 * @param content the content, with the prefix removed
	 * @return The best matching command for this content
	 * @throws CommandNotFoundException If no command was found for this path
	 * @throws ArgumentParsingException If there was an error parsing all of the candidate commands
	 */
	@Override
	public ParsedData findAndParse(MessageReceivedEvent event, String content) throws CommandNotFoundException,
			ArgumentParsingException {
		
		// Get the path
		Matcher matcher = aliasSplitPattern.matcher(content);
		List<String> path = new ArrayList<>();
		while (matcher.find()) {
			String token = matcher.group();
			if ((token.startsWith("'") || token.startsWith("\"")) &&
					token.length() > 2) {
				token = token.substring(1, token.length() - 1);
			}
			path.add(token);
		}
		
		Set<Command> commands = root.findCommands(path.toArray(new String[0]));
		
		// Check if there are any possible command candidates
		if (commands.isEmpty()) {
			throw new CommandNotFoundException();
		}
		
		var parsingData = new ArgumentParsingData(event, content.substring(matcher.end()));
		
		if (commands.size() > 1) {
			List<Command> possibleCommands = new ArrayList<>();
			for (Command command : commands) {
				if (parsingData.size() == command.getArguments().size()) {
					possibleCommands.add(command);
				}
			}
			
			// If there are no commands with the exact number of arguments as provided,
			// check if there are any commands with less arguments but with a "take-all" argument.
			if (possibleCommands.isEmpty()) {
				possibleCommands.addAll(commands);
			}
			
			// If there still are no possible commands,
			// add all commands, parsing will fail but we will get a better error message
			if (possibleCommands.isEmpty()) {
				possibleCommands.addAll(commands);
			}
			
			// Parse arguments to see which one gets further
			for (Command command : possibleCommands) {
				command.getArguments()
						.parseArguments(parsingData.clone());
			}
			
			return parseAny(possibleCommands, parsingData);
		} else {
			// Exactly one command
			Command command = commands.iterator().next();
			return command.parseArguments(parsingData);
		}
		
	}
	
	@Override
	public void executeCommand(CommandEvent event) {
		
		if (!event.command.getBotOwnerCanAlwaysExecute() && !ownerId.equals(event.getAuthor().getId())) {
			// Check if the user has permissions
			if (!permissionProvider.hasPermissions(event.command.getPermissions(), event.getAuthor(), event.isFromGuild() ? event.getGuild() : null)) {
				Set<String> missingPermissions = new HashSet<>(event.command.getPermissions());
				missingPermissions.removeAll(permissionProvider.getPermissions(event.getAuthor(), event.isFromGuild() ? event.getGuild() : null));
				terminate(event, new MissingPermissionsException(missingPermissions, event.isFromGuild() ? event.getGuild() : null));
				return;
			}
		}
		
		// Check if the command is from the correct source
		if ((event.isFromGuild() && event.command.getSource() == Command.Source.PRIVATE) ||
				(!event.isFromGuild() && event.command.getSource() == Command.Source.GUILD)) {
			terminate(event, new IncorrectSourceException(event.command.getSource()));
			return;
		}
		
		try {
			RestAction<?> action = event.command.call(event);
			if (action != null) {
				action.queue();
			}
		} catch (InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
		} catch (CommandRuntimeException e) {
			terminate(event, e);
		}
		
	}
	
	@Override
	public void terminate(CommandEvent event, Throwable t) {
		
		if (t instanceof MissingPermissionsException) {
			event.getMessage()
					.addReaction(Reactions.NO_PERMISSIONS)
					.queue();
		} else if (t instanceof IncorrectSourceException) {
			event.respondError("This command can only be run as a " + ((IncorrectSourceException) t)
					.getRequiredSource() + " message.")
					.queue();
		} else if (t instanceof CommandRuntimeException) {
			event.respond(((CommandRuntimeException) t)
					.getErrorEmbed())
					.queue();
		}
		
	}
	
	/**
	 * Send a command run error to a user
	 *
	 * @param e     the error to send
	 * @param event the event which caused this error
	 */
	private void sendError(CommandException e, MessageReceivedEvent event) {
		if (e.isSensitive()) {
			event.getAuthor()
					.openPrivateChannel()
					.flatMap(c -> c.sendMessage(e.getErrorEmbed()
							.build()))
					.queue();
		} else {
			event.getChannel()
					.sendMessage(e.getErrorEmbed()
							.build())
					.queue();
		}
	}
	
	private BufferedImage generateErrorImage(String message, int errorStart, int errorLength) {
		
		var pre = message.substring(0, errorStart);
		var err = message.substring(errorStart, errorStart + errorLength);
		var post = message.substring(errorStart + errorLength);
		
		var startLen = metrics.stringWidth(pre);
		var errorLen = metrics.stringWidth(err);
		
		var width = metrics.stringWidth(message);
		var height = metrics.getHeight();
		
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		var graphics = image.createGraphics();
		graphics.setFont(font);
		graphics.setRenderingHints(rh);
		
		Color backgroundColor = new Color(0x23272a);
		
		graphics.setColor(backgroundColor);
		graphics.fillRect(0, 0, width, height);
		graphics.setColor(Color.WHITE);
		graphics.drawString(pre, 0, height - metrics.getDescent());
		graphics.setColor(Colors.ERROR);
		graphics.drawString(err, startLen, height - metrics.getDescent());
		graphics.setColor(Color.WHITE);
		graphics.drawString(post, startLen + errorLen, height - metrics.getDescent());
		
		graphics.dispose();
		
		return image;
	}
	
}
