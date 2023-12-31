/*     */ package com.fasterxml.jackson.databind.jsontype.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonTypeInfo;
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.DatabindContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.jsontype.NamedType;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.TreeSet;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public class TypeNameIdResolver
/*     */   extends TypeIdResolverBase
/*     */ {
/*     */   protected final MapperConfig<?> _config;
/*     */   protected final ConcurrentHashMap<String, String> _typeToId;
/*     */   protected final Map<String, JavaType> _idToType;
/*     */   protected final boolean _caseInsensitive;
/*     */   
/*     */   protected TypeNameIdResolver(MapperConfig<?> config, JavaType baseType, ConcurrentHashMap<String, String> typeToId, HashMap<String, JavaType> idToType) {
/*  42 */     super(baseType, config.getTypeFactory());
/*  43 */     this._config = config;
/*  44 */     this._typeToId = typeToId;
/*  45 */     this._idToType = idToType;
/*  46 */     this._caseInsensitive = config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES);
/*     */   }
/*     */ 
/*     */   
/*     */   public static TypeNameIdResolver construct(MapperConfig<?> config, JavaType baseType, Collection<NamedType> subtypes, boolean forSer, boolean forDeser) {
/*     */     ConcurrentHashMap<String, String> typeToId;
/*     */     HashMap<String, JavaType> idToType;
/*  53 */     if (forSer == forDeser) throw new IllegalArgumentException();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  58 */     if (forSer) {
/*     */ 
/*     */       
/*  61 */       typeToId = new ConcurrentHashMap<>();
/*  62 */       idToType = null;
/*     */     } else {
/*  64 */       idToType = new HashMap<>();
/*     */ 
/*     */ 
/*     */       
/*  68 */       typeToId = new ConcurrentHashMap<>(4);
/*     */     } 
/*  70 */     boolean caseInsensitive = config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES);
/*     */     
/*  72 */     if (subtypes != null) {
/*  73 */       for (NamedType t : subtypes) {
/*     */ 
/*     */         
/*  76 */         Class<?> cls = t.getType();
/*  77 */         String id = t.hasName() ? t.getName() : _defaultTypeId(cls);
/*  78 */         if (forSer) {
/*  79 */           typeToId.put(cls.getName(), id);
/*     */         }
/*  81 */         if (forDeser) {
/*     */           
/*  83 */           if (caseInsensitive) {
/*  84 */             id = id.toLowerCase();
/*     */           }
/*     */ 
/*     */           
/*  88 */           JavaType prev = idToType.get(id);
/*  89 */           if (prev != null && 
/*  90 */             cls.isAssignableFrom(prev.getRawClass())) {
/*     */             continue;
/*     */           }
/*     */           
/*  94 */           idToType.put(id, config.constructType(cls));
/*     */         } 
/*     */       } 
/*     */     }
/*  98 */     return new TypeNameIdResolver(config, baseType, typeToId, idToType);
/*     */   }
/*     */   
/*     */   public JsonTypeInfo.Id getMechanism() {
/* 102 */     return JsonTypeInfo.Id.NAME;
/*     */   }
/*     */   
/*     */   public String idFromValue(Object value) {
/* 106 */     return idFromClass(value.getClass());
/*     */   }
/*     */ 
/*     */   
/*     */   protected String idFromClass(Class<?> clazz) {
/* 111 */     if (clazz == null) {
/* 112 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 116 */     String key = clazz.getName();
/* 117 */     String name = this._typeToId.get(key);
/*     */     
/* 119 */     if (name == null) {
/*     */ 
/*     */       
/* 122 */       Class<?> cls = this._typeFactory.constructType(clazz).getRawClass();
/*     */ 
/*     */       
/* 125 */       if (this._config.isAnnotationProcessingEnabled()) {
/* 126 */         BeanDescription beanDesc = this._config.introspectClassAnnotations(cls);
/* 127 */         name = this._config.getAnnotationIntrospector().findTypeName(beanDesc.getClassInfo());
/*     */       } 
/* 129 */       if (name == null)
/*     */       {
/* 131 */         name = _defaultTypeId(cls);
/*     */       }
/* 133 */       this._typeToId.put(key, name);
/*     */     } 
/* 135 */     return name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String idFromValueAndType(Object value, Class<?> type) {
/* 142 */     if (value == null) {
/* 143 */       return idFromClass(type);
/*     */     }
/* 145 */     return idFromValue(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType typeFromId(DatabindContext context, String id) {
/* 150 */     return _typeFromId(id);
/*     */   }
/*     */ 
/*     */   
/*     */   protected JavaType _typeFromId(String id) {
/* 155 */     if (this._caseInsensitive) {
/* 156 */       id = id.toLowerCase();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 162 */     return this._idToType.get(id);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDescForKnownTypeIds() {
/* 169 */     TreeSet<String> ids = new TreeSet<>();
/* 170 */     for (Map.Entry<String, JavaType> entry : this._idToType.entrySet()) {
/* 171 */       if (((JavaType)entry.getValue()).isConcrete()) {
/* 172 */         ids.add(entry.getKey());
/*     */       }
/*     */     } 
/* 175 */     return ids.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 180 */     return String.format("[%s; id-to-type=%s]", new Object[] { getClass().getName(), this._idToType });
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
/*     */   protected static String _defaultTypeId(Class<?> cls) {
/* 195 */     String n = cls.getName();
/* 196 */     int ix = n.lastIndexOf('.');
/* 197 */     return (ix < 0) ? n : n.substring(ix + 1);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jsontype/impl/TypeNameIdResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */