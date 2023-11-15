package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class NoNametag implements Scenario {

    public String getName() {
        return "No NameTag";
    }

    @Override
    public String getDescription() {
        return "Masque les pseudonymes des joueurs, ajoutant un élément de mystère au jeu.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.NAME_TAG);
    }

    @Override
    public int getSlot() {
        return 33;
    }

    @Override
    public void startScenario(Main main) {

    }
}