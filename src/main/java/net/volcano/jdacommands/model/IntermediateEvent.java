package net.volcano.jdacommands.model;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Used for overriding non-null java methods to nullable kotlin methods
 */
public class IntermediateEvent extends MessageReceivedEvent {
	
	public IntermediateEvent(@NotNull JDA api, long responseNumber, @NotNull Message message) {
		super(api, responseNumber, message);
	}
	
	@Nullable
	@Override
	public Guild getGuild() {
		return isFromGuild() ? super.getGuild() : null;
	}
	
	@Nullable
	@Override
	public TextChannel getTextChannel() {
		return isFromGuild() ? super.getTextChannel() : null;
	}
	
}
