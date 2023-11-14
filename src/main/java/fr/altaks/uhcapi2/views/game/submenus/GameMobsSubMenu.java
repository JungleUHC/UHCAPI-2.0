package fr.altaks.uhcapi2.views.game.submenus;

import fr.altaks.uhcapi2.core.GameManager;
import fr.altaks.uhcapi2.views.game.GameConfigMainMenu;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class GameMobsSubMenu extends FastInv {

    private final GameManager manager;

    private final HashMap<Integer, EntityType> slotsToEntityType = new HashMap<>();

    @SuppressWarnings("deprecation")
    public GameMobsSubMenu(GameManager manager, GameConfigMainMenu upperMenu) {
        super(5*9, "Création du monde");
        this.manager = manager;

        EntityType[] mobsOrAnimals = {
                EntityType.BAT, EntityType.BLAZE, EntityType.CAVE_SPIDER, EntityType.CHICKEN, EntityType.COW,
                EntityType.CREEPER, EntityType.ENDERMAN, EntityType.GHAST, EntityType.HORSE,
                EntityType.MAGMA_CUBE, EntityType.MUSHROOM_COW, EntityType.PIG, EntityType.PIG_ZOMBIE, EntityType.SHEEP,
                EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.SPIDER, EntityType.SQUID,
                EntityType.VILLAGER, EntityType.WITCH, EntityType.WOLF, EntityType.ZOMBIE
        };

        for(EntityType type : mobsOrAnimals){
            String itemName = type.name().toLowerCase().replace("_", " ");
            // capitalize first letter
            itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);
            int firstEmptySlot = getInventory().firstEmpty();
            setItem(firstEmptySlot, new ItemBuilder(Material.MONSTER_EGG)
                    .data(type.getTypeId())
                    .name(itemName)
                    .lore(ChatColor.RESET + "" + ChatColor.GREEN + "Activé")
                    .build()
            );
            slotsToEntityType.put(firstEmptySlot, type);
        }

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

        if(slotsToEntityType.containsKey(event.getSlot())){
            EntityType entityType = slotsToEntityType.get(event.getSlot());
            manager.getGameController().getGameMobsController().switchMobActivation(entityType);
            updateItemLoreAccordingToActivation(event, entityType);
        }
    }

    private void updateItemLoreAccordingToActivation(InventoryClickEvent event, EntityType entityType){
        ArrayList<String> lore = new ArrayList<>();
        if(manager.getGameController().getGameMobsController().getDisabledEntityType().contains(entityType)){
            lore.add(ChatColor.RESET + "" + ChatColor.RED + "Désactivé");
        } else {
            lore.add(ChatColor.RESET + "" + ChatColor.GREEN + "Activé");
        }

        ItemMeta meta = event.getCurrentItem().getItemMeta();
        meta.setLore(lore);
        event.getCurrentItem().setItemMeta(meta);
    }
}