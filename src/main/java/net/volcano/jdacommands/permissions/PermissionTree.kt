package net.volcano.jdacommands.permissions

import kotlin.math.max

class PermissionTree(
	vararg holders: PermissionHolder
) {

	private val root = Node()

	constructor(holders: Set<PermissionHolder>) : this(*holders.toTypedArray())

	init {
		for (holder in holders) {
			add(holder)
		}
	}

	fun add(holder: PermissionHolder) {
		var current = root
		var wildcard = false
		for (key in holder.permission.path) {
			current[key] = current[key] ?: Node()
			current = current[key]!!
			wildcard = key == "*"
		}
		current.isEnding = true
		current.isWildcard = current.isWildcard || wildcard
		current.cooldown = holder.cooldown
		current.limit = holder.limit
	}

	fun get(permission: Permission): PermissionHolder? {
		var current = root
		val taken = mutableListOf<String>()
		for (key in permission.path) {
			current = current[key] ?: return null

			if (current.isWildcard && current.isEnding) {
				taken += "*"
				break
			}
			taken += key
		}
		if (current.isEnding)
			return PermissionHolder(Permission(taken.joinToString(".")), current.cooldown, current.limit)

		return null
	}

	fun has(permission: Permission): Boolean {
		return get(permission) != null
	}

	private class Node(
		cooldown: Long = 0,
		limit: Int = 1,
		var isWildcard: Boolean = false,
		var isEnding: Boolean = false
	) {

		private var _cooldown: Long = 0
		var cooldown: Long
			get() = _cooldown
			set(value) {
				_cooldown = max(0, max(_cooldown, value))
			}

		private var _limit: Int = 0
		var limit: Int
			get() = _limit
			set(value) {
				_limit = max(1, max(_limit, value))
			}

		private val nodes: MutableMap<String, Node> = HashMap()

		operator fun get(key: String): Node? {
			return nodes["*"] ?: nodes[key]
		}

		operator fun set(key: String, node: Node) {
			nodes[key] = node
		}

	}

}