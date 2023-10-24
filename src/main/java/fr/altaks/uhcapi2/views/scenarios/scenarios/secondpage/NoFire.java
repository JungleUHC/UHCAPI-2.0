package fr.altaks.uhcapi2.views.scenarios.scenarios.secondpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class NoFire implements Scenario {

    public String getName() {
        return "No Fire";
    }

    @Override
    public String getDescription() {
        return "Annule les dégâts causés par le feu, garantissant la sécurité des joueurs.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.FLINT_AND_STEEL);
    }

    @Override
    public int getSlot() {
        return 65;
    }

    @Override
    public void setup(Main main) {

    }
}