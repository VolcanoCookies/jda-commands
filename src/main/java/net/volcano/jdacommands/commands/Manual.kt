package net.volcano.jdacommands.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.requests.RestAction
import net.volcano.jdacommands.model.command.CommandEvent
import net.volcano.jdacommands.model.command.annotations.CommandController
import net.volcano.jdacommands.model.command.annotations.CommandMethod
import net.volcano.jdacommands.model.command.annotations.Help
import net.volcano.jdacommands.model.menu.pagers.EmbedEntirePagerBuilder
import net.volcano.jdautils.constants.Colors
import java.time.Instant

@CommandController
class Manual {

	@CommandMethod(
		path = ["manual", "man"],
		permissions = "commands.manual"
	)
	@Help(
		description = "Show more detailed information about commands."
	)
	fun manual(event: CommandEvent, command: String): RestAction<*>? {

		val commands = event.client.rootCommandNode.findCommands(command).first
			.filter { it.help != null }

		if (commands.isEmpty())
			return event.respondError("No commands found.")

		val pager = EmbedEntirePagerBuilder()
		pager.setEmbeds(
			commands.map {
				val embed = EmbedBuilder()
				embed.setTitle("Manual: ${it.paths[0]}")
				embed.setTimestamp(Instant.now())
				embed.setColor(Colors.INFO)
				val help = it.help
				embed.addField("Usage", it.usageFormatted, false)
				embed.addField("Short Description", it.descriptionFormatted, false)
				embed.addField("Category", help.category, false)
				if (help.examples.isNotEmpty())
					embed.addField("Examples", help.examples.joinToString("\n"), false)
				if (help.details.isNotBlank()) {
					embed.setDescription(help.details)
				}
				embed.addField("Required permissions", "Command execution: `${it.permission}`", false)
				embed
			}
		)

		return event.respond(pager)

	}

}