package net.volcano.jdacommands.interfaces

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.volcano.jdacommands.permissions.Permission
import net.volcano.jdacommands.permissions.PermissionHolder
import net.volcano.jdacommands.permissions.PermissionResult
import java.time.OffsetDateTime

interface PermissionClient {

	/**
	 * Check a [member]s [permission] without triggering any cooldowns.
	 */
	fun checkPermissions(permission: Permission, member: Member, channel: TextChannel? = null): PermissionResult {
		return checkPermissions(permission, member.user, member.guild, channel)
	}

	/**
	 * Check a [user]s [permission] without triggering any cooldowns.
	 */
	fun checkPermissions(
		permission: Permission,
		user: User,
		guild: Guild? = null,
		channel: TextChannel? = null
	): PermissionResult

	/**
	 * Put this [member]s [holder] on cooldown for time provided by the [holder] itself.
	 */
	fun invokeCooldown(member: Member, holder: PermissionHolder) {
		invokeCooldown(member.user, member.guild, holder)
	}

	/**
	 * Put this [user]s [holder] on cooldown for time provided by the [holder] itself.
	 */
	fun invokeCooldown(user: User, guild: Guild?, holder: PermissionHolder)

	/**
	 * Check if a [member]s [permission] are on cooldown.
	 */
	fun onCooldown(member: Member, holder: PermissionHolder): Boolean {
		return onCooldown(member.user, member.guild, holder)
	}

	/**
	 * Check if a [user]s [permission] are on cooldown.
	 */
	fun onCooldown(user: User, guild: Guild?, holder: PermissionHolder): Boolean

	/**
	 * Get the cooldown of a [member]s [holder].
	 */
	fun getCooldownLeft(member: Member, holder: PermissionHolder): OffsetDateTime? {
		return getCooldownLeft(member.user, member.guild, holder)
	}

	fun getInvocations(user: User, guild: Guild?, holder: PermissionHolder): Int

	/**
	 * Get the cooldown of a [user]s [permission].
	 */
	fun getCooldownLeft(user: User, guild: Guild?, holder: PermissionHolder): OffsetDateTime?

	/**
	 * Get a map with permissions as keys and descriptions as values
	 */
	fun getKnownPermissions(): Map<Permission, String>

}