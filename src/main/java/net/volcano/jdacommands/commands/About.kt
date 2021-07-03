package net.volcano.jdacommands.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.requests.RestAction
import net.volcano.jdacommands.model.command.CommandEvent
import net.volcano.jdacommands.model.command.annotations.CommandController
import net.volcano.jdacommands.model.command.annotations.CommandMethod
import net.volcano.jdacommands.model.command.annotations.Help
import net.volcano.jdautils.constants.Colors
import java.util.concurrent.ExecutionException

@CommandController
class About {

	@CommandMethod(
		path = ["about"],
		permissions = "command.about"
	)
	@Help(description = "Show information about the bot.")
	fun about(event: CommandEvent): RestAction<*> {

		val embedBuilder = EmbedBuilder()
		embedBuilder.setTitle("About")
		embedBuilder.setColor(Colors.INFO)
		try {
			val owner = event.jda
				.retrieveApplicationInfo()
				.submit()
				.get()
				.owner
			embedBuilder.addField("Creator", owner.asMention, true)
		} catch (e: InterruptedException) {
			e.printStackTrace()
		} catch (e: ExecutionException) {
			e.printStackTrace()
		}
		/*embedBuilder.addField("Version", buildProperties.version, true)
		embedBuilder.addField(
			"Built",
			TimeUtil.format(if (buildProperties.time == null) Instant.EPOCH else buildProperties.time),
			true
		)^*/
		embedBuilder.addField("Connected Servers", event.jda.guilds.size.toString(), true)
		return event.respond(embedBuilder)

	}
}