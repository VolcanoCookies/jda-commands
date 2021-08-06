package net.volcano.jdacommands.model.command.arguments.interfaces

interface CodecRegistry {

	fun <T> getCodec(clazz: Class<T>): Codec<T>?
	fun <T> registerCodec(codec: Codec<T>)

}