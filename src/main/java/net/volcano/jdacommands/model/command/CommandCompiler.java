package net.volcano.jdacommands.model.command;

import net.dv8tion.jda.api.requests.RestAction;
import net.volcano.jdacommands.model.command.annotations.*;
import net.volcano.jdacommands.model.command.arguments.ArgumentList;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.CodecRegistry;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class CommandCompiler {
	
	private final CodecRegistry registry;
	
	public CommandCompiler(CodecRegistry registry) {
		this.registry = registry;
	}
	
	public Set<Command> compile(Object controller) {
		
		CommandController module = controller.getClass().getAnnotation(CommandController.class);
		
		if (module == null) {
			throw new IllegalArgumentException("Object is not a command!");
		}
		
		return Arrays.stream(controller.getClass()
				.getMethods())
				.filter(method -> method.isAnnotationPresent(CommandMethod.class))
				.map(method -> compileCommand(controller, module, method))
				.collect(Collectors.toSet());
		
	}
	
	public Command compileCommand(Object o, CommandController controller, Method method) {
		
		CommandMethod commandMethod = method.getAnnotation(CommandMethod.class);
		Command.CommandBuilder builder = Command.builder();
		
		if (method.getReturnType() != Void.TYPE && method.getReturnType() != RestAction.class) {
			throw new IllegalArgumentException("Command method needs to return RestAction or Void!");
		}
		
		List<String> paths = new ArrayList<>();
		for (String controllerPath : controller.path()) {
			for (String commandPath : commandMethod.path()) {
				String path = controllerPath + " " + commandPath;
				path = path.replaceAll(" +", " ").trim();
				if (path.length() == 0) {
					throw new IllegalArgumentException("Command path cannot be empty");
				}
				paths.add(path);
			}
		}
		builder.paths(paths.toArray(new String[0]));
		
		// Build the arguments for this command
		List<CommandArgument> arguments = new ArrayList<>();
		boolean firstIsEvent = false;
		boolean lastIsArray = false;
		var params = method.getParameters();
		for (int i = 0; i < params.length; i++) {
			
			if (i == 0 && params[i].getType() == CommandEvent.class) {
				firstIsEvent = true;
				continue;
			}
			
			var isArray = params[i].getType().isArray();
			
			if (isArray && i != params.length - 1) {
				throw new IllegalArgumentException("Cannot have array argument as non last parameter");
			}
			
			var type = isArray ? params[i].getType().componentType() : params[i].getType();
			
			var codec = registry.getCodec(type);
			if (codec == null) {
				throw new IllegalArgumentException("Unsupported type for command argument; " + type);
			} else {
				arguments.add(codec.encodeArgument(params[i], type));
			}
			
			if (isArray) {
				lastIsArray = true;
			}
			
		}
		
		ArgumentList argumentList = new ArgumentList(arguments, lastIsArray);
		builder.arguments(argumentList);
		
		// Build command help
		
		if (method.isAnnotationPresent(Help.class)) {
			Command.Help.HelpBuilder helpBuilder = Command.Help.builder();
			
			Help help = method.getAnnotation(Help.class);
			
			helpBuilder.usage(help.usage().equals("GENERATE") ? argumentList.generateUsage() : help.usage());
			helpBuilder.description(help.description());
			helpBuilder.category(help.category());
			
			if (method.isAnnotationPresent(DetailedHelp.class)) {
				DetailedHelp detailedHelp = method.getAnnotation(DetailedHelp.class);
				
				helpBuilder.details(detailedHelp.text());
				helpBuilder.examples(detailedHelp.examples());
			}
			
			builder.help(helpBuilder.build());
		}
		
		// Build the command method for this command
		CommandFunction.CommandFunctionBuilder methodBuilder = CommandFunction.builder();
		
		methodBuilder.method(method);
		methodBuilder.argumentCount(arguments.size() + (firstIsEvent ? 1 : 0));
		methodBuilder.includeEvent(firstIsEvent);
		methodBuilder.instance(o);
		
		builder.method(methodBuilder.build());
		
		// Build the permissions for this command
		Set<String> permissions = new HashSet<>();
		permissions.addAll(Arrays.asList(commandMethod.permissions()));
		permissions.addAll(Arrays.asList(controller.permissions()));
		builder.permissions(Collections.unmodifiableSet(permissions));
		
		// Add misc values
		builder.source(commandMethod.source());
		builder.sensitive(commandMethod.sensitive());
		builder.globalPermissions(commandMethod.global());
		
		if (method.isAnnotationPresent(BotOwnerCanAlwaysExecute.class)) {
			builder.botOwnerCanAlwaysExecute(true);
		} else {
			builder.botOwnerCanAlwaysExecute(false);
		}
		
		return builder.build();
		
	}
	
}
