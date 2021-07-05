package net.volcano.jdacommands.model.command.arguments.interfaces;

import net.volcano.jdacommands.model.command.annotations.argument.Optional;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdautils.utils.ClassUtil;

import javax.annotation.Nullable;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public abstract class Codec<T> {
	
	protected Class<? extends CommandArgument<T>> argumentClass;
	
	public CommandArgument<T> encodeArgument(Parameter parameter, Type codecType, Type actualType) {
		
		if (codecType != ClassUtil.stripWildcard(ClassUtil.getGenericType(getClass()))) {
			throw new IllegalArgumentException("Invalid codec used for type.");
		}
		var arg = buildArgument(parameter);
		
		arg.setNullable(parameter.isAnnotationPresent(Nullable.class));
		
		arg.setOptional(parameter.isAnnotationPresent(Optional.class));
		
		arg.setParameter(parameter);
		
		arg.setType(actualType);
		
		return arg;
		
	}
	
	protected abstract CommandArgument<T> buildArgument(Parameter parameter);
	
}
