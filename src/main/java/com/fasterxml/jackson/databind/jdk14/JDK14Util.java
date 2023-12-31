/*     */ package com.fasterxml.jackson.databind.jdk14;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonCreator;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
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
/*     */ public class JDK14Util
/*     */ {
/*     */   public static String[] getRecordFieldNames(Class<?> recordType) {
/*  27 */     return RecordAccessor.instance().getRecordFieldNames(recordType);
/*     */   }
/*     */ 
/*     */   
/*     */   public static AnnotatedConstructor findRecordConstructor(DeserializationContext ctxt, BeanDescription beanDesc, List<String> names) {
/*  32 */     return (new CreatorLocator(ctxt, beanDesc))
/*  33 */       .locate(names);
/*     */   }
/*     */   
/*     */   static class RecordAccessor
/*     */   {
/*     */     private final Method RECORD_GET_RECORD_COMPONENTS;
/*     */     private final Method RECORD_COMPONENT_GET_NAME;
/*     */     private final Method RECORD_COMPONENT_GET_TYPE;
/*     */     private static final RecordAccessor INSTANCE;
/*     */     private static final RuntimeException PROBLEM;
/*     */     
/*     */     static {
/*  45 */       RuntimeException prob = null;
/*  46 */       RecordAccessor inst = null;
/*     */       try {
/*  48 */         inst = new RecordAccessor();
/*  49 */       } catch (RuntimeException e) {
/*  50 */         prob = e;
/*     */       } 
/*  52 */       INSTANCE = inst;
/*  53 */       PROBLEM = prob;
/*     */     }
/*     */     
/*     */     private RecordAccessor() throws RuntimeException {
/*     */       try {
/*  58 */         this.RECORD_GET_RECORD_COMPONENTS = Class.class.getMethod("getRecordComponents", new Class[0]);
/*  59 */         Class<?> c = Class.forName("java.lang.reflect.RecordComponent");
/*  60 */         this.RECORD_COMPONENT_GET_NAME = c.getMethod("getName", new Class[0]);
/*  61 */         this.RECORD_COMPONENT_GET_TYPE = c.getMethod("getType", new Class[0]);
/*  62 */       } catch (Exception e) {
/*  63 */         throw new RuntimeException(String.format("Failed to access Methods needed to support `java.lang.Record`: (%s) %s", new Object[] { e
/*     */                 
/*  65 */                 .getClass().getName(), e.getMessage() }), e);
/*     */       } 
/*     */     }
/*     */     
/*     */     public static RecordAccessor instance() {
/*  70 */       if (PROBLEM != null) {
/*  71 */         throw PROBLEM;
/*     */       }
/*  73 */       return INSTANCE;
/*     */     }
/*     */ 
/*     */     
/*     */     public String[] getRecordFieldNames(Class<?> recordType) throws IllegalArgumentException {
/*  78 */       Object[] components = recordComponents(recordType);
/*  79 */       String[] names = new String[components.length];
/*  80 */       for (int i = 0; i < components.length; i++) {
/*     */         try {
/*  82 */           names[i] = (String)this.RECORD_COMPONENT_GET_NAME.invoke(components[i], new Object[0]);
/*  83 */         } catch (Exception e) {
/*  84 */           throw new IllegalArgumentException(String.format("Failed to access name of field #%d (of %d) of Record type %s", new Object[] {
/*     */                   
/*  86 */                   Integer.valueOf(i), Integer.valueOf(components.length), ClassUtil.nameOf(recordType) }), e);
/*     */         } 
/*     */       } 
/*  89 */       return names;
/*     */     }
/*     */ 
/*     */     
/*     */     public JDK14Util.RawTypeName[] getRecordFields(Class<?> recordType) throws IllegalArgumentException {
/*  94 */       Object[] components = recordComponents(recordType);
/*  95 */       JDK14Util.RawTypeName[] results = new JDK14Util.RawTypeName[components.length];
/*  96 */       for (int i = 0; i < components.length; i++) {
/*     */         String name; Class<?> type;
/*     */         try {
/*  99 */           name = (String)this.RECORD_COMPONENT_GET_NAME.invoke(components[i], new Object[0]);
/* 100 */         } catch (Exception e) {
/* 101 */           throw new IllegalArgumentException(String.format("Failed to access name of field #%d (of %d) of Record type %s", new Object[] {
/*     */                   
/* 103 */                   Integer.valueOf(i), Integer.valueOf(components.length), ClassUtil.nameOf(recordType)
/*     */                 }), e);
/*     */         } 
/*     */         try {
/* 107 */           type = (Class)this.RECORD_COMPONENT_GET_TYPE.invoke(components[i], new Object[0]);
/* 108 */         } catch (Exception e) {
/* 109 */           throw new IllegalArgumentException(String.format("Failed to access type of field #%d (of %d) of Record type %s", new Object[] {
/*     */                   
/* 111 */                   Integer.valueOf(i), Integer.valueOf(components.length), ClassUtil.nameOf(recordType) }), e);
/*     */         } 
/* 113 */         results[i] = new JDK14Util.RawTypeName(type, name);
/*     */       } 
/* 115 */       return results;
/*     */     }
/*     */ 
/*     */     
/*     */     protected Object[] recordComponents(Class<?> recordType) throws IllegalArgumentException {
/*     */       try {
/* 121 */         return (Object[])this.RECORD_GET_RECORD_COMPONENTS.invoke(recordType, new Object[0]);
/* 122 */       } catch (Exception e) {
/* 123 */         throw new IllegalArgumentException("Failed to access RecordComponents of type " + 
/* 124 */             ClassUtil.nameOf(recordType));
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   static class RawTypeName {
/*     */     public final Class<?> rawType;
/*     */     public final String name;
/*     */     
/*     */     public RawTypeName(Class<?> rt, String n) {
/* 134 */       this.rawType = rt;
/* 135 */       this.name = n;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class CreatorLocator
/*     */   {
/*     */     protected final BeanDescription _beanDesc;
/*     */     protected final DeserializationConfig _config;
/*     */     protected final AnnotationIntrospector _intr;
/*     */     protected final List<AnnotatedConstructor> _constructors;
/*     */     protected final AnnotatedConstructor _primaryConstructor;
/*     */     protected final JDK14Util.RawTypeName[] _recordFields;
/*     */     
/*     */     CreatorLocator(DeserializationContext ctxt, BeanDescription beanDesc) {
/* 150 */       this._beanDesc = beanDesc;
/*     */       
/* 152 */       this._intr = ctxt.getAnnotationIntrospector();
/* 153 */       this._config = ctxt.getConfig();
/*     */       
/* 155 */       this._recordFields = JDK14Util.RecordAccessor.instance().getRecordFields(beanDesc.getBeanClass());
/* 156 */       int argCount = this._recordFields.length;
/*     */ 
/*     */ 
/*     */       
/* 160 */       AnnotatedConstructor primary = null;
/*     */ 
/*     */       
/* 163 */       if (argCount == 0) {
/* 164 */         primary = beanDesc.findDefaultConstructor();
/* 165 */         this._constructors = Collections.singletonList(primary);
/*     */       } else {
/* 167 */         this._constructors = beanDesc.getConstructors();
/*     */         
/* 169 */         label24: for (AnnotatedConstructor ctor : this._constructors) {
/* 170 */           if (ctor.getParameterCount() != argCount) {
/*     */             continue;
/*     */           }
/* 173 */           for (int i = 0; i < argCount; i++) {
/* 174 */             if (!ctor.getRawParameterType(i).equals((this._recordFields[i]).rawType)) {
/*     */               continue label24;
/*     */             }
/*     */           } 
/* 178 */           primary = ctor;
/*     */         } 
/*     */       } 
/*     */       
/* 182 */       if (primary == null) {
/* 183 */         throw new IllegalArgumentException("Failed to find the canonical Record constructor of type " + 
/* 184 */             ClassUtil.getTypeDescription(this._beanDesc.getType()));
/*     */       }
/* 186 */       this._primaryConstructor = primary;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public AnnotatedConstructor locate(List<String> names) {
/* 194 */       for (AnnotatedConstructor ctor : this._constructors) {
/* 195 */         JsonCreator.Mode creatorMode = this._intr.findCreatorAnnotation((MapperConfig)this._config, (Annotated)ctor);
/* 196 */         if (null == creatorMode || JsonCreator.Mode.DISABLED == creatorMode) {
/*     */           continue;
/*     */         }
/*     */         
/* 200 */         if (JsonCreator.Mode.DELEGATING == creatorMode) {
/* 201 */           return null;
/*     */         }
/* 203 */         if (ctor != this._primaryConstructor) {
/* 204 */           return null;
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 210 */       for (JDK14Util.RawTypeName field : this._recordFields) {
/* 211 */         names.add(field.name);
/*     */       }
/* 213 */       return this._primaryConstructor;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jdk14/JDK14Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */