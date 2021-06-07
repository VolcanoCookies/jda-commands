package net.volcano.jdacommands.exceptions.command.run;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;

@RequiredArgsConstructor
public class CommandRuntimeException extends CommandException {
	
	private final String message;
	
	@Override
	protected EmbedBuilder getErrorEmbed(EmbedBuilder embedBuilder) {
		embedBuilder.setTitle("Error: Runtime exception");
		embedBuilder.setDescription(message);
		return embedBuilder;
	}
}
