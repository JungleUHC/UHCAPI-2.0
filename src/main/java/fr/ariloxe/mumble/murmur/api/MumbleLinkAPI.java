/*    */ package fr.ariloxe.mumble.murmur.api;
/*    */ 
/*    */ import fr.ariloxe.mumble.murmur.api.mumble.IMumbleManager;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class MumbleLinkAPI
/*    */ {
/*    */   private static MumbleLinkAPI api;
/*    */   
/*    */   public static MumbleLinkAPI getApi() {
/* 13 */     return api;
/*    */   }
/*    */   
/*    */   public static void setLink(MumbleLinkAPI api) {
/* 17 */     MumbleLinkAPI.api = api;
/*    */   }
/*    */   
/*    */   public abstract IMumbleManager getMumbleManager();
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/fr/ariloxe/mumble/murmur/api/MumbleLinkAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */