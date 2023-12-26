package fr.altaks.uhcapi2.core;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

public interface IController extends Listener {

    void onGameStart();

    default void onConfigLoad(FileConfiguration config){
        return;
    };

}
