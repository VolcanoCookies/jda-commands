package net.volcano.jdacommands.model.command.arguments.implementation.argument

import net.dv8tion.jda.api.entities.Member
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData
import net.volcano.jdautils.utils.UserUtil.findUser

class MemberArgument(
	/**
	 * If the argument should default to the command caller in-case another user is not found.
	 */
	private val defaultToCaller: Boolean = false
) : CommandArgument<Member?>() {

	@Throws(InvalidArgumentsException::class)
	override fun parseValue(data: ArgumentParsingData): Member? {
		val user = findUser(data.arg, data.event.jda, if (data.event.isFromGuild) data.event.guild else null)

		if (user == null && defaultToCaller) return data.event.member!!

		if (user == null && nullable) {
			return null
		} else if (user != null && data.event.isFromGuild) {
			val member = data.event.guild.getMember(user)
			if (member != null || nullable) {
				return member!!
			}
		}
		throw InvalidArgumentsException(data, "Member \"${data.arg}\" not found.")
	}

}