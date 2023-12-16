package fr.altaks.uhcapi2.views.scenarios.scenarios.secondpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class Unbreakable implements Scenario {

    public String getName() {
        return "Unbreakable";
    }

    @Override
    public String getDescription() {
        return "Rend tous les outils et armures incassables, éliminant la nécessité de réparation.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.GOLD_AXE)
                .durability((short)11);
    }

    @Override
    public int getSlot() {
        return 64;
    }

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemTakesDamage(PlayerItemDamageEvent event){
        event.getItem().setDurability((short)0);
        event.setDamage(0);
        ItemMeta meta = event.getItem().getItemMeta();
        meta.spigot().setUnbreakable(true);
        event.getItem().setItemMeta(meta);
        event.setCancelled(true);
    }
}