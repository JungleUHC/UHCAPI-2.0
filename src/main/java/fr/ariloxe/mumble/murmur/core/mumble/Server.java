/*    */ package fr.ariloxe.mumble.murmur.core.mumble;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonProcessingException;
/*    */ import fr.ariloxe.mumble.murmur.api.MumbleLinkAPI;
/*    */ import fr.ariloxe.mumble.murmur.api.mumble.IServer;
/*    */ import fr.ariloxe.mumble.murmur.api.mumble.IUser;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.List;
/*    */ import org.springframework.web.client.RestTemplate;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Server
/*    */   implements IServer
/*    */ {
/*    */   private String address;
/*    */   private String host;
/*    */   private String humanize_uptime;
/*    */   private int id;
/*    */   private int log_length;
/*    */   private int maxusers;
/*    */   private String name;
/*    */   private String password;
/*    */   private int port;
/*    */   private boolean running;
/*    */   private int user_count;
/*    */   private String welcometext;
/*    */   
/*    */   public int getId() {
/* 42 */     return this.id;
/*    */   }
/*    */   
/*    */   public int getPort() {
/* 46 */     return this.port;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 50 */     return "Server{address='" + this.address + '\'' + ", host='" + this.host + '\'' + ", humanize_uptime='" + this.humanize_uptime + '\'' + ", id=" + this.id + ", log_length=" + this.log_length + ", maxusers=" + this.maxusers + ", name='" + this.name + '\'' + ", password='" + this.password + '\'' + ", port=" + this.port + ", running=" + this.running + ", user_count=" + this.user_count + ", welcometext='" + this.welcometext + '\'' + '}';
/*    */   }
/*    */   
/*    */   public static Server createServer() {
/* 54 */     RestTemplate restTemplate = new RestTemplate();
/* 55 */     Server server = (Server)restTemplate.postForObject("http://" + MumbleLinkAPI.getApi().getMumbleManager().getHostName() + ":" + MumbleLinkAPI.getApi().getMumbleManager().getPort() + "/servers/", null, Server.class, new Object[0]);
/* 56 */     return server;
/*    */   }
/*    */   
/*    */   public void stop() {
/* 60 */     RestTemplate restTemplate = new RestTemplate();
/* 61 */     restTemplate.postForObject("http://" + MumbleLinkAPI.getApi().getMumbleManager().getHostName() + ":" + MumbleLinkAPI.getApi().getMumbleManager().getPort() + "/servers/{idserver}/stop", null, Message.class, new Object[] { Integer.valueOf(this.id) });
/*    */   }
/*    */   
/*    */   public void delete() {
/* 65 */     RestTemplate restTemplate = new RestTemplate();
/* 66 */     restTemplate.delete("http://" + MumbleLinkAPI.getApi().getMumbleManager().getHostName() + ":" + MumbleLinkAPI.getApi().getMumbleManager().getPort() + "/servers/{idserver}/", new Object[] { Integer.valueOf(this.id) });
/*    */   }
/*    */   
/*    */   public String join(String username, String password, Channel channel) {
/* 70 */     return "mumble://" + username + ":" + password + "@" + MumbleLinkAPI.getApi().getMumbleManager().getHostName() + ":" + this.port + "/" + channel.getName();
/*    */   }
/*    */   
/*    */   public List<IUser> getUsers() {
/* 74 */     List<IUser> userList = new ArrayList<>();
/*    */     
/*    */     try {
/* 77 */       userList.addAll(Arrays.asList((IUser[])User.getUsers(this.id)));
/* 78 */     } catch (JsonProcessingException e) {
/* 79 */       e.printStackTrace();
/*    */     } 
/*    */     
/* 82 */     return userList;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/fr/ariloxe/mumble/murmur/core/mumble/Server.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */