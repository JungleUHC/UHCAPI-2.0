package fr.altaks.uhcapi2.controllers;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.GameMode;
import fr.altaks.uhcapi2.core.IController;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public class ParametersController implements IController {

    private Main main;

    public ParametersController(Main main) {
        this.main = main;
    }

    @Override
    public void onGameStart() {}

    @Override
    public void onConfigLoad(FileConfiguration config) {
        for(Map.Entry<GameMode.RoleParameter, Object> entry : main.getGameManager().getChosenGameMode().getRolesParameters().entrySet()){
            config.set("roles.parameters." + entry.getKey().getPath(), entry.getValue());
        }
    }
}
