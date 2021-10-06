package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.ParameterData;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.EnumArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import net.volcano.jdautilities.utils.ListUtilKt;
import net.volcano.jdautilities.utils.StringUtilKt;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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
		
		var constants = enumClass.getEnumConstants();
		
		if (constants.length == 0) {
			throw new IllegalArgumentException("Cannot have empty enum as argument");
		}
		
		builder.options(enumClass.getEnumConstants());
		
		builder.usage(ListUtilKt.asString(Arrays.asList(constants), " | ", e -> StringUtilKt.capitalize(e.name().toLowerCase())));
		
		return builder.build();
	}
	
}