package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class DiamondLimit implements Scenario {

    public String getName() {
        return "Diamond Limit";
    }

    @Override
    public String getDescription() {
        return "Permet de définir une limite pour la quantité de diamants que les joueurs peuvent collecter.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.DIAMOND);
    }

    @Override
    public int getSlot() {
        return 21;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public void setup(Main main) {

    }
}