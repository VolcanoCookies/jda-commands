package net.volcano.jdacommands.model.command

import net.dv8tion.jda.api.requests.RestAction
import net.volcano.jdacommands.exceptions.command.run.CommandException
import net.volcano.jdacommands.model.command.arguments.ParsedData
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class CommandFunction(
	private val method: Method,
	private val includeEvent: Boolean,
	private val argumentCount: Int,
	private val instance: Any?
) {

	@Throws(IllegalAccessException::class, CommandException::class)
	operator fun invoke(event: CommandEvent, data: ParsedData): RestAction<*>? {
		val args = arrayOfNulls<Any>(argumentCount)

		var i = 0
		if (includeEvent) {
			args[i++] = event
		}

		while (i < argumentCount) {
			args[i] = data.parsedArguments[if (includeEvent) i - 1 else i]
			i++
		}

		try {
			val returned = method.invoke(instance, *args)
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