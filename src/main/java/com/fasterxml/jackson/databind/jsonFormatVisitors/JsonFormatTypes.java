/*    */ package com.fasterxml.jackson.databind.jsonFormatVisitors;
/*    */ 
/*    */ import com.fasterxml.jackson.annotation.JsonCreator;
/*    */ import com.fasterxml.jackson.annotation.JsonValue;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public enum JsonFormatTypes
/*    */ {
/* 10 */   STRING,
/* 11 */   NUMBER,
/* 12 */   INTEGER,
/* 13 */   BOOLEAN,
/* 14 */   OBJECT,
/* 15 */   ARRAY,
/* 16 */   NULL,
/* 17 */   ANY;
/*    */   static {
/* 19 */     _byLCName = new HashMap<>();
/*    */     
/* 21 */     for (JsonFormatTypes t : values())
/* 22 */       _byLCName.put(t.name().toLowerCase(), t); 
/*    */   }
/*    */   private static final Map<String, JsonFormatTypes> _byLCName;
/*    */   
/*    */   @JsonValue
/*    */   public String value() {
/* 28 */     return name().toLowerCase();
/*    */   }
/*    */   
/*    */   @JsonCreator
/*    */   public static JsonFormatTypes forValue(String s) {
/* 33 */     return _byLCName.get(s);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jsonFormatVisitors/JsonFormatTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */