/*     */ package com.fasterxml.jackson.databind.type;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ResolvedRecursiveType
/*     */   extends TypeBase
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected JavaType _referencedType;
/*     */   
/*     */   public ResolvedRecursiveType(Class<?> erasedType, TypeBindings bindings) {
/*  17 */     super(erasedType, bindings, null, null, 0, null, null, false);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReference(JavaType ref) {
/*  23 */     if (this._referencedType != null) {
/*  24 */       throw new IllegalStateException("Trying to re-set self reference; old value = " + this._referencedType + ", new = " + ref);
/*     */     }
/*  26 */     this._referencedType = ref;
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType getSuperClass() {
/*  31 */     if (this._referencedType != null) {
/*  32 */       return this._referencedType.getSuperClass();
/*     */     }
/*  34 */     return super.getSuperClass();
/*     */   }
/*     */   public JavaType getSelfReferencedType() {
/*  37 */     return this._referencedType;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeBindings getBindings() {
/*  42 */     if (this._referencedType != null) {
/*  43 */       return this._referencedType.getBindings();
/*     */     }
/*  45 */     return super.getBindings();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringBuilder getGenericSignature(StringBuilder sb) {
/*  53 */     if (this._referencedType != null)
/*     */     {
/*  55 */       return this._referencedType.getErasedSignature(sb);
/*     */     }
/*  57 */     return sb.append("?");
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getErasedSignature(StringBuilder sb) {
/*  62 */     if (this._referencedType != null) {
/*  63 */       return this._referencedType.getErasedSignature(sb);
/*     */     }
/*  65 */     return sb;
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType withContentType(JavaType contentType) {
/*  70 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType withTypeHandler(Object h) {
/*  75 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType withContentTypeHandler(Object h) {
/*  80 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType withValueHandler(Object h) {
/*  85 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType withContentValueHandler(Object h) {
/*  90 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType withStaticTyping() {
/*  95 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected JavaType _narrow(Class<?> subclass) {
/* 101 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/* 107 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isContainerType() {
/* 112 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 118 */     StringBuilder sb = (new StringBuilder(40)).append("[recursive type; ");
/* 119 */     if (this._referencedType == null) {
/* 120 */       sb.append("UNRESOLVED");
/*     */     }
/*     */     else {
/*     */       
/* 124 */       sb.append(this._referencedType.getRawClass().getName());
/*     */     } 
/* 126 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 131 */     if (o == this) return true; 
/* 132 */     if (o == null) return false; 
/* 133 */     if (o.getClass() == getClass())
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 138 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 149 */     return false;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/type/ResolvedRecursiveType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */