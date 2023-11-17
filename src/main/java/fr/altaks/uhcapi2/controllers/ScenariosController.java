package fr.altaks.uhcapi2.controllers;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IController;
import fr.altaks.uhcapi2.views.scenarios.Scenario;

import java.util.ArrayList;
import java.util.HashMap;

public class ScenariosController implements IController {

    private final Main main;

    public ArrayList<Scenario> getScenariosToEnable() {
        return scenariosToEnable;
    }

    private final ArrayList<Scenario> scenariosToEnable = new ArrayList<>();

    public HashMap<Class<? extends Scenario>, Scenario> getEnabledScenarioInstances() {
        return enabledScenarioInstances;
    }

    private final HashMap<Class<? extends Scenario>, Scenario> enabledScenarioInstances = new HashMap<>();

    public ScenariosController(Main main) {
        this.main = main;
    }

    @Override
    public void onGameStart() {
        for(Scenario scenario : scenariosToEnable){
            scenario.startScenario(main);
            enabledScenarioInstances.put(scenario.getClass(), scenario);
            Main.logDev("Starting scenario " + scenario.getName());
        }
    }

    /**
     * Switch the activation state of a scenario
     * @param scenario
     */
    public void switchScenarioActivationState(Scenario scenario){
        if(scenariosToEnable.contains(scenario)){
            Main.logDev("Disabling scenario " + scenario.getName());
            scenariosToEnable.remove(scenario);
        } else {
            Main.logDev("Enabling scenario " + scenario.getName());
            scenariosToEnable.add(scenario);
        }
    }
}
