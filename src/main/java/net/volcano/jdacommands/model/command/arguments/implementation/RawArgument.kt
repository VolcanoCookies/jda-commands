package net.volcano.jdacommands.model.command.arguments.implementation

data class RawArgument(
	val value: String,
	val startIndex: Int,
	val inQuotations: Boolean
)