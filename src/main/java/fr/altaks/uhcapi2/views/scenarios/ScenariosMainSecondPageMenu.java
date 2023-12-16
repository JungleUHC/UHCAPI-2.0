package fr.altaks.uhcapi2.views.scenarios;

import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.altaks.uhcapi2.views.HostMainMenu;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ScenariosMainSecondPageMenu extends FastInv {

    private final String FIRST_PAGE_VALUE  = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZhMjJjYzZkZGQ1NjlhNmNlODk0YWFiOTA2YjczZGI4YmE4OWY2YTJiYjA3MWJhYjIyZTU3YTRmMDg4NWFiZiJ9fX0=";

    private final ItemStack firstPageItem = HeadBuilder.of(FIRST_PAGE_VALUE)
            .name("Page précédente")
            .build();

    private ScenariosMainMenu firstPage;

    public ScenariosMainSecondPageMenu(HostMainMenu upperMenu, ScenariosMainMenu firstPage) {
        super(6*9, "Configuration des scenarios");

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        setItem(39, this.firstPageItem, e -> firstPage.open((Player) e.getWhoClicked()));

        // Set the return arrow
        setItem(49, new ItemBuilder(Material.ARROW).name("Retour").build(),
                e -> upperMenu.open((Player) e.getWhoClicked())
        );

        this.firstPage = firstPage;
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);

        // checks to avoid NPEs
        if(event.getClickedInventory() == null || event.getView().getBottomInventory() == event.getClickedInventory()) return;
        if(!firstPage.getScenariosSlots().containsKey(event.getSlot() + 6*9)) return;
        if(Arrays.asList(49, 39).contains(event.getSlot())) return; // return arrow or second page

        Scenario scenario = firstPage.getScenariosSlots().get(event.getSlot() + 6*9);
        if(scenario == null) throw new RuntimeException("Scenario not found");

        if(event.isRightClick() && scenario.isConfigurable()) {
            scenario.processClick(event);
        } else {
            // add glowing effect to the item if it was in the selected scenarios list
            ItemStack item = event.getCurrentItem();

            firstPage.getManager().getScenariosController().switchScenarioActivationState(scenario);
            firstPage.changeItemVisualActivationState(scenario, item, firstPage.getManager().getScenariosController().getScenariosToEnable().contains(scenario));
        }
    }
}
