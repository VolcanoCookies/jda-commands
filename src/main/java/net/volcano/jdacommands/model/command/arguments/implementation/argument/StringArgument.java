package net.volcano.jdacommands.model.command.arguments.implementation.argument;

import lombok.experimental.SuperBuilder;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;

@SuperBuilder
public class StringArgument extends CommandArgument<String> {
	
	/**
	 * If the last argument, take the rest of the input
	 */
	protected final boolean takeRest;
	
	@Override
	public String parseValue(ArgumentParsingData data) throws InvalidArgumentsException {
		return data.getArg();
	}
	
	@Override
	public String getUsage() {
		return "<Text" + (takeRest ? "..." : "") + ">";
	}
	
}
