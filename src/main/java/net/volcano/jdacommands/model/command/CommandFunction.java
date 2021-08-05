package net.volcano.jdacommands.model.command;

import lombok.Builder;
import net.dv8tion.jda.api.requests.RestAction;
import net.volcano.jdacommands.exceptions.command.run.CommandException;
import net.volcano.jdacommands.model.command.arguments.ParsedData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Builder
public class CommandFunction {
	
	protected final Method method;
	
	protected final boolean includeEvent;
	
	protected final int argumentCount;
	
	protected final Object instance;
	
	protected RestAction<?> invoke(CommandEvent event, ParsedData data) throws IllegalAccessException, CommandException {
		
		Object[] args = new Object[argumentCount];
		
		int i = 0;
		if (includeEvent) {
			args[i++] = event;
		}
		for (; i < argumentCount; i++) {
			args[i] = data.parsedArguments[includeEvent ? i - 1 : i];
		}
		
		try {
			Object returned = method.invoke(instance, args);
			if (returned instanceof RestAction) {
				return (RestAction<?>) returned;
			}
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof CommandException) {
				throw (CommandException) e.getTargetException();
			} else {
				e.getTargetException().printStackTrace();
			}
		}
		return null;
	}
	
}
