/*    */ package com.fasterxml.jackson.core;
/*    */ 
/*    */ import com.fasterxml.jackson.core.util.JacksonFeature;
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
/*    */ public enum StreamReadCapability
/*    */   implements JacksonFeature
/*    */ {
/* 30 */   DUPLICATE_PROPERTIES(false),
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 40 */   SCALARS_AS_OBJECTS(false),
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 50 */   UNTYPED_SCALARS(false);
/*    */ 
/*    */   
/*    */   private final boolean _defaultState;
/*    */ 
/*    */   
/*    */   private final int _mask;
/*    */ 
/*    */ 
/*    */   
/*    */   StreamReadCapability(boolean defaultState) {
/* 61 */     this._defaultState = defaultState;
/* 62 */     this._mask = 1 << ordinal();
/*    */   }
/*    */   
/*    */   public boolean enabledByDefault() {
/* 66 */     return this._defaultState;
/*    */   } public boolean enabledIn(int flags) {
/* 68 */     return ((flags & this._mask) != 0);
/*    */   } public int getMask() {
/* 70 */     return this._mask;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/StreamReadCapability.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */