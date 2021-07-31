package net.volcano.jdacommands.exceptions.command.parsing;

import net.dv8tion.jda.api.EmbedBuilder;
import net.volcano.jdacommands.model.command.Command;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;

public class MissingArgumentsException extends ArgumentParsingException {
	
	private final int requiredArguments;
	
	public MissingArgumentsException(Command command, ArgumentParsingData data, int argumentIndex, int requiredArguments) {
		super(command, data, argumentIndex);
		this.requiredArguments = requiredArguments;
	}
	
	@Override
	protected EmbedBuilder getErrorEmbed(EmbedBuilder embedBuilder) {
		embedBuilder.setTitle(String.format("Error: Missing %d Arguments.", requiredArguments - getArgumentIndex()));
		embedBuilder.setDescription("Required arguments: " + requiredArguments);
		return embedBuilder;
	}
}
