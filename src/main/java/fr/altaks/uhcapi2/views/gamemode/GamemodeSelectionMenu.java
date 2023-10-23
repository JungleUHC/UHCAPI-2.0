package fr.altaks.uhcapi2.views.gamemode;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.GameMode;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GamemodeSelectionMenu extends FastInv {

    private HashMap<ItemStack, Pair<File, File>> availableGameModes = new HashMap<>();
    private ItemStack chosenGameModeItem = null;

    private Main main;

    public GamemodeSelectionMenu(Main main) {
        super(6*9, "Selection du mode de jeu");

        // load all gameModes from the filesystem
        // path is in the config file on gameModes-path key

        File serverFolder = main.getDataFolder().getParentFile();
        File gameModesFolder = new File(serverFolder + File.separator + main.getConfig().getString("game-modes-path"));

        Main.logDebug("GameModes folder: " + gameModesFolder.getAbsolutePath());

        HashMap<File, File> gameModes = searchForGameModes(gameModesFolder);

        Main.logDebug("GameModes found : " + gameModes.size());
        for(Map.Entry<File, File> entry : gameModes.entrySet()){

            Main.logDebug("GameMode: " + entry.getKey().getName() + " - Configurator: " + entry.getValue().getName());
            // Load infos from YAML file

            FileConfiguration configurator = YamlConfiguration.loadConfiguration(entry.getValue());
            String pluginName = configurator.getString("game-name");
            String pluginDescription = configurator.getString("game-description");

            // add the gameMode item to the inventory
            ArrayList<String> pluginDescriptionAsLore = new ArrayList<>();
            pluginDescriptionAsLore.add("");
            pluginDescription = ChatColor.translateAlternateColorCodes('&', pluginDescription);
            for(String line : pluginDescription.split("\n")){
                pluginDescriptionAsLore.add(ChatColor.GRAY + line);
            }
            ItemStack gameModeItem = new ItemBuilder(Material.PAPER)
                    .name(ChatColor.YELLOW + pluginName)
                    .lore(pluginDescriptionAsLore)
                    .build();

            addItem(gameModeItem);
            availableGameModes.put(gameModeItem, Pair.of(entry.getKey(), entry.getValue()));
        }

        this.main = main;

    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        // checks to avoid NPEs
        if(event.getClickedInventory() == null || event.getView().getBottomInventory() == event.getClickedInventory()) return;
        if(event.getCurrentItem() == null) return;

        // when an item is clicked, print the chosen gamemode file name to the console
        Pair<File, File> gameMode = availableGameModes.get(event.getCurrentItem());
        if(gameMode == null) throw new RuntimeException("GameMode not found");
        Main.logDebug("GameMode selected: " + gameMode.getLeft().getName() + " with configurator: " + gameMode.getRight().getName());

        // if there was already a game mode, remove the enchantment on the item
        if(this.main.getGameManager().getChosenGameMode() != null){
            availableGameModes.remove(chosenGameModeItem, gameMode);
            chosenGameModeItem.removeEnchantment(Enchantment.DURABILITY);
            availableGameModes.put(chosenGameModeItem, gameMode);
        }

        // load the gameMode
        this.main.getGameManager().setChosenGameMode(getGameModeFromConfigurator(gameMode.getLeft(), gameMode.getRight()));
        chosenGameModeItem = event.getCurrentItem();

        // update hashmap for item
        availableGameModes.remove(chosenGameModeItem, gameMode);
        chosenGameModeItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        availableGameModes.put(chosenGameModeItem, gameMode);

        // Send a message to the player
        event.getWhoClicked().sendMessage(Main.MSG_PREFIX + ChatColor.GREEN + "Mode de jeu choisi : " + ChatColor.YELLOW + this.main.getGameManager().getChosenGameMode().getPluginName());
    }

    private GameMode getGameModeFromConfigurator(File pluginFile, File configurator){
        if(main.getGameManager().getChosenGameMode() != null && main.getGameManager().getChosenGameMode().getPluginFile().equals(pluginFile)){
            Main.logDebug("Selected GameMode is already loaded, no need to reload it");
            return main.getGameManager().getChosenGameMode();
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configurator);

        String pluginName = config.getString("game-name");
        String pluginDescription = config.getString("game-description");

        // Loading roles amounts

        HashMap<GameMode.Role, Integer> playersPerRole = loadPlayersPerRole(config);
        Main.logDebug("Loaded " + playersPerRole.size() + " roles profiles for game mode \"" + pluginName + "\" (" + pluginFile.getName() + ")");

        if(Main.isDevMode) for(Map.Entry<GameMode.Role, Integer> role : playersPerRole.entrySet()){
            Main.logDev(" | Role: " + role.getKey().getName() + " - Amount: " + role.getValue());
        }

        // Loading game options

        HashMap<GameMode.GameOption, Object> gameOptions = loadGameOptions(config);
        Main.logDebug("Loaded " + gameOptions.size() + " game options for game mode \"" + pluginName + "\" (" + pluginFile.getName() + ")");

        if(Main.isDevMode) for(Map.Entry<GameMode.GameOption, Object> gameOption : gameOptions.entrySet()){
            Main.logDev(" | GameOption: " + gameOption.getKey().getName() + " - Value: " + gameOption.getValue() + " - Unit: " + gameOption.getKey().getType());
        }

        // Load roles timers

        HashMap<GameMode.RoleTimer, Long> rolesTimers = loadRolesTimers(config);
        Main.logDebug("Loaded " + rolesTimers.size() + " roles timers for game mode \"" + pluginName + "\" (" + pluginFile.getName() + ")");

        if(Main.isDevMode) for(Map.Entry<GameMode.RoleTimer, Long> roleTimer : rolesTimers.entrySet()){
            Main.logDev(" | Timer: " + roleTimer.getKey().getName() + " - Value: " + roleTimer.getValue() + " - Unit: " + roleTimer.getKey().getUnit());
        }

        // Load roles parameters

        HashMap<GameMode.RoleParameter, Object> rolesParameters = loadRolesParameters(config);
        Main.logDebug("Loaded " + rolesParameters.size() + " roles parameters for game mode \"" + pluginName + "\" (" + pluginFile.getName() + ")");

        if(Main.isDevMode) for(Map.Entry<GameMode.RoleParameter, Object> roleParameter : rolesParameters.entrySet()){
            Main.logDev(" | Parameter: " + roleParameter.getKey().getName() + " - Value: " + roleParameter.getValue() + " - Type: " + roleParameter.getKey().getType());
        }

        // debug-mode-path: <path>
        String debugModePath = config.getString("debug-mode-path");

        return new GameMode(pluginName, pluginDescription, pluginFile, playersPerRole, gameOptions, rolesTimers, rolesParameters, debugModePath);
    }


    private HashMap<GameMode.RoleParameter, Object> loadRolesParameters(FileConfiguration config) {
        // roles-parameters:
        //   <role-parameter-name>:
        //     name: <parameter-name>
        //     unit: <integer/percentage>
        //     value: <default-value>
        HashMap<GameMode.RoleParameter, Object> rolesParameters = new HashMap<>();
        for(String roleParameterPath : config.getConfigurationSection("roles-parameters").getKeys(false)){
            String roleParameterName = config.getString("roles-parameters." + roleParameterPath + ".name");
            String unit = config.getString("roles-parameters." + roleParameterPath + ".unit");
            Object value = config.get("roles-parameters." + roleParameterPath + ".value");
            GameMode.GameModeOptionType gameModeOptionType = GameMode.GameModeOptionType.valueOf(unit.toUpperCase());
            rolesParameters.put(new GameMode.RoleParameter(roleParameterName, roleParameterPath, gameModeOptionType), value);
        }
        return rolesParameters;
    }

    private HashMap<GameMode.RoleTimer, Long> loadRolesTimers(FileConfiguration config) {
        // roles-timers:
        //   <role-timer-name>:
        //     name: <role-timer-name>
        //     unit: <seconds/minutes>
        //     value: <default-value>
        HashMap<GameMode.RoleTimer, Long> rolesTimers = new HashMap<>();
        for(String roleTimerPath : config.getConfigurationSection("roles-timers").getKeys(false)){
            String roleTimerName = config.getString("roles-timers." + roleTimerPath + ".name");
            String unit = config.getString("roles-timers." + roleTimerPath + ".unit");
            long value = config.getLong("roles-timers." + roleTimerPath + ".value");
            GameMode.TimeType timeType = GameMode.TimeType.valueOf(unit.toUpperCase());
            rolesTimers.put(new GameMode.RoleTimer(roleTimerName, roleTimerPath, timeType), value);
        }
        return rolesTimers;
    }

    private HashMap<GameMode.GameOption, Object> loadGameOptions(FileConfiguration config) {
        // game-options
        //   <option-name>:
        //     description: <description>
        //     unit: <integer>
        //     value: <default-value>
        HashMap<GameMode.GameOption, Object> gameOptions = new HashMap<>();
        for(String optionName : config.getConfigurationSection("game-options").getKeys(false)){
            String name = config.getString("game-options." + optionName + ".name");
            String description = config.getString("game-options." + optionName + ".description");
            String unit = config.getString("game-options." + optionName + ".unit");
            Object value = config.get("game-options." + optionName + ".value");
            GameMode.GameModeOptionType gameModeOptionType = GameMode.GameModeOptionType.valueOf(unit.toUpperCase());
            gameOptions.put(new GameMode.GameOption(name, description, unit, gameModeOptionType), value);
        }
        return gameOptions;
    }

    private HashMap<GameMode.Role, Integer> loadPlayersPerRole(FileConfiguration config) {
        // roles-default-amount:
        //  <role-name-path>:
        //    role: <role-name>
        //    value: <amount>
        HashMap<GameMode.Role, Integer> playersPerRole = new HashMap<>();
        for(String rolePath : config.getConfigurationSection("roles-default-amount").getKeys(false)){
            String roleName = config.getString("roles-default-amount." + rolePath + ".role");
            int amount = config.getInt("roles-default-amount." + rolePath + ".value");
            playersPerRole.put(new GameMode.Role(roleName, rolePath), amount);
        }
        return playersPerRole;
    }

    private static HashMap<File, File> searchForGameModes(File gameModesFolder) {
        HashMap<File, File> gameModesToConfigurator = new HashMap<>();

        for(File file : Objects.requireNonNull(gameModesFolder.listFiles())){
            if(file.getName().endsWith(".jar")){
                // check for a yml file with the same name
                String gameModeName = file.getName().replace(".jar", "");
                File gameModeConfigurator = new File(gameModesFolder + "/" + gameModeName + ".yml");
                if(gameModeConfigurator.exists()){
                    gameModesToConfigurator.put(file, gameModeConfigurator);
                }
            }
        }
        return gameModesToConfigurator;
    }
}
