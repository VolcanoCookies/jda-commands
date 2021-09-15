package net.volcano.jdacommands.permissions

object Permissions {

	private val NODE_REGEX = Regex("((?:[A-z0-9]+\\.)*(?:[A-z0-9]+|\\*))(?:;(\\d{1,32})(?:;(\\d{1,3}))?)?")

	@JvmStatic
	fun parse(permission: String): PermissionHolder {
		val res = NODE_REGEX.matchEntire(permission)
			?: throw IllegalArgumentException("Invalid permission format; $permission")
		val path = res.groupValues[1]
		val cooldown = res.groups[2]?.value?.toLong() ?: 0
		val limit = res.groups[3]?.value?.toInt() ?: 0
		return PermissionHolder(Permission(path), cooldown, limit)
	}

}

