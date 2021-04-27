package net.volcano.jdacommands.interfaces;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.volcano.jdacommands.client.ReactionMenuClient;
import net.volcano.jdacommands.exceptions.command.parsing.ArgumentParsingException;
import net.volcano.jdacommands.exceptions.command.parsing.CommandNotFoundException;
import net.volcano.jdacommands.model.command.Command;
import net.volcano.jdacommands.model.command.CommandCompiler;
import net.volcano.jdacommands.model.command.CommandEvent;
import net.volcano.jdacommands.model.command.arguments.ParsedData;
import net.volcano.jdacommands.model.command.arguments.interfaces.CodecRegistry;

import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public interface CommandClient {
	
	UserProvider getUserProvider();
	
	PrefixProvider getPrefixProvider();
	
	PermissionProvider getPermissionProvider();
	
	CommandCompiler getCommandCompiler();
	
	CodecRegistry getCodecRegistry();
	
	Set<Command> getCommands();
	
	ScheduledThreadPoolExecutor getExecutorService();
	
	ReactionMenuClient getReactionMenuClient();
	
	boolean registerCommand(Command command);
	
	boolean registerController(Object controller);
	
	ParsedData findAndParse(MessageReceivedEvent event, String content) throws CommandNotFoundException, ArgumentParsingException;
	
	default void call(CommandEvent event) {
		try {
			executeCommand(event);
		} catch (Exception e) {
			terminate(event, e);
		}
	}
	
	void executeCommand(CommandEvent event);
	
	void terminate(CommandEvent event, Throwable t);
	
}
