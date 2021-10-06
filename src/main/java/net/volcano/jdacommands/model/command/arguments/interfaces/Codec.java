package net.volcano.jdacommands.model.command.arguments.interfaces;

import net.volcano.jdacommands.model.ParameterData;
import net.volcano.jdacommands.model.command.annotations.argument.Arg;
import net.volcano.jdacommands.model.command.annotations.argument.Optional;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdautilities.utils.ClassUtil;

import javax.annotation.Nullable;

public abstract class Codec<T> {
	
	protected Class<? extends CommandArgument<T>> argumentClass;
	
	public CommandArgument<T> encodeArgument(ParameterData data) {
		
		var expectedType = ClassUtil.stripWildcard(ClassUtil.getGenericType(getClass()));
		
		if (data.codecType != expectedType) {
			throw new IllegalArgumentException("Invalid codec " + data.codecType.getTypeName() + " used for type " + expectedType.getTypeName() + ".");
		}
		var arg = buildArgument(data);
		
		var ann = data.parameter.getAnnotation(Arg.class);
		if (ann != null) {
			if (!ann.usage().equals("DEFAULT")) {
				arg.setUsage(ann.usage());
			}
		}
		
		arg.setNullable(data.parameter.isAnnotationPresent(Nullable.class));
		
		arg.setOptional(data.parameter.isAnnotationPresent(Optional.class));
		
		arg.setParameter(data.parameter);
		
		arg.setType(data.actualType);
		
		return arg;
		
	}
	
	protected abstract CommandArgument<T> buildArgument(ParameterData data);
	
}
