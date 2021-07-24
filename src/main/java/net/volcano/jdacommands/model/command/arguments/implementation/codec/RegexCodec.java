package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.ParameterData;
import net.volcano.jdacommands.model.command.annotations.argument.Arg;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.RegexArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

@Component
public class RegexCodec extends Codec<Matcher> {
	
	@Override
	protected CommandArgument<Matcher> buildArgument(ParameterData data) {
		var builder = RegexArgument.builder();
		
		var arg = data.parameter.getAnnotation(Arg.class);
		
		if (arg == null || arg.regex().isEmpty())
			throw new IllegalArgumentException("No regex provided for matcher parameter.");
		
		builder.regex(arg.regex());
		builder.flags(arg.flags());
		
		return builder.build();
	}
	
}
