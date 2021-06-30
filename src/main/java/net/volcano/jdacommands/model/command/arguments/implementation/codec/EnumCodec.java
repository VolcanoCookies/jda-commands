package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.command.arguments.implementation.argument.EnumArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;

@Component
public class EnumCodec extends Codec<Enum<?>> {
	
	@Override
	public EnumArgument buildArgument(Parameter parameter) {
		var builder = EnumArgument.builder();
		
		if (!(parameter.getParameterizedType() instanceof Class)) {
			throw new IllegalArgumentException("Parameter type not instance of class");
		}
		
		var enumClass = ((Class<Enum>) parameter.getParameterizedType());
		
		builder.options(enumClass.getEnumConstants());
		
		return builder.build();
	}
	
}