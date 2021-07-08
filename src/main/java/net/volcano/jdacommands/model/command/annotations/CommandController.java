package net.volcano.jdacommands.model.command.annotations;

import net.volcano.jdacommands.model.command.Command;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandController {
	
	@AliasFor("path")
	String[] value() default {""};
	
	/**
	 * @return base path for all commands under this controller
	 */
	@AliasFor("value")
	String[] path() default {""};
	
	/**
	 * @return base permissions required for any command under this controller
	 */
	String permissions() default "";
	
	/**
	 * @return Base source to apply to all commands under this controller unless they specify otherwise
	 */
	net.volcano.jdacommands.model.command.Command.Source source() default Command.Source.DEFAULT;
	
	/**
	 * The default category for this controller commands
	 */
	String category() default "general";
	
}
