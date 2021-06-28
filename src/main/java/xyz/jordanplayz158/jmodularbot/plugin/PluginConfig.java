package xyz.jordanplayz158.jmodularbot.plugin;

import lombok.Getter;

@Getter
public class PluginConfig {
    private String name;
    private String version;
    private String description;
    private String[] authors;
    private String main;
    private String[] depend;
    private String[] softdepend;
}
