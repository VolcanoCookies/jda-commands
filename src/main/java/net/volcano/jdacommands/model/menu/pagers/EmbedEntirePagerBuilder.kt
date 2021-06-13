package net.volcano.jdacommands.model.menu.pagers

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed
import java.util.*

class EmbedEntirePagerBuilder : EmbedPagerBuilder() {

	private val embeds = mutableListOf<MessageEmbed>()

	override fun buildEmbed(baseEmbed: MessageEmbed): EmbedPager {
		next()
		return EmbedEntirePager(embeds, expiration)
	}

	fun setEmbeds(embeds: List<EmbedBuilder>) {
		this.embeds.clear()
		this.embeds.addAll(embeds.map { it.build() })
	}

	fun addEmbed(embed: EmbedBuilder) {
		this.embeds.add(embed.build())
	}

	fun next() {

		embeds.add(
			MessageEmbed(
				url,
				title,
				description.toString(),
				EmbedType.RICH,
				timestamp,
				color,
				thumbnail,
				null,
				author,
				null,
				footer,
				image,
				LinkedList(fields)
			)
		)

		this.clear()

	}

}