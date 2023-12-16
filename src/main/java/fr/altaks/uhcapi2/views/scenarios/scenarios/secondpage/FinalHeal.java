package fr.altaks.uhcapi2.views.scenarios.scenarios.secondpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.util.LoreUtil;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class FinalHeal implements Scenario {

    public String getName() {
        return "Final Heal";
    }

    @Override
    public String getDescription() {
        return "Les joueurs reçoivent une régénération complète de leurs points de vie après un certain temps dans la partie.";
    }

    @Override
    public ItemBuilder getIcon() {
        return new ItemBuilder(Material.GOLD_NUGGET);
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public int getSlot() {
        return 74;
    }

    @Override
    public void startScenario(Main main) {
        new BukkitRunnable(){
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()){
                    if(player.getGameMode() == GameMode.SURVIVAL){
                        player.setHealth(player.getMaxHealth());
                    }
                }
            }
        }.runTaskLater(main, healingDelay * 60 * 20L); // minutes -> seconds -> ticks
    }

    private int healingDelay = 20; // minutes

    @Override
    public String[] getConfigurationLore() {
        return new String[]{
                ChatColor.YELLOW + "Valeur actuelle : " + ChatColor.GREEN + healingDelay + " minutes",
                "",
                ChatColor.GRAY + "Clic droit pour augmenter de 1 minute",
                ChatColor.GRAY + "Shift + Clic droit pour diminuer de 1 minute"
        };
    }

    @Override
    public void processClick(InventoryClickEvent event) {
        int modifier = (event.isShiftClick() ? -1 : 1);
        healingDelay += modifier;

        if(healingDelay < 1) healingDelay = 1;

        // update item lore
        LoreUtil.updateLore(event.getCurrentItem(), -4, ChatColor.YELLOW + "Valeur actuelle : " + ChatColor.GREEN + healingDelay + " minutes");
    }
}