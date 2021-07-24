package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.dv8tion.jda.api.entities.User;
import net.volcano.jdacommands.model.ParameterData;
import net.volcano.jdacommands.model.command.annotations.argument.Arg;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.UserArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

@Component
public class UserCodec extends Codec<User> {
	
	@Override
	public CommandArgument<User> buildArgument(ParameterData data) {
		var builder = UserArgument.builder();
		
		var arg = data.parameter.getAnnotation(Arg.class);
		
		if (arg != null) {
			builder.defaultToCaller(arg.defaultToCaller());
		}
		
		builder.usage(data.parameter.getName());
		
		return builder.build();
	}
	
}
