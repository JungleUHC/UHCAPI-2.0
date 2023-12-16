package fr.altaks.uhcapi2.views.scenarios.scenarios.secondpage;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.altaks.uhcapi2.views.scenarios.Scenario;
import fr.mrmicky.fastinv.ItemBuilder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class GoldenHead implements Scenario {

    private String ICON_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGFkMDQ3NmU4NjcxNjk2YWYzYTg5NDlhZmEyYTgxNGI5YmRkZTY1ZWNjZDFhOGI1OTNhZWVmZjVhMDMxOGQifX19";

    public String getName() {
        return "Golden Heads";
    }

    @Override
    public String getDescription() {
        return "Permet la fabrication d'une pomme d'or qui régénère 4 cœurs en utilisant la tête d'un joueur éliminé";
    }

    @Override
    public ItemBuilder getIcon() {
        return HeadBuilder.of(ICON_VALUE);
    }

    @Override
    public int getSlot() {
        return 67;
    }

    private final ItemStack goldenHead = HeadBuilder.of("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTI0YzEyOWJhOTZkMjM3YmE2NWY5ZTA2Mjk1OGQyN2FiYWFmY2IxYjQyMjkzMGI3MmUxYzUyZjBiYmRhYjkyIn19fQ==")
            .addLore("§7Pomme d'or qui permet de régénérer 4 cœurs")
            .name("§6Tête dorée")
            .build();

    @SuppressWarnings("deprecation")
    @Override
    public void startScenario(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);

        ShapedRecipe recipe = new ShapedRecipe(goldenHead);
        recipe.shape("GGG", "GHG", "GGG");
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('H', Material.SKULL_ITEM, 3);

        Bukkit.addRecipe(recipe);
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        if(event.getEntity().getKiller() != null){
            event.getEntity().getKiller().getInventory().addItem(HeadBuilder.of(event.getEntity()).build());
        }
    }

    @EventHandler
    public void onGoldenHeadUse(PlayerInteractEvent event){
        if(!event.hasItem()) return;
        if(!(event.getItem().getType() == Material.SKULL_ITEM && event.getItem().getItemMeta().hasDisplayName())) return;
        if(event.getItem().getItemMeta().getDisplayName().equals(goldenHead.getItemMeta().getDisplayName())){
            if(event.getPlayer().getHealth() == event.getPlayer().getMaxHealth()){
                // send message to hotbar
                String msg = "§cVous n'avez pas besoin de vous régénerer maintenant !";
                IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + msg + "\"}");
                PacketPlayOutChat bar = new PacketPlayOutChat(icbc, (byte)2);
                ((CraftPlayer)event.getPlayer()).getHandle().playerConnection.sendPacket(bar);

                event.setCancelled(true);
                return;
            }
            event.getPlayer().setHealth(Math.min(event.getPlayer().getHealth() + 8, event.getPlayer().getMaxHealth()));
            event.setCancelled(true);
            if(event.getPlayer().getItemInHand().getAmount() > 1){
                event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
            } else {
                event.getPlayer().setItemInHand(null);
            }
        }
    }
}