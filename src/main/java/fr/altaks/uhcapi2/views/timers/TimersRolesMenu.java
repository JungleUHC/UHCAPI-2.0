package fr.altaks.uhcapi2.views.timers;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.GameMode;
import fr.altaks.uhcapi2.core.util.LoreUtil;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimersRolesMenu extends FastInv {

    private final Main main;

    public TimersRolesMenu(Main main, TimersMainMenu upperMenu) {
        super(6*9, "Timers des rôles");
        this.main = main;

        // if there's no currently loaded game mode, stop here and show an error in the logs + avoid the inventory opening
        if(main.getGameManager().getChosenGameMode() == null){
            Main.logDebug("No game mode is currently loaded, can't open the roles timers menu");
            return;
        }

        // Add corners to the inventory
        setItems(verticalCenteredRows, ItemBuilder.FILLING_PANE);

        // Set the return arrow
        setItem(49, new ItemBuilder(Material.ARROW).name("Retour").build(),
                e -> upperMenu.open((Player) e.getWhoClicked())
        );

        // get all the role timers from the game mode
        Main.logDebug("Timers available : " + main.getGameManager().getChosenGameMode().getRolesTimers().size());

        // for all available timers, place them in the inventory
        for(Map.Entry<GameMode.RoleTimer, Long> entry : main.getGameManager().getChosenGameMode().getRolesTimers().entrySet()){
            int slot = this.getInventory().firstEmpty();
            if(slot == -1) {
                Main.logInfo(Main.INFO_CONSOLE_PREFIX + "The roles timers menu is full, can't add more timers");
                break;
            }
            setItem(slot, generateTimerItem(entry.getKey(), entry.getValue()));
            main.getGameManager().getTimersController().getSlotsToTimers().put(slot, entry.getKey());
        }
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);

        // Check interaction validity
        if(event.getClickedInventory() == null || event.getClickedInventory().equals(event.getView().getBottomInventory())) return;
        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        if(!main.getGameManager().canModifyRules((Player) event.getWhoClicked())) return;

        // check if the clicked item is a timer item
        if(main.getGameManager().getTimersController().getSlotsToTimers().containsKey(event.getSlot())){
            // Get the timer and the time value
            GameMode.RoleTimer timer = main.getGameManager().getTimersController().getSlotsToTimers().get(event.getSlot());
            Long timeValue = main.getGameManager().getChosenGameMode().getRolesTimers().get(timer);

            int modifier = (event.isLeftClick() ? 1 : -1);
            timeValue += modifier;

            // Check if the time value is valid
            if(timeValue < 0) timeValue = 0L;

            // Update the time value in the game manager & update the item
            main.getGameManager().getChosenGameMode().getRolesTimers().put(timer, timeValue);
            updateTimerValueInLore(event.getCurrentItem(), timeValue, timer.getUnit());

            Main.logDebug("Timer " + timer.getName() + " updated to " + timeValue + " " + timer.getUnit().humanName);
        }
    }

    public ItemStack generateTimerItem(GameMode.RoleTimer timer, Long timeValue){
        GameMode.TimeType timeType = timer.getUnit();

        return new ItemBuilder(Material.WATCH)
                .name("Timer")
                .lore(
                        "",
                        ChatColor.YELLOW + "Temps actuel : " + timeValue + " " + timeType.humanName,
                        "",
                        ChatColor.GRAY + "Clic gauche : +1 " + timeType.humanName,
                        ChatColor.GRAY + "Clic droit : -1 " + timeType.humanName,
                        ""
                )
                .addLore(LoreUtil.wrapLore(timer.getName(), 30))
                .build();
    }

    public void updateTimerValueInLore(ItemStack item, Long newValue, GameMode.TimeType unit){
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        lore.set(1, ChatColor.YELLOW + "Temps actuel : " + newValue + " " + unit.humanName);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private static final int[] verticalCenteredRows = {
            0,  1,  2,  3,  4,  5,  6,  7,  8,
            45, 46, 47, 48, 49, 50, 51, 52, 53
    };
}