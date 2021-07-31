package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.ParameterData;
import net.volcano.jdacommands.model.command.annotations.argument.Arg;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.IntegerArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

@Component
public class IntegerCodec extends Codec<Integer> {
	
	@Override
	protected CommandArgument<Integer> buildArgument(ParameterData data) {
		var builder = IntegerArgument.builder();
		
		var arg = data.parameter.getAnnotation(Arg.class);
		
		if (arg != null) {
			builder.min((int) arg.min());
			builder.max((int) arg.max());
		} else {
			builder.min(Integer.MIN_VALUE);
			builder.max(Integer.MAX_VALUE);
		}
		
		return builder.build();
	}
}
