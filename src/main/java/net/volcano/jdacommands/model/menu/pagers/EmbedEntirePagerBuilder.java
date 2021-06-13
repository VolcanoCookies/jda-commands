package net.volcano.jdacommands.model.menu.pagers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmbedEntirePagerBuilder extends EmbedPagerBuilder {
	
	private List<MessageEmbed> embeds = new ArrayList<>();
	
	@Override
	protected EmbedPager buildEmbed(MessageEmbed baseEmbed) {
		return new EmbedEntirePager(embeds, getExpiration());
	}
	
	public void setEmbeds(List<EmbedBuilder> embeds) {
		this.embeds = embeds.stream().map(EmbedBuilder::build).collect(Collectors.toList());
	}
	
	public void addEmbed(EmbedBuilder embed) {
		embeds.add(embed.build());
	}
	
	public void next() {
		embeds.add(new MessageEmbed(
				getUrl(),
				getTitle(),
				getDescription().toString(),
				EmbedType.RICH,
				getTimestamp(),
				getColor(),
				getThumbnail(),
				null,
				getAuthor(),
				null,
				getFooter(),
				getImage(),
				getFields()
		));
		clear();
	}
	
}

