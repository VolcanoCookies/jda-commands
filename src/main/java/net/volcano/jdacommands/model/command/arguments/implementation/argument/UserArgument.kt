package net.volcano.jdacommands.model.command.arguments.implementation.argument

import net.dv8tion.jda.api.entities.User
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData
import net.volcano.jdautils.utils.UserUtil.findUser

class UserArgument(
	/**
	 * If the argument should default to the command caller in-case another user is not found.
	 */
	private val defaultToCaller: Boolean = false
) : CommandArgument<User?>() {

	@Throws(InvalidArgumentsException::class)
	override fun parseValue(data: ArgumentParsingData): User? {
		val user = findUser(data.arg, data.event.jda, if (data.event.isFromGuild) data.event.guild else null)

		return if (user != null)
			user
		else if (defaultToCaller)
			data.event.author
		else if (nullable)
			null
		else
			throw InvalidArgumentsException(data, "User \"${data.arg}\" not found.")
	}

}