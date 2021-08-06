package net.volcano.jdacommands.client

import net.volcano.jdacommands.interfaces.CommandClient
import net.volcano.jdacommands.model.command.annotations.CommandController
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

/**
 * Finds and registers all commands in the application context
 */
@Component
class CommandAttacher(
	applicationContext: ApplicationContext,
	commandClient: CommandClient
) {

	init {

		applicationContext.getBeansWithAnnotation(CommandController::class.java)
			.forEach { (_, v) ->
				commandClient.registerController(v)
			}

	}
}