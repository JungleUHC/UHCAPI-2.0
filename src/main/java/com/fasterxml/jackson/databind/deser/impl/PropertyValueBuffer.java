/*     */ package com.fasterxml.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DatabindException;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.deser.SettableAnyProperty;
/*     */ import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import java.io.IOException;
/*     */ import java.util.BitSet;
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
/*     */ public class PropertyValueBuffer
/*     */ {
/*     */   protected final JsonParser _parser;
/*     */   protected final DeserializationContext _context;
/*     */   protected final ObjectIdReader _objectIdReader;
/*     */   protected final Object[] _creatorParameters;
/*     */   protected int _paramsNeeded;
/*     */   protected int _paramsSeen;
/*     */   protected final BitSet _paramsSeenBig;
/*     */   protected PropertyValue _buffered;
/*     */   protected Object _idValue;
/*     */   
/*     */   public PropertyValueBuffer(JsonParser p, DeserializationContext ctxt, int paramCount, ObjectIdReader oir) {
/*  87 */     this._parser = p;
/*  88 */     this._context = ctxt;
/*  89 */     this._paramsNeeded = paramCount;
/*  90 */     this._objectIdReader = oir;
/*  91 */     this._creatorParameters = new Object[paramCount];
/*  92 */     if (paramCount < 32) {
/*  93 */       this._paramsSeenBig = null;
/*     */     } else {
/*  95 */       this._paramsSeenBig = new BitSet();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean hasParameter(SettableBeanProperty prop) {
/* 107 */     if (this._paramsSeenBig == null) {
/* 108 */       return ((this._paramsSeen >> prop.getCreatorIndex() & 0x1) == 1);
/*     */     }
/* 110 */     return this._paramsSeenBig.get(prop.getCreatorIndex());
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
/*     */   public Object getParameter(SettableBeanProperty prop) throws JsonMappingException {
/*     */     Object value;
/* 127 */     if (hasParameter(prop)) {
/* 128 */       value = this._creatorParameters[prop.getCreatorIndex()];
/*     */     } else {
/* 130 */       value = this._creatorParameters[prop.getCreatorIndex()] = _findMissing(prop);
/*     */     } 
/* 132 */     if (value == null && this._context.isEnabled(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)) {
/* 133 */       return this._context.reportInputMismatch((BeanProperty)prop, "Null value for creator property '%s' (index %d); `DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES` enabled", new Object[] { prop
/*     */             
/* 135 */             .getName(), Integer.valueOf(prop.getCreatorIndex()) });
/*     */     }
/* 137 */     return value;
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
/*     */   public Object[] getParameters(SettableBeanProperty[] props) throws JsonMappingException {
/* 151 */     if (this._paramsNeeded > 0) {
/* 152 */       if (this._paramsSeenBig == null) {
/* 153 */         int mask = this._paramsSeen;
/*     */ 
/*     */         
/* 156 */         for (int ix = 0, len = this._creatorParameters.length; ix < len; ix++, mask >>= 1) {
/* 157 */           if ((mask & 0x1) == 0) {
/* 158 */             this._creatorParameters[ix] = _findMissing(props[ix]);
/*     */           }
/*     */         } 
/*     */       } else {
/* 162 */         int len = this._creatorParameters.length;
/* 163 */         for (int ix = 0; (ix = this._paramsSeenBig.nextClearBit(ix)) < len; ix++) {
/* 164 */           this._creatorParameters[ix] = _findMissing(props[ix]);
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 169 */     if (this._context.isEnabled(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)) {
/* 170 */       for (int ix = 0; ix < props.length; ix++) {
/* 171 */         if (this._creatorParameters[ix] == null) {
/* 172 */           SettableBeanProperty prop = props[ix];
/* 173 */           this._context.reportInputMismatch((BeanProperty)prop, "Null value for creator property '%s' (index %d); `DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES` enabled", new Object[] { prop
/*     */                 
/* 175 */                 .getName(), Integer.valueOf(props[ix].getCreatorIndex()) });
/*     */         } 
/*     */       } 
/*     */     }
/* 179 */     return this._creatorParameters;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object _findMissing(SettableBeanProperty prop) throws JsonMappingException {
/* 185 */     Object injectableValueId = prop.getInjectableValueId();
/* 186 */     if (injectableValueId != null) {
/* 187 */       return this._context.findInjectableValue(prop.getInjectableValueId(), (BeanProperty)prop, null);
/*     */     }
/*     */ 
/*     */     
/* 191 */     if (prop.isRequired()) {
/* 192 */       this._context.reportInputMismatch((BeanProperty)prop, "Missing required creator property '%s' (index %d)", new Object[] { prop
/* 193 */             .getName(), Integer.valueOf(prop.getCreatorIndex()) });
/*     */     }
/* 195 */     if (this._context.isEnabled(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)) {
/* 196 */       this._context.reportInputMismatch((BeanProperty)prop, "Missing creator property '%s' (index %d); `DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES` enabled", new Object[] { prop
/*     */             
/* 198 */             .getName(), Integer.valueOf(prop.getCreatorIndex()) });
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 203 */       Object absentValue = prop.getNullValueProvider().getAbsentValue(this._context);
/* 204 */       if (absentValue != null) {
/* 205 */         return absentValue;
/*     */       }
/*     */ 
/*     */       
/* 209 */       JsonDeserializer<Object> deser = prop.getValueDeserializer();
/* 210 */       return deser.getAbsentValue(this._context);
/* 211 */     } catch (DatabindException e) {
/*     */       
/* 213 */       AnnotatedMember member = prop.getMember();
/* 214 */       if (member != null) {
/* 215 */         e.prependPath(member.getDeclaringClass(), prop.getName());
/*     */       }
/* 217 */       throw e;
/*     */     } 
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
/*     */   public boolean readIdProperty(String propName) throws IOException {
/* 235 */     if (this._objectIdReader != null && propName.equals(this._objectIdReader.propertyName.getSimpleName())) {
/* 236 */       this._idValue = this._objectIdReader.readObjectReference(this._parser, this._context);
/* 237 */       return true;
/*     */     } 
/* 239 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object handleIdValue(DeserializationContext ctxt, Object bean) throws IOException {
/* 247 */     if (this._objectIdReader != null) {
/* 248 */       if (this._idValue != null) {
/* 249 */         ReadableObjectId roid = ctxt.findObjectId(this._idValue, this._objectIdReader.generator, this._objectIdReader.resolver);
/* 250 */         roid.bindItem(bean);
/*     */         
/* 252 */         SettableBeanProperty idProp = this._objectIdReader.idProperty;
/* 253 */         if (idProp != null) {
/* 254 */           return idProp.setAndReturn(bean, this._idValue);
/*     */         }
/*     */       } else {
/*     */         
/* 258 */         ctxt.reportUnresolvedObjectId(this._objectIdReader, bean);
/*     */       } 
/*     */     }
/* 261 */     return bean;
/*     */   }
/*     */   protected PropertyValue buffered() {
/* 264 */     return this._buffered;
/*     */   } public boolean isComplete() {
/* 266 */     return (this._paramsNeeded <= 0);
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
/*     */   public boolean assignParameter(SettableBeanProperty prop, Object value) {
/* 278 */     int ix = prop.getCreatorIndex();
/* 279 */     this._creatorParameters[ix] = value;
/* 280 */     if (this._paramsSeenBig == null) {
/* 281 */       int old = this._paramsSeen;
/* 282 */       int newValue = old | 1 << ix;
/*     */       
/* 284 */       this._paramsSeen = newValue;
/* 285 */       if (old != newValue && --this._paramsNeeded <= 0)
/*     */       {
/* 287 */         return (this._objectIdReader == null || this._idValue != null);
/*     */       }
/*     */     }
/*     */     else {
/*     */       
/* 292 */       this._paramsSeenBig.set(ix);
/* 293 */       if (this._paramsSeenBig.get(ix) || --this._paramsNeeded <= 0);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 298 */     return false;
/*     */   }
/*     */   
/*     */   public void bufferProperty(SettableBeanProperty prop, Object value) {
/* 302 */     this._buffered = new PropertyValue.Regular(this._buffered, value, prop);
/*     */   }
/*     */   
/*     */   public void bufferAnyProperty(SettableAnyProperty prop, String propName, Object value) {
/* 306 */     this._buffered = new PropertyValue.Any(this._buffered, value, prop, propName);
/*     */   }
/*     */   
/*     */   public void bufferMapProperty(Object key, Object value) {
/* 310 */     this._buffered = new PropertyValue.Map(this._buffered, value, key);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/impl/PropertyValueBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */