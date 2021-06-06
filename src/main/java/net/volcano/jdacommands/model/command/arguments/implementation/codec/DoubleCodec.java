package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.DoubleArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;

import java.lang.reflect.Parameter;

public class DoubleCodec extends Codec<Double> {
	
	@Override
	protected CommandArgument<Double> buildArgument(Parameter parameter) {
		var builder = DoubleArgument.builder();
		
		return builder.build();
	}
}
