package net.volcano.jdacommands.model.command.arguments.implementation

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.volcano.jdacommands.model.command.Command
import net.volcano.jdautils.utils.TOKEN_REGEX

val ARGUMENT_REGEX =
	Regex(TOKEN_REGEX, setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE, RegexOption.IGNORE_CASE))

class ArgumentParsingData(
	var event: MessageReceivedEvent,
	var rawPrefix: String,
	var rawPath: String,
	var rawContent: String
) {

	var currentArg = 0

	var rawArguments: Array<RawArgument>

	lateinit var command: Command

	fun nextArgument(): Boolean {
		if (hasNext()) {
			currentArg++
			return true
		}
		return false
	}

	operator fun hasNext(): Boolean {
		return currentArg + 1 < size
	}

	val size: Int
		get() = rawArguments.size

	val arg: String?
		get() = if (rawArguments.size > currentArg) rawArguments[currentArg].value else null

	fun clone(): ArgumentParsingData {
		val data = ArgumentParsingData(
			event,
			rawPrefix,
			rawPath,
			rawContent
		)
		data.currentArg = currentArg
		data.command = command
		return data
	}

	init {
		val argumentList: MutableList<RawArgument> = ArrayList()
		for (result in ARGUMENT_REGEX.findAll(rawContent.trim { it <= ' ' })) {
			val token = result.value
			if (Regex("^(['\"].*['\"])$").matches(token) &&
				token.length > 2
			) {
				argumentList.add(RawArgument(token.substring(1, token.length - 1), result.range.first, true))
			} else {
				argumentList.add(RawArgument(token, result.range.first, false))
			}
		}
		rawArguments = argumentList.toTypedArray()
	}
}