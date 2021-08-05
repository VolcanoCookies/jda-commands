package net.volcano.jdacommands.model.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Help {
	
	String usage() default "GENERATE";
	
	String description();
	
	String category() default "DEFAULT";
	
	String[] examples() default {};
	
	String details() default "";
	
	Permission[] permissions() default {};
	
}

