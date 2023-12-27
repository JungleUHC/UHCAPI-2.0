package fr.altaks.uhcapi2.controllers;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.GameManager;
import fr.altaks.uhcapi2.core.GameMode;
import fr.altaks.uhcapi2.core.IController;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class TimersController implements IController {

    private int minutesBeforePvp = 20;
    private int minutesBeforeInvincibilityEnds = 5;
    private int minutesBeforeRolesAreGiven = 20;

    private final HashMap<Integer, GameMode.RoleTimer> slotsToTimers = new HashMap<>();

    private GameManager manager;
    private Main main;

    private long partyStart;

    public TimersController(GameManager manager, Main main) {
        this.manager = manager;
        this.main = main;
    }

    @Override
    public void onGameStart() {
        partyStart = System.currentTimeMillis();
        Bukkit.getPluginManager().registerEvents(this, this.main);

        for(World world : Bukkit.getWorlds()){
            world.setPVP(false);
        }
        new BukkitRunnable(){
            @Override
            public void run() {
                Main.logDev("Pvp is now enabled");
                for(World world : Bukkit.getWorlds()){
                    world.setPVP(true);
                }
            }
        }.runTaskLater(main, minutesBeforePvp * 60 * 20L); // minutes -> seconds -> ticks
    }

    @Override
    public void onConfigLoad(FileConfiguration config) {
        config.set("timers.time-before-pvp", minutesBeforePvp);
        config.set("timers.time-before-taking-damage", minutesBeforeInvincibilityEnds);
        config.set("timers.time-before-roles-are-given", minutesBeforeRolesAreGiven);

        if(main.getGameManager().getChosenGameMode() != null){
            for(Map.Entry<GameMode.RoleTimer, Long> entry : main.getGameManager().getChosenGameMode().getRolesTimers().entrySet()){
                config.set("roles.timers." + entry.getKey().getPath(), entry.getValue());
            }
        }
    }

    @EventHandler
    public void onPlayerTakesDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            if(System.currentTimeMillis() < partyStart + (minutesBeforeInvincibilityEnds * 60 * 1000L)){
                event.setCancelled(true);
            }
        }
    }

    public int getMinutesBeforePvp() {
        return minutesBeforePvp;
    }

    public void setMinutesBeforePvp(int minutesBeforePvp) {
        Main.logDev("Setting minutes before pvp to " + minutesBeforePvp);
        this.minutesBeforePvp = minutesBeforePvp;
    }

    public int getMinutesBeforeInvincibilityEnds() {
        return minutesBeforeInvincibilityEnds;
    }

    public void setMinutesBeforeInvincibilityEnds(int minutesBeforeInvincibilityEnds) {
        Main.logDev("Setting minutes before invincibility ends to " + minutesBeforeInvincibilityEnds);
        this.minutesBeforeInvincibilityEnds = minutesBeforeInvincibilityEnds;
    }

    public HashMap<Integer, GameMode.RoleTimer> getSlotsToTimers() {
        return slotsToTimers;
    }

    public int getMinutesBeforeRolesAreGiven() {
        return minutesBeforeRolesAreGiven;
    }

    public void setMinutesBeforeRolesAreGiven(int minutesBeforeRolesAreGiven) {
        this.minutesBeforeRolesAreGiven = minutesBeforeRolesAreGiven;
    }
}
