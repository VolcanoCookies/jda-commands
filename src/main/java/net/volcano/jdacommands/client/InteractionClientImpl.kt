package net.volcano.jdacommands.client

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.volcano.jdacommands.interfaces.InteractionClient
import net.volcano.jdacommands.model.interaction.InteractionListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class InteractionClientImpl(
	val jda: JDA
) : InteractionClient, ListenerAdapter() {

	private val listeners: MutableMap<String, Pair<InteractionListener, Long>> = mutableMapOf()

	override fun addListener(message: Message, listener: InteractionListener, expiration: Long) {
		listeners[message.id] = Pair(listener, Instant.now().epochSecond + expiration)
		listener.messageId = message.id
		listener.interactionClient = this
		listener.jda = jda
		listener.setup(message)
		listener.onAdd()
	}

	override fun addListener(messageId: String, listener: InteractionListener, expiration: Long) {
		listeners[messageId] = Pair(listener, Instant.now().epochSecond + expiration)
		listener.messageId = messageId
		listener.interactionClient = this
		listener.jda = jda
		listener.onAdd()
	}

	override fun removeListener(message: Message) {
		val listener = listeners[message.id]
		listeners.remove(message.id)
		listener?.first?.destruct(message)
		listener?.first?.onRemove()
	}

	override fun removeListener(messageId: String) {
		val listener = listeners[messageId]
		listeners.remove(messageId)
		listener?.first?.onRemove()
	}

	override fun getListeners(): Map<String, InteractionListener> {
		return listeners.mapValues { it.value.first }.toMap()
	}

	override fun onGenericMessageReaction(event: GenericMessageReactionEvent) {
		listeners[event.messageId]?.let {
			if (Instant.now().epochSecond < it.second)
				it.first.onInteraction(event)
			else
				removeListener(event.messageId)
		}
	}

	override fun onButtonClick(event: ButtonClickEvent) {
		listeners[event.messageId]?.let {
			if (Instant.now().epochSecond < it.second)
				it.first.onInteraction(event)
			else
				removeListener(event.messageId)
		}
	}

	@Scheduled(fixedRate = 1000L * 60)
	fun clean() {
		val now = Instant.now().epochSecond
		listeners.filterValues { it.second < now }
			.forEach {
				removeListener(it.key)
			}
	}

}