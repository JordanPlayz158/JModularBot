package xyz.jordanplayz158.jmodularbot.managers;

import java.util.HashMap;

public class PluginManager {
    private static final HashMap<String, Object> plugins = new HashMap<>();

    /**
     * Gets the plugin instance from the hashmap using the plugins name
     * @param plugin the plugins name
     * @return the plugin instance
     */
    public static Object getPlugin(String plugin) {
        return plugins.get(plugin);
    }

    /**
     * Adds a plugin to the hashmap (stored for unloading the plugin at any point)
     * @param pluginName The name of the plugin
     * @param plugin The instance of the plugin
     */
    public static void addPlugin(String pluginName, Object plugin) {
        plugins.put(pluginName, plugin);
    }

    /**
     * Unloads the plugin (events, listeners, and (maybe) plugin class loader)
     * @param pluginName The name of the plugin to be unloaded
     */
    public static void unloadPlugin(String pluginName) {
        Object plugin = getPlugin(pluginName);
        EventManager.removeAllEvents(plugin);
        CommandManager.removeAllCommands(plugin);
        plugins.remove(pluginName);
    }
}
