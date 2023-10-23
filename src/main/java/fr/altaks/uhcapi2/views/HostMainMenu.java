package fr.altaks.uhcapi2.views;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.game.GameConfigMainMenu;
import fr.altaks.uhcapi2.views.gamemode.GameModeSelectionMenu;
import fr.altaks.uhcapi2.views.scenarios.ScenariosMainMenu;
import fr.altaks.uhcapi2.views.timers.TimersMainMenu;
import fr.altaks.uhcapi2.views.world.WorldMainMenu;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

    private WorldMainMenu worldMainMenu = new WorldMainMenu(this);
    private TimersMainMenu timersMainMenu = new TimersMainMenu(this);
    private GameConfigMainMenu gameConfigMainMenu = new GameConfigMainMenu(this);
    private GameModeSelectionMenu gamemodeSelectionMenu;
    private ScenariosMainMenu scenariosMainMenu = new ScenariosMainMenu(this);

    private Main main;

    public HostMainMenu(Main main) {
        super(6*9, "Menu principal");

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        // First line
        setItem(4, gameModeSelector, event -> gamemodeSelectionMenu.open((Player) event.getWhoClicked()));

        // Second line
        setItem(11, configGame, event -> gameConfigMainMenu.open((Player) event.getWhoClicked()));
        setItem(15, configScenarios, event -> scenariosMainMenu.open((Player) event.getWhoClicked()));

        // Third line
        setItem(18, configMod);
        setItem(22, configRoles);
        setItem(26, configTimers, event -> timersMainMenu.open((Player) event.getWhoClicked()));

        // Fourth line
        setItem(31, configSupp);

        // Fifth line
        setItem(38, configWorld, event -> worldMainMenu.open((Player) event.getWhoClicked()));
        setItem(42, configWhitelist);

        // Sixth line
        setItem(49, startButton);

        this.main = main;
        gamemodeSelectionMenu = new GameModeSelectionMenu(this.main);
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
