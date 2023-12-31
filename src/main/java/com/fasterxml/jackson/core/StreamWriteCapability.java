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
/*    */ public enum StreamWriteCapability
/*    */   implements JacksonFeature
/*    */ {
/* 24 */   CAN_WRITE_BINARY_NATIVELY(false),
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 34 */   CAN_WRITE_FORMATTED_NUMBERS(false);
/*    */ 
/*    */   
/*    */   private final boolean _defaultState;
/*    */ 
/*    */   
/*    */   private final int _mask;
/*    */ 
/*    */ 
/*    */   
/*    */   StreamWriteCapability(boolean defaultState) {
/* 45 */     this._defaultState = defaultState;
/* 46 */     this._mask = 1 << ordinal();
/*    */   }
/*    */   
/*    */   public boolean enabledByDefault() {
/* 50 */     return this._defaultState;
/*    */   } public boolean enabledIn(int flags) {
/* 52 */     return ((flags & this._mask) != 0);
/*    */   } public int getMask() {
/* 54 */     return this._mask;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/StreamWriteCapability.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */