package net.volcano.jdacommands.model.command.arguments.implementation.argument;

import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.User;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;
import net.volcano.jdautils.utils.UserUtil;

@SuperBuilder
public class UserArgument extends CommandArgument<User> {
	
	protected final boolean defaultToCaller;
	
	@Override
	public User parseValue(ArgumentParsingData data) throws InvalidArgumentsException {
		User user = UserUtil.findUser(data.getArg(), data.event.getJDA(), data.event.isFromGuild() ? data.event.getGuild() : null);
		if (user != null || nullable) {
			return user;
		} else {
			throw new InvalidArgumentsException(data, String.format("User \"%s\" not found.", data.getArg()));
		}
	}
	
}
