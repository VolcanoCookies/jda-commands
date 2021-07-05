package net.volcano.jdacommands.exceptions.command.run;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.volcano.jdacommands.model.interaction.pager.EmbedAttachment;
import net.volcano.jdautils.constants.Colors;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

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
		return getErrorEmbed(embedBuilder);
	}
	
	public List<EmbedAttachment> getAttachments() {
		return Collections.emptyList();
	}
	
	protected abstract EmbedBuilder getErrorEmbed(EmbedBuilder embedBuilder);
	
}
