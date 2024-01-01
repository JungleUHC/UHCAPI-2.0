package fr.altaks.uhcapi2.views.world.submenus;

import fr.altaks.uhcapi2.Main;
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

public class WorldNewWorldSubMenu extends FastInv {

    private WorldMainMenu upperMenu;

    private String START_CREATION_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODFjZTk1ZGQ0ODBmMTdmZWUwNzFmOWU4ZjdlMmU5YzA1NDJhYzcyYWI1ZDJhZTUzY2NlYWQyYmQ3MjM3MGMyNSJ9fX0=";
    private String STOP_CREATION_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTkwMjUzYzQ5ZTEzY2MyZGUwMDA5MGVlNjU4MDlkYTYxN2M1M2Y1OWUzNWYwOWE3YzZlMzUwMTFkMTlhY2IzZCJ9fX0=";

    private ItemStack START_CREATION = HeadBuilder.of(START_CREATION_VALUE)
            .name(ChatColor.GREEN + "\u00BB Confirmer")
            .build();

    private ItemStack STOP_CREATION = HeadBuilder.of(STOP_CREATION_VALUE)
            .name(ChatColor.RED + "\u2715 Rétracter")
            .build();

    public WorldNewWorldSubMenu(GameManager manager, WorldMainMenu upperMenu) {
        super(5*9, "Création du monde");
        this.upperMenu = upperMenu;

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        setItem(21, START_CREATION, event -> {
            if(!manager.canModifyRules((Player) event.getWhoClicked())) return;
            manager.getWorldsController().startWorldGeneration();
        });
        setItem(23, STOP_CREATION, event -> {
            if(!manager.canModifyRules((Player) event.getWhoClicked())) return;
            if(!manager.getWorldsController().cancelWorldGeneration())
                event.getWhoClicked().sendMessage(Main.MSG_PREFIX + ChatColor.RED + "La génération du monde n'a pas pu être annulée");;
        });

        // Set the return arrow
        setItem(40, new ItemBuilder(Material.ARROW).name("Retour").build(),
                e -> upperMenu.open((Player) e.getWhoClicked())
        );
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}