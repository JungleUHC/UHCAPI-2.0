/*     */ package com.fasterxml.jackson.databind.cfg;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.Serializable;
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
/*     */ public final class ConstructorDetector
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public enum SingleArgConstructor
/*     */   {
/*  38 */     DELEGATING,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  43 */     PROPERTIES,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  51 */     HEURISTIC,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  58 */     REQUIRE_MODE;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  80 */   public static final ConstructorDetector DEFAULT = new ConstructorDetector(SingleArgConstructor.HEURISTIC);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  87 */   public static final ConstructorDetector USE_PROPERTIES_BASED = new ConstructorDetector(SingleArgConstructor.PROPERTIES);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  94 */   public static final ConstructorDetector USE_DELEGATING = new ConstructorDetector(SingleArgConstructor.DELEGATING);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 101 */   public static final ConstructorDetector EXPLICIT_ONLY = new ConstructorDetector(SingleArgConstructor.REQUIRE_MODE);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final SingleArgConstructor _singleArgMode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final boolean _requireCtorAnnotation;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final boolean _allowJDKTypeCtors;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ConstructorDetector(SingleArgConstructor singleArgMode, boolean requireCtorAnnotation, boolean allowJDKTypeCtors) {
/* 135 */     this._singleArgMode = singleArgMode;
/* 136 */     this._requireCtorAnnotation = requireCtorAnnotation;
/* 137 */     this._allowJDKTypeCtors = allowJDKTypeCtors;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ConstructorDetector(SingleArgConstructor singleArgMode) {
/* 145 */     this(singleArgMode, false, false);
/*     */   }
/*     */   
/*     */   public ConstructorDetector withSingleArgMode(SingleArgConstructor singleArgMode) {
/* 149 */     return new ConstructorDetector(singleArgMode, this._requireCtorAnnotation, this._allowJDKTypeCtors);
/*     */   }
/*     */ 
/*     */   
/*     */   public ConstructorDetector withRequireAnnotation(boolean state) {
/* 154 */     return new ConstructorDetector(this._singleArgMode, state, this._allowJDKTypeCtors);
/*     */   }
/*     */ 
/*     */   
/*     */   public ConstructorDetector withAllowJDKTypeConstructors(boolean state) {
/* 159 */     return new ConstructorDetector(this._singleArgMode, this._requireCtorAnnotation, state);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SingleArgConstructor singleArgMode() {
/* 170 */     return this._singleArgMode;
/*     */   }
/*     */   
/*     */   public boolean requireCtorAnnotation() {
/* 174 */     return this._requireCtorAnnotation;
/*     */   }
/*     */   
/*     */   public boolean allowJDKTypeConstructors() {
/* 178 */     return this._allowJDKTypeCtors;
/*     */   }
/*     */   
/*     */   public boolean singleArgCreatorDefaultsToDelegating() {
/* 182 */     return (this._singleArgMode == SingleArgConstructor.DELEGATING);
/*     */   }
/*     */   
/*     */   public boolean singleArgCreatorDefaultsToProperties() {
/* 186 */     return (this._singleArgMode == SingleArgConstructor.PROPERTIES);
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
/*     */   public boolean shouldIntrospectorImplicitConstructors(Class<?> rawType) {
/* 201 */     if (this._requireCtorAnnotation) {
/* 202 */       return false;
/*     */     }
/*     */     
/* 205 */     if (!this._allowJDKTypeCtors && 
/* 206 */       ClassUtil.isJDKClass(rawType))
/*     */     {
/*     */ 
/*     */       
/* 210 */       if (!Throwable.class.isAssignableFrom(rawType)) {
/* 211 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 215 */     return true;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/cfg/ConstructorDetector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */