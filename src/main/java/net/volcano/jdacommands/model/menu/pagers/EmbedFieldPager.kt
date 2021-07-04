package net.volcano.jdacommands.model.menu.pagers

import lombok.Getter
import lombok.Setter
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed

@Getter
@Setter
class EmbedFieldPager(
	private val fields: List<List<MessageEmbed.Field>>,
	userId: String,
	baseEmbed: EmbedBuilder,
	download: ByteArray? = null,
	currentPage: Int = 0,
	expiration: Long = 60L * 30L,
) : EmbedPager(userId, baseEmbed, download, currentPage, expiration) {

	override val size: Int
		get() = fields.size

	override fun getPage(page: Int): MessageEmbed {
		val embedBuilder = EmbedBuilder(baseEmbed)
		embedBuilder.setFooter(generateFooter())
		fields[page].forEach { embedBuilder.addField(it) }
		return embedBuilder.build()
	}
	
}