package net.volcano.jdacommands.interfaces;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.volcano.jdacommands.model.command.CommandEvent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

public interface PermissionProvider {
	
	Set<String> getPermissions(String userId);
	
	Set<String> getPermissions(String userId, @Nullable String guildId);
	
	default Set<String> getPermissions(User user, @Nullable Guild guild) {
		return getPermissions(user.getId(), guild == null ? null : guild.getId());
	}
	
	default Set<String> getPermissions(CommandEvent event) {
		return getPermissions(event.getAuthor(), event.isFromGuild() ? event.getGuild() : null);
	}
	
	boolean hasPermissions(Collection<String> permissions, String userId, @Nullable String guildId);
	
	default boolean hasPermissions(Collection<String> permissions, User user, @Nullable Guild guild) {
		return hasPermissions(permissions, user.getId(), guild == null ? null : guild.getId());
	}
	
}


