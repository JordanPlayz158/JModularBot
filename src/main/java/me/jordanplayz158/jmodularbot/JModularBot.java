package me.jordanplayz158.jmodularbot;

import lombok.Getter;
import me.jordanplayz158.jmodularbot.commands.HelpCommand;
import me.jordanplayz158.jmodularbot.events.CommandsListener;
import me.jordanplayz158.jmodularbot.json.Config;
import me.jordanplayz158.utils.FileUtils;
import me.jordanplayz158.utils.Initiate;
import me.jordanplayz158.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;

@Getter
public class JModularBot {
    @Getter
    private static final JModularBot instance = new JModularBot();
    private Config config;
    private Logger logger;
    private JDA jda;

    // Files and Folders
    private final File configFile = new File("config.json");
    private final File pluginsFolder = new File("plugins");

    public static void main(String[] args) throws LoginException, IOException, InterruptedException {
        //Copy config
        FileUtils.copyFile(instance.configFile);
        instance.config = new Config(instance.configFile);
        instance.config.loadJson();

        // Initiates the log
        instance.logger = Initiate.log(Level.toLevel(instance.config.getLogLevel()));

        instance.pluginsFolder.mkdir();

        final String token = instance.config.getJson().get("token").getAsString();
        // Checks if the Token is 1 character or less and if so, tell the person they need to provide a token
        if(token.length() <= 1) {
            instance.logger.fatal("You have to provide a token in your config file!");
            System.exit(1);
        }

        // Token and activity is read from and set in the config.json
        JDABuilder jdaBuilder = JDABuilder.createLight(token);

        if(!instance.config.getPrefix().isEmpty()) {
            jdaBuilder.enableIntents(GatewayIntent.GUILD_MESSAGES);
            jdaBuilder.addEventListeners(new CommandsListener());
        }

        instance.jda = jdaBuilder
                .setActivity(Activity.of(instance.config.getActivityType(), instance.config.getActivityName()))
                .build()
                .awaitReady();

        CommandHandler.addCommands(new HelpCommand());
    }

    public EmbedBuilder getTemplate(User author) {
        return new EmbedBuilder()
                .setFooter("Faster | " + MessageUtils.nameAndTag(author));
    }
}
