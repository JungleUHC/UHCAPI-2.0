package fr.altaks.uhcapi2.core.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabCompleteUtil {

    private static ArrayList<String> matchingValues = new ArrayList<>();

    /**
     * Determines a list of all values that start with the current input
     * @param values The values to filter according to the current input
     * @param currentInput The current input
     * @return A {@link List} of all values that start with the current input
     */
    public static List<String> getMatchingValues(List<String> values, String currentInput){

        matchingValues.clear();

        for(String value : values){
            if(value.toLowerCase().startsWith(currentInput.toLowerCase())){
                matchingValues.add(value);
            }
        }

        return matchingValues;
    }

    /**
     * Determines a list of all online players that start with the current input
     * @param currentInput The current input
     * @return A {@link List} of all online players that start with the current input
     */
    public static List<String> getDefaultImprovedCompleter(String currentInput){
        matchingValues.clear();

        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.getName().toLowerCase().startsWith(currentInput.toLowerCase())){
                matchingValues.add(player.getName());
            }
        }

        return matchingValues;
    }
}
