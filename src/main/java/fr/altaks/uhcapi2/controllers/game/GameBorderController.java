package fr.altaks.uhcapi2.controllers.game;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IController;

public class GameBorderController implements IController {

    private Main main;

    public GameBorderController(Main main){
        this.main = main;
    }

    @Override
    public void onGameStart() {

    }
}
