package fr.altaks.uhcapi2.controllers;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IController;
import fr.altaks.uhcapi2.views.scenarios.Scenario;

import java.util.ArrayList;

public class ScenariosController implements IController {

    private final Main main;
    private final ArrayList<Scenario> scenariosToEnable = new ArrayList<>();

    public ScenariosController(Main main) {
        this.main = main;
    }

    @Override
    public void onGameStart() {
        for(Scenario scenario : scenariosToEnable){
            scenario.startScenario(main);
        }
    }

    /**
     * Switch the activation state of a scenario
     * @param scenario
     */
    public void switchScenarioActivationState(Scenario scenario){
        if(scenariosToEnable.contains(scenario)){
            scenariosToEnable.remove(scenario);
        } else {
            scenariosToEnable.add(scenario);
        }
    }
}
