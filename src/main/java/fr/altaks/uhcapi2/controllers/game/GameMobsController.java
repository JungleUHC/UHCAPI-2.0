package fr.altaks.uhcapi2.controllers.game;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IController;

public class GameMobsController implements IController {

    private Main main;

    public GameMobsController(Main main){
        this.main = main;
    }

    @Override
    public void onGameStart() {

    }
}
