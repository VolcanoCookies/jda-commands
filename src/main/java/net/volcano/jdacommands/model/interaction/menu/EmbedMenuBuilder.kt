package net.volcano.jdacommands.model.interaction.menu

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.MessageEmbed.*
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.internal.utils.Checks
import net.dv8tion.jda.internal.utils.Helpers
import net.volcano.jdautils.constants.EmbedLimit
import net.volcano.jdautils.utils.splitAt
import java.awt.Color
import java.time.*
import java.time.temporal.TemporalAccessor
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

abstract class EmbedMenuBuilder {

	protected val fields: MutableList<Field> = LinkedList()

	protected var description: String? = null
	protected var color = Role.DEFAULT_COLOR_RAW
	protected var url: String? = null
	protected var title: String? = null
	protected var timestamp: OffsetDateTime? = null
	protected var thumbnail: Thumbnail? = null
	protected var author: AuthorInfo? = null
	protected var footer: Footer? = null
	protected var image: ImageInfo? = null

	lateinit var userId: String
	var expiration: Long = 60 * 30L
	var download: ByteArray? = null

	var extraButtons: MutableList<Button> = ArrayList()

	var asReply: Boolean = false
	var ephemeral: Boolean = false

	constructor() {}
	constructor(builder: EmbedBuilder?) : this(builder?.build()) {}

	/**
	 * Creates an EmbedBuilder using fields in an existing embed.
	 *
	 * @param embed the existing embed
	 */
	constructor(embed: MessageEmbed?) {
		if (embed != null) {
			setDescription(embed.description)
			url = embed.url
			title = embed.title
			timestamp = embed.timestamp
			color = embed.colorRaw
			thumbnail = embed.thumbnail
			author = embed.author
			footer = embed.footer
			image = embed.image
			if (embed.fields != null) {
				fields.addAll(embed.fields)
			}
		}
	}

	protected abstract fun buildEmbed(baseEmbed: MessageEmbed): EmbedMenu

	/**
	 * Returns a [MessageEmbed]
	 * that has been checked as being valid for sending.
	 *
	 * @return the built, sendable [MessageEmbed]
	 * @throws IllegalStateException If the embed is empty. Can be checked with [.isEmpty].
	 */
	fun build(): EmbedMenu {
		if (isEmpty) {
			throw IllegalStateException("Cannot build an empty embed!")
		}
		description = if (description?.isBlank() == true) null else description

		return buildEmbed(
			MessageEmbed(
				url, title, description, EmbedType.RICH, timestamp,
				color, thumbnail, null, author, null, footer, image, LinkedList(fields)
			)
		)
	}

	fun setDownload(content: CharSequence): EmbedMenuBuilder {
		download = content.toString().toByteArray()
		return this
	}

	fun addButton(button: Button): EmbedMenuBuilder {
		extraButtons.add(button)
		return this
	}

	/**
	 * Resets this builder to default state.
	 * <br></br>All parts will be either empty or null after this method has returned.
	 *
	 * @return The current EmbedBuilder with default values
	 */
	fun clear(): EmbedMenuBuilder {
		description = null
		fields.clear()
		url = null
		title = null
		timestamp = null
		color = Role.DEFAULT_COLOR_RAW
		thumbnail = null
		author = null
		footer = null
		image = null
		return this
	}

	/**
	 * Checks if the given embed is empty. Empty embeds will throw an exception if built
	 *
	 * @return true if the embed is empty and cannot be built
	 */
	val isEmpty: Boolean
		get() = (title == null
				) && (timestamp == null
				) && (thumbnail == null
				) && (author == null
				) && (footer == null
				) && (image == null
				) && (color == Role.DEFAULT_COLOR_RAW
				) && (description?.length == 0
				) && fields.isEmpty()

	/**
	 * The overall length of the current EmbedBuilder in displayed characters.
	 * <br></br>Represents the [MessageEmbed.getLength()][MessageEmbed.getLength] value.
	 *
	 * @return length of the current builder state
	 */
	fun length(): Int {
		var length = description?.length ?: 0

		synchronized(fields) {
			length = fields.sumOf { (it.name?.length ?: 0) + (it.value?.length ?: 0) }
		}

		length += title?.length ?: 0
		length += author?.name?.length ?: 0
		length += footer?.text?.length ?: 0

		return length
	}

	/**
	 * Checks whether the constructed [MessageEmbed]
	 * is within the limits for a bot account.
	 *
	 * @return True, if the [length][.length] is less or equal to the specific limit
	 * @see MessageEmbed.EMBED_MAX_LENGTH_BOT
	 */
	val isValidLength: Boolean
		get() {
			val length = length()
			return length <= EMBED_MAX_LENGTH_BOT
		}

	/**
	 * Sets the Title of the embed.
	 * <br></br>Overload for [.setTitle] without URL parameter.
	 *
	 *
	 * **[Example](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/04-setTitle.png)**
	 *
	 * @param title the title of the embed
	 * @return the builder after the title has been set
	 * @throws IllegalArgumentException
	 *  * If the provided `title` is an empty String.
	 *  * If the length of `title` is greater than [MessageEmbed.TITLE_MAX_LENGTH].
	 *
	 */
	fun setTitle(title: String?): EmbedMenuBuilder {
		return setTitle(title, null)
	}

	/**
	 * Sets the Title of the embed.
	 * <br></br>You can provide `null` as url if no url should be used.
	 *
	 *
	 * **[Example](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/04-setTitle.png)**
	 *
	 * @param title the title of the embed
	 * @param url   Makes the title into a hyperlink pointed at this url.
	 * @return the builder after the title has been set
	 * @throws IllegalArgumentException
	 *  * If the provided `title` is an empty String.
	 *  * If the length of `title` is greater than [MessageEmbed.TITLE_MAX_LENGTH].
	 *  * If the length of `url` is longer than [MessageEmbed.URL_MAX_LENGTH].
	 *  * If the provided `url` is not a properly formatted http or https url.
	 *
	 */
	fun setTitle(title: String?, url: String?): EmbedMenuBuilder {
		var url = url
		if (title == null) {
			this.title = null
			this.url = null
		} else {
			Checks.notEmpty(title, "Title")
			Checks.check(
				title.length <= TITLE_MAX_LENGTH,
				"Title cannot be longer than %d characters.",
				TITLE_MAX_LENGTH
			)
			if (Helpers.isBlank(url)) {
				url = null
			}
			urlCheck(url)
			this.title = title
			this.url = url
		}
		return this
	}

	/**
	 * Sets the Description of the embed. This is where the main chunk of text for an embed is typically placed.
	 *
	 *
	 * **[Example](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/05-setDescription.png)**
	 *
	 * @param description the description of the embed, `null` to reset
	 * @return the builder after the description has been set
	 * @throws IllegalArgumentException If the length of `description` is greater than [MessageEmbed.TEXT_MAX_LENGTH]
	 */
	fun setDescription(description: String?): EmbedMenuBuilder {
		this.description = description
		return this
	}

	/**
	 * Sets the Timestamp of the embed.
	 *
	 *
	 * **[Example](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/13-setTimestamp.png)**
	 *
	 *
	 * **Hint:** You can get the current time using [Instant.now()][Instant.now] or convert time from a
	 * millisecond representation by using [Instant.ofEpochMilli(long)][Instant.ofEpochMilli];
	 *
	 * @param temporal the temporal accessor of the timestamp
	 * @return the builder after the timestamp has been set
	 */
	fun setTimestamp(temporal: TemporalAccessor?): EmbedMenuBuilder {
		if (temporal == null) {
			timestamp = null
		} else if (temporal is OffsetDateTime) {
			timestamp = temporal
		} else {
			var offset: ZoneOffset?
			try {
				offset = ZoneOffset.from(temporal)
			} catch (ignore: DateTimeException) {
				offset = ZoneOffset.UTC
			}
			try {
				val ldt = LocalDateTime.from(temporal)
				timestamp = OffsetDateTime.of(ldt, offset)
			} catch (ignore: DateTimeException) {
				try {
					val instant = Instant.from(temporal)
					timestamp = OffsetDateTime.ofInstant(instant, offset)
				} catch (ex: DateTimeException) {
					throw DateTimeException(
						"Unable to obtain OffsetDateTime from TemporalAccessor: " +
								temporal + " of type " + temporal.javaClass.name, ex
					)
				}
			}
		}
		return this
	}

	/**
	 * Sets the Timestamp of the embed to now.
	 *
	 * @return the builder after the timestamp has been set
	 */
	fun setTimestampToNow(): EmbedMenuBuilder {
		return setTimestamp(Instant.now())
	}

	/**
	 * Sets the Color of the embed.
	 *
	 * [Example](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/02-setColor.png)
	 *
	 * @param color The [Color] of the embed
	 * or `null` to use no color
	 * @return the builder after the color has been set
	 * @see .setColor
	 */
	fun setColor(color: Color?): EmbedMenuBuilder {
		this.color = color?.rgb ?: Role.DEFAULT_COLOR_RAW
		return this
	}

	/**
	 * Sets the raw RGB color value for the embed.
	 *
	 * [Example](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/02-setColor.png)
	 *
	 * @param color The raw rgb value, or [Role.DEFAULT_COLOR_RAW] to use no color
	 * @return the builder after the color has been set
	 * @see .setColor
	 */
	fun setColor(color: Int): EmbedMenuBuilder {
		this.color = color
		return this
	}

	/**
	 * Sets the Thumbnail of the embed.
	 *
	 *
	 * **[Example](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/06-setThumbnail.png)**
	 *
	 *
	 * **Uploading images with Embeds**
	 * <br></br>When uploading an <u>image</u>
	 * (using [MessageChannel.sendFile(...)][net.dv8tion.jda.api.entities.MessageChannel.sendFile])
	 * you can reference said image using the specified filename as URI `attachment://filename.ext`.
	 *
	 *
	 * <u>Example</u>
	 * <pre>`
	 * MessageChannel channel; // = reference of a MessageChannel
	 * EmbedBuilder embed = new EmbedBuilder();
	 * InputStream file = new URL("https://http.cat/500").openStream();
	 * embed.setThumbnail("attachment://cat.png") // we specify this in sendFile as "cat.png"
	 * .setDescription("This is a cute cat :3");
	 * channel.sendFile(file, "cat.png").embed(embed.build()).queue();
	`</pre> *
	 *
	 * @param url the url of the thumbnail of the embed
	 * @return the builder after the thumbnail has been set
	 * @throws IllegalArgumentException
	 *  * If the length of `url` is longer than [MessageEmbed.URL_MAX_LENGTH].
	 *  * If the provided `url` is not a properly formatted http or https url.
	 *
	 */
	fun setThumbnail(url: String?): EmbedMenuBuilder {
		if (url == null) {
			thumbnail = null
		} else {
			urlCheck(url)
			thumbnail = Thumbnail(url, null, 0, 0)
		}
		return this
	}

	/**
	 * Sets the Image of the embed.
	 *
	 *
	 * **[Example](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/11-setImage.png)**
	 *
	 *
	 * **Uploading images with Embeds**
	 * <br></br>When uploading an <u>image</u>
	 * (using [MessageChannel.sendFile(...)][net.dv8tion.jda.api.entities.MessageChannel.sendFile])
	 * you can reference said image using the specified filename as URI `attachment://filename.ext`.
	 *
	 *
	 * <u>Example</u>
	 * <pre>`
	 * MessageChannel channel; // = reference of a MessageChannel
	 * EmbedBuilder embed = new EmbedBuilder();
	 * InputStream file = new URL("https://http.cat/500").openStream();
	 * embed.setImage("attachment://cat.png") // we specify this in sendFile as "cat.png"
	 * .setDescription("This is a cute cat :3");
	 * channel.sendFile(file, "cat.png").embed(embed.build()).queue();
	`</pre> *
	 *
	 * @param url the url of the image of the embed
	 * @return the builder after the image has been set
	 * @throws IllegalArgumentException
	 *  * If the length of `url` is longer than [MessageEmbed.URL_MAX_LENGTH].
	 *  * If the provided `url` is not a properly formatted http or https url.
	 *
	 * @see net.dv8tion.jda.api.entities.MessageChannel.sendFile
	 */
	fun setImage(url: String?): EmbedMenuBuilder {
		if (url == null) {
			image = null
		} else {
			urlCheck(url)
			image = ImageInfo(url, null, 0, 0)
		}
		return this
	}

	/**
	 * Sets the Author of the embed. The author appears in the top left of the embed and can have a small
	 * image beside it along with the author's name being made clickable by way of providing a url.
	 * This convenience method just sets the name.
	 *
	 *
	 * **[Example](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/03-setAuthor.png)**
	 *
	 * @param name the name of the author of the embed. If this is not set, the author will not appear in the embed
	 * @return the builder after the author has been set
	 */

	fun setAuthor(name: String?): EmbedMenuBuilder {
		return setAuthor(name, null, null)
	}

	/**
	 * Sets the Author of the embed. The author appears in the top left of the embed and can have a small
	 * image beside it along with the author's name being made clickable by way of providing a url.
	 * This convenience method just sets the name and the url.
	 *
	 *
	 * **[Example](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/03-setAuthor.png)**
	 *
	 * @param name the name of the author of the embed. If this is not set, the author will not appear in the embed
	 * @param url  the url of the author of the embed
	 * @return the builder after the author has been set
	 * @throws IllegalArgumentException
	 *  * If the length of `url` is longer than [MessageEmbed.URL_MAX_LENGTH].
	 *  * If the provided `url` is not a properly formatted http or https url.
	 *
	 */
	fun setAuthor(name: String?, url: String?): EmbedMenuBuilder {
		return setAuthor(name, url, null)
	}

	/**
	 * Sets the Author of the embed. The author appears in the top left of the embed and can have a small
	 * image beside it along with the author's name being made clickable by way of providing a url.
	 *
	 *
	 * **[Example](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/03-setAuthor.png)**
	 *
	 *
	 * **Uploading images with Embeds**
	 * <br></br>When uploading an <u>image</u>
	 * (using [MessageChannel.sendFile(...)][net.dv8tion.jda.api.entities.MessageChannel.sendFile])
	 * you can reference said image using the specified filename as URI `attachment://filename.ext`.
	 *
	 *
	 * <u>Example</u>
	 * <pre>`
	 * MessageChannel channel; // = reference of a MessageChannel
	 * EmbedBuilder embed = new EmbedBuilder();
	 * InputStream file = new URL("https://http.cat/500").openStream();
	 * embed.setAuthor("Minn", null, "attachment://cat.png") // we specify this in sendFile as "cat.png"
	 * .setDescription("This is a cute cat :3");
	 * channel.sendFile(file, "cat.png").embed(embed.build()).queue();
	`</pre> *
	 *
	 * @param name    the name of the author of the embed. If this is not set, the author will not appear in the embed
	 * @param url     the url of the author of the embed
	 * @param iconUrl the url of the icon for the author
	 * @return the builder after the author has been set
	 * @throws IllegalArgumentException
	 *  * If the length of `url` is longer than [MessageEmbed.URL_MAX_LENGTH].
	 *  * If the provided `url` is not a properly formatted http or https url.
	 *  * If the length of `iconUrl` is longer than [MessageEmbed.URL_MAX_LENGTH].
	 *  * If the provided `iconUrl` is not a properly formatted http or https url.
	 *
	 */
	fun setAuthor(name: String?, url: String?, iconUrl: String?): EmbedMenuBuilder {
		//We only check if the name is null because its presence is what determines if the
		// the author will appear in the embed.
		if (name == null) {
			author = null
		} else {
			urlCheck(url)
			urlCheck(iconUrl)
			author = AuthorInfo(name, url, iconUrl, null)
		}
		return this
	}

	/**
	 * Sets the Footer of the embed without icon.
	 *
	 *
	 * **[Example](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/12-setFooter.png)**
	 *
	 * @param text the text of the footer of the embed. If this is not set or set to null, the footer will not appear in the embed.
	 * @return the builder after the footer has been set
	 * @throws IllegalArgumentException If the length of `text` is longer than [MessageEmbed.TEXT_MAX_LENGTH].
	 */
	fun setFooter(text: String?): EmbedMenuBuilder {
		return setFooter(text, null)
	}

	/**
	 * Sets the Footer of the embed.
	 *
	 *
	 * **[Example](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/12-setFooter.png)**
	 *
	 *
	 * **Uploading images with Embeds**
	 * <br></br>When uploading an <u>image</u>
	 * (using [MessageChannel.sendFile(...)][net.dv8tion.jda.api.entities.MessageChannel.sendFile])
	 * you can reference said image using the specified filename as URI `attachment://filename.ext`.
	 *
	 *
	 * <u>Example</u>
	 * <pre>`
	 * MessageChannel channel; // = reference of a MessageChannel
	 * EmbedBuilder embed = new EmbedBuilder();
	 * InputStream file = new URL("https://http.cat/500").openStream();
	 * embed.setFooter("Cool footer!", "attachment://cat.png") // we specify this in sendFile as "cat.png"
	 * .setDescription("This is a cute cat :3");
	 * channel.sendFile(file, "cat.png").embed(embed.build()).queue();
	`</pre> *
	 *
	 * @param text    the text of the footer of the embed. If this is not set, the footer will not appear in the embed.
	 * @param iconUrl the url of the icon for the footer
	 * @return the builder after the footer has been set
	 * @throws IllegalArgumentException
	 *  * If the length of `text` is longer than [MessageEmbed.TEXT_MAX_LENGTH].
	 *  * If the length of `iconUrl` is longer than [MessageEmbed.URL_MAX_LENGTH].
	 *  * If the provided `iconUrl` is not a properly formatted http or https url.
	 *
	 */
	fun setFooter(text: String?, iconUrl: String?): EmbedMenuBuilder {
		//We only check if the text is null because its presence is what determines if the
		// footer will appear in the embed.
		if (text == null) {
			footer = null
		} else {
			Checks.check(
				text.length <= TEXT_MAX_LENGTH,
				"Text cannot be longer than %d characters.",
				TEXT_MAX_LENGTH
			)
			urlCheck(iconUrl)
			footer = Footer(text, iconUrl, null)
		}
		return this
	}

	/**
	 * Copies the provided Field into a new Field for this builder.
	 * <br></br>For additional documentation, see [.addField]
	 *
	 * @param field the field object to add
	 * @return the builder after the field has been added
	 */
	open fun addField(field: Field?): EmbedMenuBuilder {
		return if (field == null) this else addField(field.name, field.value, field.isInline)
	}

	/**
	 * Adds a Field to the embed.
	 *
	 *
	 * Note: If a blank string is provided to either `name` or `value`, the blank string is replaced
	 * with [EmbedMenuBuilder.ZERO_WIDTH_SPACE].
	 *
	 *
	 * **[Example of Inline](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/07-addField.png)**
	 *
	 * **[Example if Non-inline](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/08-addField.png)**
	 *
	 * @param name   the name of the Field, displayed in bold above the `value`.
	 * @param value  the contents of the field.
	 * @param inline whether or not this field should display inline.
	 * @return the builder after the field has been added
	 * @throws IllegalArgumentException
	 *  * If only `name` or `value` is set. Both must be set.
	 *  * If the length of `name` is greater than [MessageEmbed.TITLE_MAX_LENGTH].
	 *  * If the length of `value` is greater than [MessageEmbed.VALUE_MAX_LENGTH].
	 *
	 */
	open fun addField(name: String?, value: String?, inline: Boolean): EmbedMenuBuilder {
		if (name == null && value == null) {
			return this
		}
		if (value != null && value.length > EmbedLimit.EMBED_FIELD_VALUE_LIMIT) {
			val split = value.splitAt(EmbedLimit.EMBED_FIELD_VALUE_LIMIT, "\n")
			fields.add(Field(name, split[0], inline))
			for (i in 1 until split.size) {
				fields.add(Field("\\a", split[i], inline))
			}
		} else {
			fields.add(Field(name, value, inline))
		}
		return this
	}

	/**
	 * Adds a Field to the embed.
	 * Inline = false
	 *
	 *
	 * Note: If a blank string is provided to either `name` or `value`, the blank string is replaced
	 * with [EmbedMenuBuilder.ZERO_WIDTH_SPACE].
	 *
	 *
	 * **[Example of Inline](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/07-addField.png)**
	 *
	 * **[Example if Non-inline](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/08-addField.png)**
	 *
	 * @param name  the name of the Field, displayed in bold above the `value`.
	 * @param value the contents of the field.
	 * @return the builder after the field has been added
	 * @throws IllegalArgumentException
	 *  * If only `name` or `value` is set. Both must be set.
	 *  * If the length of `name` is greater than [MessageEmbed.TITLE_MAX_LENGTH].
	 *  * If the length of `value` is greater than [MessageEmbed.VALUE_MAX_LENGTH].
	 *
	 */
	open fun addField(name: String?, value: String?): EmbedMenuBuilder {
		if (name == null && value == null) {
			return this
		}
		if (value != null && value.length > EmbedLimit.EMBED_FIELD_VALUE_LIMIT) {
			val split = value.splitAt(EmbedLimit.EMBED_FIELD_VALUE_LIMIT, "\n")
			fields.add(Field(name, split[0], false))
			for (i in 1 until split.size) {
				fields.add(Field("\\a", split[i], false))
			}
		} else {
			fields.add(Field(name, value, false))
		}
		return this
	}

	/**
	 * Adds a inline Field to the embed.
	 *
	 *
	 * Note: If a blank string is provided to either `name` or `value`, the blank string is replaced
	 * with [EmbedMenuBuilder.ZERO_WIDTH_SPACE].
	 *
	 *
	 * **[Example of Inline](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/07-addField.png)**
	 *
	 * **[Example if Non-inline](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/08-addField.png)**
	 *
	 * @param name  the name of the Field, displayed in bold above the `value`.
	 * @param value the contents of the field.
	 * @return the builder after the field has been added
	 * @throws IllegalArgumentException
	 *  * If only `name` or `value` is set. Both must be set.
	 *  * If the length of `name` is greater than [MessageEmbed.TITLE_MAX_LENGTH].
	 *  * If the length of `value` is greater than [MessageEmbed.VALUE_MAX_LENGTH].
	 *
	 */
	open fun addInlineField(name: String?, value: String?): EmbedMenuBuilder {
		if (name == null && value == null) {
			return this
		}
		fields.add(Field(name, value, true))
		return this
	}

	/**
	 * Adds a blank (empty) Field to the embed.
	 *
	 *
	 * **[Example of Inline](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/07-addField.png)**
	 *
	 * **[Example if Non-inline](https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/08-addField.png)**
	 *
	 * @param inline whether or not this field should display inline
	 * @return the builder after the field has been added
	 */
	open fun addBlankField(inline: Boolean): EmbedMenuBuilder {
		fields.add(Field(ZERO_WIDTH_SPACE, ZERO_WIDTH_SPACE, inline))
		return this
	}

	private fun urlCheck(url: String?) {
		if (url != null) {
			Checks.check(url.length <= URL_MAX_LENGTH, "URL cannot be longer than %d characters.", URL_MAX_LENGTH)
			Checks.check(URL_PATTERN.matcher(url).matches(), "URL must be a valid http(s) or attachment url.")
		}
	}

	/**
	 * For how long the pager should stay active
	 *
	 * @param expiration the time from now to the expiration
	 * @param unit       the time unit of the expiration parameter
	 */
	fun setExpiration(expiration: Long, unit: TimeUnit) {
		this.expiration = unit.toMillis(expiration)
	}

	companion object {

		const val ZERO_WIDTH_SPACE = "\u200E"
		val URL_PATTERN = Pattern.compile("\\s*(https?|attachment)://\\S+\\s*", Pattern.CASE_INSENSITIVE)
	}

}