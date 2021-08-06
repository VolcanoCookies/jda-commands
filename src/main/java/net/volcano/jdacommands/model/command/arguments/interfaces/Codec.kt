package net.volcano.jdacommands.model.command.arguments.interfaces

import net.volcano.jdacommands.model.ParameterData
import net.volcano.jdacommands.model.command.annotations.argument.Arg
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdautils.utils.ClassUtil

abstract class Codec<T> {

	fun encodeArgument(data: ParameterData): CommandArgument<T> {
		require(data.codecClass === ClassUtil.stripWildcard(ClassUtil.getGenericType(javaClass))) { "Invalid codec used for type." }

		val arg = buildArgument(data)
		val ann = data.parameter.annotations.firstOrNull { it is Arg } as Arg?

		if (arg.usage == null)
			arg.usage = ann?.usage?.let { if (it == "DEFAULT") null else it }

		arg.parameter = data.parameter
		arg.type = data.actualClass

		return arg
	}

	protected abstract fun buildArgument(data: ParameterData): CommandArgument<T>

}