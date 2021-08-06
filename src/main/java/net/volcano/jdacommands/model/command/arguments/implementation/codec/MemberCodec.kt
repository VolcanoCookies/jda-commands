package net.volcano.jdacommands.model.command.arguments.implementation.codec

import net.dv8tion.jda.api.entities.Member
import net.volcano.jdacommands.model.ParameterData
import net.volcano.jdacommands.model.command.annotations.argument.Arg
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.argument.MemberArgument
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec
import org.springframework.stereotype.Component

@Component
class MemberCodec : Codec<Member?>() {

	public override fun buildArgument(data: ParameterData): CommandArgument<Member?> {
		val ann = data.parameter.annotations.firstOrNull { it is Arg } as Arg?
		return MemberArgument(defaultToCaller = ann?.defaultToCaller ?: false)
	}
}