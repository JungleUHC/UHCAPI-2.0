package fr.altaks.uhcapi2.listeners;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Random;

public class HostListener implements Listener {

    private Main main;

    public HostListener(Main main){
        this.main = main;
    }

    @EventHandler
    public void onHostJoins(PlayerJoinEvent event){
        if(main.getGameManager().getHost() == null){
            Main.logDev("HostListener#onHostJoin : Host was null");
            main.getGameManager().setHost(event.getPlayer());
            Main.logDebug("Host has joined the game, setting host to " + event.getPlayer().getName());
        }
    }

    /**
     * Re-attributes the host role to another player if the host quits the game.
     */
    @EventHandler
    public void onHostQuits(PlayerQuitEvent event){
        if(main.getGameManager().getHost() == null) {
            Main.logDev("HostListener#onHostQuits : Host was null");
            return;
        }
        if(main.getGameManager().getHost().equals(event.getPlayer())){
            Main.logDev("HostListener#onHostQuits : Host quit branch");

            if(main.getGameManager().getGameState() == GameState.WAITING_TO_START){
                Main.logDebug("Host has left the game, clearing the inventory of the host");
                main.getGameManager().getHost().getInventory().clear();
            }

            // If there's no player left, set the host to null
            if(main.getServer().getOnlinePlayers().size() - 1 == 0){
                Main.logDebug("Host has left the game, no players left, setting host to null");
                main.getGameManager().setHost(null);
                return;
            }

            // If there's cohosts, randomize between them, else between all players
            if(!main.getGameManager().getCoHosts().isEmpty()){
                Main.logDebug("Host has left the game, randomizing between cohosts");
                main.getGameManager().setHost(main.getGameManager().getCoHosts().get(new Random().nextInt(main.getGameManager().getCoHosts().size())));
            } else {
                // Determine a random player within the player list
                Main.logDebug("Host has left the game, and there's no cohost, randomizing between all players");
                int randomPlayerIndex = (int) Math.floor(Math.random() * main.getServer().getOnlinePlayers().size());
                main.getGameManager().setHost(
                        main.getServer().getOnlinePlayers().toArray(
                                new Player[main.getServer().getOnlinePlayers().size()]
                        )[randomPlayerIndex]
                );
            }
        }
    }

    /**
     * Prevents the host from moving the host items in its inventory.
     * @param event The InventoryClickEvent
     */
    @EventHandler
    public void onHostsTriesToMoveHostItems(InventoryClickEvent event){
        if(event == null) return;
        if(this.main.getGameManager().getGameState() != GameState.WAITING_TO_START) return;
        if(this.main.getGameManager().getHost().equals(event.getWhoClicked()) || this.main.getGameManager().getCoHosts().contains(event.getWhoClicked())){
            if(event.getCurrentItem() == null) return;
            if(event.getCurrentItem().equals(this.main.getGameManager().getHostMenuItem()) || event.getCurrentItem().equals(this.main.getGameManager().getHostGameLaunchItem())){
                if(!event.isLeftClick() || event.isShiftClick()) {
                    // Cancel all types of clicks if they aren't non-shift left clicks
                    event.setCancelled(true);
                }
            }
        }
    }


    /**
     * Prevents the host from dropping the host items.
     * @param event The InventoryClickEvent
     */
    @EventHandler
    public void onHostsTriesToDropHostItems(PlayerDropItemEvent event){
        if(this.main.getGameManager().getGameState() != GameState.WAITING_TO_START) return;
        if(this.main.getGameManager().getHost().equals(event.getPlayer()) || this.main.getGameManager().getCoHosts().contains(event.getPlayer())){
            if(event.getItemDrop().getItemStack().equals(this.main.getGameManager().getHostMenuItem()) || event.getItemDrop().getItemStack().equals(this.main.getGameManager().getHostGameLaunchItem())){
                event.setCancelled(true);
            }
        }
    }

}
