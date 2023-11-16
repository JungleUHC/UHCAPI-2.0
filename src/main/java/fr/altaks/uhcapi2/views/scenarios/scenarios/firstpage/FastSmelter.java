package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class FastSmelter implements Scenario {

    private String ICON_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTNiZjBiODg1OWExZTU3ZjNhYmQ2MjljMGM3MzZlNjQ0ZTgxNjUxZDRkZTAzNGZlZWE0OWY4ODNmMDBlODJiMCJ9fX0=";

    public String getName() {
        return "Fast Smelter";
    }

    @Override
    public String getDescription() {
        return "Les minerais placés dans les fours sont cuits trois fois plus rapidement, accélérant la production de ressources.";
    }

    @Override
    public ItemBuilder getIcon() {
        return HeadBuilder.of(ICON_VALUE);
    }

    @Override
    public int getSlot() {
        return 20;
    }

    private int smeltingMultiplierPercentage = 300;
    private ArrayList<Furnace> furnaces = new ArrayList<>();

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);

        final int period = 20;

        new BukkitRunnable() {

            @Override
            public void run() {
                ArrayList<Furnace> toRemove = new ArrayList<>();
                for(Furnace furnace : furnaces){
                    if(!furnace.isPlaced() || (furnace.getBlock().getType() != Material.FURNACE && furnace.getBlock().getType() != Material.BURNING_FURNACE)) {
                        toRemove.add(furnace);
                        continue;
                    }
                    if(furnace.getCookTime() != 0) {
                        furnace.setCookTime((short) (furnace.getCookTime() + (short) (((double) period) * ((double) smeltingMultiplierPercentage / 100.0d))));
                    }
                }
                furnaces.removeAll(toRemove);
            }

        }.runTaskTimer(main, 0, 5);
    }

    @EventHandler
    public void onPlayerPlacesFurnace(BlockPlaceEvent event) {
        if(event.getBlock().getType() == Material.FURNACE) {
            furnaces.add((Furnace) event.getBlock().getState());
            Main.logDebug("Furnace placed at " + event.getBlock().getLocation().toString() + " added to the list of furnaces.");
        }
    }
}