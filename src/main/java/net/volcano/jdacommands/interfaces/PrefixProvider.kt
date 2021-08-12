package net.volcano.jdacommands.interfaces

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface PrefixProvider {

	fun getEffctivePrefix(event: MessageReceivedEvent): String {
		return if (event.isFromGuild) getPrefix(event.guild) ?: default else default
	}

	/**
	 * Get the prefix for a specific guild
	 *
	 * @param guildId the guild to get the prefix for
	 * @return this guilds prefix
	 */
	fun getPrefix(guildId: String?): String?

	/**
	 * Get the prefix for a specific guild
	 *
	 * @param guild the guild to get the prefix for
	 * @return this guilds prefix
	 */
	fun getPrefix(guild: Guild): String? {
		return getPrefix(guild.id)
	}

	/**
	 * Get the prefix to be used if nothing else is specified
	 *
	 * @return the default prefix
	 */
	val default: String

}