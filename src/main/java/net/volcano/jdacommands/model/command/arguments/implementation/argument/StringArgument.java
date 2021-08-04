package net.volcano.jdacommands.model.command.arguments.implementation.argument;

import lombok.experimental.SuperBuilder;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;

@SuperBuilder
public class StringArgument extends CommandArgument<String> {
	
	protected final Integer min;
	protected final Integer max;
	
	/**
	 * If the last argument, take the rest of the input
	 */
	protected final boolean takeRest;
	
	@Override
	public String parseValue(ArgumentParsingData data) throws InvalidArgumentsException {
		
		if (min != null && data.getArg().length() < min) {
			throw new InvalidArgumentsException(data, "String needs to be at least " + min + " characters long.");
		} else if (max != null && data.getArg().length() > max) {
			throw new InvalidArgumentsException(data, "String can be no more than " + max + " characters long.");
		}
		
		return data.getArg();
	}
	
	@Override
	public String getUsage() {
		return "<Text" + (takeRest ? "..." : "") + ">";
	}
	
}
