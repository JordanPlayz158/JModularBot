package xyz.jordanplayz158.jmodularbot.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader<C> {
    public Logger logger;

    public PluginLoader(Logger logger) {
        this.logger = logger;
    }

    public void LoadClass(File directory, Class<C> parentClass) {
        File pluginsDir = new File(System.getProperty("user.dir") + "/" + directory);

        if(pluginsDir.listFiles() == null) {
            return;
        }

        for (File jar : Objects.requireNonNull(pluginsDir.listFiles())) {
            try {
                ClassLoader loader = URLClassLoader.newInstance(
                        new URL[] { jar.toURI().toURL() },
                        getClass().getClassLoader()
                );

                PluginConfig config = getPluginConfig(jar);

                loadMainClass(config, loader, parentClass);

                logger.debug("Successfully loaded plugin \"" + config.getName() + "\" v" + config.getVersion() + " by " + authorText(config.getAuthors()));
            } catch (ClassNotFoundException | FileNotFoundException e) {
                // There might be multiple JARs in the directory so keep looking
                //continue;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException e) {
                logger.debug(e.getMessage(), e);
            }
        }
        //throw new ClassNotFoundException("Class " + classpath + " wasn't found in directory " + pluginsDir);
    }

    public Plugin LoadClass(String file, Class<C> parentClass) {
        File pluginFile = getJarPath(file);
        System.out.println(pluginFile.getAbsolutePath());

        if (!pluginFile.exists()) {
            return null;
        }

        try {
            ClassLoader loader = URLClassLoader.newInstance(
                    new URL[]{pluginFile.toURI().toURL()},
                    getClass().getClassLoader()
            );

            PluginConfig config = getPluginConfig(pluginFile);

            Plugin plugin = loadMainClass(config, loader, parentClass);

            logger.debug("Successfully loaded plugin \"" + config.getName() + "\" v" + config.getVersion() + " by " + authorText(config.getAuthors()));

            return plugin;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException e) {
            logger.debug(e.getMessage(), e);
        }
        //throw new ClassNotFoundException("Class " + classpath + " wasn't found in directory " + pluginsDir);

        return null;
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

    private Plugin loadMainClass(PluginConfig config, ClassLoader loader, Class<C> parentClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class<?> clazz = Class.forName(config.getMain(), true, loader);
        Class<? extends C> newClass = clazz.asSubclass(parentClass);

        String pluginName = config.getName();

        // Apparently its bad to use Class.newInstance, so we use
        // newClass.getConstructor() instead
        Constructor constructor = newClass.getConstructor();
        Plugin plugin = (Plugin) constructor.newInstance();

        newClass.getMethod("init", File.class, PluginConfig.class).invoke(plugin, new File("plugins/" + config.getName()), config);

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
}
