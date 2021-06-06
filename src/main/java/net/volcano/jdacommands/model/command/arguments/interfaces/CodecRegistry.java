package net.volcano.jdacommands.model.command.arguments.interfaces;

import net.volcano.jdacommands.model.command.arguments.implementation.codec.*;

import javax.annotation.Nullable;

public interface CodecRegistry {
	
	@Nullable
	<T> Codec<T> getCodec(Class<T> clazz);
	
	<T> void registerCodec(Codec<T> codec);
	
	default void loadDefaults() {
		registerCodec(new BooleanCodec());
		registerCodec(new MemberCodec());
		registerCodec(new LongCodec());
		registerCodec(new IntegerCodec());
		registerCodec(new DoubleCodec());
		registerCodec(new RegexCodec());
		registerCodec(new RoleCodec());
		registerCodec(new StringCodec());
		registerCodec(new SwitchCodec());
		registerCodec(new UserCodec());
	}
	
}
