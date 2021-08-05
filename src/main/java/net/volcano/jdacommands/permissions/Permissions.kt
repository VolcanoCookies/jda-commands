package net.volcano.jdacommands.permissions

class PermissionTree(paths: Collection<String> = emptyList()) {

	val root = PermissionNode("")

	init {
		for (path in paths) {
			add(path)
		}
	}

	fun add(path: String): Boolean {
		val res = NODE_REGEX.matchEntire(path) ?: throw IllegalArgumentException("Invalid path format")
		val paths = res.groupValues[1].split(".")
		val cooldown = res.groups[2]?.value?.toLong() ?: 0
		return root.add(paths, cooldown)
	}

	fun remove(path: String): Boolean {
		val res = NODE_REGEX.matchEntire(path) ?: throw IllegalArgumentException("Invalid path format")
		return root.remove(res.groupValues[1].split("."))
	}

	fun contains(path: String): Boolean {
		val res = NODE_REGEX.matchEntire(path) ?: throw IllegalArgumentException("Invalid path format")
		return root.contains(res.groupValues[1].split("."))
	}

	fun containsAll(paths: Collection<String>): Boolean {
		for (path in paths) {
			if (!contains(path))
				return false
		}
		return true
	}

	fun getCooldown(path: String): Long? {
		val res = NODE_REGEX.matchEntire(path) ?: throw IllegalArgumentException("Invalid path format")
		return root.getCooldown(res.groupValues[1].split("."))
	}

	companion object {

		val NODE_REGEX = Regex("((?:[A-z0-9]+\\.)*(?:[A-z0-9]+|\\*))(?::(\\d{1,32}))?")
	}

}

class PermissionNode(
	val value: String,
	var cooldown: Long = 0
) {

	private val nodes = mutableMapOf<String, PermissionNode>()

	fun add(path: List<String>, cooldown: Long): Boolean {
		return when {
			path.isEmpty() -> throw IllegalArgumentException("Path cannot be empty")
			path.size == 1 -> {
				val node = nodes[path[0]]
				if (node != null) {
					val same = node.cooldown == cooldown
					node.cooldown = cooldown
					same
				} else {
					nodes[path[0]] = PermissionNode(path[0], cooldown)
					true
				}
			}
			else -> {
				val next = nodes[path[0]] ?: PermissionNode(path[0])
				val res = next.add(path.subList(1, path.size), cooldown)
				nodes[path[0]] = next
				res
			}
		}
	}

	fun remove(path: List<String>): Boolean {
		return when {
			path.isEmpty() -> throw IllegalArgumentException("Path cannot be empty")
			path.size == 1 -> nodes.remove(path[0]) != null
			else -> {
				nodes[path[0]]?.remove(path.subList(1, path.size)) ?: false
			}
		}
	}

	fun contains(path: List<String>): Boolean {
		return when {
			path.isEmpty() -> false
			hasWildcard() -> true
			path.size == 1 -> nodes[path[0]] != null
			else -> nodes[path[0]]?.contains(path.subList(1, path.size)) ?: false
		}
	}

	fun getCooldown(path: List<String>): Long? {
		return when {
			path.isEmpty() -> null
			path.size == 1 -> nodes[path[0]]?.cooldown
			else -> nodes[path[0]]?.getCooldown(path.subList(1, path.size))
		}
	}

	private fun hasWildcard(): Boolean {
		return nodes["*"] != null
	}

}

