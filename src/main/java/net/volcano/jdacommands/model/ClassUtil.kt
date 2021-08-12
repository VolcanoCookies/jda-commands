package net.volcano.jdacommands.model

import net.volcano.jdautils.utils.isEnum
import net.volcano.jdautils.utils.isPrimitive
import net.volcano.jdautils.utils.kClass
import net.volcano.jdautils.utils.primitive
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf

object ClassUtil {

	@JvmStatic
	fun getCodecClass(clazz: KClass<*>): KClass<*> {
		val actualType = getActualClass(clazz)

		return when {
			actualType.isEnum -> Enum::class
			actualType.isPrimitive -> actualType.primitive!!
			else -> actualType
		}
	}

	@JvmStatic
	fun getCodecClass(parameter: KParameter): KClass<*> {
		return getCodecClass(parameter.type.kClass)
	}

	@JvmStatic
	fun getActualClass(clazz: KClass<*>): KClass<*> {
		return if (clazz.isSubclassOf(Array::class)) clazz.javaObjectType.componentType.kotlin else clazz
	}

	@JvmStatic
	fun getActualClass(parameter: KParameter): KClass<*> {
		return getActualClass(parameter.type.kClass)
	}

}