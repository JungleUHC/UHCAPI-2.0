/*     */ package com.fasterxml.jackson.databind.type;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.type.ResolvedType;
/*     */ import com.fasterxml.jackson.core.type.WritableTypeId;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonSerializable;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ public abstract class TypeBase
/*     */   extends JavaType
/*     */   implements JsonSerializable {
/*     */   private static final long serialVersionUID = 1L;
/*  21 */   private static final TypeBindings NO_BINDINGS = TypeBindings.emptyBindings();
/*  22 */   private static final JavaType[] NO_TYPES = new JavaType[0];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JavaType _superClass;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JavaType[] _superInterfaces;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final TypeBindings _bindings;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   volatile transient String _canonicalName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TypeBase(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInts, int hash, Object valueHandler, Object typeHandler, boolean asStatic) {
/*  49 */     super(raw, hash, valueHandler, typeHandler, asStatic);
/*  50 */     this._bindings = (bindings == null) ? NO_BINDINGS : bindings;
/*  51 */     this._superClass = superClass;
/*  52 */     this._superInterfaces = superInts;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TypeBase(TypeBase base) {
/*  61 */     super(base);
/*  62 */     this._superClass = base._superClass;
/*  63 */     this._superInterfaces = base._superInterfaces;
/*  64 */     this._bindings = base._bindings;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toCanonical() {
/*  70 */     String str = this._canonicalName;
/*  71 */     if (str == null) {
/*  72 */       str = buildCanonicalName();
/*     */     }
/*  74 */     return str;
/*     */   }
/*     */   
/*     */   protected String buildCanonicalName() {
/*  78 */     return this._class.getName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeBindings getBindings() {
/*  89 */     return this._bindings;
/*     */   }
/*     */ 
/*     */   
/*     */   public int containedTypeCount() {
/*  94 */     return this._bindings.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType containedType(int index) {
/*  99 */     return this._bindings.getBoundType(index);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String containedTypeName(int index) {
/* 105 */     return this._bindings.getBoundName(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType getSuperClass() {
/* 110 */     return this._superClass;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<JavaType> getInterfaces() {
/* 115 */     if (this._superInterfaces == null) {
/* 116 */       return Collections.emptyList();
/*     */     }
/* 118 */     switch (this._superInterfaces.length) {
/*     */       case 0:
/* 120 */         return Collections.emptyList();
/*     */       case 1:
/* 122 */         return Collections.singletonList(this._superInterfaces[0]);
/*     */     } 
/* 124 */     return Arrays.asList(this._superInterfaces);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final JavaType findSuperType(Class<?> rawTarget) {
/* 130 */     if (rawTarget == this._class) {
/* 131 */       return this;
/*     */     }
/*     */     
/* 134 */     if (rawTarget.isInterface() && this._superInterfaces != null) {
/* 135 */       for (int i = 0, count = this._superInterfaces.length; i < count; i++) {
/* 136 */         JavaType type = this._superInterfaces[i].findSuperType(rawTarget);
/* 137 */         if (type != null) {
/* 138 */           return type;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 143 */     if (this._superClass != null) {
/* 144 */       JavaType type = this._superClass.findSuperType(rawTarget);
/* 145 */       if (type != null) {
/* 146 */         return type;
/*     */       }
/*     */     } 
/* 149 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType[] findTypeParameters(Class<?> expType) {
/* 155 */     JavaType match = findSuperType(expType);
/* 156 */     if (match == null) {
/* 157 */       return NO_TYPES;
/*     */     }
/* 159 */     return match.getBindings().typeParameterArray();
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
/*     */   public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/* 173 */     WritableTypeId typeIdDef = new WritableTypeId(this, JsonToken.VALUE_STRING);
/* 174 */     typeSer.writeTypePrefix(g, typeIdDef);
/* 175 */     serialize(g, provider);
/* 176 */     typeSer.writeTypeSuffix(g, typeIdDef);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 183 */     gen.writeString(toCanonical());
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
/*     */   protected static StringBuilder _classSignature(Class<?> cls, StringBuilder sb, boolean trailingSemicolon) {
/* 199 */     if (cls.isPrimitive()) {
/* 200 */       if (cls == boolean.class) {
/* 201 */         sb.append('Z');
/* 202 */       } else if (cls == byte.class) {
/* 203 */         sb.append('B');
/*     */       }
/* 205 */       else if (cls == short.class) {
/* 206 */         sb.append('S');
/*     */       }
/* 208 */       else if (cls == char.class) {
/* 209 */         sb.append('C');
/*     */       }
/* 211 */       else if (cls == int.class) {
/* 212 */         sb.append('I');
/*     */       }
/* 214 */       else if (cls == long.class) {
/* 215 */         sb.append('J');
/*     */       }
/* 217 */       else if (cls == float.class) {
/* 218 */         sb.append('F');
/*     */       }
/* 220 */       else if (cls == double.class) {
/* 221 */         sb.append('D');
/*     */       }
/* 223 */       else if (cls == void.class) {
/* 224 */         sb.append('V');
/*     */       } else {
/* 226 */         throw new IllegalStateException("Unrecognized primitive type: " + cls.getName());
/*     */       } 
/*     */     } else {
/* 229 */       sb.append('L');
/* 230 */       String name = cls.getName();
/* 231 */       for (int i = 0, len = name.length(); i < len; i++) {
/* 232 */         char c = name.charAt(i);
/* 233 */         if (c == '.') c = '/'; 
/* 234 */         sb.append(c);
/*     */       } 
/* 236 */       if (trailingSemicolon) {
/* 237 */         sb.append(';');
/*     */       }
/*     */     } 
/* 240 */     return sb;
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
/*     */   protected static JavaType _bogusSuperClass(Class<?> cls) {
/* 253 */     Class<?> parent = cls.getSuperclass();
/* 254 */     if (parent == null) {
/* 255 */       return null;
/*     */     }
/* 257 */     return TypeFactory.unknownType();
/*     */   }
/*     */   
/*     */   protected boolean _hasNTypeParameters(int count) {
/* 261 */     TypeVariable[] arrayOfTypeVariable = this._class.getTypeParameters();
/* 262 */     return (arrayOfTypeVariable.length == count);
/*     */   }
/*     */   
/*     */   public abstract StringBuilder getGenericSignature(StringBuilder paramStringBuilder);
/*     */   
/*     */   public abstract StringBuilder getErasedSignature(StringBuilder paramStringBuilder);
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/type/TypeBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */