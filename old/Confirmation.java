package net.volcano.jdacommands.model.menu;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.volcano.jdautilities.constants.Colors;
import net.volcano.jdautilities.constants.Reactions;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Confirmation extends Menu {
	
	private final String userId;
	private final CompletableFuture<Boolean> future;
	
	public Confirmation(String userId, String messageId) {
		this.userId = userId;
		future = new CompletableFuture<>();
		future.completeOnTimeout(false, 5, TimeUnit.MINUTES);
		future.whenComplete((v, t) -> {
			remove();
		});
		this.messageId = messageId;
		expiration = 1000 * 60 * 5;
	}
	
	@Override
	public void onReactionEvent(GenericMessageReactionEvent event) {
		
		if (event.getUserId().equals(userId)) {
			if (event.getReactionEmote().isEmoji()) {
				switch (event.getReactionEmote().getEmoji()) {
					case Reactions.YES -> {
						complete(true, event);
					}
					case Reactions.NO -> {
						complete(false, event);
					}
				}
			}
		}
		
	}
	
	private void complete(boolean bool, GenericMessageReactionEvent event) {
		future.complete(bool);
		event.getChannel()
				.retrieveMessageById(event.getMessageId())
				.flatMap(message -> {
					EmbedBuilder embedBuilder = new EmbedBuilder();
					embedBuilder.setColor(bool ? Colors.YES : Colors.NO);
					embedBuilder.setTitle("Action " + (bool ? "confirmed" : "cancelled"));
					embedBuilder.setDescription("No further action required.");
					return message.editMessageEmbeds(embedBuilder.build());
				}).queue();
	}
	
	public CompletableFuture<Boolean> getFuture() {
		return future;
	}
	
	public static EmbedBuilder getEmbed(String content) {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle("Confirm");
		embedBuilder.setDescription(content);
		embedBuilder.setColor(Colors.NOTES);
		embedBuilder.setFooter("You have 5 minutes to respond, after which this confirmation will automatically be denied.");
		return embedBuilder;
	}
	
}
