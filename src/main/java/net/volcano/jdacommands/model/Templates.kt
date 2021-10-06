package net.volcano.jdacommands.model

import net.dv8tion.jda.api.EmbedBuilder
import net.volcano.jdacommands.model.command.Field
import net.volcano.jdautilities.constants.Colors

object Templates {

	fun success(content: String, vararg fields: Field): EmbedBuilder {
		val embed = EmbedBuilder()
		embed.setTitle("Success")
		embed.setDescription(content)
		for (field in fields) {
			embed.addField(field.build())
		}
		return embed.setColor(Colors.SUCCESS)
	}

	fun error(content: String, vararg fields: Field): EmbedBuilder {
		val embed = EmbedBuilder()
		embed.setTitle("Error")
		embed.setDescription(content)
		for (field in fields) {
			embed.addField(field.build())
		}
		return embed.setColor(Colors.ERROR)
	}

	fun info(content: String, vararg fields: Field): EmbedBuilder {
		val embed = EmbedBuilder()
		embed.setTitle("Info")
		embed.setDescription(content)
		for (field in fields) {
			embed.addField(field.build())
		}
		return embed.setColor(Colors.INFO)
	}

	fun partialSuccess(content: String, vararg fields: Field): EmbedBuilder {
		val embed = EmbedBuilder()
		embed.setTitle("Partial Success")
		embed.setDescription(content)
		for (field in fields) {
			embed.addField(field.build())
		}
		return embed.setColor(Colors.ERROR)
	}

}