package net.volcano.jdacommands.interfaces

import net.volcano.jdacommands.exceptions.command.CommandCompileException
import net.volcano.jdacommands.model.command.Command
import net.volcano.jdacommands.model.command.CommandCompiler
import net.volcano.jdacommands.model.command.CommandEvent
import net.volcano.jdacommands.model.command.CommandNode
import net.volcano.jdacommands.model.command.arguments.interfaces.CodecRegistry
import java.util.concurrent.ScheduledThreadPoolExecutor

interface CommandClient {

	val userProvider: UserProvider
	val guildProvider: GuildProvider
	val prefixProvider: PrefixProvider
	val permissionProvider: PermissionProvider
	val commandCompiler: CommandCompiler
	val codecRegistry: CodecRegistry
	val allCommands: Set<Command>
	val rootCommandNode: CommandNode
	val executorService: ScheduledThreadPoolExecutor
	val interactionClient: InteractionClient
	val permissionClient: PermissionClient

	fun registerCommand(command: Command): Boolean

	@Throws(CommandCompileException::class)
	fun registerController(controller: Any): Boolean

	fun call(event: CommandEvent) {
		try {
			executeCommand(event)
		} catch (e: Exception) {
			terminate(event, e)
		}
	}

	fun executeCommand(event: CommandEvent)

	fun terminate(event: CommandEvent, t: Throwable)

}