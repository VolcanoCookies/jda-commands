package net.volcano.jdacommands.model.command.arguments.implementation.argument;

import kotlin.Pair;
import lombok.experimental.SuperBuilder;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.model.command.arguments.CommandArgument;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;

@SuperBuilder
public class PairArgument extends CommandArgument<Pair<?, ?>> {
	
	protected final CommandArgument<?> typeArgument1;
	protected final CommandArgument<?> typeArgument2;
	
	@Override
	public Pair<?, ?> parseValue(ArgumentParsingData data) throws InvalidArgumentsException {
		
		try {
			return new Pair<>(typeArgument1.parseValue(data), null);
		} catch (InvalidArgumentsException e) {
			return new Pair<>(null, typeArgument2.parseValue(data));
		}
		
	}
	
	@Override
	public String getUsage() {
		return "<" + typeArgument1.getUsage() + " | " + typeArgument2 + ">";
	}
}
