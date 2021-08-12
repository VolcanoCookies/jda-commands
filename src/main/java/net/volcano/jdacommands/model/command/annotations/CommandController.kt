package net.volcano.jdacommands.model.command.annotations

import net.volcano.jdacommands.model.command.Source
import org.springframework.stereotype.Component

@Component
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandController(

	vararg val value: String = [""],

	/**
	 * @return base path for all commands under this controller
	 */
	val path: Array<String> = [""],

	/**
	 * @return base permissions required for any command under this controller
	 */
	val permissions: String = "",

	/**
	 * @return Base source to apply to all commands under this controller unless they specify otherwise
	 */
	val source: Source = Source.DEFAULT,

	/**
	 * The default category for this controller commands
	 */
	val category: String = "general"

)