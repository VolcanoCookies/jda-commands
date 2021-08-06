package net.volcano.jdacommands.model.command.arguments.implementation.argument

import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData
import net.volcano.jdautils.utils.TimeUtil
import net.volcano.jdautils.utils.TimeUtil.getDateTimeFromString
import java.time.OffsetDateTime

class OffsetDateTimeArgument : CommandArgument<OffsetDateTime?>() {

	@Throws(InvalidArgumentsException::class)
	override fun parseValue(data: ArgumentParsingData): OffsetDateTime? {

		return try {
			getDateTimeFromString(data.arg)
		} catch (e: TimeUtil.InvalidDateTimeFormatException) {

			if (nullable) return null

			val errorStartIndex: Int =
				data.rawArguments[data.currentArg].startIndex + data.rawPath.length + data.rawPrefix.length + e.errorStartIndex

			throw InvalidArgumentsException(
				data.command,
				data.event.message.contentRaw,
				errorStartIndex,
				e.errorLength,
				e.hint
			)
		}

	}
}