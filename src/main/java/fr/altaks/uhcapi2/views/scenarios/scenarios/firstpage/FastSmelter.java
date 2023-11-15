package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;

public class FastSmelter implements Scenario {

    private String ICON_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTNiZjBiODg1OWExZTU3ZjNhYmQ2MjljMGM3MzZlNjQ0ZTgxNjUxZDRkZTAzNGZlZWE0OWY4ODNmMDBlODJiMCJ9fX0=";

    public String getName() {
        return "Fast Smelter";
    }

    @Override
    public String getDescription() {
        return "Les minerais placés dans les fours sont cuits trois fois plus rapidement, accélérant la production de ressources.";
    }

    @Override
    public ItemBuilder getIcon() {
        return HeadBuilder.of(ICON_VALUE);
    }

    @Override
    public int getSlot() {
        return 20;
    }

    @Override
    public void startScenario(Main main) {

    }
}