/*    */ package org.springframework.beans.factory.parsing;
/*    */ 
/*    */ import org.springframework.util.StringUtils;
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
/*    */ public class QualifierEntry
/*    */   implements ParseState.Entry
/*    */ {
/*    */   private final String typeName;
/*    */   
/*    */   public QualifierEntry(String typeName) {
/* 37 */     if (!StringUtils.hasText(typeName)) {
/* 38 */       throw new IllegalArgumentException("Invalid qualifier type '" + typeName + "'");
/*    */     }
/* 40 */     this.typeName = typeName;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 46 */     return "Qualifier '" + this.typeName + "'";
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/parsing/QualifierEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */