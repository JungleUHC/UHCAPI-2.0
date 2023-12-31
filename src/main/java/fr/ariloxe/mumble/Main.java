/*    */ package fr.ariloxe.mumble;
/*    */ 
/*    */ import fr.ariloxe.mumble.minecraft.BukkitListeners;
/*    */ import fr.ariloxe.mumble.minecraft.MumbleCommand;
/*    */ import fr.ariloxe.mumble.minecraft.MumbleService;
/*    */ import fr.ariloxe.mumble.murmur.api.MumbleLinkAPI;
/*    */ import fr.ariloxe.mumble.murmur.core.MumbleLink;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ import org.bukkit.plugin.java.JavaPlugin;
/*    */ 
/*    */ public final class Main
/*    */   extends JavaPlugin
/*    */ {
/*    */   private static Main instance;
/*    */   
/*    */   public void onEnable() {
/* 20 */     MumbleLinkAPI.setLink((MumbleLinkAPI)new MumbleLink());
/*    */     
/* 22 */     instance = this;
/*    */     
/* 24 */     MumbleService mumbleService = new MumbleService();
/* 25 */     getCommand("mumble").setExecutor((CommandExecutor)new MumbleCommand(mumbleService));
/* 26 */     Bukkit.getPluginManager().registerEvents((Listener)new BukkitListeners(), (Plugin)this);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onDisable() {
/* 31 */     if (MumbleLinkAPI.getApi().getMumbleManager().getServer() != null) {
/* 32 */       MumbleLinkAPI.getApi().getMumbleManager().getServer().stop();
/* 33 */       MumbleLinkAPI.getApi().getMumbleManager().getServer().delete();
/*    */     } 
/*    */   }
/*    */   
/*    */   public static Main getInstance() {
/* 38 */     return instance;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/fr/ariloxe/mumble/Main.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */