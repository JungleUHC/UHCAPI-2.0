package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class SpeedyMiner implements Scenario {

    public String getName() {
        return "Speedy Miner";
    }

    @Override
    public String getDescription() {
        return "Accorde l'effet Speed I aux joueurs sous la couche 32, améliorant leur vitesse de déplacement.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.CHAINMAIL_BOOTS);
    }

    @Override
    public int getSlot() {
        return 23;
    }

    @Override
    public void startScenario(Main main) {

    }
}