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

public class HasteyBabies implements Scenario {

    public String getName() {
        return "Hastey Babies";
    }

    @Override
    public String getDescription() {
        return "Les outils fabriqués reçoivent l'enchantement Efficiency 1 et Unbreaking 1, offrant un léger avantage en vitesse.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.STONE_PICKAXE);
    }

    @Override
    public int getSlot() {
        return 15;
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
            event.getCurrentItem().addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
            event.getCurrentItem().addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        }
    }

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }
}