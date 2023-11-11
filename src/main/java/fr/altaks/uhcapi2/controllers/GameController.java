package fr.altaks.uhcapi2.controllers;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.controllers.game.GameBorderController;
import fr.altaks.uhcapi2.controllers.game.GameInvsController;
import fr.altaks.uhcapi2.controllers.game.GameMobsController;
import fr.altaks.uhcapi2.controllers.game.GameStuffController;
import fr.altaks.uhcapi2.core.IController;

public class GameController implements IController {

    private Main main;

    private GameBorderController gameBorderController;
    private GameInvsController gameInvsController;
    private GameMobsController gameMobsController;
    private GameStuffController gameStuffController;

    public GameController(Main main){
        this.main = main;

        this.gameBorderController = new GameBorderController(main);
        this.gameInvsController = new GameInvsController(main);
        this.gameMobsController = new GameMobsController(main);
        this.gameStuffController = new GameStuffController(main);
    }

    @Override
    public void onGameStart() {

    }

    public GameBorderController getGameBorderController() {
        return gameBorderController;
    }

    public GameInvsController getGameInvsController() {
        return gameInvsController;
    }

    public GameMobsController getGameMobsController() {
        return gameMobsController;
    }

    public GameStuffController getGameStuffController() {
        return gameStuffController;
    }
}
