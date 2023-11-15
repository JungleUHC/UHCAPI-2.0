package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class HasteyBoys implements Scenario {

    public String getName() {
        return "Hastey Boys";
    }

    @Override
    public String getDescription() {
        return "Les outils fabriqués reçoivent l'enchantement Efficiency 3 et Unbreaking 3, accélérant ainsi la collecte.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.IRON_PICKAXE);
    }

    @Override
    public int getSlot() {
        return 14;
    }

    @Override
    public void startScenario(Main main) {

    }
}