/*     */ package com.fasterxml.jackson.databind.util;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
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
/*     */ public class EnumResolver
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final Class<Enum<?>> _enumClass;
/*     */   protected final Enum<?>[] _enums;
/*     */   protected final HashMap<String, Enum<?>> _enumsById;
/*     */   protected final Enum<?> _defaultValue;
/*     */   protected final boolean _isIgnoreCase;
/*     */   protected final boolean _isFromIntValue;
/*     */   
/*     */   protected EnumResolver(Class<Enum<?>> enumClass, Enum<?>[] enums, HashMap<String, Enum<?>> map, Enum<?> defaultValue, boolean isIgnoreCase, boolean isFromIntValue) {
/*  52 */     this._enumClass = enumClass;
/*  53 */     this._enums = enums;
/*  54 */     this._enumsById = map;
/*  55 */     this._defaultValue = defaultValue;
/*  56 */     this._isIgnoreCase = isIgnoreCase;
/*  57 */     this._isFromIntValue = isFromIntValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected EnumResolver(Class<Enum<?>> enumClass, Enum<?>[] enums, HashMap<String, Enum<?>> map, Enum<?> defaultValue, boolean isIgnoreCase) {
/*  67 */     this(enumClass, enums, map, defaultValue, isIgnoreCase, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static EnumResolver constructFor(DeserializationConfig config, Class<?> enumCls) {
/*  78 */     return _constructFor(enumCls, config.getAnnotationIntrospector(), config
/*  79 */         .isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static EnumResolver _constructFor(Class<?> enumCls0, AnnotationIntrospector ai, boolean isIgnoreCase) {
/*  88 */     Class<Enum<?>> enumCls = _enumClass(enumCls0);
/*  89 */     Enum[] arrayOfEnum = (Enum[])_enumConstants(enumCls0);
/*  90 */     String[] names = ai.findEnumValues(enumCls, arrayOfEnum, new String[arrayOfEnum.length]);
/*  91 */     String[][] allAliases = new String[names.length][];
/*  92 */     ai.findEnumAliases(enumCls, arrayOfEnum, allAliases);
/*  93 */     HashMap<String, Enum<?>> map = new HashMap<>();
/*  94 */     for (int i = 0, len = arrayOfEnum.length; i < len; i++) {
/*  95 */       Enum<?> enumValue = arrayOfEnum[i];
/*  96 */       String name = names[i];
/*  97 */       if (name == null) {
/*  98 */         name = enumValue.name();
/*     */       }
/* 100 */       map.put(name, enumValue);
/* 101 */       String[] aliases = allAliases[i];
/* 102 */       if (aliases != null) {
/* 103 */         for (String alias : aliases) {
/*     */ 
/*     */           
/* 106 */           if (!map.containsKey(alias)) {
/* 107 */             map.put(alias, enumValue);
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/* 112 */     return new EnumResolver(enumCls, (Enum<?>[])arrayOfEnum, map, 
/* 113 */         _enumDefault(ai, enumCls), isIgnoreCase, false);
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
/*     */   public static EnumResolver constructUsingToString(DeserializationConfig config, Class<?> enumCls) {
/* 125 */     return _constructUsingToString(enumCls, config.getAnnotationIntrospector(), config
/* 126 */         .isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static EnumResolver _constructUsingToString(Class<?> enumCls0, AnnotationIntrospector ai, boolean isIgnoreCase) {
/* 135 */     Class<Enum<?>> enumCls = _enumClass(enumCls0);
/* 136 */     Enum[] arrayOfEnum = (Enum[])_enumConstants(enumCls0);
/* 137 */     HashMap<String, Enum<?>> map = new HashMap<>();
/* 138 */     String[][] allAliases = new String[arrayOfEnum.length][];
/* 139 */     if (ai != null) {
/* 140 */       ai.findEnumAliases(enumCls, arrayOfEnum, allAliases);
/*     */     }
/*     */ 
/*     */     
/* 144 */     for (int i = arrayOfEnum.length; --i >= 0; ) {
/* 145 */       Enum<?> enumValue = arrayOfEnum[i];
/* 146 */       map.put(enumValue.toString(), enumValue);
/* 147 */       String[] aliases = allAliases[i];
/* 148 */       if (aliases != null) {
/* 149 */         for (String alias : aliases) {
/*     */ 
/*     */           
/* 152 */           if (!map.containsKey(alias)) {
/* 153 */             map.put(alias, enumValue);
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/* 158 */     return new EnumResolver(enumCls, (Enum<?>[])arrayOfEnum, map, 
/* 159 */         _enumDefault(ai, enumCls), isIgnoreCase, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static EnumResolver constructUsingMethod(DeserializationConfig config, Class<?> enumCls, AnnotatedMember accessor) {
/* 170 */     return _constructUsingMethod(enumCls, accessor, config.getAnnotationIntrospector(), config
/* 171 */         .isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static EnumResolver _constructUsingMethod(Class<?> enumCls0, AnnotatedMember accessor, AnnotationIntrospector ai, boolean isIgnoreCase) {
/* 180 */     Class<Enum<?>> enumCls = _enumClass(enumCls0);
/* 181 */     Enum[] arrayOfEnum = (Enum[])_enumConstants(enumCls0);
/* 182 */     HashMap<String, Enum<?>> map = new HashMap<>();
/*     */     
/* 184 */     for (int i = arrayOfEnum.length; --i >= 0; ) {
/* 185 */       Enum<?> en = arrayOfEnum[i];
/*     */       try {
/* 187 */         Object o = accessor.getValue(en);
/* 188 */         if (o != null) {
/* 189 */           map.put(o.toString(), en);
/*     */         }
/* 191 */       } catch (Exception e) {
/* 192 */         throw new IllegalArgumentException("Failed to access @JsonValue of Enum value " + en + ": " + e.getMessage());
/*     */       } 
/*     */     } 
/* 195 */     return new EnumResolver(enumCls, (Enum<?>[])arrayOfEnum, map, 
/* 196 */         _enumDefault(ai, enumCls), isIgnoreCase, 
/*     */         
/* 198 */         _isIntType(accessor.getRawType()));
/*     */   }
/*     */ 
/*     */   
/*     */   public CompactStringObjectMap constructLookup() {
/* 203 */     return CompactStringObjectMap.construct(this._enumsById);
/*     */   }
/*     */ 
/*     */   
/*     */   protected static Class<Enum<?>> _enumClass(Class<?> enumCls0) {
/* 208 */     return (Class)enumCls0;
/*     */   }
/*     */   
/*     */   protected static Enum<?>[] _enumConstants(Class<?> enumCls) {
/* 212 */     Enum[] arrayOfEnum = (Enum[])_enumClass(enumCls).getEnumConstants();
/* 213 */     if (arrayOfEnum == null) {
/* 214 */       throw new IllegalArgumentException("No enum constants for class " + enumCls.getName());
/*     */     }
/* 216 */     return (Enum<?>[])arrayOfEnum;
/*     */   }
/*     */   
/*     */   protected static Enum<?> _enumDefault(AnnotationIntrospector intr, Class<?> enumCls) {
/* 220 */     return (intr != null) ? intr.findDefaultEnumValue(_enumClass(enumCls)) : null;
/*     */   }
/*     */   
/*     */   protected static boolean _isIntType(Class<?> erasedType) {
/* 224 */     if (erasedType.isPrimitive()) {
/* 225 */       erasedType = ClassUtil.wrapperType(erasedType);
/*     */     }
/* 227 */     return (erasedType == Long.class || erasedType == Integer.class || erasedType == Short.class || erasedType == Byte.class);
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
/*     */   
/*     */   @Deprecated
/*     */   protected EnumResolver(Class<Enum<?>> enumClass, Enum<?>[] enums, HashMap<String, Enum<?>> map, Enum<?> defaultValue) {
/* 246 */     this(enumClass, enums, map, defaultValue, false, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static EnumResolver constructFor(Class<Enum<?>> enumCls, AnnotationIntrospector ai) {
/* 254 */     return _constructFor(enumCls, ai, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static EnumResolver constructUnsafe(Class<?> rawEnumCls, AnnotationIntrospector ai) {
/* 262 */     return _constructFor(rawEnumCls, ai, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static EnumResolver constructUsingToString(Class<Enum<?>> enumCls, AnnotationIntrospector ai) {
/* 271 */     return _constructUsingToString(enumCls, ai, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static EnumResolver constructUnsafeUsingToString(Class<?> rawEnumCls, AnnotationIntrospector ai) {
/* 281 */     return _constructUsingToString(rawEnumCls, ai, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static EnumResolver constructUsingToString(Class<Enum<?>> enumCls) {
/* 289 */     return _constructUsingToString(enumCls, null, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static EnumResolver constructUsingMethod(Class<Enum<?>> enumCls, AnnotatedMember accessor, AnnotationIntrospector ai) {
/* 298 */     return _constructUsingMethod(enumCls, accessor, ai, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static EnumResolver constructUnsafeUsingMethod(Class<?> rawEnumCls, AnnotatedMember accessor, AnnotationIntrospector ai) {
/* 308 */     return _constructUsingMethod(rawEnumCls, accessor, ai, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enum<?> findEnum(String key) {
/* 318 */     Enum<?> en = this._enumsById.get(key);
/* 319 */     if (en == null && 
/* 320 */       this._isIgnoreCase) {
/* 321 */       return _findEnumCaseInsensitive(key);
/*     */     }
/*     */     
/* 324 */     return en;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Enum<?> _findEnumCaseInsensitive(String key) {
/* 329 */     for (Map.Entry<String, Enum<?>> entry : this._enumsById.entrySet()) {
/* 330 */       if (key.equalsIgnoreCase(entry.getKey())) {
/* 331 */         return entry.getValue();
/*     */       }
/*     */     } 
/* 334 */     return null;
/*     */   }
/*     */   
/*     */   public Enum<?> getEnum(int index) {
/* 338 */     if (index < 0 || index >= this._enums.length) {
/* 339 */       return null;
/*     */     }
/* 341 */     return this._enums[index];
/*     */   }
/*     */   
/*     */   public Enum<?> getDefaultValue() {
/* 345 */     return this._defaultValue;
/*     */   }
/*     */   
/*     */   public Enum<?>[] getRawEnums() {
/* 349 */     return this._enums;
/*     */   }
/*     */   
/*     */   public List<Enum<?>> getEnums() {
/* 353 */     ArrayList<Enum<?>> enums = new ArrayList<>(this._enums.length);
/* 354 */     for (Enum<?> e : this._enums) {
/* 355 */       enums.add(e);
/*     */     }
/* 357 */     return enums;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<String> getEnumIds() {
/* 364 */     return this._enumsById.keySet();
/*     */   }
/*     */   public Class<Enum<?>> getEnumClass() {
/* 367 */     return this._enumClass;
/*     */   } public int lastValidIndex() {
/* 369 */     return this._enums.length - 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFromIntValue() {
/* 380 */     return this._isFromIntValue;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/util/EnumResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */