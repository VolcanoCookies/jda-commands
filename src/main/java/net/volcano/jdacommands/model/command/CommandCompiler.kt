package net.volcano.jdacommands.model.command

import net.dv8tion.jda.api.requests.RestAction
import net.volcano.jdacommands.config.CategoryConfig
import net.volcano.jdacommands.exceptions.command.CommandCompileException
import net.volcano.jdacommands.model.ClassUtil
import net.volcano.jdacommands.model.ParameterData
import net.volcano.jdacommands.model.command.Command.Companion.CommandBuilder
import net.volcano.jdacommands.model.command.Help.Companion.HelpBuilder
import net.volcano.jdacommands.model.command.annotations.BotOwnerCanAlwaysExecute
import net.volcano.jdacommands.model.command.annotations.CommandController
import net.volcano.jdacommands.model.command.annotations.CommandMethod
import net.volcano.jdacommands.model.command.annotations.Help
import net.volcano.jdacommands.model.command.arguments.ArgumentList
import net.volcano.jdacommands.model.command.arguments.CommandArgument
import net.volcano.jdacommands.model.command.arguments.interfaces.CodecRegistry
import net.volcano.jdautils.utils.isArray
import net.volcano.jdautils.utils.kClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation

class CommandCompiler(
	private val registry: CodecRegistry,
	private val config: CategoryConfig
) {

	@Throws(CommandCompileException::class)
	fun compile(controller: Any): Set<Command> {

		val module = controller.javaClass.getAnnotation(CommandController::class.java)
			?: throw IllegalArgumentException("Object is not a command!")

		val set: MutableSet<Command> = HashSet()
		for (method in controller::class.declaredMemberFunctions) {
			if (method.findAnnotation<CommandMethod>() != null) {
				val command = compileCommand(controller, module, method)
				command.arguments.command = command
				set.add(command)
			}
		}

		return set
	}

	@Throws(CommandCompileException::class)
	fun compileCommand(o: Any?, controller: CommandController, function: KFunction<*>): Command {
		val annotation = function.findAnnotation<CommandMethod>()!!
		val builder = CommandBuilder()

		if (function.returnType != Void.TYPE && function.returnType != RestAction::class)
			throw CommandCompileException(function, "Command function needs to return RestAction or Void!");

		val paths = controller.path
			.flatMap { controllerPath ->
				annotation.path.map { commandPath ->
					"$controllerPath $commandPath".replace(Regex("  +"), " ").trim()
				}
			}

		if (paths.any { it.isBlank() }) throw CommandCompileException(function, "Command path cannot be empty.")

		builder.paths = paths.toTypedArray()

		// Build the arguments for this command
		val arguments: MutableList<CommandArgument<*>> = mutableListOf()
		var firstIsEvent = false
		var lastIsArray = false
		val parameters = function.parameters
		for ((index, param) in parameters.withIndex()) {

			if (index == 0 && param.type == CommandEvent::class.java) {
				firstIsEvent = true
				continue
			}

			if (param.type.isArray || param.isVararg) {
				if (index + 1 == function.parameters.size)
					lastIsArray = true
				else
					throw CommandCompileException(function, "Cannot have array/vararg argument as non-last parameter.")
			}

			val codecType = ClassUtil.getCodecClass(param.type.kClass)

			val codec = registry.getCodec(codecType) ?: throw CommandCompileException(
				function,
				"Unsupported type for command argument; $codecType"
			)

			arguments.add(codec.encodeArgument(ParameterData(param, codecType, registry)))

		}
		val argumentList = ArgumentList(arguments, lastIsArray)
		builder.arguments = argumentList
		// Build command help
		function.findAnnotation<Help>()?.let {
			val helpBuilder = HelpBuilder()

			helpBuilder.usage = if (it.usage == "GENERATE") paths[0] + " " + argumentList.generateUsage() else it.usage
			helpBuilder.description = it.description

			val category = if (it.category == "DEFAULT") controller.category else it.category
			helpBuilder.category = category

			helpBuilder.emoji =
				config.emojis[category] ?: throw CommandCompileException(function, "No emoji for category $category")
			helpBuilder.examples = it.examples
			helpBuilder.details = it.details
			helpBuilder.permissions = it.permissions.map { p -> "`${p.permission}` : ${p.description}" }.toTypedArray()

			builder.help = helpBuilder.build()
		}

		// Build the command method for this command
		builder.function = CommandFunction(function, firstIsEvent)

		// Build the permissions for this command
		var permissions = controller.permissions
		if (permissions.isNotBlank() && annotation.permissions.isNotBlank()) permissions += "."
		permissions += annotation.permissions
		if (permissions.isEmpty()) {
			throw CommandCompileException(
				function,
				"Command permissions are empty, set some and provide them by default instead."
			)
		} else if (!permissions.startsWith("command.")) {
			permissions = "command.$permissions"
		}
		builder.permission = permissions

		builder.source = if (annotation.source == Source.DEFAULT)
			if (controller.source == Source.DEFAULT)
				Source.BOTH
			else
				controller.source
		else
			annotation.source

		// Add misc values
		builder.sensitive = annotation.sensitive
		builder.globalPermissions = annotation.global
		builder.botOwnerCanAlwaysExecute = function.findAnnotation<BotOwnerCanAlwaysExecute>() != null

		return builder.build()
	}
}