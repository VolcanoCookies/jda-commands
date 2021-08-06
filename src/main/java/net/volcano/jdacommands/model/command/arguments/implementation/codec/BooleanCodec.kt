package net.volcano.jdacommands.model.command.arguments.implementation.codec

import net.volcano.jdacommands.model.ParameterData
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.argument.BooleanArgument
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec
import org.springframework.stereotype.Component

@Component
class BooleanCodec : Codec<Boolean?>() {

	override fun buildArgument(data: ParameterData): CommandArgument<Boolean?> {
		return BooleanArgument()
	}
}