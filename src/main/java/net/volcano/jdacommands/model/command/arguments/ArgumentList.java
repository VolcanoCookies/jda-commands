package net.volcano.jdacommands.model.command.arguments;

import lombok.RequiredArgsConstructor;
import net.volcano.jdacommands.exceptions.command.parsing.ArgumentParsingException;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.exceptions.command.parsing.MissingArgumentsException;
import net.volcano.jdacommands.exceptions.command.parsing.TooManyArgumentsException;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;
import net.volcano.jdacommands.model.command.arguments.implementation.RawArgument;
import net.volcano.jdautils.utils.ListUtil;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class ArgumentList {
	
	protected final List<CommandArgument> commandArguments;
	
	public final boolean lastIsArbitraryNumber;
	
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
	
	public ParsedData parseArguments(ArgumentParsingData argumentData) throws ArgumentParsingException, InvalidArgumentsException {
		
		if (size() == 0 && argumentData.size() > 0) {
			throw new TooManyArgumentsException(argumentData, 0);
		}
		
		// If the input raw argument size is bigger than the expected argument size,
		// And the last argument is a "Take All" argument,
		// Trim the raw argument to the expected size, and merge all arguments beyond that size into the last one
		if (size() < argumentData.size() && !lastIsArbitraryNumber) {
			
			// Check if any of the arguments after, and including, the last one are in parenthesis
			// If none are, we can merge them, else throw TooManyArgumentsException
			boolean inQuotations = false;
			for (int i = size() - 1; i < argumentData.size(); i++) {
				inQuotations = argumentData.rawArguments[i].inQuotations || inQuotations;
			}
			
			if (!inQuotations) {
				// The last argument is not in parenthesis so we can merge it with anything following it
				argumentData.rawArguments = Arrays.copyOfRange(argumentData.rawArguments, 0, size());
				var builder = RawArgument.builder();
				
				var lastArgument = argumentData.rawArguments[size() - 1];
				
				builder.startIndex(lastArgument.startIndex);
				builder.inQuotations(true);
				builder.value(argumentData.rawContent
						.substring(argumentData.rawArguments[size() - 1].startIndex));
				// Set the last argument to be a combination of all the ones that exceeded it
				argumentData.rawArguments[size() - 1] = builder.build();
				
			} else {
				throw new TooManyArgumentsException(argumentData, argumentData.size() - size());
			}
		}
		
		if (argumentData.size() < size()) {
			throw new MissingArgumentsException(argumentData, argumentData.currentArg, size());
		}
		
		var data = new ParsedData(argumentData.rawArguments);
		
		data.parsedArguments = new Object[size()];
		
		Object last = null;
		if (lastIsArbitraryNumber) {
			last = Array.newInstance((Class<?>) commandArguments.get(size() - 1).type, 1 + argumentData.size() - size());
		}
		
		int j = 0;
		for (int i = 0; i < argumentData.size(); i++) {
			
			if (i >= size() - (lastIsArbitraryNumber ? 1 : 0)) {
				
				if (lastIsArbitraryNumber) {
					Array.set(last, j++, commandArguments.get(size() - 1).parseValue(argumentData));
				} else {
					throw new TooManyArgumentsException(argumentData, argumentData.currentArg);
				}
				
			} else {
				data.parsedArguments[i] = commandArguments.get(i).parseValue(argumentData);
			}
			
			argumentData.nextArgument();
		}
		
		if (lastIsArbitraryNumber) {
			data.parsedArguments[data.parsedArguments.length - 1] = last;
		}
		
		return data;
		
	}
	
}

