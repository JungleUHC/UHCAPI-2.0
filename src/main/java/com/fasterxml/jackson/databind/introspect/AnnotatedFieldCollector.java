/*     */ package com.fasterxml.jackson.databind.introspect;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
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
/*     */ public class AnnotatedFieldCollector
/*     */   extends CollectorBase
/*     */ {
/*     */   private final TypeFactory _typeFactory;
/*     */   private final ClassIntrospector.MixInResolver _mixInResolver;
/*     */   private final boolean _collectAnnotations;
/*     */   
/*     */   AnnotatedFieldCollector(AnnotationIntrospector intr, TypeFactory types, ClassIntrospector.MixInResolver mixins, boolean collectAnnotations) {
/*  31 */     super(intr);
/*  32 */     this._typeFactory = types;
/*  33 */     this._mixInResolver = (intr == null) ? null : mixins;
/*  34 */     this._collectAnnotations = collectAnnotations;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<AnnotatedField> collectFields(AnnotationIntrospector intr, TypeResolutionContext tc, ClassIntrospector.MixInResolver mixins, TypeFactory types, JavaType type, boolean collectAnnotations) {
/*  42 */     return (new AnnotatedFieldCollector(intr, types, mixins, collectAnnotations))
/*  43 */       .collect(tc, type);
/*     */   }
/*     */ 
/*     */   
/*     */   List<AnnotatedField> collect(TypeResolutionContext tc, JavaType type) {
/*  48 */     Map<String, FieldBuilder> foundFields = _findFields(tc, type, null);
/*  49 */     if (foundFields == null) {
/*  50 */       return Collections.emptyList();
/*     */     }
/*  52 */     List<AnnotatedField> result = new ArrayList<>(foundFields.size());
/*  53 */     for (FieldBuilder b : foundFields.values()) {
/*  54 */       result.add(b.build());
/*     */     }
/*  56 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Map<String, FieldBuilder> _findFields(TypeResolutionContext tc, JavaType type, Map<String, FieldBuilder> fields) {
/*  65 */     JavaType parent = type.getSuperClass();
/*  66 */     if (parent == null) {
/*  67 */       return fields;
/*     */     }
/*  69 */     Class<?> cls = type.getRawClass();
/*     */     
/*  71 */     fields = _findFields(new TypeResolutionContext.Basic(this._typeFactory, parent.getBindings()), parent, fields);
/*     */     
/*  73 */     for (Field f : cls.getDeclaredFields()) {
/*     */       
/*  75 */       if (_isIncludableField(f)) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  81 */         if (fields == null) {
/*  82 */           fields = new LinkedHashMap<>();
/*     */         }
/*  84 */         FieldBuilder b = new FieldBuilder(tc, f);
/*  85 */         if (this._collectAnnotations) {
/*  86 */           b.annotations = collectAnnotations(b.annotations, f.getDeclaredAnnotations());
/*     */         }
/*  88 */         fields.put(f.getName(), b);
/*     */       } 
/*     */     } 
/*  91 */     if (fields != null && this._mixInResolver != null) {
/*  92 */       Class<?> mixin = this._mixInResolver.findMixInClassFor(cls);
/*  93 */       if (mixin != null) {
/*  94 */         _addFieldMixIns(mixin, cls, fields);
/*     */       }
/*     */     } 
/*  97 */     return fields;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void _addFieldMixIns(Class<?> mixInCls, Class<?> targetClass, Map<String, FieldBuilder> fields) {
/* 108 */     List<Class<?>> parents = ClassUtil.findSuperClasses(mixInCls, targetClass, true);
/* 109 */     for (Class<?> mixin : parents) {
/* 110 */       for (Field mixinField : mixin.getDeclaredFields()) {
/*     */         
/* 112 */         if (_isIncludableField(mixinField)) {
/*     */ 
/*     */           
/* 115 */           String name = mixinField.getName();
/*     */           
/* 117 */           FieldBuilder b = fields.get(name);
/* 118 */           if (b != null) {
/* 119 */             b.annotations = collectAnnotations(b.annotations, mixinField.getDeclaredAnnotations());
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean _isIncludableField(Field f) {
/* 128 */     if (f.isSynthetic()) {
/* 129 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 133 */     int mods = f.getModifiers();
/* 134 */     if (Modifier.isStatic(mods)) {
/* 135 */       return false;
/*     */     }
/* 137 */     return true;
/*     */   }
/*     */   
/*     */   private static final class FieldBuilder
/*     */   {
/*     */     public final TypeResolutionContext typeContext;
/*     */     public final Field field;
/*     */     public AnnotationCollector annotations;
/*     */     
/*     */     public FieldBuilder(TypeResolutionContext tc, Field f) {
/* 147 */       this.typeContext = tc;
/* 148 */       this.field = f;
/* 149 */       this.annotations = AnnotationCollector.emptyCollector();
/*     */     }
/*     */     
/*     */     public AnnotatedField build() {
/* 153 */       return new AnnotatedField(this.typeContext, this.field, this.annotations.asAnnotationMap());
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/AnnotatedFieldCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */