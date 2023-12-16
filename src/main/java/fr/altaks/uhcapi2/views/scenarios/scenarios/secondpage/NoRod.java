package fr.altaks.uhcapi2.views.scenarios.scenarios.secondpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class NoRod  implements Scenario {

    public String getName() {
        return "No Rod";
    }

    @Override
    public String getDescription() {
        return "Interdit le craft/utilisation de la canne à pêche";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.FISHING_ROD);
    }

    @Override
    public int getSlot() {
        return 73;
    }

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onPlayerCraftRod(CraftItemEvent event){
        if(event.getRecipe().getResult().getType() == Material.FISHING_ROD){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerUsesRod(PlayerInteractEvent event){
        if(event.getItem() != null && event.getItem().getType() == Material.FISHING_ROD){
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cVous ne pouvez pas utiliser de canne à pêche");
        }
    }
}