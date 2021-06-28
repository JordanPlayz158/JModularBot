package xyz.jordanplayz158.jmodularbot.managers;

import xyz.jordanplayz158.jmodularbot.commands.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {
    private static final Map<Object, List<Command>> commands = new HashMap<>();

    /**
     * @return an immutable map of commands (commands are only meant to be modified via the methods in CommandManager)
     */
    public static Map<Object, List<Command>> getCommandMap() {
        return Collections.unmodifiableMap(commands);
    }

    /**
     * Adds specified commands for a plugin
     * @param plugin the plugin you wish to load commands for
     * @param commands the commands you wish to load
     */
    public static void addCommands(Object plugin, Command... commands) {
        for(Command command : commands) {
            CommandManager.commands.putIfAbsent(plugin, new ArrayList<>());
            CommandManager.commands.get(plugin).add(command);
        }
    }

    /**
     * Removes specific commands from a plugin (useful for plugins to unload certain commands).
     * @param plugin the plugin you wish to unload the commands from
     * @param commands the commands you wish to unload
     */
    public static void removeCommands(Object plugin, Command... commands) {
        for(Command command : commands) {
            CommandManager.commands.get(plugin).remove(command);
        }
    }

    /**
     * Removes all commands from specified plugin (useful when unloading plugins).
     * @param plugin the plugin you wish to unload all commands from
     */
    public static void removeAllCommands(Object plugin) {
        commands.remove(plugin);
    }
}
