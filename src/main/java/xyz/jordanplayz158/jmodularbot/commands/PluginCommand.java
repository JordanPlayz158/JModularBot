package xyz.jordanplayz158.jmodularbot.commands;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.jordanplayz158.jmodularbot.JModularBot;
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
    public boolean onCommand(MessageReceivedEvent event, String[] args) {
        MessageChannel channel = event.getChannel();

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
                channel.sendMessageEmbeds(JModularBot.getTemplate(event.getAuthor())
                        .setColor(Color.YELLOW)
                        .setTitle("Plugin List")
                        .setDescription(stringBuilder.toString())
                        .build()
                ).queue();
            }
            case "plugin" -> {
                if (args.length == 1) {
                    return false;
                }

                switch (args[1]) {
                    case "load" -> {
                        PluginConfig config = JModularBot.instance.getLoader().LoadClass(args[2]).getPluginConfig();

                        if (config != null) {
                            channel.sendMessageEmbeds(JModularBot.getTemplate(event.getAuthor())
                                    .setColor(Color.GREEN)
                                    .setTitle("Plugin \"" + config.getName() + "\" was loaded successfully!")
                                    .addField("Description", config.getDescription(), false)
                                    .addField("Version", config.getVersion(), false)
                                    .addField("Authors", PluginLoader.authorText(config.getAuthors()), false)
                                    .build()
                            ).queue();
                        } else {
                            channel.sendMessageEmbeds(JModularBot.getTemplate(event.getAuthor())
                                    .setColor(Color.RED)
                                    .setTitle("Plugin wasn't loaded successfully")
                                    .setDescription("Check console for details!")
                                    .build()
                            ).queue();
                        }
                    }

                    case "unload" -> {
                        Plugin plugin = PluginManager.getPlugin(args[2]);
                        PluginManager.unloadPlugin(args[2]);
                        channel.sendMessageEmbeds(JModularBot.getTemplate(event.getAuthor())
                                .setColor(Color.GREEN)
                                .setTitle("Plugin \"" + plugin.getPluginConfig().getName() + "\" was unloaded successfully!")
                                .build()
                        ).queue();
                    }
                }
            }
        }

        return true;
    }
}
