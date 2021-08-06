package net.volcano.jdacommands.model.command.arguments.implementation.argument

import lombok.experimental.SuperBuilder
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData

@SuperBuilder
class LongArgument(
	/**
	 * Min allowed value of this argument.
	 */
	private val min: Long? = null,
	/**
	 * Max allowed value of this argument.
	 */
	private val max: Long? = null
) : CommandArgument<Long?>() {

	@Throws(InvalidArgumentsException::class)
	override fun parseValue(data: ArgumentParsingData): Long? {

		if (!Regex("-?\\d+").matches(data.arg)) {
			throw InvalidArgumentsException(data, "Expected a non-decimal number.")
		}

		val isNegative = data.arg.startsWith("-")

		val arg = if (isNegative) data.arg.substring(1) else data.arg

		return try {
			var value = arg.toLong()
			value = if (isNegative) -value else value

			if (min != null && value < min) {
				throw InvalidArgumentsException(data, "Expected a number greater than, or equal to $min.")
			} else if (max != null && value > max) {
				throw InvalidArgumentsException(data, "Expected a number less than, or equal to $max.")
			}
			value

		} catch (e: NumberFormatException) {
			throw InvalidArgumentsException(
				data,
				"Could not parse as number, has to be non-decimal number less than ±2^32."
			)
		}
	}

	override val details: String?
		get() = if (min != null && max != null) {
			"$min-$max"
		} else if (min != null) {
			"<$min"
		} else if (max != null) {
			">$max"
		} else {
			super.details
		}
}