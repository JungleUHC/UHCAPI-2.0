/*     */ package com.fasterxml.jackson.databind.type;
/*     */ 
/*     */ import com.fasterxml.jackson.core.type.ResolvedType;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import java.lang.reflect.TypeVariable;
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
/*     */ public class MapLikeType
/*     */   extends TypeBase
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JavaType _keyType;
/*     */   protected final JavaType _valueType;
/*     */   
/*     */   protected MapLikeType(Class<?> mapType, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType keyT, JavaType valueT, Object valueHandler, Object typeHandler, boolean asStatic) {
/*  39 */     super(mapType, bindings, superClass, superInts, keyT.hashCode() ^ valueT
/*  40 */         .hashCode(), valueHandler, typeHandler, asStatic);
/*  41 */     this._keyType = keyT;
/*  42 */     this._valueType = valueT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MapLikeType(TypeBase base, JavaType keyT, JavaType valueT) {
/*  49 */     super(base);
/*  50 */     this._keyType = keyT;
/*  51 */     this._valueType = valueT;
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
/*     */   public static MapLikeType upgradeFrom(JavaType baseType, JavaType keyT, JavaType valueT) {
/*  65 */     if (baseType instanceof TypeBase) {
/*  66 */       return new MapLikeType((TypeBase)baseType, keyT, valueT);
/*     */     }
/*  68 */     throw new IllegalArgumentException("Cannot upgrade from an instance of " + baseType
/*  69 */         .getClass());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static MapLikeType construct(Class<?> rawType, JavaType keyT, JavaType valueT) {
/*     */     TypeBindings bindings;
/*  78 */     TypeVariable[] arrayOfTypeVariable = (TypeVariable[])rawType.getTypeParameters();
/*     */     
/*  80 */     if (arrayOfTypeVariable == null || arrayOfTypeVariable.length != 2) {
/*  81 */       bindings = TypeBindings.emptyBindings();
/*     */     } else {
/*  83 */       bindings = TypeBindings.create(rawType, keyT, valueT);
/*     */     } 
/*  85 */     return new MapLikeType(rawType, bindings, _bogusSuperClass(rawType), null, keyT, valueT, null, null, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected JavaType _narrow(Class<?> subclass) {
/*  93 */     return new MapLikeType(subclass, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MapLikeType withKeyType(JavaType keyType) {
/* 102 */     if (keyType == this._keyType) {
/* 103 */       return this;
/*     */     }
/* 105 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType withContentType(JavaType contentType) {
/* 112 */     if (this._valueType == contentType) {
/* 113 */       return this;
/*     */     }
/* 115 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, contentType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MapLikeType withTypeHandler(Object h) {
/* 122 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, this._valueHandler, h, this._asStatic);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MapLikeType withContentTypeHandler(Object h) {
/* 129 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType
/* 130 */         .withTypeHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MapLikeType withValueHandler(Object h) {
/* 136 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, h, this._typeHandler, this._asStatic);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MapLikeType withContentValueHandler(Object h) {
/* 143 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType
/* 144 */         .withValueHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType withHandlersFrom(JavaType src) {
/* 150 */     JavaType type = super.withHandlersFrom(src);
/* 151 */     JavaType srcKeyType = src.getKeyType();
/*     */     
/* 153 */     if (type instanceof MapLikeType && 
/* 154 */       srcKeyType != null) {
/* 155 */       JavaType ct = this._keyType.withHandlersFrom(srcKeyType);
/* 156 */       if (ct != this._keyType) {
/* 157 */         type = ((MapLikeType)type).withKeyType(ct);
/*     */       }
/*     */     } 
/*     */     
/* 161 */     JavaType srcCt = src.getContentType();
/* 162 */     if (srcCt != null) {
/* 163 */       JavaType ct = this._valueType.withHandlersFrom(srcCt);
/* 164 */       if (ct != this._valueType) {
/* 165 */         type = type.withContentType(ct);
/*     */       }
/*     */     } 
/* 168 */     return type;
/*     */   }
/*     */ 
/*     */   
/*     */   public MapLikeType withStaticTyping() {
/* 173 */     if (this._asStatic) {
/* 174 */       return this;
/*     */     }
/* 176 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType
/* 177 */         .withStaticTyping(), this._valueHandler, this._typeHandler, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/* 184 */     return new MapLikeType(rawType, bindings, superClass, superInterfaces, this._keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected String buildCanonicalName() {
/* 190 */     StringBuilder sb = new StringBuilder();
/* 191 */     sb.append(this._class.getName());
/*     */ 
/*     */     
/* 194 */     if (this._keyType != null && _hasNTypeParameters(2)) {
/* 195 */       sb.append('<');
/* 196 */       sb.append(this._keyType.toCanonical());
/* 197 */       sb.append(',');
/* 198 */       sb.append(this._valueType.toCanonical());
/* 199 */       sb.append('>');
/*     */     } 
/* 201 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isContainerType() {
/* 212 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isMapLikeType() {
/* 217 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType getKeyType() {
/* 222 */     return this._keyType;
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType getContentType() {
/* 227 */     return this._valueType;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getContentValueHandler() {
/* 232 */     return this._valueType.getValueHandler();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getContentTypeHandler() {
/* 237 */     return this._valueType.getTypeHandler();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasHandlers() {
/* 242 */     return (super.hasHandlers() || this._valueType.hasHandlers() || this._keyType
/* 243 */       .hasHandlers());
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getErasedSignature(StringBuilder sb) {
/* 248 */     return _classSignature(this._class, sb, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getGenericSignature(StringBuilder sb) {
/* 253 */     _classSignature(this._class, sb, false);
/* 254 */     sb.append('<');
/* 255 */     this._keyType.getGenericSignature(sb);
/* 256 */     this._valueType.getGenericSignature(sb);
/* 257 */     sb.append(">;");
/* 258 */     return sb;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MapLikeType withKeyTypeHandler(Object h) {
/* 268 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType
/* 269 */         .withTypeHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
/*     */   }
/*     */ 
/*     */   
/*     */   public MapLikeType withKeyValueHandler(Object h) {
/* 274 */     return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType
/* 275 */         .withValueHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
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
/*     */   @Deprecated
/*     */   public boolean isTrueMapType() {
/* 288 */     return Map.class.isAssignableFrom(this._class);
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
/* 299 */     return String.format("[map-like type; class %s, %s -> %s]", new Object[] { this._class
/* 300 */           .getName(), this._keyType, this._valueType });
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 305 */     if (o == this) return true; 
/* 306 */     if (o == null) return false; 
/* 307 */     if (o.getClass() != getClass()) return false;
/*     */     
/* 309 */     MapLikeType other = (MapLikeType)o;
/* 310 */     return (this._class == other._class && this._keyType.equals(other._keyType) && this._valueType
/* 311 */       .equals(other._valueType));
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/type/MapLikeType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */