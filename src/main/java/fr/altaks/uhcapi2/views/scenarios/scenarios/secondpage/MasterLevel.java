package fr.altaks.uhcapi2.views.scenarios.scenarios.secondpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MasterLevel implements Scenario {

    public String getName() {
        return "Golden Heads";
    }

    @Override
    public String getDescription() {
        return "Accorde à tous les joueurs 10 000 niveaux d'EXP, facilitant l'enchantement des objets";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.EXP_BOTTLE);
    }

    @Override
    public int getSlot() {
        return 68;
    }

    @Override
    public void startScenario(Main main) {
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.getGameMode() != GameMode.SPECTATOR){
                player.setLevel(10_000);
            }
        }
    }
}