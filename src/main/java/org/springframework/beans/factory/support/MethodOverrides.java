/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CopyOnWriteArraySet;
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
/*     */ public class MethodOverrides
/*     */ {
/*  39 */   private final Set<MethodOverride> overrides = new CopyOnWriteArraySet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MethodOverrides() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MethodOverrides(MethodOverrides other) {
/*  52 */     addOverrides(other);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addOverrides(@Nullable MethodOverrides other) {
/*  60 */     if (other != null) {
/*  61 */       this.overrides.addAll(other.overrides);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addOverride(MethodOverride override) {
/*  69 */     this.overrides.add(override);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<MethodOverride> getOverrides() {
/*  78 */     return this.overrides;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  85 */     return this.overrides.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public MethodOverride getOverride(Method method) {
/*  95 */     MethodOverride match = null;
/*  96 */     for (MethodOverride candidate : this.overrides) {
/*  97 */       if (candidate.matches(method)) {
/*  98 */         match = candidate;
/*     */       }
/*     */     } 
/* 101 */     return match;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 107 */     if (this == other) {
/* 108 */       return true;
/*     */     }
/* 110 */     if (!(other instanceof MethodOverrides)) {
/* 111 */       return false;
/*     */     }
/* 113 */     MethodOverrides that = (MethodOverrides)other;
/* 114 */     return this.overrides.equals(that.overrides);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 119 */     return this.overrides.hashCode();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/MethodOverrides.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */