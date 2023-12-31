/*    */ package org.springframework.beans.factory.config;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import org.springframework.lang.Nullable;
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
/*    */ public final class AutowiredPropertyMarker
/*    */   implements Serializable
/*    */ {
/* 43 */   public static final Object INSTANCE = new AutowiredPropertyMarker();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private Object readResolve() {
/* 50 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object obj) {
/* 56 */     return (this == obj);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 61 */     return AutowiredPropertyMarker.class.hashCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 66 */     return "(autowired)";
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/config/AutowiredPropertyMarker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */