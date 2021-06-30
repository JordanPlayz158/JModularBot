package xyz.jordanplayz158.jmodularbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Getter;
import me.jordanplayz158.utils.FileUtils;
import xyz.jordanplayz158.jmodularbot.commands.ListCommand;
import xyz.jordanplayz158.jmodularbot.commands.PluginCommand;
import xyz.jordanplayz158.jmodularbot.events.CommandListener;
import xyz.jordanplayz158.jmodularbot.managers.PluginManager;
import xyz.jordanplayz158.jmodularbot.plugin.Plugin;
import xyz.jordanplayz158.jmodularbot.storage.Config;
import xyz.jordanplayz158.jmodularbot.managers.CommandManager;
import xyz.jordanplayz158.jmodularbot.managers.EventManager;
import xyz.jordanplayz158.jmodularbot.plugin.PluginLoader;
import me.jordanplayz158.utils.Initiate;
import me.jordanplayz158.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

@Getter
public class JModularBot {
    public static final JModularBot instance = new JModularBot();
    private Config config;
    public static Logger logger;
    private JDA jda;

    // Files and Folders
    private final File configFile = new File("config.yml");
    private final File pluginsFolder = new File("plugins");

    PluginLoader loader;
    private CommandHandler commandHandler;

    public static void main(String[] args) throws LoginException, IOException {
        FileUtils.copyFile(new File("config.yml.bot"), instance.configFile);

        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        instance.config = objectMapper.readValue(instance.configFile, Config.class);
        Config config = instance.config;

        // Initiates the log
        logger = Initiate.log(Level.toLevel(config.getLogLevel()));

        logger.debug("Config File has been copied!");
        logger.debug("A new instance of Config has been created!");
        logger.debug("Json has been loaded from config.yml!");
        logger.debug("Logger has been initialized!");

        if (instance.pluginsFolder.mkdir()) {
            logger.debug("\"plugins\" folder has been created!");
        } else if(instance.pluginsFolder.exists()) {
            logger.debug("\"plugins\" folder already exists!");
        } else {
            logger.debug("\"plugins\" folder was unable to be created and does not exist!");
        }

        final String token = config.getToken();
        // Valid discord tokens are 59 characters in length
        if(token.length() < 59) {
            logger.fatal("The token you have provided is invalid!");
            shutdown(1);
        }

        JDABuilder jdaBuilder = JDABuilder.createLight(token);

        if(!config.getPrefix().isEmpty()) {
            logger.debug("Prefix is not empty!");
            jdaBuilder.enableIntents(GatewayIntent.GUILD_MESSAGES);
            logger.debug("Enabling necessary intents for CommandsListener!");
            jdaBuilder.addEventListeners(new CommandListener());
            logger.debug("CommandsListener has been added as an event listener!");
        }

        instance.loader = new PluginLoader();
        logger.debug("PluginLoader successfully instantiated!\n");

        for (File pluginJar : Objects.requireNonNull(instance.pluginsFolder.listFiles())) {
            instance.loader.LoadClass(pluginJar.getPath());
            System.out.println();
        }

        instance.loader.loadWaitingOnDependenciesPlugins();

        logger.debug("Plugins successfully loaded!");

        EventManager.registerEvents(jdaBuilder);

        instance.jda = jdaBuilder
                .setActivity(Activity.of(Activity.ActivityType.valueOf(config.getActivity().get("type").toUpperCase()), config.getActivity().get("name")))
                .build();

        logger.debug("The bot has successfully been initialized and logged in!");

        new EventManager(instance.jda);

        logger.debug("EventManager has been instantiated and supplied with jda instance!");

        instance.commandHandler = new CommandHandler();
        logger.debug("CommandHandler has been initialized!");
        CommandManager.addCommands(null, new PluginCommand());

        if(logger.getLevel() == Level.DEBUG) {
            CommandManager.addCommands(null, new ListCommand());
        }
        logger.debug("Commands have been successfully added to CommandManager!");


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();

        while(!line.equals("exit")) {
            line = br.readLine();
        }

        shutdown(0);
    }

    public static EmbedBuilder getTemplate(User author) {
        return new EmbedBuilder()
                .setFooter("JModularBot | " + MessageUtils.nameAndTag(author));
    }

    public static void shutdown(int shutdownCode) {
        if(instance.jda != null) {
            List<Plugin> plugins = PluginManager.getPluginMap().values().stream().toList();
            plugins.forEach(plugin -> PluginManager.unloadPlugin(plugin.getPluginConfig().getName()));
            instance.jda.shutdown();
        }

        logger.debug("Plugins: " + PluginManager.getPluginMap());
        logger.debug("Events: " + EventManager.getEventsMap());
        logger.debug("Commands: " + CommandManager.getCommandMap());

        System.exit(shutdownCode);
    }
}
