package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.LongArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;

@Component
public class LongCodec extends Codec<Long> {
	
	@Override
	protected CommandArgument<Long> buildArgument(Parameter parameter) {
		var builder = LongArgument.builder();
		
		return builder.build();
	}
}
