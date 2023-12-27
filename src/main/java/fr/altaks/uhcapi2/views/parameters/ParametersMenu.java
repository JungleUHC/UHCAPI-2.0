package fr.altaks.uhcapi2.views.parameters;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.GameMode;
import fr.altaks.uhcapi2.core.util.MinecraftDecimalFormat;
import fr.altaks.uhcapi2.views.HostMainMenu;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParametersMenu extends FastInv {

    private HostMainMenu upperMenu;
    private Main main;

    private static final int[] verticalCenteredRows = {
            0,  1,  2,  3,  4,  5,  6,  7,  8,
            45, 46, 47, 48, 49, 50, 51, 52, 53
    };

    private final HashMap<Integer, GameMode.RoleParameter> slotsToParameters = new HashMap<>();

    public ParametersMenu(Main main, HostMainMenu upperMenu) {
        super(6 * 9, "Gestion des Timers");
        this.upperMenu = upperMenu;
        this.main = main;

        if(main.getGameManager().getChosenGameMode() == null){
            Main.logDebug("No game mode is currently loaded, can't open the parameters menu");
            return;
        }

        // Add corners to the inventory
        setItems(verticalCenteredRows, ItemBuilder.FILLING_PANE);

        // Set the return arrow
        setItem(49, new ItemBuilder(Material.ARROW).name("Retour").build(),
                e -> upperMenu.open((Player) e.getWhoClicked())
        );

        // get all the role timers from the game mode
        Main.logDebug("Parameters available : " + main.getGameManager().getChosenGameMode().getRolesParameters().size());

        for(Map.Entry<GameMode.RoleParameter, Object> entry : main.getGameManager().getChosenGameMode().getRolesParameters().entrySet()){
            int slot = this.getInventory().firstEmpty();
            if(slot == -1) {
                Main.logInfo(Main.INFO_CONSOLE_PREFIX + "The parameters menu is full, can't add more parameters");
                break;
            }
            setItem(slot, generateParameterItem(entry.getKey(), entry.getValue()));
            this.slotsToParameters.put(slot, entry.getKey());
        }
    }

    private ItemStack generateParameterItem(GameMode.RoleParameter parameter, Object value){
        final Material itemMaterial = Material.REDSTONE_COMPARATOR;
        final ArrayList<String> itemLore = new ArrayList<>();
        itemLore.add("");

        switch (parameter.getType()) {
            case BOOLEAN:
                itemLore.add(ChatColor.YELLOW + "Valeur actuelle : " + ((boolean) value ? "§aActivé" : "§cDésactivé"));
                itemLore.add("");
                itemLore.add(ChatColor.GRAY + "Cliquez pour changer la valeur");
                itemLore.add("");
                itemLore.add("");
                break;
            case INTEGER:
                itemLore.add(ChatColor.YELLOW + "Valeur actuelle : " + value);
                itemLore.add("");
                itemLore.add(ChatColor.GRAY + "Clic gauche : +1");
                itemLore.add(ChatColor.GRAY + "Clic droit : -1");
                break;
            case DECIMAL:
                itemLore.add(ChatColor.YELLOW + "Valeur actuelle : " + MinecraftDecimalFormat.format((double)value));
                itemLore.add("");
                itemLore.add(ChatColor.GRAY + "Clic gauche : +0.1");
                itemLore.add(ChatColor.GRAY + "Clic droit : -0.1");
                break;
            case PERCENTAGE:
                itemLore.add(ChatColor.YELLOW + "Valeur actuelle : " + MinecraftDecimalFormat.format((double)value * 100d) + " %");
                itemLore.add("");
                itemLore.add(ChatColor.GRAY + "Clic gauche : +1 %");
                itemLore.add(ChatColor.GRAY + "Clic droit : -1 %");
                break;
        }

        return new ItemBuilder(itemMaterial)
                .name(ChatColor.YELLOW + parameter.getName())
                .lore(itemLore)
                .build();
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if(event.getClickedInventory() == null || event.getClickedInventory().equals(event.getView().getBottomInventory())) return;
        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        if(this.slotsToParameters.containsKey(event.getSlot())){
            GameMode.RoleParameter parameter = this.slotsToParameters.get(event.getSlot());
            Object value = main.getGameManager().getChosenGameMode().getRolesParameters().get(parameter);

            switch (parameter.getType()) {
                case BOOLEAN:
                    value = !(boolean) value;
                    break;
                case INTEGER:
                    int modifier = (event.isLeftClick() ? 1 : -1);
                    value = (int) value + modifier;
                    if((int)value < 0) value = 0;
                    break;
                case DECIMAL:
                    double modifierDouble = (event.isLeftClick() ? 0.1 : -0.1);
                    value = (double) value + modifierDouble;
                    if((double)value < 0d) value = 0d;
                    break;
                case PERCENTAGE:
                    double modifierPercentage = (event.isLeftClick() ? 0.01 : -0.01);
                    value = (double) value + modifierPercentage;
                    if((double)value < 0d) value = 0d;
                    break;
            }

            main.getGameManager().getChosenGameMode().getRolesParameters().put(parameter, value);
            updateParameterValueInLore(parameter.getType(), event.getCurrentItem(), value);

            Main.logDebug("Parameter " + parameter.getName() + " updated to " + value);
        }
    }

    private void updateParameterValueInLore(GameMode.GameModeOptionType type, ItemStack item, Object newValue){
        ItemMeta meta = item.getItemMeta();
        List<String> itemLore = meta.getLore();

        switch (type) {
            case BOOLEAN:
                itemLore.set(1 ,ChatColor.YELLOW + "Valeur actuelle : " + ((boolean) newValue ? "§aActivé" : "§cDésactivé"));
                break;
            case INTEGER:
                itemLore.set(1, ChatColor.YELLOW + "Valeur actuelle : " + newValue);
                break;
            case DECIMAL:
                itemLore.set(1, ChatColor.YELLOW + "Valeur actuelle : " + MinecraftDecimalFormat.format((double)newValue));
                break;
            case PERCENTAGE:
                itemLore.set(1, ChatColor.YELLOW + "Valeur actuelle : " + MinecraftDecimalFormat.format((double)newValue * 100d) + " %");
                break;
        }

        meta.setLore(itemLore);
        item.setItemMeta(meta);
    }
}
