package net.volcano.jdacommands.model.interaction.selectors

import dev.minn.jda.ktx.interactions.SelectOption
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu
import net.volcano.jdacommands.model.interaction.InteractionListener
import net.volcano.jdautilities.constants.SELECTION_OPTION_LABEL_LIMIT
import net.volcano.jdautilities.constants.SELECTION_OPTION_LIMIT
import net.volcano.jdautilities.utils.trim
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class Selector : InteractionListener(3600) {

	var removeOnSelect: Boolean = true
	var minSelections: Int = 1
	var maxSelection: Int = 1
	var placeholder: String? = null
	var options: Map<String, SelectorOption> = HashMap()

	override fun onInteraction(event: SelectionMenuEvent) {
		val option = options[event.componentId] ?: return

		val prev = page

		option.func.invoke(event)

		if (page != prev) {
			event.editComponents(
				ActionRow.of(getPage(page))
			).queue()
		}

		if (event.isAcknowledged)
			event.deferEdit().queue()

		if (removeOnSelect)
			this.removeSelf()

	}

	var page: Int = 0

	val size: Int
		get() = options.size

	val pages: Int
		get() = if (size > SELECTION_OPTION_LIMIT) ceil(size / SELECTION_OPTION_LIMIT.toFloat()).toInt() else 1

	fun getPage(page: Int): SelectionMenu {
		val options = (if (pages > 1) {
			val chunk = options.values.chunked(SELECTION_OPTION_LIMIT - 2)[page].toMutableList()
			if (page == 0) {
				chunk.add(NEXT)
			} else if (page + 1 < pages) {
				chunk.add(0, PREV)
				chunk.add(NEXT)
			} else {
				chunk.add(0, PREV)
			}
			chunk
		} else options.values).map { it.build() }

		val menu = SelectionMenu.create("selection")
		menu.minValues = minSelections
		menu.maxValues = maxSelection
		menu.placeholder = placeholder
		menu.options.addAll(options)

		return menu.build()
	}

	val NEXT = SelectorOption("NEXT", "Next Page") {
		page = min(pages, page + 1)
	}

	val PREV = SelectorOption("PREV", "Previous Page") {
		page = max(0, page - 1)
	}

}

fun selector(init: Selector.() -> Unit): Selector {
	val selector = Selector()
	selector.init()
	return selector
}

data class SelectorOption(
	val id: String,
	val name: String? = null,
	val description: String? = null,
	val icon: Emoji? = null,
	val func: (SelectionMenuEvent) -> Unit
) {

	fun build(): SelectOption {
		return SelectOption(
			name ?: id.trim(SELECTION_OPTION_LABEL_LIMIT),
			id,
			description,
			icon
		)
	}
}
