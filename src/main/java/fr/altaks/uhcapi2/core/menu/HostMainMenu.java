package fr.altaks.uhcapi2.core.menu;

import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class HostMainMenu extends FastInv {

    private ItemStack gameModeSelector = new ItemBuilder(Material.PAINTING)
            .name(ChatColor.RESET +""+ ChatColor.YELLOW + "Sélection du mode de jeu")
            .build();

    private ItemStack configGame = new ItemBuilder(Material.COMMAND)
            .name(ChatColor.RESET +""+ ChatColor.YELLOW + "Configuration de la partie")
            .build();

    private ItemStack configScenarios = new ItemBuilder(Material.BOOKSHELF)
            .name(ChatColor.RESET +""+ ChatColor.YELLOW + "Configuration des scénarios")
            .build();

    private ItemStack configMod = new ItemBuilder(Material.BARRIER)
            .name(ChatColor.RESET +""+ ChatColor.YELLOW + "Configuration de la modération")
            .build();

    private ItemStack configRoles = new ItemBuilder(Material.ARMOR_STAND)
            .name(ChatColor.RESET +""+ ChatColor.YELLOW + "Configuration des rôles")
            .build();

    private ItemStack configSupp = new ItemBuilder(Material.BEDROCK)
            .name(ChatColor.RESET +""+ ChatColor.YELLOW + "Configuration supplémentaire")
            .build();

    private ItemStack configTimers = new ItemBuilder(Material.WATCH)
            .name(ChatColor.RESET +""+ ChatColor.YELLOW + "Configuration des timers")
            .build();

    private ItemStack configWorld = new ItemBuilder(Material.GRASS)
            .name(ChatColor.RESET +""+ ChatColor.YELLOW + "Configuration du monde")
            .build();

    private String WHITELIST_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY2OTJmOTljYzZkNzgyNDIzMDQxMTA1NTM1ODk0ODQyOThiMmU0YTAyMzNiNzY3NTNmODg4ZTIwN2VmNSJ9fX0=";

    private ItemStack configWhitelist = HeadBuilder.of(WHITELIST_VALUE)
            .name(ChatColor.RESET +""+ ChatColor.YELLOW + "Configuration de la whitelist")
            .build();

    private String START_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODFjZTk1ZGQ0ODBmMTdmZWUwNzFmOWU4ZjdlMmU5YzA1NDJhYzcyYWI1ZDJhZTUzY2NlYWQyYmQ3MjM3MGMyNSJ9fX0=";
    private String STOP_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTkwMjUzYzQ5ZTEzY2MyZGUwMDA5MGVlNjU4MDlkYTYxN2M1M2Y1OWUzNWYwOWE3YzZlMzUwMTFkMTlhY2IzZCJ9fX0=";
    private ItemStack startButton = HeadBuilder.of(START_VALUE)
            .name(ChatColor.RESET +""+ ChatColor.GREEN + "\u00BB Lancer la partie \u00AB")
            .build();

    private ItemStack stopButotn = HeadBuilder.of(STOP_VALUE)
            .name(ChatColor.RESET +""+ ChatColor.RED +"La partie ne peut pas être lancée :(")
            .lore(ChatColor.GRAY + "Veuillez choisir un mode de jeu")
            .build();

    public HostMainMenu() {
        super(6*9, "Menu principal");

        setItems(getCorners(), new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));

        // First line
        setItem(4, gameModeSelector);

        // Second line
        setItem(11, configGame);
        setItem(15, configScenarios);

        // Third line
        setItem(18, configMod);
        setItem(22, configRoles);
        setItem(26, configTimers);

        // Fourth line
        setItem(31, configSupp);

        // Fifth line
        setItem(38, configWorld);
        setItem(42, configWhitelist);

        // Sixth line
        setItem(49, startButton);
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
