package net.volcano.jdacommands.model.command.arguments.implementation.argument;

import lombok.experimental.SuperBuilder;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuperBuilder
public class RegexArgument extends CommandArgument<Matcher> {
	
	protected final String regex;
	
	protected final int flags;
	
	protected final String usage;
	
	@Override
	public Matcher parseValue(ArgumentParsingData data) throws InvalidArgumentsException {
		Matcher matcher = Pattern.compile(regex, flags).matcher(data.getArg());
		
		if (matcher.matches()) {
			return matcher;
		}
		
		throw new InvalidArgumentsException(data, "Needs to match the following regex: `" + regex + "`");
	}
	
	@Override
	public String getUsage() {
		if (usage.length() == 0) {
			return "<Regex: " + regex + ">";
		} else {
			return usage;
		}
	}
	
}
