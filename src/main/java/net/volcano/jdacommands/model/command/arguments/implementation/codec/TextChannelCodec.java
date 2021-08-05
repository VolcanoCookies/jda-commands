package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.dv8tion.jda.api.entities.TextChannel;
import net.volcano.jdacommands.model.ParameterData;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.TextChannelArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

@Component
public class TextChannelCodec extends Codec<TextChannel> {
	
	@Override
	protected CommandArgument<TextChannel> buildArgument(ParameterData data) {
		var builder = TextChannelArgument.builder();
		
		return builder.build();
	}
	
}
