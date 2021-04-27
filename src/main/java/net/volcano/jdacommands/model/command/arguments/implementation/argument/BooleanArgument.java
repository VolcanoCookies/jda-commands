package net.volcano.jdacommands.model.command.arguments.implementation.argument;

import lombok.experimental.SuperBuilder;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;

@SuperBuilder
public class BooleanArgument extends CommandArgument<Boolean> {
	
	private static final String[] trues = {"yes", "true", "1", "confirm", "y"};
	private static final String[] falses = {"no", "false", "0", "deny", "n"};
	
	@Override
	public Boolean parseValue(ArgumentParsingData data) throws InvalidArgumentsException {
		
		for (String aTrue : trues) {
			if (aTrue.equalsIgnoreCase(data.getArg())) {
				return true;
			}
		}
		
		for (String aFalse : falses) {
			if (aFalse.equalsIgnoreCase(data.getArg())) {
				return false;
			}
		}
		
		throw new InvalidArgumentsException(data, "Expected: True or False");
	}
	
	@Override
	public String getUsage() {
		return "<True | False>";
	}
	
}
