package net.volcano.jdacommands.model.command

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.lang.reflect.Method

class CoroutineFunction {

	fun invoke(method: Method, instance: Any, args: Array<Any>): Any? {

		val job = GlobalScope.async {
			method.invoke(instance, args)
		}

		return runBlocking { job.await() }

	}

}