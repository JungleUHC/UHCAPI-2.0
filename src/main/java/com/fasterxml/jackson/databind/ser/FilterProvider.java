/*    */ package com.fasterxml.jackson.databind.ser;
/*    */ 
/*    */ import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class FilterProvider
/*    */ {
/*    */   @Deprecated
/*    */   public abstract BeanPropertyFilter findFilter(Object paramObject);
/*    */   
/*    */   public PropertyFilter findPropertyFilter(Object filterId, Object valueToFilter) {
/* 52 */     BeanPropertyFilter old = findFilter(filterId);
/* 53 */     if (old == null) {
/* 54 */       return null;
/*    */     }
/* 56 */     return SimpleBeanPropertyFilter.from(old);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/FilterProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */