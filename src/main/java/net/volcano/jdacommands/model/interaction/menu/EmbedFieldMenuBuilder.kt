package net.volcano.jdacommands.model.interaction.menu

import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.volcano.jdacommands.model.command.Field

class EmbedFieldMenuBuilder : EmbedMenuBuilder() {

	val options: MutableMap<SelectOption, MutableList<Field>> = HashMap()

	override fun buildEmbed(baseEmbed: MessageEmbed): EmbedFieldMenu {
		return EmbedFieldMenu(options, userId, baseEmbed, frontBaseEmbed, download, asReply, ephemeral, expiration)
	}

	fun addField(key: String, title: String, value: String, inline: Boolean = false): EmbedFieldMenuBuilder {
		val opt = SelectOption.of(key, key)
		if (options[opt] == null) {
			this.options[opt] = mutableListOf(Field(title, value, inline))
		} else {
			this.options[opt]?.add(Field(title, value, inline))
		}
		return this
	}

	fun addField(
		key: String,
		title: String,
		value: String,
		emoji: Emoji,
		inline: Boolean = false
	): EmbedFieldMenuBuilder {
		val opt = SelectOption.of(key, key).withEmoji(emoji)
		if (options[opt] == null) {
			this.options[opt] = mutableListOf(Field(title, value, inline))
		} else {
			this.options[opt]?.add(Field(title, value, inline))
		}
		return this
	}

}