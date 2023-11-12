package fr.altaks.uhcapi2.views.moderation;

import fr.altaks.uhcapi2.core.util.LoreUtil;
import fr.altaks.uhcapi2.views.HostMainMenu;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ModerationMainMenu extends FastInv {

    private ItemStack accessRolesIcon = new ItemBuilder(Material.BANNER)
            .name("Accès aux rôles")
            .lore(
                    LoreUtil.wrapLore("Permet aux spectateurs de voir les rôles des joueurs", 30)
            )
            .build();

    private ItemStack accessTeamsIcon = new ItemBuilder(Material.BANNER)
            .name("Accès aux équipes")
            .lore(
                    LoreUtil.wrapLore("Permet aux spectateurs de voir les équipes des joueurs", 30)
            )
            .build();

    public ModerationMainMenu(HostMainMenu upperMenu) {
        super(3 * 9, "Gestion de la modération");

        // Add corners to the inventory
        setItems(getBorders(), ItemBuilder.FILLING_PANE);

        // Set the access roles icon
        setItem(12, accessRolesIcon);

        // Set the access teams icon
        setItem(14, accessTeamsIcon);

        // Set the return arrow
        setItem(22, new ItemBuilder(Material.ARROW).name("Retour").build(), event -> upperMenu.open((Player) event.getWhoClicked()));
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
