package net.volcano.jdacommands.model.command.arguments.implementation.argument

import net.dv8tion.jda.api.entities.TextChannel
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData
import net.volcano.jdautils.utils.ChannelUtil.findTextChannel

class TextChannelArgument : CommandArgument<TextChannel?>() {

	@Throws(InvalidArgumentsException::class)
	override fun parseValue(data: ArgumentParsingData): TextChannel? {
		val channel = findTextChannel(data.arg, data.event.jda, data.event.guild)

		return if (channel != null || nullable) {
			channel
		} else {
			throw InvalidArgumentsException(data, "Channel \"${data.arg}\" not found.")
		}
	}
}