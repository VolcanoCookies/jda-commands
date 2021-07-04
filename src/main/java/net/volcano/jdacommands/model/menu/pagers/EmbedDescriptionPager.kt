package net.volcano.jdacommands.model.menu.pagers

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.requests.RestAction
import net.volcano.jdacommands.constants.Reactions

class EmbedDescriptionPager(
	private val pages: List<String>,
	userId: String,
	baseEmbed: EmbedBuilder,
	download: ByteArray? = null,
	currentPage: Int = 0,
	expiration: Long = 60L * 30L,
) : EmbedPager(userId, baseEmbed, download, currentPage, expiration) {

	override val size: Int
		get() = pages.size

	override fun getPage(page: Int): MessageEmbed {
		val embedBuilder = EmbedBuilder(baseEmbed)
		embedBuilder.setDescription(pages[page])
		embedBuilder.setFooter(generateFooter())
		return embedBuilder.build()
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