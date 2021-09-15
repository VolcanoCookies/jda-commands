package net.volcano.jdacommands.permissions

data class Permission(
	val value: String
) {

	val path: List<String> = value.split(".")

	override fun toString(): String {
		return value
	}

	override fun equals(other: Any?): Boolean {
		val any = other ?: return false
		if (any !is Permission) return false
		return (this.value == any.value)
	}

}
