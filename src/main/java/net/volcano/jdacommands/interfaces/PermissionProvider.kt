package net.volcano.jdacommands.interfaces

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.volcano.jdacommands.permissions.PermissionHolder

interface PermissionProvider {

	fun getPermissions(event: MessageReceivedEvent): Set<PermissionHolder> {
		return getPermissions(
			event.author,
			if (event.isFromGuild) event.guild else null,
			if (event.isFromGuild) event.textChannel else null
		)
	}

	fun getPermissions(user: User, guild: Guild? = null, channel: TextChannel? = null): Set<PermissionHolder> {
		return getPermissions(user.id, guild?.id, channel?.id)
	}

	fun getPermissions(userId: String, guildId: String? = null, channelId: String? = null): Set<PermissionHolder>

	fun isOverriding(userId: String): Boolean

	fun startOverriding(userId: String)

	fun stopOverriding(userId: String)

}