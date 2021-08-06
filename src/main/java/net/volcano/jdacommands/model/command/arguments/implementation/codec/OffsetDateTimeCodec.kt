package net.volcano.jdacommands.model.command.arguments.implementation.codec

import net.volcano.jdacommands.model.ParameterData
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.argument.OffsetDateTimeArgument
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class OffsetDateTimeCodec : Codec<OffsetDateTime?>() {

	override fun buildArgument(data: ParameterData): CommandArgument<OffsetDateTime?> {
		return OffsetDateTimeArgument()
	}
}