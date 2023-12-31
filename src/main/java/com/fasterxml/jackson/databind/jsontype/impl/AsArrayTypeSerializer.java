/*    */ package com.fasterxml.jackson.databind.jsontype.impl;
/*    */ 
/*    */ import com.fasterxml.jackson.annotation.JsonTypeInfo;
/*    */ import com.fasterxml.jackson.databind.BeanProperty;
/*    */ import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
/*    */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AsArrayTypeSerializer
/*    */   extends TypeSerializerBase
/*    */ {
/*    */   public AsArrayTypeSerializer(TypeIdResolver idRes, BeanProperty property) {
/* 15 */     super(idRes, property);
/*    */   }
/*    */ 
/*    */   
/*    */   public AsArrayTypeSerializer forProperty(BeanProperty prop) {
/* 20 */     return (this._property == prop) ? this : new AsArrayTypeSerializer(this._idResolver, prop);
/*    */   }
/*    */   
/*    */   public JsonTypeInfo.As getTypeInclusion() {
/* 24 */     return JsonTypeInfo.As.WRAPPER_ARRAY;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jsontype/impl/AsArrayTypeSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */