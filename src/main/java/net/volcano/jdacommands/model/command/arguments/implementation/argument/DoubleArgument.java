package net.volcano.jdacommands.model.command.arguments.implementation.argument;

import lombok.experimental.SuperBuilder;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;

@SuperBuilder
public class DoubleArgument extends CommandArgument<Double> {
	
	protected final Double min;
	protected final Double max;
	
	@Override
	public Double parseValue(ArgumentParsingData data) throws InvalidArgumentsException {
		
		if (!data.getArg().matches("-?\\d+(\\.\\d+)?")) {
			throw new InvalidArgumentsException(data, "Expected a number");
		}
		
		boolean isNegative = false;
		if (data.getArg().startsWith("-") && data.getArg().length() > 1) {
			isNegative = true;
		}
		
		String arg = isNegative ? data.getArg().substring(1) : data.getArg();
		
		try {
			double val = Double.parseDouble(arg.replaceFirst(",", "."));
			val = isNegative ? -val : val;
			
			if (min != null && val < min) {
				throw new InvalidArgumentsException(data, "Expected a number greater than, or equal to " + min);
			} else if (max != null && val > max) {
				throw new InvalidArgumentsException(data, "Expected a number less than, or equal to " + max);
			}
			
			return val;
		} catch (NumberFormatException e) {
			throw new InvalidArgumentsException(data, "Could not parse as number, has to be decimal number");
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
