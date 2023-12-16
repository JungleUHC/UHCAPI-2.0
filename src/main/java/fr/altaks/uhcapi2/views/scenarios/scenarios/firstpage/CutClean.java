package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemSpawnEvent;

import java.util.HashMap;

public class CutClean implements Scenario {

    private final HashMap<Material, Material> materialConversionMap = new HashMap<>();

    public CutClean(){
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

    public String getName() {
        return "Cut Clean";
    }

    @Override
    public String getDescription() {
        return "Simplifie le processus de collecte en cuisant instantanément tous les minerais, le sable et la nourriture extraits.";
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event){
        if(materialConversionMap.containsKey(event.getEntity().getItemStack().getType())){
            event.getEntity().getItemStack().setType(materialConversionMap.get(event.getEntity().getItemStack().getType()));
        }
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.GOLDEN_CARROT);
    }

    @Override
    public int getSlot() {
        return 11;
    }

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }
}
