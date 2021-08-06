package net.volcano.jdacommands.model.command.arguments.implementation.argument

import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData

class BooleanArgument : CommandArgument<Boolean?>() {

	@Throws(InvalidArgumentsException::class)
	override fun parseValue(data: ArgumentParsingData): Boolean? {

		for (aTrue in trues) {
			if (aTrue.equals(data.arg, ignoreCase = true)) {
				return true
			}
		}

		for (aFalse in falses) {
			if (aFalse.equals(data.arg, ignoreCase = true)) {
				return false
			}
		}

		if (nullable)
			return null

		throw InvalidArgumentsException(data, "Expected: True or False")
	}

	companion object {

		private val trues = arrayOf("yes", "true", "1", "confirm", "y")
		private val falses = arrayOf("no", "false", "0", "deny", "n")

	}
}