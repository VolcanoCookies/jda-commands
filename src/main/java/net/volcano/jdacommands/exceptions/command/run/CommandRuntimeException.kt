package net.volcano.jdacommands.exceptions.command.run

import net.dv8tion.jda.api.EmbedBuilder
import net.volcano.jdautils.constants.Colors
import net.volcano.jdautils.constants.EMBED_DESCRIPTION_LIMIT
import net.volcano.jdautils.utils.trim
import java.time.Instant

open class CommandRuntimeException(
	message: String? = null
) : Exception(message) {

	open val errorEmbed: EmbedBuilder
		get() {
			val builder = EmbedBuilder()
			builder.setTitle("Error: Runtime Exception")
			builder.setColor(Colors.ERROR)
			message?.let { builder.setDescription(it.trim(EMBED_DESCRIPTION_LIMIT)) }
			builder.setTimestamp(Instant.now())
			return builder
		}

}