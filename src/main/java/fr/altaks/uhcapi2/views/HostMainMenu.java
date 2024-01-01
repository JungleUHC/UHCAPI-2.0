package fr.altaks.uhcapi2.views;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.GameManager;
import fr.altaks.uhcapi2.views.game.GameConfigMainMenu;
import fr.altaks.uhcapi2.views.gamemode.GameModeSelectionMenu;
import fr.altaks.uhcapi2.views.moderation.ModerationMainMenu;
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
import org.bukkit.event.inventory.InventoryOpenEvent;
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

    private ItemStack configParameters = new ItemBuilder(Material.REDSTONE_COMPARATOR)
            .name(ChatColor.RESET +""+ ChatColor.YELLOW + "Configuration des paramètres")
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

    private ItemStack stopButton = HeadBuilder.of(STOP_VALUE)
            .name(ChatColor.RESET +""+ ChatColor.RED +"La partie ne peut pas être lancée :(")
            .lore(ChatColor.GRAY + "Veuillez choisir un mode de jeu")
            .build();

    private WorldMainMenu worldMainMenu;
    private TimersMainMenu timersMainMenu;

    public GameConfigMainMenu getGameConfigMainMenu() {
        return gameConfigMainMenu;
    }

    private GameConfigMainMenu gameConfigMainMenu;
    private GameModeSelectionMenu gamemodeSelectionMenu;
    private ScenariosMainMenu scenariosMainMenu;
    private ModerationMainMenu moderationMainMenu = new ModerationMainMenu(this);

    private Main main;

    public HostMainMenu(GameManager manager, Main main) {
        super(6*9, "Menu principal");
        this.worldMainMenu = new WorldMainMenu(manager, this);
        this.gameConfigMainMenu = new GameConfigMainMenu(manager, this);
        this.scenariosMainMenu = new ScenariosMainMenu(manager, this);

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        // First line
        setItem(4, gameModeSelector, event -> gamemodeSelectionMenu.open((Player) event.getWhoClicked()));

        // Second line
        setItem(11, configGame, event -> gameConfigMainMenu.open((Player) event.getWhoClicked()));
        setItem(15, configScenarios, event -> scenariosMainMenu.open((Player) event.getWhoClicked()));

        // Third line
        setItem(18, configMod, event -> moderationMainMenu.open((Player) event.getWhoClicked()));
        setItem(22, configRoles, event -> {
            // if the role menu is set, open it, otherwise say that the game mode is not loaded
            if(main.getGameManager().getRolesAmountsMainMenu() != null){
                main.getGameManager().getRolesAmountsMainMenu().open((Player) event.getWhoClicked());
            } else {
                event.getWhoClicked().sendMessage(Main.MSG_PREFIX + ChatColor.RED + "Veuillez choisir un mode de jeu avant de configurer les rôles");
            }
        });
        setItem(26, configTimers, event -> timersMainMenu.open((Player) event.getWhoClicked()));

        // Fourth line
        setItem(31, configSupp, event -> {
            event.getWhoClicked().sendMessage(Main.MSG_PREFIX + ChatColor.RED + "Veuillez choisir un mode de jeu avant de configurer les paramètres supplémentaires");
        });

        // Fifth line
        setItem(38, configWorld, event -> worldMainMenu.open((Player) event.getWhoClicked()));
        setItem(42, configWhitelist);

        // Sixth line
        setItem(49, stopButton);

        this.main = main;
        gamemodeSelectionMenu = new GameModeSelectionMenu(this.main);
        timersMainMenu = new TimersMainMenu(this.main, this);
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @Override
    protected void onOpen(InventoryOpenEvent e) {
        if(main.getGameManager().getChosenGameMode() != null){
            if(main.getGameManager().getChosenGameMode().getRolesParameters().isEmpty()){
                setItem(31, configParameters, event -> event.getWhoClicked().sendMessage(Main.MSG_PREFIX + ChatColor.RED + "Ce mode de jeu n'a pas de paramètres supplémentaires"));
            } else {
                setItem(31, configParameters, event -> main.getGameManager().getParametersMenu().open((Player) event.getWhoClicked()));
            }
        } else {
            setItem(31, configSupp, event -> event.getWhoClicked().sendMessage(Main.MSG_PREFIX + ChatColor.RED + "Veuillez choisir un mode de jeu avant de configurer les paramètres supplémentaires"));
        }
    }

    public TimersMainMenu getTimersMainMenu() {
        return timersMainMenu;
    }
}
