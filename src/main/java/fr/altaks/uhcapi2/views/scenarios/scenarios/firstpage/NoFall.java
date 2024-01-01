package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class NoFall  implements Scenario {

    public String getName() {
        return "No Fall";
    }

    @Override
    public String getDescription() {
        return "Empêche les joueurs de subir des dégâts dus aux chutes.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.CHAINMAIL_BOOTS);
    }

    @Override
    public int getSlot() {
        return 24;
    }

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerFallDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL){
            event.setCancelled(true);
        }
    }
}