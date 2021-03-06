package net.volcano.jdacommands.model.command.arguments;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.volcano.jdacommands.exceptions.command.parsing.InvalidArgumentsException;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;
import net.volcano.jdautilities.utils.StringUtilKt;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@Getter
@Setter
@SuperBuilder
public abstract class CommandArgument<T> {
	
	/**
	 * The usage of this argument
	 */
	public String usage;
	
	/**
	 * If the value is allowed to be missing
	 */
	public Boolean optional;
	
	/**
	 * If the value is allowed to resolve to {@code null}
	 */
	public Boolean nullable;
	
	/**
	 * The name of the parameter behind this argument
	 */
	public Parameter parameter;
	
	/**
	 * The type of this argument.
	 * For arrays this is the component type.
	 * For enums it will be the actual enum type and not Enum.class
	 */
	public Type type;
	
	/**
	 * How to actually parse the argument
	 *
	 * @param data the data to parse
	 * @return the parsed value
	 */
	public abstract T parseValue(ArgumentParsingData data) throws InvalidArgumentsException;
	
	public String getUsage() {
		
		// Get usage, provided or generated from parameter name.
		var u = usage != null ? usage : StringUtilKt.camelCaseToSpaces(StringUtilKt.capitalize(parameter.getName()));
		
		if (optional) {
			return "[" + u + "]";
		} else {
			return "<" + u + ">";
		}
	}
	
	public String getDetails() {
		return "";
	}
	
}
