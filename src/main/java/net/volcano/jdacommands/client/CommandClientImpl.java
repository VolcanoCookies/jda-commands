package net.volcano.jdacommands.client;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import net.volcano.jdacommands.config.CategoryConfig;
import net.volcano.jdacommands.config.EmoteConfig;
import net.volcano.jdacommands.constants.Reactions;
import net.volcano.jdacommands.exceptions.command.CommandCompileException;
import net.volcano.jdacommands.exceptions.command.parsing.*;
import net.volcano.jdacommands.exceptions.command.run.*;
import net.volcano.jdacommands.interfaces.*;
import net.volcano.jdacommands.model.EmbedAttachment;
import net.volcano.jdacommands.model.command.Command;
import net.volcano.jdacommands.model.command.CommandCompiler;
import net.volcano.jdacommands.model.command.CommandEvent;
import net.volcano.jdacommands.model.command.CommandNode;
import net.volcano.jdacommands.model.command.annotations.CommandController;
import net.volcano.jdacommands.model.command.arguments.ParsedData;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;
import net.volcano.jdacommands.model.command.arguments.implementation.CodecRegistryImpl;
import net.volcano.jdacommands.model.command.arguments.interfaces.CodecRegistry;
import net.volcano.jdacommands.permissions.PermissionResult;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
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
@Service
public class CommandClientImpl extends ListenerAdapter implements CommandClient {
	
	private static final Pattern aliasSplitPattern = Pattern.compile("\\S+", Pattern.MULTILINE);
	private static final Pattern argumentPattern = Pattern.compile("\"[^\"]+\"|'[^']+'|\\S+", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	
	private final Set<Command> allCommands = new HashSet<>();
	
	private final CommandNode root = new CommandNode(true);
	
	private final ScheduledThreadPoolExecutor executorService;
	
	private final PermissionProvider permissionProvider;
	private final PrefixProvider prefixProvider;
	private final UserProvider userProvider;
	private final GuildProvider guildProvider;
	
	private final CommandCompiler commandCompiler;
	private final CodecRegistry codecRegistry;
	private final PermissionClient permissionClient;
	private final InteractionClient interactionClient;
	@Nullable
	private final Extension extension;
	
	private final String ownerId;
	
	private final EmoteConfig discordConfig;
	
	public CommandClientImpl(JDA jda,
	                         PermissionProvider permissionProvider,
	                         PrefixProvider prefixProvider,
	                         UserProvider userProvider,
	                         GuildProvider guildProvider,
	                         PermissionClient permissionClient,
	                         InteractionClient interactionClient,
	                         CategoryConfig categoryConfig,
	                         EmoteConfig discordConfig,
	                         @Nullable Extension extension,
	                         ApplicationContext context) throws ExecutionException, InterruptedException {
		
		this.permissionProvider = permissionProvider;
		this.prefixProvider = prefixProvider;
		this.userProvider = userProvider;
		this.guildProvider = guildProvider;
		this.permissionClient = permissionClient;
		this.interactionClient = interactionClient;
		this.discordConfig = discordConfig;
		this.extension = extension;
		
		executorService = new ScheduledThreadPoolExecutor(1);
		executorService.setMaximumPoolSize(10);
		
		((PermissionClientImpl) this.permissionClient).setClient(this);
		
		codecRegistry = new CodecRegistryImpl(context);
		commandCompiler = new CommandCompiler(codecRegistry, categoryConfig);
		
		ownerId = jda.retrieveApplicationInfo()
				.submit()
				.get()
				.getOwner()
				.getId();
		
		log.info("Initialized command client.");
		
	}
	
	private static final Pattern INVALID_MESSAGE_REGEX = Pattern.compile("^<((a?:|@[&!]?\\d+|#\\d+)|http).*$", Pattern.DOTALL);
	
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
		if (!content.startsWith(prefix) || INVALID_MESSAGE_REGEX.matcher(content).matches()) {
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
				
				var commandEvent = new CommandEvent(this, data, extension);
				
				executeCommand(commandEvent);
				
				// Log
				User caller = event.getAuthor();
				log.info("{}[{}] ran {}", caller.getAsTag(), caller.getId(), event.getMessage().getContentRaw());
				
			} catch (ArgumentParsingException | InvalidArgumentsException e) {
				
				sendError(e, event);
				
			} catch (CommandNotFoundException e) {
				
				// Add warning to unknown commands
				if (discordConfig.getNotFoundEmoteId() != null) {
					
					var emote = event.getJDA().getEmoteById(discordConfig.getNotFoundEmoteId());
					if (emote != null) {
						event.getMessage()
								.addReaction(emote)
								.queue();
					} else {
						event.getMessage()
								.addReaction(Reactions.WARNING)
								.queue();
					}
					
				}
				
			} catch (MissingPermissionsException e) {
				
				if (discordConfig.getNoPermissionsEmoteId() != null) {
					
					var emote = event.getJDA().getEmoteById(discordConfig.getNoPermissionsEmoteId());
					if (emote != null) {
						event.getMessage()
								.addReaction(emote)
								.queue();
					} else {
						event.getMessage()
								.addReaction(Reactions.NO_PERMISSIONS)
								.queue();
					}
					
				}
				
			} catch (PermissionsOnCooldownException e) {
				
				if (discordConfig.getCooldownEmoteId() != null) {
					
					var emote = event.getJDA().getEmoteById(discordConfig.getCooldownEmoteId());
					if (emote != null) {
						event.getMessage()
								.addReaction(emote)
								.queue();
					} else {
						event.getMessage()
								.addReaction(Reactions.NO_PERMISSIONS)
								.queue();
					}
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
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
	private ParsedData parseAny(List<Command> commands, ArgumentParsingData argumentData) throws CommandException,
			CommandNotFoundException {
		
		// The latest fully parsed command,
		// where the last argument is a take-all
		int longestParsedWithError = 0;
		CommandException exception = null;
		
		// Sort commands by longest argument chain first,
		// And take all second
		// This lets us return immediately when we parse successfully.
		commands = commands.stream()
				.sorted(Command.argumentComparator())
				.collect(Collectors.toList());
		
		for (Command command : commands) {
			
			ArgumentParsingData parsingData = argumentData.clone();
			parsingData.command = command;
			
			try {
				
				return command.parseArguments(parsingData);
				
			} catch (MissingArgumentsException | TooManyArgumentsException e) {
				if (e.getData().rawArguments[e.getArgumentIndex()].startIndex > longestParsedWithError) {
					longestParsedWithError = e.getData().rawArguments[e.getArgumentIndex()].startIndex;
					exception = e;
				}
			} catch (InvalidArgumentsException e) {
				if (e.getErrorStartIndex() > longestParsedWithError) {
					longestParsedWithError = e.getErrorStartIndex();
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
	
	@Override
	public CommandNode getRootCommandNode() {
		return root;
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
			change = root.addCommand(command, path.split(" ")) || change;
			log.info("Registered command at " + path);
		}
		allCommands.add(command);
		return change;
	}
	
	/**
	 * Register a {@link CommandController}, thus registering all of its commands
	 *
	 * @param controller the {@link CommandController} annotated object
	 * @return whether or not a change occurred to the command set
	 */
	@Override
	public boolean registerController(Object controller) throws CommandCompileException {
		boolean change = false;
		for (Command command : commandCompiler.compile(controller)) {
			change = registerCommand(command) || change;
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
	public ParsedData findAndParse(MessageReceivedEvent event, String content) throws CommandNotFoundException,
			CommandException {
		
		// Get the path
		Matcher matcher = aliasSplitPattern.matcher(content);
		List<String> path = new ArrayList<>();
		List<Integer> pathIndexes = new ArrayList<>();
		while (matcher.find()) {
			String token = matcher.group();
			if ((token.startsWith("'") || token.startsWith("\"")) &&
					token.length() > 2) {
				token = token.substring(1, token.length() - 1);
			}
			path.add(token);
			pathIndexes.add(matcher.start());
		}
		
		var pair = root.findCommands(path.toArray(new String[0]));
		
		Set<Command> commands = pair.getFirst();
		
		// Check if there are any possible command candidates
		if (commands.isEmpty()) {
			throw new CommandNotFoundException();
		}
		
		var divider = pair.getSecond() == 0 ? content.length() : pathIndexes.get(path.size() - pair.getSecond());
		
		var rawPrefix = event.getMessage().getContentRaw().substring(0, event.getMessage().getContentRaw().length() - content.length());
		var rawPath = content.substring(0, divider);
		var rawArguments = content.substring(divider);
		
		var parsingData = new ArgumentParsingData(event, rawPrefix, rawPath, rawArguments);
		
		if (commands.size() > 1) {
			List<Command> permittedCommands = new ArrayList<>();
			for (Command command : commands) {
				if (permissionClient.checkPermissions(command.permission, event.getAuthor(), event.isFromGuild() ? event.getGuild() : null, event.isFromGuild() ? event.getTextChannel() : null).getHasPermissions()) {
					permittedCommands.add(command);
				}
			}
			
			// If there are no permitted commands
			// Add all non-permitted to get the closest matching argument wise to know what missing permissions to return.
			if (permittedCommands.isEmpty()) {
				permittedCommands.addAll(commands);
			}
			
			List<Command> possibleCommands = new ArrayList<>();
			for (Command command : permittedCommands) {
				if (parsingData.size() == command.getArguments().size() ||
						(command.getArguments().lastIsArbitraryNumber && parsingData.size() >= command.getArguments().size())) {
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
			
			try {
				
				var any = parseAny(possibleCommands, parsingData);
				// Check if the user has permissions for this command
				
				if (!any.command.getBotOwnerCanAlwaysExecute() || !event.getAuthor().getId().equals(getOwnerId())) {
					checkPermissions(event, any.command);
				}
				
				return any;
			} catch (CommandException e) {
				
				var command = e.getCommand();
				if (!command.getBotOwnerCanAlwaysExecute() || !event.getAuthor().getId().equals(getOwnerId())) {
					checkPermissions(event, command);
				}
				
				throw e;
				
			}
			
		} else {
			// Exactly one command
			Command command = commands.iterator().next();
			if (!command.getBotOwnerCanAlwaysExecute() || !event.getAuthor().getId().equals(getOwnerId())) {
				checkPermissions(event, command);
			}
			
			return command.parseArguments(parsingData);
		}
		
	}
	
	@Override
	public void executeCommand(CommandEvent event) {
		
		boolean botOwnerOverriding = false;
		
		PermissionResult result = null;
		
		if (event.command.getBotOwnerCanAlwaysExecute()) {
			if (ownerId.equals(event.getAuthor().getId())) {
				botOwnerOverriding = true;
			}
		}
		
		if (!botOwnerOverriding) {
			// Check if the user has permissions
			result = permissionClient.checkPermissions(event.command.permission, event.getAuthor(), event.getGuild(), event.getTextChannel());
			if (!result.getHasPermissions()) {
				try {
					checkPermissions(event, event.command);
				} catch (PermissionsOnCooldownException | MissingPermissionsException e) {
					terminate(event, e);
					return;
				}
			}
		}
		
		// Check if the command is from the correct source
		if ((event.isFromGuild() && event.command.getSource() == Command.Source.PRIVATE) ||
				(!event.isFromGuild() && event.command.getSource() == Command.Source.GUILD)) {
			terminate(event, new IncorrectSourceException(event.command, event.command.getSource()));
			return;
		}
		
		try {
			log.info(event.getAuthor().getAsTag() + " ran " + event.getMessage().getContentDisplay());
			RestAction<?> action = event.command.call(event);
			if (action != null) {
				action.queue();
			}
			if (!botOwnerOverriding) {
				permissionClient.invokeCooldown(event.getAuthor(), event.getGuild(), result.getHolder());
			}
		} catch (CommandException | CommandRuntimeException e) {
			terminate(event, e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void terminate(CommandEvent event, Throwable t) {
		
		if (t instanceof MissingPermissionsException) {
			
			if (discordConfig.getNoPermissionsEmoteId() != null) {
				
				var emote = event.getJDA().getEmoteById(discordConfig.getNoPermissionsEmoteId());
				if (emote != null) {
					event.getMessage()
							.addReaction(emote)
							.queue();
				} else {
					event.getMessage()
							.addReaction(Reactions.NO_PERMISSIONS)
							.queue();
				}
				
			}
			
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
					.flatMap(c -> {
						var action = c.sendMessageEmbeds(e.getErrorEmbed().build());
						for (EmbedAttachment attachment : e.getAttachments()) {
							action = action.addFile(attachment.inputStream, attachment.name);
						}
						return action;
					})
					.queue();
		} else {
			
			var action = event.getChannel().sendMessageEmbeds(e.getErrorEmbed().build());
			for (EmbedAttachment attachment : e.getAttachments()) {
				action = action.addFile(attachment.inputStream, attachment.name);
			}
			
			action.queue();
		}
	}
	
	private void checkPermissions(MessageReceivedEvent event, Command command) throws PermissionsOnCooldownException, MissingPermissionsException {
		var guild = event.isFromGuild() ? event.getGuild() : null;
		var queryResult = permissionClient.checkPermissions(command.permission, event.getAuthor(), guild, event.isFromGuild() ? event.getTextChannel() : null);
		if (!queryResult.getHasPermissions()) {
			if (queryResult.getExpiration() != null) {
				throw new PermissionsOnCooldownException(command, guild, command.permission, queryResult.getExpiration());
			} else {
				throw new MissingPermissionsException(command, guild, command.permission);
			}
		}
	}
	
	@Override
	@Bean
	public CodecRegistry getCodecRegistry() {
		return codecRegistry;
	}
}
