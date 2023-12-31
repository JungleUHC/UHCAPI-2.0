/*    */ package fr.ariloxe.mumble.murmur.core.mumble;
/*    */ 
/*    */ import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/*    */ import com.fasterxml.jackson.annotation.JsonProperty;
/*    */ import fr.ariloxe.mumble.murmur.api.MumbleLinkAPI;
/*    */ import fr.ariloxe.mumble.murmur.api.mumble.IChannel;
/*    */ import fr.ariloxe.mumble.murmur.api.mumble.IServer;
/*    */ import org.springframework.http.HttpEntity;
/*    */ import org.springframework.http.HttpHeaders;
/*    */ import org.springframework.http.MediaType;
/*    */ import org.springframework.util.LinkedMultiValueMap;
/*    */ import org.springframework.util.MultiValueMap;
/*    */ import org.springframework.web.client.RestTemplate;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @JsonIgnoreProperties(ignoreUnknown = true)
/*    */ public class Channel
/*    */   implements IChannel
/*    */ {
/*    */   @JsonProperty("description")
/*    */   private String description;
/*    */   @JsonProperty("id")
/*    */   private int id;
/*    */   @JsonProperty("name")
/*    */   private String name;
/*    */   @JsonProperty("parent")
/*    */   private int parent;
/*    */   @JsonProperty("temporary")
/*    */   private boolean temporary;
/*    */   
/*    */   public int getId() {
/* 37 */     return this.id;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 41 */     return this.name;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 45 */     return "Channel{description='" + this.description + '\'' + ", id=" + this.id + ", name='" + this.name + '\'' + ", parent=" + this.parent + ", temporary=" + this.temporary + '}';
/*    */   }
/*    */   
/*    */   public static IChannel getChannel(int idserver, int idchannel) {
/* 49 */     RestTemplate restTemplate = new RestTemplate();
/* 50 */     return (IChannel)restTemplate.getForObject("http://" + MumbleLinkAPI.getApi().getMumbleManager().getHostName() + ":" + MumbleLinkAPI.getApi().getMumbleManager().getPort() + "/servers/{idserver}/channels/{idchannel}", Channel.class, new Object[] { Integer.valueOf(idserver), Integer.valueOf(idchannel) });
/*    */   }
/*    */   
/*    */   public static IChannel createChannel(IServer server, IChannel parent, String name) {
/* 54 */     RestTemplate restTemplate = new RestTemplate();
/* 55 */     HttpHeaders headers = new HttpHeaders();
/* 56 */     headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
/* 57 */     LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap();
/* 58 */     linkedMultiValueMap.add("name", name);
/* 59 */     linkedMultiValueMap.add("parent", parent.getId() + "");
/* 60 */     HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(linkedMultiValueMap, (MultiValueMap)headers);
/* 61 */     return (IChannel)restTemplate.postForObject("http://" + MumbleLinkAPI.getApi().getMumbleManager().getHostName() + ":" + MumbleLinkAPI.getApi().getMumbleManager().getPort() + "/servers/{idserver}/channels", request, Channel.class, new Object[] { Integer.valueOf(server.getId()) });
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/fr/ariloxe/mumble/murmur/core/mumble/Channel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */