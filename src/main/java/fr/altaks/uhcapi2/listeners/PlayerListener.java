package fr.altaks.uhcapi2.listeners;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.GameState;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

public class PlayerListener implements Listener {

    private Main main;

    public PlayerListener(Main main){
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoinsTheServer(PlayerJoinEvent event){
        event.setJoinMessage("[" + ChatColor.GREEN + "+" + ChatColor.RESET + "] " + event.getPlayer().getName());
        event.getPlayer().setGameMode(GameMode.SURVIVAL);
        if(main.getGameManager().getGameState() == GameState.WAITING_TO_START){
            event.getPlayer().setExp(0);
            event.getPlayer().setLevel(0);
            for(PotionEffect effect : event.getPlayer().getActivePotionEffects()) {
                event.getPlayer().removePotionEffect(effect.getType());
            }
        }
    }

    @EventHandler
    public void onPlayerTakesDamageDuringWaiting(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player)) return;
        if(main.getGameManager().getGameState() == GameState.WAITING_TO_START){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLoosesHungerDuringWaiting(FoodLevelChangeEvent event){
        if(!(event.getEntity() instanceof Player)) return;
        if(main.getGameManager().getGameState() == GameState.WAITING_TO_START){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerTriesToDestroyBlockDuringWaiting(BlockBreakEvent event){
        if(main.getGameManager().getGameState() == GameState.WAITING_TO_START){
            event.setCancelled(true);
            event.getPlayer().sendMessage(Main.MSG_PREFIX + ChatColor.RED + "Vous ne pouvez pas casser de blocs avant le début de la partie !");
        }
    }

    @EventHandler
    public void onPlayerTriesToPlaceBlockDuringWaiting(BlockPlaceEvent event){
        if(main.getGameManager().getGameState() == GameState.WAITING_TO_START){
            event.setCancelled(true);
            event.getPlayer().sendMessage(Main.MSG_PREFIX + ChatColor.RED + "Vous ne pouvez pas poser de blocs avant le début de la partie !");
        }
    }

    @EventHandler
    public void onPlayerQuitsTheServer(PlayerQuitEvent event){
        event.setQuitMessage("[" + ChatColor.RED + "+" + ChatColor.RESET + "] " + event.getPlayer().getName());
    }
}
