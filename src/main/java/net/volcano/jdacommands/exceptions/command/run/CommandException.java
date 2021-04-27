package net.volcano.jdacommands.exceptions.command.run;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.volcano.jdautils.constants.Colors;

import java.time.Instant;

@Getter
public abstract class CommandException extends Exception {
	
	/**
	 * If the error should be sent in dms
	 */
	private final boolean sensitive = false;
	
	public EmbedBuilder getErrorEmbed() {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setColor(Colors.ERROR);
		embedBuilder.setTimestamp(Instant.now());
		getErrorEmbed(embedBuilder);
		return embedBuilder;
	}
	
	protected abstract void getErrorEmbed(EmbedBuilder embedBuilder);
	
}
