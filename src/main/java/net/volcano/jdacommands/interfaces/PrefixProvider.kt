package net.volcano.jdacommands.interfaces

import net.dv8tion.jda.api.entities.Guild

interface PrefixProvider {

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