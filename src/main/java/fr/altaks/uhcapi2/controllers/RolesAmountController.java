package fr.altaks.uhcapi2.controllers;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.GameMode;
import fr.altaks.uhcapi2.core.IController;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public class RolesAmountController implements IController {

    private Main main;

    public RolesAmountController(Main main){
        this.main = main;
    }

    @Override
    public void onGameStart() {}

    @Override
    public void onConfigLoad(FileConfiguration config) {
        // inject roles amount into config

        if(main.getGameManager().getChosenGameMode() != null){
            if(main.getGameManager().getChosenGameMode().getPlayersPerRole() != null){

                for(Map.Entry<GameMode.Role, Integer> entry: main.getGameManager().getChosenGameMode().getPlayersPerRole().entrySet()){
                    config.set("roles-default-amount." + entry.getKey().getPath(), entry.getValue());
                }

                Main.logDev("Injected roles amount into config");
            } else {
                Main.logDev("No roles amount found, skipping roles amount injection");
            }
        } else {
            Main.logDev("No game mode chosen, skipping roles amount injection");
        }
    }
}
