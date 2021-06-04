package net.volcano.jdacommands.model.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Help {
	
	String usage() default "GENERATE";
	
	String description() default "No description found, yell at Volcano#2343.";
	
	String category() default "general";
	
}

