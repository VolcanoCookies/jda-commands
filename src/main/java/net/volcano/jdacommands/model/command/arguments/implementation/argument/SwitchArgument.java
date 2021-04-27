package net.volcano.jdacommands.model.command.arguments.implementation.argument;

import lombok.experimental.SuperBuilder;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;
import net.volcano.jdautils.utils.ListUtil;

@SuperBuilder
public class SwitchArgument extends CommandArgument<Enum<?>> {
	
	protected final Enum<?>[] options;
	
	@Override
	public Enum<?> parseValue(ArgumentParsingData data) throws InvalidArgumentsException {
		
		for (Enum<?> option : options) {
			if (option.name().equalsIgnoreCase(data.getArg())) {
				return option;
			}
		}
		
		throw new InvalidArgumentsException(data,
				String.format("Expected one of the following: %s",
						ListUtil.asString(", ", options, Enum::name)));
	}
	
	@Override
	public String getUsage() {
		return "<" + ListUtil.asString(", ", options, Enum::name) + ">";
	}
}
