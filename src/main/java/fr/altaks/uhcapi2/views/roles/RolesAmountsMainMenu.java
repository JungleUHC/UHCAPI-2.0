package fr.altaks.uhcapi2.views.roles;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.GameMode;
import fr.altaks.uhcapi2.views.HostMainMenu;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RolesAmountsMainMenu extends FastInv {

    private final Main main;

    private HashMap<Integer, GameMode.Role> slotToRole = new HashMap<>();
    private HashMap<Integer, GameMode.GameTeam> slotToTeam = new HashMap<>();
    private GameMode.GameTeam selectedTeam;

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

        // place team items on the first row according to icons in the config

        int teamSlot = determineCenterSlotGroupFirstSlot(2, 7, main.getGameManager().getChosenGameMode().getRolesFromTeam().keySet().size());
        Main.logDev("First slot : " + teamSlot + " for RolesAmountsMainMenu#constructor:L41");

        for(GameMode.GameTeam team : main.getGameManager().getChosenGameMode().getRolesFromTeam().keySet()){
            if(this.selectedTeam == null){
                this.selectedTeam = team;
            }
            // place team items on the first row according to icons in the config
            setItem(teamSlot, team.getItem());
            slotToTeam.put(teamSlot, team);
            teamSlot++;
        }

        setDisplayedTeam(this.selectedTeam);
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        // Cancel any click you fool
        event.setCancelled(true);

        // Check interaction validity
        if(event.getClickedInventory() == null || event.getClickedInventory().equals(event.getView().getBottomInventory())) return;
        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        // Check if the clicked item is a team item
        if(slotToTeam.containsKey(event.getSlot())){
            Main.logDebug("Player " + event.getWhoClicked().getName() + " clicked on team " + slotToTeam.get(event.getSlot()).getName());
            setDisplayedTeam(slotToTeam.get(event.getSlot()));
            Main.logDebug("Displayed team is now " + this.selectedTeam.getName());
            return;
        }

        // Check if the clicked item is a role item
        if(slotToRole.containsKey(event.getSlot())){
            // Get role from slot
            GameMode.Role role = slotToRole.get(event.getSlot());
            Main.logDebug("Updating role's player amount : " + role.getName());

            int modifier = (event.isLeftClick() ? 1 : -1);
            // apply the modifier to the role's player amount in the game manager
            int previousAmount = main.getGameManager().getChosenGameMode().getPlayersPerRole().get(role);
            int newAmount = previousAmount + modifier;
            if(newAmount < 0) newAmount = 0;

            main.getGameManager().getChosenGameMode().getPlayersPerRole().put(role, newAmount);

            // update the clicked item lore
            ItemMeta meta = event.getCurrentItem().getItemMeta();
            List<String> lore = meta.getLore();
            lore.set(1, ChatColor.YELLOW + "Nombre de joueurs : " + ChatColor.GREEN + newAmount);
            meta.setLore(lore);
            event.getCurrentItem().setItemMeta(meta);

            // if the role amount is now 0, change the item to a gray dye
            if(newAmount == 0){
                event.getCurrentItem().setType(Material.INK_SACK);
                event.getCurrentItem().setDurability((short) 8);
            } else {
                event.getCurrentItem().setType(this.selectedTeam.getItem().getType());
                event.getCurrentItem().setDurability(this.selectedTeam.getItem().getDurability());
            }
        }
    }

    @Override
    protected void onOpen(InventoryOpenEvent event) {
        if(main.getGameManager().getChosenGameMode() == null){
            event.setCancelled(true);
            Main.logDebug("Player " + event.getPlayer().getName() + "tried to open invalid menu\n" +
                    "No game mode is currently loaded, can't open the roles amounts menu");
        }
    }

    private int determineCenterSlotGroupFirstSlot(int min, int max, int size){
        return min + (((max - min) - size) / 2);
    }

    private ArrayList<Integer> getCenteredSlotGroup(){
        ArrayList<Integer> slots = new ArrayList<>();
        for(int row = 1; row <= 4; row++){
            for(int col = 1; col <= 7; col++){
                slots.add((row * 9) + col);
            }
        }
        return slots;
    }

    public void setDisplayedTeam(GameMode.GameTeam team){
        // clear previous mappings
        slotToRole.clear();
        // clear slots
        for(int slot : getCenteredSlotGroup()){
            setItem(slot, null);
        }

        this.selectedTeam = team;

        ArrayList<Integer> centeredSlots = getCenteredSlotGroup();
        Iterator<Integer> slotIterator = centeredSlots.iterator();

        // get all roles from the team and add them to the inventory and the hashmap
        for(GameMode.Role role : main.getGameManager().getChosenGameMode().getRolesFromTeam().get(team)){
            ItemBuilder item;
            // if the role is disabled, use a gray dye, else use the team icon
            int amountOfPlayerOnRole = main.getGameManager().getChosenGameMode().getPlayersPerRole().get(role);

            if(amountOfPlayerOnRole <= 0){
                item = new ItemBuilder(Material.INK_SACK).data(8);
            } else {
                item = new ItemBuilder(team.getItem());
            }
            item.name(role.getName())
                .lore(
                        "",
                        ChatColor.YELLOW + "Nombre de joueurs : " + ChatColor.GREEN + amountOfPlayerOnRole,
                        "",
                        ChatColor.GRAY + "Clic gauche : +1 joueur",
                        ChatColor.GRAY + "Clic droit  : -1 joueur"
                );

            int slot = slotIterator.next();
            setItem(slot, item.build());
            slotToRole.put(slot, role);
        }
    }
}
