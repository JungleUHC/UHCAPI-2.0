package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class StarterTools implements Scenario {

    public String getName() {
        return "Starter Tools";
    }

    @Override
    public String getDescription() {
        return "Ajoute des outils en fer enchantés avec Efficiency 2 et Unbreaking 2 dans l'inventaire de départ, simplifiant le début de la partie.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.STONE_PICKAXE);
    }

    @Override
    public int getSlot() {
        return 16;
    }

    @Override
    public void startScenario(Main main) {

    }
}