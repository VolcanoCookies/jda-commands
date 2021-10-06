package net.volcano.jdacommands.client

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.volcano.jdacommands.interfaces.CommandClient
import net.volcano.jdacommands.interfaces.PermissionClient
import net.volcano.jdacommands.interfaces.PermissionProvider
import net.volcano.jdacommands.permissions.*
import net.volcano.jdautilities.utils.asString
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap

@Service
class PermissionClientImpl(
	val provider: PermissionProvider
) : PermissionClient {

	private data class CooldownKey(
		val user: User,
		val guild: Guild?,
		val permission: Permission
	) {

		override fun equals(other: Any?): Boolean {
			val any = other ?: return false
			if (any !is CooldownKey) return false
			return (this.user == any.user) && (this.guild == any.guild) && (this.permission == any.permission)
		}
	}

	private data class CooldownEntry(
		val expiration: OffsetDateTime,
		val invocation: OffsetDateTime
	) {

		constructor(cooldown: Long) : this(
			OffsetDateTime.now().plusSeconds(cooldown),
			OffsetDateTime.now()
		)

		val hasExpired: Boolean
			get() = OffsetDateTime.now().isAfter(expiration)
	}

	private val cooldowns: MutableMap<CooldownKey, MutableList<CooldownEntry>> = ConcurrentHashMap()

	lateinit var client: CommandClient

	override fun checkPermissions(
		permission: Permission,
		user: User,
		guild: Guild?,
		channel: TextChannel?
	): PermissionResult {
		val tree = getTree(user, guild, channel)
		val holder = tree.get(permission) ?: return PermissionResult.NO_PERMISSIONS

		return PermissionResult(
			true,
			holder,
			getCooldownLeft(user, guild, holder),
			holder.cooldown,
			holder.limit,
			holder.limit - getInvocations(user, guild, holder)
		)
	}

	override fun invokeCooldown(user: User, guild: Guild?, holder: PermissionHolder) {
		if (holder.cooldown == 0L) return
		val key = CooldownKey(user, guild, holder.permission)
		val expiration = CooldownEntry(holder.cooldown)

		synchronized(cooldowns) {
			if (cooldowns[key] != null)
				cooldowns[key]!! += expiration
			else
				cooldowns[key] = mutableListOf(expiration)
		}
	}

	override fun onCooldown(user: User, guild: Guild?, holder: PermissionHolder): Boolean {
		return getCooldownLeft(user, guild, holder) != null
	}

	override fun getCooldownLeft(user: User, guild: Guild?, holder: PermissionHolder): OffsetDateTime? {
		val key = CooldownKey(user, guild, holder.permission)

		if (getInvocations(user, guild, holder) < holder.limit) return null

		return cooldowns[key]?.filter { !it.hasExpired }
			?.sortedBy { it.expiration.toEpochSecond() }
			?.takeLast(holder.limit)
			?.firstOrNull()
			?.expiration
	}

	override fun getInvocations(user: User, guild: Guild?, holder: PermissionHolder): Int {
		val key = CooldownKey(user, guild, holder.permission)
		return cooldowns[key]?.count { !it.hasExpired } ?: 0
	}

	override fun getKnownPermissions(): Map<Permission, String> {
		val commandPermissions = this.client.allCommands
			.groupBy { it.permission }
			.mapValues {
				if (it.value.size == 1) {
					"Execute command ${it.value[0].paths[0]}"
				} else {
					"Execute commands ${it.value.asString(",") { c -> c.paths[0] }}"
				}
			}

		val explicitHelpPermissions = this.client.allCommands
			.mapNotNull {
				it.help?.permissions?.map { p ->
					p.split(":", limit = 2)
						.let { a -> Pair(Permissions.parse(a[0]).permission, a[1]) }
				}
			}
			.flatten()
			.toMap()

		return commandPermissions + explicitHelpPermissions
	}

	private fun getTree(user: User, guild: Guild?, channel: TextChannel?): PermissionTree {
		val permissions = provider.getPermissions(user, guild, channel)
		return PermissionTree(permissions)
	}

	@Scheduled(fixedRate = 1000 * 60 * 30)
	fun clearExpiredCooldowns() {
		val now = OffsetDateTime.now()

		synchronized(cooldowns) {
			val empty = cooldowns.filter { it.value.all { v -> v.hasExpired } }.keys
			for (key in empty) {
				cooldowns.remove(key)
			}

			cooldowns.forEach {
				it.value.removeIf { v -> v.hasExpired }
			}
		}

	}

	fun printCooldowns() {
		println(cooldowns)
	}

}