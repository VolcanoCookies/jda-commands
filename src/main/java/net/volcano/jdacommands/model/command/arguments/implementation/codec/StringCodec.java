package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.ParameterData;
import net.volcano.jdacommands.model.command.annotations.argument.Arg;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.StringArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

@Component
public class StringCodec extends Codec<String> {
	
	@Override
	protected CommandArgument<String> buildArgument(ParameterData data) {
		var builder = StringArgument.builder();
		
		var arg = data.parameter.getAnnotation(Arg.class);
		
		if (arg != null) {
			builder.min(arg.min() != Double.MIN_VALUE ? (int) arg.min() : null);
			builder.max(arg.max() != Double.MAX_VALUE ? (int) arg.max() : null);
		}
		
		return builder.build();
	}
}
