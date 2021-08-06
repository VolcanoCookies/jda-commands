package net.volcano.jdacommands.model.command.arguments

import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData
import net.volcano.jdacommands.model.command.arguments.implementation.argument.IntegerArgument
import net.volcano.jdautils.utils.camelCaseToSpaces
import net.volcano.jdautils.utils.capitalize
import kotlin.reflect.KParameter
import kotlin.reflect.KType

abstract class CommandArgument<T> {

	/**
	 * Explicit usage of this argument, keep [null] for generated.
	 */
	var usage: String? = null

	/**
	 * The parameter that this argument refers to.
	 *
	 * Mainly used for getting the parameter name.
	 */
	lateinit var parameter: KParameter

	/**
	 * The type of the argument.
	 * For arrays this is the component type.
	 * For enums it will be the actual enum type and not [Enum.class]
	 */
	lateinit var type: KType

	/**
	 * How to actually parse the argument
	 *
	 * @param data the data to parse
	 * @return the parsed value
	 */
	@Throws(InvalidArgumentsException::class)
	abstract fun parseValue(data: ArgumentParsingData): T

	/**
	 * The usage for this argument, formatted to include required or optional.
	 * If no usage is provided in the argument builder then this will default to the parameter name.
	 *
	 * Not recommended to override.
	 */
	open fun getFormattedUsage(): String {

		val formatted = usage ?: parameter.name!!.capitalize().camelCaseToSpaces()

		return if (optional) {
			"[$formatted]"
		} else {
			"<$formatted>"
		}
	}

	/**
	 * Additional details about this argument.
	 *
	 * Example is min/max values for [IntegerArgument].
	 *
	 * Will show up on the man page.
	 *
	 * @see [IntegerArgument]
	 */
	open val details: String?
		get() = ""

	/**
	 * If the argument is allowed to be resolved to null.
	 */
	val nullable: Boolean
		get() = parameter.type.isMarkedNullable

	/**
	 * If the argument can be omitted entirely.
	 */
	val optional: Boolean
		get() = parameter.isOptional

}