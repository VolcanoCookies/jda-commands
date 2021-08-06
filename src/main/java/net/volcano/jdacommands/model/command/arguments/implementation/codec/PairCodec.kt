package net.volcano.jdacommands.model.command.arguments.implementation.codec

import net.volcano.jdacommands.model.ClassUtil.getActualClass
import net.volcano.jdacommands.model.ClassUtil.getCodecClass
import net.volcano.jdacommands.model.ParameterData
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.argument.PairArgument
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec
import org.springframework.stereotype.Component

@Component
class PairCodec : Codec<Pair<*, *>?>() {

	override fun buildArgument(data: ParameterData): CommandArgument<Pair<*, *>?> {

		val clazz1 = data.parameter.type.arguments[0].type!!
		val clazz2 = data.parameter.type.arguments[1].type!!

		require(clazz1 != clazz2) { "Pair argument needs to have two different types." }

		val codec1 = data.codecRegistry.getCodec(getCodecClass(clazz1 as Class<*>))
		val codec2 = data.codecRegistry.getCodec(getCodecClass(clazz2 as Class<*>))

		requireNotNull(codec1) { "Codec for class $clazz1 not found." }
		requireNotNull(codec2) { "Codec for class $clazz2 not found." }

		val arg1 = codec1.encodeArgument(
			ParameterData(
				data.parameter,
				getActualClass(clazz1),
				getCodecClass(clazz1),
				data.codecRegistry
			)
		)

		val arg2 = codec2.encodeArgument(
			ParameterData(
				data.parameter,
				getActualClass(clazz2),
				getCodecClass(clazz2),
				data.codecRegistry
			)
		)

		val pairArg = PairArgument(arg1, arg2)
		pairArg.usage = "<" + arg1.usage + " | " + arg2 + ">"

		return pairArg
	}
}