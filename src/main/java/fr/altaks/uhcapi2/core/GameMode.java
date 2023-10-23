package fr.altaks.uhcapi2.core;

import fr.altaks.uhcapi2.Main;

import java.io.File;
import java.util.HashMap;

public class GameMode {

    private String pluginName;

    public String getPluginName() {
        return pluginName;
    }

    public String getPluginDescription() {
        return pluginDescription;
    }

    public File getPluginFile() {
        return pluginFile;
    }

    public HashMap<Role, Integer> getPlayersPerRole() {
        return playersPerRole;
    }

    public HashMap<GameOption, Object> getGameOptions() {
        return gameOptions;
    }

    public HashMap<RoleTimer, Long> getRolesTimers() {
        return rolesTimers;
    }

    public HashMap<RoleParameter, Object> getRolesParameters() {
        return rolesParameters;
    }

    public String getDebugModePath() {
        return debugModePath;
    }

    private String pluginDescription;

    private File pluginFile;

    private HashMap<GameMode.Role, Integer> playersPerRole;
    private HashMap<GameMode.GameOption, Object> gameOptions;
    private HashMap<GameMode.RoleTimer, Long> rolesTimers;
    private HashMap<GameMode.RoleParameter, Object> rolesParameters;

    private String debugModePath;

    public GameMode(String name, String pluginDescription, File pluginFile, HashMap<Role, Integer> playersPerRole, HashMap<GameOption, Object> gameOptions, HashMap<RoleTimer, Long> rolesTimers, HashMap<RoleParameter, Object> rolesParameters, String debugModePath){
        this.pluginName = name;
        this.pluginDescription = pluginDescription;
        this.pluginFile = pluginFile;

        this.playersPerRole = playersPerRole;
        this.gameOptions = gameOptions;
        this.rolesTimers = rolesTimers;
        this.rolesParameters = rolesParameters;

        this.debugModePath = debugModePath;
    }

    public void load(Main main){
        // load plugin
        // copy default config file
        // rewrite options in config file
        // if the api is in dev/debug mode, make the plugin go in dev/debug mode as well.
        // enable the plugin
    }

    public void start(){
        // trigger the GameStartEvent for the game to really start
    }

    public enum GameModeOptionType {

        INTEGER,
        DECIMAL,
        PERCENTAGE,
        BOOLEAN;

    }

    public static class GameOption {

        private String path;
        private String name;
        private String description;

        private GameModeOptionType type;

        public GameOption(String name, String path, String description, GameModeOptionType type){
            this.name = name;
            this.path = path;
            this.description = description;
            this.type = type;
        }

        public String getPath() {
            return path;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public GameModeOptionType getType() {
            return type;
        }
    }

    public static class Role {

        private String name;
        private String path;

        public Role(String name, String path){
            this.name = name;
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }
    }

    public static class RoleTimer {
        private String name;
        private String path;

        private TimeType unit;

        public RoleTimer(String name, String path, TimeType unit){
            this.name = name;
            this.path = path;
            this.unit = unit;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }


        public TimeType getUnit() {
            return unit;
        }

    }

    public enum TimeType {
        SECONDS, MINUTES
    }

    public static class RoleParameter {

        private String name;
        private String path;

        private GameModeOptionType type;

        public RoleParameter(String name, String path, GameModeOptionType type){
            this.name = name;
            this.path = path;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        public GameModeOptionType getType() {
            return type;
        }
    }
}
