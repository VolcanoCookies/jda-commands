package net.volcano.jdacommands.interfaces

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.volcano.jdacommands.model.command.CommandEvent

interface PermissionProvider {

	fun getPermissions(userId: String): Set<String>

	fun getPermissions(userId: String, guildId: String?): Set<String>

	fun getPermissions(user: User, guild: Guild?): Set<String> {
		return getPermissions(user.id, guild?.id)
	}

	fun getPermissions(event: CommandEvent): Set<String> {
		return getPermissions(event.author, event.guild)
	}

	fun isOverriding(userId: String): Boolean

	fun startOverriding(userId: String)

	fun stopOverriding(userId: String)

}