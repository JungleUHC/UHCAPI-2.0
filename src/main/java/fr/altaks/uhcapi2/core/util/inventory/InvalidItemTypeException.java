package fr.altaks.uhcapi2.core.util.inventory;

import org.bukkit.inventory.ItemStack;

public class InvalidItemTypeException extends Exception {

    public InvalidItemTypeException(ItemStack item, String msg){
        super("Invalid item type/inv interaction mode for item " + item + " : " + msg);
    }
}
