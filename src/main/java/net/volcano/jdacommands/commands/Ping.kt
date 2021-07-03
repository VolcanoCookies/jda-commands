package net.volcano.jdacommands.commands

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.RestAction
import net.volcano.jdacommands.model.command.CommandEvent
import net.volcano.jdacommands.model.command.annotations.CommandController
import net.volcano.jdacommands.model.command.annotations.CommandMethod
import net.volcano.jdacommands.model.command.annotations.Help
import java.util.*

@CommandController
class Ping {

	private val random = Random()

	@CommandMethod(
		path = ["ping"],
		permissions = "command.ping"
	)
	@Help(description = "Ping the bot.")
	fun ping(event: CommandEvent): RestAction<*> {

		var res = "Pong!"
		if (random.nextDouble() < 0.01) {
			res = "Pang!"
		}

		val initial = event.message
			.timeCreated
			.toInstant()
			.toEpochMilli()

		val processTime = System.currentTimeMillis() - event.message
			.timeCreated
			.toInstant()
			.toEpochMilli()

		return event.respond(res)
			.flatMap { message: Message ->
				val response = message.timeCreated
					.toInstant()
					.toEpochMilli()
				val duration = response - initial
				message.editMessage(
					"""
	${message.contentRaw}```
	Bot: ${processTime}ms
	Api: ${duration}ms```
	""".trimIndent()
				)
			}
	}
}