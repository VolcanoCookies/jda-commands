package net.volcano.jdacommands.client

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.volcano.jdacommands.interfaces.PermissionClient
import net.volcano.jdacommands.interfaces.PermissionProvider
import net.volcano.jdacommands.interfaces.QueryResult
import net.volcano.jdacommands.permissions.PermissionTree
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class PermissionClientImpl(
	val provider: PermissionProvider
) : PermissionClient {

	// GuildID -> UserID -> Path -> Expiration Time
	private val guildCooldowns = mutableMapOf<String, MutableMap<String, MutableMap<String, OffsetDateTime>>>()

	// UserID -> Path -> Expiration Time
	private val globalCooldowns = mutableMapOf<String, MutableMap<String, OffsetDateTime>>()

	override fun checkPermissions(user: User, guild: Guild?, permission: String): QueryResult {
		val perms = PermissionTree(provider.getPermissions(user, guild))
		return QueryResult(perms.contains(permission), getCooldown(user, guild, permission))
	}

	override fun putCooldown(user: User, guild: Guild?, permission: String, time: Long): OffsetDateTime {
		val dateTime = OffsetDateTime.now().plusSeconds(time)
		if (guild != null) {
			guildCooldowns.putIfAbsent(guild.id, mutableMapOf())
			guildCooldowns[guild.id]!!.putIfAbsent(user.id, mutableMapOf())
			guildCooldowns[guild.id]!![user.id]!![permission] = dateTime
		} else {
			globalCooldowns.putIfAbsent(user.id, mutableMapOf())
			globalCooldowns[user.id]!![permission] = dateTime
		}

		return dateTime
	}

	override fun invokeCooldown(user: User, guild: Guild?, permission: String) {
		val perms = PermissionTree(provider.getPermissions(user, guild))
		perms.getCooldown(permission)?.let {
			putCooldown(user, guild, permission, it)
		}
	}

	override fun onCooldown(user: User, guild: Guild?, permission: String): Boolean {
		return if (guild != null) {
			guildCooldowns[guild.id]?.get(user.id)?.get(permission) != null
		} else {
			globalCooldowns[user.id]?.get(permission) != null
		}
	}

	override fun getCooldown(user: User, guild: Guild?, permission: String): OffsetDateTime? {
		return if (guild != null) {
			guildCooldowns[guild.id]?.get(user.id)?.get(permission)
		} else {
			globalCooldowns[user.id]?.get(permission)
		}
	}

	@Scheduled(fixedRate = 1000 * 60 * 5)
	fun clearExpiredCooldowns() {
		val now = OffsetDateTime.now()
		for ((userId, permissions) in globalCooldowns) {
			for ((permission, time) in permissions) {
				if (time.isBefore(now))
					permissions.remove(permission)
			}
			if (permissions.isEmpty())
				globalCooldowns.remove(userId)
		}

		for ((guildId, memberCooldowns) in guildCooldowns) {
			for ((memberId, permissions) in memberCooldowns) {
				for ((permission, time) in permissions) {
					if (time.isBefore(now))
						permissions.remove(permission)
				}
				if (permissions.isEmpty())
					memberCooldowns.remove(memberId)
			}
			if (memberCooldowns.isEmpty())
				guildCooldowns.remove(guildId)
		}

	}

}