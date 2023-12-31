/*    */ package com.fasterxml.jackson.core.util;
/*    */ 
/*    */ import java.util.concurrent.ConcurrentHashMap;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class InternCache
/*    */   extends ConcurrentHashMap<String, String>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final int MAX_ENTRIES = 180;
/* 28 */   public static final InternCache instance = new InternCache();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 35 */   private final Object lock = new Object();
/*    */   private InternCache() {
/* 37 */     super(180, 0.8F, 4);
/*    */   }
/*    */   public String intern(String input) {
/* 40 */     String result = get(input);
/* 41 */     if (result != null) return result;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 48 */     if (size() >= 180)
/*    */     {
/*    */ 
/*    */ 
/*    */       
/* 53 */       synchronized (this.lock) {
/* 54 */         if (size() >= 180) {
/* 55 */           clear();
/*    */         }
/*    */       } 
/*    */     }
/* 59 */     result = input.intern();
/* 60 */     put(result, result);
/* 61 */     return result;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/util/InternCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */