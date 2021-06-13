package net.volcano.jdacommands.commands

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.RestAction
import net.volcano.jdacommands.model.command.CommandEvent
import net.volcano.jdacommands.model.command.annotations.BotOwnerCanAlwaysExecute
import net.volcano.jdacommands.model.command.annotations.CommandController
import net.volcano.jdacommands.model.command.annotations.CommandMethod
import net.volcano.jdacommands.model.command.annotations.Help

@CommandController
class Override {

	@BotOwnerCanAlwaysExecute
	@CommandMethod(
		path = ["override"],
		permissions = "command.override",
		global = true
	)
	@Help(description = "Override all permission checks.", category = "admin")
	fun override(event: CommandEvent): RestAction<Message> {

		return if (event.client.permissionProvider.isOverriding(event.author.id)) {
			event.client.permissionProvider.stopOverriding(event.author.id)
			event.respond("```fix\nResetting Overrides\n```")
		} else {
			event.client.permissionProvider.startOverriding(event.author.id)
			event.respond("```prolog\nOverriding Permissions\n```")
		}

	}
}