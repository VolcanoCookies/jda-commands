package net.volcano.jdacommands.exceptions.command.run;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.volcano.jdacommands.model.command.Command;

@Getter
public class CommandBannedException extends CommandException {
	
	private final User user;
	
	public CommandBannedException(String message, Command command, User user) {
		super(message, command);
		this.user = user;
	}
	
	@Override
	protected EmbedBuilder getErrorEmbed(EmbedBuilder embedBuilder) {
		embedBuilder.setTitle("Error: Command banned");
		embedBuilder.setDescription("You are banned from running commands");
		return embedBuilder;
	}
}
