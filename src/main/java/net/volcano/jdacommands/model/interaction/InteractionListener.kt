package net.volcano.jdacommands.model.interaction

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent
import net.volcano.jdacommands.interfaces.InteractionClient

abstract class InteractionListener {

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

	open fun onInteraction(event: ButtonClickEvent) {}

	open fun onInteraction(event: GenericMessageReactionEvent) {}

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