package net.volcano.jdacommands.model.command.arguments.implementation.argument;

import lombok.experimental.SuperBuilder;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;
import net.volcano.jdautils.utils.TimeUtil;

import java.time.OffsetDateTime;

@SuperBuilder
public class OffsetDateTimeArgument extends CommandArgument<OffsetDateTime> {
	
	@Override
	public OffsetDateTime parseValue(ArgumentParsingData data) throws InvalidArgumentsException {
		try {
			return TimeUtil.getDateTimeFromString(data.getArg());
		} catch (TimeUtil.InvalidDateTimeFormatException e) {
			var errorStartIndex = data.rawArguments[data.currentArg].startIndex + data.rawPath.length() + data.rawPrefix.length() + e.getErrorStartIndex();
			throw new InvalidArgumentsException(data.command, data.event.getMessage().getContentRaw(), errorStartIndex, e.getErrorLength(), e.getHint());
		}
	}
	
	@Override
	public String getUsage() {
		return "<Date>";
	}
	
}
