package net.volcano.jdacommands.model.command.arguments.implementation;

import lombok.Builder;

@Builder
public class RawArgument {
	
	public final String value;
	public final int startIndex;
	public final boolean inParenthesis;
	
	RawArgument(String value, int startIndex, boolean inParenthesis) {
		this.value = value;
		this.startIndex = startIndex;
		this.inParenthesis = inParenthesis;
	}
}
