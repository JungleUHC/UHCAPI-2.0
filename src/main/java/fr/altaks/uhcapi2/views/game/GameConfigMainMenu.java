package fr.altaks.uhcapi2.views.game;

import fr.altaks.uhcapi2.core.GameManager;
import fr.altaks.uhcapi2.core.util.MinecraftDecimalFormat;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GameConfigMainMenu  extends FastInv {

    private final ItemStack stuffConfig = new ItemBuilder(Material.DIAMOND_CHESTPLATE)
            .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            .flags(ItemFlag.HIDE_ENCHANTS)
            .name("Configuration des stuffs")
            .build();

    private final ItemStack borderConfig = new ItemBuilder(Material.IRON_FENCE)
            .name("Configuration de la bordure")
            .build();

    private final ItemStack invsConfig = new ItemBuilder(Material.CHEST)
            .name("Configuration des inventaires")
            .build();

    private final ItemStack xpConfig = new ItemBuilder(Material.EXP_BOTTLE)
            .name("Configuration des niveaux")
            .lore(
                    ChatColor.RESET +""+ ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "XP Boost",
                    "",
                    ChatColor.RESET +""+ ChatColor.BOLD + "DESCRIPTION",
                    ChatColor.RESET +""+ ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "L'expérience récupérée est multipliée",
                    "",
                    ChatColor.RESET +""+ ChatColor.BOLD + "STATUT" + ChatColor.RESET,
                    ChatColor.RESET +""+ ChatColor.DARK_GRAY + "| " + ChatColor.RESET + ChatColor.RED + "Désactivé",
                    "",
                    ChatColor.RESET +""+ ChatColor.BOLD + "OPTIONS" + ChatColor.RESET,
                    ChatColor.RESET +""+ ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "Multiplicateur " + ChatColor.DARK_GRAY + "\u00BB " + ChatColor.RESET + "1,0x",
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
    private final ItemStack mobsConfig = new ItemBuilder(Material.MONSTER_EGG)
            .data(EntityType.CREEPER.getTypeId())
            .name("Configuration des monstres")
            .build();

    public GameStuffSubMenu getStuffSubMenu() {
        return stuffSubMenu;
    }

    private GameStuffSubMenu stuffSubMenu;
    private GameInvsSubMenu invsSubMenu;
    private GameBordersSubMenu bordersSubMenu;
    private GameMobsSubMenu mobsSubMenu;

    private final GameManager manager;

    public GameConfigMainMenu(GameManager manager, HostMainMenu upperMenu) {
        super(5*9, "Configuration de la partie");
        this.stuffSubMenu = new GameStuffSubMenu(manager, this);
        this.invsSubMenu = new GameInvsSubMenu(manager, this);
        this.mobsSubMenu = new GameMobsSubMenu(manager, this);
        this.bordersSubMenu = new GameBordersSubMenu(manager, this);

        this.manager = manager;

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        // 11,   , 15,
        //     22
        // 29,   , 33

        setItem(11, stuffConfig, event -> stuffSubMenu.open((Player) event.getWhoClicked()));
        setItem(15, borderConfig, event -> bordersSubMenu.open((Player) event.getWhoClicked()));

        setItem(22, invsConfig, event -> invsSubMenu.open((Player) event.getWhoClicked()));

        setItem(29, xpConfig, this::processXpBoostClick);
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

    private void processXpBoostClick(InventoryClickEvent event){
        if(!manager.canModifyRules((Player) event.getWhoClicked())) return;
        if(event.isLeftClick()){
            // Toggle xp boost
            if(manager.getGameController().isXpBoostEnabled()){
                // Disable xp boost
                manager.getGameController().setXpBoostEnabled(false);
                changeLoreLine(event.getCurrentItem(), 6, ChatColor.RESET +""+ ChatColor.DARK_GRAY + "| " + ChatColor.RESET + ChatColor.RED + "Désactivé");
            } else {
                // Enable xp boost
                manager.getGameController().setXpBoostEnabled(true);
                changeLoreLine(event.getCurrentItem(), 6, ChatColor.RESET +""+ ChatColor.DARK_GRAY + "| " + ChatColor.RESET + ChatColor.GREEN + "Activé");
            }
        } else {
            if(event.isShiftClick()){
                // Increase multiplier by 0.1
                manager.getGameController().setXpMultiplier(manager.getGameController().getXpMultiplier() + 0.1f);
            } else {
                // Decrease multiplier by 0.1
                manager.getGameController().setXpMultiplier(manager.getGameController().getXpMultiplier() - 0.1f);
            }
            // Update multiplier lore
            changeLoreLine(event.getCurrentItem(), 9, ChatColor.RESET +""+ ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "Multiplicateur " + ChatColor.DARK_GRAY + "\u00BB " + ChatColor.RESET + MinecraftDecimalFormat.format(manager.getGameController().getXpMultiplier()) + "x");
        }
    }

    private void changeLoreLine(ItemStack item, int line, String newLine){
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        lore.set(line, newLine);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

}
