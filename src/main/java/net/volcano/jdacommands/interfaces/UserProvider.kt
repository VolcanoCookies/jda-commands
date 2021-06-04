package net.volcano.jdacommands.interfaces

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User

interface UserProvider {

	/**
	 * Check if a user is banned from running commands
	 *
	 * @param userId the user to check
	 * @return `true` if the user is banned from running ANY commands
	 */
	fun isCommandBanned(userId: String): Boolean {
		return isCommandBanned(userId, null)
	}

	/**
	 * Check if a user is banned from running commands
	 *
	 * @param user the user to check
	 * @return `true` if the user is banned from running ANY commands
	 */
	fun isCommandBanned(user: User): Boolean {
		return isCommandBanned(user.id)
	}

	/**
	 * Check if a user is banned from running commands in a specific guild
	 *
	 * @param userId  the user to check
	 * @param guildId the guild to check in
	 * @return `true` if the user is banned from running ANY commands in the guild
	 */
	fun isCommandBanned(userId: String, guildId: String?): Boolean

	/**
	 * Check if a user is banned from running commands in a specific guild
	 *
	 * @param user  the user to check
	 * @param guild the guild to check in
	 * @return `true` if the user is banned from running ANY commands in the guild
	 */
	fun isCommandBanned(user: User, guild: Guild?): Boolean {
		return isCommandBanned(user.id, guild?.id)
	}

	/**
	 * Get a user entry
	 *
	 * Receiver has to ensure type
	 */
	fun getUserEntry(user: User): Any? {
		return getUserEntry(user.id)
	}

	/**
	 * Get a user entry
	 *
	 * Receiver has to ensure type
	 */
	fun getUserEntry(userId: String): Any?

}