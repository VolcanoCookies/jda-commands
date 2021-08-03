package net.volcano.jdacommands.model.interaction.menu

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.volcano.jdacommands.model.interaction.InteractionListener
import net.volcano.jdautils.constants.Colors

abstract class EmbedMenu(
	var userId: String,
	var baseEmbed: MessageEmbed,
	var download: ByteArray? = null,
	private val asReply: Boolean = false,
	private val ephemeral: Boolean = false,
	expiration: Long = 60L * 15L
) : InteractionListener(expiration) {

	open var frontPage = defaultFront

	abstract val size: Int

	abstract fun getPage(selection: String): MessageEmbed?

	lateinit var originalHook: InteractionHook

	override fun onInteraction(event: SelectionMenuEvent) {
		if (event.user.id != userId) {
			event.reply("```diff\n- This embed does not belong to you, thus you cannot interact with it!```")
				.setEphemeral(true)
				.queue()
			return
		}
		event.selectedOptions?.get(0)
			?.value
			?.let { getPage(it) }
			?.let {
				if (asReply) {
					event.replyEmbeds(it)
						.setEphemeral(ephemeral)
				} else {
					if (this::originalHook.isInitialized) {
						event.deferEdit().queue()
						originalHook.editOriginalEmbeds(it)
					} else {
						originalHook = event.hook
						event.replyEmbeds(it)
							.setEphemeral(ephemeral)
					}
				}.queue()
			}
			?: return
	}

	abstract fun getActionRows(): List<ActionRow>

	fun postSend(message: Message) {
		message.editMessage(
			MessageBuilder(message)
				.setActionRows(getActionRows()).build()
		)
			.queue()
	}

	private val defaultFront: MessageEmbed
		get() {
			val embed = EmbedBuilder()
			embed.setTitle("Selection Menu")
			if (asReply && baseEmbed.description != null)
				embed.setDescription(baseEmbed.description!!)
			else
				embed.setDescription("Please select a page to continue.")
			embed.setColor(Colors.INFO)
			return embed.build()
		}

}