package fr.altaks.uhcapi2.views.game.submenus;

import fr.altaks.uhcapi2.core.GameManager;
import fr.altaks.uhcapi2.views.game.GameConfigMainMenu;
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

    private GameManager manager;

    public GameStuffSubMenu(GameManager manager, GameConfigMainMenu upperMenu) {
        super(5*9, "Création du monde");
        this.upperMenu = upperMenu;
        this.manager = manager;

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        // 11 13 15 30 32
        setItem(11, swordConfig, event -> manager.getGameController().getGameStuffController().getSwordsLimitsInventory().open((Player) event.getWhoClicked()));
        setItem(13, armorConfig, event -> manager.getGameController().getGameStuffController().getArmorsLimitsInventory().open((Player) event.getWhoClicked()));
        setItem(15, bowConfig, event -> manager.getGameController().getGameStuffController().getBowsLimitsInventory().open((Player) event.getWhoClicked()));

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