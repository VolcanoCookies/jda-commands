package net.volcano.jdacommands.model.interaction.menu

import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent
import net.volcano.jdacommands.model.interaction.InteractionListener

class RawMenuAttacher(
	val options: Set<String>,
	val func: (SelectionMenuEvent, String) -> Unit,
	expiration: Long = 30L * 60L
) : InteractionListener(expiration) {

	override fun onInteraction(event: SelectionMenuEvent) {
		val token = event.selectedOptions?.get(0)
			?.value
			?.let { if (options.contains(it)) it else null } ?: return

		func.invoke(event, token)
	}

}