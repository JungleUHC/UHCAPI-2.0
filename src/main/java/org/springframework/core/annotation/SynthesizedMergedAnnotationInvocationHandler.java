/*     */ package org.springframework.core.annotation;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ import org.springframework.util.ReflectionUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class SynthesizedMergedAnnotationInvocationHandler<A extends Annotation>
/*     */   implements InvocationHandler
/*     */ {
/*     */   private final MergedAnnotation<?> annotation;
/*     */   private final Class<A> type;
/*     */   private final AttributeMethods attributes;
/*  56 */   private final Map<String, Object> valueCache = new ConcurrentHashMap<>(8);
/*     */   
/*     */   @Nullable
/*     */   private volatile Integer hashCode;
/*     */   
/*     */   @Nullable
/*     */   private volatile String string;
/*     */ 
/*     */   
/*     */   private SynthesizedMergedAnnotationInvocationHandler(MergedAnnotation<A> annotation, Class<A> type) {
/*  66 */     Assert.notNull(annotation, "MergedAnnotation must not be null");
/*  67 */     Assert.notNull(type, "Type must not be null");
/*  68 */     Assert.isTrue(type.isAnnotation(), "Type must be an annotation");
/*  69 */     this.annotation = annotation;
/*  70 */     this.type = type;
/*  71 */     this.attributes = AttributeMethods.forAnnotationType(type);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object invoke(Object proxy, Method method, Object[] args) {
/*  77 */     if (ReflectionUtils.isEqualsMethod(method)) {
/*  78 */       return Boolean.valueOf(annotationEquals(args[0]));
/*     */     }
/*  80 */     if (ReflectionUtils.isHashCodeMethod(method)) {
/*  81 */       return Integer.valueOf(annotationHashCode());
/*     */     }
/*  83 */     if (ReflectionUtils.isToStringMethod(method)) {
/*  84 */       return annotationToString();
/*     */     }
/*  86 */     if (isAnnotationTypeMethod(method)) {
/*  87 */       return this.type;
/*     */     }
/*  89 */     if (this.attributes.indexOf(method.getName()) != -1) {
/*  90 */       return getAttributeValue(method);
/*     */     }
/*  92 */     throw new AnnotationConfigurationException(String.format("Method [%s] is unsupported for synthesized annotation type [%s]", new Object[] { method, this.type }));
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isAnnotationTypeMethod(Method method) {
/*  97 */     return (method.getName().equals("annotationType") && method.getParameterCount() == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean annotationEquals(Object other) {
/* 105 */     if (this == other) {
/* 106 */       return true;
/*     */     }
/* 108 */     if (!this.type.isInstance(other)) {
/* 109 */       return false;
/*     */     }
/* 111 */     for (int i = 0; i < this.attributes.size(); i++) {
/* 112 */       Method attribute = this.attributes.get(i);
/* 113 */       Object thisValue = getAttributeValue(attribute);
/* 114 */       Object otherValue = ReflectionUtils.invokeMethod(attribute, other);
/* 115 */       if (!ObjectUtils.nullSafeEquals(thisValue, otherValue)) {
/* 116 */         return false;
/*     */       }
/*     */     } 
/* 119 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int annotationHashCode() {
/* 126 */     Integer hashCode = this.hashCode;
/* 127 */     if (hashCode == null) {
/* 128 */       hashCode = computeHashCode();
/* 129 */       this.hashCode = hashCode;
/*     */     } 
/* 131 */     return hashCode.intValue();
/*     */   }
/*     */   
/*     */   private Integer computeHashCode() {
/* 135 */     int hashCode = 0;
/* 136 */     for (int i = 0; i < this.attributes.size(); i++) {
/* 137 */       Method attribute = this.attributes.get(i);
/* 138 */       Object value = getAttributeValue(attribute);
/* 139 */       hashCode += 127 * attribute.getName().hashCode() ^ getValueHashCode(value);
/*     */     } 
/* 141 */     return Integer.valueOf(hashCode);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private int getValueHashCode(Object value) {
/* 147 */     if (value instanceof boolean[]) {
/* 148 */       return Arrays.hashCode((boolean[])value);
/*     */     }
/* 150 */     if (value instanceof byte[]) {
/* 151 */       return Arrays.hashCode((byte[])value);
/*     */     }
/* 153 */     if (value instanceof char[]) {
/* 154 */       return Arrays.hashCode((char[])value);
/*     */     }
/* 156 */     if (value instanceof double[]) {
/* 157 */       return Arrays.hashCode((double[])value);
/*     */     }
/* 159 */     if (value instanceof float[]) {
/* 160 */       return Arrays.hashCode((float[])value);
/*     */     }
/* 162 */     if (value instanceof int[]) {
/* 163 */       return Arrays.hashCode((int[])value);
/*     */     }
/* 165 */     if (value instanceof long[]) {
/* 166 */       return Arrays.hashCode((long[])value);
/*     */     }
/* 168 */     if (value instanceof short[]) {
/* 169 */       return Arrays.hashCode((short[])value);
/*     */     }
/* 171 */     if (value instanceof Object[]) {
/* 172 */       return Arrays.hashCode((Object[])value);
/*     */     }
/* 174 */     return value.hashCode();
/*     */   }
/*     */   
/*     */   private String annotationToString() {
/* 178 */     String string = this.string;
/* 179 */     if (string == null) {
/* 180 */       StringBuilder builder = (new StringBuilder("@")).append(getName(this.type)).append('(');
/* 181 */       for (int i = 0; i < this.attributes.size(); i++) {
/* 182 */         Method attribute = this.attributes.get(i);
/* 183 */         if (i > 0) {
/* 184 */           builder.append(", ");
/*     */         }
/* 186 */         builder.append(attribute.getName());
/* 187 */         builder.append('=');
/* 188 */         builder.append(toString(getAttributeValue(attribute)));
/*     */       } 
/* 190 */       builder.append(')');
/* 191 */       string = builder.toString();
/* 192 */       this.string = string;
/*     */     } 
/* 194 */     return string;
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
/*     */   private String toString(Object value) {
/* 211 */     if (value instanceof String) {
/* 212 */       return '"' + value.toString() + '"';
/*     */     }
/* 214 */     if (value instanceof Character) {
/* 215 */       return '\'' + value.toString() + '\'';
/*     */     }
/* 217 */     if (value instanceof Byte) {
/* 218 */       return String.format("(byte) 0x%02X", new Object[] { value });
/*     */     }
/* 220 */     if (value instanceof Long) {
/* 221 */       return Long.toString(((Long)value).longValue()) + 'L';
/*     */     }
/* 223 */     if (value instanceof Float) {
/* 224 */       return Float.toString(((Float)value).floatValue()) + 'f';
/*     */     }
/* 226 */     if (value instanceof Double) {
/* 227 */       return Double.toString(((Double)value).doubleValue()) + 'd';
/*     */     }
/* 229 */     if (value instanceof Enum) {
/* 230 */       return ((Enum)value).name();
/*     */     }
/* 232 */     if (value instanceof Class) {
/* 233 */       return getName((Class)value) + ".class";
/*     */     }
/* 235 */     if (value.getClass().isArray()) {
/* 236 */       StringBuilder builder = new StringBuilder("{");
/* 237 */       for (int i = 0; i < Array.getLength(value); i++) {
/* 238 */         if (i > 0) {
/* 239 */           builder.append(", ");
/*     */         }
/* 241 */         builder.append(toString(Array.get(value, i)));
/*     */       } 
/* 243 */       builder.append('}');
/* 244 */       return builder.toString();
/*     */     } 
/* 246 */     return String.valueOf(value);
/*     */   }
/*     */   
/*     */   private Object getAttributeValue(Method method) {
/* 250 */     Object value = this.valueCache.computeIfAbsent(method.getName(), attributeName -> {
/*     */           Class<?> type = ClassUtils.resolvePrimitiveIfNecessary(method.getReturnType());
/*     */ 
/*     */           
/*     */           return this.annotation.getValue(attributeName, type).orElseThrow(());
/*     */         });
/*     */ 
/*     */     
/* 258 */     if (value.getClass().isArray() && Array.getLength(value) > 0) {
/* 259 */       value = cloneArray(value);
/*     */     }
/*     */     
/* 262 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object cloneArray(Object array) {
/* 270 */     if (array instanceof boolean[]) {
/* 271 */       return ((boolean[])array).clone();
/*     */     }
/* 273 */     if (array instanceof byte[]) {
/* 274 */       return ((byte[])array).clone();
/*     */     }
/* 276 */     if (array instanceof char[]) {
/* 277 */       return ((char[])array).clone();
/*     */     }
/* 279 */     if (array instanceof double[]) {
/* 280 */       return ((double[])array).clone();
/*     */     }
/* 282 */     if (array instanceof float[]) {
/* 283 */       return ((float[])array).clone();
/*     */     }
/* 285 */     if (array instanceof int[]) {
/* 286 */       return ((int[])array).clone();
/*     */     }
/* 288 */     if (array instanceof long[]) {
/* 289 */       return ((long[])array).clone();
/*     */     }
/* 291 */     if (array instanceof short[]) {
/* 292 */       return ((short[])array).clone();
/*     */     }
/*     */ 
/*     */     
/* 296 */     return ((Object[])array).clone();
/*     */   }
/*     */ 
/*     */   
/*     */   static <A extends Annotation> A createProxy(MergedAnnotation<A> annotation, Class<A> type) {
/* 301 */     ClassLoader classLoader = type.getClassLoader();
/* 302 */     InvocationHandler<A> handler = new SynthesizedMergedAnnotationInvocationHandler<>(annotation, type);
/* 303 */     (new Class[2])[0] = type; (new Class[2])[1] = SynthesizedAnnotation.class; (new Class[1])[0] = type; Class<?>[] interfaces = isVisible(classLoader, SynthesizedAnnotation.class) ? new Class[2] : new Class[1];
/*     */     
/* 305 */     return (A)Proxy.newProxyInstance(classLoader, interfaces, handler);
/*     */   }
/*     */   
/*     */   private static String getName(Class<?> clazz) {
/* 309 */     String canonicalName = clazz.getCanonicalName();
/* 310 */     return (canonicalName != null) ? canonicalName : clazz.getName();
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isVisible(ClassLoader classLoader, Class<?> interfaceClass) {
/* 315 */     if (classLoader == interfaceClass.getClassLoader()) {
/* 316 */       return true;
/*     */     }
/*     */     try {
/* 319 */       return (Class.forName(interfaceClass.getName(), false, classLoader) == interfaceClass);
/*     */     }
/* 321 */     catch (ClassNotFoundException ex) {
/* 322 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/annotation/SynthesizedMergedAnnotationInvocationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */