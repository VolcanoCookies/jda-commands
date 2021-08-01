package net.volcano.jdacommands.exceptions.command.run;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.volcano.jdacommands.model.command.Command;

@Getter
public class IncorrectSourceException extends CommandException {
	
	private final Command.Source requiredSource;
	
	public IncorrectSourceException(Command command, Command.Source requiredSource) {
		super(command);
		this.requiredSource = requiredSource;
	}
	
	@Override
	public EmbedBuilder getErrorEmbed(EmbedBuilder embedBuilder) {
		embedBuilder.setTitle("Error: Incorrect source");
		embedBuilder.setDescription("This command has to be ran in " + (requiredSource == Command.Source.GUILD ? "a guild" : "dms") + ".");
		return embedBuilder;
	}
}
