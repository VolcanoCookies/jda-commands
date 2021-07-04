package net.volcano.jdacommands.model.menu.pagers

import lombok.Getter
import lombok.Setter
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent
import net.dv8tion.jda.api.requests.RestAction
import net.volcano.jdacommands.constants.Reactions
import net.volcano.jdacommands.model.interaction.InteractionListener
import net.volcano.jdautils.constants.EmbedLimit
import net.volcano.jdautils.utils.StringUtil
import kotlin.math.max
import kotlin.math.min

@Getter
@Setter
abstract class EmbedPager(
	var userId: String,
	var baseEmbed: EmbedBuilder,
	var download: ByteArray? = null,
	var currentPage: Int = 0,
	expiration: Long = 60L * 30L,
) : InteractionListener(expiration) {

	abstract fun getPage(page: Int): MessageEmbed

	abstract val size: Int

	open fun postSend(message: Message): RestAction<*>? {
		return null
	}

	val page: MessageEmbed
		get() = getPage(currentPage)

	override fun onInteraction(event: GenericMessageReactionEvent) {

		if (event.userId != userId)
			return

		if (event.reactionEmote.isEmoji) {

			val prevPage = currentPage

			when (event.reactionEmote.emoji) {
				Reactions.PAGE_START -> currentPage = 0
				Reactions.PAGE_BACK -> currentPage = max(currentPage - 1, 0)
				Reactions.PAGE_FORWARD -> currentPage = min(currentPage + 1, size - 1)
				Reactions.PAGE_END -> currentPage = max(0, size - 1)
				Reactions.DOWNLOAD -> {
					download?.let {
						event.channel
							.sendFile(it, "download.txt")
							.queue { download = null }
					}
				}
				else -> {
					event.user?.let { event.reaction.removeReaction(it).queue() }
				}
			}

			if (prevPage != currentPage) {
				event.channel.retrieveMessageById(messageId)
					.flatMap {
						it.editMessageEmbeds(page)
					}.queue()
			}

		}

	}

	fun generateFooter(): String {
		val base = baseEmbed.build();
		return StringUtil.trim("Page ${currentPage + 1} of $size${base.footer?.text?.let { " | $it" } ?: ""}",
			EmbedLimit.EMBED_FOOTER_LIMIT);
	}

}
