package net.volcano.jdacommands.interfaces

import net.dv8tion.jda.api.entities.Message
import net.volcano.jdacommands.model.interaction.InteractionListener

interface InteractionClient {

	/**
	 * Attach a [InteractionListener] to a [Message].
	 *
	 * Overload for addListener(message.id, listener)
	 *
	 * @param message the message this listener wants interactions from.
	 * @param listener the listener to attach to this [message].
	 */
	fun addListener(message: Message, listener: InteractionListener) {
		addListener(message.id, listener, 1000L * 60 * 30)
	}

	/**
	 * Attach a [InteractionListener] to a [Message].
	 *
	 * Overload for addListener(message.id, listener, DEFAULT_TIME)
	 *
	 * @param messageId the id of the message this listener wants interactions from.
	 * @param listener the listener to attach to this [messageId].
	 */
	fun addListener(messageId: String, listener: InteractionListener) {
		addListener(messageId, listener, 1000L * 60 * 30)
	}

	/**
	 * Attach a [InteractionListener] to a [Message].
	 *
	 * @param message the message this listener wants interactions from.
	 * @param listener the listener to attach to this [message].
	 * @param expiration time in seconds after which this listener should be removed.
	 */
	fun addListener(message: Message, listener: InteractionListener, expiration: Long)

	/**
	 * Attach a [InteractionListener] to a [Message].
	 *
	 * Overload for addListener(message.id, listener)
	 *
	 * @param messageId the id of the message this listener wants interactions from.
	 * @param listener the listener to attach to this [messageId].
	 * @param expiration time in seconds after which this listener should be removed.
	 */
	fun addListener(messageId: String, listener: InteractionListener, expiration: Long)

	fun removeListener(message: Message)

	fun removeListener(messageId: String)

	fun getListeners(): Map<String, InteractionListener>

}

