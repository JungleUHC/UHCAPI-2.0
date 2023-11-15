package fr.altaks.uhcapi2.views.scenarios.scenarios.secondpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class NoNether implements Scenario {

    public String getName() {
        return "No Nether";
    }

    @Override
    public String getDescription() {
        return "Empêche l'accès au Nether";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.MOB_SPAWNER);
    }

    @Override
    public int getSlot() {
        return 69;
    }

    @Override
    public void startScenario(Main main) {

    }
}