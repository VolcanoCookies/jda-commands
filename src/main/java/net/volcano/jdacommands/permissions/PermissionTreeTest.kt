package net.volcano.jdacommands.permissions

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.PrivateChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.RestAction
import net.volcano.jdacommands.client.PermissionClientImpl
import net.volcano.jdacommands.interfaces.PermissionProvider
import org.junit.jupiter.api.Test
import java.util.*

const val TEST_PERM_1 = "command.kick"
const val TEST_PERM_2 = "command.ban;2"
const val TEST_PERM_3 = "command.purge;300;3"

internal class PermissionTreeTest {

	val provider: PermissionProvider = TestProvider()
	var client = PermissionClientImpl(provider)

	val user1: User = FakeUser("123")
	val holder1 = PermissionHolder.parse(TEST_PERM_1)
	val user2: User = FakeUser("456")
	val holder2 = PermissionHolder.parse(TEST_PERM_2)
	val user3: User = FakeUser("789")
	val holder3 = PermissionHolder.parse(TEST_PERM_3)

	@Test
	fun regular() {

		var check1 = client.checkPermissions(holder1.permission, user1)

		assert(!check1.onCooldown)
		assert(check1.hasPermissions)
		assert(check1.holdsPermissions)

		client.invokeCooldown(user1, null, holder1)

		check1 = client.checkPermissions(holder1.permission, user1)

		assert(!check1.onCooldown)
		assert(check1.hasPermissions)
		assert(check1.holdsPermissions)

	}

	@Test
	fun cooldowns() {

		var check2 = client.checkPermissions(holder2.permission, user2)

		assert(!check2.onCooldown)
		assert(check2.hasPermissions)
		assert(check2.holdsPermissions)

		client.invokeCooldown(user2, null, holder2)

		check2 = client.checkPermissions(holder2.permission, user2)

		assert(check2.onCooldown)
		assert(!check2.hasPermissions)
		assert(check2.holdsPermissions)

		Thread.sleep(2000)

		check2 = client.checkPermissions(holder2.permission, user2)

		assert(!check2.onCooldown)
		assert(check2.hasPermissions)
		assert(check2.holdsPermissions)

	}

	@Test
	fun limits() {

		var check3 = client.checkPermissions(holder3.permission, user3)

		assert(!check3.onCooldown)
		assert(check3.hasPermissions)
		assert(check3.holdsPermissions)

		repeat(100) {
			assert(client.getInvocations(user3, null, holder3) == it)
			client.invokeCooldown(user3, null, holder3)
		}

		client = PermissionClientImpl(provider)

		repeat(3) {
			check3 = client.checkPermissions(holder3.permission, user3)

			assert(!check3.onCooldown)
			assert(check3.hasPermissions)
			assert(check3.holdsPermissions)
			assert(check3.limit == 3)
			assert(check3.limitLeft == 3 - it)

			client.invokeCooldown(user3, null, holder3)
		}

		check3 = client.checkPermissions(holder3.permission, user3)

		assert(check3.onCooldown)
		assert(!check3.hasPermissions)
		assert(check3.holdsPermissions)
		assert(check3.limitLeft == 0)

		assert(client.getInvocations(user3, null, holder3) == 3)

	}

	@Test
	fun tree() {
		val tree = PermissionTree()
		tree.add(Permissions.parse("command.kick.user;300"))
		tree.add(Permissions.parse("command.kick.*"))
		tree.add(Permissions.parse("command.ban"))
		tree.add(Permissions.parse("command.join;300"))
		tree.add(Permissions.parse("command.leave;300;3"))
		tree.add(Permissions.parse("testing.testing.testing;300"))
		tree.add(Permissions.parse("testing.testing.*;250"))
		tree.add(Permissions.parse("testing.*;200"))

		assert(tree.get(Permission("command.kick.user")) != null)
		assert(tree.get(Permission("command.join")) != null)
		assert(tree.get(Permission("command.leave")) != null)
		assert(tree.get(Permission("command.disconnect")) == null)

		assert(tree.get(Permission("command.kick.owner")) != null)
		assert(tree.get(Permission("command.kick.owner"))!!.permission == Permission("command.kick.*"))
		assert(tree.get(Permission("command.kick.owner.someone"))!!.permission == Permission("command.kick.*"))
		assert(tree.get(Permission("command.kick.user"))!!.cooldown == 0L)
		assert(tree.get(Permission("command.kick.owner"))!!.cooldown == 0L)
		assert(tree.get(Permission("testing.testing.testing"))!!.cooldown == 200L)
		assert(tree.get(Permission("testing.testing.disconnect"))!!.cooldown == 200L)
		assert(tree.get(Permission("testing.testing.*"))!!.cooldown == 200L)
		assert(tree.get(Permission("testing.er"))!!.cooldown == 200L)
		assert(tree.get(Permission("testing.testing.testing"))!!.permission == Permission("testing.*"))

		tree.add(Permissions.parse("*"))

		assert(tree.has(Permission("some")))
		assert(tree.has(Permission("some.permissions")))
		assert(tree.has(Permission("some.permissions.here")))

	}

	class TestProvider : PermissionProvider {

		val perms = mapOf(
			Pair("123", TEST_PERM_1),
			Pair("456", TEST_PERM_2),
			Pair("789", TEST_PERM_3)
		)

		override fun getPermissions(userId: String, guildId: String?, channelId: String?): Set<PermissionHolder> {
			return setOf(Permissions.parse(perms[userId]!!))
		}

		override fun isOverriding(userId: String): Boolean {
			return false
		}

		override fun startOverriding(userId: String) {
			TODO("Not yet implemented")
		}

		override fun stopOverriding(userId: String) {
			TODO("Not yet implemented")
		}

	}

	class FakeUser(
		val fakeId: String
	) : User {

		override fun equals(other: Any?): Boolean {
			val any = other ?: return false
			if (any !is FakeUser) return false
			return (this.id == any.id)
		}

		override fun getId(): String {
			return fakeId
		}

		override fun getIdLong(): Long {
			TODO("Not yet implemented")
		}

		override fun getAsMention(): String {
			TODO("Not yet implemented")
		}

		override fun getName(): String {
			TODO("Not yet implemented")
		}

		override fun getDiscriminator(): String {
			TODO("Not yet implemented")
		}

		override fun getAvatarId(): String? {
			TODO("Not yet implemented")
		}

		override fun getDefaultAvatarId(): String {
			TODO("Not yet implemented")
		}

		override fun getAsTag(): String {
			TODO("Not yet implemented")
		}

		override fun hasPrivateChannel(): Boolean {
			TODO("Not yet implemented")
		}

		override fun openPrivateChannel(): RestAction<PrivateChannel> {
			TODO("Not yet implemented")
		}

		override fun getMutualGuilds(): MutableList<Guild> {
			TODO("Not yet implemented")
		}

		override fun isBot(): Boolean {
			TODO("Not yet implemented")
		}

		override fun isSystem(): Boolean {
			TODO("Not yet implemented")
		}

		override fun getJDA(): JDA {
			TODO("Not yet implemented")
		}

		override fun getFlags(): EnumSet<User.UserFlag> {
			TODO("Not yet implemented")
		}

		override fun getFlagsRaw(): Int {
			TODO("Not yet implemented")
		}

	}

}