package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class CutClean implements Scenario {

    public String getName() {
        return "Cut Clean";
    }

    @Override
    public String getDescription() {
        return "Simplifie le processus de collecte en cuisant instantanément tous les minerais, le sable et la nourriture extraits.";
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
    public void setup(Main main) {

    }
}
