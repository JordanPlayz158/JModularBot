package xyz.jordanplayz158.jmodularbot.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.jordanplayz158.jmodularbot.JModularBot;
import xyz.jordanplayz158.jmodularbot.managers.CommandManager;
import xyz.jordanplayz158.jmodularbot.managers.EventManager;
import xyz.jordanplayz158.jmodularbot.managers.PluginManager;
import xyz.jordanplayz158.jmodularbot.plugin.Plugin;
import xyz.jordanplayz158.jmodularbot.plugin.PluginConfig;
import xyz.jordanplayz158.jmodularbot.plugin.PluginLoader;

import java.awt.Color;
import java.util.List;

public class PluginCommand extends Command {
    public PluginCommand() {
        super("plugin",
                List.of("pluginList", "listPlugins"),
                "Used to load and unload plugins (at runtime (all plugins in plugins folder are loaded on startup))",
                null,
                null,
                "plugin <load|unload> <name>",
                false,
                false);
    }

    @Override
    public void onCommand(MessageReceivedEvent event, String[] args) {
        switch (args[0]) {
            case "pluginList", "listPlugins" -> {
                StringBuilder stringBuilder = new StringBuilder();
                for (Plugin plugin : PluginManager.getPluginMap().values()) {
                    PluginConfig config = plugin.getPluginConfig();

                    stringBuilder.append(config.getName())
                            .append(" v")
                            .append(config.getVersion())
                            .append(" by ")
                            .append(PluginLoader.authorText(config.getAuthors()))
                            .append(" - ")
                            .append(config.getDescription())
                            .append("\n");
                }
                event.getChannel().sendMessageEmbeds(JModularBot.getTemplate(event.getAuthor())
                        .setColor(Color.YELLOW)
                        .setTitle("Plugin List")
                        .setDescription(stringBuilder.toString())
                        .build()
                ).queue();
            }
            case "plugin" -> {
                if (args.length == 1) {
                    StringBuilder aliases = new StringBuilder();

                    if (!getAliases().isEmpty()) {
                        for (String alias : getAliases()) {
                            aliases.append(JModularBot.instance.getConfig().getPrefix()).append(alias).append(", ");
                        }
                    }

                    event.getChannel().sendMessageEmbeds(JModularBot.getTemplate(event.getAuthor()).setColor(Color.YELLOW)
                            .setTitle(getName())
                            .addField("Description", getDescription(), false)
                            .addField("Syntax", getSyntax(), false)
                            .addField("Aliases", aliases.substring(0, aliases.length() - 2), false)
                            .build()
                    ).queue();
                    return;
                }
                Plugin plugin;
                PluginConfig config;
                switch (args[1]) {
                    case "load" -> {
                        JModularBot.logger.debug("PluginManager Map: " + PluginManager.getPluginMap());
                        JModularBot.logger.debug("CommandManager Map: " + CommandManager.getCommandMap());
                        JModularBot.logger.debug("EventManager Map: " + EventManager.getEventsMap() + "\n");

                        plugin = JModularBot.instance.getLoader().LoadClass(args[2], Plugin.class);
                        config = plugin.getPluginConfig();

                        if (config != null) {
                            event.getChannel().sendMessageEmbeds(JModularBot.getTemplate(event.getAuthor())
                                    .setColor(Color.GREEN)
                                    .setTitle("Plugin \"" + config.getName() + "\" was loaded successfully!")
                                    .addField("Description", config.getDescription(), false)
                                    .addField("Version", config.getVersion(), false)
                                    .addField("Authors", PluginLoader.authorText(config.getAuthors()), false)
                                    .build()
                            ).queue();
                        } else {
                            event.getChannel().sendMessageEmbeds(JModularBot.getTemplate(event.getAuthor())
                                    .setColor(Color.RED)
                                    .setTitle("Plugin wasn't loaded successfully")
                                    .setDescription("Check console for details!")
                                    .build()
                            ).queue();
                        }

                        JModularBot.logger.debug("PluginManager Map: " + PluginManager.getPluginMap());
                        JModularBot.logger.debug("CommandManager Map: " + CommandManager.getCommandMap());
                        JModularBot.logger.debug("EventManager Map: " + EventManager.getEventsMap());
                    }

                    case "unload" -> {
                        JModularBot.logger.debug("PluginManager Map: " + PluginManager.getPluginMap());
                        JModularBot.logger.debug("CommandManager Map: " + CommandManager.getCommandMap());
                        JModularBot.logger.debug("EventManager Map: " + EventManager.getEventsMap() + "\n");

                        plugin = PluginManager.getPlugin(args[2]);
                        config = plugin.getPluginConfig();


                        PluginManager.unloadPlugin(args[2]);
                        event.getChannel().sendMessageEmbeds(JModularBot.getTemplate(event.getAuthor())
                                .setColor(Color.GREEN)
                                .setTitle("Plugin \"" + config.getName() + "\" was unloaded successfully!")
                                .build()
                        ).queue();

                        JModularBot.logger.debug("PluginManager Map: " + PluginManager.getPluginMap());
                        JModularBot.logger.debug("CommandManager Map: " + CommandManager.getCommandMap());
                        JModularBot.logger.debug("EventManager Map: " + EventManager.getEventsMap());
                    }
                }
            }
        }
    }
}
