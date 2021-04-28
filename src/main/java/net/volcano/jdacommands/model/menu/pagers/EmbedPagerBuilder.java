package net.volcano.jdacommands.model.menu.pagers;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ForRemoval;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import net.volcano.jdautils.constants.EmbedLimit;
import net.volcano.jdautils.utils.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Getter
@Setter
public abstract class EmbedPagerBuilder {
	
	public final static String ZERO_WIDTH_SPACE = "\u200E";
	public final static Pattern URL_PATTERN = Pattern.compile("\\s*(https?|attachment)://\\S+\\s*", Pattern.CASE_INSENSITIVE);
	
	private final List<MessageEmbed.Field> fields = new LinkedList<>();
	private final StringBuilder description = new StringBuilder();
	private int color = Role.DEFAULT_COLOR_RAW;
	private String url, title;
	private OffsetDateTime timestamp;
	private MessageEmbed.Thumbnail thumbnail;
	private MessageEmbed.AuthorInfo author;
	private MessageEmbed.Footer footer;
	private MessageEmbed.ImageInfo image;
	
	private long expiration;
	
	private byte[] download;
	
	public EmbedPagerBuilder() {
	}
	
	public EmbedPagerBuilder(@Nullable net.dv8tion.jda.api.EmbedBuilder builder) {
		this(builder != null ? builder.build() : null);
	}
	
	/**
	 * Creates an EmbedBuilder using fields in an existing embed.
	 *
	 * @param embed the existing embed
	 */
	public EmbedPagerBuilder(@Nullable MessageEmbed embed) {
		if (embed != null) {
			setDescription(embed.getDescription());
			url = embed.getUrl();
			title = embed.getTitle();
			timestamp = embed.getTimestamp();
			color = embed.getColorRaw();
			thumbnail = embed.getThumbnail();
			author = embed.getAuthor();
			footer = embed.getFooter();
			image = embed.getImage();
			if (embed.getFields() != null) {
				fields.addAll(embed.getFields());
			}
		}
	}
	
	protected abstract EmbedPager buildEmbed(MessageEmbed baseEmbed);
	
	/**
	 * Returns a {@link MessageEmbed MessageEmbed}
	 * that has been checked as being valid for sending.
	 *
	 * @return the built, sendable {@link MessageEmbed}
	 * @throws IllegalStateException If the embed is empty. Can be checked with {@link #isEmpty()}.
	 */
	@Nonnull
	public EmbedPager build() {
		
		if (isEmpty()) {
			throw new IllegalStateException("Cannot build an empty embed!");
		}
		
		final String description = this.description.length() < 1 ? null : this.description.toString();
		
		return buildEmbed(new MessageEmbed(url, title, description, EmbedType.RICH, timestamp,
				color, thumbnail, null, author, null, footer, image, new LinkedList<>(fields)));
	}
	
	public EmbedPagerBuilder setDownload(CharSequence content) {
		download = content.toString().getBytes();
		return this;
	}
	
	/**
	 * Resets this builder to default state.
	 * <br>All parts will be either empty or null after this method has returned.
	 *
	 * @return The current EmbedBuilder with default values
	 */
	@Nonnull
	public EmbedPagerBuilder clear() {
		description.setLength(0);
		fields.clear();
		url = null;
		title = null;
		timestamp = null;
		color = Role.DEFAULT_COLOR_RAW;
		thumbnail = null;
		author = null;
		footer = null;
		image = null;
		return this;
	}
	
	/**
	 * Checks if the given embed is empty. Empty embeds will throw an exception if built
	 *
	 * @return true if the embed is empty and cannot be built
	 */
	public boolean isEmpty() {
		return title == null
				&& timestamp == null
				&& thumbnail == null
				&& author == null
				&& footer == null
				&& image == null
				&& color == Role.DEFAULT_COLOR_RAW
				&& description.length() == 0
				&& fields.isEmpty();
	}
	
	/**
	 * The overall length of the current EmbedBuilder in displayed characters.
	 * <br>Represents the {@link MessageEmbed#getLength() MessageEmbed.getLength()} value.
	 *
	 * @return length of the current builder state
	 */
	public int length() {
		int length = description.length();
		synchronized (fields) {
			length = fields.stream().map(f -> Objects.requireNonNull(f.getName()).length() +
					Objects.requireNonNull(f.getValue()).length()).reduce(length, Integer::sum);
		}
		if (title != null) {
			length += title.length();
		}
		if (author != null) {
			length += Objects.requireNonNull(author.getName()).length();
		}
		if (footer != null) {
			length += Objects.requireNonNull(footer.getText()).length();
		}
		return length;
	}
	
	/**
	 * Checks whether the constructed {@link MessageEmbed MessageEmbed}
	 * is within the limits for a bot account.
	 *
	 * @return True, if the {@link #length() length} is less or equal to the specific limit
	 * @see MessageEmbed#EMBED_MAX_LENGTH_BOT
	 */
	public boolean isValidLength() {
		final int length = length();
		return length <= MessageEmbed.EMBED_MAX_LENGTH_BOT;
	}
	
	/**
	 * Checks whether the constructed {@link MessageEmbed MessageEmbed}
	 * is within the limits for the specified {@link AccountType AccountType}
	 * <ul>
	 *     <li>Bot: {@value MessageEmbed#EMBED_MAX_LENGTH_BOT}</li>
	 *     <li>Client: {@value MessageEmbed#EMBED_MAX_LENGTH_CLIENT}</li>
	 * </ul>
	 *
	 * @param type The {@link AccountType AccountType} to validate
	 * @return True, if the {@link #length() length} is less or equal to the specific limit
	 * @throws IllegalArgumentException If provided with {@code null}
	 * @deprecated Replace with {@link #isValidLength()}
	 */
	@Deprecated
	@ForRemoval
	@DeprecatedSince("4.2.0")
	public boolean isValidLength(@Nonnull AccountType type) {
		Checks.notNull(type, "AccountType");
		final int length = length();
		switch (type) {
			case BOT:
				return length <= MessageEmbed.EMBED_MAX_LENGTH_BOT;
			case CLIENT:
			default:
				return length <= MessageEmbed.EMBED_MAX_LENGTH_CLIENT;
		}
	}
	
	/**
	 * Sets the Title of the embed.
	 * <br>Overload for {@link #setTitle(String, String)} without URL parameter.
	 *
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/04-setTitle.png">Example</a></b>
	 *
	 * @param title the title of the embed
	 * @return the builder after the title has been set
	 * @throws IllegalArgumentException <ul>
	 *                                  <li>If the provided {@code title} is an empty String.</li>
	 *                                  <li>If the length of {@code title} is greater than {@link MessageEmbed#TITLE_MAX_LENGTH}.</li>
	 *                                  </ul>
	 */
	@Nonnull
	public EmbedPagerBuilder setTitle(@Nullable String title) {
		return setTitle(title, null);
	}
	
	/**
	 * Sets the Title of the embed.
	 * <br>You can provide {@code null} as url if no url should be used.
	 *
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/04-setTitle.png">Example</a></b>
	 *
	 * @param title the title of the embed
	 * @param url   Makes the title into a hyperlink pointed at this url.
	 * @return the builder after the title has been set
	 * @throws IllegalArgumentException <ul>
	 *                                  <li>If the provided {@code title} is an empty String.</li>
	 *                                  <li>If the length of {@code title} is greater than {@link MessageEmbed#TITLE_MAX_LENGTH}.</li>
	 *                                  <li>If the length of {@code url} is longer than {@link MessageEmbed#URL_MAX_LENGTH}.</li>
	 *                                  <li>If the provided {@code url} is not a properly formatted http or https url.</li>
	 *                                  </ul>
	 */
	@Nonnull
	public EmbedPagerBuilder setTitle(@Nullable String title, @Nullable String url) {
		if (title == null) {
			this.title = null;
			this.url = null;
		} else {
			Checks.notEmpty(title, "Title");
			Checks.check(title.length() <= MessageEmbed.TITLE_MAX_LENGTH, "Title cannot be longer than %d characters.", MessageEmbed.TITLE_MAX_LENGTH);
			if (Helpers.isBlank(url)) {
				url = null;
			}
			urlCheck(url);
			
			this.title = title;
			this.url = url;
		}
		return this;
	}
	
	/**
	 * The {@link StringBuilder StringBuilder} used to
	 * build the description for the embed.
	 * <br>Note: To reset the description use {@link #setDescription(CharSequence) setDescription(null)}
	 *
	 * @return StringBuilder with current description context
	 */
	@Nonnull
	public StringBuilder getDescriptionBuilder() {
		return description;
	}
	
	/**
	 * Sets the Description of the embed. This is where the main chunk of text for an embed is typically placed.
	 *
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/05-setDescription.png">Example</a></b>
	 *
	 * @param description the description of the embed, {@code null} to reset
	 * @return the builder after the description has been set
	 * @throws IllegalArgumentException If the length of {@code description} is greater than {@link MessageEmbed#TEXT_MAX_LENGTH}
	 */
	@Nonnull
	public final EmbedPagerBuilder setDescription(@Nullable CharSequence description) {
		this.description.setLength(0);
		if (description != null && description.length() >= 1) {
			appendDescription(description);
		}
		return this;
	}
	
	/**
	 * Appends to the description of the embed. This is where the main chunk of text for an embed is typically placed.
	 *
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/05-setDescription.png">Example</a></b>
	 *
	 * @param description the string to append to the description of the embed
	 * @return the builder after the description has been set
	 * @throws IllegalArgumentException <ul>
	 *                                  <li>If the provided {@code description} String is null</li>
	 *                                  <li>If the length of {@code description} is greater than {@link MessageEmbed#TEXT_MAX_LENGTH}.</li>
	 *                                  </ul>
	 */
	@Nonnull
	public EmbedPagerBuilder appendDescription(@Nonnull CharSequence description) {
		Checks.notNull(description, "description");
		Checks.check(this.description.length() + description.length() <= MessageEmbed.TEXT_MAX_LENGTH,
				"Description cannot be longer than %d characters.", MessageEmbed.TEXT_MAX_LENGTH);
		this.description.append(description);
		return this;
	}
	
	/**
	 * Sets the Timestamp of the embed.
	 *
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/13-setTimestamp.png">Example</a></b>
	 *
	 * <p><b>Hint:</b> You can get the current time using {@link Instant#now() Instant.now()} or convert time from a
	 * millisecond representation by using {@link Instant#ofEpochMilli(long) Instant.ofEpochMilli(long)};
	 *
	 * @param temporal the temporal accessor of the timestamp
	 * @return the builder after the timestamp has been set
	 */
	@Nonnull
	public EmbedPagerBuilder setTimestamp(@Nullable TemporalAccessor temporal) {
		if (temporal == null) {
			timestamp = null;
		} else if (temporal instanceof OffsetDateTime) {
			timestamp = (OffsetDateTime) temporal;
		} else {
			ZoneOffset offset;
			try {
				offset = ZoneOffset.from(temporal);
			} catch (DateTimeException ignore) {
				offset = ZoneOffset.UTC;
			}
			try {
				LocalDateTime ldt = LocalDateTime.from(temporal);
				timestamp = OffsetDateTime.of(ldt, offset);
			} catch (DateTimeException ignore) {
				try {
					Instant instant = Instant.from(temporal);
					timestamp = OffsetDateTime.ofInstant(instant, offset);
				} catch (DateTimeException ex) {
					throw new DateTimeException("Unable to obtain OffsetDateTime from TemporalAccessor: " +
							temporal + " of type " + temporal.getClass().getName(), ex);
				}
			}
		}
		return this;
	}
	
	/**
	 * Sets the Color of the embed.
	 *
	 * <a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/02-setColor.png" target="_blank">Example</a>
	 *
	 * @param color The {@link Color Color} of the embed
	 *              or {@code null} to use no color
	 * @return the builder after the color has been set
	 * @see #setColor(int)
	 */
	@Nonnull
	public EmbedPagerBuilder setColor(@Nullable Color color) {
		this.color = color == null ? Role.DEFAULT_COLOR_RAW : color.getRGB();
		return this;
	}
	
	/**
	 * Sets the raw RGB color value for the embed.
	 *
	 * <a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/02-setColor.png" target="_blank">Example</a>
	 *
	 * @param color The raw rgb value, or {@link Role#DEFAULT_COLOR_RAW} to use no color
	 * @return the builder after the color has been set
	 * @see #setColor(Color)
	 */
	@Nonnull
	public EmbedPagerBuilder setColor(int color) {
		this.color = color;
		return this;
	}
	
	/**
	 * Sets the Thumbnail of the embed.
	 *
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/06-setThumbnail.png">Example</a></b>
	 *
	 * <p><b>Uploading images with Embeds</b>
	 * <br>When uploading an <u>image</u>
	 * (using {@link net.dv8tion.jda.api.entities.MessageChannel#sendFile(java.io.File, net.dv8tion.jda.api.utils.AttachmentOption...) MessageChannel.sendFile(...)})
	 * you can reference said image using the specified filename as URI {@code attachment://filename.ext}.
	 *
	 * <p><u>Example</u>
	 * <pre><code>
	 * MessageChannel channel; // = reference of a MessageChannel
	 * EmbedBuilder embed = new EmbedBuilder();
	 * InputStream file = new URL("https://http.cat/500").openStream();
	 * embed.setThumbnail("attachment://cat.png") // we specify this in sendFile as "cat.png"
	 *      .setDescription("This is a cute cat :3");
	 * channel.sendFile(file, "cat.png").embed(embed.build()).queue();
	 * </code></pre>
	 *
	 * @param url the url of the thumbnail of the embed
	 * @return the builder after the thumbnail has been set
	 * @throws IllegalArgumentException <ul>
	 *                                  <li>If the length of {@code url} is longer than {@link MessageEmbed#URL_MAX_LENGTH}.</li>
	 *                                  <li>If the provided {@code url} is not a properly formatted http or https url.</li>
	 *                                  </ul>
	 */
	@Nonnull
	public EmbedPagerBuilder setThumbnail(@Nullable String url) {
		if (url == null) {
			thumbnail = null;
		} else {
			urlCheck(url);
			thumbnail = new MessageEmbed.Thumbnail(url, null, 0, 0);
		}
		return this;
	}
	
	/**
	 * Sets the Image of the embed.
	 *
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/11-setImage.png">Example</a></b>
	 *
	 * <p><b>Uploading images with Embeds</b>
	 * <br>When uploading an <u>image</u>
	 * (using {@link net.dv8tion.jda.api.entities.MessageChannel#sendFile(java.io.File, net.dv8tion.jda.api.utils.AttachmentOption...) MessageChannel.sendFile(...)})
	 * you can reference said image using the specified filename as URI {@code attachment://filename.ext}.
	 *
	 * <p><u>Example</u>
	 * <pre><code>
	 * MessageChannel channel; // = reference of a MessageChannel
	 * EmbedBuilder embed = new EmbedBuilder();
	 * InputStream file = new URL("https://http.cat/500").openStream();
	 * embed.setImage("attachment://cat.png") // we specify this in sendFile as "cat.png"
	 *      .setDescription("This is a cute cat :3");
	 * channel.sendFile(file, "cat.png").embed(embed.build()).queue();
	 * </code></pre>
	 *
	 * @param url the url of the image of the embed
	 * @return the builder after the image has been set
	 * @throws IllegalArgumentException <ul>
	 *                                  <li>If the length of {@code url} is longer than {@link MessageEmbed#URL_MAX_LENGTH}.</li>
	 *                                  <li>If the provided {@code url} is not a properly formatted http or https url.</li>
	 *                                  </ul>
	 * @see net.dv8tion.jda.api.entities.MessageChannel#sendFile(java.io.File, String, net.dv8tion.jda.api.utils.AttachmentOption...) MessageChannel.sendFile(...)
	 */
	@Nonnull
	public EmbedPagerBuilder setImage(@Nullable String url) {
		if (url == null) {
			image = null;
		} else {
			urlCheck(url);
			image = new MessageEmbed.ImageInfo(url, null, 0, 0);
		}
		return this;
	}
	
	/**
	 * Sets the Author of the embed. The author appears in the top left of the embed and can have a small
	 * image beside it along with the author's name being made clickable by way of providing a url.
	 * This convenience method just sets the name.
	 *
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/03-setAuthor.png">Example</a></b>
	 *
	 * @param name the name of the author of the embed. If this is not set, the author will not appear in the embed
	 * @return the builder after the author has been set
	 */
	@Nonnull
	public EmbedPagerBuilder setAuthor(@Nullable String name) {
		return setAuthor(name, null, null);
	}
	
	/**
	 * Sets the Author of the embed. The author appears in the top left of the embed and can have a small
	 * image beside it along with the author's name being made clickable by way of providing a url.
	 * This convenience method just sets the name and the url.
	 *
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/03-setAuthor.png">Example</a></b>
	 *
	 * @param name the name of the author of the embed. If this is not set, the author will not appear in the embed
	 * @param url  the url of the author of the embed
	 * @return the builder after the author has been set
	 * @throws IllegalArgumentException <ul>
	 *                                  <li>If the length of {@code url} is longer than {@link MessageEmbed#URL_MAX_LENGTH}.</li>
	 *                                  <li>If the provided {@code url} is not a properly formatted http or https url.</li>
	 *                                  </ul>
	 */
	@Nonnull
	public EmbedPagerBuilder setAuthor(@Nullable String name, @Nullable String url) {
		return setAuthor(name, url, null);
	}
	
	/**
	 * Sets the Author of the embed. The author appears in the top left of the embed and can have a small
	 * image beside it along with the author's name being made clickable by way of providing a url.
	 *
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/03-setAuthor.png">Example</a></b>
	 *
	 * <p><b>Uploading images with Embeds</b>
	 * <br>When uploading an <u>image</u>
	 * (using {@link net.dv8tion.jda.api.entities.MessageChannel#sendFile(java.io.File, net.dv8tion.jda.api.utils.AttachmentOption...) MessageChannel.sendFile(...)})
	 * you can reference said image using the specified filename as URI {@code attachment://filename.ext}.
	 *
	 * <p><u>Example</u>
	 * <pre><code>
	 * MessageChannel channel; // = reference of a MessageChannel
	 * EmbedBuilder embed = new EmbedBuilder();
	 * InputStream file = new URL("https://http.cat/500").openStream();
	 * embed.setAuthor("Minn", null, "attachment://cat.png") // we specify this in sendFile as "cat.png"
	 *      .setDescription("This is a cute cat :3");
	 * channel.sendFile(file, "cat.png").embed(embed.build()).queue();
	 * </code></pre>
	 *
	 * @param name    the name of the author of the embed. If this is not set, the author will not appear in the embed
	 * @param url     the url of the author of the embed
	 * @param iconUrl the url of the icon for the author
	 * @return the builder after the author has been set
	 * @throws IllegalArgumentException <ul>
	 *                                  <li>If the length of {@code url} is longer than {@link MessageEmbed#URL_MAX_LENGTH}.</li>
	 *                                  <li>If the provided {@code url} is not a properly formatted http or https url.</li>
	 *                                  <li>If the length of {@code iconUrl} is longer than {@link MessageEmbed#URL_MAX_LENGTH}.</li>
	 *                                  <li>If the provided {@code iconUrl} is not a properly formatted http or https url.</li>
	 *                                  </ul>
	 */
	@Nonnull
	public EmbedPagerBuilder setAuthor(@Nullable String name, @Nullable String url, @Nullable String iconUrl) {
		//We only check if the name is null because its presence is what determines if the
		// the author will appear in the embed.
		if (name == null) {
			author = null;
		} else {
			urlCheck(url);
			urlCheck(iconUrl);
			author = new MessageEmbed.AuthorInfo(name, url, iconUrl, null);
		}
		return this;
	}
	
	/**
	 * Sets the Footer of the embed without icon.
	 *
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/12-setFooter.png">Example</a></b>
	 *
	 * @param text the text of the footer of the embed. If this is not set or set to null, the footer will not appear in the embed.
	 * @return the builder after the footer has been set
	 * @throws IllegalArgumentException If the length of {@code text} is longer than {@link MessageEmbed#TEXT_MAX_LENGTH}.
	 */
	@Nonnull
	public EmbedPagerBuilder setFooter(@Nullable String text) {
		return setFooter(text, null);
	}
	
	/**
	 * Sets the Footer of the embed.
	 *
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/12-setFooter.png">Example</a></b>
	 *
	 * <p><b>Uploading images with Embeds</b>
	 * <br>When uploading an <u>image</u>
	 * (using {@link net.dv8tion.jda.api.entities.MessageChannel#sendFile(java.io.File, net.dv8tion.jda.api.utils.AttachmentOption...) MessageChannel.sendFile(...)})
	 * you can reference said image using the specified filename as URI {@code attachment://filename.ext}.
	 *
	 * <p><u>Example</u>
	 * <pre><code>
	 * MessageChannel channel; // = reference of a MessageChannel
	 * EmbedBuilder embed = new EmbedBuilder();
	 * InputStream file = new URL("https://http.cat/500").openStream();
	 * embed.setFooter("Cool footer!", "attachment://cat.png") // we specify this in sendFile as "cat.png"
	 *      .setDescription("This is a cute cat :3");
	 * channel.sendFile(file, "cat.png").embed(embed.build()).queue();
	 * </code></pre>
	 *
	 * @param text    the text of the footer of the embed. If this is not set, the footer will not appear in the embed.
	 * @param iconUrl the url of the icon for the footer
	 * @return the builder after the footer has been set
	 * @throws IllegalArgumentException <ul>
	 *                                  <li>If the length of {@code text} is longer than {@link MessageEmbed#TEXT_MAX_LENGTH}.</li>
	 *                                  <li>If the length of {@code iconUrl} is longer than {@link MessageEmbed#URL_MAX_LENGTH}.</li>
	 *                                  <li>If the provided {@code iconUrl} is not a properly formatted http or https url.</li>
	 *                                  </ul>
	 */
	@Nonnull
	public EmbedPagerBuilder setFooter(@Nullable String text, @Nullable String iconUrl) {
		//We only check if the text is null because its presence is what determines if the
		// footer will appear in the embed.
		if (text == null) {
			footer = null;
		} else {
			Checks.check(text.length() <= MessageEmbed.TEXT_MAX_LENGTH, "Text cannot be longer than %d characters.", MessageEmbed.TEXT_MAX_LENGTH);
			urlCheck(iconUrl);
			footer = new MessageEmbed.Footer(text, iconUrl, null);
		}
		return this;
	}
	
	/**
	 * Copies the provided Field into a new Field for this builder.
	 * <br>For additional documentation, see {@link #addField(String, String, boolean)}
	 *
	 * @param field the field object to add
	 * @return the builder after the field has been added
	 */
	@Nonnull
	public EmbedPagerBuilder addField(@Nullable MessageEmbed.Field field) {
		return field == null ? this : addField(field.getName(), field.getValue(), field.isInline());
	}
	
	/**
	 * Adds a Field to the embed.
	 *
	 * <p>Note: If a blank string is provided to either {@code name} or {@code value}, the blank string is replaced
	 * with {@link EmbedPagerBuilder#ZERO_WIDTH_SPACE}.
	 *
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/07-addField.png">Example of Inline</a></b>
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/08-addField.png">Example if Non-inline</a></b>
	 *
	 * @param name   the name of the Field, displayed in bold above the {@code value}.
	 * @param value  the contents of the field.
	 * @param inline whether or not this field should display inline.
	 * @return the builder after the field has been added
	 * @throws IllegalArgumentException <ul>
	 *                                  <li>If only {@code name} or {@code value} is set. Both must be set.</li>
	 *                                  <li>If the length of {@code name} is greater than {@link MessageEmbed#TITLE_MAX_LENGTH}.</li>
	 *                                  <li>If the length of {@code value} is greater than {@link MessageEmbed#VALUE_MAX_LENGTH}.</li>
	 *                                  </ul>
	 */
	@Nonnull
	public EmbedPagerBuilder addField(@Nullable String name, @Nullable String value, boolean inline) {
		
		if (name == null && value == null) {
			return this;
		}
		
		if (value != null && value.length() > EmbedLimit.EMBED_FIELD_VALUE_LIMIT) {
			List<String> split = StringUtil.splitAt(value, EmbedLimit.EMBED_FIELD_VALUE_LIMIT, "\n");
			fields.add(new MessageEmbed.Field(name, split.get(0), inline));
			for (int i = 1; i < split.size(); i++) {
				fields.add(new MessageEmbed.Field("\\a", split.get(i), inline));
			}
		} else {
			fields.add(new MessageEmbed.Field(name, value, inline));
		}
		
		return this;
	}
	
	/**
	 * Adds a inline Field to the embed.
	 *
	 * <p>Note: If a blank string is provided to either {@code name} or {@code value}, the blank string is replaced
	 * with {@link EmbedPagerBuilder#ZERO_WIDTH_SPACE}.
	 *
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/07-addField.png">Example of Inline</a></b>
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/08-addField.png">Example if Non-inline</a></b>
	 *
	 * @param name  the name of the Field, displayed in bold above the {@code value}.
	 * @param value the contents of the field.
	 * @return the builder after the field has been added
	 * @throws IllegalArgumentException <ul>
	 *                                  <li>If only {@code name} or {@code value} is set. Both must be set.</li>
	 *                                  <li>If the length of {@code name} is greater than {@link MessageEmbed#TITLE_MAX_LENGTH}.</li>
	 *                                  <li>If the length of {@code value} is greater than {@link MessageEmbed#VALUE_MAX_LENGTH}.</li>
	 *                                  </ul>
	 */
	@Nonnull
	public EmbedPagerBuilder addInlineField(@Nullable String name, @Nullable String value) {
		if (name == null && value == null) {
			return this;
		}
		fields.add(new MessageEmbed.Field(name, value, true));
		return this;
	}
	
	/**
	 * Adds a blank (empty) Field to the embed.
	 *
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/07-addField.png">Example of Inline</a></b>
	 * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/08-addField.png">Example if Non-inline</a></b>
	 *
	 * @param inline whether or not this field should display inline
	 * @return the builder after the field has been added
	 */
	@Nonnull
	public EmbedPagerBuilder addBlankField(boolean inline) {
		fields.add(new MessageEmbed.Field(ZERO_WIDTH_SPACE, ZERO_WIDTH_SPACE, inline));
		return this;
	}
	
	/**
	 * Clears all fields from the embed, such as those created with the
	 * {@link EmbedPagerBuilder#( MessageEmbed) EmbedBuilder(MessageEmbed)}
	 * constructor or via the
	 * {@link EmbedPagerBuilder#addField(MessageEmbed.Field) addField} methods.
	 *
	 * @return the builder after the field has been added
	 */
	@Nonnull
	public EmbedPagerBuilder clearFields() {
		fields.clear();
		return this;
	}
	
	/**
	 * <b>Modifiable</b> list of {@link MessageEmbed MessageEmbed} Fields that the builder will
	 * use for {@link #build()}.
	 * <br>You can add/remove Fields and restructure this {@link List List} and it will then be applied in the
	 * built MessageEmbed. These fields will be available again through {@link MessageEmbed#getFields() MessageEmbed.getFields()}.
	 *
	 * @return Mutable List of {@link MessageEmbed.Field Fields}
	 */
	@Nonnull
	public List<MessageEmbed.Field> getFields() {
		return fields;
	}
	
	private void urlCheck(@Nullable String url) {
		if (url != null) {
			Checks.check(url.length() <= MessageEmbed.URL_MAX_LENGTH, "URL cannot be longer than %d characters.", MessageEmbed.URL_MAX_LENGTH);
			Checks.check(URL_PATTERN.matcher(url).matches(), "URL must be a valid http(s) or attachment url.");
		}
	}
	
	/**
	 * For how long the pager should stay active
	 *
	 * @param expiration the time from now to the expiration
	 * @param unit       the time unit of the expiration parameter
	 */
	public void setExpiration(long expiration, TimeUnit unit) {
		this.expiration = unit.toMillis(expiration);
	}
	
}
