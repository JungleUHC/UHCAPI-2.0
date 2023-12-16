package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class IronMan implements Scenario {

    private String ICON_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTJiNzE0NGRiM2E5ZmQ4OGMzNzk3ZGNmOThiNDZlYjUzODFiOWQ1ZmRhNzdlMzQ4MTU3MDM3MTc3YWM4OGUifX19";

    public String getName() {
        return "Iron Man";
    }

    @Override
    public String getDescription() {
        return "Récompense le dernier joueur à ne pas prendre de dégâts avec 2 pommes d'or.";
    }

    @Override
    public ItemBuilder getIcon() {
        return HeadBuilder.of(ICON_VALUE);
    }

    @Override
    public int getSlot() {
        return 34;
    }

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.getGameMode() != GameMode.SPECTATOR){
                players.add(player);
            }
        }
    }

    private final Set<Player> players = new HashSet<>();
    private boolean hasBeenGiven = false;

    @EventHandler
    public void onPlayerTakesDamage(EntityDamageEvent event){
        if(hasBeenGiven) return;
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            players.remove(player);
            if(players.size() == 1){
                Player winner = players.iterator().next();
                winner.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 2));
                winner.sendMessage(Main.MSG_PREFIX + "Vous êtes le dernier joueur à ne pas avoir pris de dégâts, vous gagnez 2 pommes d'or.");
                hasBeenGiven = true;
            }
        }
    }
}