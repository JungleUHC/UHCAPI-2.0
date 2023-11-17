package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.util.LoreUtil;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class DiamondLimit implements Scenario {

    public String getName() {
        return "Diamond Limit";
    }

    @Override
    public String getDescription() {
        return "Permet de définir une limite pour la quantité de diamants que les joueurs peuvent collecter.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.DIAMOND);
    }

    @Override
    public int getSlot() {
        return 21;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    private int maxDiamonds = 32;

    private final HashMap<Player, Integer> collectedDiamonds = new HashMap<>();

    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerPicksUpDiamond(PlayerPickupItemEvent event){
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        // if the item is a diamond
        if(event.getItem().getItemStack().getType() == Material.DIAMOND) {

            collectedDiamonds.putIfAbsent(event.getPlayer(), 0);

            // get the amount of diamonds picked up and the amount of diamonds already picked up
            int amount = event.getItem().getItemStack().getAmount();
            int collected = collectedDiamonds.get(event.getPlayer());

            if(collected >= maxDiamonds) {
                event.getItem().setPickupDelay(20 * 5);
                event.getPlayer().sendMessage(Main.MSG_PREFIX + "Vous avez atteint la limite de diamants ramassés (" + maxDiamonds + ")");
                event.setCancelled(true);
                return;
            }

            // if there's an overflow, ajust the picked up amount and the remaining amount in the entity
            if(collected + amount > maxDiamonds) {
                event.setCancelled(true);

                // get the remaining amount and the picked up amount
                int remainingAmount = (collected + amount) - maxDiamonds;
                int pickedUpAmount = amount - remainingAmount;

                // set the remaining amount in the entity and add the picked up amount to the player's inventory
                event.getItem().getItemStack().setAmount(remainingAmount);
                event.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND, pickedUpAmount));
                event.getItem().setPickupDelay(20 * 5);

                // update the collected diamonds hashmap
                collectedDiamonds.put(event.getPlayer(), maxDiamonds);
            } else {

                // update the collected diamonds hashmap
                collectedDiamonds.put(event.getPlayer(), collected + amount);
            }
        }
    }

    @Override
    public void processClick(InventoryClickEvent event) {
        // determine modifier from click
        int modifier = (event.isShiftClick() ? -1 : 1);
        this.maxDiamonds += modifier;

        // floor the value to 0
        if(this.maxDiamonds < 0) {
            this.maxDiamonds = 0;
        }

        // update the lore of the item
        LoreUtil.updateLore(event.getCurrentItem(), -2, ChatColor.YELLOW + "Valeur actuelle : " + ChatColor.GREEN + maxDiamonds);
    }

    @Override
    public String[] getConfigurationLore() {
        return new String[]{
            "",
            ChatColor.GRAY + "Clic droit       : +1 diamant",
            ChatColor.GRAY + "Shift clic droit : -1 diamant",
            "",
            ChatColor.YELLOW + "Valeur actuelle : " + ChatColor.GREEN + maxDiamonds,
            ""
        };
    }
}