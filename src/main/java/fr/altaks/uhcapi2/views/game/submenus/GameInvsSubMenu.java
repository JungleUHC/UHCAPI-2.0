package fr.altaks.uhcapi2.views.game.submenus;

import fr.altaks.uhcapi2.controllers.game.GameInvsController;
import fr.altaks.uhcapi2.core.GameManager;
import fr.altaks.uhcapi2.views.game.GameConfigMainMenu;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.stream.IntStream;

public class GameInvsSubMenu extends FastInv {

    private GameConfigMainMenu upperMenu;
    private GameManager manager;

    private String
            START_INV_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWNkNWM5YjQxYWZlNGRkZmEwNjAwMWY3OGM3ODFkMWEzOWQ4ZTFiYTlkODRiYjE0YTA4MGE3YTIxOWVmZGUzIn19fQ==",
            DEATH_INV_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODk4MzA2NWVkNjk3Yzg0YTg2MDJiNDZkN2I4ZWJmZjNjZGEzNDYxMTJmZDY5MDNiOTEyOTZlYjM4ZWYxOWU0ZSJ9fX0=";

    private ItemStack startInv = HeadBuilder.of(START_INV_VALUE)
            .name("Inventaire de départ")
            .lore(
                    "",
                    ChatColor.GRAY + "Clic gauche pour voir l'inventaire de départ",
                    ChatColor.GRAY + "Clic droit pour valider l'inventaire de départ",
                    "",
                    ChatColor.GRAY + "L'inventaire ci-dessus sera sauvegardé en",
                    ChatColor.GRAY + "tant qu'inventaire de départ",
                    ""
            )
            .build();

    private ItemStack deathInv = HeadBuilder.of(DEATH_INV_VALUE)
            .name("Inventaire de décès")
            .lore(
                    "",
                    ChatColor.GRAY + "Clic gauche pour voir l'inventaire de décès",
                    ChatColor.GRAY + "Clic droit pour valider l'inventaire de décès",
                    "",
                    ChatColor.GRAY + "L'inventaire ci-dessus sera sauvegardé en",
                    ChatColor.GRAY + "tant qu'inventaire de décès",
                    ""
            )
            .build();

    public GameInvsSubMenu(GameManager manager, GameConfigMainMenu upperMenu) {
        super(6*9, "Gestion des inventaires");
        this.upperMenu = upperMenu;
        this.manager = manager;

        for(int i = 27; i <= 35; i++){
            setItem(i, ItemBuilder.FILLING_PANE);
        }
        // 45 48 50
        setItem(48, startInv, this::processStartInvClick);
        setItem(50, deathInv, this::processDeathInvClick);

        // Set the return arrow
        setItem(49, new ItemBuilder(Material.ARROW).name("Retour").build(),
                e -> upperMenu.open((Player) e.getWhoClicked())
        );
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    private void processStartInvClick(InventoryClickEvent event){
        if(!manager.canModifyRules((Player) event.getWhoClicked())) return;
        if(event.isRightClick()){
            manager.getGameController().getGameInvsController().preparePlayerForInventoryCloning((Player) event.getWhoClicked(), true);
        } else {
            displayInventory(manager.getGameController().getGameInvsController().getStartingInventory(), event.getClickedInventory());
        }
    }

    private void processDeathInvClick(InventoryClickEvent event){
        if(!manager.canModifyRules((Player) event.getWhoClicked())) return;
        if(event.isRightClick()){
            manager.getGameController().getGameInvsController().preparePlayerForInventoryCloning((Player) event.getWhoClicked(), false);
        } else {
            displayInventory(manager.getGameController().getGameInvsController().getDeathDropsInventory(), event.getClickedInventory());
        }
    }

    private final int[] normalSlotsDisplay = IntStream.range(0, 27).sorted().toArray();
    private final int[] hotBarSlotsDisplay = new int[]{36, 37, 38, 39, 40, 41, 42, 43, 44};

    private final int[] normalSlots = IntStream.range(9, 35).sorted().toArray();
    private final int[] hotbarSlots = IntStream.range(0, 9).sorted().toArray();

    private void displayInventory(GameInvsController.CloneableInventory inventory, Inventory toInject){

        // Clearing the inventory from last injection
        for(int slot : normalSlotsDisplay){
            toInject.setItem(slot, null);
        }
        for(int slot : hotBarSlotsDisplay){
            toInject.setItem(slot, null);
        }


        if(inventory == null || inventory.getItems() == null) return;
        HashMap<Integer, ItemStack> inventoryContents = inventory.getItems();

        // Clone the hotbar
        for(int slotOffset = 0; slotOffset < hotbarSlots.length; slotOffset++){
            int displaySlot = hotBarSlotsDisplay[slotOffset];
            int realSlot    = hotbarSlots[slotOffset];

            ItemStack item = inventoryContents.get(realSlot);
            toInject.setItem(displaySlot, item);
        }

        // Clone the inventory
        for(int slotOffset = 0; slotOffset < normalSlots.length; slotOffset++){
            int displaySlot = normalSlotsDisplay[slotOffset];
            int realSlot    = normalSlots[slotOffset];

            ItemStack item = inventoryContents.get(realSlot);
            toInject.setItem(displaySlot, item);
        }


    }
}