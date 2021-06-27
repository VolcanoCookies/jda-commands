package net.volcano.jdacommands.interfaces;

import net.volcano.jdacommands.client.ReactionMenuClient;
import net.volcano.jdacommands.exceptions.command.CommandCompileException;
import net.volcano.jdacommands.model.command.Command;
import net.volcano.jdacommands.model.command.CommandCompiler;
import net.volcano.jdacommands.model.command.CommandEvent;
import net.volcano.jdacommands.model.command.CommandNode;
import net.volcano.jdacommands.model.command.arguments.interfaces.CodecRegistry;

import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public interface CommandClient {
	
	UserProvider getUserProvider();
	
	GuildProvider getGuildProvider();
	
	PrefixProvider getPrefixProvider();
	
	PermissionProvider getPermissionProvider();
	
	CommandCompiler getCommandCompiler();
	
	CodecRegistry getCodecRegistry();
	
	Set<Command> getAllCommands();
	
	CommandNode getRootCommandNode();
	
	ScheduledThreadPoolExecutor getExecutorService();
	
	ReactionMenuClient getReactionMenuClient();
	
	PermissionClient getPermissionClient();
	
	boolean registerCommand(Command command);
	
	boolean registerController(Object controller) throws CommandCompileException;
	
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
