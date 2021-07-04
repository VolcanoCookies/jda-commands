package net.volcano.jdacommands.model.menu.pagers

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.volcano.jdautils.constants.EmbedLimit
import kotlin.math.max
import kotlin.math.min

class EmbedFieldPagerBuilder : EmbedPagerBuilder {

	var fieldsPerPage: Int = EmbedLimit.EMBED_FIELD_COUNT_LIMIT
		set(value) {
			field = max(1, min(EmbedLimit.EMBED_FIELD_COUNT_LIMIT, value))
		}

	constructor() : super()
	constructor(builder: EmbedBuilder?) : super(builder)
	constructor(embed: MessageEmbed?) : super(embed)

	public override fun buildEmbed(baseEmbed: MessageEmbed): EmbedPager {
		check(
			!(baseEmbed.description != null &&
					baseEmbed.description!!.length > EmbedLimit.EMBED_DESCRIPTION_LIMIT)
		) { String.format("Description is longer than %d! Please limit your input!", MessageEmbed.TEXT_MAX_LENGTH) }
		val embedBuilder = EmbedBuilder(baseEmbed)
		val fields: MutableList<List<MessageEmbed.Field>> = ArrayList()
		var i = 0
		var currentLength = 0
		var list: MutableList<MessageEmbed.Field> = ArrayList()
		for (field in baseEmbed.fields) {
			if (++i > fieldsPerPage || baseEmbed.length + currentLength > EmbedLimit.EMBED_TOTAL_LIMIT) {
				fields.add(list)
				currentLength = 0
				i = 0
				list = ArrayList()
			}
			list.add(field)
		}
		fields.add(list)
		return EmbedFieldPager(fields, userId, embedBuilder.clearFields(), download, 0)
	}

}