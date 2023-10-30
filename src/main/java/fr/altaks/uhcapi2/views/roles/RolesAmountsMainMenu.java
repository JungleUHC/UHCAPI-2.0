package fr.altaks.uhcapi2.views.roles;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.GameMode;
import fr.altaks.uhcapi2.views.HostMainMenu;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class RolesAmountsMainMenu extends FastInv {

    private final Main main;

    public RolesAmountsMainMenu(Main main, HostMainMenu upperMenu) {
        super(6*9, "Nombre de joueurs/rôle");
        this.main = main;

        // if there's no currently loaded game mode, stop here and show an error in the logs + avoid the inventory opening
        if(main.getGameManager().getChosenGameMode() == null){
            Main.logDebug("No game mode is currently loaded, can't open the roles amounts menu");
            return;
        }

        // Add corners to the inventory
        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        // Set the return arrow
        setItem(49, new ItemBuilder(Material.ARROW).name("Retour").build(),
                e -> upperMenu.open((Player) e.getWhoClicked())
        );

        // detect teams for currently loaded game mode
        for(GameMode.GameTeam team : main.getGameManager().getChosenGameMode().getRolesFromTeam().keySet()){
            // place team items on the first row according to icons in the config
            addItem(team.getItem());
        }

    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @Override
    protected void onOpen(InventoryOpenEvent event) {
        if(main.getGameManager().getChosenGameMode() == null){
            event.setCancelled(true);
            Main.logDebug("Player " + event.getPlayer().getName() + "tried to open invalid menu\n" +
                    "No game mode is currently loaded, can't open the roles amounts menu");
        }
    }
}
