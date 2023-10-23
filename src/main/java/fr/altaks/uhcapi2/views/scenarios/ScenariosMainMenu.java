package fr.altaks.uhcapi2.views.scenarios;

import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.altaks.uhcapi2.views.HostMainMenu;
import fr.altaks.uhcapi2.views.scenarios.scenarios.CatEyes;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ScenariosMainMenu extends FastInv {

    private HostMainMenu upperMenu;

    private ScenariosMainSecondPageMenu scenariosMainSecondPageMenu;

    private String SECOND_PAGE_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjYzMTRkMzFiMDk1ZTRkNDIxNzYwNDk3YmU2YTE1NmY0NTlkOGM5OTU3YjdlNmIxYzEyZGViNGU0Nzg2MGQ3MSJ9fX0=";

    private ItemStack secondPage = HeadBuilder.of(SECOND_PAGE_VALUE)
            .name("Page suivante")
            .build();

    private List<Scenario> scenarios = Arrays.asList(
            new CatEyes()
    );

    public HashMap<ItemStack, Scenario> scenarioItems = new HashMap<>();

    public ScenariosMainMenu(HostMainMenu upperMenu) {
        super(6*9, "Configuration des scenarios");

        scenariosMainSecondPageMenu = new ScenariosMainSecondPageMenu(upperMenu, this);

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        setItem(41, secondPage, e -> scenariosMainSecondPageMenu.open((Player) e.getWhoClicked()));

        for(Scenario scenario : scenarios){
            ItemBuilder itemBuilder = new ItemBuilder(scenario.getIcon())
                    .name(ChatColor.YELLOW + scenario.getName())
                    .lore(ChatColor.GRAY + scenario.getDescription());

            setItem(scenario.getSlot(), itemBuilder.build());
        }

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
