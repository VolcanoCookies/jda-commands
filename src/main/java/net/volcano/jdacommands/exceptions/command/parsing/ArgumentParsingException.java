package net.volcano.jdacommands.exceptions.command.parsing;

import lombok.Getter;
import net.volcano.jdacommands.exceptions.command.run.CommandException;
import net.volcano.jdacommands.model.command.Command;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;

@Getter
public abstract class ArgumentParsingException extends CommandException {
	
	/**
	 * The data that was used when parsing the argument
	 */
	protected final ArgumentParsingData data;
	/**
	 * The argument index of the argument that failed
	 */
	protected final int argumentIndex;
	
	public ArgumentParsingException(Command command, ArgumentParsingData data, int argumentIndex) {
		super(command);
		this.data = data;
		this.argumentIndex = argumentIndex;
	}
	
	protected ArgumentParsingException(String message, Command command, ArgumentParsingData data, int argumentIndex) {
		super(message, command);
		this.data = data;
		this.argumentIndex = argumentIndex;
	}
	
}

