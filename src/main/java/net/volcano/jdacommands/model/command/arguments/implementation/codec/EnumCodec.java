package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.ParameterData;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.EnumArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

@Component
public class EnumCodec extends Codec<Enum<?>> {
	
	@Override
	public EnumArgument buildArgument(ParameterData data) {
		var builder = EnumArgument.builder();
		
		if (!(data.parameter.getParameterizedType() instanceof Class)) {
			throw new IllegalArgumentException("Parameter type not instance of class");
		}
		
		var enumClass = ((Class<Enum>) data.parameter.getParameterizedType());
		
		if (enumClass.isArray()) {
			enumClass = (Class<Enum>) enumClass.componentType();
		}
		
		builder.options(enumClass.getEnumConstants());
		
		return builder.build();
	}
	
}