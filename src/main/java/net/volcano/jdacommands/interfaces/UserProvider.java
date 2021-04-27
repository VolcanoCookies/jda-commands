package net.volcano.jdacommands.interfaces;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public interface UserProvider {
	
	/**
	 * Check if a user is banned from running commands
	 *
	 * @param userId the user to check
	 * @return {@code true} if the user is banned from running ANY commands
	 */
	boolean isCommandBanned(String userId);
	
	/**
	 * Check if a user is banned from running commands
	 *
	 * @param user the user to check
	 * @return {@code true} if the user is banned from running ANY commands
	 */
	default boolean isCommandBanned(User user) {
		return isCommandBanned(user.getId());
	}
	
	/**
	 * Check if a user is banned from running commands in a specific guild
	 *
	 * @param userId  the user to check
	 * @param guildId the guild to check in
	 * @return {@code true} if the user is banned from running ANY commands in the guild
	 */
	boolean isCommandBanned(String userId, String guildId);
	
	/**
	 * Check if a user is banned from running commands in a specific guild
	 *
	 * @param user  the user to check
	 * @param guild the guild to check in
	 * @return {@code true} if the user is banned from running ANY commands in the guild
	 */
	default boolean isCommandBanned(User user, Guild guild) {
		if (guild != null) {
			return isCommandBanned(user.getId(), guild.getId());
		} else {
			return isCommandBanned(user.getId());
		}
	}
	
}


