/*     */ package com.fasterxml.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
/*     */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*     */ import com.fasterxml.jackson.databind.util.Converter;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public abstract class JavaUtilCollectionsDeserializers
/*     */ {
/*     */   private static final int TYPE_SINGLETON_SET = 1;
/*     */   private static final int TYPE_SINGLETON_LIST = 2;
/*     */   private static final int TYPE_SINGLETON_MAP = 3;
/*     */   private static final int TYPE_UNMODIFIABLE_SET = 4;
/*     */   private static final int TYPE_UNMODIFIABLE_LIST = 5;
/*     */   private static final int TYPE_UNMODIFIABLE_MAP = 6;
/*     */   private static final int TYPE_SYNC_SET = 7;
/*     */   private static final int TYPE_SYNC_COLLECTION = 8;
/*     */   private static final int TYPE_SYNC_LIST = 9;
/*     */   private static final int TYPE_SYNC_MAP = 10;
/*     */   public static final int TYPE_AS_LIST = 11;
/*     */   private static final String PREFIX_JAVA_UTIL_COLLECTIONS = "java.util.Collections$";
/*     */   private static final String PREFIX_JAVA_UTIL_ARRAYS = "java.util.Arrays$";
/*     */   private static final String PREFIX_JAVA_UTIL_IMMUTABLE_COLL = "java.util.ImmutableCollections$";
/*     */   
/*     */   public static JsonDeserializer<?> findForCollection(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/*  45 */     String clsName = type.getRawClass().getName();
/*  46 */     if (!clsName.startsWith("java.util.")) {
/*  47 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  52 */     String localName = _findUtilCollectionsTypeName(clsName);
/*  53 */     if (localName != null) {
/*  54 */       JavaUtilCollectionsConverter conv = null;
/*     */       
/*     */       String name;
/*  57 */       if ((name = _findUnmodifiableTypeName(localName)) != null) {
/*  58 */         if (name.endsWith("Set")) {
/*  59 */           conv = converter(4, type, Set.class);
/*  60 */         } else if (name.endsWith("List")) {
/*  61 */           conv = converter(5, type, List.class);
/*     */         } 
/*  63 */       } else if ((name = _findSingletonTypeName(localName)) != null) {
/*  64 */         if (name.endsWith("Set")) {
/*  65 */           conv = converter(1, type, Set.class);
/*  66 */         } else if (name.endsWith("List")) {
/*  67 */           conv = converter(2, type, List.class);
/*     */         } 
/*  69 */       } else if ((name = _findSyncTypeName(localName)) != null) {
/*     */         
/*  71 */         if (name.endsWith("Set")) {
/*  72 */           conv = converter(7, type, Set.class);
/*  73 */         } else if (name.endsWith("List")) {
/*  74 */           conv = converter(9, type, List.class);
/*  75 */         } else if (name.endsWith("Collection")) {
/*  76 */           conv = converter(8, type, Collection.class);
/*     */         } 
/*     */       } 
/*     */       
/*  80 */       return (conv == null) ? null : (JsonDeserializer<?>)new StdDelegatingDeserializer(conv);
/*     */     } 
/*  82 */     if ((localName = _findUtilArrayTypeName(clsName)) != null) {
/*     */       
/*  84 */       if (localName.contains("List")) {
/*  85 */         return (JsonDeserializer<?>)new StdDelegatingDeserializer(
/*  86 */             converter(5, type, List.class));
/*     */       }
/*  88 */       return null;
/*     */     } 
/*     */     
/*  91 */     if ((localName = _findUtilCollectionsImmutableTypeName(clsName)) != null) {
/*     */       
/*  93 */       if (localName.contains("List")) {
/*  94 */         return (JsonDeserializer<?>)new StdDelegatingDeserializer(
/*  95 */             converter(11, type, List.class));
/*     */       }
/*  97 */       if (localName.contains("Set")) {
/*  98 */         return (JsonDeserializer<?>)new StdDelegatingDeserializer(
/*  99 */             converter(4, type, Set.class));
/*     */       }
/* 101 */       return null;
/*     */     } 
/*     */     
/* 104 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonDeserializer<?> findForMap(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/* 111 */     String clsName = type.getRawClass().getName();
/*     */     
/* 113 */     JavaUtilCollectionsConverter conv = null;
/*     */     String localName;
/* 115 */     if ((localName = _findUtilCollectionsTypeName(clsName)) != null) {
/*     */       String name;
/*     */       
/* 118 */       if ((name = _findUnmodifiableTypeName(localName)) != null) {
/* 119 */         if (name.contains("Map")) {
/* 120 */           conv = converter(6, type, Map.class);
/*     */         }
/* 122 */       } else if ((name = _findSingletonTypeName(localName)) != null) {
/* 123 */         if (name.contains("Map")) {
/* 124 */           conv = converter(3, type, Map.class);
/*     */         }
/* 126 */       } else if ((name = _findSyncTypeName(localName)) != null) {
/*     */         
/* 128 */         if (name.contains("Map")) {
/* 129 */           conv = converter(10, type, Map.class);
/*     */         }
/*     */       } 
/* 132 */     } else if ((localName = _findUtilCollectionsImmutableTypeName(clsName)) != null && 
/* 133 */       localName.contains("Map")) {
/* 134 */       conv = converter(6, type, Map.class);
/*     */     } 
/*     */     
/* 137 */     return (conv == null) ? null : (JsonDeserializer<?>)new StdDelegatingDeserializer(conv);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static JavaUtilCollectionsConverter converter(int kind, JavaType concreteType, Class<?> rawSuper) {
/* 143 */     return new JavaUtilCollectionsConverter(kind, concreteType.findSuperType(rawSuper));
/*     */   }
/*     */   
/*     */   private static String _findUtilArrayTypeName(String clsName) {
/* 147 */     if (clsName.startsWith("java.util.Arrays$")) {
/* 148 */       return clsName.substring("java.util.Arrays$".length());
/*     */     }
/* 150 */     return null;
/*     */   }
/*     */   
/*     */   private static String _findUtilCollectionsTypeName(String clsName) {
/* 154 */     if (clsName.startsWith("java.util.Collections$")) {
/* 155 */       return clsName.substring("java.util.Collections$".length());
/*     */     }
/* 157 */     return null;
/*     */   }
/*     */   
/*     */   private static String _findUtilCollectionsImmutableTypeName(String clsName) {
/* 161 */     if (clsName.startsWith("java.util.ImmutableCollections$")) {
/* 162 */       return clsName.substring("java.util.ImmutableCollections$".length());
/*     */     }
/* 164 */     return null;
/*     */   }
/*     */   
/*     */   private static String _findSingletonTypeName(String localName) {
/* 168 */     return localName.startsWith("Singleton") ? localName.substring(9) : null;
/*     */   }
/*     */   
/*     */   private static String _findSyncTypeName(String localName) {
/* 172 */     return localName.startsWith("Synchronized") ? localName.substring(12) : null;
/*     */   }
/*     */   
/*     */   private static String _findUnmodifiableTypeName(String localName) {
/* 176 */     return localName.startsWith("Unmodifiable") ? localName.substring(12) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class JavaUtilCollectionsConverter
/*     */     implements Converter<Object, Object>
/*     */   {
/*     */     private final JavaType _inputType;
/*     */ 
/*     */     
/*     */     private final int _kind;
/*     */ 
/*     */     
/*     */     JavaUtilCollectionsConverter(int kind, JavaType inputType) {
/* 191 */       this._inputType = inputType;
/* 192 */       this._kind = kind; } public Object convert(Object value) {
/*     */       Set<?> set;
/*     */       List<?> list;
/*     */       Map<?, ?> map;
/*     */       Map.Entry<?, ?> entry;
/* 197 */       if (value == null) {
/* 198 */         return null;
/*     */       }
/*     */       
/* 201 */       switch (this._kind) {
/*     */         
/*     */         case 1:
/* 204 */           set = (Set)value;
/* 205 */           _checkSingleton(set.size());
/* 206 */           return Collections.singleton(set.iterator().next());
/*     */ 
/*     */         
/*     */         case 2:
/* 210 */           list = (List)value;
/* 211 */           _checkSingleton(list.size());
/* 212 */           return Collections.singletonList(list.get(0));
/*     */ 
/*     */         
/*     */         case 3:
/* 216 */           map = (Map<?, ?>)value;
/* 217 */           _checkSingleton(map.size());
/* 218 */           entry = map.entrySet().iterator().next();
/* 219 */           return Collections.singletonMap(entry.getKey(), entry.getValue());
/*     */ 
/*     */         
/*     */         case 4:
/* 223 */           return Collections.unmodifiableSet((Set)value);
/*     */         case 5:
/* 225 */           return Collections.unmodifiableList((List)value);
/*     */         case 6:
/* 227 */           return Collections.unmodifiableMap((Map<?, ?>)value);
/*     */         
/*     */         case 7:
/* 230 */           return Collections.synchronizedSet((Set)value);
/*     */         case 9:
/* 232 */           return Collections.synchronizedList((List)value);
/*     */         case 8:
/* 234 */           return Collections.synchronizedCollection((Collection)value);
/*     */         case 10:
/* 236 */           return Collections.synchronizedMap((Map<?, ?>)value);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 241 */       return value;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public JavaType getInputType(TypeFactory typeFactory) {
/* 247 */       return this._inputType;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public JavaType getOutputType(TypeFactory typeFactory) {
/* 253 */       return this._inputType;
/*     */     }
/*     */     
/*     */     private void _checkSingleton(int size) {
/* 257 */       if (size != 1)
/*     */       {
/* 259 */         throw new IllegalArgumentException("Can not deserialize Singleton container from " + size + " entries");
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/impl/JavaUtilCollectionsDeserializers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */