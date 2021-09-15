package net.volcano.jdacommands.permissions

data class PermissionHolder(
	val permission: Permission,
	val cooldown: Long,
	val limit: Int
) {

	override fun toString(): String {
		var s = permission.value
		if (cooldown > 0) {
			s += ";$cooldown"
			if (limit > 1)
				s += ";$limit"
		}
		return s
	}

	companion object {

		fun parse(input: String): PermissionHolder {
			val (permission, cooldown, limit) = Permissions.parse(input)
			return PermissionHolder(permission, cooldown, limit)
		}
	}

}