package xyz.jordanplayz158.jmodularbot.managers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.jordanplayz158.jmodularbot.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    private static JDA jda = null;
    private static final Map<Plugin, List<ListenerAdapter>> events = new HashMap<>();

    /**
     * Passes jda for registering and unregistering events after JDA has been initialized
     * @param jda the jda instance you wish to pass to event manager
     */
    public EventManager(JDA jda) {
        EventManager.jda = jda;
    }

    /**
     * @return an immutable map of events (events are only meant to be modified via the methods in EventManager)
     */
    public static Map<Plugin, List<ListenerAdapter>> getEventsMap() {
        return Collections.unmodifiableMap(events);
    }

    /**
     * Adds specific events for a plugin
     * @param plugin the plugin you wish to load events for
     * @param events the events you wish to load
     */
    public static void addEvents(Plugin plugin, ListenerAdapter... events) {
        for(ListenerAdapter event : events) {
            EventManager.events.putIfAbsent(plugin, new ArrayList<>());
            EventManager.events.get(plugin).add(event);
            if(jda != null) {
                registerEvents(jda, event);
            }
        }
    }

    /**
     * Removes specific events from a plugin (useful for plugins to unload certain events).
     * @param plugin the plugin you wish to unload the events from
     * @param events the events you wish to unload
     */
    public static void removeEvents(Plugin plugin, ListenerAdapter... events) {
        for(ListenerAdapter event : events) {
            EventManager.events.get(plugin).remove(event);
            unregisterEvents(jda, event);
        }
    }

    /**
     * Removes all events from specified plugin (useful when unloading plugins)
     * @param plugin the plugin you wish to unload all events from
     */
    public static void removeAllEvents(Plugin plugin) {
        events.get(plugin).forEach(jda::removeEventListener);
        events.remove(plugin);
    }

    /**
     * Registers all events in the events values list to the JDABuilder (for use before jda is initialized)
     * @param builder the JDABuilder to register the events to
     */
    public static void registerEvents(JDABuilder builder) {
        events.values().forEach(events -> events.forEach(builder::addEventListeners));
    }

    /**
     * Registers the event to the current jda instance (for use after jda is initialized)
     * @param jda the jda instance to register the events to
     * @param event the event listener to register
     */
    public static void registerEvents(JDA jda, ListenerAdapter event) {
        jda.addEventListener(event);
    }

    /**
     * Unregisters the event from the current jda instance (for use after jda is initialized)
     * @param jda the jda instance to register the events to
     * @param event the event listener to register
     */
    public static void unregisterEvents(JDA jda, ListenerAdapter event) {
        jda.removeEventListener(event);
    }
}