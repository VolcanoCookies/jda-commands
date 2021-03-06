package net.volcano.jdacommands.model.interaction.pager

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.Button
import net.volcano.jdautilities.constants.EMBED_FOOTER_LIMIT
import net.volcano.jdautilities.utils.trim

class EmbedEntirePager(
	private val embeds: List<MessageEmbed>,
	userId: String,
	download: ByteArray? = null,
	fileName: String? = null,
	currentPage: Int = 0,
	extraButtons: List<Button> = listOf(),
	expiration: Long = 60L * 30L,
) : EmbedPager(userId, EmbedBuilder(), download, fileName, currentPage, extraButtons, expiration) {

	override val size: Int
		get() = embeds.size

	override fun getPage(page: Int): MessageEmbed {
		return embeds[page]
	}

	override val footer: MessageEmbed.Footer
		get() {
			val text = "Page ${currentPage + 1} of $size${baseEmbed.build().footer?.text?.let { " | $it" } ?: ""}".trim(
				EMBED_FOOTER_LIMIT
			)
			return MessageEmbed.Footer(text, page.footer?.iconUrl, page.footer?.proxyIconUrl)
		}

}