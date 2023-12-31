/*     */ package com.fasterxml.jackson.databind.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IgnorePropertiesUtil
/*     */ {
/*     */   public static boolean shouldIgnore(Object value, Collection<String> toIgnore, Collection<String> toInclude) {
/*  17 */     if (toIgnore == null && toInclude == null) {
/*  18 */       return false;
/*     */     }
/*     */     
/*  21 */     if (toInclude == null) {
/*  22 */       return toIgnore.contains(value);
/*     */     }
/*     */     
/*  25 */     if (toIgnore == null) {
/*  26 */       return !toInclude.contains(value);
/*     */     }
/*     */ 
/*     */     
/*  30 */     return (!toInclude.contains(value) || toIgnore.contains(value));
/*     */   }
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
/*     */   public static Checker buildCheckerIfNeeded(Set<String> toIgnore, Set<String> toInclude) {
/*  44 */     if (toInclude == null && (toIgnore == null || toIgnore.isEmpty())) {
/*  45 */       return null;
/*     */     }
/*  47 */     return Checker.construct(toIgnore, toInclude);
/*     */   }
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
/*     */   public static Set<String> combineNamesToInclude(Set<String> prevToInclude, Set<String> newToInclude) {
/*  63 */     if (prevToInclude == null) {
/*  64 */       return newToInclude;
/*     */     }
/*  66 */     if (newToInclude == null) {
/*  67 */       return prevToInclude;
/*     */     }
/*  69 */     Set<String> result = new HashSet<>();
/*     */ 
/*     */     
/*  72 */     for (String prop : newToInclude) {
/*  73 */       if (prevToInclude.contains(prop)) {
/*  74 */         result.add(prop);
/*     */       }
/*     */     } 
/*  77 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class Checker
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     private final Set<String> _toIgnore;
/*     */     
/*     */     private final Set<String> _toInclude;
/*     */ 
/*     */     
/*     */     private Checker(Set<String> toIgnore, Set<String> toInclude) {
/*  93 */       if (toIgnore == null) {
/*  94 */         toIgnore = Collections.emptySet();
/*     */       }
/*  96 */       this._toIgnore = toIgnore;
/*  97 */       this._toInclude = toInclude;
/*     */     }
/*     */     
/*     */     public static Checker construct(Set<String> toIgnore, Set<String> toInclude) {
/* 101 */       return new Checker(toIgnore, toInclude);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean shouldIgnore(Object propertyName) {
/* 106 */       return ((this._toInclude != null && !this._toInclude.contains(propertyName)) || this._toIgnore
/* 107 */         .contains(propertyName));
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/util/IgnorePropertiesUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */