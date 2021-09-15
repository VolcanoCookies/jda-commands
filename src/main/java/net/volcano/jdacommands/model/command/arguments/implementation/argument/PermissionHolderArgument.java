package net.volcano.jdacommands.model.command.arguments.implementation.argument;

import lombok.experimental.SuperBuilder;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;
import net.volcano.jdacommands.permissions.PermissionHolder;
import net.volcano.jdacommands.permissions.Permissions;

@SuperBuilder
public class PermissionHolderArgument extends CommandArgument<PermissionHolder> {
	
	protected final boolean defaultToCaller;
	
	@Override
	public PermissionHolder parseValue(ArgumentParsingData data) throws InvalidArgumentsException {
		try {
			return Permissions.parse(data.getArg());
		} catch (IllegalArgumentException e) {
			throw new InvalidArgumentsException(data, "Invalid permission format: '" + data.getArg() + "'");
		}
	}
	
}
