package net.volcano.jdacommands.model.interaction

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent
import net.dv8tion.jda.api.utils.TimeUtil
import net.volcano.jdacommands.interfaces.InteractionClient

abstract class InteractionListener(
	/**
	 * Time in seconds after the message creation time that this listener should be removed.
	 */
	var expiration: Long = Long.MAX_VALUE
) {

	/**
	 * Available after this listener has been registered, including in [onAdd].
	 */
	lateinit var messageId: String

	/**
	 * Available after this listener has been registered, including in [onAdd].
	 */
	lateinit var interactionClient: InteractionClient

	/**
	 * Available after this listener has been registered, including in [onAdd].
	 */
	lateinit var jda: JDA

	val expirationEpochSeconds: Long
		get() = if (expiration == Long.MAX_VALUE) Long.MAX_VALUE else TimeUtil.getTimeCreated(messageId.toLong())
			.toEpochSecond() + expiration

	open fun onInteraction(event: ButtonClickEvent) {}

	open fun onInteraction(event: GenericMessageReactionEvent) {}

	open fun onInteraction(event: SelectionMenuEvent) {}

	/**
	 * Ran if the listener was registered using a message instance and not id.
	 */
	open fun setup(message: Message) {}

	/**
	 * Ran if the listener was removed using a message instance and not id.
	 */
	open fun destruct(message: Message) {}

	open fun onAdd() {}

	open fun onRemove() {}

	fun removeSelf(message: Message) {
		interactionClient.removeListener(message)
	}

	fun removeSelf() {
		interactionClient.removeListener(messageId)
	}

}