/*    */ package fr.ariloxe.mumble.murmur.core.mumble;
/*    */ 
/*    */ import fr.ariloxe.mumble.murmur.api.mumble.IMessage;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Message
/*    */   implements IMessage
/*    */ {
/*    */   private String message;
/*    */   private String muted;
/*    */   private String last_active;
/*    */   private String user_id;
/*    */   private String username;
/*    */   
/*    */   public String getMessage() {
/* 17 */     return this.message;
/*    */   }
/*    */   
/*    */   public String getMuted() {
/* 21 */     return this.muted;
/*    */   }
/*    */   
/*    */   public String getLast_active() {
/* 25 */     return this.last_active;
/*    */   }
/*    */   
/*    */   public String getUser_id() {
/* 29 */     return this.user_id;
/*    */   }
/*    */   
/*    */   public String getUsername() {
/* 33 */     return this.username;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 37 */     return "Message{message='" + this.message + '\'' + ", muted='" + this.muted + '\'' + ", last_active='" + this.last_active + '\'' + ", user_id='" + this.user_id + '\'' + ", username='" + this.username + '\'' + '}';
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/fr/ariloxe/mumble/murmur/core/mumble/Message.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */