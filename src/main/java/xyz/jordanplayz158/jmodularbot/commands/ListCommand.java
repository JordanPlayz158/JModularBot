package xyz.jordanplayz158.jmodularbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.jordanplayz158.jmodularbot.JModularBot;
import xyz.jordanplayz158.jmodularbot.managers.CommandManager;
import xyz.jordanplayz158.jmodularbot.managers.EventManager;
import xyz.jordanplayz158.jmodularbot.managers.PluginManager;


public class ListCommand extends Command {
    public ListCommand() {
        super("list",
                null,
                "Lists the various options.",
                null,
                null,
                "list <plugins|events|commands>",
                true,
                false);
    }

    @Override
    public boolean onCommand(MessageReceivedEvent event, String[] args) {
        MessageChannel channel = event.getChannel();
        User author = event.getAuthor();

        switch (args[1]) {
            case "plugins" ->
                    channel.sendMessageEmbeds(getListEmbed(author, "Plugins")
                            .setDescription(PluginManager.getPluginMap().toString())
                            .build()).queue();
            case "events" ->
                    channel.sendMessageEmbeds(getListEmbed(author, "Events")
                            .setDescription(EventManager.getEventsMap().toString())
                            .build()).queue();
            case "commands" ->
                    channel.sendMessageEmbeds(getListEmbed(author, "Commands")
                            .setDescription(CommandManager.getCommandMap().toString())
                            .build()).queue();
        }

        return true;
    }

    public EmbedBuilder getListEmbed(User author, String listName) {
        return JModularBot.getTemplate(author)
                .setTitle("List | " + listName);
    }
}
