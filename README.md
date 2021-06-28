# JModularBot
This is a Java Modular Discord Bot made to be modular with plugin loading and unloading while running.


## Making a plugin

### Java Version
The java version used for JModularBot is Java 16 (more specifically, I use AdoptOpenJDK 16)


### Dependency

#### Maven
```xml
<repository>
    <id>JordanPlayz158</id>
    <url>https://maven.jordanplayz158.xyz/snapshots</url>
</repository>

<dependency>
    <groupId>xyz.jordanplayz158</groupId>
    <artifactId>JModularBot</artifactId>
    <version>VERSION</version>
</dependency>
```
Current version is 0.0.5

#### Gradle (If someone knows gradle could you do this section?)


### Plugin Class
The start of the plugin happens in the main class, it needs to extend Plugin <br>
**After** the class has initialized you will have access to the `pluginConfig` and the `dataFolder` variable (Plan to implement a better method).


### Registering Events
`EventManager.addEvents(this, new Listener());` <br>
Valid listeners extend `ListenerAdapter` or implements `EventListener`


### Creating a command
In order to create a command you must have a class extend Command and pass the values asked, to the extended Classes class constructor (most can be null, only ones that are labelled @NotNull cannot be null), and you must override the onCommand method and that is where you will put your code for what the command is going to do. You get provided with the event, and the args (commands included at args[0]), so you have no restrictions on what your command can do.


### Registering Commands
`CommandManager.addCommands(MainClass.instance, new Command())` <br>
Valid commands extend `Command` and it is recommended to run this method in the ReadyListener if you wish to use roles for authentication (jda needs to be initialized in order to get Roles from discord)


### Plugin yml
The plugin.yml file is the file at the base of the jar read from to know what to instantiate what the name is (for unloading), what the version is, and the main class is (for loading)

There are 5 required fields

| Variable    | Value  | Explanation |
|:-----------:|:------:|:-----------:|
| name        | String | Self explanatory
| version     | String | Self explanatory
| description | String | Self explanatory
| authors     | String | Self explanatory
| main        | String | Path to main class inside jar (ex. `xyz.jordanplayz158.kickplugin.KickPlugin`)


### Examples
[Plugin Repository](https://github.com/JordanPlayz158/JModularBot-Plugins/)