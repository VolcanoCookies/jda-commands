package net.volcano.jdacommands.commands

import net.volcano.jdacommands.model.command.CommandEvent
import net.volcano.jdacommands.model.command.annotations.CommandController
import net.volcano.jdacommands.model.command.annotations.CommandMethod
import net.volcano.jdacommands.model.command.annotations.Help
import kotlin.system.exitProcess

@CommandController
class Kill {

	@CommandMethod(path = ["kill"], permissions = ["kill"])
	@Help(description = "Kill the instance.", category = "admin")
	fun kill(event: CommandEvent) {

		event.respond("```diff\n- Exiting -```")
			.queue { exitProcess(0) }

	}

}