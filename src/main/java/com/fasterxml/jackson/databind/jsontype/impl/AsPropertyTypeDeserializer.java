/*     */ package com.fasterxml.jackson.databind.jsontype.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonTypeInfo;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.util.JsonParserSequence;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
/*     */ import com.fasterxml.jackson.databind.util.TokenBuffer;
/*     */ import java.io.IOException;
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
/*     */ public class AsPropertyTypeDeserializer
/*     */   extends AsArrayTypeDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JsonTypeInfo.As _inclusion;
/*  32 */   protected final String _msgForMissingId = (this._property == null) ? 
/*  33 */     String.format("missing type id property '%s'", new Object[] { this._typePropertyName
/*  34 */       }) : String.format("missing type id property '%s' (for POJO property '%s')", new Object[] { this._typePropertyName, this._property.getName() });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl) {
/*  42 */     this(bt, idRes, typePropertyName, typeIdVisible, defaultImpl, JsonTypeInfo.As.PROPERTY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl, JsonTypeInfo.As inclusion) {
/*  52 */     super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
/*  53 */     this._inclusion = inclusion;
/*     */   }
/*     */   
/*     */   public AsPropertyTypeDeserializer(AsPropertyTypeDeserializer src, BeanProperty property) {
/*  57 */     super(src, property);
/*  58 */     this._inclusion = src._inclusion;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeDeserializer forProperty(BeanProperty prop) {
/*  63 */     return (prop == this._property) ? this : new AsPropertyTypeDeserializer(this, prop);
/*     */   }
/*     */   
/*     */   public JsonTypeInfo.As getTypeInclusion() {
/*  67 */     return this._inclusion;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeTypedFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  77 */     if (p.canReadTypeId()) {
/*  78 */       Object typeId = p.getTypeId();
/*  79 */       if (typeId != null) {
/*  80 */         return _deserializeWithNativeTypeId(p, ctxt, typeId);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  85 */     JsonToken t = p.currentToken();
/*  86 */     if (t == JsonToken.START_OBJECT) {
/*  87 */       t = p.nextToken();
/*  88 */     } else if (t != JsonToken.FIELD_NAME) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  96 */       return _deserializeTypedUsingDefaultImpl(p, ctxt, (TokenBuffer)null, this._msgForMissingId);
/*     */     } 
/*     */     
/*  99 */     TokenBuffer tb = null;
/* 100 */     boolean ignoreCase = ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
/*     */     
/* 102 */     for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 103 */       String name = p.currentName();
/* 104 */       p.nextToken();
/* 105 */       if (name.equals(this._typePropertyName) || (ignoreCase && name
/* 106 */         .equalsIgnoreCase(this._typePropertyName))) {
/*     */         
/* 108 */         String typeId = p.getValueAsString();
/* 109 */         if (typeId != null) {
/* 110 */           return _deserializeTypedForId(p, ctxt, tb, typeId);
/*     */         }
/*     */       } 
/* 113 */       if (tb == null) {
/* 114 */         tb = ctxt.bufferForInputBuffering(p);
/*     */       }
/* 116 */       tb.writeFieldName(name);
/* 117 */       tb.copyCurrentStructure(p);
/*     */     } 
/* 119 */     return _deserializeTypedUsingDefaultImpl(p, ctxt, tb, this._msgForMissingId);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object _deserializeTypedForId(JsonParser p, DeserializationContext ctxt, TokenBuffer tb, String typeId) throws IOException {
/*     */     JsonParserSequence jsonParserSequence;
/* 125 */     JsonDeserializer<Object> deser = _findDeserializer(ctxt, typeId);
/* 126 */     if (this._typeIdVisible) {
/* 127 */       if (tb == null) {
/* 128 */         tb = ctxt.bufferForInputBuffering(p);
/*     */       }
/* 130 */       tb.writeFieldName(p.currentName());
/* 131 */       tb.writeString(typeId);
/*     */     } 
/* 133 */     if (tb != null) {
/*     */ 
/*     */       
/* 136 */       p.clearCurrentToken();
/* 137 */       jsonParserSequence = JsonParserSequence.createFlattened(false, tb.asParser(p), p);
/*     */     } 
/* 139 */     if (jsonParserSequence.currentToken() != JsonToken.END_OBJECT)
/*     */     {
/* 141 */       jsonParserSequence.nextToken();
/*     */     }
/*     */     
/* 144 */     return deser.deserialize((JsonParser)jsonParserSequence, ctxt);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected Object _deserializeTypedUsingDefaultImpl(JsonParser p, DeserializationContext ctxt, TokenBuffer tb) throws IOException {
/* 150 */     return _deserializeTypedUsingDefaultImpl(p, ctxt, tb, (String)null);
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
/*     */   protected Object _deserializeTypedUsingDefaultImpl(JsonParser p, DeserializationContext ctxt, TokenBuffer tb, String priorFailureMsg) throws IOException {
/* 162 */     if (!hasDefaultImpl()) {
/*     */       
/* 164 */       Object result = TypeDeserializer.deserializeIfNatural(p, ctxt, this._baseType);
/* 165 */       if (result != null) {
/* 166 */         return result;
/*     */       }
/*     */       
/* 169 */       if (p.isExpectedStartArrayToken()) {
/* 170 */         return super.deserializeTypedFromAny(p, ctxt);
/*     */       }
/* 172 */       if (p.hasToken(JsonToken.VALUE_STRING) && 
/* 173 */         ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
/* 174 */         String str = p.getText().trim();
/* 175 */         if (str.isEmpty()) {
/* 176 */           return null;
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 183 */     JsonDeserializer<Object> deser = _findDefaultImplDeserializer(ctxt);
/* 184 */     if (deser == null) {
/* 185 */       JavaType t = _handleMissingTypeId(ctxt, priorFailureMsg);
/* 186 */       if (t == null)
/*     */       {
/* 188 */         return null;
/*     */       }
/*     */       
/* 191 */       deser = ctxt.findContextualValueDeserializer(t, this._property);
/*     */     } 
/* 193 */     if (tb != null) {
/* 194 */       tb.writeEndObject();
/* 195 */       p = tb.asParser(p);
/*     */       
/* 197 */       p.nextToken();
/*     */     } 
/* 199 */     return deser.deserialize(p, ctxt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeTypedFromAny(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 210 */     if (p.hasToken(JsonToken.START_ARRAY)) {
/* 211 */       return deserializeTypedFromArray(p, ctxt);
/*     */     }
/* 213 */     return deserializeTypedFromObject(p, ctxt);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jsontype/impl/AsPropertyTypeDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */