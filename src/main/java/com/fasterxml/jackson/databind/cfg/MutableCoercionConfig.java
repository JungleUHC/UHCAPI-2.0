/*    */ package com.fasterxml.jackson.databind.cfg;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MutableCoercionConfig
/*    */   extends CoercionConfig
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public MutableCoercionConfig() {}
/*    */   
/*    */   protected MutableCoercionConfig(MutableCoercionConfig src) {
/* 19 */     super(src);
/*    */   }
/*    */   
/*    */   public MutableCoercionConfig copy() {
/* 23 */     return new MutableCoercionConfig(this);
/*    */   }
/*    */ 
/*    */   
/*    */   public MutableCoercionConfig setCoercion(CoercionInputShape shape, CoercionAction action) {
/* 28 */     this._coercionsByShape[shape.ordinal()] = action;
/* 29 */     return this;
/*    */   }
/*    */   
/*    */   public MutableCoercionConfig setAcceptBlankAsEmpty(Boolean state) {
/* 33 */     this._acceptBlankAsEmpty = state;
/* 34 */     return this;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/cfg/MutableCoercionConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */