package net.volcano.jdacommands.commands

import net.dv8tion.jda.api.requests.RestAction
import net.volcano.jdacommands.model.command.CommandEvent
import net.volcano.jdacommands.model.command.annotations.BotOwnerCanAlwaysExecute
import net.volcano.jdacommands.model.command.annotations.CommandController
import net.volcano.jdacommands.model.command.annotations.CommandMethod
import net.volcano.jdacommands.model.command.annotations.Help
import net.volcano.jdacommands.model.menu.pagers.EmbedFieldPagerBuilder
import net.volcano.jdautils.constants.Colors
import net.volcano.jdautils.utils.StringUtil

@CommandController
class Help {

	@BotOwnerCanAlwaysExecute
	@CommandMethod(path = ["help"], permissions = ["help"])
	@Help(description = "Show this embed.")
	fun help(event: CommandEvent): RestAction<*> {

		val commands = event.client.commands
			.filter {
				event.client.permissionProvider.hasPermissions(
					it.permissions,
					event.author.id,
					if (!it.isGlobalPermissions) event.guildId else null
				)
			}

		val pager = EmbedFieldPagerBuilder()
		pager.setTitle("__**Help**__")
		pager.setFooter("<Required> [Optional]")
		pager.setFieldsPerPage(12)
		commands.groupBy { it.help.category }
			.forEach { (cat, com) ->
				pager.addField("__**Category: ${StringUtil.capitalize(cat)}**__", "")
				com.forEach { pager.addInlineField("**${it.usageFormatted}**", it.descriptionFormatted) }
			}
		pager.setColor(Colors.HELP)

		return event.respond(pager)

	}

}