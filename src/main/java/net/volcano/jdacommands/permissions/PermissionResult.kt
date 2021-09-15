package net.volcano.jdacommands.permissions

import java.time.OffsetDateTime

data class PermissionResult(
	/**
	 * If the user holds the required permissions, regardless of any cooldown.
	 */
	val holdsPermissions: Boolean,
	/**
	 * The holder for the permissions, if the user has them.
	 */
	val holder: PermissionHolder?,
	val expiration: OffsetDateTime?,
	val cooldown: Long,
	val limit: Int,
	// number of invocations left before limit is hit
	val limitLeft: Int
) {

	/**
	 * If the permission is on cooldown.
	 */
	val onCooldown: Boolean
		get() = expiration != null

	/**
	 * If the user can actually use the permissions.
	 * Takes cooldown into account, returning true only if it is not on cooldown.
	 */
	val hasPermissions: Boolean
		get() = holdsPermissions && !onCooldown

	companion object {

		val NO_PERMISSIONS = PermissionResult(false, null, null, 0, 0, 0)

	}

}