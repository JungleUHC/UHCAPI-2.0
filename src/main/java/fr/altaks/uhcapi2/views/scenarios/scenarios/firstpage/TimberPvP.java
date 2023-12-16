package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class TimberPvP implements Scenario {

    private String ICON_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2YxMzQ2MDkyYzgwZDNkYjIxN2VmZTRjOTM2OTY5MWU2MWM4YWZjMWIyODc0MWZhNTA0ODJjOTJjOWZkM2QxOCJ9fX0=";

    public String getName() {
        return "Timber";
    }

    @Override
    public String getDescription() {
        return "Permet de casser instantanément les troncs d'arbres jusqu'à l'activation du PvP, facilitant la collecte de bois.";
    }

    private final ArrayList<Material> woodTypes = new ArrayList<>(
            Arrays.asList(
                    Material.LOG,
                    Material.LOG_2
            )
    );

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerBreaksWood(BlockBreakEvent event){
        if(event.getPlayer().getWorld().getPVP()) {
            return; // if pvp is enabled, remove timber ability
        }
        if(woodTypes.contains(event.getBlock().getType())){
            // Broken block is a wood block, start a recursion algorithm to find all the wood blocks nearby
            timberNearbyWood(event.getPlayer(), event.getBlock(), 0);
        }
    }

    private void timberNearbyWood(Player breaker, Block block, int recursionDepth){
        if(recursionDepth == 5) return; // Prevent infinite recursion (should never happen)
        if(block == null || !woodTypes.contains(block.getType())) return;

        block.breakNaturally(breaker.getItemInHand());

        // reduce the durability of the item in hand
        breaker.getInventory().getItemInHand().setDurability((short) (breaker.getInventory().getItemInHand().getDurability() + 1));

        for(BlockFace face : BlockFace.values()){
            timberNearbyWood(breaker, block.getRelative(face), recursionDepth + 1);
        }
    }


    @Override
    public ItemBuilder getIcon() {
        return HeadBuilder.of(ICON_VALUE);
    }

    @Override
    public int getSlot() {
        return 12;
    }

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }
}