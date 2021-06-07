package net.volcano.jdacommands.exceptions.command.run;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

@RequiredArgsConstructor
@Getter
public class CommandBannedException extends CommandException {
	
	private final User user;
	
	@Override
	protected EmbedBuilder getErrorEmbed(EmbedBuilder embedBuilder) {
		embedBuilder.setTitle("Error: Command banned");
		embedBuilder.setDescription("You are banned from running commands");
		return embedBuilder;
	}
}
