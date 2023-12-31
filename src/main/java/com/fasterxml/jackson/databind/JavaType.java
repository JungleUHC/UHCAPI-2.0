/*     */ package com.fasterxml.jackson.databind;
/*     */ 
/*     */ import com.fasterxml.jackson.core.type.ResolvedType;
/*     */ import com.fasterxml.jackson.databind.type.TypeBindings;
/*     */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.Type;
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
/*     */ public abstract class JavaType
/*     */   extends ResolvedType
/*     */   implements Serializable, Type
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final Class<?> _class;
/*     */   protected final int _hash;
/*     */   protected final Object _valueHandler;
/*     */   protected final Object _typeHandler;
/*     */   protected final boolean _asStatic;
/*     */   
/*     */   protected JavaType(Class<?> raw, int additionalHash, Object valueHandler, Object typeHandler, boolean asStatic) {
/*  88 */     this._class = raw;
/*  89 */     this._hash = raw.getName().hashCode() + additionalHash;
/*  90 */     this._valueHandler = valueHandler;
/*  91 */     this._typeHandler = typeHandler;
/*  92 */     this._asStatic = asStatic;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JavaType(JavaType base) {
/* 102 */     this._class = base._class;
/* 103 */     this._hash = base._hash;
/* 104 */     this._valueHandler = base._valueHandler;
/* 105 */     this._typeHandler = base._typeHandler;
/* 106 */     this._asStatic = base._asStatic;
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
/*     */ 
/*     */   
/*     */   public JavaType withHandlersFrom(JavaType src) {
/* 210 */     JavaType type = this;
/* 211 */     Object h = src.getTypeHandler();
/* 212 */     if (h != this._typeHandler) {
/* 213 */       type = type.withTypeHandler(h);
/*     */     }
/* 215 */     h = src.getValueHandler();
/* 216 */     if (h != this._valueHandler) {
/* 217 */       type = type.withValueHandler(h);
/*     */     }
/* 219 */     return type;
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
/*     */   @Deprecated
/*     */   public JavaType forcedNarrowBy(Class<?> subclass) {
/* 250 */     if (subclass == this._class) {
/* 251 */       return this;
/*     */     }
/* 253 */     return _narrow(subclass);
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
/*     */   public final Class<?> getRawClass() {
/* 266 */     return this._class;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean hasRawClass(Class<?> clz) {
/* 274 */     return (this._class == clz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasContentType() {
/* 284 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isTypeOrSubTypeOf(Class<?> clz) {
/* 291 */     return (this._class == clz || clz.isAssignableFrom(this._class));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isTypeOrSuperTypeOf(Class<?> clz) {
/* 298 */     return (this._class == clz || this._class.isAssignableFrom(clz));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAbstract() {
/* 303 */     return Modifier.isAbstract(this._class.getModifiers());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isConcrete() {
/* 313 */     int mod = this._class.getModifiers();
/* 314 */     if ((mod & 0x600) == 0) {
/* 315 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 320 */     return this._class.isPrimitive();
/*     */   }
/*     */   
/*     */   public boolean isThrowable() {
/* 324 */     return Throwable.class.isAssignableFrom(this._class);
/*     */   }
/*     */   public boolean isArrayType() {
/* 327 */     return false;
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
/*     */   public final boolean isEnumType() {
/* 340 */     return ClassUtil.isEnumType(this._class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isEnumImplType() {
/* 350 */     return (ClassUtil.isEnumType(this._class) && this._class != Enum.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isRecordType() {
/* 357 */     return ClassUtil.isRecordType(this._class);
/*     */   }
/*     */   
/*     */   public final boolean isInterface() {
/* 361 */     return this._class.isInterface();
/*     */   }
/*     */   public final boolean isPrimitive() {
/* 364 */     return this._class.isPrimitive();
/*     */   }
/*     */   public final boolean isFinal() {
/* 367 */     return Modifier.isFinal(this._class.getModifiers());
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
/*     */   public boolean isCollectionLikeType() {
/* 382 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isMapLikeType() {
/* 390 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isJavaLangObject() {
/* 401 */     return (this._class == Object.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean useStaticType() {
/* 411 */     return this._asStatic;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasGenericTypes() {
/* 420 */     return (containedTypeCount() > 0);
/*     */   }
/*     */   public JavaType getKeyType() {
/* 423 */     return null;
/*     */   }
/*     */   public JavaType getContentType() {
/* 426 */     return null;
/*     */   }
/*     */   public JavaType getReferencedType() {
/* 429 */     return null;
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
/*     */   @Deprecated
/*     */   public Class<?> getParameterSource() {
/* 444 */     return null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType containedTypeOrUnknown(int index) {
/* 470 */     JavaType t = containedType(index);
/* 471 */     return (t == null) ? TypeFactory.unknownType() : t;
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
/*     */   public <T> T getValueHandler() {
/* 528 */     return (T)this._valueHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getTypeHandler() {
/* 538 */     return (T)this._typeHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getContentValueHandler() {
/* 549 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getContentTypeHandler() {
/* 560 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasValueHandler() {
/* 565 */     return (this._valueHandler != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasHandlers() {
/* 576 */     return (this._typeHandler != null || this._valueHandler != null);
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
/*     */ 
/*     */   
/*     */   public String getGenericSignature() {
/* 596 */     StringBuilder sb = new StringBuilder(40);
/* 597 */     getGenericSignature(sb);
/* 598 */     return sb.toString();
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
/*     */   
/*     */   public String getErasedSignature() {
/* 617 */     StringBuilder sb = new StringBuilder(40);
/* 618 */     getErasedSignature(sb);
/* 619 */     return sb.toString();
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
/*     */   public final int hashCode() {
/* 648 */     return this._hash;
/*     */   }
/*     */   
/*     */   public abstract JavaType withContentType(JavaType paramJavaType);
/*     */   
/*     */   public abstract JavaType withStaticTyping();
/*     */   
/*     */   public abstract JavaType withTypeHandler(Object paramObject);
/*     */   
/*     */   public abstract JavaType withContentTypeHandler(Object paramObject);
/*     */   
/*     */   public abstract JavaType withValueHandler(Object paramObject);
/*     */   
/*     */   public abstract JavaType withContentValueHandler(Object paramObject);
/*     */   
/*     */   public abstract JavaType refine(Class<?> paramClass, TypeBindings paramTypeBindings, JavaType paramJavaType, JavaType[] paramArrayOfJavaType);
/*     */   
/*     */   @Deprecated
/*     */   protected abstract JavaType _narrow(Class<?> paramClass);
/*     */   
/*     */   public abstract boolean isContainerType();
/*     */   
/*     */   public abstract int containedTypeCount();
/*     */   
/*     */   public abstract JavaType containedType(int paramInt);
/*     */   
/*     */   @Deprecated
/*     */   public abstract String containedTypeName(int paramInt);
/*     */   
/*     */   public abstract TypeBindings getBindings();
/*     */   
/*     */   public abstract JavaType findSuperType(Class<?> paramClass);
/*     */   
/*     */   public abstract JavaType getSuperClass();
/*     */   
/*     */   public abstract List<JavaType> getInterfaces();
/*     */   
/*     */   public abstract JavaType[] findTypeParameters(Class<?> paramClass);
/*     */   
/*     */   public abstract StringBuilder getGenericSignature(StringBuilder paramStringBuilder);
/*     */   
/*     */   public abstract StringBuilder getErasedSignature(StringBuilder paramStringBuilder);
/*     */   
/*     */   public abstract String toString();
/*     */   
/*     */   public abstract boolean equals(Object paramObject);
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/JavaType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */