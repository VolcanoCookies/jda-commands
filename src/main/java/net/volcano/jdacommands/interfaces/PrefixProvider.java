package net.volcano.jdacommands.interfaces;

import net.dv8tion.jda.api.entities.Guild;

public interface PrefixProvider {
	
	/**
	 * Get the prefix for a specific guild
	 *
	 * @param guildId the guild to get the prefix for
	 * @return this guilds prefix
	 */
	String getPrefix(String guildId);
	
	/**
	 * Get the prefix for a specific guild
	 *
	 * @param guild the guild to get the prefix for
	 * @return this guilds prefix
	 */
	default String getPrefix(Guild guild) {
		return getPrefix(guild.getId());
	}
	
	/**
	 * Get the prefix to be used if nothing else is specified
	 *
	 * @return the default prefix
	 */
	String getDefault();
	
}


