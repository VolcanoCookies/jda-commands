package net.volcano.jdacommands.model.command;

import net.volcano.jdautilities.Pair;

import java.util.*;

public class CommandNode {
	
	private final boolean isRoot;
	
	/**
	 * The {@link Command}'s on this node.
	 */
	private final Set<Command> commands = new HashSet<>();
	/**
	 * The {@link CommandNode}'s on this node.
	 */
	private final Map<String, CommandNode> map = new HashMap<>();
	
	public CommandNode(boolean isRoot) {
		this.isRoot = isRoot;
	}
	
	public CommandNode() {
		isRoot = false;
	}
	
	/**
	 * Add a command to this node, or any of its children
	 *
	 * @param command the {@link Command} to add
	 * @param path    the path to add this command at
	 * @return {@code true} if any change occurred
	 */
	public boolean addCommand(Command command, String... path) {
		if (isRoot && path.length == 0) {
			throw new IllegalArgumentException("Path cannot be empty.");
		}
		
		if (path.length == 0) {
			return commands.add(command);
		}
		if (!map.containsKey(path[0])) {
			map.put(path[0], new CommandNode());
		}
		return map.get(path[0]).addCommand(command, path.length == 1 ? new String[0] : Arrays.copyOfRange(path, 1, path.length));
	}
	
	/**
	 * Tries to find the longest possible chain from the path, then return the commands at the end
	 *
	 * @param path the path to go down
	 * @return a set of commands found at the end
	 */
	public Pair<Set<Command>, Integer> findCommands(String... path) {
		if (path.length == 0) {
			return new Pair<>(commands, 0);
		}
		
		if (map.containsKey(path[0])) {
			return map.get(path[0])
					.findCommands(Arrays.copyOfRange(path, 1, path.length));
		} else {
			return new Pair<>(commands, path.length);
		}
	}
	
}
