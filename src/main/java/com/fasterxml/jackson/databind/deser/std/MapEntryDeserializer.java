/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JacksonException;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.KeyDeserializer;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import java.io.IOException;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ @JacksonStdImpl
/*     */ public class MapEntryDeserializer
/*     */   extends ContainerDeserializerBase<Map.Entry<Object, Object>>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final KeyDeserializer _keyDeserializer;
/*     */   protected final JsonDeserializer<Object> _valueDeserializer;
/*     */   protected final TypeDeserializer _valueTypeDeserializer;
/*     */   
/*     */   public MapEntryDeserializer(JavaType type, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser) {
/*  59 */     super(type);
/*  60 */     if (type.containedTypeCount() != 2) {
/*  61 */       throw new IllegalArgumentException("Missing generic type information for " + type);
/*     */     }
/*  63 */     this._keyDeserializer = keyDeser;
/*  64 */     this._valueDeserializer = valueDeser;
/*  65 */     this._valueTypeDeserializer = valueTypeDeser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MapEntryDeserializer(MapEntryDeserializer src) {
/*  74 */     super(src);
/*  75 */     this._keyDeserializer = src._keyDeserializer;
/*  76 */     this._valueDeserializer = src._valueDeserializer;
/*  77 */     this._valueTypeDeserializer = src._valueTypeDeserializer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MapEntryDeserializer(MapEntryDeserializer src, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser) {
/*  84 */     super(src);
/*  85 */     this._keyDeserializer = keyDeser;
/*  86 */     this._valueDeserializer = valueDeser;
/*  87 */     this._valueTypeDeserializer = valueTypeDeser;
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
/*     */   protected MapEntryDeserializer withResolved(KeyDeserializer keyDeser, TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser) {
/*  99 */     if (this._keyDeserializer == keyDeser && this._valueDeserializer == valueDeser && this._valueTypeDeserializer == valueTypeDeser)
/*     */     {
/* 101 */       return this;
/*     */     }
/* 103 */     return new MapEntryDeserializer(this, keyDeser, (JsonDeserializer)valueDeser, valueTypeDeser);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LogicalType logicalType() {
/* 110 */     return LogicalType.Map;
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
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/* 127 */     KeyDeserializer kd = this._keyDeserializer;
/* 128 */     if (kd == null) {
/* 129 */       kd = ctxt.findKeyDeserializer(this._containerType.containedType(0), property);
/*     */     }
/* 131 */     else if (kd instanceof ContextualKeyDeserializer) {
/* 132 */       kd = ((ContextualKeyDeserializer)kd).createContextual(ctxt, property);
/*     */     } 
/*     */     
/* 135 */     JsonDeserializer<?> vd = this._valueDeserializer;
/* 136 */     vd = findConvertingContentDeserializer(ctxt, property, vd);
/* 137 */     JavaType contentType = this._containerType.containedType(1);
/* 138 */     if (vd == null) {
/* 139 */       vd = ctxt.findContextualValueDeserializer(contentType, property);
/*     */     } else {
/* 141 */       vd = ctxt.handleSecondaryContextualization(vd, property, contentType);
/*     */     } 
/* 143 */     TypeDeserializer vtd = this._valueTypeDeserializer;
/* 144 */     if (vtd != null) {
/* 145 */       vtd = vtd.forProperty(property);
/*     */     }
/* 147 */     return withResolved(kd, vtd, vd);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType getContentType() {
/* 158 */     return this._containerType.containedType(1);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonDeserializer<Object> getContentDeserializer() {
/* 163 */     return this._valueDeserializer;
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
/*     */   public Map.Entry<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 180 */     JsonToken t = p.currentToken();
/* 181 */     if (t == JsonToken.START_OBJECT) {
/* 182 */       t = p.nextToken();
/* 183 */     } else if (t != JsonToken.FIELD_NAME && t != JsonToken.END_OBJECT) {
/*     */       
/* 185 */       if (t == JsonToken.START_ARRAY) {
/* 186 */         return _deserializeFromArray(p, ctxt);
/*     */       }
/* 188 */       return (Map.Entry<Object, Object>)ctxt.handleUnexpectedToken(getValueType(ctxt), p);
/*     */     } 
/* 190 */     if (t != JsonToken.FIELD_NAME) {
/* 191 */       if (t == JsonToken.END_OBJECT) {
/* 192 */         return (Map.Entry<Object, Object>)ctxt.reportInputMismatch(this, "Cannot deserialize a Map.Entry out of empty JSON Object", new Object[0]);
/*     */       }
/*     */       
/* 195 */       return (Map.Entry<Object, Object>)ctxt.handleUnexpectedToken(handledType(), p);
/*     */     } 
/*     */     
/* 198 */     KeyDeserializer keyDes = this._keyDeserializer;
/* 199 */     JsonDeserializer<Object> valueDes = this._valueDeserializer;
/* 200 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/*     */     
/* 202 */     String keyStr = p.currentName();
/* 203 */     Object key = keyDes.deserializeKey(keyStr, ctxt);
/* 204 */     Object value = null;
/*     */     
/* 206 */     t = p.nextToken();
/*     */     
/*     */     try {
/* 209 */       if (t == JsonToken.VALUE_NULL) {
/* 210 */         value = valueDes.getNullValue(ctxt);
/* 211 */       } else if (typeDeser == null) {
/* 212 */         value = valueDes.deserialize(p, ctxt);
/*     */       } else {
/* 214 */         value = valueDes.deserializeWithType(p, ctxt, typeDeser);
/*     */       } 
/* 216 */     } catch (Exception e) {
/* 217 */       wrapAndThrow(ctxt, e, Map.Entry.class, keyStr);
/*     */     } 
/*     */ 
/*     */     
/* 221 */     t = p.nextToken();
/* 222 */     if (t != JsonToken.END_OBJECT) {
/* 223 */       if (t == JsonToken.FIELD_NAME) {
/* 224 */         ctxt.reportInputMismatch(this, "Problem binding JSON into Map.Entry: more than one entry in JSON (second field: '%s')", new Object[] { p
/*     */               
/* 226 */               .currentName() });
/*     */       } else {
/*     */         
/* 229 */         ctxt.reportInputMismatch(this, "Problem binding JSON into Map.Entry: unexpected content after JSON Object entry: " + t, new Object[0]);
/*     */       } 
/*     */       
/* 232 */       return null;
/*     */     } 
/* 234 */     return new AbstractMap.SimpleEntry<>(key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map.Entry<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt, Map.Entry<Object, Object> result) throws IOException {
/* 241 */     throw new IllegalStateException("Cannot update Map.Entry values");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 250 */     return typeDeserializer.deserializeTypedFromObject(p, ctxt);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/MapEntryDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */