/*     */ package com.fasterxml.jackson.databind.jsontype.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.jsontype.NamedType;
/*     */ import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class StdSubtypeResolver
/*     */   extends SubtypeResolver implements Serializable {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   protected StdSubtypeResolver(StdSubtypeResolver src) {
/*  28 */     LinkedHashSet<NamedType> reg = src._registeredSubtypes;
/*  29 */     this._registeredSubtypes = (reg == null) ? null : new LinkedHashSet<>(reg);
/*     */   }
/*     */ 
/*     */   
/*     */   protected LinkedHashSet<NamedType> _registeredSubtypes;
/*     */   
/*     */   public SubtypeResolver copy() {
/*  36 */     return new StdSubtypeResolver(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StdSubtypeResolver() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerSubtypes(NamedType... types) {
/*  47 */     if (this._registeredSubtypes == null) {
/*  48 */       this._registeredSubtypes = new LinkedHashSet<>();
/*     */     }
/*  50 */     for (NamedType type : types) {
/*  51 */       this._registeredSubtypes.add(type);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void registerSubtypes(Class<?>... classes) {
/*  57 */     NamedType[] types = new NamedType[classes.length];
/*  58 */     for (int i = 0, len = classes.length; i < len; i++) {
/*  59 */       types[i] = new NamedType(classes[i]);
/*     */     }
/*  61 */     registerSubtypes(types);
/*     */   }
/*     */ 
/*     */   
/*     */   public void registerSubtypes(Collection<Class<?>> subtypes) {
/*  66 */     int len = subtypes.size();
/*  67 */     NamedType[] types = new NamedType[len];
/*  68 */     int i = 0;
/*  69 */     for (Class<?> subtype : subtypes) {
/*  70 */       types[i++] = new NamedType(subtype);
/*     */     }
/*  72 */     registerSubtypes(types);
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
/*     */   public Collection<NamedType> collectAndResolveSubtypesByClass(MapperConfig<?> config, AnnotatedMember property, JavaType baseType) {
/*     */     Class<?> rawBase;
/*  85 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*     */ 
/*     */ 
/*     */     
/*  89 */     if (baseType != null) {
/*  90 */       rawBase = baseType.getRawClass();
/*  91 */     } else if (property != null) {
/*  92 */       rawBase = property.getRawType();
/*     */     } else {
/*  94 */       throw new IllegalArgumentException("Both property and base type are nulls");
/*     */     } 
/*     */     
/*  97 */     HashMap<NamedType, NamedType> collected = new HashMap<>();
/*     */     
/*  99 */     if (this._registeredSubtypes != null) {
/* 100 */       for (NamedType subtype : this._registeredSubtypes) {
/*     */         
/* 102 */         if (rawBase.isAssignableFrom(subtype.getType())) {
/* 103 */           AnnotatedClass curr = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype
/* 104 */               .getType());
/* 105 */           _collectAndResolve(curr, subtype, config, ai, collected);
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 111 */     if (property != null) {
/* 112 */       Collection<NamedType> st = ai.findSubtypes((Annotated)property);
/* 113 */       if (st != null) {
/* 114 */         for (NamedType nt : st) {
/* 115 */           AnnotatedClass annotatedClass = AnnotatedClassResolver.resolveWithoutSuperTypes(config, nt
/* 116 */               .getType());
/* 117 */           _collectAndResolve(annotatedClass, nt, config, ai, collected);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 122 */     NamedType rootType = new NamedType(rawBase, null);
/* 123 */     AnnotatedClass ac = AnnotatedClassResolver.resolveWithoutSuperTypes(config, rawBase);
/*     */ 
/*     */     
/* 126 */     _collectAndResolve(ac, rootType, config, ai, collected);
/*     */     
/* 128 */     return new ArrayList<>(collected.values());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<NamedType> collectAndResolveSubtypesByClass(MapperConfig<?> config, AnnotatedClass type) {
/* 135 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/* 136 */     HashMap<NamedType, NamedType> subtypes = new HashMap<>();
/*     */ 
/*     */     
/* 139 */     if (this._registeredSubtypes != null) {
/* 140 */       Class<?> rawBase = type.getRawType();
/* 141 */       for (NamedType subtype : this._registeredSubtypes) {
/*     */         
/* 143 */         if (rawBase.isAssignableFrom(subtype.getType())) {
/* 144 */           AnnotatedClass curr = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype
/* 145 */               .getType());
/* 146 */           _collectAndResolve(curr, subtype, config, ai, subtypes);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 151 */     NamedType rootType = new NamedType(type.getRawType(), null);
/* 152 */     _collectAndResolve(type, rootType, config, ai, subtypes);
/* 153 */     return new ArrayList<>(subtypes.values());
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
/*     */   public Collection<NamedType> collectAndResolveSubtypesByTypeId(MapperConfig<?> config, AnnotatedMember property, JavaType baseType) {
/* 166 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/* 167 */     Class<?> rawBase = baseType.getRawClass();
/*     */ 
/*     */     
/* 170 */     Set<Class<?>> typesHandled = new HashSet<>();
/* 171 */     Map<String, NamedType> byName = new LinkedHashMap<>();
/*     */ 
/*     */     
/* 174 */     NamedType rootType = new NamedType(rawBase, null);
/* 175 */     AnnotatedClass ac = AnnotatedClassResolver.resolveWithoutSuperTypes(config, rawBase);
/*     */     
/* 177 */     _collectAndResolveByTypeId(ac, rootType, config, typesHandled, byName);
/*     */ 
/*     */     
/* 180 */     if (property != null) {
/* 181 */       Collection<NamedType> st = ai.findSubtypes((Annotated)property);
/* 182 */       if (st != null) {
/* 183 */         for (NamedType nt : st) {
/* 184 */           ac = AnnotatedClassResolver.resolveWithoutSuperTypes(config, nt.getType());
/* 185 */           _collectAndResolveByTypeId(ac, nt, config, typesHandled, byName);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 190 */     if (this._registeredSubtypes != null) {
/* 191 */       for (NamedType subtype : this._registeredSubtypes) {
/*     */         
/* 193 */         if (rawBase.isAssignableFrom(subtype.getType())) {
/* 194 */           AnnotatedClass curr = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype
/* 195 */               .getType());
/* 196 */           _collectAndResolveByTypeId(curr, subtype, config, typesHandled, byName);
/*     */         } 
/*     */       } 
/*     */     }
/* 200 */     return _combineNamedAndUnnamed(rawBase, typesHandled, byName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<NamedType> collectAndResolveSubtypesByTypeId(MapperConfig<?> config, AnnotatedClass baseType) {
/* 207 */     Class<?> rawBase = baseType.getRawType();
/* 208 */     Set<Class<?>> typesHandled = new HashSet<>();
/* 209 */     Map<String, NamedType> byName = new LinkedHashMap<>();
/*     */     
/* 211 */     NamedType rootType = new NamedType(rawBase, null);
/* 212 */     _collectAndResolveByTypeId(baseType, rootType, config, typesHandled, byName);
/*     */     
/* 214 */     if (this._registeredSubtypes != null) {
/* 215 */       for (NamedType subtype : this._registeredSubtypes) {
/*     */         
/* 217 */         if (rawBase.isAssignableFrom(subtype.getType())) {
/* 218 */           AnnotatedClass curr = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype
/* 219 */               .getType());
/* 220 */           _collectAndResolveByTypeId(curr, subtype, config, typesHandled, byName);
/*     */         } 
/*     */       } 
/*     */     }
/* 224 */     return _combineNamedAndUnnamed(rawBase, typesHandled, byName);
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
/*     */   protected void _collectAndResolve(AnnotatedClass annotatedType, NamedType namedType, MapperConfig<?> config, AnnotationIntrospector ai, HashMap<NamedType, NamedType> collectedSubtypes) {
/* 241 */     if (!namedType.hasName()) {
/* 242 */       String name = ai.findTypeName(annotatedType);
/* 243 */       if (name != null) {
/* 244 */         namedType = new NamedType(namedType.getType(), name);
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 250 */     NamedType typeOnlyNamedType = new NamedType(namedType.getType());
/*     */ 
/*     */     
/* 253 */     if (collectedSubtypes.containsKey(typeOnlyNamedType)) {
/*     */       
/* 255 */       if (namedType.hasName()) {
/* 256 */         NamedType prev = collectedSubtypes.get(typeOnlyNamedType);
/* 257 */         if (!prev.hasName()) {
/* 258 */           collectedSubtypes.put(typeOnlyNamedType, namedType);
/*     */         }
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/* 264 */     collectedSubtypes.put(typeOnlyNamedType, namedType);
/* 265 */     Collection<NamedType> st = ai.findSubtypes((Annotated)annotatedType);
/* 266 */     if (st != null && !st.isEmpty()) {
/* 267 */       for (NamedType subtype : st) {
/* 268 */         AnnotatedClass subtypeClass = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype
/* 269 */             .getType());
/* 270 */         _collectAndResolve(subtypeClass, subtype, config, ai, collectedSubtypes);
/*     */       } 
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
/*     */   protected void _collectAndResolveByTypeId(AnnotatedClass annotatedType, NamedType namedType, MapperConfig<?> config, Set<Class<?>> typesHandled, Map<String, NamedType> byName) {
/* 283 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/* 284 */     if (!namedType.hasName()) {
/* 285 */       String name = ai.findTypeName(annotatedType);
/* 286 */       if (name != null) {
/* 287 */         namedType = new NamedType(namedType.getType(), name);
/*     */       }
/*     */     } 
/* 290 */     if (namedType.hasName()) {
/* 291 */       byName.put(namedType.getName(), namedType);
/*     */     }
/*     */ 
/*     */     
/* 295 */     if (typesHandled.add(namedType.getType())) {
/* 296 */       Collection<NamedType> st = ai.findSubtypes((Annotated)annotatedType);
/* 297 */       if (st != null && !st.isEmpty()) {
/* 298 */         for (NamedType subtype : st) {
/* 299 */           AnnotatedClass subtypeClass = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype
/* 300 */               .getType());
/* 301 */           _collectAndResolveByTypeId(subtypeClass, subtype, config, typesHandled, byName);
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Collection<NamedType> _combineNamedAndUnnamed(Class<?> rawBase, Set<Class<?>> typesHandled, Map<String, NamedType> byName) {
/* 314 */     ArrayList<NamedType> result = new ArrayList<>(byName.values());
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 319 */     for (NamedType t : byName.values()) {
/* 320 */       typesHandled.remove(t.getType());
/*     */     }
/* 322 */     for (Class<?> cls : typesHandled) {
/*     */ 
/*     */       
/* 325 */       if (cls == rawBase && Modifier.isAbstract(cls.getModifiers())) {
/*     */         continue;
/*     */       }
/* 328 */       result.add(new NamedType(cls));
/*     */     } 
/* 330 */     return result;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jsontype/impl/StdSubtypeResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */