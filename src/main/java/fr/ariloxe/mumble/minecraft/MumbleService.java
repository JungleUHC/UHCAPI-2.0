/*     */ package fr.ariloxe.mumble.minecraft;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonProcessingException;
/*     */ import fr.ariloxe.mumble.Main;
/*     */ import fr.ariloxe.mumble.murmur.MumbleWrapper;
/*     */ import fr.ariloxe.mumble.murmur.api.MumbleLinkAPI;
/*     */ import fr.ariloxe.mumble.murmur.api.mumble.IMumbleManager;
/*     */ import fr.ariloxe.mumble.murmur.api.mumble.IServer;
/*     */ import fr.ariloxe.mumble.murmur.api.mumble.MumbleState;
/*     */ import fr.ariloxe.mumble.murmur.core.mumble.User;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.GameMode;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.scheduler.BukkitRunnable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MumbleService
/*     */ {
/*  26 */   private final List<String> speakers = new ArrayList<>();
/*     */   private boolean started = false;
/*     */   
/*     */   public List<String> getSpeakers() {
/*  30 */     return this.speakers;
/*     */   }
/*     */   
/*     */   public void start() {
/*  34 */     this.started = true;
/*     */   }
/*     */   
/*     */   public void waitingTask() {
/*  38 */     final IMumbleManager iMumbleManager = MumbleLinkAPI.getApi().getMumbleManager();
/*     */     
/*  40 */     (new BukkitRunnable()
/*     */       {
/*     */         public void run() {
/*  43 */           if (iMumbleManager.getServer() == null) {
/*  44 */             cancel(); return;
/*     */           } 
/*  46 */           if (MumbleService.this.started) {
/*  47 */             cancel();
/*  48 */             MumbleService.this.gameTask();
/*     */             
/*     */             return;
/*     */           } 
/*  52 */           for (Player player : Bukkit.getOnlinePlayers()) {
/*  53 */             MumbleState mumbleState = MumbleWrapper.getMumbleState(player.getName());
/*  54 */             player.setPlayerListName(player.getName() + " " + mumbleState.getName());
/*     */           } 
/*     */           
/*     */           try {
/*  58 */             IServer server = MumbleLinkAPI.getApi().getMumbleManager().getServer();
/*     */             
/*  60 */             for (User onlineUser : User.getUsers(server.getId())) {
/*  61 */               Player player = Bukkit.getPlayer(onlineUser.getName());
/*     */               
/*  63 */               if (!onlineUser.isMute() && !MumbleService.this.getSpeakers().contains(onlineUser.getName())) {
/*  64 */                 onlineUser.muteUser();
/*  65 */                 if (player != null) {
/*  66 */                   player.sendMessage("§3§lMumbleLink §8§l» §fVous n'êtes pas dans la liste des §cSpeakers§f sur ce Mumble !");
/*  67 */                   player.sendMessage("§3§lMumbleLink §8§l» §fVous avez §cperdu la parole§f.");
/*     */                 } 
/*  69 */               } else if (onlineUser.isMute() && MumbleService.this.getSpeakers().contains(onlineUser.getName())) {
/*  70 */                 onlineUser.unmuteUser();
/*  71 */                 if (player != null) {
/*  72 */                   player.sendMessage("§3§lMumbleLink §8§l» §fVotre §6compte§f a été détecté dans la liste des §cSpeakers§f de ce Mumble !");
/*  73 */                   player.sendMessage("§3§lMumbleLink §8§l» §fVous avez §aretrouvé la parole§f.");
/*     */                 } 
/*     */               } 
/*     */             } 
/*  77 */           } catch (JsonProcessingException e) {
/*  78 */             throw new RuntimeException(e);
/*     */           } 
/*     */         }
/*  81 */       }).runTaskTimerAsynchronously((Plugin)Main.getInstance(), 20L, 30L);
/*     */   }
/*     */   
/*     */   public void gameTask() {
/*  85 */     final IMumbleManager iMumbleManager = MumbleLinkAPI.getApi().getMumbleManager();
/*     */     
/*  87 */     (new BukkitRunnable()
/*     */       {
/*     */         public void run() {
/*  90 */           if (iMumbleManager.getServer() == null) {
/*  91 */             cancel();
/*     */             
/*     */             return;
/*     */           } 
/*  95 */           for (Player player : Bukkit.getOnlinePlayers()) {
/*  96 */             MumbleState mumbleState = MumbleWrapper.getMumbleState(player.getName());
/*  97 */             player.setPlayerListName(player.getName() + " " + mumbleState.getName());
/*     */           } 
/*     */           
/*     */           try {
/* 101 */             IServer server = MumbleLinkAPI.getApi().getMumbleManager().getServer();
/*     */ 
/*     */             
/* 104 */             for (User onlineUser : User.getUsers(server.getId())) {
/* 105 */               if (!((String)onlineUser.isLinked().get("identity")).contains(onlineUser.getName())) {
/* 106 */                 if (!onlineUser.isMute()) {
/* 107 */                   onlineUser.muteUser();
/* 108 */                   Player player = Bukkit.getPlayer(onlineUser.getName());
/* 109 */                   if (player != null) {
/* 110 */                     player.sendMessage("§3§lMumbleLink §8§l» §fVous êtes actuellement §6§lDé-Link ✈");
/* 111 */                     player.sendMessage("§3§lMumbleLink §8§l» §fVous avez §cperdu la parole§f.");
/*     */                   }
/*     */                 
/*     */                 }
/*     */               
/*     */               } else {
/*     */                 
/* 118 */                 Player player = Bukkit.getPlayer(onlineUser.getName());
/* 119 */                 if (player == null && !onlineUser.isMute()) {
/* 120 */                   onlineUser.muteUser();
/*     */                 }
/* 122 */                 else if (player != null) {
/* 123 */                   MumbleState mumbleState = MumbleWrapper.getMumbleState(onlineUser.getName());
/*     */                   
/* 125 */                   if (MumbleService.this.getSpeakers().contains(player.getName())) {
/* 126 */                     if (onlineUser.isMute()) {
/* 127 */                       onlineUser.unmuteUser();
/* 128 */                       player.sendMessage("§3§lMumbleLink §8§l» §fVotre §6compte§f a été détecté dans la liste des §cSpeakers§f de ce Mumble !");
/* 129 */                       player.sendMessage("§3§lMumbleLink §8§l» §fVous avez §aretrouvé la parole§f.");
/*     */                     
/*     */                     }
/*     */                   
/*     */                   }
/* 134 */                   else if (player.getGameMode() == GameMode.SPECTATOR && !onlineUser.isMute()) {
/* 135 */                     onlineUser.muteUser();
/* 136 */                     player.sendMessage("§3§lMumbleLink §8§l» §fVous êtes actuellement §céliminé§f de la partie.");
/* 137 */                     player.sendMessage("§3§lMumbleLink §8§l» §fVous avez §cperdu la parole§f.");
/* 138 */                   } else if (mumbleState == MumbleState.LINK && onlineUser.isMute() && player.getGameMode() != GameMode.SPECTATOR) {
/* 139 */                     onlineUser.unmuteUser();
/* 140 */                     player.sendMessage("§3§lMumbleLink §8§l» §fVous êtes actuellement §a§lLink ✔");
/* 141 */                     player.sendMessage("§3§lMumbleLink §8§l» §fVous avez §aretrouvé la parole§f.");
/*     */                   } 
/*     */                 } 
/*     */               } 
/*     */             } 
/* 146 */           } catch (JsonProcessingException e) {
/* 147 */             e.printStackTrace();
/*     */           } 
/*     */         }
/* 150 */       }).runTaskTimerAsynchronously((Plugin)Main.getInstance(), 20L, 30L);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/fr/ariloxe/mumble/minecraft/MumbleService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */