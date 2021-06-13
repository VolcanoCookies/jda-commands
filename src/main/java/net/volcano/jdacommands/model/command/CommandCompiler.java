package net.volcano.jdacommands.model.command;

import net.dv8tion.jda.api.requests.RestAction;
import net.volcano.jdacommands.exceptions.command.CommandCompileException;
import net.volcano.jdacommands.model.command.annotations.BotOwnerCanAlwaysExecute;
import net.volcano.jdacommands.model.command.annotations.CommandController;
import net.volcano.jdacommands.model.command.annotations.CommandMethod;
import net.volcano.jdacommands.model.command.annotations.Help;
import net.volcano.jdacommands.model.command.arguments.ArgumentList;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.CodecRegistry;
import net.volcano.jdautils.utils.ClassUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandCompiler {
	
	private final CodecRegistry registry;
	
	public CommandCompiler(CodecRegistry registry) {
		this.registry = registry;
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
				set.add(compileCommand(controller, module, method));
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
			var isEnum = params[i].getType().isEnum();
			var isPrimitive = params[i].getType().isPrimitive();
			//var isGeneric = ((ParameterizedType) ((ParameterizedType) params[i].getType().getGenericSuperclass()).getActualTypeArguments()[0]).getActualTypeArguments()[0] instanceof WildcardType;
			
			if (isArray && i != params.length - 1) {
				throw new CommandCompileException(method, "Cannot have array argument as non last parameter");
			}
			
			Class<?> type;
			if (isArray) {
				type = params[i].getType().componentType();
			} else if (isEnum) {
				type = Enum.class;
			} else if (isPrimitive) {
				type = ClassUtil.dePrimitivize(params[i].getType());
			} else {
				type = params[i].getType();
				
			}
			
			var codec = registry.getCodec(type);
			if (codec == null) {
				throw new CommandCompileException(method, "Unsupported type for command argument; " + type);
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
			
			helpBuilder.usage(help.usage().equals("GENERATE") ? paths.get(0) + " " + argumentList.generateUsage() : help.usage());
			helpBuilder.description(help.description());
			helpBuilder.category(help.category());
			helpBuilder.examples(help.examples());
			helpBuilder.details(help.details());
			
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
		builder.permission(permissions);
		
		if (permissions.isEmpty()) {
			throw new CommandCompileException(method, "Command permissions are empty, set some and provide them by default instead.");
		}
		
		// Add misc values
		builder.source(commandMethod.source());
		builder.sensitive(commandMethod.sensitive());
		builder.globalPermissions(commandMethod.global());
		
		builder.botOwnerCanAlwaysExecute(method.isAnnotationPresent(BotOwnerCanAlwaysExecute.class));
		
		return builder.build();
		
	}
	
}
