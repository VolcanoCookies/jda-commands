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
			var aMin = arg.min() == Double.MIN_VALUE ? null : (long) arg.min();
			var aMax = arg.max() == Double.MAX_VALUE ? null : (long) arg.max();
			
			builder.min(aMin);
			builder.max(aMax);
		}
		
		return builder.build();
	}
}
