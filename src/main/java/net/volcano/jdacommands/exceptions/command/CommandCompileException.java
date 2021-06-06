package net.volcano.jdacommands.exceptions.command;

import java.lang.reflect.Method;

public class CommandCompileException extends Exception {
	
	public CommandCompileException(Method commandMethod, String message) {
		super(commandMethod.getDeclaringClass() + " : " + commandMethod.getName() + " : " + message);
	}
	
}
