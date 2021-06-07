package net.volcano.jdacommands.exceptions.command.parsing;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;

@Getter
public class InvalidArgumentsException extends ArgumentParsingException {
	
	private final String hint;
	
	public InvalidArgumentsException(ArgumentParsingData data, int argumentIndex, String hint) {
		super(data, argumentIndex);
		this.hint = hint;
	}
	
	public InvalidArgumentsException(ArgumentParsingData data, String hint) {
		super(data, data.currentArg);
		this.hint = hint;
	}
	
	@Override
	protected void getErrorEmbed(EmbedBuilder embedBuilder) {
		embedBuilder.setTitle("Error: Invalid arguments");
		int aroundStartIndex = data.rawArguments[argumentIndex].startIndex;
		int aroundEndIndex = aroundStartIndex + data.getArg().length();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("`");
		stringBuilder.append(data.rawContent, aroundStartIndex, aroundEndIndex);
		stringBuilder.append("`");
		stringBuilder.insert(0, data.rawContent, Math.max(0, aroundStartIndex - 20), aroundStartIndex);
		stringBuilder.append(data.rawContent, aroundEndIndex, Math.min(aroundEndIndex + 20, data.rawContent.length()));
		embedBuilder.setDescription(stringBuilder.toString());
		embedBuilder.addField("Hint", hint, false);
	}
}

