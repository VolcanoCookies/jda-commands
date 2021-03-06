package net.volcano.jdacommands.model.command.annotations;

import net.volcano.jdacommands.model.command.Command;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommandMethod {
	
	@AliasFor("path")
	String[] value() default {""};
	
	@AliasFor("value")
	String[] path() default {""};
	
	String permissions() default "";
	
	net.volcano.jdacommands.model.command.Command.Source source() default Command.Source.DEFAULT;
	
	boolean sensitive() default false;
	
	boolean global() default false;
	
}
