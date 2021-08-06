package net.volcano.jdacommands.model.command.arguments

import lombok.RequiredArgsConstructor
import net.volcano.jdacommands.exceptions.command.parsing.ArgumentParsingException
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException
import net.volcano.jdacommands.exceptions.command.parsing.MissingArgumentsException
import net.volcano.jdacommands.exceptions.command.parsing.TooManyArgumentsException
import net.volcano.jdacommands.model.command.Command
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData
import net.volcano.jdacommands.model.command.arguments.implementation.RawArgument
import net.volcano.jdautils.utils.asString
import java.lang.reflect.Array
import java.util.*

@RequiredArgsConstructor
class ArgumentList(
	val arguments: List<CommandArgument<*>>,
	val lastIsVarArg: Boolean,
	val command: Command
) {

	/**
	 * Generate usage string based on arguments
	 *
	 * @return the usage string
	 */
	fun generateUsage(): String {
		return arguments.asString(" ") { it.getFormattedUsage() }
	}

	val size: Int
		get() = arguments.size

	operator fun get(index: Int): CommandArgument<*>? {
		return if (arguments.size > index) arguments[index] else null
	}

	@Throws(ArgumentParsingException::class, InvalidArgumentsException::class)
	fun parseArguments(argumentData: ArgumentParsingData): ParsedData {

		if (size == 0 && argumentData.size() > 0) {
			throw TooManyArgumentsException(command, argumentData, 0)
		}

		// If the input raw argument size is bigger than the expected argument size,
		// And the last argument is a "Take All" argument,
		// Trim the raw argument to the expected size, and merge all arguments beyond that size into the last one
		if (size < argumentData.size() && !lastIsVarArg) {

			// Check if any of the arguments after, and including, the last one are in parenthesis
			// If none are, we can merge them, else throw TooManyArgumentsException
			var inQuotations = false
			for (i in size - 1 until argumentData.size()) {
				inQuotations = argumentData.rawArguments[i].inQuotations || inQuotations
			}

			if (!inQuotations) {

				// The last argument is not in parenthesis so we can merge it with anything following it
				argumentData.rawArguments = Arrays.copyOfRange(argumentData.rawArguments, 0, size)
				val (_, startIndex) = argumentData.rawArguments[size - 1]
				// Set the last argument to be a combination of all the ones that exceeded it
				argumentData.rawArguments[size - 1] = RawArgument(
					argumentData.rawContent
						.substring(argumentData.rawArguments[size - 1].startIndex), startIndex, true
				)

			} else {
				throw TooManyArgumentsException(command, argumentData, argumentData.size() - size)
			}
		}

		if (argumentData.size() < size) {
			throw MissingArgumentsException(command, argumentData, argumentData.currentArg, size)
		}

		val data = ParsedData(argumentData.rawArguments)
		data.parsedArguments = arrayOfNulls(size)

		var last: Any? = null
		if (lastIsVarArg) {
			last = Array.newInstance(arguments[size - 1].type as Class<*>, 1 + argumentData.size() - size)
		}

		var j = 0
		for (i in 0 until argumentData.size()) {
			if (i >= size - (if (lastIsVarArg) 1 else 0)) {
				if (lastIsVarArg) {
					Array.set(last, j++, arguments[size - 1].parseValue(argumentData))
				} else {
					throw TooManyArgumentsException(command, argumentData, argumentData.currentArg)
				}
			} else {
				data.parsedArguments[i] = arguments[i].parseValue(argumentData)!!
			}
			argumentData.nextArgument()
		}

		if (lastIsVarArg) {
			data.parsedArguments[data.parsedArguments.size - 1] = last!!
		}

		return data
	}
}