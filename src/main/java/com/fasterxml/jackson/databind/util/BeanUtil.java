/*     */ package com.fasterxml.jackson.databind.util;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonInclude;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BeanUtil
/*     */ {
/*     */   @Deprecated
/*     */   public static String okNameForGetter(AnnotatedMethod am, boolean stdNaming) {
/*  30 */     String name = am.getName();
/*  31 */     String str = okNameForIsGetter(am, name, stdNaming);
/*  32 */     if (str == null) {
/*  33 */       str = okNameForRegularGetter(am, name, stdNaming);
/*     */     }
/*  35 */     return str;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static String okNameForRegularGetter(AnnotatedMethod am, String name, boolean stdNaming) {
/*  47 */     if (name.startsWith("get")) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  55 */       if ("getCallbacks".equals(name)) {
/*  56 */         if (isCglibGetCallbacks(am)) {
/*  57 */           return null;
/*     */         }
/*  59 */       } else if ("getMetaClass".equals(name)) {
/*     */         
/*  61 */         if (isGroovyMetaClassGetter(am)) {
/*  62 */           return null;
/*     */         }
/*     */       } 
/*  65 */       return stdNaming ? 
/*  66 */         stdManglePropertyName(name, 3) : 
/*  67 */         legacyManglePropertyName(name, 3);
/*     */     } 
/*  69 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static String okNameForIsGetter(AnnotatedMethod am, String name, boolean stdNaming) {
/*  81 */     if (name.startsWith("is")) {
/*  82 */       Class<?> rt = am.getRawType();
/*  83 */       if (rt == Boolean.class || rt == boolean.class) {
/*  84 */         return stdNaming ? 
/*  85 */           stdManglePropertyName(name, 2) : 
/*  86 */           legacyManglePropertyName(name, 2);
/*     */       }
/*     */     } 
/*  89 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static String okNameForSetter(AnnotatedMethod am, boolean stdNaming) {
/*  96 */     return okNameForMutator(am, "set", stdNaming);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static String okNameForMutator(AnnotatedMethod am, String prefix, boolean stdNaming) {
/* 107 */     String name = am.getName();
/* 108 */     if (name.startsWith(prefix)) {
/* 109 */       return stdNaming ? 
/* 110 */         stdManglePropertyName(name, prefix.length()) : 
/* 111 */         legacyManglePropertyName(name, prefix.length());
/*     */     }
/* 113 */     return null;
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
/*     */   public static Object getDefaultValue(JavaType type) {
/* 139 */     Class<?> cls = type.getRawClass();
/*     */ 
/*     */ 
/*     */     
/* 143 */     Class<?> prim = ClassUtil.primitiveType(cls);
/* 144 */     if (prim != null) {
/* 145 */       return ClassUtil.defaultValue(prim);
/*     */     }
/* 147 */     if (type.isContainerType() || type.isReferenceType()) {
/* 148 */       return JsonInclude.Include.NON_EMPTY;
/*     */     }
/* 150 */     if (cls == String.class) {
/* 151 */       return "";
/*     */     }
/*     */ 
/*     */     
/* 155 */     if (type.isTypeOrSubTypeOf(Date.class)) {
/* 156 */       return new Date(0L);
/*     */     }
/* 158 */     if (type.isTypeOrSubTypeOf(Calendar.class)) {
/* 159 */       Calendar c = new GregorianCalendar();
/* 160 */       c.setTimeInMillis(0L);
/* 161 */       return c;
/*     */     } 
/* 163 */     return null;
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
/*     */   protected static boolean isCglibGetCallbacks(AnnotatedMethod am) {
/* 182 */     Class<?> rt = am.getRawType();
/*     */     
/* 184 */     if (rt.isArray()) {
/*     */ 
/*     */ 
/*     */       
/* 188 */       Class<?> compType = rt.getComponentType();
/*     */       
/* 190 */       String className = compType.getName();
/* 191 */       if (className.contains(".cglib")) {
/* 192 */         return (className.startsWith("net.sf.cglib") || className
/*     */           
/* 194 */           .startsWith("org.hibernate.repackage.cglib") || className
/*     */           
/* 196 */           .startsWith("org.springframework.cglib"));
/*     */       }
/*     */     } 
/* 199 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static boolean isGroovyMetaClassGetter(AnnotatedMethod am) {
/* 206 */     return am.getRawType().getName().startsWith("groovy.lang");
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
/*     */   protected static String legacyManglePropertyName(String basename, int offset) {
/* 224 */     int end = basename.length();
/* 225 */     if (end == offset) {
/* 226 */       return null;
/*     */     }
/*     */     
/* 229 */     char c = basename.charAt(offset);
/* 230 */     char d = Character.toLowerCase(c);
/*     */     
/* 232 */     if (c == d) {
/* 233 */       return basename.substring(offset);
/*     */     }
/*     */     
/* 236 */     StringBuilder sb = new StringBuilder(end - offset);
/* 237 */     sb.append(d);
/* 238 */     int i = offset + 1;
/* 239 */     for (; i < end; i++) {
/* 240 */       c = basename.charAt(i);
/* 241 */       d = Character.toLowerCase(c);
/* 242 */       if (c == d) {
/* 243 */         sb.append(basename, i, end);
/*     */         break;
/*     */       } 
/* 246 */       sb.append(d);
/*     */     } 
/* 248 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String stdManglePropertyName(String basename, int offset) {
/* 258 */     int end = basename.length();
/* 259 */     if (end == offset) {
/* 260 */       return null;
/*     */     }
/*     */     
/* 263 */     char c0 = basename.charAt(offset);
/* 264 */     char c1 = Character.toLowerCase(c0);
/* 265 */     if (c0 == c1) {
/* 266 */       return basename.substring(offset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 271 */     if (offset + 1 < end && 
/* 272 */       Character.isUpperCase(basename.charAt(offset + 1))) {
/* 273 */       return basename.substring(offset);
/*     */     }
/*     */     
/* 276 */     StringBuilder sb = new StringBuilder(end - offset);
/* 277 */     sb.append(c1);
/* 278 */     sb.append(basename, offset + 1, end);
/* 279 */     return sb.toString();
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
/*     */   public static String checkUnsupportedType(JavaType type) {
/* 298 */     String typeName, moduleName, className = type.getRawClass().getName();
/*     */ 
/*     */     
/* 301 */     if (isJava8TimeClass(className)) {
/*     */ 
/*     */       
/* 304 */       if (className.indexOf('.', 10) >= 0) {
/* 305 */         return null;
/*     */       }
/* 307 */       typeName = "Java 8 date/time";
/* 308 */       moduleName = "com.fasterxml.jackson.datatype:jackson-datatype-jsr310";
/* 309 */     } else if (isJodaTimeClass(className)) {
/* 310 */       typeName = "Joda date/time";
/* 311 */       moduleName = "com.fasterxml.jackson.datatype:jackson-datatype-joda";
/*     */     } else {
/* 313 */       return null;
/*     */     } 
/* 315 */     return String.format("%s type %s not supported by default: add Module \"%s\" to enable handling", new Object[] { typeName, 
/* 316 */           ClassUtil.getTypeDescription(type), moduleName });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isJava8TimeClass(Class<?> rawType) {
/* 323 */     return isJava8TimeClass(rawType.getName());
/*     */   }
/*     */   
/*     */   private static boolean isJava8TimeClass(String className) {
/* 327 */     return className.startsWith("java.time.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isJodaTimeClass(Class<?> rawType) {
/* 334 */     return isJodaTimeClass(rawType.getName());
/*     */   }
/*     */   
/*     */   private static boolean isJodaTimeClass(String className) {
/* 338 */     return className.startsWith("org.joda.time.");
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/util/BeanUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */