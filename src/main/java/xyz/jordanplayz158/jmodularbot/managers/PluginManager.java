package xyz.jordanplayz158.jmodularbot.managers;

import xyz.jordanplayz158.jmodularbot.plugin.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PluginManager {
    private static final Map<String, Plugin> plugins = new HashMap<>();

    /**
     * @return an immutable map of plugins (plugins are only meant to be modified via the methods in PluginManager)
     */
    public static Map<String, Plugin> getPluginMap() {
        return Collections.unmodifiableMap(plugins);
    }

    /**
     * Gets the plugin instance from the hashmap using the plugins name
     * @param plugin the plugins name
     * @return the plugin instance
     */
    public static Plugin getPlugin(String plugin) {
        return plugins.get(plugin);
    }

    /**
     * Adds a plugin to the hashmap (stored for unloading the plugin at any point)
     * @param pluginName The name of the plugin
     * @param plugin The instance of the plugin
     */
    public static void addPlugin(String pluginName, Plugin plugin) {
        plugins.put(pluginName, plugin);
    }

    /**
     * Unloads the plugin (events, listeners, and (maybe) plugin class loader)
     * @param pluginName The name of the plugin to be unloaded
     */
    public static void unloadPlugin(String pluginName) {
        Plugin plugin = getPlugin(pluginName);
        plugin.onDisable();
        EventManager.removeAllEvents(plugin);
        CommandManager.removeAllCommands(plugin);
        plugins.remove(pluginName);
    }
}
