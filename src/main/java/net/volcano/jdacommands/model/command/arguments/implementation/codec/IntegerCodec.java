package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.IntegerArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;

import java.lang.reflect.Parameter;

public class IntegerCodec extends Codec<Integer> {
	
	@Override
	protected CommandArgument<Integer> buildArgument(Parameter parameter) {
		var builder = IntegerArgument.builder();
		
		return builder.build();
	}
}
