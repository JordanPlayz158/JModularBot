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
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {
    public Logger logger;

    public PluginLoader(Logger logger) {
        this.logger = logger;
    }

    public void LoadClass(File directory) throws ClassNotFoundException {
        File pluginsDir = new File(System.getProperty("user.dir") + "/" + directory);
        String classpath;

        if(pluginsDir.listFiles() == null) {
            return;
        }

        for (File jar : pluginsDir.listFiles()) {
            try {
                ClassLoader loader = URLClassLoader.newInstance(
                        new URL[] { jar.toURI().toURL() },
                        getClass().getClassLoader()
                );

                PluginConfig config = getPluginConfig(jar);
                classpath = config.getMain();

                loadMainClass(classpath, loader, config.getName());

                logger.debug("Successfully loaded plugin \"" + config.getName() + "\" v" + config.getVersion() + " by " + authorText(config.getAuthors()));
            } catch (ClassNotFoundException | FileNotFoundException e) {
                // There might be multiple JARs in the directory so keep looking
                continue;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException e) {
                logger.debug(e.getMessage(), e);
            }
        }
        //throw new ClassNotFoundException("Class " + classpath + " wasn't found in directory " + pluginsDir);
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

    private void loadMainClass(String classpath, ClassLoader loader, String pluginName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class<?> clazz = Class.forName(classpath, true, loader);
        // Apparently its bad to use Class.newInstance, so we use
        // newClass.getConstructor() instead
        Constructor constructor = clazz.getConstructor();
        PluginManager.addPlugin(pluginName, constructor.newInstance());
    }

    private String authorText(String[] authors) {
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
