package fr.altaks.uhcapi2.views.scenarios.scenarios.secondpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class NoRod  implements Scenario {

    public String getName() {
        return "No Rod";
    }

    @Override
    public String getDescription() {
        return "Interdit le craft/utilisation de la canne à pêche";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.FISHING_ROD);
    }

    @Override
    public int getSlot() {
        return 73;
    }

    @Override
    public void startScenario(Main main) {

    }
}