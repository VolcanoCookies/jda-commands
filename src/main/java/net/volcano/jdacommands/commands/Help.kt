package net.volcano.jdacommands.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.requests.RestAction
import net.volcano.jdacommands.constants.Reactions
import net.volcano.jdacommands.interfaces.PermissionClient
import net.volcano.jdacommands.model.command.Command
import net.volcano.jdacommands.model.command.CommandEvent
import net.volcano.jdacommands.model.command.annotations.BotOwnerCanAlwaysExecute
import net.volcano.jdacommands.model.command.annotations.CommandController
import net.volcano.jdacommands.model.command.annotations.CommandMethod
import net.volcano.jdacommands.model.command.annotations.Help
import net.volcano.jdacommands.model.interaction.menu.EmbedDescriptionMenuBuilder
import net.volcano.jdacommands.model.interaction.pager.EmbedFieldPagerBuilder
import net.volcano.jdautilities.constants.Colors
import net.volcano.jdautilities.utils.capitalize

@CommandController
class Help(
	private val permissionClient: PermissionClient
) {

	@BotOwnerCanAlwaysExecute
	@CommandMethod(
		path = ["help", "h"],
		permissions = "command.help"
	)
	@Help(
		usage = "help [Category]",
		description = "Show this embed, optionally for one category only."
	)
	fun help(event: CommandEvent): RestAction<*> {

		val commands = event.client.allCommands
			.filter {
				permissionClient.checkPermissions(
					it.permission,
					event.author,
					event.guild,
					event.textChannel
				).hasPermissions
			}
			.let {
				if (event.isFromGuild)
					it.filter { c -> c.source != Command.Source.PRIVATE }
				else
					it.filter { c -> c.source != Command.Source.GUILD }
			}
			.filter { it.help != null }

		val frontEmbed = EmbedBuilder()
		frontEmbed.setTitle("__**Help**__")
		frontEmbed.setColor(Colors.HELP)
		frontEmbed.addField("Arguments", "<Required> [Optional]", false)
		frontEmbed.addField(
			"Manual",
			"You can always use the 'manual' command to view more information about a specific command.",
			false
		)
		frontEmbed.addField(
			"Reactions",
			"${Reactions.WARNING} means the command was not found.\n${Reactions.NO_PERMISSIONS} means the command was found but you are lacking permissions.",
			false
		)
		frontEmbed.setDescription("Pick a category.")

		val pager = EmbedDescriptionMenuBuilder()
		pager.ephemeral = true
		pager.setTitle("__**Help**__")
		pager.setFrontBaseEmbed(frontEmbed)
		commands.filter { it.help != null }
			.groupBy { it.help.category }
			.forEach { (cat, com) ->
				pager.addOption(
					cat,
					com.sortedBy { it.usageFormatted }
						.joinToString("\n\n") { "**${it.usageFormatted}**\n${it.descriptionFormatted}" },
					Emoji.fromUnicode(com.first().help.emoji)
				)
			}
		pager.setColor(Colors.HELP)

		return event.respond(pager)
	}

	@CommandMethod(
		path = ["help"],
		permissions = "help"
	)
	fun help(event: CommandEvent, category: String): RestAction<*>? {

		val commands = event.client.allCommands
			.filter {
				permissionClient.checkPermissions(
					it.permission,
					event.author,
					event.guild,
					event.textChannel
				).hasPermissions
			}
			.let {
				if (event.isFromGuild)
					it.filter { c -> c.source != Command.Source.PRIVATE }
				else
					it.filter { c -> c.source != Command.Source.GUILD }
			}
			.filter { it.help != null }
			.filter {
				it.help.category.lowercase() == category.lowercase()
			}

		val pager = EmbedFieldPagerBuilder()
		pager.setTitle("__**Help ${category.capitalize()}**__")
		pager.setFooter("<Required> [Optional]")
		commands.sortedBy { it.usageFormatted.length }
			.forEach { pager.addField("**${it.usageFormatted}**", it.descriptionFormatted) }
		pager.setColor(Colors.HELP)

		return event.respond(pager)

	}

}