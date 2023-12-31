/*    */ package fr.ariloxe.mumble.minecraft;
/*    */ 
/*    */ import fr.ariloxe.mumble.Main;
/*    */ import fr.ariloxe.mumble.murmur.MumbleWrapper;
/*    */ import fr.ariloxe.mumble.murmur.api.MumbleLinkAPI;
/*    */ import fr.ariloxe.mumble.murmur.core.mumble.User;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.player.PlayerJoinEvent;
/*    */ import org.bukkit.event.player.PlayerQuitEvent;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ 
/*    */ public class BukkitListeners
/*    */   implements Listener
/*    */ {
/*    */   @EventHandler
/*    */   public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
/* 21 */     if (MumbleLinkAPI.getApi().getMumbleManager().getServer() != null) {
/* 22 */       Player player = playerJoinEvent.getPlayer();
/*    */       
/* 24 */       player.sendMessage("");
/* 25 */       player.sendMessage("§3§lMumbleLink §8§l» §fL'utilisation de Mumble est §eobligatoire§f pour cette partie.");
/* 26 */       player.sendMessage("");
/*    */ 
/*    */       
/* 29 */       Bukkit.getScheduler().runTaskAsynchronously((Plugin)Main.getInstance(), () -> {
/*    */             try {
/*    */               User.createUser(MumbleLinkAPI.getApi().getMumbleManager().getServer(), player.getName(), "1234");
/* 32 */             } catch (Exception exception) {}
/*    */           });
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   @EventHandler
/*    */   public void onQuit(PlayerQuitEvent playerQuitEvent) {
/* 40 */     if (MumbleLinkAPI.getApi().getMumbleManager().getServer() != null) {
/* 41 */       Player player = playerQuitEvent.getPlayer();
/* 42 */       Bukkit.getScheduler().runTaskAsynchronously((Plugin)Main.getInstance(), () -> {
/*    */             try {
/*    */               MumbleWrapper.muteUser(player.getName());
/* 45 */             } catch (Exception exception) {}
/*    */           });
/*    */     } 
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/fr/ariloxe/mumble/minecraft/BukkitListeners.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */