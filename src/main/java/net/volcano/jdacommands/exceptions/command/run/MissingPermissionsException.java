package net.volcano.jdacommands.exceptions.command.run;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.volcano.jdautils.utils.ListUtil;

import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
public class MissingPermissionsException extends CommandException {
	
	private final Set<String> missingFlags;
	private final boolean isGlobal;
	
	@Override
	protected void getErrorEmbed(EmbedBuilder embedBuilder) {
		embedBuilder.setTitle("Error: Missing " + (isGlobal ? "global" : "local") + " permissions");
		embedBuilder.setDescription("Missing; " + ListUtil.asString(", ", missingFlags, Objects::toString));
	}
}
