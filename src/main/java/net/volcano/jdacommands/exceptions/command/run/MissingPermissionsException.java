package net.volcano.jdacommands.exceptions.command.run;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.volcano.jdautils.utils.ListUtil;

import java.util.Objects;
import java.util.Set;

// TODO Actually use the guild to tell the user where they need permissions

@RequiredArgsConstructor
public class MissingPermissionsException extends CommandException {
	
	private final Set<String> missingFlags;
	private final Guild guild;
	
	public MissingPermissionsException(Guild guild, String... missingFlags) {
		this.missingFlags = Set.of(missingFlags);
		this.guild = guild;
	}
	
	@Override
	protected void getErrorEmbed(EmbedBuilder embedBuilder) {
		embedBuilder.setTitle("Error: Missing " + (guild == null ? "global" : "local") + " permissions");
		embedBuilder.setDescription("Missing; " + ListUtil.asString(", ", missingFlags, Objects::toString));
	}
}
