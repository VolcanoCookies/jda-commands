package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import net.volcano.jdacommands.model.ParameterData;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.PermissionHolderArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import net.volcano.jdacommands.permissions.PermissionHolder;
import org.springframework.stereotype.Component;

@Component
public class PermissionHolderCodec extends Codec<PermissionHolder> {
	
	@Override
	protected CommandArgument<PermissionHolder> buildArgument(ParameterData data) {
		var builder = PermissionHolderArgument.builder();
		
		return builder.build();
	}
}
