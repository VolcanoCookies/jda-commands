package net.volcano.jdacommands.model.command

import lombok.Builder
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.requests.restaction.MessageAction
import net.volcano.jdacommands.exceptions.command.run.MissingPermissionsException
import net.volcano.jdacommands.exceptions.command.run.PermissionsOnCooldownException
import net.volcano.jdacommands.interfaces.CommandClient
import net.volcano.jdacommands.interfaces.Extension
import net.volcano.jdacommands.interfaces.QueryResult
import net.volcano.jdacommands.model.IntermediateEvent
import net.volcano.jdacommands.model.Templates.error
import net.volcano.jdacommands.model.Templates.info
import net.volcano.jdacommands.model.Templates.partialSuccess
import net.volcano.jdacommands.model.Templates.success
import net.volcano.jdacommands.model.command.arguments.ParsedData
import net.volcano.jdacommands.model.interaction.Confirmation
import net.volcano.jdacommands.model.interaction.Confirmation.Companion.message
import net.volcano.jdacommands.model.interaction.menu.EmbedMenuBuilder
import net.volcano.jdacommands.model.interaction.pager.EmbedPagerBuilder
import net.volcano.jdautils.utils.RoleUtil.findRole
import net.volcano.jdautils.utils.UserUtil
import javax.annotation.CheckReturnValue

class CommandEvent @Builder constructor(
	/**
	 * The command client, providing access to underlying classes
	 */
	val client: CommandClient,
	/**
	 * The data parsed to run this argument
	 */
	@JvmField val data: ParsedData,
	/**
	 * Can be used to inject anything into a CommandEvent and then cast it back on the user side to facilitate extending the CommandEvent locally with autowired beans.
	 */
	val extension: Extension?
) : IntermediateEvent(data.event.jda, data.event.responseNumber, data.event.message) {

	/**
	 * The command this event is going to
	 */
	@JvmField
	val command: Command = data.command

	/**
	 * The permissions of the user executing the command
	 */
	val userPermissions: Set<String> = client.permissionProvider.getPermissions(this)

	/**
	 * Respond to the command caller in the same channel
	 *
	 * @param embedBuilder the response
	 * @return a message action
	 */
	@CheckReturnValue
	fun respond(embedBuilder: EmbedBuilder): MessageAction {
		return channel.sendMessageEmbeds(embedBuilder.build())
	}

	/**
	 * Respond to the command caller in the same channel
	 *
	 * @param messageBuilder the response
	 * @return a message action
	 */
	@CheckReturnValue
	fun respond(messageBuilder: MessageBuilder): MessageAction {
		return channel.sendMessage(messageBuilder.build())
	}

	/**
	 * Respond to the command caller in the same channel
	 *
	 * @param message the response
	 * @return a message action
	 */
	@CheckReturnValue
	fun respond(message: String): MessageAction {
		return channel.sendMessage(message)
	}

	/**
	 * Respond to the command caller in the same channel
	 *
	 * @param embedPagerBuilder the response
	 * @return a message action
	 */
	@CheckReturnValue
	fun respond(embedPagerBuilder: EmbedPagerBuilder): RestAction<Message> {
		embedPagerBuilder.userId = author.id
		val embedPager = embedPagerBuilder.build()
		return channel.sendMessageEmbeds(embedPager.page)
			.map {
				embedPager.messageId = it.id
				embedPager.postSend(it)
				client.interactionClient
					.addListener(it, embedPager)
				it
			}
	}

	/**
	 * Respond to the command caller in the same channel
	 *
	 * @param embedMenuBuilder the response
	 * @return a message action
	 */
	@CheckReturnValue
	fun respond(embedMenuBuilder: EmbedMenuBuilder): RestAction<Message> {
		embedMenuBuilder.userId = author.id
		val embedMenu = embedMenuBuilder.build()
		return channel.sendMessageEmbeds(embedMenu.frontPage)
			.map {
				embedMenu.messageId = it.id
				embedMenu.postSend(it)
				client.interactionClient.addListener(it, embedMenu)
				it
			}
	}

	/**
	 * Respond to the command caller in their private channel
	 *
	 * @param embedBuilder the response
	 * @return a action ready for execution
	 */
	@CheckReturnValue
	fun respondPrivate(embedBuilder: EmbedBuilder): RestAction<Message> {
		return author
			.openPrivateChannel()
			.flatMap { it.sendMessageEmbeds(embedBuilder.build()) }
	}

	/**
	 * Respond to the command caller in their private channel
	 *
	 * @param messageBuilder the response
	 * @return a action ready for execution
	 */
	@CheckReturnValue
	fun respondPrivate(messageBuilder: MessageBuilder): RestAction<Message> {
		return author
			.openPrivateChannel()
			.flatMap { it.sendMessage(messageBuilder.build()) }
	}

	/**
	 * Respond to the command caller in their private channel
	 *
	 * @param message the response
	 * @return a action ready for execution
	 */
	@CheckReturnValue
	fun respondPrivate(message: String): RestAction<Message> {
		return author
			.openPrivateChannel()
			.flatMap { it.sendMessage(message) }
	}

	/**
	 * Respond to the command caller with a success message
	 *
	 * @param message the response
	 * @return a action ready for execution
	 */
	@CheckReturnValue
	fun respondSuccess(message: String, vararg fields: Field): MessageAction {
		return respond(success(message, *fields))
	}

	/**
	 * Respond to the command caller with an informative message
	 *
	 * @param message the response
	 * @return a action ready for execution
	 */
	@CheckReturnValue
	fun respondInfo(message: String, vararg fields: Field): MessageAction {
		return respond(info(message, *fields))
	}

	/**
	 * Respond to the command caller with an error
	 *
	 * @param message the response
	 * @return a action ready for execution
	 */
	@CheckReturnValue
	fun respondError(message: String, vararg fields: Field): MessageAction {
		return respond(error(message, *fields))
	}

	/**
	 * Respond to the command caller with a partial success
	 *
	 * @param message the response
	 * @return a action ready for execution
	 */
	@CheckReturnValue
	fun respondPartialSuccess(message: String, vararg fields: Field): MessageAction {
		return respond(partialSuccess(message, *fields))
	}

	/**
	 * Create a yes/no prompt.
	 * The prompt will last 5 minutes, after which it will default to `false`
	 *
	 * @param content   the contents of the prompt
	 * @param generator what to do with the answer
	 */
	@CheckReturnValue
	fun askConfirmation(
		content: String,
		generator: Function1<ButtonClickEvent, Any?>
	): RestAction<*> {
		return channel.sendMessage(message(content))
			.map {
				val confirmation = Confirmation(author.id, generator)
				client.interactionClient.addListener(it, confirmation)
				it
			}
	}

	/**
	 * Create a yes/no prompt.
	 * The prompt will last 5 minutes, after which it will default to `false`
	 *
	 * @param content   the contents of the prompt
	 * @param generator what to do with the answer
	 */
	@CheckReturnValue
	fun askConfirmation(
		content: String,
		generator: Function1<ButtonClickEvent, Any?>,
		deniedGenerator: Function1<ButtonClickEvent, Any?>?
	): RestAction<*> {
		return channel.sendMessage(message(content))
			.map {
				val confirmation = Confirmation(author.id, generator, deniedGenerator)
				client.interactionClient.addListener(it, confirmation)
				it
			}
	}

	/**
	 * Create a yes/no prompt.
	 * The prompt will last 5 minutes, after which it will default to `false`
	 *
	 * @param content the contents of the prompt
	 * @param user    the user to ask
	 * @return a future that completes when a answer is provided.
	 */
	@CheckReturnValue
	fun askConfirmation(
		content: String,
		user: User,
		generator: Function1<ButtonClickEvent, Any?>?,
		deniedGenerator: Function1<ButtonClickEvent, Any?>?
	): RestAction<*> {
		return user.openPrivateChannel()
			.flatMap { it.sendMessage(message(content)) }
			.map {
				val confirmation = Confirmation(user.id, generator, deniedGenerator)
				client.interactionClient.addListener(it, confirmation)
				it
			}
	}

	/**
	 * Find a user
	 * If ran in a guild, will only look at users in that guild
	 *
	 * @param query the query to find by
	 * @return the best match, or null if none found
	 */
	fun findUser(query: String): User? {
		return UserUtil.findUser(query, jda, guild)
	}

	/**
	 * Find a role
	 * If ran in a guild, will only look for roles in that guild
	 *
	 * @param query the query to find by
	 * @return the best match, or null if none found
	 */
	fun findRole(query: String): Role? {
		return findRole(query, jda, guild)
	}

	val guildId: String?
		get() = guild?.id

	/**
	 * Check if the command author has the specified permissions.
	 *
	 * @param guild      the guild to check in, or null for global.
	 * @param permission the permissions to check for.
	 * @return `true` if, and only if, the author has all the permissions.
	 */
	fun hasPermissions(permission: String, guild: Guild? = null): QueryResult {
		return client.permissionClient.checkPermissions(permission, author, guild ?: this.guild)
	}

	/**
	 * Check if the command author has the specified permissions.
	 *
	 *
	 * This checks the server the command was ran in, or globally if ran in dms.
	 *
	 *
	 * This method will throw an error if the author does not have the permissions provided.
	 *
	 * @param permission the permissions to check for.
	 * @param guild      the guild to check in, or null for global.
	 * @throws MissingPermissionsException if the author does not have the required permissions.
	 */
	@Throws(MissingPermissionsException::class, PermissionsOnCooldownException::class)
	fun checkPermission(permission: String, guild: Guild? = null) {
		val res = client.permissionClient.checkPermissions(
			permission,
			author,
			guild ?: this.guild,
			if (guild == this.guild) textChannel else null
		)
		if (!res.hasPermissions) {
			throw MissingPermissionsException(null, guild, permission)
		} else if (res.onCooldown) {
			throw PermissionsOnCooldownException(null, guild, permission, res.cooldownExpiration!!)
		}
	}

}