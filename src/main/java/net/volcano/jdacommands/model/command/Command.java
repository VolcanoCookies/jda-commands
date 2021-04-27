package net.volcano.jdacommands.model.command;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.RestAction;
import net.volcano.jdacommands.exceptions.command.parsing.ArgumentParsingException;
import net.volcano.jdacommands.exceptions.command.run.CommandRuntimeException;
import net.volcano.jdacommands.interfaces.CommandClient;
import net.volcano.jdacommands.model.command.arguments.ArgumentList;
import net.volcano.jdacommands.model.command.arguments.ParsedData;
import net.volcano.jdacommands.model.command.arguments.implementation.ArgumentParsingData;
import net.volcano.jdautils.constants.Colors;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Comparator;
import java.util.Set;

@Builder
@Data
@Slf4j
public class Command {
	
	/**
	 * The various paths of this command
	 */
	protected final String[] paths;
	
	/**
	 * The required permissions for this command
	 */
	protected final Set<String> permissions;
	
	/**
	 * The source for this command
	 * default: {@link Source#BOTH}
	 */
	protected final Source source;
	
	/**
	 * If this command requires global permissions
	 * Commands with this as {@code true} requires the caller to have
	 * the required permissions globally, and not only locally in the guild running the command.
	 * default: {@code false}
	 */
	protected final boolean globalPermissions;
	
	/**
	 * If this command is sensitive
	 * Sensitive commands will only be shown in dms.
	 * default: {@code false}
	 */
	protected final boolean sensitive;
	
	/**
	 * The help message for this particular command
	 */
	protected final Help help;
	
	/**
	 * The arguments for this command
	 */
	protected final ArgumentList arguments;
	
	/**
	 * The method to call when this command should be executed
	 */
	protected final CommandFunction method;
	
	@Builder
	@Getter
	@Nullable
	public static class Help {
		
		/**
		 * The usage of this command
		 * if none provided, autogenerated from argument list
		 */
		protected final String usage;
		
		/**
		 * A short description of this command
		 */
		protected final String description;
		
		/**
		 * Any examples of this command
		 */
		protected final String[] examples;
		
		/**
		 * A detailed message for this command
		 * Displayed when user requests help on the particular command
		 */
		protected final String details;
		
		/**
		 * The category this command belongs to
		 */
		protected final Category category;
		
	}
	
	/**
	 * Ran when this command gets registered with the {@link CommandClient}
	 */
	public void onRegister() {
		log.info("Registered '{}' as a command.", paths[0]);
	}
	
	public RestAction<?> call(CommandEvent event) throws InvocationTargetException,
			IllegalAccessException,
			CommandRuntimeException {
		return method.invoke(event, event.data);
	}
	
	public String getUsageFormatted() {
		return (paths[0] + (help.usage.isBlank() ? "" : " " + help.usage)).trim();
	}
	
	public String getDescriptionFormatted() {
		return help.description.trim();
	}
	
	public EmbedBuilder getDetailedHelp() {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle("Command: " + paths[0]);
		embedBuilder.addField("Usage", "`" + getUsageFormatted() + "`", false);
		embedBuilder.addField("Description", getDescriptionFormatted(), false);
		if (help.getExamples().length > 0) {
			embedBuilder.addField("Examples", String.join("\n", help.getExamples()), false);
		}
		if (help.getDetails() != null) {
			embedBuilder.setDescription(help.getDescription());
		}
		embedBuilder.setColor(Colors.HELP);
		embedBuilder.setTimestamp(Instant.now());
		return embedBuilder;
	}
	
	/**
	 * The different sources where the bot can receive a message from
	 */
	public enum Source {
		PRIVATE,
		GUILD,
		BOTH
	}
	
	/**
	 * The different help categories.
	 */
	public enum Category {
		
		GENERAL("ℹ️"),
		CONTROL("⚙️"),
		FIREWALL("🔥"),
		ROLE_MENU("➖"),
		SERVER_PROTECTION("🛡️"),
		PERMISSIONS("⚛️"),
		SERVER_SETTINGS("⚜️"),
		PROJECT("📄"),
		DATABASE("📊"),
		UTILITY("🛠️"),
		MODERATION("🚨");
		
		private final String value;
		
		Category(String value) {
			this.value = value;
		}
		
		public String value() {
			return value;
		}
		
		public static Category fromValue(String value) {
			for (Category category : values()) {
				if (value.equals(category.value())) {
					return category;
				}
			}
			return null;
		}
		
	}
	
	public ParsedData parseArguments(ArgumentParsingData parsingData) throws ArgumentParsingException {
		ParsedData data = arguments.parseArguments(parsingData);
		data.command = this;
		return data;
	}
	
	public static Comparator<Command> argumentComparator() {
		return (c1, c2) -> {
			int res = c1.getArguments().size() - c2.getArguments().size();
			/*if (res == 0) {
				if (c1.getArguments().lastIsTakeAll() && !c2.getArguments().lastIsTakeAll()) {
					return 1;
				} else if (!c1.getArguments().lastIsTakeAll() && c2.getArguments().lastIsTakeAll()) {
					return -1;
				}
			}*/
			return res;
		};
	}
	
}


