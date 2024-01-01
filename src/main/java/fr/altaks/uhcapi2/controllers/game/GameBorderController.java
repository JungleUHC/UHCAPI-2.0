package fr.altaks.uhcapi2.controllers.game;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IController;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class GameBorderController implements IController {

    private Main main;

    private float initialBorderSize = 1000; // in blocks
    private float finalBorderSize = 150; // in blocks

    private boolean safeBorder = true;

    private float borderShrinkSpeed = 0.5f; // in blocks per second
    private float timeBeforeBorderShrink = 30; // in minutes

    public GameBorderController(Main main){
        this.main = main;
    }

    private BukkitTask borderReductionTask;
    private BukkitRunnable borderReductionRunnable;

    public void forceBorderReduction() {
        if(borderReductionTask != null){
            borderReductionTask.cancel();
        }
        this.borderReductionRunnable.run();
    }

    @Override
    public void onGameStart() {

        for(World world : Bukkit.getWorlds()){
            world.getWorldBorder().setSize(initialBorderSize);
            if(safeBorder){
                world.getWorldBorder().setDamageAmount(0);
                world.getWorldBorder().setDamageBuffer(200);
            }
        }

        this.borderReductionRunnable = new BukkitRunnable() {

            @Override
            public void run() {

                Main.logInfo("Border is now shrinking");
                Main.logDebug("Border is shrinking by " + borderShrinkSpeed + " blocks/s");

                for(World world : Bukkit.getWorlds()){

                    long timeForShrinkCompleting = (long) ((initialBorderSize - finalBorderSize) / borderShrinkSpeed); // in seconds
                    world.getWorldBorder().setSize(finalBorderSize, timeForShrinkCompleting);

                }

                // start runnable to teleport players at the border if safeBorder is true
                if(safeBorder){

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            for(Player player : Bukkit.getOnlinePlayers()){

                                // if the player is in the overworld, not in spectator and behind the border, teleport him back to spawn

                                if(player.getWorld().getEnvironment() == World.Environment.NORMAL){
                                    if(player.getGameMode() != GameMode.SPECTATOR){
                                        Location location = player.getLocation();
                                        Location worldSpawn = new Location(player.getWorld(), 0, 0, 0);
                                        worldSpawn.setY(player.getWorld().getHighestBlockYAt(worldSpawn) + 1);

                                        if(location.distance(worldSpawn) > (player.getLocation().getWorld().getWorldBorder().getSize() / 2)){
                                            player.teleport(worldSpawn);
                                            Main.logDebug("Player " + player.getName() + " has been teleported back to spawn because he was behind the border");
                                        }
                                    }
                                }
                            }
                        }

                    }.runTaskTimer(main, 0, 20); // every second (20 ticks)

                }

            }

        }; // convert times into game ticks
        this.borderReductionTask = this.borderReductionRunnable.runTaskLater(main, (long) (timeBeforeBorderShrink * 60 * 20));
    }

    public float getInitialBorderSize() {
        return initialBorderSize;
    }

    public void setInitialBorderSize(float initialBorderSize) {
        this.initialBorderSize = initialBorderSize;
    }

    public float getBorderShrinkSpeed() {
        return borderShrinkSpeed;
    }

    public void setBorderShrinkSpeed(float borderShrinkSpeed) {
        this.borderShrinkSpeed = borderShrinkSpeed;
    }

    public float getTimeBeforeBorderShrink() {
        return timeBeforeBorderShrink;
    }

    public void setTimeBeforeBorderShrink(float timeBeforeBorderShrink) {
        this.timeBeforeBorderShrink = timeBeforeBorderShrink;
    }

    public boolean isSafeBorder() {
        return safeBorder;
    }

    public void setSafeBorder(boolean safeBorder) {
        this.safeBorder = safeBorder;
    }

    public float getFinalBorderSize() {
        return finalBorderSize;
    }

    public void setFinalBorderSize(float finalBorderSize) {
        this.finalBorderSize = finalBorderSize;
    }
}
