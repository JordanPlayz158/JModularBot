package xyz.jordanplayz158.jmodularbot.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import xyz.jordanplayz158.jmodularbot.JModularBot;
import xyz.jordanplayz158.jmodularbot.managers.PluginManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {
    private final Logger logger = JModularBot.logger;

    public Plugin LoadClass(String file) {
        File pluginFile = getJarPath(file);

        if (!pluginFile.exists() || pluginFile.isDirectory()) {
            return null;
        }

        logger.debug("Plugin from \"" + pluginFile.getAbsolutePath() + "\" is being loaded!");

        try {
            URLClassLoader loader = new URLClassLoader(new URL[]{pluginFile.toURI().toURL()});

            PluginConfig config = getPluginConfig(pluginFile);

            Plugin plugin = loadMainClass(config, loader);

            logger.debug("Successfully loaded plugin \"" + config.getName() + "\" v" + config.getVersion() + " by " + authorText(config.getAuthors()));

            return plugin;
        } catch (IOException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            logger.debug(e.getMessage(), e);
        }

        return null;
    }

    private File getJarPath(String path) {
        if (path.contains("plugins")) {
            path = System.getProperty("user.dir") + "/" + path;
        } else {
            path = System.getProperty("user.dir") + "/plugins/" + path;
        }

        if (!path.contains(".jar")) {
            StringBuilder stringBuilder = new StringBuilder();
            String[] pathSplit = path.split("/");

            for (int i = 0; i < pathSplit.length; i++) {
                if (i == pathSplit.length - 1) {
                    stringBuilder.append(pathSplit[i]).append(".jar");
                } else {
                    stringBuilder.append(pathSplit[i]).append("/");
                }
            }

            path = stringBuilder.toString();
        }


        return new File(path);
    }

    private PluginConfig getPluginConfig(File jar) throws IOException {
        JarFile plugin = new JarFile(jar);
        JarEntry yml = plugin.getJarEntry("plugin.yml");

        if(yml == null) {
            throw new FileNotFoundException("Plugin does not contain plugin.yml");
        }

        InputStream ymlIS = plugin.getInputStream(yml);

        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        return objectMapper.readValue(ymlIS, PluginConfig.class);
    }

    private Plugin loadMainClass(PluginConfig config, ClassLoader loader) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class<?> jarClass = Class.forName(config.getMain(), true, loader);
        Class<? extends Plugin> pluginClass = jarClass.asSubclass(Plugin.class);

        String pluginName = config.getName();

        // Apparently its bad to use Class.newInstance, so we use
        // newClass.getConstructor() instead
        Constructor constructor = pluginClass.getConstructor();
        Plugin plugin = (Plugin) constructor.newInstance();

        pluginClass.getMethod("init", File.class, PluginConfig.class).invoke(plugin, new File("plugins/" + config.getName()), config);

        try {
            pluginClass.getMethod("onEnable").invoke(plugin);
        } catch (InvocationTargetException error) {
            if(error.getCause().getClass() == AbstractMethodError.class) {
                logger.debug("onEnable method not found");
            }

            throw error;
        }

        PluginManager.addPlugin(pluginName, plugin);

        return plugin;
    }

    public static String authorText(String[] authors) {
        if(authors.length == 1) {
            return authors[0];
        }

        StringBuilder stringBuilder = new StringBuilder(authors[0]);

        for (int i = 1; i < authors.length - 2; i++) {
            stringBuilder.append(", ").append(authors[i]);
        }

        stringBuilder.append(", and ").append(authors[authors.length - 1]).append(".");

        return stringBuilder.toString();
    }
}
