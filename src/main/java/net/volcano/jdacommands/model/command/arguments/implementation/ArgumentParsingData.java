package net.volcano.jdacommands.model.command.arguments.implementation;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.volcano.jdacommands.model.command.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class ArgumentParsingData {
	
	private static final Pattern argumentPattern = Pattern.compile("\"[^\"]+\"|'[^']+'|\\S+", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	
	public int currentArg = 0;
	
	public RawArgument[] rawArguments;
	
	public String rawPrefix;
	
	public String rawPath;
	
	public String rawContent;
	
	public MessageReceivedEvent event;
	
	public Command command;
	
	public ArgumentParsingData(MessageReceivedEvent event, String rawPrefix, String rawPath, String rawContent) {
		this.rawPrefix = rawPrefix;
		this.rawPath = rawPath;
		this.rawContent = rawContent;
		this.event = event;
		
		List<RawArgument> argumentList = new ArrayList<>();
		Matcher matcher = argumentPattern.matcher(rawContent.trim());
		
		while (matcher.find()) {
			var rawArgumentBuilder = RawArgument.builder();
			
			String token = matcher.group();
			if (token.matches("^(['\"].*['\"])$") &&
					token.length() > 2) {
				rawArgumentBuilder.inQuotations(true);
				rawArgumentBuilder.value(token.substring(1, token.length() - 1));
			} else {
				rawArgumentBuilder.inQuotations(false);
				rawArgumentBuilder.value(token);
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
		return new ArgumentParsingData(
				currentArg,
				rawArguments,
				rawPrefix,
				rawPath,
				rawContent,
				event,
				command
		);
	}
	
}
