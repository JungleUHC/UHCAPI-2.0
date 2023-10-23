package fr.altaks.uhcapi2.views.timers;

import fr.altaks.uhcapi2.views.HostMainMenu;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class TimersMainMenu extends FastInv {

    private HostMainMenu upperMenu;

    private ItemStack pvpConfig = new ItemBuilder(Material.IRON_SWORD)
            .name("Activation du PVP")
            .build();

    private ItemStack timersRoles = new ItemBuilder(Material.ARMOR_STAND)
            .name("Gestion des timers des rôles")
            .build();

    private ItemStack invincibilityConfig = new ItemBuilder(Material.GOLDEN_APPLE)
            .name("Invincibilité")
            .build();

    public TimersMainMenu(HostMainMenu upperMenu) {
        super(5*9, "Gestion des Timers");
        this.upperMenu = upperMenu;

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        setItem(20, pvpConfig);
        setItem(22, timersRoles);
        setItem(24, invincibilityConfig);

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
