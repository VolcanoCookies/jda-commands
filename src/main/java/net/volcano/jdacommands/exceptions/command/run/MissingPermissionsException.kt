package net.volcano.jdacommands.exceptions.command.run

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild

// TODO Actually use the guild to tell the user where they need permissions

class MissingPermissionsException(
	val guild: Guild?,
	private val missingPermissions: String
) : CommandException() {

	override fun getErrorEmbed(embedBuilder: EmbedBuilder): EmbedBuilder {
		embedBuilder.setTitle("Error: Missing " + (if (guild == null) "global" else "local") + " permissions")
		embedBuilder.setDescription("Missing $missingPermissions")
		return embedBuilder
	}

}