package net.volcano.jdacommands.model.menu.pagers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.volcano.jdautils.constants.EmbedLimit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EmbedFieldPagerBuilder extends EmbedPagerBuilder {
	
	private int fieldsPerPage = EmbedLimit.EMBED_FIELD_COUNT_LIMIT;
	
	public EmbedFieldPagerBuilder() {
	}
	
	public EmbedFieldPagerBuilder(int fieldsPerPage) {
		setFieldsPerPage(fieldsPerPage);
	}
	
	public EmbedFieldPagerBuilder(@Nullable EmbedBuilder builder) {
		super(builder);
	}
	
	public EmbedFieldPagerBuilder(@Nullable MessageEmbed embed) {
		super(embed);
	}
	
	@Override
	public EmbedPager buildEmbed(MessageEmbed baseEmbed) {
		
		if (baseEmbed.getDescription() != null &&
				baseEmbed.getDescription().length() > EmbedLimit.EMBED_DESCRIPTION_LIMIT) {
			throw new IllegalStateException(String.format("Description is longer than %d! Please limit your input!", MessageEmbed.TEXT_MAX_LENGTH));
		}
		
		EmbedBuilder embedBuilder = new EmbedBuilder(baseEmbed);
		List<List<MessageEmbed.Field>> fields = new ArrayList<>();
		
		int i = 0;
		int currentLength = 0;
		
		List<MessageEmbed.Field> list = new ArrayList<>();
		
		for (MessageEmbed.Field field : baseEmbed.getFields()) {
			
			if (++i > fieldsPerPage || baseEmbed.getLength() + currentLength > EmbedLimit.EMBED_TOTAL_LIMIT) {
				fields.add(list);
				currentLength = 0;
				i = 0;
				list = new ArrayList<>();
			}
			
			list.add(field);
		}
		fields.add(list);
		
		return new EmbedFieldPager(getExpiration(), fields, embedBuilder.clearFields(), 0, getDownload());
	}
	
	public void setFieldsPerPage(int fieldsPerPage) {
		this.fieldsPerPage = Math.max(1, Math.min(EmbedLimit.EMBED_FIELD_COUNT_LIMIT, fieldsPerPage));
	}
	
}



