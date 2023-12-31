/*    */ package org.springframework.beans.factory.support;
/*    */ 
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.util.Assert;
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
/*    */ public class ManagedArray
/*    */   extends ManagedList<Object>
/*    */ {
/*    */   @Nullable
/*    */   volatile Class<?> resolvedElementType;
/*    */   
/*    */   public ManagedArray(String elementTypeName, int size) {
/* 43 */     super(size);
/* 44 */     Assert.notNull(elementTypeName, "elementTypeName must not be null");
/* 45 */     setElementTypeName(elementTypeName);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/ManagedArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */