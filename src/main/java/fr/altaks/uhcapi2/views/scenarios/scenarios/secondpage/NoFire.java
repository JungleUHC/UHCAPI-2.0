package fr.altaks.uhcapi2.views.scenarios.scenarios.secondpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class NoFire implements Scenario {

    public String getName() {
        return "No Fire";
    }

    @Override
    public String getDescription() {
        return "Annule les dégâts causés par le feu, garantissant la sécurité des joueurs.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.FLINT_AND_STEEL);
    }

    @Override
    public int getSlot() {
        return 65;
    }

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onPlayerTakesFireDamage(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player)) return;
        if(event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.LAVA){
            event.setCancelled(true);
        }
    }
}