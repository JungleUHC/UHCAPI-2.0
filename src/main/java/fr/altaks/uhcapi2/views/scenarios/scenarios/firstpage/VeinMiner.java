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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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

    private Main main;

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
        this.main = main;
    }

    private final ArrayList<Material> oreTypes = new ArrayList<>(
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

    private final HashMap<Material, Material> materialConversionMap = new HashMap<>();

    public VeinMiner(){
        materialConversionMap.put(Material.IRON_ORE, Material.IRON_INGOT);
        materialConversionMap.put(Material.GOLD_ORE, Material.GOLD_INGOT);

        materialConversionMap.put(Material.SAND, Material.GLASS);

        materialConversionMap.put(Material.POTATO, Material.BAKED_POTATO);
        materialConversionMap.put(Material.RAW_BEEF, Material.COOKED_BEEF);
        materialConversionMap.put(Material.RAW_CHICKEN, Material.COOKED_CHICKEN);
        materialConversionMap.put(Material.RAW_FISH, Material.COOKED_FISH);
        materialConversionMap.put(Material.PORK, Material.GRILLED_PORK);
        materialConversionMap.put(Material.RABBIT, Material.COOKED_RABBIT);
        materialConversionMap.put(Material.MUTTON, Material.COOKED_MUTTON);
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerMinesOre(BlockBreakEvent event){
        if(oreTypes.contains(event.getBlock().getType())){
            // Broken block is an ore block, start a recursion algorithm to find all the ore blocks nearby
            veinMine(event.getPlayer(),
                    event.getBlock(),
                    event.getBlock().getType(),
                    0,
                    main.getGameManager().getScenariosController().getEnabledScenarioInstances().containsKey(DirectToInventory.class),
                    main.getGameManager().getScenariosController().getEnabledScenarioInstances().containsKey(CutClean.class),
                    main.getGameManager().getScenariosController().getEnabledScenarioInstances().containsKey(DoubleOres.class),
                    main.getGameManager().getScenariosController().getEnabledScenarioInstances().containsKey(TripleOres.class)
            );
        }
    }

    private void veinMine(Player breaker, Block block, Material firstMinedMaterial, int recursionDepth, boolean directToToInventory, boolean cutClean, boolean doubleOres, boolean tripleOres){
        if(recursionDepth == 5) return; // Prevent infinite recursion (should never happen)
        if(block == null || block.getType() != firstMinedMaterial) return;

        if(directToToInventory){
            if(cutClean){
                for(ItemStack drop : block.getDrops(breaker.getItemInHand())) {
                    if(materialConversionMap.containsKey(drop.getType())) {
                        drop.setType(materialConversionMap.get(drop.getType()));
                    }
                    if(doubleOres && oreTypes.contains(block.getType())) {
                        drop.setAmount(drop.getAmount() * 2);
                    } else if(tripleOres && oreTypes.contains(block.getType())) {
                        drop.setAmount(drop.getAmount() * 3);
                    }
                    breaker.getInventory().addItem(drop);
                }
            } else {
                for(ItemStack drop : block.getDrops(breaker.getItemInHand())) {
                    if(doubleOres && oreTypes.contains(block.getType())) {
                        drop.setAmount(drop.getAmount() * 2);
                    } else if(tripleOres && oreTypes.contains(block.getType())) {
                        drop.setAmount(drop.getAmount() * 3);
                    }
                    breaker.getInventory().addItem(drop);
                }
            }
            block.setType(Material.AIR);
        } else {
            block.breakNaturally();
        }

        // reduce the durability of the item in hand
        breaker.getInventory().getItemInHand().setDurability((short) (breaker.getInventory().getItemInHand().getDurability() + 1));

        for(BlockFace face : BlockFace.values()){
            veinMine(breaker, block.getRelative(face), firstMinedMaterial, recursionDepth + 1, directToToInventory, cutClean, doubleOres, tripleOres);
        }
    }
}