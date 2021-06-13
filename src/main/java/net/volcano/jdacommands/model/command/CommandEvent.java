package net.volcano.jdacommands.model.command;

import lombok.Builder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.volcano.jdacommands.exceptions.command.run.MissingPermissionsException;
import net.volcano.jdacommands.exceptions.command.run.PermissionsOnCooldownException;
import net.volcano.jdacommands.interfaces.CommandClient;
import net.volcano.jdacommands.interfaces.QueryResult;
import net.volcano.jdacommands.model.command.arguments.ParsedData;
import net.volcano.jdacommands.model.menu.Confirmation;
import net.volcano.jdacommands.model.menu.pagers.EmbedPager;
import net.volcano.jdacommands.model.menu.pagers.EmbedPagerBuilder;
import net.volcano.jdautils.constants.Colors;
import net.volcano.jdautils.constants.Reactions;
import net.volcano.jdautils.utils.RoleUtil;
import net.volcano.jdautils.utils.UserUtil;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class CommandEvent extends MessageReceivedEvent {
	
	/**
	 * The command client, providing access to underlying classes
	 */
	public final CommandClient client;
	
	/**
	 * The command this event is going to
	 */
	public final Command command;
	
	/**
	 * The permissions of the user executing the command
	 */
	public final Set<String> userPermissions;
	
	/**
	 * The data parsed to run this argument
	 */
	public final ParsedData data;
	
	@Builder
	public CommandEvent(CommandClient client, ParsedData data) {
		super(data.event.getJDA(), data.event.getResponseNumber(), data.event.getMessage());
		this.client = client;
		this.data = data;
		command = data.command;
		userPermissions = client.getPermissionProvider().getPermissions(this);
	}
	
	/**
	 * Respond to the command caller in the same channel
	 *
	 * @param embedBuilder the response
	 * @return a message action
	 */
	@CheckReturnValue
	public MessageAction respond(EmbedBuilder embedBuilder) {
		return getChannel().sendMessage(embedBuilder.build());
	}
	
	/**
	 * Respond to the command caller in the same channel
	 *
	 * @param messageBuilder the response
	 * @return a message action
	 */
	@CheckReturnValue
	public MessageAction respond(MessageBuilder messageBuilder) {
		return getChannel().sendMessage(messageBuilder.build());
	}
	
	/**
	 * Respond to the command caller in the same channel
	 *
	 * @param message the response
	 * @return a message action
	 */
	@CheckReturnValue
	public MessageAction respond(String message) {
		return getChannel().sendMessage(message);
	}
	
	/**
	 * Respond to the command caller in the same channel
	 *
	 * @param embedPagerBuilder the response
	 * @return a message action
	 */
	@CheckReturnValue
	public RestAction<Message> respond(EmbedPagerBuilder embedPagerBuilder) {
		EmbedPager embedPager = embedPagerBuilder.build();
		embedPager.setUserId(getAuthor().getId());
		return getChannel().sendMessage(embedPager.getPage())
				.map(message -> {
					embedPager.setMessageId(message.getId());
					embedPager.postSend(message);
					client.getReactionMenuClient()
							.register(embedPager);
					return message;
				});
	}
	
	/**
	 * Respond to the command caller in their private channel
	 *
	 * @param embedBuilder the response
	 * @return a action ready for execution
	 */
	@CheckReturnValue
	public RestAction<Message> respondPrivate(EmbedBuilder embedBuilder) {
		return getAuthor()
				.openPrivateChannel()
				.flatMap(c -> c.sendMessage(embedBuilder.build()));
	}
	
	/**
	 * Respond to the command caller in their private channel
	 *
	 * @param messageBuilder the response
	 * @return a action ready for execution
	 */
	@CheckReturnValue
	public RestAction<Message> respondPrivate(MessageBuilder messageBuilder) {
		return getAuthor()
				.openPrivateChannel()
				.flatMap(c -> c.sendMessage(messageBuilder.build()));
	}
	
	/**
	 * Respond to the command caller in their private channel
	 *
	 * @param message the response
	 * @return a action ready for execution
	 */
	@CheckReturnValue
	public RestAction<Message> respondPrivate(String message) {
		return getAuthor()
				.openPrivateChannel()
				.flatMap(c -> c.sendMessage(message));
	}
	
	/**
	 * Respond to the command caller with a success message
	 *
	 * @param message the response
	 * @return a action ready for execution
	 */
	@CheckReturnValue
	public MessageAction respondSuccess(String message) {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setDescription("```diff\n+ Success +```\n\n" + message.trim());
		embedBuilder.setColor(Colors.SUCCESS);
		return respond(embedBuilder);
	}
	
	/**
	 * Respond to the command caller with an informative message
	 *
	 * @param message the response
	 * @return a action ready for execution
	 */
	@CheckReturnValue
	public MessageAction respondInfo(String message) {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setDescription("```asciidoc\n= Info =```\n\n" + message.trim());
		embedBuilder.setColor(Colors.INFO);
		return respond(embedBuilder);
	}
	
	/**
	 * Respond to the command caller with an error
	 *
	 * @param message the response
	 * @return a action ready for execution
	 */
	@CheckReturnValue
	public MessageAction respondError(String message) {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setDescription("```diff\n- ERROR -```\n\n" + message.trim());
		embedBuilder.setColor(Colors.ERROR);
		return respond(embedBuilder);
	}
	
	/**
	 * Respond to the command caller with a partial success
	 *
	 * @param message the response
	 * @return a action ready for execution
	 */
	@CheckReturnValue
	public MessageAction respondPartialSuccess(String message) {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle("Partial Success");
		embedBuilder.setDescription(message.trim());
		embedBuilder.setColor(Colors.ERROR);
		return respond(embedBuilder);
	}
	
	/**
	 * Create a yes/no prompt.
	 * The prompt will last 5 minutes, after which it will default to {@code false}
	 *
	 * @param content  the contents of the prompt
	 * @param consumer what to do with the answer
	 */
	@CheckReturnValue
	public RestAction<?> askConfirmation(String content, Consumer<Boolean> consumer) {
		return respond(Confirmation.getEmbed(content))
				.map(message -> {
					Confirmation confirmation = new Confirmation(getAuthor().getId(), message.getId());
					client.getReactionMenuClient()
							.register(confirmation);
					message.addReaction(Reactions.YES).queue();
					message.addReaction(Reactions.NO).queue();
					confirmation.getFuture().thenAcceptAsync(consumer);
					return confirmation.getFuture();
				});
	}
	
	/**
	 * Create a yes/no prompt.
	 * The prompt will last 5 minutes, after which it will default to {@code false}
	 *
	 * @param content the contents of the prompt
	 * @param user    the user to ask
	 * @return a future that completes when a answer is provided.
	 */
	@CheckReturnValue
	public RestAction<?> askConfirmation(String content, User user, Consumer<Boolean> consumer) {
		return user.openPrivateChannel()
				.flatMap(c -> c.sendMessage(Confirmation.getEmbed(content).build()))
				.map(message -> {
					Confirmation confirmation = new Confirmation(message.getId(), user.getId());
					client.getReactionMenuClient()
							.register(confirmation);
					message.addReaction(Reactions.YES).queue();
					message.addReaction(Reactions.NO).queue();
					confirmation.getFuture().thenAcceptAsync(consumer);
					return null;
				});
	}
	
	/**
	 * Find a user
	 * If ran in a guild, will only look at users in that guild
	 *
	 * @param query the query to find by
	 * @return the best match, or null if none found
	 */
	@Nullable
	public User findUser(String query) {
		return UserUtil.findUser(query, getJDA(), isFromGuild() ? getGuild() : null);
	}
	
	/**
	 * Find a role
	 * If ran in a guild, will only look for roles in that guild
	 *
	 * @param query the query to find by
	 * @return the best match, or null if none found
	 */
	@Nullable
	public Role findRole(String query) {
		return RoleUtil.findRole(query, getJDA(), isFromGuild() ? getGuild() : null);
	}
	
	@Nullable
	@Override
	public Guild getGuild() {
		return isFromGuild() ? getTextChannel().getGuild() : null;
	}
	
	@Nullable
	public String getGuildId() {
		return isFromGuild() ? getGuild().getId() : null;
	}
	
	/**
	 * Check if the command author has the specified permissions.
	 * <p>
	 * This checks the server the command was ran in, or globally if ran in dms.
	 *
	 * @param permission the permissions to check for.
	 * @return {@code true} if, and only if, the author has all the permissions.
	 */
	public QueryResult hasPermissions(String permission) {
		return hasPermissions(getGuild(), permission);
	}
	
	/**
	 * Check if the command author has the specified permissions.
	 *
	 * @param guild      the guild to check in, or null for global.
	 * @param permission the permissions to check for.
	 * @return {@code true} if, and only if, the author has all the permissions.
	 */
	public QueryResult hasPermissions(@Nullable Guild guild, String permission) {
		return client.getPermissionClient().checkPermissions(getAuthor(), guild, permission);
	}
	
	/**
	 * Check if the command author has the specified permissions.
	 * <p>
	 * This checks the server the command was ran in, or globally if ran in dms.
	 * <p>
	 * This method will throw an error if the author does not have the permissions provided.
	 *
	 * @param permission the permission to check for.
	 * @throws MissingPermissionsException if the author does not have the required permissions.
	 */
	public void checkPermission(String permission) throws MissingPermissionsException, PermissionsOnCooldownException {
		checkPermission(getGuild(), permission);
	}
	
	/**
	 * Check if the command author has the specified permissions.
	 * <p>
	 * This checks the server the command was ran in, or globally if ran in dms.
	 * <p>
	 * This method will throw an error if the author does not have the permissions provided.
	 *
	 * @param permission the permissions to check for.
	 * @param guild      the guild to check in, or null for global.
	 * @throws MissingPermissionsException if the author does not have the required permissions.
	 */
	public void checkPermission(@Nullable Guild guild, String permission) throws MissingPermissionsException, PermissionsOnCooldownException {
		var res = client.getPermissionClient().checkPermissions(getAuthor(), getGuild(), permission);
		if (!res.getHasPermissions()) {
			throw new MissingPermissionsException(guild, permission);
		} else if (res.getOnCooldown()) {
			throw new PermissionsOnCooldownException(guild, permission, Objects.requireNonNull(res.getCooldownExpiration()));
		}
	}
	
}
