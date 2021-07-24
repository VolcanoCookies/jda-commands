package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.dv8tion.jda.api.entities.Role;
import net.volcano.jdacommands.model.ParameterData;
import net.volcano.jdacommands.model.command.annotations.argument.Arg;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.RoleArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

@Component
public class RoleCodec extends Codec<Role> {
	
	@Override
	protected CommandArgument<Role> buildArgument(ParameterData data) {
		var builder = RoleArgument.builder();
		
		var arg = data.parameter.getAnnotation(Arg.class);
		
		if (arg != null) {
			builder.sameGuild(arg.sameGuild());
		} else {
			builder.sameGuild(true);
		}
		
		builder.usage(data.parameter.getName());
		
		return builder.build();
	}
	
}
