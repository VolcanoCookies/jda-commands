package net.volcano.jdacommands.exceptions.command.run;

import net.dv8tion.jda.api.EmbedBuilder;
import net.volcano.jdacommands.model.command.Command;

public class CommandRuntimeException extends CommandException {
	
	public CommandRuntimeException(String message, Command command) {
		super(message, command);
	}
	
	@Override
	public EmbedBuilder getErrorEmbed(EmbedBuilder embedBuilder) {
		embedBuilder.setTitle("Error: Runtime exception");
		embedBuilder.setDescription(getMessage());
		return embedBuilder;
	}
}
