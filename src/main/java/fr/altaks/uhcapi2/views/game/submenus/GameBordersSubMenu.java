package fr.altaks.uhcapi2.views.game.submenus;

import fr.altaks.uhcapi2.core.util.LoreUtil;
import fr.altaks.uhcapi2.views.game.GameConfigMainMenu;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
                    ChatColor.YELLOW + "Valeur actuelle : " + ChatColor.GOLD + "1000",
                    "",
                    ChatColor.GRAY + "Clic gauche : +205 blocs",
                    ChatColor.GRAY + "Clic droit : -250 blocs"
            )
            .build();

    private ItemStack finalSize = HeadBuilder.of(FINAL_SIZE_VALUE)
            .name("Taille finale")
            .lore(
                    "",
                    ChatColor.YELLOW + "Valeur actuelle : " + ChatColor.GOLD + "200",
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
                    ChatColor.YELLOW + "Valeur actuelle : " + ChatColor.GOLD + "0.5 bloc/s",
                    "",
                    ChatColor.GRAY + "Clic gauche : +0.1 bloc/s",
                    ChatColor.GRAY + "Clic droit : -0.1 bloc/s"
            )
            .build();


    public GameBordersSubMenu(GameConfigMainMenu upperMenu) {
        super(5*9, "Gestion des bordures");
        this.upperMenu = upperMenu;

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        // 11 13 15
        setItem(11, initialSize);
        setItem(13, finalSize);
        setItem(15, suppConfig);

        // 29 31 33
        setItem(29, borderType);
        setItem(31, borderTimer);
        setItem(33, borderSpeed);

        // Set the return arrow
        setItem(40, new ItemBuilder(Material.ARROW).name("Retour").build(),
                e -> upperMenu.open((Player) e.getWhoClicked())
        );
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}