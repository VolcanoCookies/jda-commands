package net.volcano.jdacommands.model.command.annotations.argument;

import org.intellij.lang.annotations.Language;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Regex {
	
	@Language("RegExp")
	String value();
	
	int flags() default 0;
	
}
