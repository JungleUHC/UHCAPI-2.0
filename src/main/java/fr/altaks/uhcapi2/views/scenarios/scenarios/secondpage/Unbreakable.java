package fr.altaks.uhcapi2.views.scenarios.scenarios.secondpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class Unbreakable implements Scenario {

    public String getName() {
        return "Unbreakable";
    }

    @Override
    public String getDescription() {
        return "Rend tous les outils et armures incassables, éliminant la nécessité de réparation.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.GOLD_AXE)
                .durability((short)11);
    }

    @Override
    public int getSlot() {
        return 64;
    }

    @Override
    public void startScenario(Main main) {

    }
}