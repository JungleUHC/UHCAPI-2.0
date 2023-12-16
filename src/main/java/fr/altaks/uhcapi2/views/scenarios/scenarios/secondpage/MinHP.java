package fr.altaks.uhcapi2.views.scenarios.scenarios.secondpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class MinHP implements Scenario {

    public String getName() {
        return "Min HP";
    }

    @Override
    public String getDescription() {
        return "Empêche les joueurs de descendre en dessous de 3 cœurs avant l'activation du PvP.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.POTION);
    }

    @Override
    public int getSlot() {
        return 70;
    }

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onPlayerTakesDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            if(player.getWorld().getPVP()) return;
            if(player.getHealth() - event.getFinalDamage() <= 6){
                event.setCancelled(true);
                player.setHealth(6);
            }
        }
    }
}