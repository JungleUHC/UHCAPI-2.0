package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class SafeMiner implements Scenario {

    public String getName() {
        return "Safe Miner";
    }

    @Override
    public String getDescription() {
        return "Les joueurs ne subissent pas de dégâts de feu sous la couche 32.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.LAVA_BUCKET);
    }

    @Override
    public int getSlot() {
        return 19;
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerTakesDamageFromFire(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            if(event.getEntity().getLocation().getY() >= 32) return;
            if(event.getCause() == EntityDamageEvent.DamageCause.LAVA || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK){
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }
}