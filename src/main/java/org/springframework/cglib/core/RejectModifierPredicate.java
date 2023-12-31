/*    */ package org.springframework.cglib.core;
/*    */ 
/*    */ import java.lang.reflect.Member;
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
/*    */ public class RejectModifierPredicate
/*    */   implements Predicate
/*    */ {
/*    */   private int rejectMask;
/*    */   
/*    */   public RejectModifierPredicate(int rejectMask) {
/* 24 */     this.rejectMask = rejectMask;
/*    */   }
/*    */   
/*    */   public boolean evaluate(Object arg) {
/* 28 */     return ((((Member)arg).getModifiers() & this.rejectMask) == 0);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/cglib/core/RejectModifierPredicate.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */