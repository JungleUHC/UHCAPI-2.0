package fr.altaks.uhcapi2.controllers.game;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.GameManager;
import fr.altaks.uhcapi2.core.IController;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GameInvsController implements IController {

    private Main main;

    private CloneableInventory startingInventory, deathDropsInventory;

    public GameInvsController(Main main){
        this.main = main;
    }

    @Override
    public void onGameStart() {
        Bukkit.getPluginManager().registerEvents(this, main);

        if(this.startingInventory != null) for(Player player : Bukkit.getOnlinePlayers()){
            if(player.getGameMode() != GameMode.SPECTATOR){
                this.startingInventory.cloneToPlayer(player);
                Main.logDebug("Player " + player.getName() + " has been given its starting inventory");
            }
        }
    }

    @EventHandler
    public void onPlayersDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        if(player.getGameMode() == GameMode.SPECTATOR) return;

        Main.logDebug("Player " + player.getName() + " has died, dropping its death inventory");
        if(this.deathDropsInventory != null) this.deathDropsInventory.dropItemsAsPlayer(player);
    }

    public static final String chatDelimiter = ChatColor.GRAY +""+ ChatColor.STRIKETHROUGH + "----------------------------------------";
    private final Set<Player> playersConfiguringStartInv = new HashSet<>();
    private final Set<Player> playersConfiguringDeathInv = new HashSet<>();

    public void preparePlayerForInventoryCloning(Player player, boolean isForStartInv){
        if(isForStartInv){
            if(playersConfiguringStartInv.contains(player)){
                Main.logDebug("Player " + player.getName() + " is already configuring the start inventory");
                return;
            }
            playersConfiguringStartInv.add(player);
        } else {
            if(playersConfiguringDeathInv.contains(player)){
                Main.logDebug("Player " + player.getName() + " is already configuring the death inventory");
                return;
            }
            playersConfiguringDeathInv.add(player);
        }

        Main.logDebug("Player " + player.getName() + " has started the inventory cloning process for " + (isForStartInv ? "start" : "death") + " inventory");

        player.closeInventory();
        player.getInventory().clear();
        player.setGameMode(GameMode.CREATIVE);

        // Send the player a validation message that will be handled by a command executor : '/validate startinv' or '/validate deathinv'
        TextComponent message = new TextComponent(chatDelimiter);
        TextComponent validate = getValidate(isForStartInv);

        message.addExtra(validate);
        message.addExtra(new TextComponent(chatDelimiter));

        player.spigot().sendMessage(message);
    }

    private static TextComponent getValidate(boolean isForStartInv) {
        TextComponent validate = new TextComponent("\n" + ChatColor.GOLD + "Veuillez valider l'inventaire " + (isForStartInv ? "de départ" : "de décès") + " en cliquant ici.\n");

        validate.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/validate " + (isForStartInv ? "startinv" : "deathinv")));
        validate.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(ChatColor.YELLOW + "Cliquez ici pour valider l'inventaire " + (isForStartInv ? "de départ" : "de décès"))}));
        return validate;
    }

    public void validateInventoryCloning(Player player, boolean isForStartInv){
        if(isForStartInv){
            if(!playersConfiguringStartInv.contains(player)){
                Main.logDebug("Player " + player.getName() + " is not configuring the start inventory");
                return;
            }
            playersConfiguringStartInv.remove(player);
        } else {
            if(!playersConfiguringDeathInv.contains(player)){
                Main.logDebug("Player " + player.getName() + " is not configuring the death inventory");
                return;
            }
            playersConfiguringDeathInv.remove(player);
        }
        Main.logDebug("Player " + player.getName() + " has validated the inventory cloning process for " + (isForStartInv ? "start" : "death") + " inventory");

        Main.logDebug("Saving the inventory of player " + player.getName() + " for " + (isForStartInv ? "start" : "death") + " inventory");

        Main.logDev("Detected inventory content :");
        for(ItemStack item : player.getInventory().getContents()){
            if(item != null) Main.logDev(item.toString());
        }

        // Saving the inventory
        HashMap<Integer, ItemStack> items = new HashMap<>();
        for(int slot = 0; slot < player.getInventory().getContents().length; slot++){
            items.put(slot, player.getInventory().getContents()[slot]);
        }

        Main.logDebug("Player " + player.getName() + " had " + items.size() + " items in its inventory");
        if(Main.isDevMode){
            for(Map.Entry<Integer, ItemStack> entry : items.entrySet()){
                if(entry.getValue() != null) Main.logDebug("Item " + entry.getKey() + " : " + entry.getValue());
            }
        }

        if(isForStartInv){
            startingInventory = new CloneableInventory(items);
        } else {
            deathDropsInventory = new CloneableInventory(items);
        }

        Main.logDebug("Player's inventory has been saved");

        // Give back host or cohost items to player
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);

        if(main.getGameManager().getHost().equals(player)){
            main.getGameManager().giveHostItems(player);
        } else if(main.getGameManager().getCoHosts().contains(player)){
            main.getGameManager().giveCoHostItems(player);
        }

        player.sendMessage(Main.MSG_PREFIX + "§aVotre inventaire a été sauvegardé en tant qu'inventaire " + (isForStartInv ? "de départ" : "de décès") + ".");
    }

    public CloneableInventory getStartingInventory() {
        return startingInventory;
    }

    public CloneableInventory getDeathDropsInventory() {
        return deathDropsInventory;
    }

    public static class CloneableInventory {

        public HashMap<Integer, ItemStack> getItems() {
            return items;
        }

        private final HashMap<Integer, ItemStack> items;

        public CloneableInventory(HashMap<Integer, ItemStack> items){
            this.items = items;
        }

        public void cloneToPlayer(Player player){
            // copy the items to the player inventory
            for(Map.Entry<Integer, ItemStack> slotToItemEntry : items.entrySet()){
                player.getInventory().setItem(slotToItemEntry.getKey(), slotToItemEntry.getValue());
            }
        }

        public void dropItemsAsPlayer(Player player){
            // drop the specified items if they aren't in the player's inventory at death

            List<ItemStack> playersInv = Arrays.asList(player.getInventory().getContents());

            for(Map.Entry<Integer, ItemStack> slotToItemEntry : items.entrySet()){
                if(!playersInv.contains(slotToItemEntry.getValue())){
                    player.getWorld().dropItemNaturally(player.getLocation(), slotToItemEntry.getValue());
                    Main.logDev("Dropping item " + slotToItemEntry.getValue() + " as player " + player.getName() + " died.");
                }
            }
        }

    }
}
