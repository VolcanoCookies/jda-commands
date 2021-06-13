package net.volcano.jdacommands.exceptions.command.run

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.volcano.jdautils.utils.TimeUtil
import java.time.Duration
import java.time.OffsetDateTime

// TODO Actually use the guild to tell the user where they need permissions

class PermissionsOnCooldownException(
	val guild: Guild?,
	private val missingPermissions: String,
	private val expirationTime: OffsetDateTime
) : CommandException() {

	override fun getErrorEmbed(embedBuilder: EmbedBuilder): EmbedBuilder {
		embedBuilder.setTitle("Error: Permission cooldown.")
		embedBuilder.setDescription(
			"Cooldown of ${
				TimeUtil.format(
					Duration.between(
						OffsetDateTime.now(),
						expirationTime
					)
				)
			} on '$missingPermissions'"
		)
		return embedBuilder
	}

}