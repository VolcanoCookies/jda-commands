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
	
	public RawArgument[] rawArguments;
	
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
		
		List<RawArgument> argumentList = new ArrayList<>();
		Matcher matcher = argumentPattern.matcher(arguments);
		
		while (matcher.find()) {
			var rawArgumentBuilder = RawArgument.builder();
			
			String token = matcher.group();
			if (token.matches("^(['\"].*['\"])$") &&
					token.length() > 2) {
				rawArgumentBuilder.inQuotations(true);
				rawArgumentBuilder.value(token.substring(1, token.length() - 1));
			}
			rawArgumentBuilder.startIndex(matcher.start());
			argumentList.add(rawArgumentBuilder.build());
		}
		rawArguments = argumentList.toArray(new RawArgument[0]);
		
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
		return rawArguments.length > currentArg ? rawArguments[currentArg].value : null;
	}
	
	@Override
	public ArgumentParsingData clone() {
		return new ArgumentParsingData(currentArg,
				rawArguments,
				rawContent,
				event);
	}
	
}
