package net.volcano.jdacommands.client;

import lombok.extern.slf4j.Slf4j;
import net.volcano.jdacommands.interfaces.CommandClient;
import net.volcano.jdacommands.model.command.annotations.CommandController;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Finds and registers all commands in the application context
 */

@Slf4j
@Component
public class CommandAttacher {
	
	public CommandAttacher(ApplicationContext applicationContext,
	                       CommandClient commandClient) {
		
		applicationContext.getBeansWithAnnotation(CommandController.class).forEach((name, o) -> {
			commandClient.registerController(o);
		});
		
	}
}
