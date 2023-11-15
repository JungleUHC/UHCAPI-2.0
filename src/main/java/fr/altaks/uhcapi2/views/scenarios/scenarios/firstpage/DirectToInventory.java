package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;

public class DirectToInventory  implements Scenario {

    private String ICON_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmM3NjI4ZTg5N2NiNGM5MzEyZjgwMmYyOGMyZWM0NjZkN2U3MjFlYzM3MDEwMzc4Y2M0NWRkMmRjNjg4MTBjMyJ9fX0=";

    public String getName() {
        return "Direct To Inventory";
    }

    @Override
    public String getDescription() {
        return "Les minerais minés vont directement dans l'inventaire des joueurs";
    }

    @Override
    public ItemBuilder getIcon() {
        return HeadBuilder.of(ICON_VALUE);
    }

    @Override
    public int getSlot() {
        return 29;
    }

    @Override
    public void startScenario(Main main) {

    }
}