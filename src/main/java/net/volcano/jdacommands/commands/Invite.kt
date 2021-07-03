package net.volcano.jdacommands.commands

import net.dv8tion.jda.api.requests.RestAction
import net.volcano.jdacommands.model.command.CommandEvent
import net.volcano.jdacommands.model.command.annotations.CommandController
import net.volcano.jdacommands.model.command.annotations.CommandMethod
import net.volcano.jdacommands.model.command.annotations.Help

@CommandController
class Invite {

	@CommandMethod(
		path = ["invite"],
		permissions = "command.invite"
	)
	@Help(description = "Generate a invite link for the bot.")
	fun invite(event: CommandEvent): RestAction<*> {
		return event.respond(event.jda.getInviteUrl())
	}

}