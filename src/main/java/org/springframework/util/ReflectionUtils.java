/*     */ package org.springframework.util;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.UndeclaredThrowableException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.springframework.lang.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ReflectionUtils
/*     */ {
/*     */   public static final MethodFilter USER_DECLARED_METHODS;
/*     */   public static final FieldFilter COPYABLE_FIELDS;
/*     */   private static final String CGLIB_RENAMED_METHOD_PREFIX = "CGLIB$";
/*     */   
/*     */   static {
/*  53 */     USER_DECLARED_METHODS = (method -> 
/*  54 */       (!method.isBridge() && !method.isSynthetic() && method.getDeclaringClass() != Object.class));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  59 */     COPYABLE_FIELDS = (field -> 
/*  60 */       (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  69 */   private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
/*     */   
/*  71 */   private static final Method[] EMPTY_METHOD_ARRAY = new Method[0];
/*     */   
/*  73 */   private static final Field[] EMPTY_FIELD_ARRAY = new Field[0];
/*     */   
/*  75 */   private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  82 */   private static final Map<Class<?>, Method[]> declaredMethodsCache = (Map)new ConcurrentReferenceHashMap<>(256);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  87 */   private static final Map<Class<?>, Field[]> declaredFieldsCache = (Map)new ConcurrentReferenceHashMap<>(256);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void handleReflectionException(Exception ex) {
/* 103 */     if (ex instanceof NoSuchMethodException) {
/* 104 */       throw new IllegalStateException("Method not found: " + ex.getMessage());
/*     */     }
/* 106 */     if (ex instanceof IllegalAccessException) {
/* 107 */       throw new IllegalStateException("Could not access method or field: " + ex.getMessage());
/*     */     }
/* 109 */     if (ex instanceof InvocationTargetException) {
/* 110 */       handleInvocationTargetException((InvocationTargetException)ex);
/*     */     }
/* 112 */     if (ex instanceof RuntimeException) {
/* 113 */       throw (RuntimeException)ex;
/*     */     }
/* 115 */     throw new UndeclaredThrowableException(ex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void handleInvocationTargetException(InvocationTargetException ex) {
/* 126 */     rethrowRuntimeException(ex.getTargetException());
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
/*     */   public static void rethrowRuntimeException(Throwable ex) {
/* 141 */     if (ex instanceof RuntimeException) {
/* 142 */       throw (RuntimeException)ex;
/*     */     }
/* 144 */     if (ex instanceof Error) {
/* 145 */       throw (Error)ex;
/*     */     }
/* 147 */     throw new UndeclaredThrowableException(ex);
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
/*     */   public static void rethrowException(Throwable ex) throws Exception {
/* 162 */     if (ex instanceof Exception) {
/* 163 */       throw (Exception)ex;
/*     */     }
/* 165 */     if (ex instanceof Error) {
/* 166 */       throw (Error)ex;
/*     */     }
/* 168 */     throw new UndeclaredThrowableException(ex);
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
/*     */   public static <T> Constructor<T> accessibleConstructor(Class<T> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
/* 185 */     Constructor<T> ctor = clazz.getDeclaredConstructor(parameterTypes);
/* 186 */     makeAccessible(ctor);
/* 187 */     return ctor;
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
/*     */   public static void makeAccessible(Constructor<?> ctor) {
/* 200 */     if ((!Modifier.isPublic(ctor.getModifiers()) || 
/* 201 */       !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
/* 202 */       ctor.setAccessible(true);
/*     */     }
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
/*     */   @Nullable
/*     */   public static Method findMethod(Class<?> clazz, String name) {
/* 219 */     return findMethod(clazz, name, EMPTY_CLASS_ARRAY);
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
/*     */   @Nullable
/*     */   public static Method findMethod(Class<?> clazz, String name, @Nullable Class<?>... paramTypes) {
/* 234 */     Assert.notNull(clazz, "Class must not be null");
/* 235 */     Assert.notNull(name, "Method name must not be null");
/* 236 */     Class<?> searchType = clazz;
/* 237 */     while (searchType != null) {
/*     */       
/* 239 */       Method[] methods = searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType, false);
/* 240 */       for (Method method : methods) {
/* 241 */         if (name.equals(method.getName()) && (paramTypes == null || hasSameParams(method, paramTypes))) {
/* 242 */           return method;
/*     */         }
/*     */       } 
/* 245 */       searchType = searchType.getSuperclass();
/*     */     } 
/* 247 */     return null;
/*     */   }
/*     */   
/*     */   private static boolean hasSameParams(Method method, Class<?>[] paramTypes) {
/* 251 */     return (paramTypes.length == method.getParameterCount() && 
/* 252 */       Arrays.equals((Object[])paramTypes, (Object[])method.getParameterTypes()));
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
/*     */   @Nullable
/*     */   public static Object invokeMethod(Method method, @Nullable Object target) {
/* 266 */     return invokeMethod(method, target, EMPTY_OBJECT_ARRAY);
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
/*     */   @Nullable
/*     */   public static Object invokeMethod(Method method, @Nullable Object target, @Nullable Object... args) {
/*     */     try {
/* 282 */       return method.invoke(target, args);
/*     */     }
/* 284 */     catch (Exception ex) {
/* 285 */       handleReflectionException(ex);
/*     */       
/* 287 */       throw new IllegalStateException("Should never get here");
/*     */     } 
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
/*     */   public static boolean declaresException(Method method, Class<?> exceptionType) {
/* 300 */     Assert.notNull(method, "Method must not be null");
/* 301 */     Class<?>[] declaredExceptions = method.getExceptionTypes();
/* 302 */     for (Class<?> declaredException : declaredExceptions) {
/* 303 */       if (declaredException.isAssignableFrom(exceptionType)) {
/* 304 */         return true;
/*     */       }
/*     */     } 
/* 307 */     return false;
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
/*     */   public static void doWithLocalMethods(Class<?> clazz, MethodCallback mc) {
/* 321 */     Method[] methods = getDeclaredMethods(clazz, false);
/* 322 */     for (Method method : methods) {
/*     */       try {
/* 324 */         mc.doWith(method);
/*     */       }
/* 326 */       catch (IllegalAccessException ex) {
/* 327 */         throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
/*     */       } 
/*     */     } 
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
/*     */   public static void doWithMethods(Class<?> clazz, MethodCallback mc) {
/* 343 */     doWithMethods(clazz, mc, null);
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
/*     */   public static void doWithMethods(Class<?> clazz, MethodCallback mc, @Nullable MethodFilter mf) {
/* 357 */     if (mf == USER_DECLARED_METHODS && clazz == Object.class) {
/*     */       return;
/*     */     }
/*     */     
/* 361 */     Method[] methods = getDeclaredMethods(clazz, false);
/* 362 */     for (Method method : methods) {
/* 363 */       if (mf == null || mf.matches(method)) {
/*     */         
/*     */         try {
/*     */           
/* 367 */           mc.doWith(method);
/*     */         }
/* 369 */         catch (IllegalAccessException ex) {
/* 370 */           throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
/*     */         } 
/*     */       }
/*     */     } 
/* 374 */     if (clazz.getSuperclass() != null && (mf != USER_DECLARED_METHODS || clazz.getSuperclass() != Object.class)) {
/* 375 */       doWithMethods(clazz.getSuperclass(), mc, mf);
/*     */     }
/* 377 */     else if (clazz.isInterface()) {
/* 378 */       for (Class<?> superIfc : clazz.getInterfaces()) {
/* 379 */         doWithMethods(superIfc, mc, mf);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Method[] getAllDeclaredMethods(Class<?> leafClass) {
/* 391 */     List<Method> methods = new ArrayList<>(20);
/* 392 */     doWithMethods(leafClass, methods::add);
/* 393 */     return methods.<Method>toArray(EMPTY_METHOD_ARRAY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Method[] getUniqueDeclaredMethods(Class<?> leafClass) {
/* 404 */     return getUniqueDeclaredMethods(leafClass, null);
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
/*     */   public static Method[] getUniqueDeclaredMethods(Class<?> leafClass, @Nullable MethodFilter mf) {
/* 417 */     List<Method> methods = new ArrayList<>(20);
/* 418 */     doWithMethods(leafClass, method -> { boolean knownSignature = false; Method methodBeingOverriddenWithCovariantReturnType = null; for (Method existingMethod : methods) { if (method.getName().equals(existingMethod.getName()) && method.getParameterCount() == existingMethod.getParameterCount() && Arrays.equals((Object[])method.getParameterTypes(), (Object[])existingMethod.getParameterTypes())) { if (existingMethod.getReturnType() != method.getReturnType() && existingMethod.getReturnType().isAssignableFrom(method.getReturnType())) { methodBeingOverriddenWithCovariantReturnType = existingMethod; break; }  knownSignature = true; break; }  }  if (methodBeingOverriddenWithCovariantReturnType != null) methods.remove(methodBeingOverriddenWithCovariantReturnType);  if (!knownSignature && !isCglibRenamedMethod(method)) methods.add(method);  }mf);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 443 */     return methods.<Method>toArray(EMPTY_METHOD_ARRAY);
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
/*     */   public static Method[] getDeclaredMethods(Class<?> clazz) {
/* 459 */     return getDeclaredMethods(clazz, true);
/*     */   }
/*     */   
/*     */   private static Method[] getDeclaredMethods(Class<?> clazz, boolean defensive) {
/* 463 */     Assert.notNull(clazz, "Class must not be null");
/* 464 */     Method[] result = declaredMethodsCache.get(clazz);
/* 465 */     if (result == null) {
/*     */       try {
/* 467 */         Method[] declaredMethods = clazz.getDeclaredMethods();
/* 468 */         List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);
/* 469 */         if (defaultMethods != null) {
/* 470 */           result = new Method[declaredMethods.length + defaultMethods.size()];
/* 471 */           System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
/* 472 */           int index = declaredMethods.length;
/* 473 */           for (Method defaultMethod : defaultMethods) {
/* 474 */             result[index] = defaultMethod;
/* 475 */             index++;
/*     */           } 
/*     */         } else {
/*     */           
/* 479 */           result = declaredMethods;
/*     */         } 
/* 481 */         declaredMethodsCache.put(clazz, (result.length == 0) ? EMPTY_METHOD_ARRAY : result);
/*     */       }
/* 483 */       catch (Throwable ex) {
/* 484 */         throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz
/* 485 */             .getClassLoader() + "]", ex);
/*     */       } 
/*     */     }
/* 488 */     return (result.length == 0 || !defensive) ? result : (Method[])result.clone();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
/* 493 */     List<Method> result = null;
/* 494 */     for (Class<?> ifc : clazz.getInterfaces()) {
/* 495 */       for (Method ifcMethod : ifc.getMethods()) {
/* 496 */         if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
/* 497 */           if (result == null) {
/* 498 */             result = new ArrayList<>();
/*     */           }
/* 500 */           result.add(ifcMethod);
/*     */         } 
/*     */       } 
/*     */     } 
/* 504 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isEqualsMethod(@Nullable Method method) {
/* 512 */     if (method == null) {
/* 513 */       return false;
/*     */     }
/* 515 */     if (method.getParameterCount() != 1) {
/* 516 */       return false;
/*     */     }
/* 518 */     if (!method.getName().equals("equals")) {
/* 519 */       return false;
/*     */     }
/* 521 */     return (method.getParameterTypes()[0] == Object.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isHashCodeMethod(@Nullable Method method) {
/* 529 */     return (method != null && method.getParameterCount() == 0 && method.getName().equals("hashCode"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isToStringMethod(@Nullable Method method) {
/* 537 */     return (method != null && method.getParameterCount() == 0 && method.getName().equals("toString"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isObjectMethod(@Nullable Method method) {
/* 544 */     return (method != null && (method.getDeclaringClass() == Object.class || 
/* 545 */       isEqualsMethod(method) || isHashCodeMethod(method) || isToStringMethod(method)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isCglibRenamedMethod(Method renamedMethod) {
/* 554 */     String name = renamedMethod.getName();
/* 555 */     if (name.startsWith("CGLIB$")) {
/* 556 */       int i = name.length() - 1;
/* 557 */       while (i >= 0 && Character.isDigit(name.charAt(i))) {
/* 558 */         i--;
/*     */       }
/* 560 */       return (i > "CGLIB$".length() && i < name.length() - 1 && name.charAt(i) == '$');
/*     */     } 
/* 562 */     return false;
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
/*     */   public static void makeAccessible(Method method) {
/* 575 */     if ((!Modifier.isPublic(method.getModifiers()) || 
/* 576 */       !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
/* 577 */       method.setAccessible(true);
/*     */     }
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
/*     */   @Nullable
/*     */   public static Field findField(Class<?> clazz, String name) {
/* 593 */     return findField(clazz, name, null);
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
/*     */   @Nullable
/*     */   public static Field findField(Class<?> clazz, @Nullable String name, @Nullable Class<?> type) {
/* 607 */     Assert.notNull(clazz, "Class must not be null");
/* 608 */     Assert.isTrue((name != null || type != null), "Either name or type of the field must be specified");
/* 609 */     Class<?> searchType = clazz;
/* 610 */     while (Object.class != searchType && searchType != null) {
/* 611 */       Field[] fields = getDeclaredFields(searchType);
/* 612 */       for (Field field : fields) {
/* 613 */         if ((name == null || name.equals(field.getName())) && (type == null || type
/* 614 */           .equals(field.getType()))) {
/* 615 */           return field;
/*     */         }
/*     */       } 
/* 618 */       searchType = searchType.getSuperclass();
/*     */     } 
/* 620 */     return null;
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
/*     */   public static void setField(Field field, @Nullable Object target, @Nullable Object value) {
/*     */     try {
/* 637 */       field.set(target, value);
/*     */     }
/* 639 */     catch (IllegalAccessException ex) {
/* 640 */       handleReflectionException(ex);
/*     */     } 
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
/*     */   @Nullable
/*     */   public static Object getField(Field field, @Nullable Object target) {
/*     */     try {
/* 658 */       return field.get(target);
/*     */     }
/* 660 */     catch (IllegalAccessException ex) {
/* 661 */       handleReflectionException(ex);
/*     */       
/* 663 */       throw new IllegalStateException("Should never get here");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void doWithLocalFields(Class<?> clazz, FieldCallback fc) {
/* 675 */     for (Field field : getDeclaredFields(clazz)) {
/*     */       try {
/* 677 */         fc.doWith(field);
/*     */       }
/* 679 */       catch (IllegalAccessException ex) {
/* 680 */         throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void doWithFields(Class<?> clazz, FieldCallback fc) {
/* 693 */     doWithFields(clazz, fc, null);
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
/*     */   public static void doWithFields(Class<?> clazz, FieldCallback fc, @Nullable FieldFilter ff) {
/* 706 */     Class<?> targetClass = clazz;
/*     */     do {
/* 708 */       Field[] fields = getDeclaredFields(targetClass);
/* 709 */       for (Field field : fields) {
/* 710 */         if (ff == null || ff.matches(field))
/*     */           
/*     */           try {
/*     */             
/* 714 */             fc.doWith(field);
/*     */           }
/* 716 */           catch (IllegalAccessException ex) {
/* 717 */             throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
/*     */           }  
/*     */       } 
/* 720 */       targetClass = targetClass.getSuperclass();
/*     */     }
/* 722 */     while (targetClass != null && targetClass != Object.class);
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
/*     */   private static Field[] getDeclaredFields(Class<?> clazz) {
/* 734 */     Assert.notNull(clazz, "Class must not be null");
/* 735 */     Field[] result = declaredFieldsCache.get(clazz);
/* 736 */     if (result == null) {
/*     */       try {
/* 738 */         result = clazz.getDeclaredFields();
/* 739 */         declaredFieldsCache.put(clazz, (result.length == 0) ? EMPTY_FIELD_ARRAY : result);
/*     */       }
/* 741 */       catch (Throwable ex) {
/* 742 */         throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz
/* 743 */             .getClassLoader() + "]", ex);
/*     */       } 
/*     */     }
/* 746 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void shallowCopyFieldState(Object src, Object dest) {
/* 756 */     Assert.notNull(src, "Source for field copy cannot be null");
/* 757 */     Assert.notNull(dest, "Destination for field copy cannot be null");
/* 758 */     if (!src.getClass().isAssignableFrom(dest.getClass())) {
/* 759 */       throw new IllegalArgumentException("Destination class [" + dest.getClass().getName() + "] must be same or subclass as source class [" + src
/* 760 */           .getClass().getName() + "]");
/*     */     }
/* 762 */     doWithFields(src.getClass(), field -> { makeAccessible(field); Object srcValue = field.get(src); field.set(dest, srcValue); }COPYABLE_FIELDS);
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
/*     */   public static boolean isPublicStaticFinal(Field field) {
/* 774 */     int modifiers = field.getModifiers();
/* 775 */     return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
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
/*     */   public static void makeAccessible(Field field) {
/* 788 */     if ((!Modifier.isPublic(field.getModifiers()) || 
/* 789 */       !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || 
/* 790 */       Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
/* 791 */       field.setAccessible(true);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void clearCache() {
/* 803 */     declaredMethodsCache.clear();
/* 804 */     declaredFieldsCache.clear();
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
/*     */   @FunctionalInterface
/*     */   public static interface MethodCallback
/*     */   {
/*     */     void doWith(Method param1Method) throws IllegalArgumentException, IllegalAccessException;
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
/*     */   @FunctionalInterface
/*     */   public static interface MethodFilter
/*     */   {
/*     */     boolean matches(Method param1Method);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default MethodFilter and(MethodFilter next) {
/* 843 */       Assert.notNull(next, "Next MethodFilter must not be null");
/* 844 */       return method -> (matches(method) && next.matches(method));
/*     */     }
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
/*     */   @FunctionalInterface
/*     */   public static interface FieldCallback
/*     */   {
/*     */     void doWith(Field param1Field) throws IllegalArgumentException, IllegalAccessException;
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
/*     */   @FunctionalInterface
/*     */   public static interface FieldFilter
/*     */   {
/*     */     boolean matches(Field param1Field);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default FieldFilter and(FieldFilter next) {
/* 884 */       Assert.notNull(next, "Next FieldFilter must not be null");
/* 885 */       return field -> (matches(field) && next.matches(field));
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/util/ReflectionUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */