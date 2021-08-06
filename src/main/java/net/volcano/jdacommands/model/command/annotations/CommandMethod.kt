package net.volcano.jdacommands.model.command.annotations

import net.volcano.jdacommands.model.command.Command

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class CommandMethod(

	vararg val value: String = [""],

	val path: Array<String> = [""],

	val permissions: String = "",

	val source: Command.Source = Command.Source.DEFAULT,

	val sensitive: Boolean = false,

	val global: Boolean = false

)