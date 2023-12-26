package fr.altaks.uhcapi2.views.timers;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.HostMainMenu;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TimersMainMenu extends FastInv {

    private HostMainMenu upperMenu;

    private ItemStack pvpConfig = new ItemBuilder(Material.IRON_SWORD)
            .name("Activation du PvP")
            .lore(
                    "",
                    ChatColor.YELLOW + "Temps actuel : " + 20 + " minutes",
                    "",
                    ChatColor.GRAY + "Clic gauche : +1 minute",
                    ChatColor.GRAY + "Clic droit : -1 minute"
            )
            .flags(ItemFlag.HIDE_ATTRIBUTES)
            .build();

    private ItemStack timersRoles = new ItemBuilder(Material.ARMOR_STAND)
            .name("Gestion des timers des rôles")
            .lore(
                    "",
                    ChatColor.YELLOW + "Temps avant attribution des rôles : " + 20 + " minutes",
                    "",
                    ChatColor.GRAY + "Clic gauche : +1 minute",
                    ChatColor.GRAY + "Clic droit : -1 minute",
                    "",
                    ChatColor.GRAY + "Shift + Clic droit : Timers de chaque rôle"
            )
            .build();

    private ItemStack invincibilityConfig = new ItemBuilder(Material.GOLDEN_APPLE)
            .name("Invincibilité")
            .lore(
                    "",
                    ChatColor.YELLOW + "Temps actuel : " + 20 + " minutes",
                    "",
                    ChatColor.GRAY + "Clic gauche : +1 minute",
                    ChatColor.GRAY + "Clic droit : -1 minute"
            )
            .build();

    private Main main;

    public TimersMainMenu(Main main, HostMainMenu upperMenu) {
        super(5*9, "Gestion des Timers");
        this.upperMenu = upperMenu;
        this.main = main;

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        setItem(20, pvpConfig, event -> processClick(event, main.getGameManager().getTimersController()::setMinutesBeforePvp, main.getGameManager().getTimersController()::getMinutesBeforePvp, 0));
        setItem(22, timersRoles, event -> {
            if(event.isShiftClick() && event.isRightClick()){
                // if the role menu is set, open it, otherwise say that the game mode is not loaded
                if(main.getGameManager().getTimersRolesMenu() != null){
                    main.getGameManager().getTimersRolesMenu().open((Player) event.getWhoClicked());
                }
            } else if(currentlySelectedGameModeContainsRoles()){
                int modifier = (event.isLeftClick() ? 1 : -1);
                int newValue = main.getGameManager().getTimersController().getMinutesBeforeRolesAreGiven() + modifier;

                if(newValue < 0) newValue = 0;

                main.getGameManager().getTimersController().setMinutesBeforeRolesAreGiven(newValue);

                // update item lore
                ItemStack item = event.getCurrentItem();
                ItemBuilder itemBuilder = new ItemBuilder(item);
                itemBuilder.lore(
                        "",
                        ChatColor.YELLOW + "Temps avant attribution des rôles : " + newValue + " minutes",
                        "",
                        ChatColor.GRAY + "Clic gauche : +1 minute",
                        ChatColor.GRAY + "Clic droit : -1 minute",
                        "",
                        ChatColor.GRAY + "Shift + Clic droit : Timers de chaque rôle"
                );
                item.setItemMeta(itemBuilder.build().getItemMeta());
            } else {
                event.getWhoClicked().sendMessage(Main.MSG_PREFIX + ChatColor.RED + "Veuillez choisir un mode de jeu avant de configurer les timers des rôles");
            }
        });
        setItem(24, invincibilityConfig, event -> processClick(event, main.getGameManager().getTimersController()::setMinutesBeforeInvincibilityEnds, main.getGameManager().getTimersController()::getMinutesBeforeInvincibilityEnds, 0));

        // Set the return arrow
        setItem(40, new ItemBuilder(Material.ARROW).name("Retour").build(),
                e -> upperMenu.open((Player) e.getWhoClicked())
        );
    }

    private boolean currentlySelectedGameModeContainsRoles(){
        return main.getGameManager().getChosenGameMode() != null
                && main.getGameManager().getChosenGameMode().getPlayersPerRole() != null
                && !main.getGameManager().getChosenGameMode().getPlayersPerRole().isEmpty();
    }

    @Override
    protected void onOpen(InventoryOpenEvent event) {
        // Make sure the lore of the role timers icon is updated
        ItemStack item = this.getInventory().getItem(22);
        ItemBuilder itemBuilder = new ItemBuilder(item);

        if(main.getGameManager().getChosenGameMode() != null){
            itemBuilder.lore(
                    "",
                    currentlySelectedGameModeContainsRoles() ? (ChatColor.YELLOW + "Temps avant attribution des rôles : " + main.getGameManager().getTimersController().getMinutesBeforeRolesAreGiven()  + " minutes") : (ChatColor.RED + "Aucun rôle n'est configuré pour ce mode de jeu"),
                    "",
                    ChatColor.GRAY + "Clic gauche : +1 minute",
                    ChatColor.GRAY + "Clic droit : -1 minute",
                    "",
                    ChatColor.GRAY + "Shift + Clic droit : Timers de chaque rôle"
            );
        } else {
            itemBuilder.lore(
                    ChatColor.RED + "Aucun mode de jeu configuré pour le moment"
            );
        }

    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    private void processClick(InventoryClickEvent event, Consumer<Integer> setter, Supplier<Integer> getter, int minValue){
        int modifier = (event.isLeftClick() ? 1 : -1);
        int newValue = getter.get() + modifier;

        if(newValue < minValue) newValue = minValue;

        setter.accept(newValue);

        // update item lore
        ItemStack item = event.getCurrentItem();
        ItemBuilder itemBuilder = new ItemBuilder(item);
        itemBuilder.lore(
                "",
                ChatColor.YELLOW + "Temps actuel : " + newValue + " minutes",
                "",
                ChatColor.GRAY + "Clic gauche : +1 minute",
                ChatColor.GRAY + "Clic droit : -1 minute"
        );
        item.setItemMeta(itemBuilder.build().getItemMeta());
    }

}
