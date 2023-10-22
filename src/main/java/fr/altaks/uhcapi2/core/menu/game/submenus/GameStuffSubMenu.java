package fr.altaks.uhcapi2.core.menu.game.submenus;

import fr.altaks.uhcapi2.core.menu.game.GameConfigMainMenu;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GameStuffSubMenu extends FastInv {

    private GameConfigMainMenu upperMenu;

    private ItemStack swordConfig = new ItemBuilder(Material.IRON_SWORD)
            .name("Limites des épées")
            .build();

    private ItemStack armorConfig = new ItemBuilder(Material.IRON_CHESTPLATE)
            .name("Limites des armures")
            .build();

    private ItemStack bowConfig = new ItemBuilder(Material.BOW)
            .name("Limites des arcs")
            .build();

    private ItemStack pearlConfig = new ItemBuilder(Material.ENDER_PEARL)
            .name("Limites des perles")
            .build();

    private ItemStack bucketConfig = new ItemBuilder(Material.BUCKET)
            .name("Limites des seaux")
            .build();

    public GameStuffSubMenu(GameConfigMainMenu upperMenu) {
        super(5*9, "Création du monde");
        this.upperMenu = upperMenu;

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        // 11 13 15 30 32
        setItem(11, swordConfig);
        setItem(13, armorConfig);
        setItem(15, bowConfig);

        setItem(30, pearlConfig);
        setItem(32, bucketConfig);

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