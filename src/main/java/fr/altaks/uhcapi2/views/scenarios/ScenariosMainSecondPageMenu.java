package fr.altaks.uhcapi2.views.scenarios;

import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.altaks.uhcapi2.views.HostMainMenu;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ScenariosMainSecondPageMenu extends FastInv {

    private String FIRST_PAGE_VALUE  = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZhMjJjYzZkZGQ1NjlhNmNlODk0YWFiOTA2YjczZGI4YmE4OWY2YTJiYjA3MWJhYjIyZTU3YTRmMDg4NWFiZiJ9fX0=";

    private ItemStack firstPage = HeadBuilder.of(FIRST_PAGE_VALUE)
            .name("Page précédente")
            .build();

    private ScenariosMainMenu upperMenu;

    public ScenariosMainSecondPageMenu(HostMainMenu upperMenu, ScenariosMainMenu secondPage) {
        super(6*9, "Configuration des scenarios");

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        setItem(39, firstPage, e -> secondPage.open((Player) e.getWhoClicked()));

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
