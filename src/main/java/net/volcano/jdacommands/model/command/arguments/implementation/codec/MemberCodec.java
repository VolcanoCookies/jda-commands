package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.dv8tion.jda.api.entities.Member;
import net.volcano.jdacommands.model.command.annotations.argument.DefaultToCaller;
import net.volcano.jdacommands.model.command.annotations.argument.Optional;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.MemberArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;

import javax.annotation.Nullable;
import java.lang.reflect.Parameter;

public class MemberCodec extends Codec<Member> {
	
	@Override
	public CommandArgument<Member> buildArgument(Parameter parameter) {
		var builder = MemberArgument.builder();
		
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
