package net.volcano.jdacommands.model.command.arguments.interfaces

import kotlin.reflect.KClass

interface CodecRegistry {

	fun <T : Any> getCodec(clazz: KClass<T>): Codec<T>?
	fun <T> registerCodec(codec: Codec<T>)

}