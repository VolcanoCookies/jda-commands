package net.volcano.jdacommands.model.command.arguments;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.volcano.jdacommands.model.command.Command;

/**
 * This class represents the finished parsed data made available when a command successfully parses all of its arguments
 */
public class ParsedData {
	
	/**
	 * The raw arguments provided
	 */
	public String[] rawArguments;
	
	/**
	 * The index at which each argument start in the raw content string
	 */
	public Integer[] rawArgumentIndex;
	
	/**
	 * The resulting parsed arguments
	 */
	public Object[] parsedArguments;
	
	/**
	 * The {@link Command} this data was created against
	 */
	public Command command;
	
	/**
	 * The event this data was parsed from
	 */
	public MessageReceivedEvent event;
	
	public ParsedData(String[] rawArguments, Integer[] rawArgumentIndex) {
		this.rawArguments = rawArguments;
		this.rawArgumentIndex = rawArgumentIndex;
	}
}
