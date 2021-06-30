package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.dv8tion.jda.api.entities.User;
import net.volcano.jdacommands.model.command.annotations.argument.DefaultToCaller;
import net.volcano.jdacommands.model.command.annotations.argument.Optional;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.UserArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.lang.reflect.Parameter;

@Component
public class UserCodec extends Codec<User> {
	
	@Override
	public CommandArgument<User> buildArgument(Parameter parameter) {
		
		var builder = UserArgument.builder();
		
		if (parameter.isAnnotationPresent(Nullable.class)) {
			builder.nullable(true);
		}
		
		if (parameter.isAnnotationPresent(Optional.class)) {
			builder.optional(true);
		}
		
		if (parameter.isAnnotationPresent(DefaultToCaller.class)) {
			builder.defaultToCaller(true);
		}
		
		return builder.build();
	}
	
}
