package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class GigaDrill implements Scenario {

    public String getName() {
        return "Giga Drill";
    }

    @Override
    public String getDescription() {
        return "Tous les outils fabriqués sont dotés d'Efficiency 10 et Unbreaking 10, améliorant considérablement l'efficacité et la durabilité.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.DIAMOND_PICKAXE);
    }

    private final ArrayList<Material> tools = new ArrayList<>(Arrays.asList(
        Material.WOOD_PICKAXE,
        Material.STONE_PICKAXE,
        Material.IRON_PICKAXE,
        Material.GOLD_PICKAXE,
        Material.DIAMOND_PICKAXE,

        Material.WOOD_AXE,
        Material.STONE_AXE,
        Material.IRON_AXE,
        Material.GOLD_AXE,
        Material.DIAMOND_AXE,

        Material.WOOD_SPADE,
        Material.STONE_SPADE,
        Material.IRON_SPADE,
        Material.GOLD_SPADE,
        Material.DIAMOND_SPADE,

        Material.WOOD_HOE,
        Material.STONE_HOE,
        Material.IRON_HOE,
        Material.GOLD_HOE,
        Material.DIAMOND_HOE,

        Material.SHEARS,
        Material.FLINT_AND_STEEL
    ));

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerCraftsItem(CraftItemEvent event){
        if(tools.contains(event.getRecipe().getResult().getType())){
            // crafted item is a tool
            event.getCurrentItem().addUnsafeEnchantment(Enchantment.DIG_SPEED, 10);
            event.getCurrentItem().addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        }
    }

    @Override
    public int getSlot() {
        return 13;
    }

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }
}