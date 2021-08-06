package net.volcano.jdacommands.model.command.arguments.implementation.argument

import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData

class EnumArgument(
	private val options: Array<Enum<*>>
) : CommandArgument<Enum<*>?>() {

	@Throws(InvalidArgumentsException::class)
	override fun parseValue(data: ArgumentParsingData): Enum<*> {

		for (option in options) {
			if (option.name.equals(data.arg, ignoreCase = true)) {
				return option
			}
		}

		throw InvalidArgumentsException(
			data,
			"Expected one of the following: ${options.joinToString(", ") { it.name }}"
		)

	}

}