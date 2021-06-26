package me.jordanplayz158.jmodularbot;

import me.jordanplayz158.jmodularbot.commands.Command;
import me.jordanplayz158.jmodularbot.json.Config;
import me.jordanplayz158.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommandHandler {
    private final List<Command> commandsList = new ArrayList<>();
    private final JModularBot instance;
    private final Config config;

    public CommandHandler() {
        instance = JModularBot.getInstance();
        config = instance.getConfig();
    }

    public void handler(MessageReceivedEvent event) {
        // Removes the prefix (substring) then splits the message into arguments by whitespace
        String[] args = getArgs(event.getMessage().getContentRaw());

        Command command = getCommand(args[0]);

        // Does the command exist?
        if (command == null) {
            return;
        }

        MessageChannel channel = event.getChannel();
        Member member = event.getMember();
        User author = event.getAuthor();

        if (permissionCheck(command, member) || rolesCheck(command, member)) {
            channel.sendMessageEmbeds(instance.getTemplate(author)
                    .setColor(Color.RED)
                    .setTitle("Access Denied")
                    .setDescription("You do not have access to that command!")
                    .build())
                    .queue();
            return;
        }

        if (command.isArgCheck() && args.length == 1) {
            StringBuilder aliases = new StringBuilder();

            if (!command.getAliases().isEmpty()) {
                for (String alias : command.getAliases()) {
                    aliases.append(config.getPrefix()).append(alias).append(", ");
                }
            }

            channel.sendMessageEmbeds(instance.getTemplate(author).setColor(Color.YELLOW)
                    .setTitle(command.getName())
                    .addField("Description", command.getDescription(), false)
                    .addField("Syntax", command.getSyntax(), false)
                    .addField("Aliases", aliases.substring(0, aliases.length() - 2), false).build()).queue();
            return;
        }

        if(command.isHierarchyCheck()) {
            Guild guild = event.getGuild();
            User mentionedUser = MessageUtils.extractMention(args[1]);

            if (guild.isMember(mentionedUser)) {
                if (!hierarchyCheck(guild, Objects.requireNonNull(member), Objects.requireNonNull(guild.getMember(mentionedUser)))) {
                    channel.sendMessageEmbeds(instance.getTemplate(author)
                            .setColor(Color.RED)
                            .setTitle("Hierarchy Check")
                            .setDescription("You can't execute this command as you are lower or the same on the hierarchy than the person you are using this on.")
                            .build())
                            .queue();
                    return;
                }
            }
        }

        command.onCommand(event, args);
    }

    private String[] getArgs(String message) {
        return message.substring(config.getPrefix().length()).split("\\s+");
    }

    private Command getCommand(String commandName) {
        for(Command c : commandsList) {
            if (c.getName().equals(commandName) || c.getAliases().contains(commandName)) {
                return c;
            }
        }

        return null;
    }

    private boolean permissionCheck(Command command, Member member) {
        return command.getPermission() != null && member.hasPermission(command.getPermission());
    }

    private boolean rolesCheck(Command command, Member member) {
        return command.getRole() != null && member.getRoles().contains(command.getRole());
    }

    private boolean hierarchyCheck(Guild guild, Member member, Member mention) {
        List<Role> guildRoles = guild.getRoles();
        List<Role> memberRoles = member.getRoles();
        List<Role> mentionRoles = mention.getRoles();

        instance.logger.debug("Member's Highest Role: "
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

    public List<Command> getCommandsList() {
        return Collections.unmodifiableList(commandsList);
    }

    public void addCommands(Command... commands) {
        commandsList.addAll(Arrays.asList(commands));
    }

    public void removeCommands(Command... commands) {
        commandsList.removeAll(Arrays.asList(commands));
    }
}
