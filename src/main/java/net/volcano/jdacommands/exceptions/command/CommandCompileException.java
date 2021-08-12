package net.volcano.jdacommands.exceptions.command;

import kotlin.reflect.KFunction;

public class CommandCompileException extends Exception {
	
	public CommandCompileException(KFunction<?> function, String message) {
		super(function.getClass() + " : " + function.getName() + " : " + message);
	}
	
}
