package net.volcano.jdacommands.model.command.arguments.implementation.argument;

import lombok.experimental.SuperBuilder;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;

@SuperBuilder
public class LongArgument extends CommandArgument<Long> {
	
	protected final Long min;
	protected final Long max;
	
	@Override
	public Long parseValue(ArgumentParsingData data) throws InvalidArgumentsException {
		
		if (!data.getArg().matches("-?\\d+")) {
			throw new InvalidArgumentsException(data, "Expected a non-decimal number");
		}
		
		boolean isNegative = false;
		if (data.getArg().startsWith("-") && data.getArg().length() > 1) {
			isNegative = true;
		}
		
		String arg = isNegative ? data.getArg().substring(1) : data.getArg();
		
		try {
			long val = Long.parseLong(arg);
			val = isNegative ? -val : val;
			
			if (min != null && val < min) {
				throw new InvalidArgumentsException(data, "Expected a number greater than, or equal to " + min);
			} else if (max != null && val > max) {
				throw new InvalidArgumentsException(data, "Expected a number less than, or equal to " + max);
			}
			
			return val;
		} catch (NumberFormatException e) {
			throw new InvalidArgumentsException(data, "Could not parse as number, has to be non-decimal number less than ±2^32");
		}
	}
	
	@Override
	public String getDetails() {
		if (min != null && max != null) {
			return min + "-" + max;
		} else if (min != null) {
			return "<" + min;
		} else if (max != null) {
			return ">" + max;
		} else {
			return super.getDetails();
		}
	}
}
