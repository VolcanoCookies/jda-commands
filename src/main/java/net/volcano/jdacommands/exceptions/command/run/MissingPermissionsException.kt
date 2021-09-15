package net.volcano.jdacommands.exceptions.command.run

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.volcano.jdacommands.model.command.Command
import net.volcano.jdacommands.permissions.Permission

// TODO Actually use the guild to tell the user where they need permissions

class MissingPermissionsException(
	command: Command?,
	val guild: Guild?,
	private val missingPermissions: Permission
) : CommandException(command) {

	override fun getErrorEmbed(embedBuilder: EmbedBuilder): EmbedBuilder {
		embedBuilder.setTitle("Error: Missing " + (if (guild == null) "global" else "local") + " permissions")
		embedBuilder.setDescription("Missing ${missingPermissions.value}")
		return embedBuilder
	}

}