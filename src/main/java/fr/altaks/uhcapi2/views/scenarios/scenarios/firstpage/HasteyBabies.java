package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class HasteyBabies implements Scenario {

    public String getName() {
        return "Hastey Babies";
    }

    @Override
    public String getDescription() {
        return "Les outils fabriqués reçoivent l'enchantement Efficiency 1 et Unbreaking 1, offrant un léger avantage en vitesse.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.STONE_PICKAXE);
    }

    @Override
    public int getSlot() {
        return 15;
    }

    @Override
    public void startScenario(Main main) {

    }
}