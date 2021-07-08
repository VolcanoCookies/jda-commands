package net.volcano.jdacommands.model.interaction.pager

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.Button

class EmbedDescriptionPager(
	private val pages: List<String>,
	userId: String,
	baseEmbed: EmbedBuilder,
	download: ByteArray? = null,
	currentPage: Int = 0,
	extraButtons: List<Button> = listOf(),
	expiration: Long = 60L * 30L,
) : EmbedPager(userId, baseEmbed, download, currentPage, extraButtons, expiration) {

	override val size: Int
		get() = pages.size

	override fun getPage(page: Int): MessageEmbed {
		val embedBuilder = EmbedBuilder(baseEmbed)
		embedBuilder.setDescription(pages[page])
		embedBuilder.setFooter(footer.text, footer.iconUrl)
		return embedBuilder.build()
	}

}