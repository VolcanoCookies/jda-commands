package net.volcano.jdacommands.model.command;

import net.dv8tion.jda.api.requests.RestAction;
import net.volcano.jdacommands.config.CategoryConfig;
import net.volcano.jdacommands.exceptions.command.CommandCompileException;
import net.volcano.jdacommands.model.ParameterData;
import net.volcano.jdacommands.model.command.annotations.BotOwnerCanAlwaysExecute;
import net.volcano.jdacommands.model.command.annotations.CommandController;
import net.volcano.jdacommands.model.command.annotations.CommandMethod;
import net.volcano.jdacommands.model.command.annotations.Help;
import net.volcano.jdacommands.model.command.arguments.ArgumentList;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.CodecRegistry;
import net.volcano.jdautils.utils.ClassUtil;

import java.lang.reflect.Method;
import java.util.*;

public class CommandCompiler {
	
	private final CodecRegistry registry;
	private final CategoryConfig config;
	
	public CommandCompiler(CodecRegistry registry, CategoryConfig config) {
		this.registry = registry;
		this.config = config;
	}
	
	public Set<Command> compile(Object controller) throws CommandCompileException {
		
		CommandController module = controller.getClass().getAnnotation(CommandController.class);
		
		if (module == null) {
			throw new IllegalArgumentException("Object is not a command!");
		}
		
		var methods = controller.getClass().getDeclaredMethods();
		
		Set<Command> set = new HashSet<>();
		for (Method method : controller.getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(CommandMethod.class)) {
				var command = compileCommand(controller, module, method);
				command.arguments.command = command;
				set.add(command);
			}
		}
		return set;
		
	}
	
	public Command compileCommand(Object o, CommandController controller, Method method) throws CommandCompileException {
		
		CommandMethod commandMethod = method.getAnnotation(CommandMethod.class);
		Command.CommandBuilder builder = Command.builder();
		
		if (method.getReturnType() != Void.TYPE && method.getReturnType() != RestAction.class) {
			throw new CommandCompileException(method, "Command method needs to return RestAction or Void!");
		}
		
		List<String> paths = new ArrayList<>();
		for (String controllerPath : controller.path()) {
			for (String commandPath : commandMethod.path()) {
				String path = controllerPath + " " + commandPath;
				path = path.replaceAll(" +", " ").trim();
				if (path.length() == 0) {
					throw new CommandCompileException(method, "Command path cannot be empty");
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
			
			Class<?> actualType = params[i].getType();
			if (isArray) {
				actualType = actualType.componentType();
			}
			
			var isEnum = actualType.isEnum();
			var isPrimitive = actualType.isPrimitive();
			//var isGeneric = ((ParameterizedType) ((ParameterizedType) params[i].getType().getGenericSuperclass()).getActualTypeArguments()[0]).getActualTypeArguments()[0] instanceof WildcardType;
			
			if (isArray && i != params.length - 1) {
				throw new CommandCompileException(method, "Cannot have array argument as non last parameter");
			}
			
			Class<?> codecType;
			if (isEnum) {
				codecType = Enum.class;
			} else if (isPrimitive) {
				codecType = ClassUtil.dePrimitivize(params[i].getType());
			} else {
				codecType = actualType;
			}
			
			var codec = registry.getCodec(codecType);
			
			if (codec == null) {
				throw new CommandCompileException(method, "Unsupported type for command argument; " + codecType);
			} else {
				var data = new ParameterData(params[i], actualType, codecType, registry);
				arguments.add(codec.encodeArgument(data));
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
			
			helpBuilder.usage(help.usage().equals("GENERATE") ? paths.get(0) + " " + argumentList.generateUsage() : help.usage());
			helpBuilder.description(help.description());
			var category = "";
			if (help.category().equals("DEFAULT")) {
				category = controller.category();
			} else {
				category = help.category();
			}
			helpBuilder.category(category);
			if (config.emojis.containsKey(category.toLowerCase())) {
				helpBuilder.emoji(config.emojis.get(category));
			} else {
				throw new CommandCompileException(method, "No emoji for category " + category);
			}
			helpBuilder.examples(help.examples());
			helpBuilder.details(help.details());
			helpBuilder.permissions(Arrays.stream(help.permissions())
					.map(p -> "`" + p.permission() + "` : " + p.description())
					.toArray(String[]::new));
			
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
		var permissions = controller.permissions();
		if (!permissions.isBlank() && !commandMethod.permissions().isBlank()) {
			permissions += ".";
		}
		permissions += commandMethod.permissions();
		if (permissions.isEmpty()) {
			throw new CommandCompileException(method, "Command permissions are empty, set some and provide them by default instead.");
		} else if (!permissions.startsWith("command.")) {
			permissions = "command." + permissions;
		}
		builder.permission(permissions);
		
		// Add command source
		if (commandMethod.source() == Command.Source.DEFAULT) {
			if (controller.source() == Command.Source.DEFAULT) {
				builder.source(Command.Source.BOTH);
			} else {
				builder.source(controller.source());
			}
		} else {
			builder.source(commandMethod.source());
		}
		
		// Add misc values
		builder.sensitive(commandMethod.sensitive());
		builder.globalPermissions(commandMethod.global());
		
		builder.botOwnerCanAlwaysExecute(method.isAnnotationPresent(BotOwnerCanAlwaysExecute.class));
		
		return builder.build();
		
	}
	
}
