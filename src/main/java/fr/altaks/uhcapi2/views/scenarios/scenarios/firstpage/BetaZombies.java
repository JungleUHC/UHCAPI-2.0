package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.util.LoreUtil;
import fr.altaks.uhcapi2.core.util.MinecraftDecimalFormat;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BetaZombies implements Scenario {

    public String getName() {
        return "Beta Zombies";
    }

    @Override
    public String getDescription() {
        return "Les zombies ont une chance de laisser tomber des plumes, avec un taux de drop configurable.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.FEATHER);
    }

    @Override
    public int getSlot() {
        return 25;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    private int featherDropChance = 5;

    @SuppressWarnings("unused")
    @EventHandler
    public void onZombiesDies(EntityDeathEvent event){
        if(event.getEntityType() == EntityType.ZOMBIE){
            int percentage = (int)(Math.random() * 100);
            if(percentage <= featherDropChance){
                event.getDrops().add(new ItemStack(Material.FEATHER));
            }
        }
    }

    @Override
    public void processClick(InventoryClickEvent event) {
        // determine modifier from click
        int modifier = (event.isShiftClick() ? -1 : 1);
        this.featherDropChance += modifier;

        // floor the value to 0
        if(this.featherDropChance < 0) {
            this.featherDropChance = 0;
        }

        // update the lore of the item
        LoreUtil.updateLore(event.getCurrentItem(), -2, ChatColor.YELLOW + "Valeur actuelle : " + ChatColor.GREEN + featherDropChance + " %");
    }

    @Override
    public String[] getConfigurationLore() {
        return new String[]{
                "",
                ChatColor.GRAY + "Clic droit       : +1 %",
                ChatColor.GRAY + "Shift clic droit : -1 %",
                "",
                ChatColor.YELLOW + "Valeur actuelle : " + ChatColor.GREEN + featherDropChance + " %",
                ""
        };
    }
}