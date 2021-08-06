package net.volcano.jdacommands.model.command.arguments.implementation.argument

import net.dv8tion.jda.api.entities.Role
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData
import net.volcano.jdautils.utils.RoleUtil.findRole

class RoleArgument(
	/**
	 * If the role has to be from the same guild as the command is ran in.
	 */
	private val sameGuild: Boolean = true
) : CommandArgument<Role?>() {

	@Throws(InvalidArgumentsException::class)
	override fun parseValue(data: ArgumentParsingData): Role? {
		var role =
			findRole(data.arg, data.event.jda, if (data.event.isFromGuild && sameGuild) data.event.guild else null)

		if (data.event.isFromGuild) {
			if (role != null && sameGuild && role.guild !== data.event.guild) {
				role = null
			}
		}

		if (role != null && !data.event.author.mutualGuilds.contains(role.guild)) {
			role = null
		}
		return if (role != null || nullable) {
			role
		} else {
			throw InvalidArgumentsException(data, "Role \"${data.arg}\" not found.")
		}
	}
}