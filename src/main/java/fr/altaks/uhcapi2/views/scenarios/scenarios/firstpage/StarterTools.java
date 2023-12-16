package fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StarterTools implements Scenario {

    public String getName() {
        return "Starter Tools";
    }

    @Override
    public String getDescription() {
        return "Ajoute des outils en fer enchantés avec Efficiency 2 et Unbreaking 2 dans l'inventaire de départ, simplifiant le début de la partie.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.STONE_PICKAXE);
    }

    @Override
    public int getSlot() {
        return 16;
    }

    private ItemStack enchantedIronPickaxe = new ItemBuilder(Material.IRON_PICKAXE)
            .lore(ChatColor.GRAY + "Objet de départ")
            .enchant(Enchantment.DURABILITY, 2)
            .enchant(Enchantment.DIG_SPEED, 2)
            .build();

    private ItemStack enchantedIronAxe = new ItemBuilder(Material.IRON_AXE)
            .lore(ChatColor.GRAY + "Objet de départ")
            .enchant(Enchantment.DURABILITY, 2)
            .enchant(Enchantment.DIG_SPEED, 2)
            .build();

    private ItemStack enchantedIronShovel = new ItemBuilder(Material.IRON_SPADE)
            .lore(ChatColor.GRAY + "Objet de départ")
            .enchant(Enchantment.DURABILITY, 2)
            .enchant(Enchantment.DIG_SPEED, 2)
            .build();

    @Override
    public void startScenario(Main main) {
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.getGameMode() != GameMode.SPECTATOR){
                player.getInventory().addItem(enchantedIronPickaxe);
                player.getInventory().addItem(enchantedIronAxe);
                player.getInventory().addItem(enchantedIronShovel);
            }
        }
    }
}