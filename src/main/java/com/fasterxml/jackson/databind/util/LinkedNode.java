/*    */ package com.fasterxml.jackson.databind.util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class LinkedNode<T>
/*    */ {
/*    */   private final T value;
/*    */   private LinkedNode<T> next;
/*    */   
/*    */   public LinkedNode(T value, LinkedNode<T> next) {
/* 16 */     this.value = value;
/* 17 */     this.next = next;
/*    */   }
/*    */ 
/*    */   
/*    */   public void linkNext(LinkedNode<T> n) {
/* 22 */     if (this.next != null) {
/* 23 */       throw new IllegalStateException();
/*    */     }
/* 25 */     this.next = n;
/*    */   }
/*    */   public LinkedNode<T> next() {
/* 28 */     return this.next;
/*    */   } public T value() {
/* 30 */     return this.value;
/*    */   }
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
/*    */   public static <ST> boolean contains(LinkedNode<ST> node, ST value) {
/* 44 */     while (node != null) {
/* 45 */       if (node.value() == value) {
/* 46 */         return true;
/*    */       }
/* 48 */       node = node.next();
/*    */     } 
/* 50 */     return false;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/util/LinkedNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */