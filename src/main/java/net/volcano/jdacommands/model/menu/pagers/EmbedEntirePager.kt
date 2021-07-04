package net.volcano.jdacommands.model.menu.pagers

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed

class EmbedEntirePager(
	private val embeds: List<MessageEmbed>,
	userId: String,
	download: ByteArray? = null,
	currentPage: Int = 0,
	expiration: Long = 60L * 30L,
) : EmbedPager(userId, EmbedBuilder(), download, currentPage, expiration) {

	override val size: Int
		get() = embeds.size

	override fun getPage(page: Int): MessageEmbed {
		return embeds[page]
	}

}