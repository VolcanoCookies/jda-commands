package net.volcano.jdacommands.model.menu.pagers

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed

class EmbedEntirePager(
	private val embeds: List<MessageEmbed>,
	expiration: Long
) : EmbedPager(expiration) {

	override fun getPage(page: Int): MessageEmbed {
		return embeds[page]
	}

	override fun postSend(message: Message?) {

	}

	override fun getSize(): Int {
		return embeds.size
	}

}