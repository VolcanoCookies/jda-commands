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
			builder.min(arg.min());
			builder.max(arg.max());
			
			if (arg.min() != Integer.MIN_VALUE && arg.max() != Integer.MAX_VALUE) {
				builder.usage("<" + arg.min() + "-" + arg.max() + ">");
			} else if (arg.min() != Integer.MIN_VALUE) {
				builder.usage("< >=" + arg.min() + " >");
			} else if (arg.max() != Integer.MAX_VALUE) {
				builder.usage("< <=" + arg.max() + ">");
			}
		} else {
			builder.min(Double.MIN_VALUE);
			builder.max(Double.MAX_VALUE);
		}
		
		return builder.build();
	}
}
