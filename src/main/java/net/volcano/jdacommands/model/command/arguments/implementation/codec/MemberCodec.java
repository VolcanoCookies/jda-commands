package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.dv8tion.jda.api.entities.Member;
import net.volcano.jdacommands.model.ParameterData;
import net.volcano.jdacommands.model.command.annotations.argument.Arg;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.MemberArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

@Component
public class MemberCodec extends Codec<Member> {
	
	@Override
	public CommandArgument<Member> buildArgument(ParameterData data) {
		var builder = MemberArgument.builder();
		
		var arg = data.parameter.getAnnotation(Arg.class);
		
		if (arg != null) {
			builder.defaultToCaller(arg.defaultToCaller());
		}
		
		builder.usage(data.parameter.getName());
		
		return builder.build();
	}
	
}
