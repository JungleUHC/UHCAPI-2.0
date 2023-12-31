/*     */ package fr.ariloxe.mumble.murmur.core.mumble;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonProcessingException;
/*     */ import fr.ariloxe.mumble.murmur.api.mumble.IChannel;
/*     */ import fr.ariloxe.mumble.murmur.api.mumble.IMumbleManager;
/*     */ import fr.ariloxe.mumble.murmur.api.mumble.IServer;
/*     */ import fr.ariloxe.mumble.murmur.api.mumble.IUser;
/*     */ import fr.ariloxe.mumble.murmur.api.mumble.MumbleState;
/*     */ 
/*     */ public class MumbleManager
/*     */   implements IMumbleManager {
/*  12 */   private String port = "8080";
/*  13 */   private String hostName = "jungleuhc.fr";
/*     */   
/*  15 */   private IServer server = null;
/*  16 */   private IChannel channel = null;
/*     */   
/*     */   public void createServer() {
/*  19 */     if (this.server != null) {
/*     */       return;
/*     */     }
/*  22 */     this.server = Server.createServer();
/*     */     try {
/*  24 */       this.channel = Channel.createChannel(this.server, Channel.getChannel(this.server.getId(), 0), "UHCHost");
/*  25 */     } catch (Exception e) {
/*  26 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public IUser getUserFromName(String userName) {
/*  31 */     User user = null;
/*  32 */     IServer server = this.server;
/*     */     try {
/*  34 */       for (User onlineUser : User.getUsers(server.getId())) {
/*  35 */         if (onlineUser.getName().equalsIgnoreCase(userName)) {
/*  36 */           user = onlineUser;
/*     */           break;
/*     */         } 
/*     */       } 
/*  40 */     } catch (JsonProcessingException e) {
/*  41 */       e.printStackTrace();
/*     */     } 
/*     */     
/*  44 */     return user;
/*     */   }
/*     */   
/*     */   public MumbleState getStateOf(String userName) {
/*     */     try {
/*  49 */       User user = null;
/*  50 */       for (User onlineUser : User.getUsers(this.server.getId())) {
/*  51 */         if (onlineUser.getName().equalsIgnoreCase(userName)) {
/*  52 */           user = onlineUser;
/*     */           break;
/*     */         } 
/*     */       } 
/*  56 */       if (user == null) {
/*  57 */         return MumbleState.DISCONNECT;
/*     */       }
/*  59 */       if (((String)user.isLinked().get("identity")).contains(user.getName())) {
/*  60 */         return MumbleState.LINK;
/*     */       }
/*  62 */       return MumbleState.UNLINK;
/*     */     }
/*  64 */     catch (JsonProcessingException e) {
/*  65 */       e.printStackTrace();
/*  66 */       return MumbleState.DISCONNECT;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHostName(String hostName) {
/*  72 */     this.hostName = hostName;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getHostName() {
/*  77 */     return this.hostName;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPort(String defaultPort) {
/*  82 */     this.port = defaultPort;
/*     */   }
/*     */ 
/*     */   
/*     */   public IServer getServer() {
/*  87 */     return this.server;
/*     */   }
/*     */ 
/*     */   
/*     */   public IChannel getChannel() {
/*  92 */     return this.channel;
/*     */   }
/*     */   
/*     */   public void setServer(IServer server) {
/*  96 */     this.server = server;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPort() {
/* 101 */     return this.port;
/*     */   }
/*     */   
/*     */   public void createUser(String userName, String password) {
/* 105 */     User.createUser(this.server, userName, password);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/fr/ariloxe/mumble/murmur/core/mumble/MumbleManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */