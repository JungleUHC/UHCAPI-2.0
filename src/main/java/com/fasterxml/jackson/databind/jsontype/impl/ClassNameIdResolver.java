/*     */ package com.fasterxml.jackson.databind.jsontype.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonTypeInfo;
/*     */ import com.fasterxml.jackson.databind.DatabindContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
/*     */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.IOException;
/*     */ import java.util.EnumMap;
/*     */ import java.util.EnumSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClassNameIdResolver
/*     */   extends TypeIdResolverBase
/*     */ {
/*     */   private static final String JAVA_UTIL_PKG = "java.util.";
/*     */   protected final PolymorphicTypeValidator _subTypeValidator;
/*     */   
/*     */   @Deprecated
/*     */   protected ClassNameIdResolver(JavaType baseType, TypeFactory typeFactory) {
/*  30 */     this(baseType, typeFactory, (PolymorphicTypeValidator)LaissezFaireSubTypeValidator.instance);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassNameIdResolver(JavaType baseType, TypeFactory typeFactory, PolymorphicTypeValidator ptv) {
/*  38 */     super(baseType, typeFactory);
/*  39 */     this._subTypeValidator = ptv;
/*     */   }
/*     */ 
/*     */   
/*     */   public static ClassNameIdResolver construct(JavaType baseType, MapperConfig<?> config, PolymorphicTypeValidator ptv) {
/*  44 */     return new ClassNameIdResolver(baseType, config.getTypeFactory(), ptv);
/*     */   }
/*     */   
/*     */   public JsonTypeInfo.Id getMechanism() {
/*  48 */     return JsonTypeInfo.Id.CLASS;
/*     */   }
/*     */ 
/*     */   
/*     */   public void registerSubtype(Class<?> type, String name) {}
/*     */ 
/*     */   
/*     */   public String idFromValue(Object value) {
/*  56 */     return _idFrom(value, value.getClass(), this._typeFactory);
/*     */   }
/*     */ 
/*     */   
/*     */   public String idFromValueAndType(Object value, Class<?> type) {
/*  61 */     return _idFrom(value, type, this._typeFactory);
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType typeFromId(DatabindContext context, String id) throws IOException {
/*  66 */     return _typeFromId(id, context);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected JavaType _typeFromId(String id, DatabindContext ctxt) throws IOException {
/*  72 */     JavaType t = ctxt.resolveAndValidateSubType(this._baseType, id, this._subTypeValidator);
/*  73 */     if (t == null && 
/*  74 */       ctxt instanceof DeserializationContext)
/*     */     {
/*  76 */       return ((DeserializationContext)ctxt).handleUnknownTypeId(this._baseType, id, this, "no such class found");
/*     */     }
/*     */ 
/*     */     
/*  80 */     return t;
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
/*     */   protected String _idFrom(Object value, Class<?> cls, TypeFactory typeFactory) {
/*  92 */     if (ClassUtil.isEnumType(cls))
/*     */     {
/*     */ 
/*     */ 
/*     */       
/*  97 */       if (!cls.isEnum())
/*     */       {
/*  99 */         cls = cls.getSuperclass();
/*     */       }
/*     */     }
/* 102 */     String str = cls.getName();
/* 103 */     if (str.startsWith("java.util.")) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 110 */       if (value instanceof EnumSet) {
/* 111 */         Class<?> enumClass = ClassUtil.findEnumType((EnumSet)value);
/*     */         
/* 113 */         str = typeFactory.constructCollectionType(EnumSet.class, enumClass).toCanonical();
/* 114 */       } else if (value instanceof EnumMap) {
/* 115 */         Class<?> enumClass = ClassUtil.findEnumType((EnumMap)value);
/* 116 */         Class<?> valueClass = Object.class;
/*     */         
/* 118 */         str = typeFactory.constructMapType(EnumMap.class, enumClass, valueClass).toCanonical();
/*     */       }
/*     */     
/*     */     }
/* 122 */     else if (str.indexOf('$') >= 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 130 */       Class<?> outer = ClassUtil.getOuterClass(cls);
/* 131 */       if (outer != null) {
/*     */ 
/*     */         
/* 134 */         Class<?> staticType = this._baseType.getRawClass();
/* 135 */         if (ClassUtil.getOuterClass(staticType) == null) {
/*     */           
/* 137 */           cls = this._baseType.getRawClass();
/* 138 */           str = cls.getName();
/*     */         } 
/*     */       } 
/*     */     } 
/* 142 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDescForKnownTypeIds() {
/* 147 */     return "class name used as type id";
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jsontype/impl/ClassNameIdResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */