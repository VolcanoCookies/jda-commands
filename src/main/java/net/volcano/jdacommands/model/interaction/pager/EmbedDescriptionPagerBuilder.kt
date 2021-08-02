package net.volcano.jdacommands.model.interaction.pager

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.volcano.jdautils.constants.EMBED_DESCRIPTION_LIMIT

class EmbedDescriptionPagerBuilder : EmbedPagerBuilder {

	private var pages: MutableList<String> = ArrayList()

	constructor() : super()
	constructor(builder: EmbedBuilder?) : super(builder)
	constructor(embed: MessageEmbed?) : super(embed)

	override fun buildEmbed(baseEmbed: MessageEmbed): EmbedPager {
		val embedBuilder = EmbedBuilder(baseEmbed)
		embedBuilder.setDescription(null)
		for (page in pages) {
			check(page.length <= EMBED_DESCRIPTION_LIMIT) {
				String.format(
					"Description page is longer than %d! Please limit your input!",
					MessageEmbed.TEXT_MAX_LENGTH
				)
			}
			check(embedBuilder.length() + page.length <= MessageEmbed.EMBED_MAX_LENGTH_BOT) { "Cannot build an embed with more than " + MessageEmbed.EMBED_MAX_LENGTH_BOT + " characters!" }
		}
		return EmbedDescriptionPager(
			pages,
			userId,
			embedBuilder,
			download,
			downloadFileName,
			0,
			extraButtons,
			expiration
		)
	}

	fun addPage(page: String) {
		check(page.length <= EMBED_DESCRIPTION_LIMIT) {
			String.format(
				"Description page is longer than %d! Please limit your input!",
				EMBED_DESCRIPTION_LIMIT
			)
		}
		pages.add(page)
	}

	fun addPages(pages: List<String>) {
		for (page in pages) {
			check(page.length <= EMBED_DESCRIPTION_LIMIT) {
				String.format(
					"Description page is longer than %d! Please limit your input!",
					EMBED_DESCRIPTION_LIMIT
				)
			}
		}
		this.pages.addAll(pages)
	}

	fun setPages(pages: List<String>) {
		this.pages = pages.toMutableList()
		for (page in this.pages) {
			check(page.length <= EMBED_DESCRIPTION_LIMIT) {
				String.format(
					"Description page is longer than %d! Please limit your input!",
					EMBED_DESCRIPTION_LIMIT
				)
			}
		}
	}
}