package net.volcano.jdacommands.exceptions.command.parsing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.volcano.jdacommands.exceptions.command.run.CommandException;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;

@Getter
@RequiredArgsConstructor
public abstract class ArgumentParsingException extends CommandException {
	
	/**
	 * The data that was used when parsing the argument
	 */
	protected final ArgumentParsingData data;
	/**
	 * The argument index of the argument that failed
	 */
	protected final int argumentIndex;
	
}

