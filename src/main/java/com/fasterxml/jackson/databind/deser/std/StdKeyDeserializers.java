/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonCreator;
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.KeyDeserializer;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.deser.KeyDeserializers;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedAndMetadata;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.EnumResolver;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.List;
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
/*     */ public class StdKeyDeserializers
/*     */   implements KeyDeserializers, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public static KeyDeserializer constructEnumKeyDeserializer(EnumResolver enumResolver) {
/*  39 */     return new StdKeyDeserializer.EnumKD(enumResolver, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public static KeyDeserializer constructEnumKeyDeserializer(EnumResolver enumResolver, AnnotatedMethod factory) {
/*  44 */     return new StdKeyDeserializer.EnumKD(enumResolver, factory);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static KeyDeserializer constructDelegatingKeyDeserializer(DeserializationConfig config, JavaType type, JsonDeserializer<?> deser) {
/*  50 */     return new StdKeyDeserializer.DelegatingKD(type.getRawClass(), deser);
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
/*     */   public static KeyDeserializer findStringBasedKeyDeserializer(DeserializationConfig config, JavaType type) throws JsonMappingException {
/*  68 */     BeanDescription beanDesc = config.introspectForCreation(type);
/*     */     
/*  70 */     AnnotatedAndMetadata<AnnotatedConstructor, JsonCreator.Mode> ctorInfo = _findStringConstructor(beanDesc);
/*     */     
/*  72 */     if (ctorInfo != null && ctorInfo.metadata != null) {
/*  73 */       return _constructCreatorKeyDeserializer(config, (AnnotatedMember)ctorInfo.annotated);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  78 */     List<AnnotatedAndMetadata<AnnotatedMethod, JsonCreator.Mode>> factoryCandidates = beanDesc.getFactoryMethodsWithMode();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  83 */     factoryCandidates.removeIf(m -> 
/*  84 */         (((AnnotatedMethod)m.annotated).getParameterCount() != 1 || ((AnnotatedMethod)m.annotated).getRawParameterType(0) != String.class || m.metadata == JsonCreator.Mode.PROPERTIES));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  90 */     AnnotatedMethod explicitFactory = _findExplicitStringFactoryMethod(factoryCandidates);
/*  91 */     if (explicitFactory != null) {
/*  92 */       return _constructCreatorKeyDeserializer(config, (AnnotatedMember)explicitFactory);
/*     */     }
/*     */     
/*  95 */     if (ctorInfo != null) {
/*  96 */       return _constructCreatorKeyDeserializer(config, (AnnotatedMember)ctorInfo.annotated);
/*     */     }
/*     */ 
/*     */     
/* 100 */     if (!factoryCandidates.isEmpty())
/*     */     {
/*     */       
/* 103 */       return _constructCreatorKeyDeserializer(config, (AnnotatedMember)((AnnotatedAndMetadata)factoryCandidates.get(0)).annotated);
/*     */     }
/* 105 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static KeyDeserializer _constructCreatorKeyDeserializer(DeserializationConfig config, AnnotatedMember creator) {
/* 111 */     if (creator instanceof AnnotatedConstructor) {
/* 112 */       Constructor<?> rawCtor = ((AnnotatedConstructor)creator).getAnnotated();
/* 113 */       if (config.canOverrideAccessModifiers()) {
/* 114 */         ClassUtil.checkAndFixAccess(rawCtor, config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*     */       }
/* 116 */       return new StdKeyDeserializer.StringCtorKeyDeserializer(rawCtor);
/*     */     } 
/* 118 */     Method m = ((AnnotatedMethod)creator).getAnnotated();
/* 119 */     if (config.canOverrideAccessModifiers()) {
/* 120 */       ClassUtil.checkAndFixAccess(m, config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*     */     }
/* 122 */     return new StdKeyDeserializer.StringFactoryKeyDeserializer(m);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static AnnotatedAndMetadata<AnnotatedConstructor, JsonCreator.Mode> _findStringConstructor(BeanDescription beanDesc) {
/* 130 */     for (AnnotatedAndMetadata<AnnotatedConstructor, JsonCreator.Mode> entry : (Iterable<AnnotatedAndMetadata<AnnotatedConstructor, JsonCreator.Mode>>)beanDesc.getConstructorsWithMode()) {
/*     */ 
/*     */ 
/*     */       
/* 134 */       AnnotatedConstructor ctor = (AnnotatedConstructor)entry.annotated;
/* 135 */       if (ctor.getParameterCount() == 1 && String.class == ctor
/* 136 */         .getRawParameterType(0)) {
/* 137 */         return entry;
/*     */       }
/*     */     } 
/* 140 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static AnnotatedMethod _findExplicitStringFactoryMethod(List<AnnotatedAndMetadata<AnnotatedMethod, JsonCreator.Mode>> candidates) throws JsonMappingException {
/* 147 */     AnnotatedMethod match = null;
/* 148 */     for (AnnotatedAndMetadata<AnnotatedMethod, JsonCreator.Mode> entry : candidates) {
/*     */       
/* 150 */       if (entry.metadata != null) {
/* 151 */         if (match == null) {
/* 152 */           match = (AnnotatedMethod)entry.annotated;
/*     */           
/*     */           continue;
/*     */         } 
/* 156 */         Class<?> rawKeyType = ((AnnotatedMethod)entry.annotated).getDeclaringClass();
/* 157 */         throw new IllegalArgumentException("Multiple suitable annotated Creator factory methods to be used as the Key deserializer for type " + 
/*     */             
/* 159 */             ClassUtil.nameOf(rawKeyType));
/*     */       } 
/*     */     } 
/*     */     
/* 163 */     return match;
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
/*     */   public KeyDeserializer findKeyDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/* 176 */     Class<?> raw = type.getRawClass();
/*     */     
/* 178 */     if (raw.isPrimitive()) {
/* 179 */       raw = ClassUtil.wrapperType(raw);
/*     */     }
/* 181 */     return StdKeyDeserializer.forType(raw);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/StdKeyDeserializers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */