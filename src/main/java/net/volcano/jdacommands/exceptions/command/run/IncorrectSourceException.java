package net.volcano.jdacommands.exceptions.command.run;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.volcano.jdacommands.model.command.Command;
import net.volcano.jdacommands.model.command.Source;

@Getter
public class IncorrectSourceException extends CommandException {
	
	private final Source requiredSource;
	
	public IncorrectSourceException(Command command, Source requiredSource) {
		super(command);
		this.requiredSource = requiredSource;
	}
	
	@Override
	public EmbedBuilder getErrorEmbed(EmbedBuilder embedBuilder) {
		embedBuilder.setTitle("Error: Incorrect source");
		embedBuilder.setDescription("This command has to be ran in " + (requiredSource == Source.GUILD ? "a guild" : "dms") + ".");
		return embedBuilder;
	}
}
