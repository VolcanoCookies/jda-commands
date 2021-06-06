package net.volcano.jdacommands.client;

import lombok.extern.slf4j.Slf4j;
import net.volcano.jdacommands.exceptions.command.CommandCompileException;
import net.volcano.jdacommands.interfaces.CommandClient;
import net.volcano.jdacommands.model.command.annotations.CommandController;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Finds and registers all commands in the application context
 */

@Slf4j
@Component
public class CommandAttacher {
	
	public CommandAttacher(ApplicationContext applicationContext,
	                       CommandClient commandClient) throws CommandCompileException {
		
		for (Map.Entry<String, Object> entry : applicationContext.getBeansWithAnnotation(CommandController.class).entrySet()) {
			String name = entry.getKey();
			Object o = entry.getValue();
			commandClient.registerController(o);
		}
		
	}
}
