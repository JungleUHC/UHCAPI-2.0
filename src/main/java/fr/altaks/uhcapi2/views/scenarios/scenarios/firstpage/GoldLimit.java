package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class GoldLimit implements Scenario {

    public String getName() {
        return "Gold Limit";
    }

    @Override
    public String getDescription() {
        return "Permet de définir une limite pour la quantité d'or que les joueurs peuvent collecter.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.GOLD_INGOT);
    }

    @Override
    public int getSlot() {
        return 22;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public void startScenario(Main main) {

    }
}