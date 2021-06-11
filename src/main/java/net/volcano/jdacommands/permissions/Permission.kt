package net.volcano.jdacommands.permissions

class Permission {

	val roots: MutableSet<Node> = mutableSetOf()

}

class Node(
	val value: String,
	val flags: MutableSet<String>
) {

	fun add(paths: Array<String>) {
		if (paths.isEmpty())
			return
	}

}