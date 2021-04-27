package net.volcano.jdacommands.model.menu.pagers;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;
import net.volcano.jdacommands.constants.Reactions;

import java.util.List;

@Getter
@Setter
public class EmbedFieldPager extends EmbedPager {
	
	private List<List<MessageEmbed.Field>> fields;
	
	public EmbedFieldPager(long expiration, List<List<MessageEmbed.Field>> fields, EmbedBuilder embedBuilder, int currentPage) {
		super(expiration);
		this.fields = fields;
		baseEmbed = embedBuilder;
		this.currentPage = currentPage;
	}
	
	@Override
	public MessageEmbed getPage(int page) {
		EmbedBuilder embedBuilder = new EmbedBuilder(baseEmbed);
		embedBuilder.setFooter(generateFooter());
		fields.get(page).forEach(embedBuilder::addField);
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
	}
	
	@Override
	int getSize() {
		return fields.size();
	}
	
}
