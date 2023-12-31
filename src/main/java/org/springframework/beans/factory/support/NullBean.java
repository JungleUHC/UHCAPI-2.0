/*    */ package org.springframework.beans.factory.support;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ final class NullBean
/*    */ {
/*    */   public boolean equals(@Nullable Object obj) {
/* 44 */     return (this == obj || obj == null);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 49 */     return NullBean.class.hashCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 54 */     return "null";
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/NullBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */