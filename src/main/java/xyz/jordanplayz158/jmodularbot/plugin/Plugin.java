package xyz.jordanplayz158.jmodularbot.plugin;

import lombok.Getter;

import java.io.File;

@Getter
public abstract class Plugin {
    private File dataFolder;
    private PluginConfig pluginConfig;

    public void init(File dataFolder, PluginConfig pluginConfig) {
        this.dataFolder = dataFolder;
        this.pluginConfig = pluginConfig;
    }
}
