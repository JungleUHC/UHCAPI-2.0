package fr.altaks.uhcapi2.core.util.inventory;

public class InvalidItemTypeException extends Exception {

    public InvalidItemTypeException(SpecialItem item, String msg){
        super("Invalid item type/inv interaction mode for item " + item + " : " + msg);
    }
}
