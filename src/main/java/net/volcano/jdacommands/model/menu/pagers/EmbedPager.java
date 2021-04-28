package net.volcano.jdacommands.model.menu.pagers;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.volcano.jdacommands.constants.Reactions;
import net.volcano.jdautils.constants.EmbedLimit;
import net.volcano.jdautils.utils.StringUtil;

@Getter
@Setter
public abstract class EmbedPager extends Menu {
	
	protected String userId;
	
	protected int currentPage = 0;
	
	protected EmbedBuilder baseEmbed;
	
	public EmbedPager(long expiration) {
		this.expiration = expiration;
	}
	
	public abstract MessageEmbed getPage(int page);
	
	public abstract void postSend(Message message);
	
	public MessageEmbed getPage() {
		return getPage(getCurrentPage());
	}
	
	abstract int getSize();
	
	@Override
	public void onReactionEvent(GenericMessageReactionEvent event) {
		if (event.getUserId().equals(userId)) {
			
			if (event.getReactionEmote().isEmoji()) {
				
				int prevPage = currentPage;
				switch (event.getReactionEmote().getEmoji()) {
					case Reactions.PAGE_START -> currentPage = 0;
					case Reactions.PAGE_BACK -> currentPage = Math.max(currentPage - 1, 0);
					case Reactions.PAGE_FORWARD -> currentPage = Math.min(currentPage + 1, getSize() - 1);
					case Reactions.PAGE_END -> currentPage = Math.max(0, getSize() - 1);
					default -> {
						onOtherReaction(event);
						return;
					}
				}
				
				if (prevPage != currentPage) {
					
					event.getChannel()
							.retrieveMessageById(getMessageId())
							.flatMap(message -> message.editMessage(getPage()))
							.queue();
					
				}
				
			}
			
		}
	}
	
	public void onOtherReaction(GenericMessageReactionEvent event) {
	
	}
	
	public String generateFooter() {
		MessageEmbed embed = baseEmbed.build();
		return StringUtil.trim("Page " + (getCurrentPage() + 1) + " of " + getSize() + (embed.getFooter() != null ? " | " + embed.getFooter() : ""),
				EmbedLimit.EMBED_FOOTER_LIMIT);
	}
	
}
