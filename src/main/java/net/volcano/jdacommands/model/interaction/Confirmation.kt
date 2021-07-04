package net.volcano.jdacommands.model.interaction

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button
import net.volcano.jdautils.constants.Colors
import java.util.concurrent.CompletableFuture

class Confirmation(
	val userId: String
) : InteractionListener(60L * 5L) {

	val future: CompletableFuture<Boolean> = CompletableFuture()

	private var expired = true

	constructor(user: User) : this(user.id)
	constructor(member: Member) : this(member.id)

	override fun onInteraction(event: ButtonClickEvent) {

		if (event.user.id != userId) {
			event.reply("You neither confirm, nor deny, this action!")
				.setEphemeral(true)
				.queue()
			return
		}

		if (event.interaction.componentId == "confirm") {
			confirm(true, event)
		} else if (event.interaction.componentId == "deny") {
			confirm(false, event)
		}
	}

	fun confirm(answer: Boolean, event: ButtonClickEvent) {
		expired = false
		event.message?.let { removeSelf(it) } ?: removeSelf()
		event.deferReply(true)
		future.complete(answer)

		val embedBuilder = EmbedBuilder()
		if (answer) {
			embedBuilder.setColor(Colors.SUCCESS)
			embedBuilder.setDescription("```diff\n+ Action confirmed. +```")
		} else {
			embedBuilder.setColor(Colors.ERROR)
			embedBuilder.setDescription("```diff\n- Action denied. -```")
		}
		event.replyEmbeds(embedBuilder.build())
			.setEphemeral(true)
			.queue()

		event.message?.let { removeSelf(it) } ?: removeSelf()
	}

	override fun destruct(message: Message) {
		message.delete().queue()
	}

	override fun onRemove() {
		if (expired && !future.isDone)
			future.complete(false)
	}

	companion object {

		fun message(content: String): Message {
			val embedBuilder = EmbedBuilder()
			embedBuilder.setTitle("Confirmation")
			embedBuilder.setDescription(content)
			embedBuilder.setColor(Colors.NOTES)
			embedBuilder.setFooter("You have 5 minutes to respond, after which this confirmation will automatically be denied.")
			val messageBuilder = MessageBuilder()
			messageBuilder.setEmbeds(embedBuilder.build())
			messageBuilder.setActionRows(
				ActionRow.of(
					Button.success("confirm", "Confirm"),
					Button.danger("deny", "Deny")
				)
			)
			return messageBuilder.build()
		}

	}

}