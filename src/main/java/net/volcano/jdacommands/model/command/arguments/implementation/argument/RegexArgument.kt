package net.volcano.jdacommands.model.command.arguments.implementation.argument

import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData

class RegexArgument(
	/**
	 * The regex of this argument.
	 */
	private val regex: Regex,
) : CommandArgument<MatchResult?>() {

	@Throws(InvalidArgumentsException::class)
	override fun parseValue(data: ArgumentParsingData): MatchResult? {

		val matcher = regex.matchEntire(data.arg)

		if (matcher != null || nullable) {
			return matcher
		}

		throw InvalidArgumentsException(data, "Needs to match the following regex: `$regex`")
	}

}