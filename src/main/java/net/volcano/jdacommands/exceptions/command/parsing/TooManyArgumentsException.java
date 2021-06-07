package net.volcano.jdacommands.exceptions.command.parsing;

import net.dv8tion.jda.api.EmbedBuilder;
import net.volcano.jdacommands.ErrorImageGenerator;
import net.volcano.jdacommands.model.EmbedAttachment;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class TooManyArgumentsException extends ArgumentParsingException {
	
	public TooManyArgumentsException(ArgumentParsingData data, int argumentIndex) {
		super(data, argumentIndex);
	}
	
	@Override
	protected EmbedBuilder getErrorEmbed(EmbedBuilder embedBuilder) {
		embedBuilder.setTitle("Error: Too many argument.");
		embedBuilder.setThumbnail("attachment://errorImage.png");
		return embedBuilder;
	}
	
	@Override
	public List<EmbedAttachment> getAttachments() {
		var indexOffset = data.event.getMessage().getContentDisplay().length() - data.rawContent.length();
		
		int startIndex = data.rawArguments[argumentIndex].startIndex + indexOffset;
		int lengthLeft = data.rawContent.length() - startIndex + indexOffset;
		
		var errorImage = ErrorImageGenerator.generateErrorImage(data.event.getMessage().getContentDisplay(), startIndex, lengthLeft);
		var os = new ByteArrayOutputStream();
		try {
			ImageIO.write(errorImage, "png", os);
			return Collections.singletonList(new EmbedAttachment(new ByteArrayInputStream(os.toByteArray()), "errorImage.png"));
		} catch (IOException e) {
			return Collections.emptyList();
		}
	}
	
}
