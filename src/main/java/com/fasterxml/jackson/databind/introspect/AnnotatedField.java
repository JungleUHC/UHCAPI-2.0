/*     */ package com.fasterxml.jackson.databind.introspect;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Modifier;
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
/*     */ 
/*     */ 
/*     */ public final class AnnotatedField
/*     */   extends AnnotatedMember
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final transient Field _field;
/*     */   protected Serialization _serialization;
/*     */   
/*     */   public AnnotatedField(TypeResolutionContext contextClass, Field field, AnnotationMap annMap) {
/*  39 */     super(contextClass, annMap);
/*  40 */     this._field = field;
/*     */   }
/*     */ 
/*     */   
/*     */   public AnnotatedField withAnnotations(AnnotationMap ann) {
/*  45 */     return new AnnotatedField(this._typeContext, this._field, ann);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AnnotatedField(Serialization ser) {
/*  53 */     super(null, null);
/*  54 */     this._field = null;
/*  55 */     this._serialization = ser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Field getAnnotated() {
/*  65 */     return this._field;
/*     */   }
/*     */   public int getModifiers() {
/*  68 */     return this._field.getModifiers();
/*     */   }
/*     */   public String getName() {
/*  71 */     return this._field.getName();
/*     */   }
/*     */   
/*     */   public Class<?> getRawType() {
/*  75 */     return this._field.getType();
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType getType() {
/*  80 */     return this._typeContext.resolveType(this._field.getGenericType());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getDeclaringClass() {
/*  90 */     return this._field.getDeclaringClass();
/*     */   }
/*     */   public Member getMember() {
/*  93 */     return this._field;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValue(Object pojo, Object value) throws IllegalArgumentException {
/*     */     try {
/*  99 */       this._field.set(pojo, value);
/* 100 */     } catch (IllegalAccessException e) {
/* 101 */       throw new IllegalArgumentException("Failed to setValue() for field " + 
/* 102 */           getFullName() + ": " + e.getMessage(), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getValue(Object pojo) throws IllegalArgumentException {
/*     */     try {
/* 110 */       return this._field.get(pojo);
/* 111 */     } catch (IllegalAccessException e) {
/* 112 */       throw new IllegalArgumentException("Failed to getValue() for field " + 
/* 113 */           getFullName() + ": " + e.getMessage(), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getAnnotationCount() {
/* 123 */     return this._annotations.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTransient() {
/* 128 */     return Modifier.isTransient(getModifiers());
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 132 */     return this._field.getName().hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 137 */     if (o == this) return true; 
/* 138 */     if (!ClassUtil.hasClass(o, getClass())) {
/* 139 */       return false;
/*     */     }
/*     */     
/* 142 */     AnnotatedField other = (AnnotatedField)o;
/* 143 */     if (other._field == null) {
/* 144 */       return (this._field == null);
/*     */     }
/* 146 */     return other._field.equals(this._field);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 152 */     return "[field " + getFullName() + "]";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Object writeReplace() {
/* 162 */     return new AnnotatedField(new Serialization(this._field));
/*     */   }
/*     */   
/*     */   Object readResolve() {
/* 166 */     Class<?> clazz = this._serialization.clazz;
/*     */     try {
/* 168 */       Field f = clazz.getDeclaredField(this._serialization.name);
/*     */       
/* 170 */       if (!f.isAccessible()) {
/* 171 */         ClassUtil.checkAndFixAccess(f, false);
/*     */       }
/* 173 */       return new AnnotatedField(null, f, null);
/* 174 */     } catch (Exception e) {
/* 175 */       throw new IllegalArgumentException("Could not find method '" + this._serialization.name + "' from Class '" + clazz
/* 176 */           .getName());
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
/*     */     protected String name;
/*     */ 
/*     */     
/*     */     public Serialization(Field f) {
/* 193 */       this.clazz = f.getDeclaringClass();
/* 194 */       this.name = f.getName();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/AnnotatedField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */