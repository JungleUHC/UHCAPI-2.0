package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class GigaDrill implements Scenario {

    public String getName() {
        return "Giga Drill";
    }

    @Override
    public String getDescription() {
        return "Tous les outils fabriqués sont dotés d'Efficiency 10 et Unbreaking 10, améliorant considérablement l'efficacité et la durabilité.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.DIAMOND_PICKAXE);
    }

    @Override
    public int getSlot() {
        return 13;
    }

    @Override
    public void startScenario(Main main) {

    }
}