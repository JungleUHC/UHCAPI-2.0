/*    */ package fr.ariloxe.mumble.murmur.core;
/*    */ 
/*    */ import fr.ariloxe.mumble.murmur.api.MumbleLinkAPI;
/*    */ import fr.ariloxe.mumble.murmur.api.mumble.IMumbleManager;
/*    */ import fr.ariloxe.mumble.murmur.core.mumble.MumbleManager;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MumbleLink
/*    */   extends MumbleLinkAPI
/*    */ {
/* 15 */   private final MumbleManager mumbleManager = new MumbleManager();
/*    */ 
/*    */   
/*    */   public IMumbleManager getMumbleManager() {
/* 19 */     return (IMumbleManager)this.mumbleManager;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/fr/ariloxe/mumble/murmur/core/MumbleLink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */