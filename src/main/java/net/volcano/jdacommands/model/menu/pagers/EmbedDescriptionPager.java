package net.volcano.jdacommands.model.menu.pagers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;
import net.volcano.jdacommands.constants.Reactions;

import java.util.List;

public class EmbedDescriptionPager extends EmbedPager {
	
	private final List<String> pages;
	
	public EmbedDescriptionPager(long expiration, List<String> pages, EmbedBuilder embedBuilder, int currentPage, byte[] download) {
		super(expiration);
		this.pages = pages;
		baseEmbed = embedBuilder;
		this.currentPage = currentPage;
		this.download = download;
	}
	
	@Override
	public MessageEmbed getPage(int page) {
		EmbedBuilder embedBuilder = new EmbedBuilder(baseEmbed);
		embedBuilder.setDescription(pages.get(page));
		embedBuilder.setFooter(generateFooter());
		return embedBuilder.build();
	}
	
	@Override
	public void postSend(Message message) {
		if (getSize() > 1) {
			RestAction.allOf(
					message.addReaction(Reactions.PAGE_START),
					message.addReaction(Reactions.PAGE_BACK),
					message.addReaction(Reactions.PAGE_FORWARD),
					message.addReaction(Reactions.PAGE_END))
					.queue();
		}
		if (download != null) {
			message.addReaction(Reactions.DOWNLOAD)
					.queue();
		}
	}
	
	@Override
	int getSize() {
		return pages.size();
	}
	
}
