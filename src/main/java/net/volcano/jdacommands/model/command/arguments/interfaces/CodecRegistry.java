package net.volcano.jdacommands.model.command.arguments.interfaces;

import javax.annotation.Nullable;

public interface CodecRegistry {
	
	@Nullable
	<T> Codec<T> getCodec(Class<T> clazz);
	
	<T> void registerCodec(Codec<T> codec);
	
}
