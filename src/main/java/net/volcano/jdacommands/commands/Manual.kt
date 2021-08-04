package net.volcano.jdacommands.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.requests.RestAction
import net.volcano.jdacommands.model.command.CommandEvent
import net.volcano.jdacommands.model.command.annotations.CommandController
import net.volcano.jdacommands.model.command.annotations.CommandMethod
import net.volcano.jdacommands.model.command.annotations.Help
import net.volcano.jdacommands.model.interaction.pager.EmbedEntirePagerBuilder
import net.volcano.jdautils.constants.Colors
import net.volcano.jdautils.utils.asString
import net.volcano.jdautils.utils.capitalize
import java.time.Instant

@CommandController
class Manual {

	@CommandMethod(
		path = ["manual", "man"],
		permissions = "command.manual"
	)
	@Help(
		description = "Show more detailed information about commands."
	)
	fun manual(event: CommandEvent, command: String): RestAction<*>? {

		val strings = command.split(" ")

		val commands = event.client.rootCommandNode.findCommands(*strings.toTypedArray()).first
			.filter { it.help != null }

		if (commands.isEmpty())
			return event.respondError("No commands found.")

		val pager = EmbedEntirePagerBuilder()
		pager.setColor(Colors.INFO)
		pager.setEmbeds(
			commands.map {

				val embed = EmbedBuilder()
				embed.setTitle("Manual: ${it.paths[0].capitalize()}")
				embed.setTimestamp(Instant.now())
				embed.setColor(Colors.INFO)

				val help = it.help
				embed.addField("Usage", it.usageFormatted, false)
				val parameters = it.arguments.commandArguments.asString("\n") { p ->
					var u = "`${
						p.getUsage().substring(1, p.getUsage().length - 1)
					}` : Type[${(p.type as Class<*>).simpleName.split(".").last()}]"
					if (p.details != null && p.details.isNotBlank()) {
						u += " : ${p.details}"
					}
					u += if (p.optional) {
						" : optional"
					} else {
						" : required"
					}
					u
				}
				embed.addField("Arguments", parameters, false)
				embed.addField("Short Description", it.descriptionFormatted, false)
				embed.addField("Category", help.category, false)

				if (help.examples.isNotEmpty())
					embed.addField("Examples", help.examples.joinToString("\n"), false)
				if (help.details.isNotBlank()) {
					embed.setDescription(help.details)
				}

				val permissions = listOf("Command execution: `${it.permission}`") + help.permissions
				embed.addField("Required permissions", permissions.joinToString("\n"), false)

				if (it.paths.size > 1)
					embed.addField(
						"Aliases",
						it.paths.joinToString("\n"),
						false
					)

				embed
			}
		)

		return event.respond(pager)

	}

}