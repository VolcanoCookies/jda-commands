package net.volcano.jdacommands.permissions

internal class PermissionTreeTest {

	val tree = PermissionTree()

	@org.junit.jupiter.api.BeforeEach
	fun setUp() {
		tree.add("some.permissions.here")
		tree.add("other.permissions.here.*")
	}

	@org.junit.jupiter.api.Test
	fun contains() {
		assert(tree.contains("some.permissions"))
		assert(!tree.contains("some.permissions.here.and.there"))
		assert(tree.contains("other.permissions.here.and.there"))
	}

}