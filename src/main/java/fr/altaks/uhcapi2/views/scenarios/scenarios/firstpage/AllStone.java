package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class AllStone implements Scenario {

    private String ICON_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2NkYjczYjQzMzZkM2ExMzM4MzQ0MjljMGVkY2RlMzM3M2RkNjRlZTIyMWUwY2Y3N2JlODE0OTQxMTYwYTQ0YSJ9fX0=";

    public String getName() {
        return "All Stone";
    }

    @Override
    public String getDescription() {
        return "La diorite, l'andésite, etc., ainsi que la pierre, sont transformées en cobblestone lorsqu'elles sont minés.";
    }

    @Override
    public ItemBuilder getIcon() {
        return HeadBuilder.of(ICON_VALUE);
    }

    @Override
    public int getSlot() {
        return 28;
    }

    @Override
    public void setup(Main main) {

    }
}
