package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class BetaZombies implements Scenario {

    public String getName() {
        return "Beta Zombies";
    }

    @Override
    public String getDescription() {
        return "Les zombies ont une chance de laisser tomber des plumes, avec un taux de drop configurable.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.FEATHER);
    }

    @Override
    public int getSlot() {
        return 25;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public void setup(Main main) {

    }
}