package net.volcano.jdacommands.model.command.arguments.implementation.argument;

import lombok.experimental.SuperBuilder;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;

@SuperBuilder
public class IntegerArgument extends CommandArgument<Integer> {
	
	protected final int min;
	protected final int max;
	
	@Override
	public Integer parseValue(ArgumentParsingData data) throws InvalidArgumentsException {
		
		if (!data.getArg().matches("-?\\d+")) {
			throw new InvalidArgumentsException(data, "Expected a non-decimal number");
		}
		
		boolean isNegative = false;
		if (data.getArg().startsWith("-") && data.getArg().length() > 1) {
			isNegative = true;
		}
		
		String arg = isNegative ? data.getArg().substring(1) : data.getArg();
		
		try {
			int val = Integer.parseInt(arg);
			val = isNegative ? -val : val;
			
			if (val < min) {
				throw new InvalidArgumentsException(data, "Expected a number greater than, or equal to " + min);
			} else if (val > max) {
				throw new InvalidArgumentsException(data, "Expected a number less than, or equal to " + max);
			}
			
			return val;
		} catch (NumberFormatException e) {
			throw new InvalidArgumentsException(data, "Could not parse as number, has to be non-decimal number less than ±2^16");
		}
	}
	
}
