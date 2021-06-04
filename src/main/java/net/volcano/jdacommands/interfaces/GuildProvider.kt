package net.volcano.jdacommands.interfaces

import net.dv8tion.jda.api.entities.Guild

interface GuildProvider {

	/**
	 * Get a guild entry
	 *
	 * Receiver has to ensure type
	 */
	fun getGuildEntry(guild: Guild): Any? {
		return getGuildEntry(guild.id)
	}

	/**
	 * Get a guild entry
	 *
	 * Receiver has to ensure type
	 */
	fun getGuildEntry(guildId: String): Any?

}