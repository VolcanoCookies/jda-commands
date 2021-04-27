package net.volcano.jdacommands.exceptions.command.run;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.volcano.jdacommands.model.command.Command;

@RequiredArgsConstructor
@Getter
public class IncorrectSourceException extends CommandException {
	
	private final Command.Source requiredSource;
	
	@Override
	protected void getErrorEmbed(EmbedBuilder embedBuilder) {
		embedBuilder.setTitle("Error: Incorrect source");
		embedBuilder.setDescription("This command has to be ran in " + (requiredSource == Command.Source.GUILD ? "a guild" : "dms") + ".");
	}
}
