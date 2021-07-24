package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.ParameterData;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.BooleanArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

@Component
public class BooleanCodec extends Codec<Boolean> {
	
	@Override
	protected CommandArgument<Boolean> buildArgument(ParameterData data) {
		var builder = BooleanArgument.builder();
		
		return builder.build();
	}
}
