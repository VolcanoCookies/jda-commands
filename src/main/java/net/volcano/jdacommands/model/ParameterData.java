package net.volcano.jdacommands.model;

import lombok.RequiredArgsConstructor;
import net.volcano.jdacommands.model.command.arguments.interfaces.CodecRegistry;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@RequiredArgsConstructor
public class ParameterData {
	
	public final Parameter parameter;
	
	public final Type actualType;
	
	public final Type codecType;
	
	public final CodecRegistry codecRegistry;
	
}
