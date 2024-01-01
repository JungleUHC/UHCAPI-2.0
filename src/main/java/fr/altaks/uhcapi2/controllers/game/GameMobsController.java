package fr.altaks.uhcapi2.controllers.game;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IController;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.ArrayList;

public class GameMobsController implements IController {

    private final Main main;

    public ArrayList<EntityType> getDisabledEntityType() {
        return disabledEntityType;
    }

    private final ArrayList<EntityType> disabledEntityType = new ArrayList<>();

    public GameMobsController(Main main){
        this.main = main;
    }

    @Override
    public void onGameStart() {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    public void switchMobActivation(EntityType entityType){
        if(disabledEntityType.contains(entityType)){
            disabledEntityType.remove(entityType);
            Main.logDebug("Entity type " + entityType.name() + " has been enabled.");
        } else {
            disabledEntityType.add(entityType);
            for(World world : Bukkit.getWorlds()) for(Entity entity : world.getEntities()){
                if(entity.getType().equals(entityType)) entity.remove();
            }
            Main.logDebug("Entity type " + entityType.name() + " has been disabled.");
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event){
        if(disabledEntityType.contains(event.getEntityType())){
            event.setCancelled(true);
        }
    }
}
