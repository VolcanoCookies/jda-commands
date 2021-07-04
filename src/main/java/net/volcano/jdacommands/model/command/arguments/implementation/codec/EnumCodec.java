package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.command.arguments.implementation.argument.EnumArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@Component
public class EnumCodec extends Codec<Enum<?>> {
	
	@Override
	public EnumArgument buildArgument(Parameter parameter, Type actualType) {
		var builder = EnumArgument.builder();
		
		if (!(parameter.getParameterizedType() instanceof Class)) {
			throw new IllegalArgumentException("Parameter type not instance of class");
		}
		
		var enumClass = ((Class<Enum>) actualType);
		
		builder.options(enumClass.getEnumConstants());
		
		return builder.build();
	}
	
}