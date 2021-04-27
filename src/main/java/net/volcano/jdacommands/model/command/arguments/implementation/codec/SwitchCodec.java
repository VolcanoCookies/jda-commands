package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.command.arguments.implementation.argument.SwitchArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;

public class SwitchCodec extends Codec<Enum<?>> {
	
	@Override
	public SwitchArgument buildArgument(Parameter parameter) {
		var builder = SwitchArgument.builder();
		
		var enumClass = ((Class<Enum>) ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0]);
		
		builder.options(enumClass.getEnumConstants());
		
		return builder.build();
	}
	
}