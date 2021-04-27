package net.volcano.jdacommands.model.menu.pagers;

import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.utils.TimeUtil;
import net.volcano.jdacommands.client.ReactionMenuClient;

public abstract class Menu {
	
	private static final long DEFAULT_EXPIRATION = 30 * 60 * 1000L;
	
	private ReactionMenuClient reactionMenuClient;
	
	protected String messageId;
	
	protected long expiration;
	
	public abstract void onReactionEvent(GenericMessageReactionEvent event);
	
	public void registered(ReactionMenuClient reactionMenuClient) {
		this.reactionMenuClient = reactionMenuClient;
	}
	
	protected void remove() {
		reactionMenuClient.unregister(this);
	}
	
	public String getMessageId() {
		return messageId;
	}
	
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	public long getExpiration() {
		return expiration <= 0 ? DEFAULT_EXPIRATION : expiration;
	}
	
	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}
	
	public long getCreationTime() {
		return TimeUtil.getTimeCreated(Long.parseLong(getMessageId()))
				.toInstant()
				.toEpochMilli();
	}
	
}
