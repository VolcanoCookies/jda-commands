package net.volcano.jdacommands.model.command.arguments;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;
import net.volcano.jdautils.utils.StringUtil;

import java.lang.reflect.Parameter;

@Getter
@Setter
@SuperBuilder
public abstract class CommandArgument<T> {
	
	/**
	 * The usage of this argument
	 */
	protected String usage;
	
	/**
	 * If the value is allowed to be missing
	 */
	protected Boolean optional;
	
	/**
	 * If the value is allowed to resolve to {@code null}
	 */
	protected Boolean nullable;
	
	/**
	 * The name of the parameter behind this argument
	 */
	protected Parameter parameter;
	
	/**
	 * How to actually parse the argument
	 *
	 * @param data the data to parse
	 * @return the parsed value
	 */
	public abstract T parseValue(ArgumentParsingData data) throws InvalidArgumentsException;
	
	public String getUsage() {
		return "<" + StringUtil.cameCaseToSpaces(StringUtil.capitalize(parameter.getName())) + ">";
	}
}
