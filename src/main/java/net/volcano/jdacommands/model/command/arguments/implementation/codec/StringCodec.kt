package net.volcano.jdacommands.model.command.arguments.implementation.codec

import net.volcano.jdacommands.model.ParameterData
import net.volcano.jdacommands.model.command.annotations.argument.Arg
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.argument.StringArgument
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec
import org.springframework.stereotype.Component

@Component
class StringCodec : Codec<String?>() {

	override fun buildArgument(data: ParameterData): CommandArgument<String?> {

		val ann = data.parameter.annotations.firstOrNull { it is Arg } as Arg?

		val min = ann?.min?.let { if (it == Long.MIN_VALUE) null else it.toInt() }
		val max = ann?.max?.let { if (it == Long.MAX_VALUE) null else it.toInt() }

		return StringArgument(min = min, max = max)
	}
}