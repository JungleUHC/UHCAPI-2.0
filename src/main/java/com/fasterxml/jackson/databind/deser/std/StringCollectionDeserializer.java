/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.core.JacksonException;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.NullValueProvider;
/*     */ import com.fasterxml.jackson.databind.deser.ValueInstantiator;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Objects;
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
/*     */ 
/*     */ 
/*     */ @JacksonStdImpl
/*     */ public final class StringCollectionDeserializer
/*     */   extends ContainerDeserializerBase<Collection<String>>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JsonDeserializer<String> _valueDeserializer;
/*     */   protected final ValueInstantiator _valueInstantiator;
/*     */   protected final JsonDeserializer<Object> _delegateDeserializer;
/*     */   
/*     */   public StringCollectionDeserializer(JavaType collectionType, JsonDeserializer<?> valueDeser, ValueInstantiator valueInstantiator) {
/*  64 */     this(collectionType, valueInstantiator, (JsonDeserializer<?>)null, valueDeser, (NullValueProvider)valueDeser, (Boolean)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected StringCollectionDeserializer(JavaType collectionType, ValueInstantiator valueInstantiator, JsonDeserializer<?> delegateDeser, JsonDeserializer<?> valueDeser, NullValueProvider nuller, Boolean unwrapSingle) {
/*  73 */     super(collectionType, nuller, unwrapSingle);
/*  74 */     this._valueDeserializer = (JsonDeserializer)valueDeser;
/*  75 */     this._valueInstantiator = valueInstantiator;
/*  76 */     this._delegateDeserializer = (JsonDeserializer)delegateDeser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected StringCollectionDeserializer withResolved(JsonDeserializer<?> delegateDeser, JsonDeserializer<?> valueDeser, NullValueProvider nuller, Boolean unwrapSingle) {
/*  83 */     if (Objects.equals(this._unwrapSingle, unwrapSingle) && this._nullProvider == nuller && this._valueDeserializer == valueDeser && this._delegateDeserializer == delegateDeser)
/*     */     {
/*  85 */       return this;
/*     */     }
/*  87 */     return new StringCollectionDeserializer(this._containerType, this._valueInstantiator, delegateDeser, valueDeser, nuller, unwrapSingle);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCachable() {
/*  95 */     return (this._valueDeserializer == null && this._delegateDeserializer == null);
/*     */   }
/*     */ 
/*     */   
/*     */   public LogicalType logicalType() {
/* 100 */     return LogicalType.Collection;
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
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/* 113 */     JsonDeserializer<Object> delegate = null;
/* 114 */     if (this._valueInstantiator != null) {
/*     */       
/* 116 */       AnnotatedWithParams delegateCreator = this._valueInstantiator.getArrayDelegateCreator();
/* 117 */       if (delegateCreator != null) {
/* 118 */         JavaType delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
/* 119 */         delegate = findDeserializer(ctxt, delegateType, property);
/* 120 */       } else if ((delegateCreator = this._valueInstantiator.getDelegateCreator()) != null) {
/* 121 */         JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
/* 122 */         delegate = findDeserializer(ctxt, delegateType, property);
/*     */       } 
/*     */     } 
/* 125 */     JsonDeserializer<?> valueDeser = this._valueDeserializer;
/* 126 */     JavaType valueType = this._containerType.getContentType();
/* 127 */     if (valueDeser == null) {
/*     */       
/* 129 */       valueDeser = findConvertingContentDeserializer(ctxt, property, valueDeser);
/* 130 */       if (valueDeser == null)
/*     */       {
/* 132 */         valueDeser = ctxt.findContextualValueDeserializer(valueType, property);
/*     */       }
/*     */     } else {
/* 135 */       valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, valueType);
/*     */     } 
/*     */ 
/*     */     
/* 139 */     Boolean unwrapSingle = findFormatFeature(ctxt, property, Collection.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
/*     */     
/* 141 */     NullValueProvider nuller = findContentNullProvider(ctxt, property, valueDeser);
/* 142 */     if (isDefaultDeserializer(valueDeser)) {
/* 143 */       valueDeser = null;
/*     */     }
/* 145 */     return withResolved(delegate, valueDeser, nuller, unwrapSingle);
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
/*     */   public JsonDeserializer<Object> getContentDeserializer() {
/* 157 */     JsonDeserializer<?> deser = this._valueDeserializer;
/* 158 */     return (JsonDeserializer)deser;
/*     */   }
/*     */ 
/*     */   
/*     */   public ValueInstantiator getValueInstantiator() {
/* 163 */     return this._valueInstantiator;
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
/*     */   public Collection<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 177 */     if (this._delegateDeserializer != null) {
/* 178 */       return (Collection<String>)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer
/* 179 */           .deserialize(p, ctxt));
/*     */     }
/* 181 */     Collection<String> result = (Collection<String>)this._valueInstantiator.createUsingDefault(ctxt);
/* 182 */     return deserialize(p, ctxt, result);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<String> deserialize(JsonParser p, DeserializationContext ctxt, Collection<String> result) throws IOException {
/* 191 */     if (!p.isExpectedStartArrayToken()) {
/* 192 */       return handleNonArray(p, ctxt, result);
/*     */     }
/*     */     
/* 195 */     if (this._valueDeserializer != null) {
/* 196 */       return deserializeUsingCustom(p, ctxt, result, this._valueDeserializer);
/*     */     }
/*     */     
/*     */     try {
/*     */       while (true) {
/* 201 */         String value = p.nextTextValue();
/* 202 */         if (value != null) {
/* 203 */           result.add(value);
/*     */           continue;
/*     */         } 
/* 206 */         JsonToken t = p.currentToken();
/* 207 */         if (t == JsonToken.END_ARRAY) {
/*     */           break;
/*     */         }
/* 210 */         if (t == JsonToken.VALUE_NULL) {
/* 211 */           if (this._skipNullValues) {
/*     */             continue;
/*     */           }
/* 214 */           value = (String)this._nullProvider.getNullValue(ctxt);
/*     */         } else {
/* 216 */           value = _parseString(p, ctxt);
/*     */         } 
/* 218 */         result.add(value);
/*     */       } 
/* 220 */     } catch (Exception e) {
/* 221 */       throw JsonMappingException.wrapWithPath(e, result, result.size());
/*     */     } 
/* 223 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Collection<String> deserializeUsingCustom(JsonParser p, DeserializationContext ctxt, Collection<String> result, JsonDeserializer<String> deser) throws IOException {
/*     */     try {
/*     */       while (true) {
/*     */         String value;
/* 237 */         if (p.nextTextValue() == null) {
/* 238 */           JsonToken t = p.currentToken();
/* 239 */           if (t == JsonToken.END_ARRAY) {
/*     */             break;
/*     */           }
/*     */           
/* 243 */           if (t == JsonToken.VALUE_NULL) {
/* 244 */             if (this._skipNullValues) {
/*     */               continue;
/*     */             }
/* 247 */             value = (String)this._nullProvider.getNullValue(ctxt);
/*     */           } else {
/* 249 */             value = (String)deser.deserialize(p, ctxt);
/*     */           } 
/*     */         } else {
/* 252 */           value = (String)deser.deserialize(p, ctxt);
/*     */         } 
/* 254 */         result.add(value);
/*     */       } 
/* 256 */     } catch (Exception e) {
/* 257 */       throw JsonMappingException.wrapWithPath(e, result, result.size());
/*     */     } 
/* 259 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 266 */     return typeDeserializer.deserializeTypedFromArray(p, ctxt);
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
/*     */   private final Collection<String> handleNonArray(JsonParser p, DeserializationContext ctxt, Collection<String> result) throws IOException {
/*     */     String value;
/* 281 */     boolean canWrap = (this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)));
/* 282 */     if (!canWrap) {
/* 283 */       if (p.hasToken(JsonToken.VALUE_STRING)) {
/* 284 */         return _deserializeFromString(p, ctxt);
/*     */       }
/* 286 */       return (Collection<String>)ctxt.handleUnexpectedToken(this._containerType, p);
/*     */     } 
/*     */     
/* 289 */     JsonDeserializer<String> valueDes = this._valueDeserializer;
/* 290 */     JsonToken t = p.currentToken();
/*     */ 
/*     */ 
/*     */     
/* 294 */     if (t == JsonToken.VALUE_NULL) {
/*     */       
/* 296 */       if (this._skipNullValues) {
/* 297 */         return result;
/*     */       }
/* 299 */       value = (String)this._nullProvider.getNullValue(ctxt);
/*     */     } else {
/*     */       try {
/* 302 */         value = (valueDes == null) ? _parseString(p, ctxt) : (String)valueDes.deserialize(p, ctxt);
/* 303 */       } catch (Exception e) {
/* 304 */         throw JsonMappingException.wrapWithPath(e, result, result.size());
/*     */       } 
/*     */     } 
/* 307 */     result.add(value);
/* 308 */     return result;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/StringCollectionDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */