package net.volcano.jdacommands.model.command

import net.dv8tion.jda.api.requests.RestAction
import net.volcano.jdacommands.exceptions.command.run.CommandException
import net.volcano.jdacommands.model.command.arguments.ParsedData
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KFunction

class CommandFunction(
	private val function: KFunction<*>,
	private val includeEvent: Boolean
) {

	@Throws(IllegalAccessException::class, CommandException::class)
	operator fun invoke(event: CommandEvent, data: ParsedData): RestAction<*>? {
		val args = mutableListOf(*data.parsedArguments)

		if (includeEvent) args.add(0, event)

		try {
			val returned = function.call(args)
			if (returned is RestAction<*>) {
				return returned
			}
		} catch (e: InvocationTargetException) {
			if (e.targetException is CommandException) {
				throw (e.targetException as CommandException)
			} else {
				e.targetException.printStackTrace()
			}
		}

		return null
	}

}