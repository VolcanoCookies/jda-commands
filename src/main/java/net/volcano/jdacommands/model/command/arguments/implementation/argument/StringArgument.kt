package net.volcano.jdacommands.model.command.arguments.implementation.argument

import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData

class StringArgument(
	/**
	 * The minimum length of the input.
	 */
	private val min: Int? = 1,
	/**
	 * The maximum length of the input.
	 */
	private val max: Int? = null
) : CommandArgument<String?>() {

	@Throws(InvalidArgumentsException::class)
	override fun parseValue(data: ArgumentParsingData): String? {
		if (min != null && data.arg.length < min) {
			throw InvalidArgumentsException(data, "String needs to be at least $min characters long.")
		} else if (max != null && data.arg.length > max) {
			throw InvalidArgumentsException(data, "String can be no more than $max characters long.")
		}
		return data.arg
	}
}