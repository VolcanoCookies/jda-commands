package net.volcano.jdacommands.model.menu.pagers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.volcano.jdautils.constants.EmbedLimit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EmbedDescriptionPagerBuilder extends EmbedPagerBuilder {
	
	private List<String> pages = new ArrayList<>();
	
	public EmbedDescriptionPagerBuilder() {
	}
	
	public EmbedDescriptionPagerBuilder(@Nullable EmbedBuilder builder) {
		super(builder);
	}
	
	public EmbedDescriptionPagerBuilder(@Nullable MessageEmbed embed) {
		super(embed);
	}
	
	@Override
	protected EmbedPager buildEmbed(MessageEmbed baseEmbed) {
		
		EmbedBuilder embedBuilder = new EmbedBuilder(baseEmbed);
		embedBuilder.setDescription(null);
		
		for (String page : pages) {
			if (page.length() > EmbedLimit.EMBED_DESCRIPTION_LIMIT) {
				throw new IllegalStateException(String.format("Description page is longer than %d! Please limit your input!", MessageEmbed.TEXT_MAX_LENGTH));
			}
			if (embedBuilder.length() + page.length() > MessageEmbed.EMBED_MAX_LENGTH_BOT) {
				throw new IllegalStateException("Cannot build an embed with more than " + MessageEmbed.EMBED_MAX_LENGTH_BOT + " characters!");
			}
		}
		
		return new EmbedDescriptionPager(getExpiration(), pages, embedBuilder, 0, getDownload());
	}
	
	public void addPage(String page) {
		if (page.length() > EmbedLimit.EMBED_DESCRIPTION_LIMIT) {
			throw new IllegalStateException(String.format("Description page is longer than %d! Please limit your input!", MessageEmbed.TEXT_MAX_LENGTH));
		}
		pages.add(page);
	}
	
	public void addPages(List<String> pages) {
		for (String page : pages) {
			if (page.length() > EmbedLimit.EMBED_DESCRIPTION_LIMIT) {
				throw new IllegalStateException(String.format("Description page is longer than %d! Please limit your input!", MessageEmbed.TEXT_MAX_LENGTH));
			}
		}
		this.pages.addAll(pages);
	}
	
	public void setPages(List<String> pages) {
		this.pages = pages;
		for (String page : this.pages) {
			if (page.length() > EmbedLimit.EMBED_DESCRIPTION_LIMIT) {
				throw new IllegalStateException(String.format("Description page is longer than %d! Please limit your input!", MessageEmbed.TEXT_MAX_LENGTH));
			}
		}
	}
}
