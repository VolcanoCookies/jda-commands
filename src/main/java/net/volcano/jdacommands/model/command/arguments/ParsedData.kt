package net.volcano.jdacommands.model.command.arguments

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.volcano.jdacommands.model.command.Command
import net.volcano.jdacommands.model.command.arguments.implementation.RawArgument

/**
 * This class represents the finished parsed data made available when a command successfully parses all of its arguments
 */
class ParsedData(
	/**
	 * The raw arguments provided
	 */
	var rawArguments: Array<RawArgument>
) {

	/**
	 * The resulting parsed arguments
	 */
	lateinit var parsedArguments: Array<Any?>

	/**
	 * The [Command] this data was created against
	 */
	lateinit var command: Command

	/**
	 * The event this data was parsed from
	 */
	lateinit var event: MessageReceivedEvent
}