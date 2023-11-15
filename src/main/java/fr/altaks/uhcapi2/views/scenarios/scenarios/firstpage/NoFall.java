package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class NoFall  implements Scenario {

    public String getName() {
        return "No Fall";
    }

    @Override
    public String getDescription() {
        return "Empêche les joueurs de subir des dégâts dus aux chutes.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.CHAINMAIL_BOOTS);
    }

    @Override
    public int getSlot() {
        return 24;
    }

    @Override
    public void startScenario(Main main) {

    }
}