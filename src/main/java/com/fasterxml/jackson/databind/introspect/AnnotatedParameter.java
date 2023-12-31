/*     */ package com.fasterxml.jackson.databind.introspect;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.lang.reflect.AnnotatedElement;
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
/*     */ public final class AnnotatedParameter
/*     */   extends AnnotatedMember
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final AnnotatedWithParams _owner;
/*     */   protected final JavaType _type;
/*     */   protected final int _index;
/*     */   
/*     */   public AnnotatedParameter(AnnotatedWithParams owner, JavaType type, TypeResolutionContext typeContext, AnnotationMap annotations, int index) {
/*  45 */     super(typeContext, annotations);
/*  46 */     this._owner = owner;
/*  47 */     this._type = type;
/*  48 */     this._index = index;
/*     */   }
/*     */ 
/*     */   
/*     */   public AnnotatedParameter withAnnotations(AnnotationMap ann) {
/*  53 */     if (ann == this._annotations) {
/*  54 */       return this;
/*     */     }
/*  56 */     return this._owner.replaceParameterAnnotations(this._index, ann);
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
/*     */   public AnnotatedElement getAnnotated() {
/*  70 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getModifiers() {
/*  77 */     return this._owner.getModifiers();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  84 */     return "";
/*     */   }
/*     */   
/*     */   public Class<?> getRawType() {
/*  88 */     return this._type.getRawClass();
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType getType() {
/*  93 */     return this._type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getDeclaringClass() {
/* 104 */     return this._owner.getDeclaringClass();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Member getMember() {
/* 111 */     return this._owner.getMember();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValue(Object pojo, Object value) throws UnsupportedOperationException {
/* 117 */     throw new UnsupportedOperationException("Cannot call setValue() on constructor parameter of " + 
/* 118 */         getDeclaringClass().getName());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getValue(Object pojo) throws UnsupportedOperationException {
/* 124 */     throw new UnsupportedOperationException("Cannot call getValue() on constructor parameter of " + 
/* 125 */         getDeclaringClass().getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Type getParameterType() {
/* 134 */     return (Type)this._type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotatedWithParams getOwner() {
/* 142 */     return this._owner;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIndex() {
/* 149 */     return this._index;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 159 */     return this._owner.hashCode() + this._index;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 164 */     if (o == this) return true; 
/* 165 */     if (!ClassUtil.hasClass(o, getClass())) {
/* 166 */       return false;
/*     */     }
/* 168 */     AnnotatedParameter other = (AnnotatedParameter)o;
/* 169 */     return (other._owner.equals(this._owner) && other._index == this._index);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 174 */     return "[parameter #" + getIndex() + ", annotations: " + this._annotations + "]";
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/AnnotatedParameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */