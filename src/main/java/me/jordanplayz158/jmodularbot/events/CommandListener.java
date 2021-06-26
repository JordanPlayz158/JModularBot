package me.jordanplayz158.jmodularbot.events;

import me.jordanplayz158.jmodularbot.JModularBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Message must start with prefix for the message to be passed to CommandHandler
        if(event.getMessage().getContentRaw().startsWith(JModularBot.getInstance().getConfig().getPrefix())) {
            JModularBot.getInstance().getCommandHandler().handler(event);
        }
    }
}
