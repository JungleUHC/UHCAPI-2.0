package fr.altaks.uhcapi2.views.world.submenus;

import fr.altaks.uhcapi2.controllers.WorldsController;
import fr.altaks.uhcapi2.core.GameManager;
import fr.altaks.uhcapi2.views.world.WorldMainMenu;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WorldStructuresSubMenu extends FastInv {

    private WorldMainMenu upperMenu;

    private String

            VILLAGES_STRUCT_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTk0Yzc5ZjAzZTE5MWI5MzQ3N2Y3YzE5NTU3NDA4ZjdhZjRmOTY2MGU1ZGZiMDY4N2UzYjhlYjkyZmJkM2FlMSJ9fX0=",
            STRONGHOLD_STRUCT_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmY5MjA5ODZjNTcyMzg5YTIzYzhhYzA0NzFiODhkZjQ0YTQ3NzY4MDNiZTY0NDRmNWZmMGI5ZDE1NTJjMjc0ZSJ9fX0=",
            MINESHAFT_STRUCT_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNlZDM0MjExZmVkNDAxMGE4Yzg1NzI0YTI3ZmE1ZmIyMDVkNjc2ODRiM2RhNTE3YjY4MjEyNzljNmI2NWQzZiJ9fX0=";

    private ItemStack villagesStruct = HeadBuilder.of(VILLAGES_STRUCT_VALUE)
            .name("Villages")
            .lore(
                    "",
                    ChatColor.YELLOW + "Etat : " + ChatColor.GREEN + "Activé",
                    "",
                    ChatColor.GRAY + "Cliquez pour activer/désactiver la génération des villages"
            )
            .build();

    private ItemStack strongholdStruct = HeadBuilder.of(STRONGHOLD_STRUCT_VALUE)
            .name("Strongholds")
            .lore(
                    "",
                    ChatColor.YELLOW + "Etat : " + ChatColor.GREEN + "Activé",
                    "",
                    ChatColor.GRAY + "Cliquez pour activer/désactiver la génération des strongholds"
            )
            .build();

    private ItemStack mineshaftStruct = HeadBuilder.of(MINESHAFT_STRUCT_VALUE)
            .name("Mineshafts")
            .lore(
                    "",
                    ChatColor.YELLOW + "Etat : " + ChatColor.GREEN + "Activé",
                    "",
                    ChatColor.GRAY + "Cliquez pour activer/désactiver la génération des mineshafts"
            )
            .build();

    private GameManager manager;
    private HashMap<Integer, WorldsController.StructureType> slotsToStructureType = new HashMap<>();

    public WorldStructuresSubMenu(GameManager manager, WorldMainMenu upperMenu) {
        super(5*9, "Gestion des structures");
        this.upperMenu = upperMenu;
        this.manager = manager;

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        // slots 20, 22, 24
        setItem(20, villagesStruct);
        setItem(22, strongholdStruct);
        setItem(24, mineshaftStruct);

        slotsToStructureType.put(20, WorldsController.StructureType.VILLAGE);
        slotsToStructureType.put(22, WorldsController.StructureType.STRONGHOLD);
        slotsToStructureType.put(24, WorldsController.StructureType.MINESHAFT);

        // Set the return arrow
        setItem(40, new ItemBuilder(Material.ARROW).name("Retour").build(),
                e -> upperMenu.open((Player) e.getWhoClicked())
        );
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);

        // Check interaction validity
        if(event.getClickedInventory() == null || event.getClickedInventory().equals(event.getView().getBottomInventory())) return;
        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        if(slotsToStructureType.containsKey(event.getSlot())){
            this.manager.getWorldsController().switchStructureActivationStatus(slotsToStructureType.get(event.getSlot()));

            // update item
            updateItemLoreAccordingToNewStructureState(event, this.manager.getWorldsController().isStructureEnabled(slotsToStructureType.get(event.getSlot())));
        }
    }

    private static void updateItemLoreAccordingToNewStructureState(InventoryClickEvent event, boolean isEnabled) {
        ItemMeta meta = event.getCurrentItem().getItemMeta();
        List<String> lore = meta.getLore();
        lore.set(1, ChatColor.YELLOW + "Etat : " + ((isEnabled) ? ChatColor.GREEN + "Activé" : ChatColor.RED + "Désactivé"));
        meta.setLore(lore);
        event.getCurrentItem().setItemMeta(meta);
    }
}