package fr.altaks.uhcapi2.views.scenarios.scenarios;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import org.bukkit.Material;

public class CatEyes implements Scenario {

    public String getName() {
        return "Cat Eyes";
    }

    @Override
    public String getDescription() {
        return "Accorde l'effet de vision nocturne à tous les joueurs, ce qui rend la nuit moins obscure";
    }

    @Override
    public Material getIcon() {
        return Material.EYE_OF_ENDER;
    }

    @Override
    public int getSlot() {
        return 10;
    }

    @Override
    public void setup(Main main) {

    }
}
