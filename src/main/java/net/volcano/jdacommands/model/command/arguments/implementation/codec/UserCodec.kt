package net.volcano.jdacommands.model.command.arguments.implementation.codec

import net.dv8tion.jda.api.entities.User
import net.volcano.jdacommands.model.ParameterData
import net.volcano.jdacommands.model.command.annotations.argument.Arg
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.argument.UserArgument
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec
import org.springframework.stereotype.Component

@Component
class UserCodec : Codec<User?>() {

	public override fun buildArgument(data: ParameterData): CommandArgument<User?> {
		val ann = data.parameter.annotations.firstOrNull { it is Arg } as Arg?
		return UserArgument(defaultToCaller = ann?.defaultToCaller ?: false)
	}
}