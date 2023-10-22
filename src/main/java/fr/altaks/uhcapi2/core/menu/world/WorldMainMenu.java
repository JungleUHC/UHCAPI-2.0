package fr.altaks.uhcapi2.core.menu.world;

import fr.altaks.uhcapi2.core.menu.HostMainMenu;
import fr.altaks.uhcapi2.core.menu.world.submenus.WorldCaveBoostMenu;
import fr.altaks.uhcapi2.core.menu.world.submenus.WorldNewWorldMenu;
import fr.altaks.uhcapi2.core.menu.world.submenus.WorldStructuresMenu;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class WorldMainMenu extends FastInv {

    private HostMainMenu upperMenu;

    private String BOOST_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTFlZDlhYmY1MWZlNGVhODRjZmNiMjcyOTdmMWJjNTRjZDM4MmVkZjg1ZTdiZDZlNzVlY2NhMmI4MDY2MTEifX19",
            STRUCTURE_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTk0Yzc5ZjAzZTE5MWI5MzQ3N2Y3YzE5NTU3NDA4ZjdhZjRmOTY2MGU1ZGZiMDY4N2UzYjhlYjkyZmJkM2FlMSJ9fX0=",
            NEW_WORLD_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODlmN2EwNGFjMzM0ZmNhZjYxOGRhOWU4NDFmMDNjMDBkNzQ5MDAyZGM1OTJmODU0MGVmOTUzNDQ0MmNlY2Y0MiJ9fX0=";

    private ItemStack boostConfig = HeadBuilder.of(BOOST_VALUE)
            .name("Boost des caves")
            .build();

    private ItemStack structureConfig = HeadBuilder.of(STRUCTURE_VALUE)
            .name("Gestion des structures")
            .build();

    private ItemStack newWorldConfig = HeadBuilder.of(NEW_WORLD_VALUE)
            .name("Création du monde")
            .build();

    private WorldCaveBoostMenu caveBoostMenu = new WorldCaveBoostMenu(this);
    private WorldStructuresMenu structuresMenu = new WorldStructuresMenu(this);
    private WorldNewWorldMenu newWorldMenu = new WorldNewWorldMenu(this);

    public WorldMainMenu(HostMainMenu upperMenu) {
        super(5*9, "Gestion du Monde");
        this.upperMenu = upperMenu;

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        setItem(20, boostConfig, e -> caveBoostMenu.open((Player) e.getWhoClicked()));
        setItem(22, structureConfig, e -> structuresMenu.open((Player) e.getWhoClicked()));
        setItem(24, newWorldConfig, e -> newWorldMenu.open((Player) e.getWhoClicked()));

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
