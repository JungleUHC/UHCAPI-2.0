/*     */ package com.fasterxml.jackson.databind.jsontype.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonTypeInfo;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
/*     */ import com.fasterxml.jackson.databind.jsontype.NamedType;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.TokenBuffer;
/*     */ import java.io.IOException;
/*     */ import java.util.BitSet;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AsDeductionTypeDeserializer
/*     */   extends AsPropertyTypeDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  36 */   private static final BitSet EMPTY_CLASS_FINGERPRINT = new BitSet(0);
/*     */ 
/*     */   
/*     */   private final Map<String, Integer> fieldBitIndex;
/*     */ 
/*     */   
/*     */   private final Map<BitSet, String> subtypeFingerprints;
/*     */ 
/*     */   
/*     */   public AsDeductionTypeDeserializer(JavaType bt, TypeIdResolver idRes, JavaType defaultImpl, DeserializationConfig config, Collection<NamedType> subtypes) {
/*  46 */     super(bt, idRes, (String)null, false, defaultImpl, (JsonTypeInfo.As)null);
/*  47 */     this.fieldBitIndex = new HashMap<>();
/*  48 */     this.subtypeFingerprints = buildFingerprints(config, subtypes);
/*     */   }
/*     */   
/*     */   public AsDeductionTypeDeserializer(AsDeductionTypeDeserializer src, BeanProperty property) {
/*  52 */     super(src, property);
/*  53 */     this.fieldBitIndex = src.fieldBitIndex;
/*  54 */     this.subtypeFingerprints = src.subtypeFingerprints;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeDeserializer forProperty(BeanProperty prop) {
/*  59 */     return (prop == this._property) ? this : new AsDeductionTypeDeserializer(this, prop);
/*     */   }
/*     */   
/*     */   protected Map<BitSet, String> buildFingerprints(DeserializationConfig config, Collection<NamedType> subtypes) {
/*  63 */     boolean ignoreCase = config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
/*     */     
/*  65 */     int nextField = 0;
/*  66 */     Map<BitSet, String> fingerprints = new HashMap<>();
/*     */     
/*  68 */     for (NamedType subtype : subtypes) {
/*  69 */       JavaType subtyped = config.getTypeFactory().constructType(subtype.getType());
/*  70 */       List<BeanPropertyDefinition> properties = config.introspect(subtyped).findProperties();
/*     */       
/*  72 */       BitSet fingerprint = new BitSet(nextField + properties.size());
/*  73 */       for (BeanPropertyDefinition property : properties) {
/*  74 */         String name = property.getName();
/*  75 */         if (ignoreCase) name = name.toLowerCase(); 
/*  76 */         Integer bitIndex = this.fieldBitIndex.get(name);
/*  77 */         if (bitIndex == null) {
/*  78 */           bitIndex = Integer.valueOf(nextField);
/*  79 */           this.fieldBitIndex.put(name, Integer.valueOf(nextField++));
/*     */         } 
/*  81 */         fingerprint.set(bitIndex.intValue());
/*     */       } 
/*     */       
/*  84 */       String existingFingerprint = fingerprints.put(fingerprint, subtype.getType().getName());
/*     */ 
/*     */       
/*  87 */       if (existingFingerprint != null) {
/*  88 */         throw new IllegalStateException(
/*  89 */             String.format("Subtypes %s and %s have the same signature and cannot be uniquely deduced.", new Object[] { existingFingerprint, subtype.getType().getName() }));
/*     */       }
/*     */     } 
/*     */     
/*  93 */     return fingerprints;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeTypedFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  99 */     JsonToken t = p.currentToken();
/* 100 */     if (t == JsonToken.START_OBJECT) {
/* 101 */       t = p.nextToken();
/* 102 */     } else if (t != JsonToken.FIELD_NAME) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 110 */       return _deserializeTypedUsingDefaultImpl(p, ctxt, (TokenBuffer)null, "Unexpected input");
/*     */     } 
/*     */ 
/*     */     
/* 114 */     if (t == JsonToken.END_OBJECT) {
/* 115 */       String emptySubtype = this.subtypeFingerprints.get(EMPTY_CLASS_FINGERPRINT);
/* 116 */       if (emptySubtype != null) {
/* 117 */         return _deserializeTypedForId(p, ctxt, (TokenBuffer)null, emptySubtype);
/*     */       }
/*     */     } 
/*     */     
/* 121 */     List<BitSet> candidates = new LinkedList<>(this.subtypeFingerprints.keySet());
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 126 */     TokenBuffer tb = ctxt.bufferForInputBuffering(p);
/* 127 */     boolean ignoreCase = ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
/*     */     
/* 129 */     for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 130 */       String name = p.currentName();
/* 131 */       if (ignoreCase) name = name.toLowerCase();
/*     */       
/* 133 */       tb.copyCurrentStructure(p);
/*     */       
/* 135 */       Integer bit = this.fieldBitIndex.get(name);
/* 136 */       if (bit != null) {
/*     */         
/* 138 */         prune(candidates, bit.intValue());
/* 139 */         if (candidates.size() == 1) {
/* 140 */           return _deserializeTypedForId(p, ctxt, tb, this.subtypeFingerprints.get(candidates.get(0)));
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 146 */     String msgToReportIfDefaultImplFailsToo = String.format("Cannot deduce unique subtype of %s (%d candidates match)", new Object[] { ClassUtil.getTypeDescription(this._baseType), Integer.valueOf(candidates.size()) });
/* 147 */     return _deserializeTypedUsingDefaultImpl(p, ctxt, tb, msgToReportIfDefaultImplFailsToo);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void prune(List<BitSet> candidates, int bit) {
/* 152 */     for (Iterator<BitSet> iter = candidates.iterator(); iter.hasNext();) {
/* 153 */       if (!((BitSet)iter.next()).get(bit))
/* 154 */         iter.remove(); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jsontype/impl/AsDeductionTypeDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */