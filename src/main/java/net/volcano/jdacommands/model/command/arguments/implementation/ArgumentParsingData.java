package net.volcano.jdacommands.model.command.arguments.implementation;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class ArgumentParsingData {
	
	private static final Pattern argumentPattern = Pattern.compile("\"[^\"]+\"|'[^']+'|\\S+", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	
	public int currentArg = 0;
	
	public String[] rawArguments;
	public Integer[] rawArgumentStartIndex;
	
	public String rawContent;
	
	public MessageReceivedEvent event;
	
	/**
	 * @param event     The event resulting in this argument parser
	 * @param arguments the content, on top of the command path, meaning the arguments
	 */
	public ArgumentParsingData(MessageReceivedEvent event, String arguments) {
		arguments = arguments.trim();
		this.event = event;
		rawContent = arguments;
		
		List<String> argumentList = new ArrayList<>();
		List<Integer> argumentStartList = new ArrayList<>();
		Matcher matcher = argumentPattern.matcher(arguments);
		
		while (matcher.find()) {
			String token = matcher.group();
			if ((token.startsWith("'") || token.startsWith("\"")) &&
					token.length() > 2) {
				token = token.substring(1, token.length() - 1);
			}
			argumentList.add(token);
			argumentStartList.add(matcher.start());
		}
		rawArguments = argumentList.toArray(new String[0]);
		rawArgumentStartIndex = argumentStartList.toArray(new Integer[0]);
		
	}
	
	public boolean nextArgument() {
		if (hasNext()) {
			currentArg++;
			return true;
		}
		return false;
	}
	
	public boolean hasNext() {
		return currentArg + 1 < size();
	}
	
	public int size() {
		return rawArguments.length;
	}
	
	public String getArg() {
		return rawArguments.length > currentArg ? rawArguments[currentArg] : null;
	}
	
	@Override
	public ArgumentParsingData clone() {
		return new ArgumentParsingData(currentArg,
				rawArguments,
				rawArgumentStartIndex,
				rawContent,
				event);
	}
	
}
