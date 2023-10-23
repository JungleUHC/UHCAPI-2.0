package fr.altaks.uhcapi2.views.game.submenus;

import fr.altaks.uhcapi2.views.game.GameConfigMainMenu;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GameMobsSubMenu extends FastInv {

    private GameConfigMainMenu upperMenu;

    private EntityType[] mobsOrAnimals = {
            EntityType.BAT, EntityType.BLAZE, EntityType.CAVE_SPIDER, EntityType.CHICKEN, EntityType.COW,
            EntityType.CREEPER, EntityType.ENDERMAN, EntityType.GHAST, EntityType.HORSE,
            EntityType.MAGMA_CUBE, EntityType.MUSHROOM_COW, EntityType.OCELOT, EntityType.PIG, EntityType.PIG_ZOMBIE, EntityType.SHEEP,
            EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.SPIDER, EntityType.SQUID,
            EntityType.VILLAGER, EntityType.WITCH, EntityType.WITHER, EntityType.WOLF, EntityType.ZOMBIE
    };

    @SuppressWarnings("deprecation")
    public GameMobsSubMenu(GameConfigMainMenu upperMenu) {
        super(5*9, "Création du monde");
        this.upperMenu = upperMenu;

        for(EntityType type : mobsOrAnimals){
            String itemName = type.name().toLowerCase().replace("_", " ");
            // capitalize first letter
            itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);
            addItem(
                    new ItemBuilder(Material.MONSTER_EGG)
                            .data(type.getTypeId())
                            .name(itemName)
                            .lore(ChatColor.RESET + "" + ChatColor.GREEN + "Activé")
                            .build()
            );
        }

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