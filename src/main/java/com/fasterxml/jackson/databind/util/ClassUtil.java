/*      */ package com.fasterxml.jackson.databind.util;
/*      */ import com.fasterxml.jackson.core.JsonGenerator;
/*      */ import com.fasterxml.jackson.databind.JavaType;
/*      */ import java.io.Closeable;
/*      */ import java.io.IOException;
/*      */ import java.lang.annotation.Annotation;
/*      */ import java.lang.reflect.AccessibleObject;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Member;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.EnumMap;
/*      */ import java.util.EnumSet;
/*      */ import java.util.List;
/*      */ 
/*      */ public final class ClassUtil {
/*   20 */   private static final Class<?> CLS_OBJECT = Object.class;
/*      */   
/*   22 */   private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];
/*   23 */   private static final Ctor[] NO_CTORS = new Ctor[0];
/*      */   
/*   25 */   private static final Iterator<?> EMPTY_ITERATOR = Collections.emptyIterator();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> Iterator<T> emptyIterator() {
/*   38 */     return (Iterator)EMPTY_ITERATOR;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<JavaType> findSuperTypes(JavaType type, Class<?> endBefore, boolean addClassItself) {
/*   63 */     if (type == null || type.hasRawClass(endBefore) || type.hasRawClass(Object.class)) {
/*   64 */       return Collections.emptyList();
/*      */     }
/*   66 */     List<JavaType> result = new ArrayList<>(8);
/*   67 */     _addSuperTypes(type, endBefore, result, addClassItself);
/*   68 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<Class<?>> findRawSuperTypes(Class<?> cls, Class<?> endBefore, boolean addClassItself) {
/*   75 */     if (cls == null || cls == endBefore || cls == Object.class) {
/*   76 */       return Collections.emptyList();
/*      */     }
/*   78 */     List<Class<?>> result = new ArrayList<>(8);
/*   79 */     _addRawSuperTypes(cls, endBefore, result, addClassItself);
/*   80 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<Class<?>> findSuperClasses(Class<?> cls, Class<?> endBefore, boolean addClassItself) {
/*   95 */     List<Class<?>> result = new ArrayList<>(8);
/*   96 */     if (cls != null && cls != endBefore) {
/*   97 */       if (addClassItself) {
/*   98 */         result.add(cls);
/*      */       }
/*  100 */       while ((cls = cls.getSuperclass()) != null && 
/*  101 */         cls != endBefore)
/*      */       {
/*      */         
/*  104 */         result.add(cls);
/*      */       }
/*      */     } 
/*  107 */     return result;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore) {
/*  112 */     return findSuperTypes(cls, endBefore, new ArrayList<>(8));
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore, List<Class<?>> result) {
/*  117 */     _addRawSuperTypes(cls, endBefore, result, false);
/*  118 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static void _addSuperTypes(JavaType type, Class<?> endBefore, Collection<JavaType> result, boolean addClassItself) {
/*  124 */     if (type == null) {
/*      */       return;
/*      */     }
/*  127 */     Class<?> cls = type.getRawClass();
/*  128 */     if (cls == endBefore || cls == Object.class)
/*  129 */       return;  if (addClassItself) {
/*  130 */       if (result.contains(type)) {
/*      */         return;
/*      */       }
/*  133 */       result.add(type);
/*      */     } 
/*  135 */     for (JavaType intCls : type.getInterfaces()) {
/*  136 */       _addSuperTypes(intCls, endBefore, result, true);
/*      */     }
/*  138 */     _addSuperTypes(type.getSuperClass(), endBefore, result, true);
/*      */   }
/*      */   
/*      */   private static void _addRawSuperTypes(Class<?> cls, Class<?> endBefore, Collection<Class<?>> result, boolean addClassItself) {
/*  142 */     if (cls == endBefore || cls == null || cls == Object.class)
/*  143 */       return;  if (addClassItself) {
/*  144 */       if (result.contains(cls)) {
/*      */         return;
/*      */       }
/*  147 */       result.add(cls);
/*      */     } 
/*  149 */     for (Class<?> intCls : _interfaces(cls)) {
/*  150 */       _addRawSuperTypes(intCls, endBefore, result, true);
/*      */     }
/*  152 */     _addRawSuperTypes(cls.getSuperclass(), endBefore, result, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String canBeABeanType(Class<?> type) {
/*  168 */     if (type.isAnnotation()) {
/*  169 */       return "annotation";
/*      */     }
/*  171 */     if (type.isArray()) {
/*  172 */       return "array";
/*      */     }
/*  174 */     if (Enum.class.isAssignableFrom(type)) {
/*  175 */       return "enum";
/*      */     }
/*  177 */     if (type.isPrimitive()) {
/*  178 */       return "primitive";
/*      */     }
/*      */ 
/*      */     
/*  182 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String isLocalType(Class<?> type, boolean allowNonStatic) {
/*      */     
/*  192 */     try { boolean isStatic = Modifier.isStatic(type.getModifiers());
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  197 */       if (!isStatic && hasEnclosingMethod(type)) {
/*  198 */         return "local/anonymous";
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  204 */       if (!allowNonStatic && 
/*  205 */         !isStatic && getEnclosingClass(type) != null) {
/*  206 */         return "non-static member class";
/*      */       
/*      */       } }
/*      */     
/*  210 */     catch (SecurityException securityException) {  }
/*  211 */     catch (NullPointerException nullPointerException) {}
/*  212 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?> getOuterClass(Class<?> type) {
/*  221 */     if (!Modifier.isStatic(type.getModifiers())) {
/*      */       
/*      */       try {
/*  224 */         if (hasEnclosingMethod(type)) {
/*  225 */           return null;
/*      */         }
/*  227 */         return getEnclosingClass(type);
/*  228 */       } catch (SecurityException securityException) {}
/*      */     }
/*  230 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isProxyType(Class<?> type) {
/*  247 */     String name = type.getName();
/*      */     
/*  249 */     if (name.startsWith("net.sf.cglib.proxy.") || name
/*  250 */       .startsWith("org.hibernate.proxy.")) {
/*  251 */       return true;
/*      */     }
/*      */     
/*  254 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isConcrete(Class<?> type) {
/*  263 */     int mod = type.getModifiers();
/*  264 */     return ((mod & 0x600) == 0);
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean isConcrete(Member member) {
/*  269 */     int mod = member.getModifiers();
/*  270 */     return ((mod & 0x600) == 0);
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean isCollectionMapOrArray(Class<?> type) {
/*  275 */     if (type.isArray()) return true; 
/*  276 */     if (Collection.class.isAssignableFrom(type)) return true; 
/*  277 */     if (Map.class.isAssignableFrom(type)) return true; 
/*  278 */     return false;
/*      */   }
/*      */   
/*      */   public static boolean isBogusClass(Class<?> cls) {
/*  282 */     return (cls == Void.class || cls == void.class || cls == NoClass.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isRecordType(Class<?> cls) {
/*  292 */     Class<?> parent = cls.getSuperclass();
/*  293 */     return (parent != null && "java.lang.Record".equals(parent.getName()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isObjectOrPrimitive(Class<?> cls) {
/*  300 */     return (cls == CLS_OBJECT || cls.isPrimitive());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean hasClass(Object inst, Class<?> raw) {
/*  309 */     return (inst != null && inst.getClass() == raw);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void verifyMustOverride(Class<?> expType, Object instance, String method) {
/*  318 */     if (instance.getClass() != expType) {
/*  319 */       throw new IllegalStateException(String.format("Sub-class %s (of class %s) must override method '%s'", new Object[] { instance
/*      */               
/*  321 */               .getClass().getName(), expType.getName(), method }));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static boolean hasGetterSignature(Method m) {
/*  338 */     if (Modifier.isStatic(m.getModifiers())) {
/*  339 */       return false;
/*      */     }
/*      */     
/*  342 */     Class<?>[] pts = m.getParameterTypes();
/*  343 */     if (pts != null && pts.length != 0) {
/*  344 */       return false;
/*      */     }
/*      */     
/*  347 */     if (void.class == m.getReturnType()) {
/*  348 */       return false;
/*      */     }
/*      */     
/*  351 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Throwable throwIfError(Throwable t) {
/*  367 */     if (t instanceof Error) {
/*  368 */       throw (Error)t;
/*      */     }
/*  370 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Throwable throwIfRTE(Throwable t) {
/*  380 */     if (t instanceof RuntimeException) {
/*  381 */       throw (RuntimeException)t;
/*      */     }
/*  383 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Throwable throwIfIOE(Throwable t) throws IOException {
/*  393 */     if (t instanceof IOException) {
/*  394 */       throw (IOException)t;
/*      */     }
/*  396 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Throwable getRootCause(Throwable t) {
/*  411 */     while (t.getCause() != null) {
/*  412 */       t = t.getCause();
/*      */     }
/*  414 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Throwable throwRootCauseIfIOE(Throwable t) throws IOException {
/*  425 */     return throwIfIOE(getRootCause(t));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void throwAsIAE(Throwable t) {
/*  433 */     throwAsIAE(t, t.getMessage());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void throwAsIAE(Throwable t, String msg) {
/*  443 */     throwIfRTE(t);
/*  444 */     throwIfError(t);
/*  445 */     throw new IllegalArgumentException(msg, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T throwAsMappingException(DeserializationContext ctxt, IOException e0) throws JsonMappingException {
/*  453 */     if (e0 instanceof JsonMappingException) {
/*  454 */       throw (JsonMappingException)e0;
/*      */     }
/*  456 */     throw JsonMappingException.from(ctxt, e0.getMessage())
/*  457 */       .withCause(e0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void unwrapAndThrowAsIAE(Throwable t) {
/*  467 */     throwAsIAE(getRootCause(t));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void unwrapAndThrowAsIAE(Throwable t, String msg) {
/*  477 */     throwAsIAE(getRootCause(t), msg);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void closeOnFailAndThrowAsIOE(JsonGenerator g, Exception fail) throws IOException {
/*  495 */     g.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
/*      */     try {
/*  497 */       g.close();
/*  498 */     } catch (Exception e) {
/*  499 */       fail.addSuppressed(e);
/*      */     } 
/*  501 */     throwIfIOE(fail);
/*  502 */     throwIfRTE(fail);
/*  503 */     throw new RuntimeException(fail);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void closeOnFailAndThrowAsIOE(JsonGenerator g, Closeable toClose, Exception fail) throws IOException {
/*  519 */     if (g != null) {
/*  520 */       g.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
/*      */       try {
/*  522 */         g.close();
/*  523 */       } catch (Exception e) {
/*  524 */         fail.addSuppressed(e);
/*      */       } 
/*      */     } 
/*  527 */     if (toClose != null) {
/*      */       try {
/*  529 */         toClose.close();
/*  530 */       } catch (Exception e) {
/*  531 */         fail.addSuppressed(e);
/*      */       } 
/*      */     }
/*  534 */     throwIfIOE(fail);
/*  535 */     throwIfRTE(fail);
/*  536 */     throw new RuntimeException(fail);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T createInstance(Class<T> cls, boolean canFixAccess) throws IllegalArgumentException {
/*  561 */     Constructor<T> ctor = findConstructor(cls, canFixAccess);
/*  562 */     if (ctor == null) {
/*  563 */       throw new IllegalArgumentException("Class " + cls.getName() + " has no default (no arg) constructor");
/*      */     }
/*      */     try {
/*  566 */       return ctor.newInstance(new Object[0]);
/*  567 */     } catch (Exception e) {
/*  568 */       unwrapAndThrowAsIAE(e, "Failed to instantiate class " + cls.getName() + ", problem: " + e.getMessage());
/*  569 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> Constructor<T> findConstructor(Class<T> cls, boolean forceAccess) throws IllegalArgumentException {
/*      */     try {
/*  577 */       Constructor<T> ctor = cls.getDeclaredConstructor(new Class[0]);
/*  578 */       if (forceAccess) {
/*  579 */         checkAndFixAccess(ctor, forceAccess);
/*      */       
/*      */       }
/*  582 */       else if (!Modifier.isPublic(ctor.getModifiers())) {
/*  583 */         throw new IllegalArgumentException("Default constructor for " + cls.getName() + " is not accessible (non-public?): not allowed to try modify access via Reflection: cannot instantiate type");
/*      */       } 
/*      */       
/*  586 */       return ctor;
/*  587 */     } catch (NoSuchMethodException noSuchMethodException) {
/*      */     
/*  589 */     } catch (Exception e) {
/*  590 */       unwrapAndThrowAsIAE(e, "Failed to find default constructor of class " + cls.getName() + ", problem: " + e.getMessage());
/*      */     } 
/*  592 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?> classOf(Object inst) {
/*  605 */     if (inst == null) {
/*  606 */       return null;
/*      */     }
/*  608 */     return inst.getClass();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?> rawClass(JavaType t) {
/*  615 */     if (t == null) {
/*  616 */       return null;
/*      */     }
/*  618 */     return t.getRawClass();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T nonNull(T valueOrNull, T defaultValue) {
/*  625 */     return (valueOrNull == null) ? defaultValue : valueOrNull;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String nullOrToString(Object value) {
/*  632 */     if (value == null) {
/*  633 */       return null;
/*      */     }
/*  635 */     return value.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String nonNullString(String str) {
/*  642 */     if (str == null) {
/*  643 */       return "";
/*      */     }
/*  645 */     return str;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String quotedOr(Object str, String forNull) {
/*  655 */     if (str == null) {
/*  656 */       return forNull;
/*      */     }
/*  658 */     return String.format("\"%s\"", new Object[] { str });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getClassDescription(Object classOrInstance) {
/*  674 */     if (classOrInstance == null) {
/*  675 */       return "unknown";
/*      */     }
/*      */     
/*  678 */     Class<?> cls = (classOrInstance instanceof Class) ? (Class)classOrInstance : classOrInstance.getClass();
/*  679 */     return nameOf(cls);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getTypeDescription(JavaType fullType) {
/*  695 */     if (fullType == null) {
/*  696 */       return "[null]";
/*      */     }
/*  698 */     StringBuilder sb = (new StringBuilder(80)).append('`');
/*  699 */     sb.append(fullType.toCanonical());
/*  700 */     return sb.append('`').toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String classNameOf(Object inst) {
/*  711 */     if (inst == null) {
/*  712 */       return "[null]";
/*      */     }
/*  714 */     Class<?> raw = (inst instanceof Class) ? (Class)inst : inst.getClass();
/*  715 */     return nameOf(raw);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String nameOf(Class<?> cls) {
/*  725 */     if (cls == null) {
/*  726 */       return "[null]";
/*      */     }
/*  728 */     int index = 0;
/*  729 */     while (cls.isArray()) {
/*  730 */       index++;
/*  731 */       cls = cls.getComponentType();
/*      */     } 
/*  733 */     String base = cls.isPrimitive() ? cls.getSimpleName() : cls.getName();
/*  734 */     if (index > 0) {
/*  735 */       StringBuilder sb = new StringBuilder(base);
/*      */       while (true) {
/*  737 */         sb.append("[]");
/*  738 */         if (--index <= 0)
/*  739 */         { base = sb.toString(); break; } 
/*      */       } 
/*  741 */     }  return backticked(base);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String nameOf(Named named) {
/*  754 */     if (named == null) {
/*  755 */       return "[null]";
/*      */     }
/*  757 */     return apostrophed(named.getName());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String name(String name) {
/*  767 */     if (name == null) {
/*  768 */       return "[null]";
/*      */     }
/*  770 */     return apostrophed(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String name(PropertyName name) {
/*  780 */     if (name == null) {
/*  781 */       return "[null]";
/*      */     }
/*      */     
/*  784 */     return apostrophed(name.getSimpleName());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String backticked(String text) {
/*  799 */     if (text == null) {
/*  800 */       return "[null]";
/*      */     }
/*  802 */     return (new StringBuilder(text.length() + 2)).append('`').append(text).append('`').toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String apostrophed(String text) {
/*  811 */     if (text == null) {
/*  812 */       return "[null]";
/*      */     }
/*  814 */     return (new StringBuilder(text.length() + 2)).append('\'').append(text).append('\'').toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String exceptionMessage(Throwable t) {
/*  828 */     if (t instanceof JacksonException) {
/*  829 */       return ((JacksonException)t).getOriginalMessage();
/*      */     }
/*  831 */     if (t instanceof java.lang.reflect.InvocationTargetException && t.getCause() != null) {
/*  832 */       return t.getCause().getMessage();
/*      */     }
/*  834 */     return t.getMessage();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object defaultValue(Class<?> cls) {
/*  849 */     if (cls == int.class) {
/*  850 */       return Integer.valueOf(0);
/*      */     }
/*  852 */     if (cls == long.class) {
/*  853 */       return Long.valueOf(0L);
/*      */     }
/*  855 */     if (cls == boolean.class) {
/*  856 */       return Boolean.FALSE;
/*      */     }
/*  858 */     if (cls == double.class) {
/*  859 */       return Double.valueOf(0.0D);
/*      */     }
/*  861 */     if (cls == float.class) {
/*  862 */       return Float.valueOf(0.0F);
/*      */     }
/*  864 */     if (cls == byte.class) {
/*  865 */       return Byte.valueOf((byte)0);
/*      */     }
/*  867 */     if (cls == short.class) {
/*  868 */       return Short.valueOf((short)0);
/*      */     }
/*  870 */     if (cls == char.class) {
/*  871 */       return Character.valueOf(false);
/*      */     }
/*  873 */     throw new IllegalArgumentException("Class " + cls.getName() + " is not a primitive type");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?> wrapperType(Class<?> primitiveType) {
/*  884 */     if (primitiveType == int.class) {
/*  885 */       return Integer.class;
/*      */     }
/*  887 */     if (primitiveType == long.class) {
/*  888 */       return Long.class;
/*      */     }
/*  890 */     if (primitiveType == boolean.class) {
/*  891 */       return Boolean.class;
/*      */     }
/*  893 */     if (primitiveType == double.class) {
/*  894 */       return Double.class;
/*      */     }
/*  896 */     if (primitiveType == float.class) {
/*  897 */       return Float.class;
/*      */     }
/*  899 */     if (primitiveType == byte.class) {
/*  900 */       return Byte.class;
/*      */     }
/*  902 */     if (primitiveType == short.class) {
/*  903 */       return Short.class;
/*      */     }
/*  905 */     if (primitiveType == char.class) {
/*  906 */       return Character.class;
/*      */     }
/*  908 */     throw new IllegalArgumentException("Class " + primitiveType.getName() + " is not a primitive type");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?> primitiveType(Class<?> type) {
/*  919 */     if (type.isPrimitive()) {
/*  920 */       return type;
/*      */     }
/*      */     
/*  923 */     if (type == Integer.class) {
/*  924 */       return int.class;
/*      */     }
/*  926 */     if (type == Long.class) {
/*  927 */       return long.class;
/*      */     }
/*  929 */     if (type == Boolean.class) {
/*  930 */       return boolean.class;
/*      */     }
/*  932 */     if (type == Double.class) {
/*  933 */       return double.class;
/*      */     }
/*  935 */     if (type == Float.class) {
/*  936 */       return float.class;
/*      */     }
/*  938 */     if (type == Byte.class) {
/*  939 */       return byte.class;
/*      */     }
/*  941 */     if (type == Short.class) {
/*  942 */       return short.class;
/*      */     }
/*  944 */     if (type == Character.class) {
/*  945 */       return char.class;
/*      */     }
/*  947 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void checkAndFixAccess(Member member) {
/*  966 */     checkAndFixAccess(member, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void checkAndFixAccess(Member member, boolean evenIfAlreadyPublic) {
/*  984 */     AccessibleObject ao = (AccessibleObject)member;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  992 */       Class<?> declaringClass = member.getDeclaringClass();
/*      */       
/*  994 */       boolean isPublic = (Modifier.isPublic(member.getModifiers()) && Modifier.isPublic(declaringClass.getModifiers()));
/*  995 */       if (!isPublic || (evenIfAlreadyPublic && !isJDKClass(declaringClass))) {
/*  996 */         ao.setAccessible(true);
/*      */       }
/*  998 */     } catch (SecurityException se) {
/*      */ 
/*      */       
/* 1001 */       if (!ao.isAccessible()) {
/* 1002 */         Class<?> declClass = member.getDeclaringClass();
/* 1003 */         throw new IllegalArgumentException("Cannot access " + member + " (from class " + declClass.getName() + "; failed to set access: " + se.getMessage());
/*      */       }
/*      */     
/*      */     }
/* 1007 */     catch (RuntimeException se) {
/* 1008 */       if ("InaccessibleObjectException".equals(se.getClass().getSimpleName())) {
/* 1009 */         throw new IllegalArgumentException(String.format("Failed to call `setAccess()` on %s '%s' (of class %s) due to `%s`, problem: %s", new Object[] { member
/*      */                 
/* 1011 */                 .getClass().getSimpleName(), member.getName(), 
/* 1012 */                 nameOf(member.getDeclaringClass()), se
/* 1013 */                 .getClass().getName(), se.getMessage() }), se);
/*      */       }
/*      */       
/* 1016 */       throw se;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEnumType(Class<?> rawType) {
/* 1033 */     return Enum.class.isAssignableFrom(rawType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<? extends Enum<?>> findEnumType(EnumSet<?> s) {
/* 1045 */     if (!s.isEmpty()) {
/* 1046 */       return findEnumType(s.iterator().next());
/*      */     }
/*      */     
/* 1049 */     return EnumTypeLocator.instance.enumTypeFor(s);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<? extends Enum<?>> findEnumType(EnumMap<?, ?> m) {
/* 1060 */     if (!m.isEmpty()) {
/* 1061 */       return findEnumType(m.keySet().iterator().next());
/*      */     }
/*      */     
/* 1064 */     return EnumTypeLocator.instance.enumTypeFor(m);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<? extends Enum<?>> findEnumType(Enum<?> en) {
/* 1076 */     return (Class)en.getDeclaringClass();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<? extends Enum<?>> findEnumType(Class<?> cls) {
/* 1089 */     if (cls.getSuperclass() != Enum.class) {
/* 1090 */       cls = cls.getSuperclass();
/*      */     }
/* 1092 */     return (Class)cls;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Annotation> Enum<?> findFirstAnnotatedEnumValue(Class<Enum<?>> enumClass, Class<T> annotationClass) {
/* 1108 */     Field[] fields = enumClass.getDeclaredFields();
/* 1109 */     for (Field field : fields) {
/* 1110 */       if (field.isEnumConstant()) {
/* 1111 */         Annotation defaultValueAnnotation = field.getAnnotation(annotationClass);
/* 1112 */         if (defaultValueAnnotation != null) {
/* 1113 */           String name = field.getName();
/* 1114 */           for (Enum<?> enumValue : (Enum[])enumClass.getEnumConstants()) {
/* 1115 */             if (name.equals(enumValue.name())) {
/* 1116 */               return enumValue;
/*      */             }
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/* 1122 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isJacksonStdImpl(Object impl) {
/* 1142 */     return (impl == null || isJacksonStdImpl(impl.getClass()));
/*      */   }
/*      */   
/*      */   public static boolean isJacksonStdImpl(Class<?> implClass) {
/* 1146 */     return (implClass.getAnnotation(JacksonStdImpl.class) != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isJDKClass(Class<?> rawType) {
/* 1163 */     String clsName = rawType.getName();
/* 1164 */     return (clsName.startsWith("java.") || clsName.startsWith("javax."));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNonStaticInnerClass(Class<?> cls) {
/* 1177 */     return (!Modifier.isStatic(cls.getModifiers()) && 
/* 1178 */       getEnclosingClass(cls) != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String getPackageName(Class<?> cls) {
/* 1188 */     Package pkg = cls.getPackage();
/* 1189 */     return (pkg == null) ? null : pkg.getName();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean hasEnclosingMethod(Class<?> cls) {
/* 1196 */     return (!isObjectOrPrimitive(cls) && cls.getEnclosingMethod() != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Field[] getDeclaredFields(Class<?> cls) {
/* 1204 */     return cls.getDeclaredFields();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Method[] getDeclaredMethods(Class<?> cls) {
/* 1212 */     return cls.getDeclaredMethods();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Annotation[] findClassAnnotations(Class<?> cls) {
/* 1219 */     if (isObjectOrPrimitive(cls)) {
/* 1220 */       return NO_ANNOTATIONS;
/*      */     }
/* 1222 */     return cls.getDeclaredAnnotations();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method[] getClassMethods(Class<?> cls) {
/*      */     try {
/* 1235 */       return cls.getDeclaredMethods();
/* 1236 */     } catch (NoClassDefFoundError ex) {
/*      */       Class<?> contextClass;
/*      */       
/* 1239 */       ClassLoader loader = Thread.currentThread().getContextClassLoader();
/* 1240 */       if (loader == null)
/*      */       {
/* 1242 */         return _failGetClassMethods(cls, ex);
/*      */       }
/*      */       
/*      */       try {
/* 1246 */         contextClass = loader.loadClass(cls.getName());
/* 1247 */       } catch (ClassNotFoundException e) {
/* 1248 */         ex.addSuppressed(e);
/* 1249 */         return _failGetClassMethods(cls, ex);
/*      */       } 
/*      */       try {
/* 1252 */         return contextClass.getDeclaredMethods();
/* 1253 */       } catch (Throwable t) {
/* 1254 */         return _failGetClassMethods(cls, t);
/*      */       } 
/* 1256 */     } catch (Throwable t) {
/* 1257 */       return _failGetClassMethods(cls, t);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static Method[] _failGetClassMethods(Class<?> cls, Throwable rootCause) throws IllegalArgumentException {
/* 1265 */     throw new IllegalArgumentException(String.format("Failed on call to `getDeclaredMethods()` on class `%s`, problem: (%s) %s", new Object[] { cls
/*      */             
/* 1267 */             .getName(), rootCause.getClass().getName(), rootCause.getMessage() }), rootCause);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Ctor[] getConstructors(Class<?> cls) {
/* 1277 */     if (cls.isInterface() || isObjectOrPrimitive(cls)) {
/* 1278 */       return NO_CTORS;
/*      */     }
/* 1280 */     Constructor[] arrayOfConstructor = (Constructor[])cls.getDeclaredConstructors();
/* 1281 */     int len = arrayOfConstructor.length;
/* 1282 */     Ctor[] result = new Ctor[len];
/* 1283 */     for (int i = 0; i < len; i++) {
/* 1284 */       result[i] = new Ctor(arrayOfConstructor[i]);
/*      */     }
/* 1286 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?> getDeclaringClass(Class<?> cls) {
/* 1296 */     return isObjectOrPrimitive(cls) ? null : cls.getDeclaringClass();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Type getGenericSuperclass(Class<?> cls) {
/* 1303 */     return cls.getGenericSuperclass();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Type[] getGenericInterfaces(Class<?> cls) {
/* 1310 */     return cls.getGenericInterfaces();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?> getEnclosingClass(Class<?> cls) {
/* 1318 */     return isObjectOrPrimitive(cls) ? null : cls.getEnclosingClass();
/*      */   }
/*      */   
/*      */   private static Class<?>[] _interfaces(Class<?> cls) {
/* 1322 */     return cls.getInterfaces();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class EnumTypeLocator
/*      */   {
/* 1337 */     static final EnumTypeLocator instance = new EnumTypeLocator();
/*      */     
/*      */     private final Field enumSetTypeField;
/*      */     
/*      */     private final Field enumMapTypeField;
/*      */     
/*      */     private final String failForEnumSet;
/*      */     
/*      */     private final String failForEnumMap;
/*      */ 
/*      */     
/*      */     private EnumTypeLocator() {
/* 1349 */       Field f = null;
/* 1350 */       String msg = null;
/*      */       
/*      */       try {
/* 1353 */         f = locateField(EnumSet.class, "elementType", Class.class);
/* 1354 */       } catch (Exception e) {
/* 1355 */         msg = e.toString();
/*      */       } 
/* 1357 */       this.enumSetTypeField = f;
/* 1358 */       this.failForEnumSet = msg;
/*      */       
/* 1360 */       f = null;
/* 1361 */       msg = null;
/*      */       try {
/* 1363 */         f = locateField(EnumMap.class, "keyType", Class.class);
/* 1364 */       } catch (Exception e) {
/* 1365 */         msg = e.toString();
/*      */       } 
/* 1367 */       this.enumMapTypeField = f;
/* 1368 */       this.failForEnumMap = msg;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Class<? extends Enum<?>> enumTypeFor(EnumSet<?> set) {
/* 1374 */       if (this.enumSetTypeField != null) {
/* 1375 */         return (Class<? extends Enum<?>>)get(set, this.enumSetTypeField);
/*      */       }
/* 1377 */       throw new IllegalStateException("Cannot figure out type parameter for `EnumSet` (odd JDK platform?), problem: " + this.failForEnumSet);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Class<? extends Enum<?>> enumTypeFor(EnumMap<?, ?> set) {
/* 1384 */       if (this.enumMapTypeField != null) {
/* 1385 */         return (Class<? extends Enum<?>>)get(set, this.enumMapTypeField);
/*      */       }
/* 1387 */       throw new IllegalStateException("Cannot figure out type parameter for `EnumMap` (odd JDK platform?), problem: " + this.failForEnumMap);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private Object get(Object bean, Field field) {
/*      */       try {
/* 1394 */         return field.get(bean);
/* 1395 */       } catch (Exception e) {
/* 1396 */         throw new IllegalArgumentException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static Field locateField(Class<?> fromClass, String expectedName, Class<?> type) throws Exception {
/* 1404 */       Field[] fields = fromClass.getDeclaredFields(); Field[] arrayOfField1; int i; byte b;
/* 1405 */       for (arrayOfField1 = fields, i = arrayOfField1.length, b = 0; b < i; ) { Field f = arrayOfField1[b];
/* 1406 */         if (!expectedName.equals(f.getName()) || f.getType() != type) {
/*      */           b++; continue;
/*      */         } 
/* 1409 */         f.setAccessible(true);
/* 1410 */         return f; }
/*      */ 
/*      */       
/* 1413 */       throw new IllegalStateException(String.format("No field named '%s' in class '%s'", new Object[] { expectedName, fromClass
/* 1414 */               .getTypeName() }));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Ctor
/*      */   {
/*      */     public final Constructor<?> _ctor;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private transient Annotation[] _annotations;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private transient Annotation[][] _paramAnnotations;
/*      */ 
/*      */ 
/*      */     
/* 1438 */     private int _paramCount = -1;
/*      */     
/*      */     public Ctor(Constructor<?> ctor) {
/* 1441 */       this._ctor = ctor;
/*      */     }
/*      */     
/*      */     public Constructor<?> getConstructor() {
/* 1445 */       return this._ctor;
/*      */     }
/*      */     
/*      */     public int getParamCount() {
/* 1449 */       int c = this._paramCount;
/* 1450 */       if (c < 0) {
/* 1451 */         c = (this._ctor.getParameterTypes()).length;
/* 1452 */         this._paramCount = c;
/*      */       } 
/* 1454 */       return c;
/*      */     }
/*      */     
/*      */     public Class<?> getDeclaringClass() {
/* 1458 */       return this._ctor.getDeclaringClass();
/*      */     }
/*      */     
/*      */     public Annotation[] getDeclaredAnnotations() {
/* 1462 */       Annotation[] result = this._annotations;
/* 1463 */       if (result == null) {
/* 1464 */         result = this._ctor.getDeclaredAnnotations();
/* 1465 */         this._annotations = result;
/*      */       } 
/* 1467 */       return result;
/*      */     }
/*      */     
/*      */     public Annotation[][] getParameterAnnotations() {
/* 1471 */       Annotation[][] result = this._paramAnnotations;
/* 1472 */       if (result == null) {
/* 1473 */         result = this._ctor.getParameterAnnotations();
/* 1474 */         this._paramAnnotations = result;
/*      */       } 
/* 1476 */       return result;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/util/ClassUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */