package me.jordanplayz158.jmodularbot.commands;

import me.jordanplayz158.jmodularbot.CommandHandler;
import me.jordanplayz158.jmodularbot.JModularBot;
import me.jordanplayz158.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.*;
import java.util.Objects;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help",
                null,
                "Help menu that tells you all the commands and their functions!",
                null,
                null,
                "help",
                false,
                false);
    }

    @Override
    public void onCommand(MessageReceivedEvent event, String[] args) {
        EmbedBuilder embed = JModularBot.getInstance().getTemplate(event.getAuthor())
                .setColor(Color.YELLOW)
                .setTitle("Help");

        Member executor = Objects.requireNonNull(event.getMember());

        for(Command command : CommandHandler.getCommandsList()) {
            if((executor.hasPermission(command.getPermission()) || command.getPermission() == null) && (executor.getRoles().contains(command.getRole()) || command.getRole() == null)) {
                embed.appendDescription(MarkdownUtil.bold(MessageUtils.upperCaseFirstLetter(command.getName())));
                embed.appendDescription(" - ");
                embed.appendDescription(command.getDescription());
                embed.appendDescription("\n");
                embed.appendDescription("Syntax: `");
                embed.appendDescription(command.getSyntax());
                embed.appendDescription("`\n\n");
            }
        }

        event.getChannel().sendMessage(embed.build()).queue();
    }
}
