package fr.altaks.uhcapi2.views.scenarios.scenarios.secondpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class MinHP implements Scenario {

    public String getName() {
        return "Min HP";
    }

    @Override
    public String getDescription() {
        return "Empêche les joueurs de descendre en dessous de 3 cœurs avant l'activation du PvP.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.POTION);
    }

    @Override
    public int getSlot() {
        return 70;
    }

    @Override
    public void setup(Main main) {

    }
}