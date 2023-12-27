package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TripleOres implements Scenario {

    private String ICON_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTAxODQzZWM0M2YwODhjOTYzZmZjM2UyZjcxYzY2ZTMxNTU5NDNiMTc3YTFhMzU5ODJiMTIwZjZmNjQ4MjJiYyJ9fX0=";

    public String getName() {
        return "Triple Ores";
    }

    @Override
    public String getDescription() {
        return "Chaque minerai miné en donne trois.";
    }

    @Override
    public ItemBuilder getIcon() {
        return HeadBuilder.of(ICON_VALUE).amount(3);
    }

    @Override
    public int getSlot() {
        return 32;
    }

    private Main main;

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
        this.main = main;
    }

    private final ArrayList<Block> blockPlacedByPlayers = new ArrayList<>();
    private final List<Material> ores = Arrays.asList(
            Material.COAL_ORE,
            Material.IRON_ORE,
            Material.GOLD_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.LAPIS_ORE,
            Material.REDSTONE_ORE,
            Material.QUARTZ_ORE
    );

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerPlacesBlockEvent(BlockPlaceEvent event) {
        if(event.getPlayer().getGameMode() == GameMode.SURVIVAL) blockPlacedByPlayers.add(event.getBlockPlaced());
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerBreaksOreEvent(BlockBreakEvent event) {
        if(blockPlacedByPlayers.contains(event.getBlock())) {
            blockPlacedByPlayers.remove(event.getBlock());
            return;
        }
        if(!main.getGameManager().getScenariosController().getEnabledScenarioInstances().containsKey(DirectToInventory.class)){
            // double the amount of the block if it's an ore
            for(ItemStack item : event.getBlock().getDrops(event.getPlayer().getItemInHand())){
                if(ores.contains(event.getBlock().getType())) {
                    // drop the item triple
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
                }
            }
        }

    }
}