package net.volcano.jdacommands.model.command.arguments.implementation.codec

import net.volcano.jdacommands.model.ParameterData
import net.volcano.jdacommands.model.command.arguments.implementation.argument.EnumArgument
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec
import net.volcano.jdautils.utils.capitalize
import net.volcano.jdautils.utils.enumConstants
import net.volcano.jdautils.utils.isEnum
import org.springframework.stereotype.Component

@Component
class EnumCodec : Codec<Enum<*>?>() {

	public override fun buildArgument(data: ParameterData): EnumArgument {

		require(data.parameter.type.isEnum) { "Parameter type is not enum." }

		val constants = data.parameter.type.enumConstants

		require(constants != null) { "Parameter type is not enum." }
		require(constants.isNotEmpty()) { "Cannot have empty enum as argument." }

		val arg = EnumArgument(constants as Array<Enum<*>>)
		arg.usage = constants.joinToString(" | ") { it.name.lowercase().capitalize() }

		return arg
	}
}