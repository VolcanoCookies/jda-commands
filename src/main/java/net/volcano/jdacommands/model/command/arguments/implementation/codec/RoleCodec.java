package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.dv8tion.jda.api.entities.Role;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.RoleArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;

@Component
public class RoleCodec extends Codec<Role> {
	
	@Override
	protected CommandArgument<Role> buildArgument(Parameter parameter) {
		var builder = RoleArgument.builder();
		
		builder.sameGuild(true);
		
		builder.usage(parameter.getName());
		
		return builder.build();
	}
	
}
