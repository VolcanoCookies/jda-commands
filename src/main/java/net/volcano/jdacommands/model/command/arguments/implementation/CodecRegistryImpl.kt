package net.volcano.jdacommands.model.command.arguments.implementation

import net.volcano.jdacommands.model.command.arguments.interfaces.Codec
import net.volcano.jdacommands.model.command.arguments.interfaces.CodecRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import java.lang.reflect.ParameterizedType

class CodecRegistryImpl(
	context: ApplicationContext
) : CodecRegistry {

	val log: Logger = LoggerFactory.getLogger(this::class.java)

	private val codecMap: MutableMap<Class<*>, Codec<*>?> = HashMap()
	override fun <T> getCodec(clazz: Class<T>): Codec<T>? {
		return codecMap.getOrDefault(clazz, null) as Codec<T>?
	}

	override fun <T> registerCodec(codec: Codec<T>) {
		try {
			val clazz = (codec.javaClass.genericSuperclass as ParameterizedType)
				.actualTypeArguments[0] as Class<*>
			codecMap[clazz] = codec
		} catch (e: ClassCastException) {
			val clazz = ((codec.javaClass.genericSuperclass as ParameterizedType)
				.actualTypeArguments[0] as ParameterizedType).rawType as Class<*>
			codecMap[clazz] = codec
		}
	}

	init {
		for ((name, codex) in context.getBeansOfType(Codec::class.java)) {
			registerCodec(codex)
			log.info("Registered codex $name.")
		}

	}

}