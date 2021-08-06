package net.volcano.jdacommands.model

import net.volcano.jdautils.utils.ClassUtil
import java.lang.reflect.Parameter

object ClassUtil {

	@JvmStatic
	fun getCodecClass(clazz: Class<*>): Class<*> {
		val actualType: Class<*> = getActualClass(clazz)

		return when {
			actualType.isEnum -> Enum::class.java
			actualType.isPrimitive -> ClassUtil.dePrimitivize(actualType)
			else -> actualType
		}
	}

	@JvmStatic
	fun getCodecClass(parameter: Parameter): Class<*> {
		return getCodecClass(parameter.type)
	}

	@JvmStatic
	fun getActualClass(clazz: Class<*>): Class<*> {
		return if (clazz.isArray) clazz.componentType else clazz
	}

	@JvmStatic
	fun getActualClass(parameter: Parameter): Class<*> {
		return getActualClass(parameter.type)
	}

}