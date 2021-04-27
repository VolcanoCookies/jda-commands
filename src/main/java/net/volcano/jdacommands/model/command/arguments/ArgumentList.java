package net.volcano.jdacommands.model.command.arguments;

import lombok.RequiredArgsConstructor;
import net.volcano.jdacommands.exceptions.command.parsing.ArgumentParsingException;
import net.volcano.jdacommands.exceptions.command.parsing.MissingArgumentsException;
import net.volcano.jdacommands.exceptions.command.parsing.TooManyArgumentsException;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;
import net.volcano.jdautils.utils.ListUtil;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class ArgumentList {
	
	protected final List<CommandArgument> commandArguments;
	
	/**
	 * Generate usage string based on arguments
	 *
	 * @return the usage string
	 */
	public String generateUsage() {
		return ListUtil.asString(" ", commandArguments, CommandArgument::getUsage);
	}
	
	public int size() {
		return commandArguments.size();
	}
	
	public CommandArgument get(int index) {
		return commandArguments.size() > index ? commandArguments.get(index) : null;
	}
	
	public ParsedData parseArguments(ArgumentParsingData argumentData) throws ArgumentParsingException {
		
		var data = new ParsedData(argumentData.rawArguments, argumentData.rawArgumentStartIndex);
		
		// If the input raw argument size is bigger than the expected argument size,
		// And the last argument is a "Take All" argument,
		// Trim the raw argument to the expected size, and merge all arguments beyond that size into the last one
		if (size() < argumentData.size()) {
			argumentData.rawArguments = Arrays.copyOfRange(argumentData.rawArguments, 0, size());
			argumentData.rawArgumentStartIndex = Arrays.copyOfRange(argumentData.rawArgumentStartIndex, 0, size());
			argumentData.rawArguments[size() - 1] = argumentData.event
					.getMessage()
					.getContentRaw()
					.substring(argumentData.rawArgumentStartIndex[size() - 1]);
		}
		
		if (argumentData.size() < size()) {
			throw new MissingArgumentsException(argumentData, argumentData.currentArg, size());
		}
		
		int i = 0;
		data.parsedArguments = new Object[argumentData.size()];
		for (CommandArgument argument : commandArguments) {
			data.parsedArguments[i++] = argument.parseValue(argumentData);
			argumentData.nextArgument();
		}
		if (argumentData.nextArgument()) {
			throw new TooManyArgumentsException(argumentData, argumentData.currentArg);
		}
		return data;
		
	}
	
}

