package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.ParameterData;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.OffsetDateTimeArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;

import java.time.OffsetDateTime;

public class OffsetDateTimeCodec extends Codec<OffsetDateTime> {
	
	@Override
	protected CommandArgument<OffsetDateTime> buildArgument(ParameterData data) {
		return OffsetDateTimeArgument.builder().build();
	}
	
}
