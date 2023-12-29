package fr.altaks.uhcapi2.views.game.submenus;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.GameManager;
import fr.altaks.uhcapi2.core.util.LoreUtil;
import fr.altaks.uhcapi2.core.util.MinecraftDecimalFormat;
import fr.altaks.uhcapi2.views.game.GameConfigMainMenu;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class GameBordersSubMenu extends FastInv {

    private GameConfigMainMenu upperMenu;

    private String
            INITIAL_SIZE_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjU5ZThkNDE5NmZlYTgyNzAyNWMyOTI3YTZmY2Q2ZTk4ZDAzMDA1NzM3MTIzOGE3N2FlNGNkZGViY2U4NjQ3NyJ9fX0=",
            FINAL_SIZE_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjdhMDhmOGY0MGIxODkxMGVhZDFlZDlkZDQ0ZGRjYzUwYTkyNDQ1MGI5NjBkZmZjYzc3M2Q1NzYzOGQ2ZGEzZCJ9fX0=",
            SUPP_CONFIG_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODc5ZTU0Y2JlODc4NjdkMTRiMmZiZGYzZjE4NzA4OTQzNTIwNDhkZmVjZDk2Mjg0NmRlYTg5M2IyMTU0Yzg1In19fQ==",
            BORDER_TYPE_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTFjMGU2OWYyY2RiNTQyNmFhZWQ3MTFjZTAzZWE2ZDcwOWM5MmU5YmViODRhZDFkMjJiNTg0YzQ2MWQ1MzM1ZCJ9fX0=",
            BORDER_TIMER_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JiYzA2YThkNmIxNDkyZTQwZjBlN2MzYjYzMmI2ZmQ4ZTY2ZGM0NWMxNTIzNDk5MGNhYTU0MTBhYzNhYzNmZCJ9fX0=",
            BORDER_SPEED_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTJlYjEzZjljMDk5YWQ1MDA3YjU0YTUxNzU4NWRjMjZjOWVjYWY1NDYzMmY2NWQ1MjM0NTcxMTk0ZGFlOWVhOCJ9fX0=";

    private ItemStack initialSize = HeadBuilder.of(INITIAL_SIZE_VALUE)
            .name("Taille initiale")
            .lore(
                    "",
                    ChatColor.YELLOW + "Valeur actuelle : " + ChatColor.GOLD + "1000 blocs",
                    "",
                    ChatColor.GRAY + "Clic gauche : +205 blocs",
                    ChatColor.GRAY + "Clic droit : -250 blocs"
            )
            .build();

    private ItemStack finalSize = HeadBuilder.of(FINAL_SIZE_VALUE)
            .name("Taille finale")
            .lore(
                    "",
                    ChatColor.YELLOW + "Valeur actuelle : " + ChatColor.GOLD + "200 blocs",
                    "",
                    ChatColor.GRAY + "Clic gauche : +205 blocs",
                    ChatColor.GRAY + "Clic droit : -250 blocs"
            )
            .build();

    private ItemStack suppConfig = HeadBuilder.of(SUPP_CONFIG_VALUE)
            .name("Configuration supplémentaire")
            .lore(
                    ChatColor.DARK_PURPLE + "Non implémenté pour le moment"
            )
            .build();

    private ItemStack borderType = HeadBuilder.of(BORDER_TYPE_VALUE)
            .name("Type de bordure")
            .lore(
                    "",
                    ChatColor.YELLOW + "Valeur actuelle : " + ChatColor.GREEN + "Téléportation"
            ).addLore(LoreUtil.wrapLore(
                    ChatColor.GRAY + "Permet de choisir le type de bordure, entre une bordure qui téléporte les joueurs en sécurité, ou une bordure qui les élimine",
                    30
            ))
            .build();

    private ItemStack borderTimer = HeadBuilder.of(BORDER_TIMER_VALUE)
            .name("Temps avant la bordure")
            .lore(
                    "",
                    ChatColor.YELLOW + "Valeur actuelle : " + ChatColor.GOLD + "30 minutes"
            )
            .build();

    private ItemStack borderSpeed = HeadBuilder.of(BORDER_SPEED_VALUE)
            .name("Vitesse de la bordure")
            .lore(
                    "",
                    ChatColor.YELLOW + "Valeur actuelle : " + ChatColor.GOLD + "0.5 bloc(s) / s",
                    "",
                    ChatColor.GRAY + "Clic gauche : +0.1 bloc(s) / s",
                    ChatColor.GRAY + "Clic droit : -0.1 bloc(s) / s"
            )
            .build();


    public GameBordersSubMenu(GameManager manager, GameConfigMainMenu upperMenu) {
        super(5*9, "Gestion des bordures");
        this.upperMenu = upperMenu;

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        // 11 13 15
        setItem(11, initialSize,
                event -> {
                    if(manager.canModifyRules((Player) event.getWhoClicked())) processIncreaseAndDecreaseClick(
                            event,
                            manager.getGameController().getGameBorderController()::setInitialBorderSize,
                            manager.getGameController().getGameBorderController()::getInitialBorderSize,
                            205,
                            250,
                            Float.MAX_VALUE,
                            manager.getGameController().getGameBorderController().getFinalBorderSize(),
                            "blocs"
                    );
                }
        );
        setItem(13, finalSize,
                event -> {
                    if(manager.canModifyRules((Player) event.getWhoClicked()))processIncreaseAndDecreaseClick(
                            event,
                            manager.getGameController().getGameBorderController()::setFinalBorderSize,
                            manager.getGameController().getGameBorderController()::getFinalBorderSize,
                            205,
                            250,
                            manager.getGameController().getGameBorderController().getInitialBorderSize(),
                            1,
                            "blocs"
                    );
                }
        );
        setItem(15, suppConfig);

        // 29 31 33
        setItem(29, borderType,
                event -> {
                    if(manager.canModifyRules((Player) event.getWhoClicked())) swapBorderType(
                            event,
                            manager.getGameController().getGameBorderController()::setSafeBorder,
                            manager.getGameController().getGameBorderController()::isSafeBorder
                    );
                }
        );
        setItem(31, borderTimer,
                event -> {
                    if(manager.canModifyRules((Player) event.getWhoClicked())) processIncreaseAndDecreaseClick(
                            event,
                            manager.getGameController().getGameBorderController()::setTimeBeforeBorderShrink,
                            manager.getGameController().getGameBorderController()::getTimeBeforeBorderShrink,
                            1,
                            1,
                            2 * 60f,
                            0,
                            "minute(s)"
                    );
                }
        );
        setItem(33, borderSpeed,
                event -> {
                    if(manager.canModifyRules((Player) event.getWhoClicked())) processIncreaseAndDecreaseClick(
                            event,
                            manager.getGameController().getGameBorderController()::setBorderShrinkSpeed,
                            manager.getGameController().getGameBorderController()::getBorderShrinkSpeed,
                            0.1f,
                            0.1f,
                            2,
                            0.1f,
                            "bloc(s) / s"
                    );
                }
        );

        // Set the return arrow
        setItem(40, new ItemBuilder(Material.ARROW).name("Retour").build(),
                e -> upperMenu.open((Player) e.getWhoClicked())
        );
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    private void processIncreaseAndDecreaseClick(InventoryClickEvent event, Consumer<Float> setter, Supplier<Float> getter, float increase, float decrease, float maxvalue, float minvalue, String unit) {
        if(event.getClick().isLeftClick()){
            setter.accept(getter.get() + increase);
            if(getter.get() > maxvalue) setter.accept(maxvalue); // limit value
        } else if(event.getClick().isRightClick()){
            setter.accept(getter.get() - decrease);
            if(getter.get() < minvalue) setter.accept(minvalue); // limit value
        }

        // update item and indicate new value
        ItemStack item = event.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(
                "",
                ChatColor.YELLOW + "Valeur actuelle : " + ChatColor.GOLD + MinecraftDecimalFormat.format(getter.get()) + " " + unit,
                "",
                ChatColor.GRAY + "Clic gauche : +" + increase + " " + unit,
                ChatColor.GRAY + "Clic droit : -" + decrease + " " + unit
        ));
        item.setItemMeta(meta);
    }

    public void swapBorderType(InventoryClickEvent event, Consumer<Boolean> setter, Supplier<Boolean> getter) {
        setter.accept(!getter.get()); // swap activation state
        String text = getter.get() ? ChatColor.GREEN + "Téléportation" : ChatColor.RED + "Élimination";

        Main.logDev("Border type is now " + text + "with safeBorder at " + getter.get());

        // update item and indicate new value
        ItemStack item = event.getCurrentItem();
        ItemMeta meta = item.getItemMeta();

        List<String> lore = meta.getLore();
        lore.set(1, ChatColor.YELLOW + "Valeur actuelle : " + text);

        meta.setLore(lore);

        item.setItemMeta(meta);
    }
}