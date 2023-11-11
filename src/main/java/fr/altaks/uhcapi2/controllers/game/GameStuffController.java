package fr.altaks.uhcapi2.controllers.game;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.GameManager;
import fr.altaks.uhcapi2.core.IController;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class GameStuffController implements IController {

    private Main main;

    // Initialize default limits
    private ItemStack swordTestItem = new ItemStack(Material.DIAMOND_SWORD);
    private ItemStack armorTestItem = new ItemStack(Material.DIAMOND_CHESTPLATE);
    private ItemStack bowTestItem = new ItemStack(Material.BOW);

    private HashMap<Enchantment, Integer> swordsLimits = new HashMap<>();
    private HashMap<Enchantment, Integer> armorsLimits = new HashMap<>();
    private HashMap<Enchantment, Integer> bowsLimits = new HashMap<>();

    private float enderPearlDropRateMultiplicator = 1.0f;

    public void setAreEnderPearlEnabled(boolean areEnderPearlEnabled) {
        this.areEnderPearlEnabled = areEnderPearlEnabled;
    }

    private boolean areEnderPearlEnabled = true;
    private boolean areWaterBucketEnabled = true;
    private boolean areLavaBucketEnabled = true;

    private FastInv swordsLimitsInventory, armorsLimitsInventory, bowsLimitsInventory;

    private GameManager manager;

    public GameStuffController(Main main){
        this.main = main;

        for(Enchantment enchantment : Enchantment.values()){
            if(enchantment.canEnchantItem(swordTestItem)){
                swordsLimits.put(enchantment, enchantment.getMaxLevel());
            }
            if(enchantment.canEnchantItem(armorTestItem)){
                armorsLimits.put(enchantment, enchantment.getMaxLevel());
            }
            if(enchantment.canEnchantItem(bowTestItem)){
                bowsLimits.put(enchantment, enchantment.getMaxLevel());
            }
        }
    }

    @Override
    public void onGameStart() {

    }

    public FastInv getSwordsLimitsInventory(){
        if(swordsLimitsInventory == null){
            swordsLimitsInventory = new EnchantsLimitsInventory(swordTestItem, this.swordsLimits);
        }
        return swordsLimitsInventory;
    }

    public FastInv getArmorsLimitsInventory(){
        if(armorsLimitsInventory == null){
            armorsLimitsInventory = new EnchantsLimitsInventory(armorTestItem, this.armorsLimits);
        }
        return armorsLimitsInventory;
    }

    public FastInv getBowsLimitsInventory() {
        if(bowsLimitsInventory == null){
            bowsLimitsInventory = new EnchantsLimitsInventory(bowTestItem, this.bowsLimits);
        }
        return bowsLimitsInventory;
    }

    public HashMap<Enchantment, Integer> getSwordsLimits() {
        return swordsLimits;
    }

    public HashMap<Enchantment, Integer> getArmorsLimits() {
        return armorsLimits;
    }

    public HashMap<Enchantment, Integer> getBowsLimits() {
        return bowsLimits;
    }

    public boolean areEnderPearlEnabled() {
        return areEnderPearlEnabled;
    }

    public float getEnderPearlDropRateMultiplicator() {
        return enderPearlDropRateMultiplicator;
    }

    public void setEnderPearlDropRateMultiplicator(float enderPearlDropRateMultiplicator) {
        this.enderPearlDropRateMultiplicator = enderPearlDropRateMultiplicator;
    }

    public boolean areWaterBucketEnabled() {
        return areWaterBucketEnabled;
    }

    public void setWaterBucketEnabled(boolean areWaterBucketEnabled) {
        this.areWaterBucketEnabled = areWaterBucketEnabled;
    }

    public boolean areLavaBucketEnabled() {
        return areLavaBucketEnabled;
    }

    public void setLavaBucketEnabled(boolean areLavaBucketEnabled) {
        this.areLavaBucketEnabled = areLavaBucketEnabled;
    }

    public class EnchantsLimitsInventory extends FastInv {

        private HashMap<Integer, Enchantment> slotsToEnchantments = new HashMap<>();
        private HashMap<Enchantment, Integer> limits;

        public EnchantsLimitsInventory(ItemStack testItem, HashMap<Enchantment, Integer> limits) {
            super(2*9, "Limites des enchantements");
            this.limits = limits;

            for(Enchantment enchant : Enchantment.values()){
                if(enchant.canEnchantItem(testItem)){

                    ItemBuilder iconBuilder = new ItemBuilder(Material.ENCHANTED_BOOK);
                    iconBuilder.lore(
                            "",
                            ChatColor.GRAY + "Cliquez pour modifier la limite",
                            "",
                            ChatColor.GRAY + "Clic gauche : +1 niveau",
                            ChatColor.GRAY + "Clic droit : -1 niveau"
                    );

                    int slot = this.getInventory().firstEmpty();
                    if(slot == -1) throw new IllegalStateException("The enchants limits inventory is full");

                    iconBuilder.name(main.getGameManager().getGameController().getEnchantmentName(enchant));

                    ItemStack icon = iconBuilder.build();

                    EnchantmentStorageMeta iconMeta = (EnchantmentStorageMeta) icon.getItemMeta();
                    iconMeta.addStoredEnchant(enchant, limits.get(enchant), false);
                    icon.setItemMeta(iconMeta);

                    slotsToEnchantments.put(slot, enchant);
                    setItem(slot, icon);
                }
            }

            setItem(13, new ItemBuilder(Material.ARROW).name("Retour").build(), event -> {
                main.getGameManager().getHostMainMenu().getGameConfigMainMenu().getStuffSubMenu().open((Player) event.getWhoClicked());
            });
        }

        @Override
        protected void onClick(InventoryClickEvent event) {
            event.setCancelled(true);

            // Check interaction validity
            if(event.getClickedInventory() == null || event.getClickedInventory().equals(event.getView().getBottomInventory())) return;
            if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

            if(slotsToEnchantments.containsKey(event.getSlot())){

                // get enchant
                Enchantment enchantment = slotsToEnchantments.get(event.getSlot());

                int newValue = event.isLeftClick() ? limits.get(enchantment) + 1 : limits.get(enchantment) - 1;
                if(newValue < 0) newValue = 0;
                if(newValue > enchantment.getMaxLevel()) newValue = enchantment.getMaxLevel();

                this.limits.put(enchantment, newValue);
                Main.logDebug("New limit for enchant " + enchantment.getName() + " : " + newValue);

                // update icon enchants
                updateIconEnchants(event, enchantment, newValue);
                updateLoreIfEnchantIsDisabled(event, enchantment, newValue);

                ItemStack toUpdate = getItemStack(limits);
                main.getGameManager().getHostMainMenu().getGameConfigMainMenu().getStuffSubMenu().updateEnchantsLimitsLore(
                        toUpdate,
                        limits
                );
            }
        }

        private void updateIconEnchants(InventoryClickEvent event, Enchantment enchantment, int newLevel) {
            ItemStack icon = event.getCurrentItem();

            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) icon.getItemMeta();
            meta.removeStoredEnchant(enchantment);
            meta.addStoredEnchant(enchantment, newLevel, false);

            icon.setItemMeta(meta);
        }

        private void updateLoreIfEnchantIsDisabled(InventoryClickEvent event, Enchantment enchantment, int newLevel) {
            ItemMeta meta = event.getCurrentItem().getItemMeta();
            List<String> lore = meta.getLore();
            if(newLevel == 0){
                lore.set(0, ChatColor.RED + "Enchantement désactivé");
                lore.set(1, ChatColor.GRAY + "Cliquez pour modifier la limite");
                lore.set(2, "");
                lore.set(3, ChatColor.GRAY + "Clic gauche : +1 niveau");
                lore.set(4, ChatColor.GRAY + "Clic droit : -1 niveau");
            } else {
                lore.set(0, "");
                lore.set(1, ChatColor.GRAY + "Cliquez pour modifier la limite");
                lore.set(2, "");
                lore.set(3, ChatColor.GRAY + "Clic gauche : +1 niveau");
                lore.set(4, ChatColor.GRAY + "Clic droit : -1 niveau");
            }
            meta.setLore(lore);
            event.getCurrentItem().setItemMeta(meta);
        }
    }

    private ItemStack getItemStack(HashMap<Enchantment, Integer> limits) {
        ItemStack toUpdate = null;

        if(limits.equals(swordsLimits)){
            toUpdate = main.getGameManager().getHostMainMenu().getGameConfigMainMenu().getStuffSubMenu().getSwordConfigIcon();
        } else if(limits.equals(armorsLimits)){
            toUpdate = main.getGameManager().getHostMainMenu().getGameConfigMainMenu().getStuffSubMenu().getArmorConfigIcon();
        } else if(limits.equals(bowsLimits)){
            toUpdate = main.getGameManager().getHostMainMenu().getGameConfigMainMenu().getStuffSubMenu().getBowConfigIcon();
        } else {
            throw new IllegalStateException("The enchantment limits inventory is not linked to any menu");
        }
        return toUpdate;
    }
}
