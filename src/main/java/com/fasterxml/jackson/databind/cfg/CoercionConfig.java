/*    */ package com.fasterxml.jackson.databind.cfg;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CoercionConfig
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 13 */   private static final int INPUT_SHAPE_COUNT = (CoercionInputShape.values()).length;
/*    */ 
/*    */   
/*    */   protected Boolean _acceptBlankAsEmpty;
/*    */ 
/*    */   
/*    */   protected final CoercionAction[] _coercionsByShape;
/*    */ 
/*    */ 
/*    */   
/*    */   public CoercionConfig() {
/* 24 */     this._coercionsByShape = new CoercionAction[INPUT_SHAPE_COUNT];
/*    */ 
/*    */     
/* 27 */     this._acceptBlankAsEmpty = null;
/*    */   }
/*    */   
/*    */   protected CoercionConfig(CoercionConfig src) {
/* 31 */     this._acceptBlankAsEmpty = src._acceptBlankAsEmpty;
/* 32 */     this._coercionsByShape = Arrays.<CoercionAction>copyOf(src._coercionsByShape, src._coercionsByShape.length);
/*    */   }
/*    */ 
/*    */   
/*    */   public CoercionAction findAction(CoercionInputShape shape) {
/* 37 */     return this._coercionsByShape[shape.ordinal()];
/*    */   }
/*    */   
/*    */   public Boolean getAcceptBlankAsEmpty() {
/* 41 */     return this._acceptBlankAsEmpty;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/cfg/CoercionConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */