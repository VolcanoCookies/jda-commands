package net.volcano.jdacommands.model

import net.volcano.jdautilities.utils.ClassUtil
import java.lang.reflect.Parameter
import java.lang.reflect.Type

object ClassUtil {

	@JvmStatic
	fun getCodecType(clazz: Class<*>): Type {
		val actualType: Class<*> = getActualType(clazz) as Class<*>

		return when {
			actualType.isEnum -> Enum::class.java
			actualType.isPrimitive -> ClassUtil.dePrimitivize(actualType)
			else -> actualType
		}
	}

	@JvmStatic
	fun getCodecType(parameter: Parameter): Type {
		return getCodecType(parameter.type)
	}

	@JvmStatic
	fun getActualType(clazz: Class<*>): Type {
		return if (clazz.isArray) clazz.componentType else clazz
	}

	@JvmStatic
	fun getActualType(parameter: Parameter): Type {
		return getActualType(parameter.type)
	}

}