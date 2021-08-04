package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.ParameterData;
import net.volcano.jdacommands.model.command.annotations.argument.Arg;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.DoubleArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

@Component
public class DoubleCodec extends Codec<Double> {
	
	@Override
	protected CommandArgument<Double> buildArgument(ParameterData data) {
		var builder = DoubleArgument.builder();
		
		var arg = data.parameter.getAnnotation(Arg.class);
		
		if (arg != null) {
			builder.min(arg.min() != Double.MIN_VALUE ? arg.min() : null);
			builder.max(arg.max() != Double.MAX_VALUE ? arg.max() : null);
		}
		
		return builder.build();
	}
}
