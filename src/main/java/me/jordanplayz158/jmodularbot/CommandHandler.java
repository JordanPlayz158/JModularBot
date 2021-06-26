package me.jordanplayz158.jmodularbot;

import me.jordanplayz158.jmodularbot.commands.Command;
import me.jordanplayz158.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommandHandler {
    private static final List<Command> commandsList = new ArrayList<>();

    public static void handler(MessageReceivedEvent event) {
        // Removes the prefix (substring) then splits the message into arguments by whitespace
        String[] args = getArgs(event.getMessage().getContentRaw());

        Command command = getCommand(args[0]);

        // Does the command exist?
        if (command == null) {
            return;
        }

        MessageChannel channel = event.getChannel();

        if ((command.getPermission() != null && !Objects.requireNonNull(event.getMember()).hasPermission(command.getPermission())) || (command.getRole() != null && !Objects.requireNonNull(event.getMember()).getRoles().contains(command.getRole()))) {
            channel.sendMessage(JModularBot.getInstance().getTemplate(event.getAuthor()).setColor(Color.RED).setTitle("Access Denied").setDescription("You do not have access to that command!").build()).queue();
            return;
        }

        boolean argCheck = command.isArgCheck();
        boolean hierarchyCheck = command.isHierarchyCheck();

        if (argCheck && args.length == 1) {
            StringBuilder aliases = new StringBuilder();

            if (command.getAliases().size() > 1) {
                aliases.append("\n\n").append(MarkdownUtil.bold("Known Aliases")).append("\n");

                for (String alias : command.getAliases()) {
                    aliases.append(JModularBot.getInstance().getConfig().getPrefix()).append(alias).append("\n");
                }
            }

            channel.sendMessage(JModularBot.getInstance().getTemplate(event.getAuthor()).setColor(Color.YELLOW)
                    .setTitle(command.getName())
                    .setDescription(command.getDescription() + aliases)
                    .addField("Syntax", command.getSyntax(), true).build()).queue();
            return;
        }

        if(hierarchyCheck) {
            Guild guild = event.getGuild();
            User mentionedUser = MessageUtils.extractMention(args[1]);

            if (guild.isMember(mentionedUser)) {
                if (!hierarchyCheck(guild, Objects.requireNonNull(event.getMember()), Objects.requireNonNull(guild.getMember(mentionedUser)))) {
                    channel.sendMessage(JModularBot.getInstance().getTemplate(event.getAuthor()).setColor(Color.RED).setTitle("Hierarchy Check").setDescription("You can't execute this command as you are lower or the same on the hierarchy than the person you are using this on.").build()).queue();
                    return;
                }
            }
        }

        command.onCommand(event, args);
    }

    private static String[] getArgs(String message) {
        return message.substring(JModularBot.getInstance().getConfig().getPrefix().length()).split("\\s+");
    }

    private static Command getCommand(String commandName) {
        for(Command c : commandsList) {
            if (c.getName().equals(commandName) || c.getAliases().contains(commandName)) {
                return c;
            }
        }

        return null;
    }

    private static boolean hierarchyCheck(Guild guild, Member member, Member mention) {
        List<Role> guildRoles = guild.getRoles();
        List<Role> memberRoles = member.getRoles();
        List<Role> mentionRoles = mention.getRoles();

        JModularBot.getInstance().getLogger().debug("Member's Highest Role: "
                + guildRoles.indexOf(memberRoles.get(0))
                + "\nMention's Highest Role: "
                + guildRoles.indexOf(mentionRoles.get(0))
                + "\nIs the member's highest role higher than the mentioned member's highest role? "
                + (guildRoles.indexOf(memberRoles.get(0)) < guildRoles.indexOf(mentionRoles.get(0))));

        if(memberRoles.size() < 1) {
            return false;
        }

        if(mentionRoles.size() < 1) {
            return true;
        }

        return guildRoles.indexOf(memberRoles.get(0)) < guildRoles.indexOf(mentionRoles.get(0));
    }

    public static List<Command> getCommandsList() {
        return Collections.unmodifiableList(commandsList);
    }

    public static void addCommands(Command... commands) {
        commandsList.addAll(Arrays.asList(commands));
    }

    public static void removeCommands(Command... commands) {
        commandsList.removeAll(Arrays.asList(commands));
    }
}
