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

public class VeinMiner implements Scenario {

    private String ICON_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFhNzg1OTE2ZDJkMTdjYTBlYTJhZDIzZDgwMjQ3YzdjNTAyMTQ0MzkwM2JiYWI3YjI0Yjc5MzRiNmEzNjFhYiJ9fX0=";

    public String getName() {
        return "Vein Miner";
    }

    @Override
    public String getDescription() {
        return "Lorsqu'un minerai est miné, tous les blocs de minerai connectés sont également casser simultanément.";
    }

    @Override
    public ItemBuilder getIcon() {
        return HeadBuilder.of(ICON_VALUE);
    }

    @Override
    public int getSlot() {
        return 30;
    }

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    private ArrayList<Material> oreTypes = new ArrayList<>(
            Arrays.asList(
                    Material.COAL_ORE,
                    Material.IRON_ORE,
                    Material.GOLD_ORE,
                    Material.DIAMOND_ORE,
                    Material.EMERALD_ORE,
                    Material.LAPIS_ORE,
                    Material.REDSTONE_ORE,
                    Material.QUARTZ_ORE
            )
    );

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerMinesOre(BlockBreakEvent event){
        if(oreTypes.contains(event.getBlock().getType())){
            // Broken block is an ore block, start a recursion algorithm to find all the ore blocks nearby
            veinMine(event.getPlayer(), event.getBlock(), event.getBlock().getType(), 0);
        }
    }

    private void veinMine(Player breaker, Block block, Material firstMinedMaterial,  int recursionDepth){
        if(recursionDepth == 5) return; // Prevent infinite recursion (should never happen)
        if(block == null || block.getType() != firstMinedMaterial) return;

        block.breakNaturally(breaker.getItemInHand());

        // reduce the durability of the item in hand
        breaker.getInventory().getItemInHand().setDurability((short) (breaker.getInventory().getItemInHand().getDurability() + 1));

        for(BlockFace face : BlockFace.values()){
            veinMine(breaker, block.getRelative(face), firstMinedMaterial, recursionDepth + 1);
        }
    }
}