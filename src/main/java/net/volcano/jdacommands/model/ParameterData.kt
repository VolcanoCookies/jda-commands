package net.volcano.jdacommands.model

import net.volcano.jdacommands.model.command.arguments.interfaces.CodecRegistry
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

data class ParameterData(
	val parameter: KParameter,
	val codecClass: KClass<*>,
	val codecRegistry: CodecRegistry
)