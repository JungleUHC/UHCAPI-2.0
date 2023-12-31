/*    */ package com.fasterxml.jackson.databind.deser.impl;
/*    */ 
/*    */ import com.fasterxml.jackson.databind.BeanProperty;
/*    */ import com.fasterxml.jackson.databind.DeserializationContext;
/*    */ import com.fasterxml.jackson.databind.JavaType;
/*    */ import com.fasterxml.jackson.databind.JsonMappingException;
/*    */ import com.fasterxml.jackson.databind.PropertyName;
/*    */ import com.fasterxml.jackson.databind.deser.NullValueProvider;
/*    */ import com.fasterxml.jackson.databind.exc.InvalidNullException;
/*    */ import com.fasterxml.jackson.databind.util.AccessPattern;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class NullsFailProvider
/*    */   implements NullValueProvider, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final PropertyName _name;
/*    */   protected final JavaType _type;
/*    */   
/*    */   protected NullsFailProvider(PropertyName name, JavaType type) {
/* 21 */     this._name = name;
/* 22 */     this._type = type;
/*    */   }
/*    */   
/*    */   public static NullsFailProvider constructForProperty(BeanProperty prop) {
/* 26 */     return constructForProperty(prop, prop.getType());
/*    */   }
/*    */ 
/*    */   
/*    */   public static NullsFailProvider constructForProperty(BeanProperty prop, JavaType type) {
/* 31 */     return new NullsFailProvider(prop.getFullName(), type);
/*    */   }
/*    */   
/*    */   public static NullsFailProvider constructForRootValue(JavaType t) {
/* 35 */     return new NullsFailProvider(null, t);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public AccessPattern getNullAccessPattern() {
/* 41 */     return AccessPattern.DYNAMIC;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getNullValue(DeserializationContext ctxt) throws JsonMappingException {
/* 47 */     throw InvalidNullException.from(ctxt, this._name, this._type);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/impl/NullsFailProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */