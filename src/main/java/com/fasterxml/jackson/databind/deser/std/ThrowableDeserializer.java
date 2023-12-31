/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.BeanDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
/*     */ import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.fasterxml.jackson.databind.util.NameTransformer;
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
/*     */ 
/*     */ public class ThrowableDeserializer
/*     */   extends BeanDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected static final String PROP_NAME_MESSAGE = "message";
/*     */   protected static final String PROP_NAME_SUPPRESSED = "suppressed";
/*     */   
/*     */   public ThrowableDeserializer(BeanDeserializer baseDeserializer) {
/*  31 */     super((BeanDeserializerBase)baseDeserializer);
/*     */     
/*  33 */     this._vanillaProcessing = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ThrowableDeserializer(BeanDeserializer src, NameTransformer unwrapper) {
/*  40 */     super((BeanDeserializerBase)src, unwrapper);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
/*  45 */     if (getClass() != ThrowableDeserializer.class) {
/*  46 */       return (JsonDeserializer<Object>)this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  51 */     return (JsonDeserializer<Object>)new ThrowableDeserializer(this, unwrapper);
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
/*     */   public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  64 */     if (this._propertyBasedCreator != null) {
/*  65 */       return _deserializeUsingPropertyBased(p, ctxt);
/*     */     }
/*  67 */     if (this._delegateDeserializer != null) {
/*  68 */       return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer
/*  69 */           .deserialize(p, ctxt));
/*     */     }
/*  71 */     if (this._beanType.isAbstract()) {
/*  72 */       return ctxt.handleMissingInstantiator(handledType(), getValueInstantiator(), p, "abstract type (need to add/enable type information?)", new Object[0]);
/*     */     }
/*     */     
/*  75 */     boolean hasStringCreator = this._valueInstantiator.canCreateFromString();
/*  76 */     boolean hasDefaultCtor = this._valueInstantiator.canCreateUsingDefault();
/*     */     
/*  78 */     if (!hasStringCreator && !hasDefaultCtor) {
/*  79 */       return ctxt.handleMissingInstantiator(handledType(), getValueInstantiator(), p, "Throwable needs a default constructor, a single-String-arg constructor; or explicit @JsonCreator", new Object[0]);
/*     */     }
/*     */ 
/*     */     
/*  83 */     Throwable throwable = null;
/*  84 */     Object[] pending = null;
/*  85 */     Throwable[] suppressed = null;
/*  86 */     int pendingIx = 0;
/*     */     
/*  88 */     for (; !p.hasToken(JsonToken.END_OBJECT); p.nextToken()) {
/*  89 */       String propName = p.currentName();
/*  90 */       SettableBeanProperty prop = this._beanProperties.find(propName);
/*  91 */       p.nextToken();
/*     */       
/*  93 */       if (prop != null) {
/*  94 */         if (throwable != null) {
/*  95 */           prop.deserializeAndSet(p, ctxt, throwable);
/*     */         }
/*     */         else {
/*     */           
/*  99 */           if (pending == null) {
/* 100 */             int len = this._beanProperties.size();
/* 101 */             pending = new Object[len + len];
/*     */           } 
/* 103 */           pending[pendingIx++] = prop;
/* 104 */           pending[pendingIx++] = prop.deserialize(p, ctxt);
/*     */         } 
/*     */         
/*     */         continue;
/*     */       } 
/* 109 */       if ("message".equals(propName)) {
/* 110 */         if (hasStringCreator) {
/* 111 */           throwable = (Throwable)this._valueInstantiator.createFromString(ctxt, p.getValueAsString());
/*     */           continue;
/*     */         } 
/* 114 */       } else if ("suppressed".equals(propName)) {
/* 115 */         suppressed = (Throwable[])ctxt.readValue(p, Throwable[].class);
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 120 */       if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 121 */         p.skipChildren();
/*     */       
/*     */       }
/* 124 */       else if (this._anySetter != null) {
/* 125 */         this._anySetter.deserializeAndSet(p, ctxt, throwable, propName);
/*     */ 
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */         
/* 132 */         handleUnknownProperty(p, ctxt, throwable, propName);
/*     */       }  continue;
/*     */     } 
/* 135 */     if (throwable == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 142 */       if (hasStringCreator) {
/* 143 */         throwable = (Throwable)this._valueInstantiator.createFromString(ctxt, null);
/*     */       } else {
/* 145 */         throwable = (Throwable)this._valueInstantiator.createUsingDefault(ctxt);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 150 */     if (pending != null) {
/* 151 */       for (int i = 0, len = pendingIx; i < len; i += 2) {
/* 152 */         SettableBeanProperty prop = (SettableBeanProperty)pending[i];
/* 153 */         prop.set(throwable, pending[i + 1]);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 158 */     if (suppressed != null) {
/* 159 */       for (Throwable s : suppressed) {
/* 160 */         throwable.addSuppressed(s);
/*     */       }
/*     */     }
/*     */     
/* 164 */     return throwable;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/ThrowableDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */