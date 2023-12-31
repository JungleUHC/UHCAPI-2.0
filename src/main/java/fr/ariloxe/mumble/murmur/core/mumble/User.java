/*     */ package fr.ariloxe.mumble.murmur.core.mumble;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonProperty;
/*     */ import com.fasterxml.jackson.core.JsonProcessingException;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.ObjectMapper;
/*     */ import fr.ariloxe.mumble.murmur.api.MumbleLinkAPI;
/*     */ import fr.ariloxe.mumble.murmur.api.mumble.IMessage;
/*     */ import fr.ariloxe.mumble.murmur.api.mumble.IServer;
/*     */ import fr.ariloxe.mumble.murmur.api.mumble.IUser;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.springframework.http.HttpEntity;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import org.springframework.web.client.RestTemplate;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class User
/*     */   implements IUser
/*     */ {
/*  26 */   private static final Map<String, String> passwords = new HashMap<>();
/*     */   
/*     */   @JsonProperty("address")
/*     */   private int[] address;
/*     */   
/*     */   @JsonProperty("bytespersec")
/*     */   private int bytespersec;
/*     */   
/*     */   @JsonProperty("channel")
/*     */   private int channel;
/*     */   
/*     */   @JsonProperty("comment")
/*     */   private String comment;
/*     */   
/*     */   @JsonProperty("context")
/*     */   private String context;
/*     */   
/*     */   @JsonProperty("deaf")
/*     */   private boolean deaf;
/*     */   
/*     */   @JsonProperty("identity")
/*     */   private String identity;
/*     */   
/*     */   @JsonProperty("idlesecs")
/*     */   private int idlesecs;
/*     */   
/*     */   @JsonProperty("mute")
/*     */   private boolean mute;
/*     */   
/*     */   @JsonProperty("name")
/*     */   private String name;
/*     */   
/*     */   @JsonProperty("onlinesecs")
/*     */   private long onlinesecs;
/*     */   
/*     */   @JsonProperty("os")
/*     */   private String os;
/*     */   
/*     */   @JsonProperty("osversion")
/*     */   private String osversion;
/*     */   
/*     */   @JsonProperty("prioritySpeaker")
/*     */   private boolean prioritySpeaker;
/*     */   
/*     */   @JsonProperty("recording")
/*     */   private boolean recording;
/*     */   
/*     */   @JsonProperty("release")
/*     */   private String release;
/*     */   
/*     */   @JsonProperty("selfDeaf")
/*     */   private boolean selfDeaf;
/*     */   
/*     */   @JsonProperty("selfMute")
/*     */   private boolean selfMute;
/*     */   
/*     */   @JsonProperty("session")
/*     */   private int session;
/*     */   
/*     */   @JsonProperty("suppress")
/*     */   private boolean suppress;
/*     */   
/*     */   @JsonProperty("tcpPing")
/*     */   private double tcpPing;
/*     */   
/*     */   @JsonProperty("tcponly")
/*     */   private boolean tcponly;
/*     */   
/*     */   @JsonProperty("udpPing")
/*     */   private long udpPing;
/*     */   
/*     */   @JsonProperty("userid")
/*     */   private int userid;
/*     */   
/*     */   @JsonProperty("version")
/*     */   private int version;
/*     */   
/*     */   public boolean isMute() {
/* 104 */     return this.mute;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 108 */     return this.name;
/*     */   }
/*     */   
/*     */   public boolean isSelfDeaf() {
/* 112 */     return this.selfDeaf;
/*     */   }
/*     */   
/*     */   public boolean isSelfMute() {
/* 116 */     return this.selfMute;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 120 */     return "User{address=" + 
/* 121 */       Arrays.toString(this.address) + ", bytespersec=" + this.bytespersec + ", channel=" + this.channel + ", comment='" + this.comment + '\'' + ", context='" + this.context + '\'' + ", deaf=" + this.deaf + ", identity='" + this.identity + '\'' + ", idlesecs=" + this.idlesecs + ", mute=" + this.mute + ", name='" + this.name + '\'' + ", onlinesecs=" + this.onlinesecs + ", os='" + this.os + '\'' + ", osversion='" + this.osversion + '\'' + ", prioritySpeaker=" + this.prioritySpeaker + ", recording=" + this.recording + ", release='" + this.release + '\'' + ", selfDeaf=" + this.selfDeaf + ", selfMute=" + this.selfMute + ", session=" + this.session + ", suppress=" + this.suppress + ", tcpPing=" + this.tcpPing + ", tcponly=" + this.tcponly + ", udpPing=" + this.udpPing + ", userid=" + this.userid + ", version=" + this.version + '}';
/*     */   }
/*     */   
/*     */   public static User[] getUsers(int idserver) throws JsonProcessingException {
/* 125 */     RestTemplate restTemplate = new RestTemplate();
/* 126 */     String json = (String)restTemplate.getForObject("http://" + MumbleLinkAPI.getApi().getMumbleManager().getHostName() + ":" + MumbleLinkAPI.getApi().getMumbleManager().getPort() + "/servers/{idserver}/user", String.class, new Object[] { Integer.valueOf(idserver) });
/* 127 */     ObjectMapper mapper = new ObjectMapper();
/* 128 */     JsonNode rootNode = mapper.readTree(json);
/* 129 */     User[] users = new User[rootNode.size()];
/* 130 */     int i = 0;
/* 131 */     for (JsonNode jn : rootNode) {
/* 132 */       users[i] = (User)mapper.readValue(jn.toString(), User.class);
/* 133 */       i++;
/*     */     } 
/* 135 */     return users;
/*     */   }
/*     */   
/*     */   public static Message createUser(IServer server, String name, String password) {
/* 139 */     RestTemplate restTemplate = new RestTemplate();
/* 140 */     HttpHeaders headers = new HttpHeaders();
/* 141 */     headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
/* 142 */     LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
/* 143 */     linkedMultiValueMap.add("username", name);
/* 144 */     linkedMultiValueMap.add("password", password);
/* 145 */     HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(linkedMultiValueMap, (MultiValueMap)headers);
/* 146 */     return (Message)restTemplate.postForObject("http://" + MumbleLinkAPI.getApi().getMumbleManager().getHostName() + ":" + MumbleLinkAPI.getApi().getMumbleManager().getPort() + "/servers/{idserver}/user", request, Message.class, new Object[] { Integer.valueOf(server.getId()) });
/*     */   }
/*     */   
/*     */   public void deleteUser() {
/* 150 */     RestTemplate restTemplate = new RestTemplate();
/* 151 */     restTemplate.delete("http://" + MumbleLinkAPI.getApi().getMumbleManager().getHostName() + ":" + MumbleLinkAPI.getApi().getMumbleManager().getPort() + "/servers/{idserver}/user/{iduser}", new Object[] { Integer.valueOf(MumbleLinkAPI.getApi().getMumbleManager().getServer().getId()), Integer.valueOf(this.userid) });
/*     */   }
/*     */   
/*     */   public IMessage muteUser() {
/* 155 */     RestTemplate restTemplate = new RestTemplate();
/* 156 */     return (IMessage)restTemplate.getForObject("http://" + MumbleLinkAPI.getApi().getMumbleManager().getHostName() + ":" + MumbleLinkAPI.getApi().getMumbleManager().getPort() + "/servers/{idserver}/user/{iduser}/mute", Message.class, new Object[] { Integer.valueOf(MumbleLinkAPI.getApi().getMumbleManager().getServer().getId()), this.name });
/*     */   }
/*     */   
/*     */   public IMessage unmuteUser() {
/* 160 */     RestTemplate restTemplate = new RestTemplate();
/* 161 */     return (IMessage)restTemplate.getForObject("http://" + MumbleLinkAPI.getApi().getMumbleManager().getHostName() + ":" + MumbleLinkAPI.getApi().getMumbleManager().getPort() + "/servers/{idserver}/user/{iduser}/unmute", Message.class, new Object[] { Integer.valueOf(MumbleLinkAPI.getApi().getMumbleManager().getServer().getId()), this.name });
/*     */   }
/*     */   
/*     */   public Map<String, String> isLinked() {
/* 165 */     Map<String, String> results = new HashMap<>();
/* 166 */     results.put("context", this.context);
/* 167 */     results.put("identity", this.identity);
/* 168 */     return results;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/fr/ariloxe/mumble/murmur/core/mumble/User.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */