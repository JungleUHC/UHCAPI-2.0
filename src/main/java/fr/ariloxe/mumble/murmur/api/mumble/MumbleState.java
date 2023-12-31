/*    */ package fr.ariloxe.mumble.murmur.api.mumble;
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum MumbleState
/*    */ {
/*  7 */   LINK("§a§l✔"),
/*  8 */   UNLINK("§6§l✈"),
/*  9 */   DISCONNECT("§c§l✖");
/*    */   private final String name;
/*    */   
/*    */   MumbleState(String name) {
/* 13 */     this.name = name;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 17 */     return this.name;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/fr/ariloxe/mumble/murmur/api/mumble/MumbleState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */