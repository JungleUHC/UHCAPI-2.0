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
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class DirectToInventory  implements Scenario {

    private String ICON_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmM3NjI4ZTg5N2NiNGM5MzEyZjgwMmYyOGMyZWM0NjZkN2U3MjFlYzM3MDEwMzc4Y2M0NWRkMmRjNjg4MTBjMyJ9fX0=";

    public String getName() {
        return "Direct To Inventory";
    }

    @Override
    public String getDescription() {
        return "Les minerais minés vont directement dans l'inventaire des joueurs";
    }

    @Override
    public ItemBuilder getIcon() {
        return HeadBuilder.of(ICON_VALUE);
    }

    @Override
    public int getSlot() {
        return 29;
    }

    private Main main;

    private final HashMap<Material, Material> materialConversionMap = new HashMap<>();

    public DirectToInventory(){
        materialConversionMap.put(Material.IRON_ORE, Material.IRON_INGOT);
        materialConversionMap.put(Material.GOLD_ORE, Material.GOLD_INGOT);

        materialConversionMap.put(Material.SAND, Material.GLASS);
    }

    private final ArrayList<Block> blockPlacedByPlayers = new ArrayList<>();

    @EventHandler
    public void onPlayerPlacesBlockEvent(BlockPlaceEvent event) {
        if(event.getPlayer().getGameMode() == GameMode.SURVIVAL) blockPlacedByPlayers.add(event.getBlockPlaced());
    }

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
        this.main = main;
    }

    private final ArrayList<Material> ores = new ArrayList<>(Arrays.asList(
            Material.COAL_ORE,
            Material.IRON_ORE,
            Material.GOLD_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.LAPIS_ORE,
            Material.REDSTONE_ORE,
            Material.QUARTZ_ORE
    ));

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerBreaksOre(BlockBreakEvent event){
        if(blockPlacedByPlayers.contains(event.getBlock())) {
            blockPlacedByPlayers.remove(event.getBlock());
        } else if(ores.contains(event.getBlock().getType())){
            Collection<ItemStack> drops = event.getBlock().getDrops(event.getPlayer().getItemInHand());
            if(event.getPlayer().getItemInHand() != null) event.getPlayer().getItemInHand().setDurability((short) (event.getPlayer().getItemInHand().getDurability() + 1));
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);

            if(main.getGameManager().getScenariosController().getEnabledScenarioInstances().containsKey(CutClean.class)){
                for(ItemStack drop : drops) if(materialConversionMap.containsKey(drop.getType())) {
                    drop.setType(materialConversionMap.get(drop.getType()));
                }
            }

            if(!blockPlacedByPlayers.contains(event.getBlock())) {
                if(main.getGameManager().getScenariosController().getEnabledScenarioInstances().containsKey(DoubleOres.class)){
                    for(ItemStack drop : drops) drop.setAmount(drop.getAmount() * 2);
                }

                if(main.getGameManager().getScenariosController().getEnabledScenarioInstances().containsKey(TripleOres.class)){
                    for(ItemStack drop : drops) drop.setAmount(drop.getAmount() * 3);
                }
            }

            for(ItemStack drop : drops){
                event.getPlayer().getInventory().addItem(drop);
            }
        }
    }
}