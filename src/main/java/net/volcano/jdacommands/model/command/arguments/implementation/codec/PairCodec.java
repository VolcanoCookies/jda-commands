package net.volcano.jdacommands.model.command.arguments.implementation.codec;

import kotlin.Pair;
import net.volcano.jdacommands.model.ClassUtil;
import net.volcano.jdacommands.model.ParameterData;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.argument.PairArgument;
import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;

@Component
public class PairCodec extends Codec<Pair<?, ?>> {
	
	@Override
	protected CommandArgument<Pair<?, ?>> buildArgument(ParameterData data) {
		var builder = PairArgument.builder();
		
		var clazz1 = (Class<?>) ((ParameterizedType) data.parameter.getParameterizedType()).getActualTypeArguments()[0];
		var clazz2 = (Class<?>) ((ParameterizedType) data.parameter.getParameterizedType()).getActualTypeArguments()[1];
		
		if (clazz1.equals(clazz2)) {
			throw new IllegalArgumentException("Pair argument needs to have two different types.");
		}
		
		var codec1 = data.codecRegistry.getCodec((Class<?>) ClassUtil.getCodecType(clazz2));
		var codec2 = data.codecRegistry.getCodec((Class<?>) ClassUtil.getCodecType(clazz2));
		
		if (codec1 == null)
			throw new IllegalArgumentException("Codec for class " + clazz1 + " not found.");
		if (codec2 == null)
			throw new IllegalArgumentException("Codec for class " + clazz2 + " not found.");
		
		var arg1 = codec1.encodeArgument(new ParameterData(
				data.parameter,
				ClassUtil.getActualType(clazz1),
				ClassUtil.getCodecType(clazz1),
				data.codecRegistry
		));
		
		var arg2 = codec2.encodeArgument(new ParameterData(
				data.parameter,
				ClassUtil.getActualType(clazz2),
				ClassUtil.getCodecType(clazz2),
				data.codecRegistry
		));
		
		builder.typeArgument1(arg1);
		builder.typeArgument2(arg2);
		
		return builder.build();
	}
}
