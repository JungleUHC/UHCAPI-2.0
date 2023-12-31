/*    */ package com.fasterxml.jackson.databind.util;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.NoSuchElementException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ArrayIterator<T>
/*    */   implements Iterator<T>, Iterable<T>
/*    */ {
/*    */   private final T[] _a;
/*    */   private int _index;
/*    */   
/*    */   public ArrayIterator(T[] a) {
/* 18 */     this._a = a;
/* 19 */     this._index = 0;
/*    */   }
/*    */   
/*    */   public boolean hasNext() {
/* 23 */     return (this._index < this._a.length);
/*    */   }
/*    */   
/*    */   public T next() {
/* 27 */     if (this._index >= this._a.length) {
/* 28 */       throw new NoSuchElementException();
/*    */     }
/* 30 */     return this._a[this._index++];
/*    */   }
/*    */   
/* 33 */   public void remove() { throw new UnsupportedOperationException(); } public Iterator<T> iterator() {
/* 34 */     return this;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/util/ArrayIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */