/*     */ package com.fasterxml.jackson.databind.util;
/*     */ 
/*     */ import com.fasterxml.jackson.core.SerializableString;
/*     */ import com.fasterxml.jackson.databind.SerializationConfig;
/*     */ import com.fasterxml.jackson.databind.SerializationFeature;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.EnumMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ public final class EnumValues
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Class<Enum<?>> _enumClass;
/*     */   private final Enum<?>[] _values;
/*     */   private final SerializableString[] _textual;
/*     */   private transient EnumMap<?, SerializableString> _asMap;
/*     */   
/*     */   private EnumValues(Class<Enum<?>> enumClass, SerializableString[] textual) {
/*  27 */     this._enumClass = enumClass;
/*  28 */     this._values = enumClass.getEnumConstants();
/*  29 */     this._textual = textual;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static EnumValues construct(SerializationConfig config, Class<Enum<?>> enumClass) {
/*  37 */     if (config.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
/*  38 */       return constructFromToString((MapperConfig<?>)config, enumClass);
/*     */     }
/*  40 */     return constructFromName((MapperConfig<?>)config, enumClass);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static EnumValues constructFromName(MapperConfig<?> config, Class<Enum<?>> enumClass) {
/*  46 */     Class<? extends Enum<?>> enumCls = ClassUtil.findEnumType(enumClass);
/*  47 */     Enum[] arrayOfEnum = (Enum[])enumCls.getEnumConstants();
/*  48 */     if (arrayOfEnum == null) {
/*  49 */       throw new IllegalArgumentException("Cannot determine enum constants for Class " + enumClass.getName());
/*     */     }
/*  51 */     String[] names = config.getAnnotationIntrospector().findEnumValues(enumCls, arrayOfEnum, new String[arrayOfEnum.length]);
/*  52 */     SerializableString[] textual = new SerializableString[arrayOfEnum.length];
/*  53 */     for (int i = 0, len = arrayOfEnum.length; i < len; i++) {
/*  54 */       Enum<?> en = arrayOfEnum[i];
/*  55 */       String name = names[i];
/*  56 */       if (name == null) {
/*  57 */         name = en.name();
/*     */       }
/*  59 */       textual[en.ordinal()] = config.compileString(name);
/*     */     } 
/*  61 */     return construct(enumClass, textual);
/*     */   }
/*     */ 
/*     */   
/*     */   public static EnumValues constructFromToString(MapperConfig<?> config, Class<Enum<?>> enumClass) {
/*  66 */     Class<? extends Enum<?>> cls = ClassUtil.findEnumType(enumClass);
/*  67 */     Enum[] arrayOfEnum = (Enum[])cls.getEnumConstants();
/*  68 */     if (arrayOfEnum == null) {
/*  69 */       throw new IllegalArgumentException("Cannot determine enum constants for Class " + enumClass.getName());
/*     */     }
/*  71 */     ArrayList<String> external = new ArrayList<>(arrayOfEnum.length);
/*  72 */     for (Enum<?> en : arrayOfEnum) {
/*  73 */       external.add(en.toString());
/*     */     }
/*  75 */     return construct(config, enumClass, external);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static EnumValues construct(MapperConfig<?> config, Class<Enum<?>> enumClass, List<String> externalValues) {
/*  83 */     int len = externalValues.size();
/*  84 */     SerializableString[] textual = new SerializableString[len];
/*  85 */     for (int i = 0; i < len; i++) {
/*  86 */       textual[i] = config.compileString(externalValues.get(i));
/*     */     }
/*  88 */     return construct(enumClass, textual);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static EnumValues construct(Class<Enum<?>> enumClass, SerializableString[] externalValues) {
/*  96 */     return new EnumValues(enumClass, externalValues);
/*     */   }
/*     */   
/*     */   public SerializableString serializedValueFor(Enum<?> key) {
/* 100 */     return this._textual[key.ordinal()];
/*     */   }
/*     */   
/*     */   public Collection<SerializableString> values() {
/* 104 */     return Arrays.asList(this._textual);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Enum<?>> enums() {
/* 113 */     return Arrays.asList(this._values);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EnumMap<?, SerializableString> internalMap() {
/* 121 */     EnumMap<?, SerializableString> result = this._asMap;
/* 122 */     if (result == null) {
/*     */       
/* 124 */       Map<Enum<?>, SerializableString> map = new LinkedHashMap<>();
/* 125 */       for (Enum<?> en : this._values) {
/* 126 */         map.put(en, this._textual[en.ordinal()]);
/*     */       }
/* 128 */       result = new EnumMap<>(map);
/*     */     } 
/* 130 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<Enum<?>> getEnumClass() {
/* 136 */     return this._enumClass;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/util/EnumValues.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */