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

public class CatEyes implements Scenario {

    public String getName() {
        return "Cat Eyes";
    }

    @Override
    public String getDescription() {
        return "Accorde l'effet de vision nocturne à tous les joueurs, ce qui rend la nuit moins obscure";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.EYE_OF_ENDER);
    }

    @Override
    public int getSlot() {
        return 10;
    }

    @Override
    public void startScenario(Main main) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Give night vision to all players if they don't have it yet every 2 seconds
                for(Player player : Bukkit.getOnlinePlayers()){
                    if(player.getGameMode() != GameMode.SPECTATOR){
                        if(!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)){
                            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false), false);
                        }
                    }
                }
            }
        }.runTaskTimer(main, 0, 2 * 20);
    }
}
