package net.volcano.jdacommands.model.command.annotations

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Help(

	val usage: String = "GENERATE",

	val description: String,

	val category: String = "DEFAULT",

	val examples: Array<String> = [],

	val details: String = "",

	val permissions: Array<Permission> = []

)