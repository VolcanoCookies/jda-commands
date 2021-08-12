package net.volcano.jdacommands.model.command

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

enum class Source {
	PRIVATE, GUILD, BOTH, DEFAULT;

	fun isCorrect(event: MessageReceivedEvent): Boolean {
		return (event.isFromGuild && this == GUILD) || (!event.isFromGuild && this == PRIVATE) || this == BOTH
	}
}