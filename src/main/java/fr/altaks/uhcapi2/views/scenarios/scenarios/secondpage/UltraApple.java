package fr.altaks.uhcapi2.views.scenarios.scenarios.secondpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class UltraApple implements Scenario {

    public String getName() {
        return "Ultra Apple";
    }

    @Override
    public String getDescription() {
        return "Les joueurs reçoivent périodiquement une pomme d'or comme récompense.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.GOLDEN_APPLE);
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public int getSlot() {
        return 66;
    }

    @Override
    public void setup(Main main) {

    }
}