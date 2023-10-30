package fr.altaks.uhcapi2.core;

import fr.altaks.uhcapi2.Main;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GameMode {

    private final String pluginName;

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

    private HashMap<GameMode.GameTeam, ArrayList<Role>> rolesFromTeam;
    private HashMap<GameMode.Role, Integer> playersPerRole;

    private HashMap<GameMode.GameOption, Object> gameOptions;
    private HashMap<GameMode.RoleTimer, Long> rolesTimers;
    private HashMap<GameMode.RoleParameter, Object> rolesParameters;

    private String debugModePath;

    public GameMode(String name, String pluginDescription, File pluginFile, HashMap<Role, Integer> playersPerRole, HashMap<GameTeam, ArrayList<Role>> rolesOfTeams, HashMap<GameOption, Object> gameOptions, HashMap<RoleTimer, Long> rolesTimers, HashMap<RoleParameter, Object> rolesParameters, String debugModePath){
        this.pluginName = name;
        this.pluginDescription = pluginDescription;
        this.pluginFile = pluginFile;

        this.playersPerRole = playersPerRole;
        this.gameOptions = gameOptions;
        this.rolesTimers = rolesTimers;
        this.rolesParameters = rolesParameters;

        this.debugModePath = debugModePath;
        this.rolesFromTeam = rolesOfTeams;
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

    public HashMap<GameTeam, ArrayList<Role>> getRolesFromTeam() {
        return rolesFromTeam;
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
        private GameTeam team;

        public Role(String name, String path, GameTeam team){
            this.name = name;
            this.path = path;
            this.team = team;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        public GameTeam getTeam() {
            return team;
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
        SECONDS("secondes"),
        MINUTES("minutes");

        public String humanName;

        TimeType(String humanName){
            this.humanName = humanName;
        }
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

    public static class GameTeam {

        private final String id;
        private final String name;
        private final ItemStack item;
        private final String description;

        public GameTeam(String id, String name, ItemStack item, String description){
            this.id = id;
            this.name = name;
            this.item = item;
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public ItemStack getItem() {
            return item;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }
    }
}
