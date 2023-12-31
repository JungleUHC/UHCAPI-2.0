/*     */ package com.fasterxml.jackson.databind.introspect;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Type;
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
/*     */ public final class AnnotatedConstructor
/*     */   extends AnnotatedWithParams
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final Constructor<?> _constructor;
/*     */   protected Serialization _serialization;
/*     */   
/*     */   public AnnotatedConstructor(TypeResolutionContext ctxt, Constructor<?> constructor, AnnotationMap classAnn, AnnotationMap[] paramAnn) {
/*  34 */     super(ctxt, classAnn, paramAnn);
/*  35 */     if (constructor == null) {
/*  36 */       throw new IllegalArgumentException("Null constructor not allowed");
/*     */     }
/*  38 */     this._constructor = constructor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AnnotatedConstructor(Serialization ser) {
/*  47 */     super((TypeResolutionContext)null, (AnnotationMap)null, (AnnotationMap[])null);
/*  48 */     this._constructor = null;
/*  49 */     this._serialization = ser;
/*     */   }
/*     */ 
/*     */   
/*     */   public AnnotatedConstructor withAnnotations(AnnotationMap ann) {
/*  54 */     return new AnnotatedConstructor(this._typeContext, this._constructor, ann, this._paramAnnotations);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Constructor<?> getAnnotated() {
/*  64 */     return this._constructor;
/*     */   }
/*     */   public int getModifiers() {
/*  67 */     return this._constructor.getModifiers();
/*     */   }
/*     */   public String getName() {
/*  70 */     return this._constructor.getName();
/*     */   }
/*     */   
/*     */   public JavaType getType() {
/*  74 */     return this._typeContext.resolveType(getRawType());
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> getRawType() {
/*  79 */     return this._constructor.getDeclaringClass();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getParameterCount() {
/*  90 */     return (this._constructor.getParameterTypes()).length;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getRawParameterType(int index) {
/*  96 */     Class<?>[] types = this._constructor.getParameterTypes();
/*  97 */     return (index >= types.length) ? null : types[index];
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType getParameterType(int index) {
/* 102 */     Type[] types = this._constructor.getGenericParameterTypes();
/* 103 */     if (index >= types.length) {
/* 104 */       return null;
/*     */     }
/* 106 */     return this._typeContext.resolveType(types[index]);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Type getGenericParameterType(int index) {
/* 112 */     Type[] types = this._constructor.getGenericParameterTypes();
/* 113 */     if (index >= types.length) {
/* 114 */       return null;
/*     */     }
/* 116 */     return types[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Object call() throws Exception {
/* 123 */     return this._constructor.newInstance((Object[])null);
/*     */   }
/*     */ 
/*     */   
/*     */   public final Object call(Object[] args) throws Exception {
/* 128 */     return this._constructor.newInstance(args);
/*     */   }
/*     */ 
/*     */   
/*     */   public final Object call1(Object arg) throws Exception {
/* 133 */     return this._constructor.newInstance(new Object[] { arg });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getDeclaringClass() {
/* 143 */     return this._constructor.getDeclaringClass();
/*     */   }
/*     */   public Member getMember() {
/* 146 */     return this._constructor;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValue(Object pojo, Object value) throws UnsupportedOperationException {
/* 152 */     throw new UnsupportedOperationException("Cannot call setValue() on constructor of " + 
/* 153 */         getDeclaringClass().getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getValue(Object pojo) throws UnsupportedOperationException {
/* 160 */     throw new UnsupportedOperationException("Cannot call getValue() on constructor of " + 
/* 161 */         getDeclaringClass().getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 172 */     int argCount = (this._constructor.getParameterTypes()).length;
/* 173 */     return String.format("[constructor for %s (%d arg%s), annotations: %s", new Object[] {
/* 174 */           ClassUtil.nameOf(this._constructor.getDeclaringClass()), Integer.valueOf(argCount), (argCount == 1) ? "" : "s", this._annotations
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 180 */     return this._constructor.getName().hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 185 */     if (o == this) return true; 
/* 186 */     if (!ClassUtil.hasClass(o, getClass())) {
/* 187 */       return false;
/*     */     }
/*     */     
/* 190 */     AnnotatedConstructor other = (AnnotatedConstructor)o;
/* 191 */     if (other._constructor == null) {
/* 192 */       return (this._constructor == null);
/*     */     }
/* 194 */     return other._constructor.equals(this._constructor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Object writeReplace() {
/* 205 */     return new AnnotatedConstructor(new Serialization(this._constructor));
/*     */   }
/*     */   
/*     */   Object readResolve() {
/* 209 */     Class<?> clazz = this._serialization.clazz;
/*     */     try {
/* 211 */       Constructor<?> ctor = clazz.getDeclaredConstructor(this._serialization.args);
/*     */       
/* 213 */       if (!ctor.isAccessible()) {
/* 214 */         ClassUtil.checkAndFixAccess(ctor, false);
/*     */       }
/* 216 */       return new AnnotatedConstructor(null, ctor, null, null);
/* 217 */     } catch (Exception e) {
/* 218 */       throw new IllegalArgumentException("Could not find constructor with " + this._serialization.args.length + " args from Class '" + clazz
/* 219 */           .getName());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class Serialization
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     protected Class<?> clazz;
/*     */     
/*     */     protected Class<?>[] args;
/*     */ 
/*     */     
/*     */     public Serialization(Constructor<?> ctor) {
/* 236 */       this.clazz = ctor.getDeclaringClass();
/* 237 */       this.args = ctor.getParameterTypes();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/AnnotatedConstructor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */