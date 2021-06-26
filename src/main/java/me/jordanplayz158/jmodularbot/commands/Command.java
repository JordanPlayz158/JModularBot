package me.jordanplayz158.jmodularbot.commands;

import lombok.Getter;
import me.jordanplayz158.jmodularbot.JModularBot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class Command {
    protected final String name;
    protected final List<String> aliases = new ArrayList<>();
    protected final String description;
    protected final Permission permission;
    protected final Role role;
    protected final String syntax;
    protected final boolean argCheck;
    protected final boolean hierarchyCheck;

    public Command(@NotNull String name, String[] aliases, @NotNull String description, Permission permission, Role role, @NotNull String syntax, boolean argCheck, boolean hierarchyCheck) {
        this.name = name;

        if(aliases != null) {
            this.aliases.addAll(Arrays.asList(aliases));
        }

        this.description = description;
        this.permission = permission;
        this.role = role;
        this.syntax = JModularBot.getInstance().getConfig().getPrefix() + syntax;
        this.argCheck = argCheck;
        this.hierarchyCheck = hierarchyCheck;
    }

    public abstract void onCommand(MessageReceivedEvent event, String[] args);

    public boolean argsCheck(String[] args, int size) {
        return args.length < size;
    }
}