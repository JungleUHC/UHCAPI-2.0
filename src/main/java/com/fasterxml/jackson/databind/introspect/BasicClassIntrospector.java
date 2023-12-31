/*     */ package com.fasterxml.jackson.databind.introspect;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.SerializationConfig;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.type.SimpleType;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class BasicClassIntrospector extends ClassIntrospector implements Serializable {
/*     */   private static final long serialVersionUID = 2L;
/*  17 */   private static final Class<?> CLS_OBJECT = Object.class;
/*  18 */   private static final Class<?> CLS_STRING = String.class;
/*  19 */   private static final Class<?> CLS_JSON_NODE = JsonNode.class;
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
/*  31 */   protected static final BasicBeanDescription STRING_DESC = BasicBeanDescription.forOtherUse(null, (JavaType)SimpleType.constructUnsafe(String.class), 
/*  32 */       AnnotatedClassResolver.createPrimordial(CLS_STRING));
/*     */ 
/*     */ 
/*     */   
/*  36 */   protected static final BasicBeanDescription BOOLEAN_DESC = BasicBeanDescription.forOtherUse(null, (JavaType)SimpleType.constructUnsafe(boolean.class), 
/*  37 */       AnnotatedClassResolver.createPrimordial(boolean.class));
/*     */ 
/*     */ 
/*     */   
/*  41 */   protected static final BasicBeanDescription INT_DESC = BasicBeanDescription.forOtherUse(null, (JavaType)SimpleType.constructUnsafe(int.class), 
/*  42 */       AnnotatedClassResolver.createPrimordial(int.class));
/*     */ 
/*     */ 
/*     */   
/*  46 */   protected static final BasicBeanDescription LONG_DESC = BasicBeanDescription.forOtherUse(null, (JavaType)SimpleType.constructUnsafe(long.class), 
/*  47 */       AnnotatedClassResolver.createPrimordial(long.class));
/*     */ 
/*     */ 
/*     */   
/*  51 */   protected static final BasicBeanDescription OBJECT_DESC = BasicBeanDescription.forOtherUse(null, (JavaType)SimpleType.constructUnsafe(Object.class), 
/*  52 */       AnnotatedClassResolver.createPrimordial(CLS_OBJECT));
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
/*     */   public ClassIntrospector copy() {
/*  66 */     return new BasicClassIntrospector();
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
/*     */   public BasicBeanDescription forSerialization(SerializationConfig config, JavaType type, ClassIntrospector.MixInResolver r) {
/*  80 */     BasicBeanDescription desc = _findStdTypeDesc((MapperConfig<?>)config, type);
/*  81 */     if (desc == null) {
/*     */ 
/*     */       
/*  84 */       desc = _findStdJdkCollectionDesc((MapperConfig<?>)config, type);
/*  85 */       if (desc == null) {
/*  86 */         desc = BasicBeanDescription.forSerialization(collectProperties((MapperConfig<?>)config, type, r, true));
/*     */       }
/*     */     } 
/*     */     
/*  90 */     return desc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicBeanDescription forDeserialization(DeserializationConfig config, JavaType type, ClassIntrospector.MixInResolver r) {
/*  98 */     BasicBeanDescription desc = _findStdTypeDesc((MapperConfig<?>)config, type);
/*  99 */     if (desc == null) {
/*     */ 
/*     */       
/* 102 */       desc = _findStdJdkCollectionDesc((MapperConfig<?>)config, type);
/* 103 */       if (desc == null) {
/* 104 */         desc = BasicBeanDescription.forDeserialization(collectProperties((MapperConfig<?>)config, type, r, false));
/*     */       }
/*     */     } 
/*     */     
/* 108 */     return desc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicBeanDescription forDeserializationWithBuilder(DeserializationConfig config, JavaType builderType, ClassIntrospector.MixInResolver r, BeanDescription valueTypeDesc) {
/* 116 */     return BasicBeanDescription.forDeserialization(collectPropertiesWithBuilder((MapperConfig<?>)config, builderType, r, valueTypeDesc, false));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public BasicBeanDescription forDeserializationWithBuilder(DeserializationConfig config, JavaType type, ClassIntrospector.MixInResolver r) {
/* 126 */     return BasicBeanDescription.forDeserialization(collectPropertiesWithBuilder((MapperConfig<?>)config, type, r, null, false));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicBeanDescription forCreation(DeserializationConfig config, JavaType type, ClassIntrospector.MixInResolver r) {
/* 134 */     BasicBeanDescription desc = _findStdTypeDesc((MapperConfig<?>)config, type);
/* 135 */     if (desc == null) {
/*     */ 
/*     */       
/* 138 */       desc = _findStdJdkCollectionDesc((MapperConfig<?>)config, type);
/* 139 */       if (desc == null) {
/* 140 */         desc = BasicBeanDescription.forDeserialization(
/* 141 */             collectProperties((MapperConfig<?>)config, type, r, false));
/*     */       }
/*     */     } 
/* 144 */     return desc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicBeanDescription forClassAnnotations(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
/* 151 */     BasicBeanDescription desc = _findStdTypeDesc(config, type);
/* 152 */     if (desc == null) {
/* 153 */       desc = BasicBeanDescription.forOtherUse(config, type, 
/* 154 */           _resolveAnnotatedClass(config, type, r));
/*     */     }
/* 156 */     return desc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicBeanDescription forDirectClassAnnotations(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
/* 163 */     BasicBeanDescription desc = _findStdTypeDesc(config, type);
/* 164 */     if (desc == null) {
/* 165 */       desc = BasicBeanDescription.forOtherUse(config, type, 
/* 166 */           _resolveAnnotatedWithoutSuperTypes(config, type, r));
/*     */     }
/* 168 */     return desc;
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
/*     */   protected POJOPropertiesCollector collectProperties(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r, boolean forSerialization) {
/* 183 */     AnnotatedClass classDef = _resolveAnnotatedClass(config, type, r);
/*     */ 
/*     */     
/* 186 */     AccessorNamingStrategy accNaming = type.isRecordType() ? config.getAccessorNaming().forRecord(config, classDef) : config.getAccessorNaming().forPOJO(config, classDef);
/* 187 */     return constructPropertyCollector(config, classDef, type, forSerialization, accNaming);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected POJOPropertiesCollector collectProperties(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r, boolean forSerialization, String mutatorPrefix) {
/* 195 */     AnnotatedClass classDef = _resolveAnnotatedClass(config, type, r);
/* 196 */     AccessorNamingStrategy accNaming = (new DefaultAccessorNamingStrategy.Provider()).withSetterPrefix(mutatorPrefix).forPOJO(config, classDef);
/* 197 */     return constructPropertyCollector(config, classDef, type, forSerialization, accNaming);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected POJOPropertiesCollector collectPropertiesWithBuilder(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r, BeanDescription valueTypeDesc, boolean forSerialization) {
/* 207 */     AnnotatedClass builderClassDef = _resolveAnnotatedClass(config, type, r);
/*     */     
/* 209 */     AccessorNamingStrategy accNaming = config.getAccessorNaming().forBuilder(config, builderClassDef, valueTypeDesc);
/* 210 */     return constructPropertyCollector(config, builderClassDef, type, forSerialization, accNaming);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected POJOPropertiesCollector collectPropertiesWithBuilder(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r, boolean forSerialization) {
/* 217 */     return collectPropertiesWithBuilder(config, type, r, null, forSerialization);
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
/*     */   protected POJOPropertiesCollector constructPropertyCollector(MapperConfig<?> config, AnnotatedClass classDef, JavaType type, boolean forSerialization, AccessorNamingStrategy accNaming) {
/* 230 */     return new POJOPropertiesCollector(config, forSerialization, type, classDef, accNaming);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected POJOPropertiesCollector constructPropertyCollector(MapperConfig<?> config, AnnotatedClass ac, JavaType type, boolean forSerialization, String mutatorPrefix) {
/* 238 */     return new POJOPropertiesCollector(config, forSerialization, type, ac, mutatorPrefix);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BasicBeanDescription _findStdTypeDesc(MapperConfig<?> config, JavaType type) {
/* 247 */     Class<?> cls = type.getRawClass();
/* 248 */     if (cls.isPrimitive()) {
/* 249 */       if (cls == int.class) {
/* 250 */         return INT_DESC;
/*     */       }
/* 252 */       if (cls == long.class) {
/* 253 */         return LONG_DESC;
/*     */       }
/* 255 */       if (cls == boolean.class) {
/* 256 */         return BOOLEAN_DESC;
/*     */       }
/* 258 */     } else if (ClassUtil.isJDKClass(cls)) {
/* 259 */       if (cls == CLS_OBJECT) {
/* 260 */         return OBJECT_DESC;
/*     */       }
/* 262 */       if (cls == CLS_STRING) {
/* 263 */         return STRING_DESC;
/*     */       }
/* 265 */       if (cls == Integer.class) {
/* 266 */         return INT_DESC;
/*     */       }
/* 268 */       if (cls == Long.class) {
/* 269 */         return LONG_DESC;
/*     */       }
/* 271 */       if (cls == Boolean.class) {
/* 272 */         return BOOLEAN_DESC;
/*     */       }
/* 274 */     } else if (CLS_JSON_NODE.isAssignableFrom(cls)) {
/* 275 */       return BasicBeanDescription.forOtherUse(config, type, 
/* 276 */           AnnotatedClassResolver.createPrimordial(cls));
/*     */     } 
/* 278 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _isStdJDKCollection(JavaType type) {
/* 288 */     if (!type.isContainerType() || type.isArrayType()) {
/* 289 */       return false;
/*     */     }
/* 291 */     Class<?> raw = type.getRawClass();
/* 292 */     if (ClassUtil.isJDKClass(raw))
/*     */     {
/*     */       
/* 295 */       if (Collection.class.isAssignableFrom(raw) || Map.class
/* 296 */         .isAssignableFrom(raw)) {
/* 297 */         return true;
/*     */       }
/*     */     }
/* 300 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   protected BasicBeanDescription _findStdJdkCollectionDesc(MapperConfig<?> cfg, JavaType type) {
/* 305 */     if (_isStdJDKCollection(type)) {
/* 306 */       return BasicBeanDescription.forOtherUse(cfg, type, 
/* 307 */           _resolveAnnotatedClass(cfg, type, (ClassIntrospector.MixInResolver)cfg));
/*     */     }
/* 309 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AnnotatedClass _resolveAnnotatedClass(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
/* 317 */     return AnnotatedClassResolver.resolve(config, type, r);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AnnotatedClass _resolveAnnotatedWithoutSuperTypes(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
/* 325 */     return AnnotatedClassResolver.resolveWithoutSuperTypes(config, type, r);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/BasicClassIntrospector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */