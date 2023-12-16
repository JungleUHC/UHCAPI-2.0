package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class SpeedyMiner implements Scenario {

    public String getName() {
        return "Speedy Miner";
    }

    @Override
    public String getDescription() {
        return "Accorde l'effet Speed I aux joueurs sous la couche 32, améliorant leur vitesse de déplacement.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.CHAINMAIL_BOOTS);
    }

    @Override
    public int getSlot() {
        return 23;
    }

    @Override
    public void startScenario(Main main) {
        new BukkitRunnable() {
            @Override
            public void run() {

                for(Player player : Bukkit.getOnlinePlayers()){
                    if(player.getGameMode() != GameMode.SPECTATOR && player.getLocation().getBlockY() <= 32){
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 25, 0, false, false));
                    }
                }

            }
        }.runTaskTimer(main, 0, 20);
    }
}