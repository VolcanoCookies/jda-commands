package net.volcano.jdacommands.model.command.arguments.implementation.argument;

import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;
import net.volcano.jdautilities.utils.ChannelUtil;

@SuperBuilder
public class TextChannelArgument extends CommandArgument<TextChannel> {
	
	protected final boolean sameGuild;
	
	@Override
	public TextChannel parseValue(ArgumentParsingData data) throws InvalidArgumentsException {
		TextChannel channel = ChannelUtil.findTextChannel(data.getArg(), data.event.getJDA(), data.event.getGuild());
		
		if (channel != null || nullable) {
			return channel;
		} else {
			throw new InvalidArgumentsException(data, String.format("Channel \"%s\" not found.", data.getArg()));
		}
	}
	
}
