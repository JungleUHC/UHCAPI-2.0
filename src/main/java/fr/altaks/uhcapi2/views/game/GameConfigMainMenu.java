package fr.altaks.uhcapi2.views.game;

import fr.altaks.uhcapi2.views.HostMainMenu;
import fr.altaks.uhcapi2.views.game.submenus.GameBordersSubMenu;
import fr.altaks.uhcapi2.views.game.submenus.GameInvsSubMenu;
import fr.altaks.uhcapi2.views.game.submenus.GameMobsSubMenu;
import fr.altaks.uhcapi2.views.game.submenus.GameStuffSubMenu;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class GameConfigMainMenu  extends FastInv {

    private HostMainMenu upperMenu;

    private ItemStack stuffConfig = new ItemBuilder(Material.DIAMOND_CHESTPLATE)
            .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            .flags(ItemFlag.HIDE_ENCHANTS)
            .name("Configuration des stuffs")
            .build();

    private ItemStack borderConfig = new ItemBuilder(Material.IRON_FENCE)
            .name("Configuration de la bordure")
            .build();

    private ItemStack invsConfig = new ItemBuilder(Material.CHEST)
            .name("Configuration des inventaires")
            .build();

    private ItemStack xpConfig = new ItemBuilder(Material.EXP_BOTTLE)
            .name("Configuration des niveaux")
            .lore(
                    ChatColor.RESET +""+ ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "XP Boost",
                    "",
                    ChatColor.RESET +""+ ChatColor.BOLD + "DESCRIPTION",
                    ChatColor.RESET +""+ ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "L'expérience récupérée est multipliée",
                    "",
                    ChatColor.RESET +""+ ChatColor.BOLD + "STATUT" + ChatColor.RESET,
                    ChatColor.RESET +""+ ChatColor.DARK_GRAY + "| " + ChatColor.DARK_PURPLE + "Non implémenté",
                    "",
                    ChatColor.RESET +""+ ChatColor.BOLD + "OPTIONS" + ChatColor.RESET,
                    ChatColor.RESET +""+ ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "Multiplicateur " + ChatColor.DARK_GRAY + "\u00BB " + ChatColor.RESET + "#.#x",
                    "",
                    ChatColor.RESET +""+ ChatColor.BOLD + "Activer/Désactiver : " + ChatColor.RESET,
                    ChatColor.RESET +""+ ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Clic gauche",
                    "",
                    ChatColor.RESET +""+ ChatColor.BOLD + "Configurer XP Boost :" + ChatColor.RESET,
                    ChatColor.RESET +""+ ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Clic droit",
                    ""
            )
            .build();

    @SuppressWarnings("deprecation")
    private ItemStack mobsConfig = new ItemBuilder(Material.MONSTER_EGG)
            .data(EntityType.CREEPER.getTypeId())
            .name("Configuration des monstres")
            .build();

    private GameStuffSubMenu stuffSubMenu = new GameStuffSubMenu(this);
    private GameInvsSubMenu invsSubMenu = new GameInvsSubMenu(this);
    private GameBordersSubMenu bordersSubMenu = new GameBordersSubMenu(this);
    private GameMobsSubMenu mobsSubMenu = new GameMobsSubMenu(this);

    public GameConfigMainMenu(HostMainMenu upperMenu) {
        super(5*9, "Configuration de la partie");
        this.upperMenu = upperMenu;

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        // 11,   , 15,
        //     22
        // 29,   , 33

        setItem(11, stuffConfig, event -> stuffSubMenu.open((Player) event.getWhoClicked()));
        setItem(15, borderConfig, event -> bordersSubMenu.open((Player) event.getWhoClicked()));

        setItem(22, invsConfig, event -> invsSubMenu.open((Player) event.getWhoClicked()));

        setItem(29, xpConfig);
        setItem(33, mobsConfig, event -> mobsSubMenu.open((Player) event.getWhoClicked()));

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
