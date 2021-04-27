package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.NumberArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;

import java.lang.reflect.Parameter;

public class NumberCodec extends Codec<Long> {
	
	@Override
	protected CommandArgument<Long> buildArgument(Parameter parameter) {
		var builder = NumberArgument.builder();
		
		return builder.build();
	}
}
