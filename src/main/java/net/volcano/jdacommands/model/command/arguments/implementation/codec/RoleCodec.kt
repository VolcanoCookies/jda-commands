package net.volcano.jdacommands.model.command.arguments.implementation.codec

import net.dv8tion.jda.api.entities.Role
import net.volcano.jdacommands.model.ParameterData
import net.volcano.jdacommands.model.command.annotations.argument.Arg
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.argument.RoleArgument
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec
import org.springframework.stereotype.Component

@Component
class RoleCodec : Codec<Role?>() {

	override fun buildArgument(data: ParameterData): CommandArgument<Role?> {

		val ann = data.parameter.annotations.firstOrNull { it is Arg } as Arg?

		return RoleArgument(sameGuild = ann?.sameGuild ?: true)

	}
}