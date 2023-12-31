/*     */ package fr.ariloxe.mumble.murmur;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonProcessingException;
/*     */ import fr.ariloxe.mumble.murmur.api.MumbleLinkAPI;
/*     */ import fr.ariloxe.mumble.murmur.api.mumble.IServer;
/*     */ import fr.ariloxe.mumble.murmur.api.mumble.MumbleState;
/*     */ import fr.ariloxe.mumble.murmur.core.mumble.User;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MumbleWrapper
/*     */ {
/*     */   public static void unmuteUser(String userName) {
/*  15 */     User user = getUser(userName);
/*     */     
/*  17 */     if (user == null) {
/*  18 */       System.out.println("Erreur: le joueur " + userName + " n'existe pas sur l'instance mumble.");
/*     */       
/*     */       return;
/*     */     } 
/*     */     try {
/*  23 */       user.unmuteUser();
/*  24 */     } catch (Exception exception) {}
/*     */   }
/*     */   
/*     */   public static void muteUser(String userName) {
/*  28 */     User user = getUser(userName);
/*     */     
/*  30 */     if (user == null) {
/*  31 */       System.out.println("Erreur: le joueur " + userName + " n'existe pas sur l'instance mumble.");
/*     */       
/*     */       return;
/*     */     } 
/*     */     try {
/*  36 */       user.muteUser();
/*  37 */     } catch (Exception exception) {}
/*     */   }
/*     */ 
/*     */   
/*     */   public static User getUser(String userName) {
/*  42 */     User user = null;
/*  43 */     IServer server = MumbleLinkAPI.getApi().getMumbleManager().getServer();
/*     */     try {
/*  45 */       for (User onlineUser : User.getUsers(server.getId())) {
/*  46 */         if (onlineUser.getName().equalsIgnoreCase(userName)) {
/*  47 */           user = onlineUser;
/*     */           break;
/*     */         } 
/*     */       } 
/*  51 */     } catch (JsonProcessingException e) {
/*  52 */       e.printStackTrace();
/*     */     } 
/*     */     
/*  55 */     return user;
/*     */   }
/*     */ 
/*     */   
/*     */   public static MumbleState getMumbleState(String userName) {
/*     */     try {
/*  61 */       User user = null;
/*  62 */       for (User onlineUser : User.getUsers(MumbleLinkAPI.getApi().getMumbleManager().getServer().getId())) {
/*  63 */         if (onlineUser.getName().equalsIgnoreCase(userName)) {
/*  64 */           user = onlineUser;
/*     */           break;
/*     */         } 
/*     */       } 
/*  68 */       if (user == null) {
/*  69 */         return MumbleState.DISCONNECT;
/*     */       }
/*  71 */       if (((String)user.isLinked().get("identity")).contains(user.getName())) {
/*  72 */         return MumbleState.LINK;
/*     */       }
/*  74 */       return MumbleState.UNLINK;
/*     */     }
/*  76 */     catch (JsonProcessingException e) {
/*  77 */       e.printStackTrace();
/*  78 */       return MumbleState.DISCONNECT;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void setMuteState(String userName, boolean muteState) {
/*  83 */     User user = null;
/*  84 */     IServer server = MumbleLinkAPI.getApi().getMumbleManager().getServer();
/*     */     try {
/*  86 */       for (User onlineUser : User.getUsers(server.getId())) {
/*  87 */         if (onlineUser.getName().equalsIgnoreCase(userName)) {
/*  88 */           user = onlineUser;
/*     */           break;
/*     */         } 
/*     */       } 
/*  92 */     } catch (JsonProcessingException e) {
/*  93 */       e.printStackTrace();
/*     */     } 
/*     */     
/*  96 */     if (!muteState) {
/*  97 */       user.unmuteUser();
/*     */     } else {
/*  99 */       user.muteUser();
/*     */     } 
/*     */   }
/*     */   
/*     */   public static boolean isMute(String userName, boolean muteState) {
/* 104 */     User user = null;
/* 105 */     IServer server = MumbleLinkAPI.getApi().getMumbleManager().getServer();
/*     */     try {
/* 107 */       for (User onlineUser : User.getUsers(server.getId())) {
/* 108 */         if (onlineUser.getName().equalsIgnoreCase(userName)) {
/* 109 */           user = onlineUser;
/*     */           break;
/*     */         } 
/*     */       } 
/* 113 */     } catch (JsonProcessingException e) {
/* 114 */       e.printStackTrace();
/*     */     } 
/*     */     
/* 117 */     if (user != null) {
/* 118 */       return user.isMute();
/*     */     }
/* 120 */     return false;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/fr/ariloxe/mumble/murmur/MumbleWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */