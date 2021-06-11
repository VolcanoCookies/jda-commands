package net.volcano.jdacommands.exceptions.command.parsing;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.volcano.jdacommands.ErrorImageGenerator;
import net.volcano.jdacommands.exceptions.command.run.CommandException;
import net.volcano.jdacommands.model.EmbedAttachment;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Getter
public class InvalidArgumentsException extends CommandException {
	
	private final String hint;
	private final String content;
	
	private final int errorStartIndex;
	private final int errorLength;
	
	public InvalidArgumentsException(ArgumentParsingData data, int argumentIndex, String hint) {
		this.hint = hint;
		content = data.event.getMessage().getContentDisplay();
		errorStartIndex = data.rawArguments[argumentIndex].startIndex;
		errorLength = data.rawArguments[argumentIndex].value.length();
	}
	
	public InvalidArgumentsException(ArgumentParsingData data, String hint) {
		this.hint = hint;
		content = data.event.getMessage().getContentDisplay();
		errorStartIndex = data.rawArguments[data.currentArg].startIndex;
		errorLength = data.rawArguments[data.currentArg].value.length();
	}
	
	public InvalidArgumentsException(String content, int errorStartIndex, int errorLength, String hint) {
		this.hint = hint;
		this.content = content;
		this.errorStartIndex = errorStartIndex;
		this.errorLength = errorLength;
	}
	
	@Override
	protected EmbedBuilder getErrorEmbed(EmbedBuilder embedBuilder) {
		embedBuilder.setTitle("Error: Invalid arguments.");
		if (hint != null) {
			embedBuilder.addField("Hint", hint, false);
		}
		embedBuilder.setImage("attachment://errorImage.png");
		return embedBuilder;
	}
	
	@Override
	public List<EmbedAttachment> getAttachments() {
		
		var errorImage = ErrorImageGenerator.generateErrorImage(content, errorStartIndex, errorLength);
		var os = new ByteArrayOutputStream();
		try {
			ImageIO.write(errorImage, "png", os);
			return Collections.singletonList(new EmbedAttachment(new ByteArrayInputStream(os.toByteArray()), "errorImage.png"));
		} catch (IOException e) {
			return Collections.emptyList();
		}
	}
	
}

