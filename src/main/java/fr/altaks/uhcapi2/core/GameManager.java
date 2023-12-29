package fr.altaks.uhcapi2.core;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.controllers.*;
import fr.altaks.uhcapi2.views.HostMainMenu;
import fr.altaks.uhcapi2.views.parameters.ParametersMenu;
import fr.altaks.uhcapi2.views.roles.RolesAmountsMainMenu;
import fr.altaks.uhcapi2.views.timers.TimersRolesMenu;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

public class GameManager {

    private Main main;
    private GameState gameState = GameState.WAITING_TO_START;

    private final HostMainMenu hostMainMenu;

    private RolesAmountsMainMenu rolesAmountsMainMenu;
    private TimersRolesMenu timersRolesMenu;
    private ParametersMenu parametersMenu;

    private WorldsController worldsController;
    private GameController gameController;
    private ScenariosController scenariosController;
    private TimersController timersController;
    private ParametersController parametersController;
    private RolesAmountController rolesAmountController;

    public GameManager(Main main){
        this.main = main;
        this.hostMainMenu = new HostMainMenu(this, main);

        this.worldsController = new WorldsController(main);
        this.gameController = new GameController(this, main);
        this.scenariosController = new ScenariosController(main);
        this.timersController = new TimersController(this, main);
        this.parametersController = new ParametersController(main);
        this.rolesAmountController = new RolesAmountController(main);
    }

    private Player host;

    public GameMode getChosenGameMode() {
        return chosenGameMode;
    }

    public void setChosenGameMode(GameMode chosenGameMode) {
        this.chosenGameMode = chosenGameMode;
    }

    private GameMode chosenGameMode = null;

    public ArrayList<Player> getCoHosts() {
        return coHosts;
    }

    private final ArrayList<Player> coHosts = new ArrayList<>();

    public ItemStack getHostMenuItem() {
        return hostMenuItem;
    }

    public boolean canModifyRules(Player player){
        return this.gameState != GameState.STARTED && (player.equals(this.host) || this.coHosts.contains(player));
    }

    private final ItemStack hostMenuItem = new ItemBuilder(Material.NETHER_STAR)
            .enchant(Enchantment.DURABILITY, 1)
            .flags(ItemFlag.HIDE_ENCHANTS)
            .name(ChatColor.RED + "\u00BB Configuration de la partie \u00AB")
            .addLore(
                    "",
                    ChatColor.GRAY + "Utilise moi pour gérer la partie.",
                    ChatColor.GRAY + "Accessible par l'hôte et les co-hôtes uniquement !"
            )
            .build();

    public ItemStack getHostGameLaunchItem() {
        return hostGameLaunchItem;
    }

    private final ItemStack hostGameLaunchItem = new ItemBuilder(Material.BEACON)
            .name(ChatColor.GREEN + "\u00BB Lancer la partie \u00AB")
            .enchant(Enchantment.DURABILITY, 1)
            .flags(ItemFlag.HIDE_ENCHANTS)
            .addLore(
                    "",
                    ChatColor.GRAY + "Utilise moi pour lancer la partie.",
                    ChatColor.GRAY + "Accessible par l'hôte uniquement !"
            )
            .build();

    public Player getHost() {
        return host;
    }

    public void giveHostItems(Player player){
        Main.logDebug("Giving host items to " + player.getName());
        player.getInventory().setItem(4, hostMenuItem);
        player.getInventory().setItem(6, hostGameLaunchItem);
        player.getInventory().setHeldItemSlot(4);
    }

    public void giveCoHostItems(Player player){
        Main.logDebug("Giving co-host items to " + player.getName());
        player.getInventory().setItem(0, hostMenuItem);
        player.getInventory().setHeldItemSlot(0);
    }

    /**
     * Sets the host of the game and gives him the host items if needed.
     * @param host The new host of the game.
     */
    public void setHost(Player host) {
        // clear old host inventory from host items if the game is in WAITING mode
        if(this.gameState == GameState.WAITING_TO_START){
            if(this.host != null){
                this.host.getInventory().clear();
                Main.logDebug("Clearing the old host inventory");
            }
        }
        this.host = host;
        if(this.host != null){
            // add host items to new host inventory if the game is in WAITING mode
            if(this.gameState == GameState.WAITING_TO_START) giveHostItems(host);
            host.sendMessage(Main.MSG_PREFIX + "Vous êtes l'hôte de la partie !");
        }
    }

    /**
     * Adds a co-host to the co-hosts list.
     * @param newCoHost The co-host to add.
     */
    public void addCoHost(Player newCoHost){
        this.coHosts.add(newCoHost);
        newCoHost.sendMessage(Main.MSG_PREFIX + "Vous êtes maintenant co-hôte de la partie !");
        if(this.gameState == GameState.WAITING_TO_START) giveCoHostItems(newCoHost);
    }

    /**
     * Removes a co-host from the co-hosts list.
     * @param coHostToRemove The co-host to remove.
     * @return true if the co-host was successfully removed, false otherwise.
     */
    public boolean removeCoHost(Player coHostToRemove){
        if(this.coHosts.contains(coHostToRemove)){
            this.coHosts.remove(coHostToRemove);
            if(this.gameState == GameState.WAITING_TO_START) coHostToRemove.getInventory().clear();
            coHostToRemove.sendMessage(Main.MSG_PREFIX + "Vous n'êtes plus co-hôte de la partie !");
            return true;
        }
        return false;
    }

    public void loadGame(){
        if(this.gameState != GameState.WAITING_TO_START) return;
        if(this.chosenGameMode == null) return;

        File configFile = new File(main.getDataFolder(), "other-config.yml");
        try {
            Plugin plugin = Bukkit.getPluginManager().loadPlugin(this.chosenGameMode.getPluginFile());
            plugin.saveDefaultConfig();

            FileConfiguration config = plugin.getConfig();

            // inject controllers infos
            this.timersController.onConfigLoad(config);
            this.parametersController.onConfigLoad(config);
            this.rolesAmountController.onConfigLoad(config);

            plugin.saveConfig();

        } catch (InvalidPluginException | InvalidDescriptionException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load game mode plugin " + this.chosenGameMode.getPluginFile().getName(), e);
            return;
        }
    }

    // Starts the game
    public void start(){
        // Changing gamestate
        this.gameState = GameState.STARTED;

        // Reset player state
        for(Player player : Bukkit.getOnlinePlayers()){
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setExp(0);
            player.setLevel(0);
            player.setFireTicks(0);
            player.setFallDistance(0);
            player.setTotalExperience(0);
            player.setExhaustion(0);
            // remove all potion effects
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        }

        this.gameController.onGameStart();
        this.worldsController.onGameStart();
        this.scenariosController.onGameStart();
        this.timersController.onGameStart();
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public HostMainMenu getHostMainMenu() {
        return hostMainMenu;
    }

    public RolesAmountsMainMenu getRolesAmountsMainMenu() {
        return rolesAmountsMainMenu;
    }

    public void setRolesAmountsMainMenu(RolesAmountsMainMenu rolesAmountsMainMenu) {
        this.rolesAmountsMainMenu = rolesAmountsMainMenu;
    }

    public TimersRolesMenu getTimersRolesMenu() {
        return timersRolesMenu;
    }

    public void setTimersRolesMenu(TimersRolesMenu timersRolesMenu) {
        this.timersRolesMenu = timersRolesMenu;
    }

    public WorldsController getWorldsController() {
        return worldsController;
    }

    public GameController getGameController() {
        return gameController;
    }

    public ScenariosController getScenariosController() {
        return scenariosController;
    }

    public TimersController getTimersController() {
        return timersController;
    }

    public ParametersController getParametersController() {
        return parametersController;
    }

    public ParametersMenu getParametersMenu() {
        return parametersMenu;
    }

    public void setParametersMenu(ParametersMenu parametersMenu) {
        this.parametersMenu = parametersMenu;
    }

    public RolesAmountController getRolesAmountController() {
        return rolesAmountController;
    }
}
