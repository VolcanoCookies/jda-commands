package net.volcano.jdacommands.model.interaction.menu

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.volcano.jdautils.constants.EmbedLimit

class EmbedDescriptionMenuBuilder : EmbedMenuBuilder() {

	val options: MutableMap<SelectOption, String> = mutableMapOf()

	override fun buildEmbed(baseEmbed: MessageEmbed): EmbedDescriptionMenu {
		val embedBuilder = EmbedBuilder(baseEmbed)
		embedBuilder.setDescription(null)
		for (page in options.values) {
			check(page.length <= EmbedLimit.EMBED_DESCRIPTION_LIMIT) {
				String.format(
					"Description page is longer than %d! Please limit your input!",
					EmbedLimit.EMBED_DESCRIPTION_LIMIT
				)
			}
			check(embedBuilder.length() + page.length <= EmbedLimit.EMBED_TOTAL_LIMIT) { "Cannot build an embed with more than " + EmbedLimit.EMBED_TOTAL_LIMIT + " characters!" }
		}
		return EmbedDescriptionMenu(options, userId, baseEmbed, download, asReply, ephemeral, expiration)
	}

	fun addOption(key: String, content: String): EmbedDescriptionMenuBuilder {
		this.options[SelectOption.of(key, key)] = content
		return this
	}

	fun addOption(key: String, content: String, emoji: Emoji): EmbedDescriptionMenuBuilder {
		this.options[SelectOption.of(key, key).withEmoji(emoji)] = content
		return this
	}

}