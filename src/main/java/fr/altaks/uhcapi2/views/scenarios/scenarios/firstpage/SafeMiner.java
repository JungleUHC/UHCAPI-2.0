package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class SafeMiner implements Scenario {

    public String getName() {
        return "Safe Miner";
    }

    @Override
    public String getDescription() {
        return "Les joueurs ne subissent pas de dégâts de feu sous la couche 32.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.LAVA_BUCKET);
    }

    @Override
    public int getSlot() {
        return 19;
    }

    @Override
    public void startScenario(Main main) {

    }
}