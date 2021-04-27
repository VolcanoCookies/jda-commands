package net.volcano.jdacommands.model.command.arguments.implementation;

import net.volcano.jdacommands.model.command.arguments.interfaces.Codec;
import net.volcano.jdacommands.model.command.arguments.interfaces.CodecRegistry;

import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

public class CodecRegistryImpl implements CodecRegistry {
	
	private final Map<Class<?>, Codec<?>> codecMap = new HashMap<>();
	
	@Override
	@Nullable
	public <T> Codec<T> getCodec(Class<T> clazz) {
		return (Codec<T>) codecMap.getOrDefault(clazz, null);
	}
	
	@Override
	public <T> void registerCodec(Codec<T> codec) {
		Class<?> clazz = (Class<?>) ((ParameterizedType) codec.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		codecMap.put(clazz, codec);
	}
	
}
