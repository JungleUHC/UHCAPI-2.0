package fr.altaks.uhcapi2.core.menu.game.submenus;

import fr.altaks.uhcapi2.core.menu.game.GameConfigMainMenu;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GameInvsSubMenu extends FastInv {

    private GameConfigMainMenu upperMenu;

    private String
            NEW_INV_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODFjZTk1ZGQ0ODBmMTdmZWUwNzFmOWU4ZjdlMmU5YzA1NDJhYzcyYWI1ZDJhZTUzY2NlYWQyYmQ3MjM3MGMyNSJ9fX0=",
            START_INV_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWNkNWM5YjQxYWZlNGRkZmEwNjAwMWY3OGM3ODFkMWEzOWQ4ZTFiYTlkODRiYjE0YTA4MGE3YTIxOWVmZGUzIn19fQ==",
            DEATH_INV_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODk4MzA2NWVkNjk3Yzg0YTg2MDJiNDZkN2I4ZWJmZjNjZGEzNDYxMTJmZDY5MDNiOTEyOTZlYjM4ZWYxOWU0ZSJ9fX0=";

    private ItemStack newInv = HeadBuilder.of(NEW_INV_VALUE)
            .name("Nouvel inventaire")
            .build();

    private ItemStack startInv = HeadBuilder.of(START_INV_VALUE)
            .name("Inventaire de départ")
            .build();

    private ItemStack deathInv = HeadBuilder.of(DEATH_INV_VALUE)
            .name("Inventaire de décès")
            .build();

    public GameInvsSubMenu(GameConfigMainMenu upperMenu) {
        super(6*9, "Gestion des inventaires");
        this.upperMenu = upperMenu;


        for(int i = 27; i <= 35; i++){
            setItem(i, ItemBuilder.FILLING_PANE);
        }
        // 45 48 50
        setItem(45, newInv);
        setItem(48, startInv);
        setItem(50, deathInv);

        // Set the return arrow
        setItem(49, new ItemBuilder(Material.ARROW).name("Retour").build(),
                e -> upperMenu.open((Player) e.getWhoClicked())
        );
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}