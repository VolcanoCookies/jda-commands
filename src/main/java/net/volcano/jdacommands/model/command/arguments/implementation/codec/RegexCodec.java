package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.command.annotations.argument.Regex;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.RegexArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.regex.Matcher;

@Component
public class RegexCodec extends Codec<Matcher> {
	
	@Override
	protected CommandArgument<Matcher> buildArgument(Parameter parameter, Type actualType) {
		var builder = RegexArgument.builder();
		
		if (parameter.isAnnotationPresent(Regex.class)) {
			
			var annotation = parameter.getAnnotation(Regex.class);
			
			builder.regex(annotation.value());
			builder.flags(annotation.flags());
			
		} else {
			throw new IllegalArgumentException("No Regex annotation for regex argument.");
		}
		
		return builder.build();
	}
	
}
