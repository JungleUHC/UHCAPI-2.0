package fr.altaks.uhcapi2.core.util;

import org.bukkit.ChatColor;

public class LoreUtil {

    public static String[] wrapLore(String loreToWrap, int charsAmountPerLine) {
        String[] description = loreToWrap.split(" ");
        StringBuilder lore = new StringBuilder();
        lore.append("\n");
        lore.append(ChatColor.GRAY);
        int lineLength = 0;
        for(String word : description){
            if(lineLength + word.length() > charsAmountPerLine+2){
                lore.append("\n");
                lore.append(ChatColor.GRAY);
                lineLength = 0;
            }
            lore.append(word).append(" ");
            lineLength += word.length();
        }
        return lore.toString().split("\n");
    }

}
