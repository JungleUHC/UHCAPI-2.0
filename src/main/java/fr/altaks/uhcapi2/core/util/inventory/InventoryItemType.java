package fr.altaks.uhcapi2.core.util.inventory;

public enum InventoryItemType {
    INTERACTIVE("Interactive"),
    ROTATING("Rotating"),
    SWAPPING("Swapping"),
    STATIC("Static");

    private String humanFormat;

    InventoryItemType(String name){
        this.humanFormat = name;
    }

    @Override
    public String toString() {
        return this.humanFormat;
    }
}
