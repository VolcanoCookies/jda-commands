package net.volcano.jdacommands.model.command.annotations.argument;

import org.intellij.lang.annotations.Language;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Arg {
	
	/**
	 * Min value for numerical input
	 */
	double min() default Double.MIN_VALUE;
	
	/**
	 * Max value for numerical input
	 */
	double max() default Double.MAX_VALUE;
	
	boolean defaultToCaller() default false;
	
	/**
	 * Regex for matcher argument
	 */
	@Language("RegExp")
	String regex() default "";
	
	/**
	 * Regex flags
	 */
	int flags() default 0;
	
	/**
	 * If roles need to be from the same guild as the command is issued from
	 */
	boolean sameGuild() default true;
	
	String usage() default "DEFAULT";
	
}
