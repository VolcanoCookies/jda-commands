package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.StringArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;

@Component
public class StringCodec extends Codec<String> {
	
	@Override
	protected CommandArgument<String> buildArgument(Parameter parameter) {
		var builder = StringArgument.builder();
		return builder.build();
	}
}
