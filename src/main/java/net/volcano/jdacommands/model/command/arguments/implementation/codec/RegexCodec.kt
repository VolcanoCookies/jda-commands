package net.volcano.jdacommands.model.command.arguments.implementation.codec

import net.volcano.jdacommands.model.ParameterData
import net.volcano.jdacommands.model.command.annotations.argument.Arg
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.argument.RegexArgument
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec
import org.springframework.stereotype.Component

@Component
class RegexCodec : Codec<MatchResult?>() {

	override fun buildArgument(data: ParameterData): CommandArgument<MatchResult?> {

		val ann = data.parameter.annotations.firstOrNull { it is Arg } as Arg?

		require(ann?.regex != null) { "No regex provided for MatchResult parameter." }
		ann!!
		require(ann.regex.isNotEmpty()) { "No regex provided for MatchResult parameter." }

		return RegexArgument(regex = Regex(ann.regex))
	}
}