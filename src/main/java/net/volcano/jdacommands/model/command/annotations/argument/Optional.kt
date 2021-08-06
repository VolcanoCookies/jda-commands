package net.volcano.jdacommands.model.command.annotations.argument

import java.lang.annotation.Inherited

@Inherited
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Optional