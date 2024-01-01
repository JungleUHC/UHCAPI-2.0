package fr.altaks.uhcapi2.core.util;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

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

    public static void updateLore(ItemStack currentItem, int line, String newText) {
        ItemMeta meta = currentItem.getItemMeta();
        List<String> lore = meta.getLore();

        if(line < 0) {
            line = lore.size() + line; // make the line relative to the end of the lore if the line number is negative
        }
        if(line >= lore.size()) {
            throw new RuntimeException("Line number is too high");
        }

        lore.set(line, newText);
        meta.setLore(lore);
        currentItem.setItemMeta(meta);
    }
}
