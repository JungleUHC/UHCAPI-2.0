package fr.altaks.uhcapi2.core;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.controllers.GameController;
import fr.altaks.uhcapi2.controllers.WorldsController;
import fr.altaks.uhcapi2.views.HostMainMenu;
import fr.altaks.uhcapi2.views.roles.RolesAmountsMainMenu;
import fr.altaks.uhcapi2.views.timers.TimersRolesMenu;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GameManager {

    private Main main;
    private GameState gameState = GameState.WAITING_TO_START;

    private final HostMainMenu hostMainMenu;

    private RolesAmountsMainMenu rolesAmountsMainMenu;
    private TimersRolesMenu timersRolesMenu;

    private WorldsController worldsController;
    private GameController gameController;

    public GameManager(Main main){
        this.main = main;
        this.hostMainMenu = new HostMainMenu(this, main);

        this.worldsController = new WorldsController(main);
        this.gameController = new GameController(main);
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

    private final ItemStack hostMenuItem = new ItemBuilder(Material.BLAZE_ROD)
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
            if(this.gameState == GameState.WAITING_TO_START){
                this.host.getInventory().setItem(3, hostMenuItem);
                this.host.getInventory().setItem(5, hostGameLaunchItem);
                this.host.getInventory().setHeldItemSlot(4);
                Main.logDebug("Giving host items to the new host");
            }
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
        newCoHost.getInventory().setItem(0, hostMenuItem);
        newCoHost.getInventory().setHeldItemSlot(0);
    }

    /**
     * Removes a co-host from the co-hosts list.
     * @param coHostToRemove The co-host to remove.
     * @return true if the co-host was successfully removed, false otherwise.
     */
    public boolean removeCoHost(Player coHostToRemove){
        if(this.coHosts.contains(coHostToRemove)){
            this.coHosts.remove(coHostToRemove);
            coHostToRemove.sendMessage(Main.MSG_PREFIX + "Vous êtes maintenant co-hôte de la partie !");
            return true;
        }
        return false;
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
}
