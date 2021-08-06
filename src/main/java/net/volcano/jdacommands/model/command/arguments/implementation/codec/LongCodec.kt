package net.volcano.jdacommands.model.command.arguments.implementation.codec

import net.volcano.jdacommands.model.ParameterData
import net.volcano.jdacommands.model.command.annotations.argument.Arg
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.argument.LongArgument
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec
import org.springframework.stereotype.Component

@Component
class LongCodec : Codec<Long?>() {

	override fun buildArgument(data: ParameterData): CommandArgument<Long?> {
		val ann = data.parameter.annotations.firstOrNull { it is Arg } as Arg?

		val min = ann?.min?.let { if (it == Long.MIN_VALUE) null else it }
		val max = ann?.max?.let { if (it == Long.MAX_VALUE) null else it }

		return LongArgument(min = min, max = max)
	}
}