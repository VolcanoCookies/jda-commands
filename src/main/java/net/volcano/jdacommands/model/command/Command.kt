package net.volcano.jdacommands.model.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.requests.RestAction
import net.volcano.jdacommands.exceptions.command.parsing.ArgumentParsingException
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException
import net.volcano.jdacommands.exceptions.command.run.CommandException
import net.volcano.jdacommands.model.command.arguments.ArgumentList
import net.volcano.jdacommands.model.command.arguments.ParsedData
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData
import net.volcano.jdautils.constants.Colors
import java.time.Instant

class Command(
	val paths: Array<String>,
	val permission: String,
	val source: Source,
	val globalPermissions: Boolean,
	val sensitive: Boolean,
	val isDiscordCommand: Boolean,
	val botOwnerCanAlwaysExecute: Boolean,
	val help: Help?,
	val arguments: ArgumentList,
	val function: CommandFunction
) {

	@Throws(IllegalAccessException::class, CommandException::class)
	fun call(event: CommandEvent): RestAction<*>? {
		return function.invoke(event, event.data)
	}

	val usageFormatted: String
		get() = (if (help!!.usage!!.isBlank()) "" else " " + help.usage).trim { it <= ' ' }

	val descriptionFormatted: String
		get() = help!!.description!!.trim { it <= ' ' }

	val detailedHelp: EmbedBuilder
		get() {
			help!!
			val embedBuilder = EmbedBuilder()
			embedBuilder.setTitle("Command: " + paths[0])
			embedBuilder.addField("Usage", "`$usageFormatted`", false)
			embedBuilder.addField("Description", descriptionFormatted, false)
			if (help.examples.isNotEmpty()) {
				embedBuilder.addField("Examples", java.lang.String.join("\n", *help.examples), false)
			}
			help.details?.let {
				embedBuilder.setDescription(it)
			}
			embedBuilder.setColor(Colors.HELP)
			embedBuilder.setTimestamp(Instant.now())
			return embedBuilder
		}

	@Throws(ArgumentParsingException::class, InvalidArgumentsException::class)
	fun parseArguments(parsingData: ArgumentParsingData): ParsedData {
		val data = arguments.parseArguments(parsingData)
		data.command = this
		data.event = parsingData.event
		return data
	}

	companion object {

		fun argumentComparator(): Comparator<Command> {
			return Comparator.comparingInt { it.arguments.size }
		}

		class CommandBuilder {

			lateinit var paths: Array<String>
			lateinit var permission: String
			lateinit var source: Source
			var globalPermissions: Boolean = false
			var sensitive: Boolean = false
			var isDiscordCommand: Boolean = false
			var botOwnerCanAlwaysExecute: Boolean = false
			var help: Help? = null
			lateinit var arguments: ArgumentList
			lateinit var function: CommandFunction

			fun build(): Command {
				return Command(
					paths,
					permission,
					source,
					globalPermissions,
					sensitive,
					isDiscordCommand,
					botOwnerCanAlwaysExecute,
					help,
					arguments,
					function
				)
			}
		}

	}
}