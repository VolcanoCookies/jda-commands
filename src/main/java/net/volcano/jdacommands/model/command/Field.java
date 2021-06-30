package net.volcano.jdacommands.model.command;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class Field {
	
	public final String name;
	
	public final String value;
	
	public final Boolean inline;
	
	public Field(String name, String value, Boolean inline) {
		this.name = name;
		this.value = value;
		this.inline = inline;
	}
	
	public Field(String name, String value) {
		this.name = name;
		this.value = value;
		inline = true;
	}
	
	public MessageEmbed.Field build() {
		return new MessageEmbed.Field(name, value, inline);
	}
	
}
