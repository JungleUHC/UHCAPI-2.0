package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;

public class IronMan implements Scenario {

    private String ICON_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTJiNzE0NGRiM2E5ZmQ4OGMzNzk3ZGNmOThiNDZlYjUzODFiOWQ1ZmRhNzdlMzQ4MTU3MDM3MTc3YWM4OGUifX19";

    public String getName() {
        return "Iron Man";
    }

    @Override
    public String getDescription() {
        return "Récompense le dernier joueur à ne pas prendre de dégâts avec 2 pommes d'or.";
    }

    @Override
    public ItemBuilder getIcon() {
        return HeadBuilder.of(ICON_VALUE);
    }

    @Override
    public int getSlot() {
        return 34;
    }

    @Override
    public void startScenario(Main main) {

    }
}