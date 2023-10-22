package fr.altaks.uhcapi2.core;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.util.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class GameManager {

    private Main main;
    private GameState gameState = GameState.WAITING_TO_START;

    public GameManager(Main main){
        this.main = main;
    }

    private Player host;

    public ArrayList<Player> getCoHosts() {
        return coHosts;
    }

    private final ArrayList<Player> coHosts = new ArrayList<>();

    public ItemStack getHostMenuItem() {
        return hostMenuItem;
    }

    private final ItemStack hostMenuItem = new ItemManager.ItemBuilder(Material.BLAZE_ROD)
            .setDisplayName(ChatColor.RED + "\u00BB Configurer la partie \u00AB")
            .setLore(
                    "",
                    ChatColor.GRAY + "Utilise moi pour accéder à la",
                    ChatColor.GRAY + "configuration de la partie !"
            )
            .build();

    public ItemStack getHostGameLaunchItem() {
        return hostGameLaunchItem;
    }

    private final ItemStack hostGameLaunchItem = new ItemManager.ItemBuilder(Material.BEACON)
            .setDisplayName(ChatColor.GREEN + "\u00BB Lancer la partie \u00AB")
            .setLore(
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
}
