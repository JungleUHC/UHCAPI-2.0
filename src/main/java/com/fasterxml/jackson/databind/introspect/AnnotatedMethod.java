/*     */ package com.fasterxml.jackson.databind.introspect;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
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
/*     */ 
/*     */ public final class AnnotatedMethod
/*     */   extends AnnotatedWithParams
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final transient Method _method;
/*     */   protected Class<?>[] _paramClasses;
/*     */   protected Serialization _serialization;
/*     */   
/*     */   public AnnotatedMethod(TypeResolutionContext ctxt, Method method, AnnotationMap classAnn, AnnotationMap[] paramAnnotations) {
/*  37 */     super(ctxt, classAnn, paramAnnotations);
/*  38 */     if (method == null) {
/*  39 */       throw new IllegalArgumentException("Cannot construct AnnotatedMethod with null Method");
/*     */     }
/*  41 */     this._method = method;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AnnotatedMethod(Serialization ser) {
/*  50 */     super((TypeResolutionContext)null, (AnnotationMap)null, (AnnotationMap[])null);
/*  51 */     this._method = null;
/*  52 */     this._serialization = ser;
/*     */   }
/*     */ 
/*     */   
/*     */   public AnnotatedMethod withAnnotations(AnnotationMap ann) {
/*  57 */     return new AnnotatedMethod(this._typeContext, this._method, ann, this._paramAnnotations);
/*     */   }
/*     */   
/*     */   public Method getAnnotated() {
/*  61 */     return this._method;
/*     */   }
/*     */   public int getModifiers() {
/*  64 */     return this._method.getModifiers();
/*     */   }
/*     */   public String getName() {
/*  67 */     return this._method.getName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType getType() {
/*  76 */     return this._typeContext.resolveType(this._method.getGenericReturnType());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getRawType() {
/*  86 */     return this._method.getReturnType();
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
/*     */   public final Object call() throws Exception {
/*  99 */     return this._method.invoke((Object)null, new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public final Object call(Object[] args) throws Exception {
/* 104 */     return this._method.invoke((Object)null, args);
/*     */   }
/*     */ 
/*     */   
/*     */   public final Object call1(Object arg) throws Exception {
/* 109 */     return this._method.invoke((Object)null, new Object[] { arg });
/*     */   }
/*     */   
/*     */   public final Object callOn(Object pojo) throws Exception {
/* 113 */     return this._method.invoke(pojo, (Object[])null);
/*     */   }
/*     */   
/*     */   public final Object callOnWith(Object pojo, Object... args) throws Exception {
/* 117 */     return this._method.invoke(pojo, args);
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
/* 128 */     return (getRawParameterTypes()).length;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getRawParameterType(int index) {
/* 134 */     Class<?>[] types = getRawParameterTypes();
/* 135 */     return (index >= types.length) ? null : types[index];
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType getParameterType(int index) {
/* 140 */     Type[] types = this._method.getGenericParameterTypes();
/* 141 */     if (index >= types.length) {
/* 142 */       return null;
/*     */     }
/* 144 */     return this._typeContext.resolveType(types[index]);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Type getGenericParameterType(int index) {
/* 150 */     Type[] types = getGenericParameterTypes();
/* 151 */     if (index >= types.length) {
/* 152 */       return null;
/*     */     }
/* 154 */     return types[index];
/*     */   }
/*     */   
/*     */   public Class<?> getDeclaringClass() {
/* 158 */     return this._method.getDeclaringClass();
/*     */   }
/*     */   public Method getMember() {
/* 161 */     return this._method;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValue(Object pojo, Object value) throws IllegalArgumentException {
/*     */     try {
/* 167 */       this._method.invoke(pojo, new Object[] { value });
/* 168 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/* 169 */       throw new IllegalArgumentException("Failed to setValue() with method " + 
/* 170 */           getFullName() + ": " + ClassUtil.exceptionMessage(e), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getValue(Object pojo) throws IllegalArgumentException {
/*     */     try {
/* 178 */       return this._method.invoke(pojo, (Object[])null);
/* 179 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/* 180 */       throw new IllegalArgumentException("Failed to getValue() with method " + 
/* 181 */           getFullName() + ": " + ClassUtil.exceptionMessage(e), e);
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
/*     */   public String getFullName() {
/* 193 */     String methodName = super.getFullName();
/* 194 */     switch (getParameterCount()) {
/*     */       case 0:
/* 196 */         return methodName + "()";
/*     */       case 1:
/* 198 */         return methodName + "(" + getRawParameterType(0).getName() + ")";
/*     */     } 
/*     */     
/* 201 */     return String.format("%s(%d params)", new Object[] { super.getFullName(), Integer.valueOf(getParameterCount()) });
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?>[] getRawParameterTypes() {
/* 206 */     if (this._paramClasses == null) {
/* 207 */       this._paramClasses = this._method.getParameterTypes();
/*     */     }
/* 209 */     return this._paramClasses;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public Type[] getGenericParameterTypes() {
/* 214 */     return this._method.getGenericParameterTypes();
/*     */   }
/*     */   
/*     */   public Class<?> getRawReturnType() {
/* 218 */     return this._method.getReturnType();
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
/*     */   @Deprecated
/*     */   public boolean hasReturnType() {
/* 232 */     Class<?> rt = getRawReturnType();
/*     */     
/* 234 */     return (rt != void.class);
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
/* 245 */     return "[method " + getFullName() + "]";
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 250 */     return this._method.getName().hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 255 */     if (o == this) return true; 
/* 256 */     if (!ClassUtil.hasClass(o, getClass())) {
/* 257 */       return false;
/*     */     }
/*     */     
/* 260 */     AnnotatedMethod other = (AnnotatedMethod)o;
/* 261 */     if (other._method == null) {
/* 262 */       return (this._method == null);
/*     */     }
/* 264 */     return other._method.equals(this._method);
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
/* 275 */     return new AnnotatedMethod(new Serialization(this._method));
/*     */   }
/*     */   
/*     */   Object readResolve() {
/* 279 */     Class<?> clazz = this._serialization.clazz;
/*     */     try {
/* 281 */       Method m = clazz.getDeclaredMethod(this._serialization.name, this._serialization.args);
/*     */ 
/*     */       
/* 284 */       if (!m.isAccessible()) {
/* 285 */         ClassUtil.checkAndFixAccess(m, false);
/*     */       }
/* 287 */       return new AnnotatedMethod(null, m, null, null);
/* 288 */     } catch (Exception e) {
/* 289 */       throw new IllegalArgumentException("Could not find method '" + this._serialization.name + "' from Class '" + clazz
/* 290 */           .getName());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class Serialization
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     protected Class<?> clazz;
/*     */     
/*     */     protected String name;
/*     */     
/*     */     protected Class<?>[] args;
/*     */ 
/*     */     
/*     */     public Serialization(Method setter) {
/* 308 */       this.clazz = setter.getDeclaringClass();
/* 309 */       this.name = setter.getName();
/* 310 */       this.args = setter.getParameterTypes();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/AnnotatedMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */