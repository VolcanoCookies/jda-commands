package net.volcano.jdacommands.commands

import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.requests.RestAction
import net.volcano.jdacommands.interfaces.PermissionClient
import net.volcano.jdacommands.model.command.Command
import net.volcano.jdacommands.model.command.CommandEvent
import net.volcano.jdacommands.model.command.annotations.BotOwnerCanAlwaysExecute
import net.volcano.jdacommands.model.command.annotations.CommandController
import net.volcano.jdacommands.model.command.annotations.CommandMethod
import net.volcano.jdacommands.model.command.annotations.Help
import net.volcano.jdacommands.model.interaction.menu.EmbedDescriptionMenuBuilder
import net.volcano.jdacommands.model.interaction.pager.EmbedFieldPagerBuilder
import net.volcano.jdautils.constants.Colors
import net.volcano.jdautils.utils.StringUtil

@CommandController
class Help(
	private val permissionClient: PermissionClient
) {

	@BotOwnerCanAlwaysExecute
	@CommandMethod(
		path = ["help"],
		permissions = "command.help"
	)
	@Help(
		usage = "help [Category]",
		description = "Show this embed, optionally for one category only."
	)
	fun help(event: CommandEvent): RestAction<*> {

		val commands = event.client.allCommands
			.filter {
				permissionClient.checkPermissions(event.author, event.guild, it.permission).hasPermissions
			}
			.let {
				if (event.isFromGuild)
					it.filter { c -> c.source != Command.Source.PRIVATE }
				else
					it.filter { c -> c.source != Command.Source.GUILD }
			}
			.filter { it.help != null }

		val pager = EmbedDescriptionMenuBuilder()
		pager.asReply = false
		pager.ephemeral = true
		pager.setTitle("__**Help**__")
		pager.setFooter("<Required> [Optional]")
		commands.filter { it.help != null }
			.groupBy { it.help.category }
			.forEach { (cat, com) ->
				pager.addOption(
					cat,
					com.joinToString("\n\n") { "**${it.usageFormatted}**\n${it.descriptionFormatted}" },
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
				permissionClient.checkPermissions(event.author, event.guild, it.permission).hasPermissions
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
		pager.setTitle("__**Help ${StringUtil.capitalize(category)}**__")
		pager.setFooter("<Required> [Optional]")
		commands.sortedBy { it.usageFormatted.length }
			.forEach { pager.addField("**${it.usageFormatted}**", it.descriptionFormatted) }
		pager.setColor(Colors.HELP)

		return event.respond(pager)

	}

}