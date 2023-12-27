package fr.altaks.uhcapi2;

import fr.altaks.uhcapi2.commands.ConfigLoad;
import fr.altaks.uhcapi2.commands.StartCommand;
import fr.altaks.uhcapi2.commands.ValidateCommand;
import fr.altaks.uhcapi2.commands.WorldTPCommand;
import fr.altaks.uhcapi2.core.GameManager;
import fr.altaks.uhcapi2.core.IPluginCommand;
import fr.altaks.uhcapi2.core.util.worldmanip.DynamicClassFunctions;
import fr.altaks.uhcapi2.listeners.HostListener;
import fr.altaks.uhcapi2.listeners.PlayerListener;
import fr.mrmicky.fastinv.FastInvManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class of the plugin.
 * This class is responsible for the complete load and execution of the plugin.
 * It handles the plugin loading, enabling and disabling tasks.
 * @author altaks
 * @since 1.0
 * @version 1.0
 * @see JavaPlugin
 */
public class Main extends JavaPlugin {

    /*
         Default constructors needed by MockBukkit in order to be able to test the plugin.
         DO NOT REMOVE !
     */
    public Main()
    {
        super();
    }
    protected Main(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file)
    {
        super(loader, description, dataFolder, file);
    }

    /**
     * The name of the plugin that appears in the console, and must be used in player chat messages.
     */
    public static final String PLUGIN_NAME = "UHC API";

    /**
     * The prefix that appears for player intended messages.
     */
    public static final String MSG_PREFIX = ChatColor.GRAY        + "[" + ChatColor.AQUA          + PLUGIN_NAME + ChatColor.GRAY        + "] \u00BB " + ChatColor.RESET;

    /**
     * The color codes for the prefixes to apply colors in console.
     */
    public static final String INFO_CONSOLE_PREFIX   = "[Info] \u00BB ";
    public static final String DEBUG_CONSOLE_PREFIX  = "[Debug] \u00BB ";
    public static final String DEVLOG_CONSOLE_PREFIX = "[Devlog] \u00BB ";

    /**
     * Values that indicate if the plugin is in debug mode, development mode or neither of them.
     */
    public static boolean isDebugging = false;
    public static boolean isDevMode = false;

    /**
     * The default logger of the plugin. This should not be used directly, use the logInfo, logDebug and logDev methods instead.
     */
    public static Logger defaultLogger = Bukkit.getLogger();

    /**
     * A HashMap of registered listening commands instances, mapped by their command name.
     */
    private final HashMap<String, IPluginCommand> registeredListeningCommands = new HashMap<>();

    /**
     * The number of registered listeners and commands during the loading {@code onEnable} phase.
     */
    private int successfullyLoadedListenersCount = 0, failedLoadListenersCount = 0, commandsCount = 0;

    /**
     * A list of classes that failed to load as listeners.
     */
    private final ArrayList<Class<?>> failedToLoadListeners = new ArrayList<>();

    private GameManager gameManager;

    @Override
    public void onEnable() {

        // Make sure the plugin.yml exists in the server's plugin folder
        saveDefaultConfig();

        // Load isDebugging and isDevMode from plugin.yml
        loadLoggerConfig();

        // Register commands and listeners if they are.
        IPluginCommand[] commands = new IPluginCommand[]{
            new WorldTPCommand(), new ValidateCommand(this), new StartCommand(this), new ConfigLoad(this)
        };
        Listener[] listeners = new Listener[]{
            new HostListener(this), new PlayerListener(this)
        };

        // Automatically register commands and listeners.
        for(IPluginCommand command : commands){
            // Save the listening commands instances in a HashMap for later use.
            if(command.listensEvents()) registeredListeningCommands.put(command.getCommandName(), command);

            // Register command class instance as CommandExecutor and TabCompleter
            getCommand(command.getCommandName()).setExecutor(command);
            getCommand(command.getCommandName()).setTabCompleter(command);
            commandsCount++;
        }
        for(Listener listener : listeners){
            // Register class instance as Listener
            getServer().getPluginManager().registerEvents(listener, this);
            successfullyLoadedListenersCount++;
        }
        for(IPluginCommand listener : registeredListeningCommands.values()){
            if(listener instanceof Listener) { // Make sure the command implements Listener
                // Register class instance as Listener
                getServer().getPluginManager().registerEvents((Listener) listener, this);
                successfullyLoadedListenersCount++;
            } else {
                logDev("Command " + listener.getCommandName() + " is registered as a listening command but does not implement Listener.");
                failedToLoadListeners.add(listener.getClass());
                failedLoadListenersCount++;
            }
        }

        // Misceallaneous registrations
        this.gameManager = new GameManager(this);
        FastInvManager.register(this);

        // Inject NMS unload methods into the server
        if(!DynamicClassFunctions.setPackages()) {
            Main.logDev("NMS/OBC package could not be detected, using " + DynamicClassFunctions.nmsPackage + " and " + DynamicClassFunctions.obcPackage);
        }
        DynamicClassFunctions.setClasses();
        DynamicClassFunctions.setMethods();
        DynamicClassFunctions.setFields();


        logDebug("Registered " + commandsCount + " commands and " + successfullyLoadedListenersCount + " listeners successfully.");
        if(failedLoadListenersCount > 0) {
            StringJoiner joiner = new StringJoiner(", ");
            for(Class<? extends Object> clazz : failedToLoadListeners){
                joiner.add(clazz.getSimpleName());
            }
            logDev("Failed to register " + failedLoadListenersCount + " listeners: " + joiner.toString());
        }
    }

    /**
     * Load isDebugging and isDevMode from plugin.yml if they are set.
     * Else the values stay the same no matter the context.
     */
    public void loadLoggerConfig(){
        if(getConfig().isSet("logging.debug-mode")) {
            isDebugging = getConfig().getBoolean("logging.debug-mode");
        }
        if(getConfig().isSet("logging.dev-mode")){
            isDevMode = getConfig().getBoolean("logging.dev-mode");
        }
    }

    /**
     * Log a message to the console with the MSG_PREFIX prefix
     * @param msg The message to send to the console.
     */
    public static void logInfo(String msg){
        defaultLogger.log(Level.INFO, INFO_CONSOLE_PREFIX + msg);
    }

    /**
     * Log a message to the console with the DBG_PREFIX prefix, can be seen only if logging.debug-mode is true in the plugin.yml file.
     * @param msg The information/message to send to the console.
     */
    public static void logDebug(String msg){
        if(isDebugging) defaultLogger.log(Level.WARNING,DEBUG_CONSOLE_PREFIX + msg);
    }

    /**
     * Log a message to the console with the DVL_PREFIX prefix, can be seen only if logging.dev-mode is true in the plugin.yml file.
     * @param msg The stacktrace/information/message to send to the console.
     */
    public static void logDev(String msg){
        if(isDevMode) defaultLogger.log(Level.WARNING,DEVLOG_CONSOLE_PREFIX + msg);
    }

    /**
     * Get the Map of registered listening commands instances, mapped by their command name.
     * @return The Map of registered listening commands instances, mapped by their command name.
     */
    public HashMap<String, IPluginCommand> getRegisteredListeningCommands() {
        return registeredListeningCommands;
    }


    public GameManager getGameManager() {
        return gameManager;
    }
}
