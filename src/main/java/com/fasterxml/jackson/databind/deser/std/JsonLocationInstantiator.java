/*    */ package com.fasterxml.jackson.databind.deser.std;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonLocation;
/*    */ import com.fasterxml.jackson.core.io.ContentReference;
/*    */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*    */ import com.fasterxml.jackson.databind.DeserializationContext;
/*    */ import com.fasterxml.jackson.databind.JavaType;
/*    */ import com.fasterxml.jackson.databind.PropertyMetadata;
/*    */ import com.fasterxml.jackson.databind.PropertyName;
/*    */ import com.fasterxml.jackson.databind.deser.CreatorProperty;
/*    */ import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*    */ import com.fasterxml.jackson.databind.deser.ValueInstantiator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JsonLocationInstantiator
/*    */   extends ValueInstantiator.Base
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public JsonLocationInstantiator() {
/* 25 */     super(JsonLocation.class);
/*    */   }
/*    */   
/*    */   public boolean canCreateFromObjectWith() {
/* 29 */     return true;
/*    */   }
/*    */   
/*    */   public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
/* 33 */     JavaType intType = config.constructType(int.class);
/* 34 */     JavaType longType = config.constructType(long.class);
/* 35 */     return new SettableBeanProperty[] {
/*    */ 
/*    */         
/* 38 */         (SettableBeanProperty)creatorProp("sourceRef", config.constructType(Object.class), 0), 
/* 39 */         (SettableBeanProperty)creatorProp("byteOffset", longType, 1), 
/* 40 */         (SettableBeanProperty)creatorProp("charOffset", longType, 2), 
/* 41 */         (SettableBeanProperty)creatorProp("lineNr", intType, 3), 
/* 42 */         (SettableBeanProperty)creatorProp("columnNr", intType, 4)
/*    */       };
/*    */   }
/*    */   
/*    */   private static CreatorProperty creatorProp(String name, JavaType type, int index) {
/* 47 */     return CreatorProperty.construct(PropertyName.construct(name), type, null, null, null, null, index, null, PropertyMetadata.STD_REQUIRED);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) {
/* 56 */     ContentReference srcRef = ContentReference.rawReference(args[0]);
/* 57 */     return new JsonLocation(srcRef, _long(args[1]), _long(args[2]), 
/* 58 */         _int(args[3]), _int(args[4]));
/*    */   }
/*    */   
/*    */   private static final long _long(Object o) {
/* 62 */     return (o == null) ? 0L : ((Number)o).longValue();
/*    */   }
/*    */   
/*    */   private static final int _int(Object o) {
/* 66 */     return (o == null) ? 0 : ((Number)o).intValue();
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/JsonLocationInstantiator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */