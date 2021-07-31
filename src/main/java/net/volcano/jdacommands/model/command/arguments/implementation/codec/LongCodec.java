package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.ParameterData;
import net.volcano.jdacommands.model.command.annotations.argument.Arg;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.LongArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

@Component
public class LongCodec extends Codec<Long> {
	
	@Override
	protected CommandArgument<Long> buildArgument(ParameterData data) {
		var builder = LongArgument.builder();
		
		var arg = data.parameter.getAnnotation(Arg.class);
		
		if (arg != null) {
			builder.min((long) arg.min());
			builder.max((long) arg.max());
		} else {
			builder.min(Long.MIN_VALUE);
			builder.max(Long.MAX_VALUE);
		}
		
		return builder.build();
	}
}
