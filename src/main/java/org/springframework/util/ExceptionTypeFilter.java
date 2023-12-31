/*    */ package org.springframework.util;
/*    */ 
/*    */ import java.util.Collection;
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
/*    */ public class ExceptionTypeFilter
/*    */   extends InstanceFilter<Class<? extends Throwable>>
/*    */ {
/*    */   public ExceptionTypeFilter(Collection<? extends Class<? extends Throwable>> includes, Collection<? extends Class<? extends Throwable>> excludes, boolean matchIfEmpty) {
/* 33 */     super(includes, excludes, matchIfEmpty);
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean match(Class<? extends Throwable> instance, Class<? extends Throwable> candidate) {
/* 38 */     return candidate.isAssignableFrom(instance);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/util/ExceptionTypeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */