package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.BooleanArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;

@Component
public class BooleanCodec extends Codec<Boolean> {
	
	@Override
	protected CommandArgument<Boolean> buildArgument(Parameter parameter) {
		var builder = BooleanArgument.builder();
		
		return builder.build();
	}
}
