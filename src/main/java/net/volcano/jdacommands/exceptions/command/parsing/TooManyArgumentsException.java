package net.volcano.jdacommands.exceptions.command.parsing;

import net.dv8tion.jda.api.EmbedBuilder;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;

public class TooManyArgumentsException extends ArgumentParsingException {
	
	public TooManyArgumentsException(ArgumentParsingData data, int argumentIndex) {
		super(data, argumentIndex);
	}
	
	@Override
	protected void getErrorEmbed(EmbedBuilder embedBuilder) {
		embedBuilder.setTitle("Error: Unexpected argument.");
		
		StringBuilder stringBuilder = new StringBuilder();
		int startIndex = data.rawArgumentStartIndex[argumentIndex];
		int lengthLeft = data.rawContent.length() - startIndex;
		
		stringBuilder.append(data.rawContent,
				startIndex > 10 ? startIndex - 10 : 0,
				lengthLeft > 20 ? startIndex + 20 : startIndex + lengthLeft);
		
		stringBuilder.insert(startIndex > 10 ? startIndex - 10 : 0, "__");
		stringBuilder.append("__");
		
		embedBuilder.setDescription(stringBuilder.toString());
		
	}
}
