/*     */ package com.fasterxml.jackson.databind.type;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.util.AbstractList;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class TypeBindings implements Serializable {
/*  17 */   private static final String[] NO_STRINGS = new String[0];
/*     */   private static final long serialVersionUID = 1L;
/*  19 */   private static final JavaType[] NO_TYPES = new JavaType[0];
/*     */   
/*  21 */   private static final TypeBindings EMPTY = new TypeBindings(NO_STRINGS, NO_TYPES, null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String[] _names;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final JavaType[] _types;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String[] _unboundVariables;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int _hashCode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TypeBindings(String[] names, JavaType[] types, String[] uvars) {
/*  54 */     this._names = (names == null) ? NO_STRINGS : names;
/*  55 */     this._types = (types == null) ? NO_TYPES : types;
/*  56 */     if (this._names.length != this._types.length) {
/*  57 */       throw new IllegalArgumentException("Mismatching names (" + this._names.length + "), types (" + this._types.length + ")");
/*     */     }
/*  59 */     int h = 1;
/*  60 */     for (int i = 0, len = this._types.length; i < len; i++) {
/*  61 */       h += this._types[i].hashCode();
/*     */     }
/*  63 */     this._unboundVariables = uvars;
/*  64 */     this._hashCode = h;
/*     */   }
/*     */   
/*     */   public static TypeBindings emptyBindings() {
/*  68 */     return EMPTY;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object readResolve() {
/*  73 */     if (this._names == null || this._names.length == 0) {
/*  74 */       return EMPTY;
/*     */     }
/*  76 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TypeBindings create(Class<?> erasedType, List<JavaType> typeList) {
/*  86 */     JavaType[] types = (typeList == null || typeList.isEmpty()) ? NO_TYPES : typeList.<JavaType>toArray(NO_TYPES);
/*  87 */     return create(erasedType, types);
/*     */   }
/*     */   
/*     */   public static TypeBindings create(Class<?> erasedType, JavaType[] types) {
/*     */     String[] names;
/*  92 */     if (types == null)
/*  93 */     { types = NO_TYPES; }
/*  94 */     else { switch (types.length) {
/*     */         case 1:
/*  96 */           return create(erasedType, types[0]);
/*     */         case 2:
/*  98 */           return create(erasedType, types[0], types[1]);
/*     */       }  }
/* 100 */      TypeVariable[] arrayOfTypeVariable = (TypeVariable[])erasedType.getTypeParameters();
/*     */     
/* 102 */     if (arrayOfTypeVariable == null || arrayOfTypeVariable.length == 0) {
/* 103 */       names = NO_STRINGS;
/*     */     } else {
/* 105 */       int len = arrayOfTypeVariable.length;
/* 106 */       names = new String[len];
/* 107 */       for (int i = 0; i < len; i++) {
/* 108 */         names[i] = arrayOfTypeVariable[i].getName();
/*     */       }
/*     */     } 
/*     */     
/* 112 */     if (names.length != types.length) {
/* 113 */       throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with " + types.length + " type parameter" + ((types.length == 1) ? "" : "s") + ": class expects " + names.length);
/*     */     }
/*     */ 
/*     */     
/* 117 */     return new TypeBindings(names, types, null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static TypeBindings create(Class<?> erasedType, JavaType typeArg1) {
/* 123 */     TypeVariable[] arrayOfTypeVariable = (TypeVariable[])TypeParamStash.paramsFor1(erasedType);
/* 124 */     int varLen = (arrayOfTypeVariable == null) ? 0 : arrayOfTypeVariable.length;
/* 125 */     if (varLen != 1) {
/* 126 */       throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with 1 type parameter: class expects " + varLen);
/*     */     }
/*     */     
/* 129 */     return new TypeBindings(new String[] { arrayOfTypeVariable[0].getName() }, new JavaType[] { typeArg1 }, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TypeBindings create(Class<?> erasedType, JavaType typeArg1, JavaType typeArg2) {
/* 136 */     TypeVariable[] arrayOfTypeVariable = (TypeVariable[])TypeParamStash.paramsFor2(erasedType);
/* 137 */     int varLen = (arrayOfTypeVariable == null) ? 0 : arrayOfTypeVariable.length;
/* 138 */     if (varLen != 2) {
/* 139 */       throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with 2 type parameters: class expects " + varLen);
/*     */     }
/*     */     
/* 142 */     return new TypeBindings(new String[] { arrayOfTypeVariable[0].getName(), arrayOfTypeVariable[1].getName() }, new JavaType[] { typeArg1, typeArg2 }, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TypeBindings create(List<String> names, List<JavaType> types) {
/* 151 */     if (names == null || names.isEmpty() || types == null || types.isEmpty()) {
/* 152 */       return EMPTY;
/*     */     }
/* 154 */     return new TypeBindings(names.<String>toArray(NO_STRINGS), types.<JavaType>toArray(NO_TYPES), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TypeBindings createIfNeeded(Class<?> erasedType, JavaType typeArg1) {
/* 164 */     TypeVariable[] arrayOfTypeVariable = (TypeVariable[])erasedType.getTypeParameters();
/* 165 */     int varLen = (arrayOfTypeVariable == null) ? 0 : arrayOfTypeVariable.length;
/* 166 */     if (varLen == 0) {
/* 167 */       return EMPTY;
/*     */     }
/* 169 */     if (varLen != 1) {
/* 170 */       throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with 1 type parameter: class expects " + varLen);
/*     */     }
/*     */     
/* 173 */     return new TypeBindings(new String[] { arrayOfTypeVariable[0].getName() }, new JavaType[] { typeArg1 }, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TypeBindings createIfNeeded(Class<?> erasedType, JavaType[] types) {
/* 184 */     TypeVariable[] arrayOfTypeVariable = (TypeVariable[])erasedType.getTypeParameters();
/* 185 */     if (arrayOfTypeVariable == null || arrayOfTypeVariable.length == 0) {
/* 186 */       return EMPTY;
/*     */     }
/* 188 */     if (types == null) {
/* 189 */       types = NO_TYPES;
/*     */     }
/* 191 */     int len = arrayOfTypeVariable.length;
/* 192 */     String[] names = new String[len];
/* 193 */     for (int i = 0; i < len; i++) {
/* 194 */       names[i] = arrayOfTypeVariable[i].getName();
/*     */     }
/*     */     
/* 197 */     if (names.length != types.length) {
/* 198 */       throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with " + types.length + " type parameter" + ((types.length == 1) ? "" : "s") + ": class expects " + names.length);
/*     */     }
/*     */ 
/*     */     
/* 202 */     return new TypeBindings(names, types, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeBindings withUnboundVariable(String name) {
/* 212 */     int len = (this._unboundVariables == null) ? 0 : this._unboundVariables.length;
/*     */     
/* 214 */     String[] names = (len == 0) ? new String[1] : Arrays.<String>copyOf(this._unboundVariables, len + 1);
/* 215 */     names[len] = name;
/* 216 */     return new TypeBindings(this._names, this._types, names);
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
/*     */   public JavaType findBoundType(String name) {
/* 230 */     for (int i = 0, len = this._names.length; i < len; i++) {
/* 231 */       if (name.equals(this._names[i])) {
/* 232 */         JavaType t = this._types[i];
/* 233 */         if (t instanceof ResolvedRecursiveType) {
/* 234 */           ResolvedRecursiveType rrt = (ResolvedRecursiveType)t;
/* 235 */           JavaType t2 = rrt.getSelfReferencedType();
/* 236 */           if (t2 != null) {
/* 237 */             t = t2;
/*     */           }
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 250 */         return t;
/*     */       } 
/*     */     } 
/* 253 */     return null;
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 257 */     return (this._types.length == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 264 */     return this._types.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getBoundName(int index) {
/* 269 */     if (index < 0 || index >= this._names.length) {
/* 270 */       return null;
/*     */     }
/* 272 */     return this._names[index];
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType getBoundType(int index) {
/* 277 */     if (index < 0 || index >= this._types.length) {
/* 278 */       return null;
/*     */     }
/* 280 */     return this._types[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<JavaType> getTypeParameters() {
/* 288 */     if (this._types.length == 0) {
/* 289 */       return Collections.emptyList();
/*     */     }
/* 291 */     return Arrays.asList(this._types);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasUnbound(String name) {
/* 298 */     if (this._unboundVariables != null) {
/* 299 */       for (int i = this._unboundVariables.length; --i >= 0;) {
/* 300 */         if (name.equals(this._unboundVariables[i])) {
/* 301 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/* 305 */     return false;
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
/*     */   public Object asKey(Class<?> rawBase) {
/* 317 */     return new AsKey(rawBase, this._types, this._hashCode);
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
/* 328 */     if (this._types.length == 0) {
/* 329 */       return "<>";
/*     */     }
/* 331 */     StringBuilder sb = new StringBuilder();
/* 332 */     sb.append('<');
/* 333 */     for (int i = 0, len = this._types.length; i < len; i++) {
/* 334 */       if (i > 0) {
/* 335 */         sb.append(',');
/*     */       }
/*     */       
/* 338 */       String sig = this._types[i].getGenericSignature();
/* 339 */       sb.append(sig);
/*     */     } 
/* 341 */     sb.append('>');
/* 342 */     return sb.toString();
/*     */   }
/*     */   public int hashCode() {
/* 345 */     return this._hashCode;
/*     */   }
/*     */   
/*     */   public boolean equals(Object o) {
/* 349 */     if (o == this) return true; 
/* 350 */     if (!ClassUtil.hasClass(o, getClass())) {
/* 351 */       return false;
/*     */     }
/* 353 */     TypeBindings other = (TypeBindings)o;
/* 354 */     int len = this._types.length;
/* 355 */     if (len != other.size()) {
/* 356 */       return false;
/*     */     }
/* 358 */     JavaType[] otherTypes = other._types;
/* 359 */     for (int i = 0; i < len; i++) {
/* 360 */       if (!otherTypes[i].equals(this._types[i])) {
/* 361 */         return false;
/*     */       }
/*     */     } 
/* 364 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JavaType[] typeParameterArray() {
/* 374 */     return this._types;
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
/*     */   static class TypeParamStash
/*     */   {
/* 395 */     private static final TypeVariable<?>[] VARS_ABSTRACT_LIST = (TypeVariable<?>[])AbstractList.class.getTypeParameters();
/* 396 */     private static final TypeVariable<?>[] VARS_COLLECTION = (TypeVariable<?>[])Collection.class.getTypeParameters();
/* 397 */     private static final TypeVariable<?>[] VARS_ITERABLE = (TypeVariable<?>[])Iterable.class.getTypeParameters();
/* 398 */     private static final TypeVariable<?>[] VARS_LIST = (TypeVariable<?>[])List.class.getTypeParameters();
/* 399 */     private static final TypeVariable<?>[] VARS_ARRAY_LIST = (TypeVariable<?>[])ArrayList.class.getTypeParameters();
/*     */     
/* 401 */     private static final TypeVariable<?>[] VARS_MAP = (TypeVariable<?>[])Map.class.getTypeParameters();
/* 402 */     private static final TypeVariable<?>[] VARS_HASH_MAP = (TypeVariable<?>[])HashMap.class.getTypeParameters();
/* 403 */     private static final TypeVariable<?>[] VARS_LINKED_HASH_MAP = (TypeVariable<?>[])LinkedHashMap.class.getTypeParameters();
/*     */ 
/*     */     
/*     */     public static TypeVariable<?>[] paramsFor1(Class<?> erasedType) {
/* 407 */       if (erasedType == Collection.class) {
/* 408 */         return VARS_COLLECTION;
/*     */       }
/* 410 */       if (erasedType == List.class) {
/* 411 */         return VARS_LIST;
/*     */       }
/* 413 */       if (erasedType == ArrayList.class) {
/* 414 */         return VARS_ARRAY_LIST;
/*     */       }
/* 416 */       if (erasedType == AbstractList.class) {
/* 417 */         return VARS_ABSTRACT_LIST;
/*     */       }
/* 419 */       if (erasedType == Iterable.class) {
/* 420 */         return VARS_ITERABLE;
/*     */       }
/* 422 */       return (TypeVariable<?>[])erasedType.getTypeParameters();
/*     */     }
/*     */ 
/*     */     
/*     */     public static TypeVariable<?>[] paramsFor2(Class<?> erasedType) {
/* 427 */       if (erasedType == Map.class) {
/* 428 */         return VARS_MAP;
/*     */       }
/* 430 */       if (erasedType == HashMap.class) {
/* 431 */         return VARS_HASH_MAP;
/*     */       }
/* 433 */       if (erasedType == LinkedHashMap.class) {
/* 434 */         return VARS_LINKED_HASH_MAP;
/*     */       }
/* 436 */       return (TypeVariable<?>[])erasedType.getTypeParameters();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static final class AsKey
/*     */   {
/*     */     private final Class<?> _raw;
/*     */     
/*     */     private final JavaType[] _params;
/*     */     
/*     */     private final int _hash;
/*     */ 
/*     */     
/*     */     public AsKey(Class<?> raw, JavaType[] params, int hash) {
/* 451 */       this._raw = raw;
/* 452 */       this._params = params;
/* 453 */       this._hash = hash;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 457 */       return this._hash;
/*     */     }
/*     */     
/*     */     public boolean equals(Object o) {
/* 461 */       if (o == this) return true; 
/* 462 */       if (o == null) return false; 
/* 463 */       if (o.getClass() != getClass()) return false; 
/* 464 */       AsKey other = (AsKey)o;
/*     */       
/* 466 */       if (this._hash == other._hash && this._raw == other._raw) {
/* 467 */         JavaType[] otherParams = other._params;
/* 468 */         int len = this._params.length;
/*     */         
/* 470 */         if (len == otherParams.length) {
/* 471 */           for (int i = 0; i < len; i++) {
/* 472 */             if (!this._params[i].equals(otherParams[i])) {
/* 473 */               return false;
/*     */             }
/*     */           } 
/* 476 */           return true;
/*     */         } 
/*     */       } 
/* 479 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 484 */       return this._raw.getName() + "<>";
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/type/TypeBindings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */