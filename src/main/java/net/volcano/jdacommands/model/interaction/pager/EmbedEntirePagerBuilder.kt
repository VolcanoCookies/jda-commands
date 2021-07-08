package net.volcano.jdacommands.model.interaction.pager

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed

class EmbedEntirePagerBuilder : EmbedPagerBuilder {

	private var embeds: MutableList<MessageEmbed> = ArrayList()

	constructor() : super()
	constructor(builder: EmbedBuilder?) : super(builder)
	constructor(embed: MessageEmbed?) : super(embed)

	override fun buildEmbed(baseEmbed: MessageEmbed): EmbedPager {
		return EmbedEntirePager(embeds, userId, download)
	}

	fun setEmbeds(embeds: List<EmbedBuilder>) {
		this.embeds = embeds.map { it.build() }.toMutableList()
	}

	fun addEmbed(embed: EmbedBuilder) {
		embeds.add(embed.build())
	}

	operator fun next() {
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
				fields
			)
		)
		clear()
	}
}