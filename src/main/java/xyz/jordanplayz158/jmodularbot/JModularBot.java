package xyz.jordanplayz158.jmodularbot;

import lombok.Getter;
import xyz.jordanplayz158.jmodularbot.commands.HelpCommand;
import xyz.jordanplayz158.jmodularbot.commands.PluginCommand;
import xyz.jordanplayz158.jmodularbot.events.CommandListener;
import xyz.jordanplayz158.jmodularbot.json.Config;
import xyz.jordanplayz158.jmodularbot.managers.CommandManager;
import xyz.jordanplayz158.jmodularbot.managers.EventManager;
import xyz.jordanplayz158.jmodularbot.plugin.Plugin;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Getter
public class JModularBot {
    public static final JModularBot instance = new JModularBot();
    private Config config;
    public static Logger logger;
    private JDA jda;

    // Files and Folders
    private final File configFile = new File("config.json");
    private final File pluginsFolder = new File("plugins");

    PluginLoader<Plugin> loader;
    private CommandHandler commandHandler;

    public static void main(String[] args) throws LoginException, IOException {
        instance.copyFile(instance.configFile);
        instance.config = new Config(instance.configFile);
        Config config = instance.config;
        config.load();

        // Initiates the log
        logger = Initiate.log(Level.toLevel(instance.config.getLogLevel()));

        logger.debug("""
                Config File has been copied!
                A new instance of Config has been created!
                Json has been loaded from config.json!
                Logger has been initialized! (all of the following have worked properly otherwise you wouldn't see this message)
                """);

        if (instance.pluginsFolder.mkdir()) {
            logger.debug("\"plugins\" folder has been created!");
        } else if(instance.pluginsFolder.exists()) {
            logger.debug("\"plugins\" folder already exists!");
        } else {
            logger.debug("\"plugins\" folder was unable to be created and does not exist!");
        }

        final String token = config.json.get("token").getAsString();
        // Valid discord tokens are 59 characters in length
        if(token.length() < 59) {
            logger.fatal("The token you have provided is invalid!");
            System.exit(1);
        }

        JDABuilder jdaBuilder = JDABuilder.createLight(token);

        if(!config.getPrefix().isEmpty()) {
            logger.debug("Prefix is not empty!");
            jdaBuilder.enableIntents(GatewayIntent.GUILD_MESSAGES);
            logger.debug("Enabling necessary intents for CommandsListener!");
            jdaBuilder.addEventListeners(new CommandListener());
            logger.debug("CommandsListener has been added as an event listener!");
        }

        instance.loader = new PluginLoader<>(logger);
        logger.debug("PluginLoader successfully instantiated!");
        instance.loader.LoadClass(instance.pluginsFolder, Plugin.class);
        logger.debug("Plugins successfully loaded!");

        EventManager.registerEvents(jdaBuilder);

        instance.jda = jdaBuilder
                .setActivity(Activity.of(config.getActivityType(), config.getActivityName()))
                .build();

        logger.debug("The bot has successfully been initialized and logged in!");

        new EventManager(instance.jda);

        logger.debug("EventManager has been instantiated and supplied with jda instance!");

        instance.commandHandler = new CommandHandler();
        logger.debug("CommandHandler has been initialized!");
        CommandManager.addCommands(null, new HelpCommand(), new PluginCommand());
        logger.debug("Commands have been successfully added to CommandManager!");
    }

    /**
     * Copies the file from the jar to the specified directory
     * @param name The name of the file inside of the jar
     */
    private void copyFile(File name) throws IOException {
        InputStream fileSrc = Thread.currentThread().getContextClassLoader().getResourceAsStream(name.getPath());
        if (name.createNewFile()) {
            assert fileSrc != null;

            Files.copy(fileSrc, name.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static EmbedBuilder getTemplate(User author) {
        return new EmbedBuilder()
                .setFooter("JModularBot | " + MessageUtils.nameAndTag(author));
    }
}
