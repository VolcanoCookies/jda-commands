package net.volcano.jdacommands.model.command

import net.dv8tion.jda.api.entities.MessageEmbed

class Field(
	val name: String,
	val value: String,
	val inline: Boolean = false
) {

	fun build(): MessageEmbed.Field {
		return MessageEmbed.Field(name, value, inline)
	}
}