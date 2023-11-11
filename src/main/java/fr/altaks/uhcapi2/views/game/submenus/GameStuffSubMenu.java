package fr.altaks.uhcapi2.views.game.submenus;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.controllers.GameController;
import fr.altaks.uhcapi2.controllers.game.GameStuffController;
import fr.altaks.uhcapi2.core.GameManager;
import fr.altaks.uhcapi2.views.game.GameConfigMainMenu;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GameStuffSubMenu extends FastInv {

    private GameConfigMainMenu upperMenu;

    private ItemStack swordConfig = new ItemBuilder(Material.IRON_SWORD)
            .name("Limites des épées")
            .lore(
                    "",
                    ChatColor.RESET +""+ ChatColor.GRAY + "Limites : Valeurs par défaut",
                    ""
            )
            .flags(ItemFlag.HIDE_ATTRIBUTES)
            .build();

    private ItemStack armorConfig = new ItemBuilder(Material.IRON_CHESTPLATE)
            .name("Limites des armures")
            .lore(
                    "",
                    ChatColor.RESET +""+ ChatColor.GRAY + "Limites : Valeurs par défaut",
                    ""
            )
            .flags(ItemFlag.HIDE_ATTRIBUTES)
            .build();

    private ItemStack bowConfig = new ItemBuilder(Material.BOW)
            .name("Limites des arcs")
            .lore(
                    "",
                    ChatColor.RESET +""+ ChatColor.GRAY + "Limites : Valeurs par défaut",
                    ""
            )
            .build();

    private ItemStack pearlConfig = new ItemBuilder(Material.ENDER_PEARL)
            .name("Activation des perles")
            .lore(
                    "",
                    ChatColor.RESET +""+ ChatColor.YELLOW + "Etat : " + ChatColor.GREEN + "Activées",
                    "",
                    ChatColor.GRAY + "Cliquez pour activer/désactiver les perles de l'End"
            )
            .build();

    private ItemStack bucketConfig = new ItemBuilder(Material.BUCKET)
            .name("Activation des seaux")
            .lore(
                    "",
                    ChatColor.RESET +""+ ChatColor.YELLOW + "Seau d'eau : " + ChatColor.GREEN + "Activé",
                    ChatColor.RESET +""+ ChatColor.YELLOW + "Seau de lave : " + ChatColor.GREEN + "Activé",
                    "",
                    ChatColor.GRAY + "Clic gauche : Activer/Désactiver le seau d'eau",
                    ChatColor.GRAY + "Clic droit : Activer/Désactiver le seau de lave"
            )
            .build();

    private GameManager manager;

    public GameStuffSubMenu(GameManager manager, GameConfigMainMenu upperMenu) {
        super(5*9, "Création du monde");
        this.upperMenu = upperMenu;
        this.manager = manager;

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        // 11 13 15 30 32
        setItem(11, swordConfig, event -> manager.getGameController().getGameStuffController().getSwordsLimitsInventory().open((Player) event.getWhoClicked()));
        setItem(13, armorConfig, event -> manager.getGameController().getGameStuffController().getArmorsLimitsInventory().open((Player) event.getWhoClicked()));
        setItem(15, bowConfig, event -> manager.getGameController().getGameStuffController().getBowsLimitsInventory().open((Player) event.getWhoClicked()));

        setItem(30, pearlConfig, this::processPearlConfigClick);
        setItem(32, bucketConfig, this::processBucketConfigClick);

        // Set the return arrow
        setItem(40, new ItemBuilder(Material.ARROW).name("Retour").build(),
                e -> upperMenu.open((Player) e.getWhoClicked())
        );
    }

    public ItemStack getSwordConfigIcon() {
        return this.getInventory().getItem(11);
    }

    public ItemStack getArmorConfigIcon() {
        return this.getInventory().getItem(13);
    }

    public ItemStack getBowConfigIcon() {
        return this.getInventory().getItem(15);
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    private void processPearlConfigClick(InventoryClickEvent event){
        // switch the pearl state and change lore of the item
        manager.getGameController().getGameStuffController().setAreEnderPearlEnabled(
                !manager.getGameController().getGameStuffController().areEnderPearlEnabled()
        );

        ItemStack item = event.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(
                "",
                ChatColor.RESET +""+ ChatColor.YELLOW + "Etat : " + (manager.getGameController().getGameStuffController().areEnderPearlEnabled() ? ChatColor.GREEN + "Activées" : ChatColor.RED + "Désactivées"),
                "",
                ChatColor.GRAY + "Cliquez pour activer/désactiver les perles de l'End"
        ));

        item.setItemMeta(meta);
    }

    private void processBucketConfigClick(InventoryClickEvent event){
        // process the click :
        // - left click -> switch water bucket activation state
        // - right click -> switch lava bucket activation state

        if(event.isLeftClick()){
            manager.getGameController().getGameStuffController().setWaterBucketEnabled(
                    !manager.getGameController().getGameStuffController().areWaterBucketEnabled()
            );
        } else if(event.isRightClick()){
            manager.getGameController().getGameStuffController().setLavaBucketEnabled(
                    !manager.getGameController().getGameStuffController().areLavaBucketEnabled()
            );
        }

        ItemStack item = event.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(
                "",
                ChatColor.RESET + "" + ChatColor.YELLOW + "Seau d'eau : " + (
                        (manager.getGameController().getGameStuffController().areWaterBucketEnabled()) ?
                                ChatColor.GREEN + "Activé" :
                                ChatColor.RED + "Désactivé"
                ),
                ChatColor.RESET + "" + ChatColor.YELLOW + "Seau de lave : " + (
                        (manager.getGameController().getGameStuffController().areLavaBucketEnabled()) ?
                                ChatColor.GREEN + "Activé" :
                                ChatColor.RED + "Désactivé"
                ),
                "",
                ChatColor.GRAY + "Clic gauche : Activer/Désactiver le seau d'eau",
                ChatColor.GRAY + "Clic droit : Activer/Désactiver le seau de lave"
        ));

        item.setItemMeta(meta);
    }

    public void updateEnchantsLimitsLore(ItemStack item, HashMap<Enchantment, Integer> enchants){
        // update the lore of the enchantment limits inventory according to the controller

        Main.logDev("Updating enchants limits lore for item " + item);

        ItemMeta meta = item.getItemMeta();

        // Nullify the previous lore
        ArrayList<String> newLore = new ArrayList<>();

        newLore.add("");
        newLore.add(ChatColor.RESET + "Nouvelles limites : ");
        newLore.add("");

        for(Map.Entry<Enchantment, Integer> entry : enchants.entrySet()){
            if(entry.getValue() != 0){
                newLore.add(ChatColor.RESET + "" + ChatColor.GRAY + "| " + manager.getGameController().getEnchantmentName(entry.getKey()) + " " + entry.getValue());
            } else
                newLore.add(ChatColor.RESET + "" + ChatColor.GRAY + "| " + manager.getGameController().getEnchantmentName(entry.getKey()) + " " + ChatColor.RED + "Désactivé");

        }

        meta.setLore(newLore);

        item.setItemMeta(meta);
    }
}