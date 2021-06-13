package net.volcano.jdacommands.interfaces

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import java.time.OffsetDateTime

interface PermissionClient {

	/**
	 * Check a [member]s [permission] without triggering any cooldowns.
	 */
	fun checkPermissions(member: Member, permission: String): QueryResult {
		return checkPermissions(member.user, member.guild, permission)
	}

	/**
	 * Check a [user]s [permission] without triggering any cooldowns.
	 */
	fun checkPermissions(user: User, guild: Guild?, permission: String): QueryResult

	/**
	 * Put this [member]s [permission] on cooldown for the specified [time] of seconds.
	 */
	fun putCooldown(member: Member, permission: String, time: Long): OffsetDateTime {
		return putCooldown(member.user, member.guild, permission, time)
	}

	/**
	 * Put this [user]s [permission] on cooldown for the specified [time] of seconds.
	 */
	fun putCooldown(user: User, guild: Guild?, permission: String, time: Long): OffsetDateTime

	/**
	 * Put this [member]s [permission] on cooldown for time provided by the [permission] itself.
	 */
	fun invokeCooldown(member: Member, permission: String) {
		invokeCooldown(member.user, member.guild, permission)
	}

	/**
	 * Put this [user]s [permission] on cooldown for time provided by the [permission] itself.
	 */
	fun invokeCooldown(user: User, guild: Guild?, permission: String)

	/**
	 * Check if a [member]s [permission] are on cooldown.
	 */
	fun onCooldown(member: Member, permission: String): Boolean {
		return onCooldown(member.user, member.guild, permission)
	}

	/**
	 * Check if a [user]s [permission] are on cooldown.
	 */
	fun onCooldown(user: User, guild: Guild?, permission: String): Boolean

	/**
	 * Get the cooldown of a [member]s [permission].
	 */
	fun getCooldown(member: Member, permission: String): OffsetDateTime? {
		return getCooldown(member.user, member.guild, permission)
	}

	/**
	 * Get the cooldown of a [user]s [permission].
	 */
	fun getCooldown(user: User, guild: Guild?, permission: String): OffsetDateTime?

}

class QueryResult(
	val hasPermissions: Boolean,
	val cooldownExpiration: OffsetDateTime? = null
) {

	val onCooldown = cooldownExpiration != null
	val hasEffectivePermissions = hasPermissions && !onCooldown

}