package xyz.jordanplayz158.jmodularbot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import xyz.jordanplayz158.jmodularbot.commands.Command;
import xyz.jordanplayz158.jmodularbot.storage.Config;
import xyz.jordanplayz158.jmodularbot.managers.CommandManager;
import me.jordanplayz158.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.List;
import java.util.Objects;

public class CommandHandler {
    private final Config config;

    public CommandHandler() {
        config = JModularBot.instance.getConfig();
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
            channel.sendMessageEmbeds(JModularBot.getTemplate(author)
                    .setColor(Color.RED)
                    .setTitle("Access Denied")
                    .setDescription("You do not have access to that command!")
                    .build())
                    .queue();
            return;
        }

        if (command.isArgCheck() && args.length == 1) {
            channel.sendMessageEmbeds(usageEmbed(command, author)).queue();
            return;
        }

        if(command.isHierarchyCheck()) {
            Guild guild = event.getGuild();
            User mentionedUser = MessageUtils.extractMention(args[1]);

            if (guild.isMember(mentionedUser)) {
                if (!hierarchyCheck(guild, Objects.requireNonNull(member), Objects.requireNonNull(guild.getMember(mentionedUser)))) {
                    channel.sendMessageEmbeds(JModularBot.getTemplate(author)
                            .setColor(Color.RED)
                            .setTitle("Hierarchy Check")
                            .setDescription("You can't execute this command as you are lower or the same on the hierarchy than the person you are using this on.")
                            .build())
                            .queue();
                    return;
                }
            }
        }

        if(!command.onCommand(event, args)) {
            channel.sendMessageEmbeds(usageEmbed(command, author)).queue();
        }
    }

    private String[] getArgs(String message) {
        return message.substring(config.getPrefix().length()).split("\\s+");
    }

    private Command getCommand(String commandName) {
        for(List<Command> commandList : CommandManager.getCommandMap().values()) {
            for(Command c : commandList) {
                if (c.getName().equals(commandName) || c.getAliases().contains(commandName)) {
                    return c;
                }
            }
        }

        return null;
    }

    private boolean permissionCheck(Command command, Member member) {
        return command.getPermission() != null && !member.hasPermission(command.getPermission());
    }

    private boolean rolesCheck(Command command, Member member) {
        return command.getRole() != null && !member.getRoles().contains(command.getRole());
    }

    private MessageEmbed usageEmbed(Command command, User author) {
        List<String> commandAliases = command.getAliases();
        StringBuilder aliases = new StringBuilder();

        if (!commandAliases.isEmpty()) {
            aliases.append(config.getPrefix()).append(commandAliases.get(0));

            for (int i = 1; i < commandAliases.size(); i++) {
                aliases.append(", ").append(config.getPrefix()).append(commandAliases.get(i));
            }
        }

        EmbedBuilder embed = JModularBot.getTemplate(author).setColor(Color.YELLOW)
                .setTitle(command.getName())
                .addField("Description", command.getDescription(), false)
                .addField("Syntax", command.getSyntax(), false);

        if(aliases.toString().isBlank()) {
            return embed.build();
        } else {
            return embed.addField("Aliases", aliases.toString(), false).build();
        }
    }

    private boolean hierarchyCheck(Guild guild, Member member, Member mention) {
        List<Role> guildRoles = guild.getRoles();
        List<Role> memberRoles = member.getRoles();
        List<Role> mentionRoles = mention.getRoles();

        JModularBot.logger.debug("Member's Highest Role: "
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
}
