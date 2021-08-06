package net.volcano.jdacommands.model.command.arguments.implementation.argument

import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData

class PairArgument(
	private val typeArgument1: CommandArgument<*>,
	private val typeArgument2: CommandArgument<*>
) : CommandArgument<Pair<Any?, Any?>?>() {

	@Throws(InvalidArgumentsException::class)
	override fun parseValue(data: ArgumentParsingData): Pair<*, *> {
		return try {
			Pair<Any?, Any?>(typeArgument1.parseValue(data), null)
		} catch (e: InvalidArgumentsException) {
			Pair<Any?, Any?>(null, typeArgument2.parseValue(data))
		}
	}

}