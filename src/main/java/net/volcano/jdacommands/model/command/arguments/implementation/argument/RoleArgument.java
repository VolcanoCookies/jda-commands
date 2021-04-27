package net.volcano.jdacommands.model.command.arguments.implementation.argument;

import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;
import net.volcano.jdautils.utils.RoleUtil;

@SuperBuilder
public class RoleArgument extends CommandArgument<Role> {
	
	protected final boolean sameGuild;
	
	@Override
	public Role parseValue(ArgumentParsingData data) throws InvalidArgumentsException {
		Role role = RoleUtil.findRole(data.getArg(), data.event.getJDA(), data.event.isFromGuild() ? data.event.getGuild() : null);
		if (role != null || nullable) {
			return role;
		} else {
			throw new InvalidArgumentsException(data, String.format("Role \"%s\" not found.", data.getArg()));
		}
	}
	
}
