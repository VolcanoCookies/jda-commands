package net.volcano.jdacommands.model.interaction.menu

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu
import net.volcano.jdautils.utils.StringUtil

class EmbedDescriptionMenu(
	opts: MutableMap<SelectOption, String>,
	userId: String,
	baseEmbed: MessageEmbed,
	download: ByteArray? = null,
	asReply: Boolean = false,
	ephemeral: Boolean = false,
	expiration: Long = 60L * 30L
) : EmbedMenu(userId, baseEmbed, download, asReply, ephemeral, expiration) {

	private val options = opts.mapKeys { it.key.value }

	private val selections = opts.keys.map {
		if (it.label.length > 25) {
			it.withLabel(StringUtil.capitalize(it.label).substring(0, 25))
				.withDescription(StringUtil.capitalize(it.label))
		} else {
			it.withLabel(StringUtil.capitalize(it.label))
		}
	}.sortedBy { it.label }

	override val size: Int
		get() = options.size

	override fun getPage(selection: String): MessageEmbed? {
		return options[selection]?.let { EmbedBuilder(baseEmbed).setDescription(it).build() }
	}

	override fun getActionRows(): List<ActionRow> {
		return listOf(
			ActionRow.of(
				SelectionMenu.create("menu")
					.setRequiredRange(1, 1)
					.addOptions(selections).build()
			)
		)
	}

}