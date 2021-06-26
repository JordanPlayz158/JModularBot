package me.jordanplayz158.jmodularbot.events;

import me.jordanplayz158.jmodularbot.CommandHandler;
import me.jordanplayz158.jmodularbot.JModularBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandsListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // If Prefix of Bot then continue, else do nothing
        if(event.getMessage().getContentRaw().startsWith(JModularBot.getInstance().getConfig().getPrefix())) {
            CommandHandler.handler(event);
        }
    }
}
