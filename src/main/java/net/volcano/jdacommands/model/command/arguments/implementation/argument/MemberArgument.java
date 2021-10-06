package net.volcano.jdacommands.model.command.arguments.implementation.argument;

import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;
import net.volcano.jdautilities.utils.UserUtil;

@SuperBuilder
public class MemberArgument extends CommandArgument<Member> {
	
	protected final boolean defaultToCaller;
	
	@Override
	public Member parseValue(ArgumentParsingData data) throws InvalidArgumentsException {
		User user = UserUtil.findUser(data.getArg(), data.event.getJDA(), data.event.isFromGuild() ? data.event.getGuild() : null);
		
		if (user == null && defaultToCaller)
			user = data.event.getAuthor();
		
		if (user == null && nullable) {
			return null;
		} else if (user != null && data.event.isFromGuild()) {
			var member = data.event.getGuild().getMember(user);
			if (member != null || nullable) {
				return member;
			}
		}
		
		throw new InvalidArgumentsException(data, String.format("Member \"%s\" not found.", data.getArg()));
	}
	
}
