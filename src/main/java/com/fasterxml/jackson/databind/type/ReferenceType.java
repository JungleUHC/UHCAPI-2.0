/*     */ package com.fasterxml.jackson.databind.type;
/*     */ 
/*     */ import com.fasterxml.jackson.core.type.ResolvedType;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import java.util.Objects;
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
/*     */ public class ReferenceType
/*     */   extends SimpleType
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JavaType _referencedType;
/*     */   protected final JavaType _anchorType;
/*     */   
/*     */   protected ReferenceType(Class<?> cls, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType refType, JavaType anchorType, Object valueHandler, Object typeHandler, boolean asStatic) {
/*  36 */     super(cls, bindings, superClass, superInts, Objects.hashCode(refType), valueHandler, typeHandler, asStatic);
/*     */     
/*  38 */     this._referencedType = refType;
/*  39 */     this._anchorType = (anchorType == null) ? this : anchorType;
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
/*     */   protected ReferenceType(TypeBase base, JavaType refType) {
/*  51 */     super(base);
/*  52 */     this._referencedType = refType;
/*     */     
/*  54 */     this._anchorType = this;
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
/*     */   public static ReferenceType upgradeFrom(JavaType baseType, JavaType refdType) {
/*  67 */     if (refdType == null) {
/*  68 */       throw new IllegalArgumentException("Missing referencedType");
/*     */     }
/*     */ 
/*     */     
/*  72 */     if (baseType instanceof TypeBase) {
/*  73 */       return new ReferenceType((TypeBase)baseType, refdType);
/*     */     }
/*  75 */     throw new IllegalArgumentException("Cannot upgrade from an instance of " + baseType.getClass());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ReferenceType construct(Class<?> cls, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType refType) {
/*  84 */     return new ReferenceType(cls, bindings, superClass, superInts, refType, null, null, null, false);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static ReferenceType construct(Class<?> cls, JavaType refType) {
/*  90 */     return new ReferenceType(cls, TypeBindings.emptyBindings(), null, null, null, refType, null, null, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType withContentType(JavaType contentType) {
/*  97 */     if (this._referencedType == contentType) {
/*  98 */       return this;
/*     */     }
/* 100 */     return new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, contentType, this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReferenceType withTypeHandler(Object h) {
/* 107 */     if (h == this._typeHandler) {
/* 108 */       return this;
/*     */     }
/* 110 */     return new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, this._referencedType, this._anchorType, this._valueHandler, h, this._asStatic);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReferenceType withContentTypeHandler(Object h) {
/* 117 */     if (h == this._referencedType.getTypeHandler()) {
/* 118 */       return this;
/*     */     }
/* 120 */     return new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, this._referencedType
/* 121 */         .withTypeHandler(h), this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ReferenceType withValueHandler(Object h) {
/* 127 */     if (h == this._valueHandler) {
/* 128 */       return this;
/*     */     }
/* 130 */     return new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, this._referencedType, this._anchorType, h, this._typeHandler, this._asStatic);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReferenceType withContentValueHandler(Object h) {
/* 137 */     if (h == this._referencedType.getValueHandler()) {
/* 138 */       return this;
/*     */     }
/* 140 */     JavaType refdType = this._referencedType.withValueHandler(h);
/* 141 */     return new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, refdType, this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReferenceType withStaticTyping() {
/* 148 */     if (this._asStatic) {
/* 149 */       return this;
/*     */     }
/* 151 */     return new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, this._referencedType
/* 152 */         .withStaticTyping(), this._anchorType, this._valueHandler, this._typeHandler, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/* 159 */     return new ReferenceType(rawType, this._bindings, superClass, superInterfaces, this._referencedType, this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String buildCanonicalName() {
/* 167 */     StringBuilder sb = new StringBuilder();
/* 168 */     sb.append(this._class.getName());
/* 169 */     if (this._referencedType != null && _hasNTypeParameters(1)) {
/* 170 */       sb.append('<');
/* 171 */       sb.append(this._referencedType.toCanonical());
/* 172 */       sb.append('>');
/*     */     } 
/* 174 */     return sb.toString();
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
/*     */   protected JavaType _narrow(Class<?> subclass) {
/* 188 */     return new ReferenceType(subclass, this._bindings, this._superClass, this._superInterfaces, this._referencedType, this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
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
/*     */   public JavaType getContentType() {
/* 201 */     return this._referencedType;
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType getReferencedType() {
/* 206 */     return this._referencedType;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasContentType() {
/* 211 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReferenceType() {
/* 216 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getErasedSignature(StringBuilder sb) {
/* 221 */     return _classSignature(this._class, sb, true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public StringBuilder getGenericSignature(StringBuilder sb) {
/* 227 */     _classSignature(this._class, sb, false);
/* 228 */     sb.append('<');
/* 229 */     sb = this._referencedType.getGenericSignature(sb);
/* 230 */     sb.append(">;");
/* 231 */     return sb;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType getAnchorType() {
/* 241 */     return this._anchorType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAnchorType() {
/* 249 */     return (this._anchorType == this);
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
/*     */   public String toString() {
/* 261 */     return (new StringBuilder(40))
/* 262 */       .append("[reference type, class ")
/* 263 */       .append(buildCanonicalName())
/* 264 */       .append('<')
/* 265 */       .append(this._referencedType)
/* 266 */       .append('>')
/* 267 */       .append(']')
/* 268 */       .toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 274 */     if (o == this) return true; 
/* 275 */     if (o == null) return false; 
/* 276 */     if (o.getClass() != getClass()) return false;
/*     */     
/* 278 */     ReferenceType other = (ReferenceType)o;
/*     */     
/* 280 */     if (other._class != this._class) return false;
/*     */ 
/*     */     
/* 283 */     return this._referencedType.equals(other._referencedType);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/type/ReferenceType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */