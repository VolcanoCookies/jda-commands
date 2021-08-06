package net.volcano.jdacommands.model.command.annotations.argument

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Arg(

	/**
	 * Min value for numerical input
	 */
	val min: Long = Long.MIN_VALUE,

	/**
	 * Max value for numerical input
	 */
	val max: Long = Long.MAX_VALUE, val defaultToCaller: Boolean = false,

	/**
	 * Regex for matcher argument
	 */
	val regex: String = "",

	/**
	 * Regex flags
	 */
	val flags: Int = 0,

	/**
	 * If roles need to be from the same guild as the command is issued from
	 */
	val sameGuild: Boolean = true, val usage: String = "DEFAULT"

)