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
			var aMin = arg.min() == Double.MIN_VALUE ? null : (int) arg.min();
			var aMax = arg.max() == Double.MAX_VALUE ? null : (int) arg.max();
			
			builder.min(aMin);
			builder.max(aMax);
		}
		
		return builder.build();
	}
}
