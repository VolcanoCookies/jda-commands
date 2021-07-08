package net.volcano.jdacommands.model.interaction.pager

import lombok.Getter
import lombok.Setter
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.Button

@Getter
@Setter
class EmbedFieldPager(
	private val fields: List<List<MessageEmbed.Field>>,
	userId: String,
	baseEmbed: EmbedBuilder,
	download: ByteArray? = null,
	currentPage: Int = 0,
	extraButtons: List<Button> = listOf(),
	expiration: Long = 60L * 30L,
) : EmbedPager(userId, baseEmbed, download, currentPage, extraButtons, expiration) {

	override val size: Int
		get() = fields.size

	override fun getPage(page: Int): MessageEmbed {
		val embedBuilder = EmbedBuilder(baseEmbed)
		embedBuilder.setFooter(footer.text, footer.iconUrl)
		fields[page].forEach { embedBuilder.addField(it) }
		return embedBuilder.build()
	}

}