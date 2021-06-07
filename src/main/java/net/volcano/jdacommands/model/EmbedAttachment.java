package net.volcano.jdacommands.model;

import lombok.RequiredArgsConstructor;

import java.io.InputStream;

@RequiredArgsConstructor
public class EmbedAttachment {
	
	public final InputStream inputStream;
	public final String name;
	
}
