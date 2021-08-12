package net.volcano.jdacommands.client

import lombok.Getter
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.PrivateChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.volcano.jdacommands.config.CategoryConfig
import net.volcano.jdacommands.constants.Reactions
import net.volcano.jdacommands.exceptions.command.CommandCompileException
import net.volcano.jdacommands.exceptions.command.parsing.*
import net.volcano.jdacommands.exceptions.command.run.*
import net.volcano.jdacommands.interfaces.*
import net.volcano.jdacommands.model.command.*
import net.volcano.jdacommands.model.command.arguments.ParsedData
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData
import net.volcano.jdacommands.model.command.arguments.implementation.CodecRegistryImpl
import net.volcano.jdacommands.model.command.arguments.interfaces.CodecRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.lang.Nullable
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledThreadPoolExecutor

@Getter
@Service
class CommandClientImpl(
	val jda: JDA,
	override val permissionProvider: PermissionProvider,
	override val prefixProvider: PrefixProvider,
	override val userProvider: UserProvider,
	override val guildProvider: GuildProvider,
	override val permissionClient: PermissionClient,
	override val interactionClient: InteractionClient,
	categoryConfig: CategoryConfig,
	@field:Nullable @param:Nullable private val extension: Extension,
	context: ApplicationContext
) : ListenerAdapter(), CommandClient {

	override val allCommands: MutableSet<Command> = HashSet()
	override val rootCommandNode = CommandNode(true)
	override val executorService: ScheduledThreadPoolExecutor
	override val commandCompiler: CommandCompiler

	@get:Bean
	override val codecRegistry: CodecRegistry

	val log: Logger = LoggerFactory.getLogger(this::class.java)

	private val ownerId: String by lazy {
		jda.retrieveApplicationInfo()
			.submit()
			.get()
			.owner
			.id
	}

	override fun onMessageReceived(event: MessageReceivedEvent) {

		// Only accept commands from regular users
		if (event.isWebhookMessage || event.author.isBot) return

		// Initialize prefix
		val prefix = prefixProvider.getEffctivePrefix(event)

		// The message content
		var content = event.message.contentRaw

		// Check if the message starts with the prefix
		if (!content.startsWith(prefix)) return

		// Remove the prefix to find the command
		content = content.replaceFirst(prefix.toRegex(), "").trim { it <= ' ' }
		val finalContent = content
		executorService.execute {
			try {

				// Check if the user is command banned
				if (userProvider.isCommandBanned(event.author)) {
					return@execute
				}

				val data = findAndParse(event, finalContent)
				val commandEvent = CommandEvent(this, data, extension)

				executeCommand(commandEvent)

				// Log
				val caller = event.author
				log.info("{}[{}] ran {}", caller.asTag, caller.id, event.message.contentRaw)
			} catch (e: ArgumentParsingException) {
				sendError(e, event)
			} catch (e: InvalidArgumentsException) {
				sendError(e, event)
			} catch (e: CommandNotFoundException) {
			} catch (e: MissingPermissionsException) {
				event.message
					.addReaction(Reactions.NO_PERMISSIONS)
					.queue()
			} catch (e: PermissionsOnCooldownException) {
				event.message
					.addReaction(Reactions.NO_PERMISSIONS)
					.queue()
			} catch (e: CommandRuntimeException) {
				sendError(e, event)
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	/**
	 * Register a [Command]
	 *
	 * @param command the [Command] to register
	 * @return whether or not a change occurred to the command set
	 */
	override fun registerCommand(command: Command): Boolean {
		var change = false
		for (path in command.paths) {
			change = rootCommandNode.addCommand(command, *path.split(" ").toTypedArray()) || change
			log.info("Registered command at $path")
		}
		allCommands.add(command)
		return change
	}

	/**
	 * Register a [CommandController], thus registering all of its commands
	 *
	 * @param controller the [CommandController] annotated object
	 * @return whether or not a change occurred to the command set
	 */
	@Throws(CommandCompileException::class)
	override fun registerController(controller: Any): Boolean {
		var change = false
		for (command in commandCompiler.compile(controller)) {
			change = registerCommand(command) || change
		}
		return change
	}

	/**
	 * Parse the arguments for the commands with the base data,
	 * return the data that accepted the most arguments
	 *
	 *
	 * If no parsing finished completely,
	 * return the error of the longest successful chain
	 *
	 * @param commandsToParse     the command list to parse
	 * @param data the  data to parse
	 * @return the parsed data
	 */
	@Throws(CommandException::class, CommandNotFoundException::class)
	private fun parseAny(commandsToParse: Set<Command>, data: ArgumentParsingData): ParsedData {

		// The latest fully parsed command,
		// where the last argument is a take-all

		// Sort commands by longest argument chain first,
		// And take all second
		// This lets us return immediately when we parse successfully.
		val commands = commandsToParse.sortedBy { it.arguments.size }

		var longestParsedWithError = 0
		var exception: CommandException? = null

		for (command in commands) {
			val clonedData = data.copy()
			clonedData.command = command

			try {
				return command.parseArguments(clonedData)
			} catch (e: MissingArgumentsException) {
				if (e.data.rawArguments[e.argumentIndex].startIndex > longestParsedWithError) {
					longestParsedWithError = e.data.rawArguments[e.argumentIndex].startIndex
					exception = e
				}
			} catch (e: TooManyArgumentsException) {
				if (e.data.rawArguments[e.argumentIndex].startIndex > longestParsedWithError) {
					longestParsedWithError = e.data.rawArguments[e.argumentIndex].startIndex
					exception = e
				}
			} catch (e: InvalidArgumentsException) {
				if (e.errorStartIndex > longestParsedWithError) {
					longestParsedWithError = e.errorStartIndex
					exception = e
				}
			}

		}

		// If any command was parsed partially and threw an error, return that error, else no command was found at all
		if (exception != null) {
			throw exception
		} else {
			throw CommandNotFoundException()
		}

	}

	/**
	 * Find what command the user tried to run.
	 *
	 *
	 * Some commands can have the exact same path and only differ in their arguments.
	 * This method tries to figure out which exact command the user tried to run,
	 * and if none found,
	 * tries to find the closest one to provide the best possible error message.
	 *
	 *
	 * This lets us have commands such as:
	 * <br></br>
	 * **!Help**
	 * <br></br>
	 * **!Help &lt page &gt**
	 * <br></br>
	 * Where both commands will be correctly resolved depending on if the user provided an argument or not.
	 *
	 * @param event   the event with the content correctly prefixed
	 * @param content the content, with the prefix removed
	 * @return The best matching command for this content
	 * @throws CommandNotFoundException If no command was found for this path
	 * @throws ArgumentParsingException If there was an error parsing all of the candidate commands
	 */
	@Throws(CommandNotFoundException::class, CommandException::class)
	fun findAndParse(event: MessageReceivedEvent, content: String): ParsedData {

		val tokens = ALIAS_SPLIT_REGEX.findAll(content)
			.map { Pair(it.value, it.range) }
			.toList()

		val (commands, pathLength) = rootCommandNode.findCommands(*tokens.map { it.first }.toTypedArray())

		// Check if there are any possible command candidates
		if (commands.isEmpty()) {
			throw CommandNotFoundException()
		}

		val divider = if (pathLength == 0) content.length else tokens[tokens.size - pathLength].second.first

		val rawPrefix = event.message.contentRaw.substring(0, event.message.contentRaw.length - content.length)
		val rawPath = content.substring(0, divider)
		val rawArguments = content.substring(divider)
		val parsingData = ArgumentParsingData(event, rawPrefix, rawPath, rawArguments)

		return if (commands.size > 1) {
			val permittedCommands: MutableList<Command> = mutableListOf()
			for (command in commands) {
				if (permissionClient.checkPermissions(
						command.permission,
						event.author,
						if (event.isFromGuild) event.guild else null,
						if (event.isFromGuild) event.textChannel else null
					).hasPermissions
				) {
					permittedCommands.add(command)
				}
			}

			// If there are no permitted commands
			// Add all non-permitted to get the closest matching argument wise to know what missing permissions to return.
			if (permittedCommands.isEmpty()) {
				permittedCommands.addAll(commands)
			}
			val possibleCommands: MutableList<Command> = mutableListOf()
			for (command in permittedCommands) {
				if (parsingData.size == command.arguments.size ||
					command.arguments.lastIsVarArg && parsingData.size >= command.arguments.size
				) {
					possibleCommands.add(command)
				}
			}

			// If there are no commands with the exact number of arguments as provided,
			// check if there are any commands with less arguments but with a "take-all" argument.
			if (possibleCommands.isEmpty()) {
				possibleCommands.addAll(commands)
			}

			// If there still are no possible commands,
			// add all commands, parsing will fail but we will get a better error message
			if (possibleCommands.isEmpty()) {
				possibleCommands.addAll(commands)
			}
			try {
				val any = parseAny(possibleCommands.toSet(), parsingData)
				// Check if the user has permissions for this command
				if (!any.command.botOwnerCanAlwaysExecute || event.author.id != ownerId) {
					checkPermissions(event, any.command)
				}
				any
			} catch (e: CommandException) {
				val command = e.getCommand()
				if (!command.botOwnerCanAlwaysExecute || event.author.id != ownerId) {
					checkPermissions(event, command)
				}
				throw e
			}
		} else {
			// Exactly one command
			val command = commands.iterator().next()
			if (!command.botOwnerCanAlwaysExecute || event.author.id != ownerId) {
				checkPermissions(event, command)
			}
			command.parseArguments(parsingData)
		}
	}

	override fun executeCommand(event: CommandEvent) {
		var botOwnerOverriding = false
		if (event.command.botOwnerCanAlwaysExecute) {
			if (ownerId == event.author.id) {
				botOwnerOverriding = true
			}
		}
		if (!botOwnerOverriding) {
			// Check if the user has permissions
			val queryResult = permissionClient.checkPermissions(
				event.command.permission,
				event.author,
				event.guild,
				event.textChannel
			)
			if (!queryResult.hasPermissions) {
				try {
					checkPermissions(event, event.command)
				} catch (e: PermissionsOnCooldownException) {
					terminate(event, e)
					return
				} catch (e: MissingPermissionsException) {
					terminate(event, e)
					return
				}
			}
		}

		// Check if the command is from the correct source
		if (!event.command.source.isCorrect(event)) {
			terminate(event, IncorrectSourceException(event.command, event.command.source))
			return
		}
		try {
			log.info(event.author.asTag + " ran " + event.message.contentDisplay)
			val action = event.command.call(event)
			action?.queue()
			if (!botOwnerOverriding) {
				permissionClient.invokeCooldown(event.author, event.guild, event.command.permission)
			}
		} catch (e: CommandException) {
			terminate(event, e)
		} catch (e: IllegalAccessException) {
			e.printStackTrace()
		}
	}

	override fun terminate(event: CommandEvent, t: Throwable) {
		when (t) {
			is MissingPermissionsException -> {
				event.message
					.addReaction(Reactions.NO_PERMISSIONS)
					.queue()
			}
			is IncorrectSourceException -> {
				event.respondError(
					"This command can only be run as a " + t
						.requiredSource + " message."
				).queue()
			}
			is CommandRuntimeException -> {
				event.respond(t.errorEmbed).queue()
			}
		}
	}

	/**
	 * Send a command run error to a user
	 *
	 * @param e     the error to send
	 * @param event the event which caused this error
	 */
	private fun sendError(e: CommandException, event: MessageReceivedEvent) {
		if (e.isSensitive) {
			event.author
				.openPrivateChannel()
				.flatMap { c: PrivateChannel ->
					var action = c.sendMessageEmbeds(e.errorEmbed.build())
					for (attachment in e.attachments) {
						action = action.addFile(attachment.inputStream, attachment.name)
					}
					action
				}
				.queue()
		} else {
			var action = event.channel.sendMessageEmbeds(e.errorEmbed.build())
			for (attachment in e.attachments) {
				action = action.addFile(attachment.inputStream, attachment.name)
			}
			action.queue()
		}
	}

	@Throws(PermissionsOnCooldownException::class, MissingPermissionsException::class)
	private fun checkPermissions(event: MessageReceivedEvent, command: Command) {
		val guild = if (event.isFromGuild) event.guild else null
		val queryResult = permissionClient.checkPermissions(
			command.permission,
			event.author,
			guild,
			if (event.isFromGuild) event.textChannel else null
		)
		if (!queryResult.hasPermissions) {
			if (queryResult.cooldownExpiration != null) {
				throw PermissionsOnCooldownException(command, guild, command.permission, queryResult.cooldownExpiration)
			} else {
				throw MissingPermissionsException(command, guild, command.permission)
			}
		}
	}

	companion object {

		private val ALIAS_SPLIT_REGEX = Regex("\\S+", RegexOption.MULTILINE)
	}

	init {
		executorService = ScheduledThreadPoolExecutor(1)
		executorService.maximumPoolSize = 10
		(permissionClient as PermissionClientImpl).client = this
		codecRegistry = CodecRegistryImpl(context)
		commandCompiler = CommandCompiler(codecRegistry, categoryConfig)
		log.info("Initialized command client.")
	}
}