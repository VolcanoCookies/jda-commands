package net.volcano.jdacommands.model.menu.pagers

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.requests.RestAction
import net.volcano.jdacommands.constants.Reactions

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

	override fun postSend(message: Message): RestAction<*>? {
		if (size > 1) {
			RestAction.allOf(
				message.addReaction(Reactions.PAGE_START),
				message.addReaction(Reactions.PAGE_BACK),
				message.addReaction(Reactions.PAGE_FORWARD),
				message.addReaction(Reactions.PAGE_END)
			).queue()
		}
		if (download != null) {
			message.addReaction(Reactions.DOWNLOAD)
				.queue()
		}
		return null
	}

}