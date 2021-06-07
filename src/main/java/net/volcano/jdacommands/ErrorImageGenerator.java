package net.volcano.jdacommands;

import net.volcano.jdautils.constants.Colors;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ErrorImageGenerator {
	
	public static Font font;
	public static FontMetrics metrics;
	
	static {
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(ErrorImageGenerator.class.getClassLoader().getResourceAsStream("Montserrat-Regular.ttf"))).deriveFont(48f);
			metrics = new BufferedImage(1, 1, 1)
					.createGraphics()
					.getFontMetrics(font);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
			font = null;
			metrics = null;
			System.exit(1);
		}
	}
	
	public static BufferedImage generateErrorImage(String message, int errorStart, int errorLength) {
		
		var pre = message.substring(0, errorStart);
		var err = message.substring(errorStart, errorStart + errorLength);
		var post = message.substring(errorStart + errorLength);
		
		var startLen = metrics.stringWidth(pre);
		var errorLen = metrics.stringWidth(err);
		
		var width = metrics.stringWidth(message);
		var height = metrics.getHeight();
		
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		var graphics = image.createGraphics();
		graphics.setFont(font);
		graphics.setRenderingHints(rh);
		
		Color backgroundColor = new Color(0x23272a);
		
		graphics.setColor(backgroundColor);
		graphics.fillRect(0, 0, width, height);
		graphics.setColor(Color.WHITE);
		graphics.drawString(pre, 0, height - metrics.getDescent());
		graphics.setColor(Colors.ERROR);
		graphics.drawString(err, startLen, height - metrics.getDescent());
		graphics.setColor(Color.WHITE);
		graphics.drawString(post, startLen + errorLen, height - metrics.getDescent());
		
		graphics.dispose();
		
		return image;
	}
	
}
