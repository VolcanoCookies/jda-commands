package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.command.arguments.implementation.argument.SwitchArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;

import java.lang.reflect.Parameter;

public class SwitchCodec extends Codec<Enum<?>> {
	
	@Override
	public SwitchArgument buildArgument(Parameter parameter) {
		var builder = SwitchArgument.builder();
		
		if (!(parameter.getParameterizedType() instanceof Class)) {
			throw new IllegalArgumentException("Parameter type not instance of class");
		}
		
		var enumClass = ((Class<Enum>) parameter.getParameterizedType());
		
		builder.options(enumClass.getEnumConstants());
		
		return builder.build();
	}
	
}