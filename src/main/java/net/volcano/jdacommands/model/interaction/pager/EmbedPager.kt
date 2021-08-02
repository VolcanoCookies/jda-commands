package net.volcano.jdacommands.model.interaction.pager

import lombok.Getter
import lombok.Setter
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import net.volcano.jdacommands.constants.Reactions
import net.volcano.jdacommands.model.interaction.InteractionListener
import net.volcano.jdautils.constants.EMBED_FOOTER_LIMIT
import net.volcano.jdautils.utils.trim
import kotlin.math.max
import kotlin.math.min

@Getter
@Setter
abstract class EmbedPager(
	var userId: String,
	var baseEmbed: EmbedBuilder,
	var download: ByteArray? = null,
	var fileName: String? = null,
	var currentPage: Int = 0,
	var extraButtons: List<Button>,
	expiration: Long = 60L * 30L,
) : InteractionListener(expiration) {

	var hadDownload = false

	abstract fun getPage(page: Int): MessageEmbed

	abstract val size: Int

	fun postSend(message: Message) {
		val builder = MessageBuilder(message)
		builder.setActionRows(getActionRow())

		message.editMessage(builder.build()).queue()
	}

	fun getActionRow(): List<ActionRow>? {
		val buttons = mutableListOf<Button>()
		if (size > 1) {
			buttons += Button.of(ButtonStyle.PRIMARY, "first", "Start", Emoji.fromUnicode(Reactions.PAGE_START))
				.withDisabled(currentPage == 0)
			buttons += Button.of(ButtonStyle.PRIMARY, "prev", "Back", Emoji.fromUnicode(Reactions.PAGE_BACK))
				.withDisabled(currentPage == 0)
			buttons += Button.of(ButtonStyle.PRIMARY, "next", "Next", Emoji.fromUnicode(Reactions.PAGE_FORWARD))
				.withDisabled(currentPage == size - 1)
			buttons += Button.of(ButtonStyle.PRIMARY, "end", "End", Emoji.fromUnicode(Reactions.PAGE_END))
				.withDisabled(currentPage == size - 1)
		}
		if (download != null || hadDownload) {
			hadDownload = true
			buttons += Button.of(ButtonStyle.PRIMARY, "download", "Download", Emoji.fromUnicode(Reactions.DOWNLOAD))
				.withDisabled(download == null)
		}
		buttons += extraButtons
		return if (buttons.isEmpty())
			null
		else
			buttons.let {
				val rows: MutableList<ActionRow> = mutableListOf()
				val l: MutableList<Button> = mutableListOf()
				for (button in it) {
					if (l.size >= 5) {
						rows += ActionRow.of(l)
						l.clear()
					}
					l += button
				}
				rows += ActionRow.of(l)
				rows
			}
	}

	val page: MessageEmbed
		get() = getPage(currentPage)

	override fun onInteraction(event: ButtonClickEvent) {

		if (event.user.id != userId) {
			event.reply("```diff\n- This embed does not belong to you, thus you cannot interact with it!```")
				.setEphemeral(true)
				.queue()
			return
		}

		var invalid = false
		when (event.componentId) {
			"first" -> currentPage = 0
			"prev" -> currentPage = max(currentPage - 1, 0)
			"next" -> currentPage = min(currentPage + 1, size - 1)
			"end" -> currentPage = max(0, size - 1)
			"download" -> {
				download?.let {
					event.channel
						.sendFile(it, fileName ?: "download.txt")
						.queue { download = null }
				}
				download = null
			}
			else -> {
				invalid = true
			}
		}

		if (!invalid) {
			event.editMessageEmbeds(page)
				.let { getActionRow()?.let { c -> it.setActionRows(c) } ?: it }
				.queue()
		}

	}

	open val footer: MessageEmbed.Footer
		get() {
			val text = "Page ${currentPage + 1} of $size${baseEmbed.build().footer?.text?.let { " | $it" } ?: ""}".trim(
				EMBED_FOOTER_LIMIT
			)
			val base = baseEmbed.build()
			return MessageEmbed.Footer(text, base.footer?.iconUrl, base.footer?.proxyIconUrl)
		}

}
