/*     */ package org.springframework.beans.factory.parsing;
/*     */ 
/*     */ import java.util.ArrayDeque;
/*     */ import org.springframework.lang.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ParseState
/*     */ {
/*     */   private final ArrayDeque<Entry> state;
/*     */   
/*     */   public ParseState() {
/*  47 */     this.state = new ArrayDeque<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ParseState(ParseState other) {
/*  55 */     this.state = other.state.clone();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void push(Entry entry) {
/*  63 */     this.state.push(entry);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void pop() {
/*  70 */     this.state.pop();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Entry peek() {
/*  79 */     return this.state.peek();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseState snapshot() {
/*  87 */     return new ParseState(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*  96 */     StringBuilder sb = new StringBuilder(64);
/*  97 */     int i = 0;
/*  98 */     for (Entry entry : this.state) {
/*  99 */       if (i > 0) {
/* 100 */         sb.append('\n');
/* 101 */         for (int j = 0; j < i; j++) {
/* 102 */           sb.append('\t');
/*     */         }
/* 104 */         sb.append("-> ");
/*     */       } 
/* 106 */       sb.append(entry);
/* 107 */       i++;
/*     */     } 
/* 109 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static interface Entry {}
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/parsing/ParseState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */