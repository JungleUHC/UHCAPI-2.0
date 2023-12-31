/*     */ package org.springframework.beans;
/*     */ 
/*     */ import java.beans.ConstructorProperties;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.beans.PropertyEditor;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.time.temporal.Temporal;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import kotlin.jvm.JvmClassMappingKt;
/*     */ import kotlin.reflect.KCallable;
/*     */ import kotlin.reflect.KFunction;
/*     */ import kotlin.reflect.KParameter;
/*     */ import kotlin.reflect.full.KClasses;
/*     */ import kotlin.reflect.jvm.KCallablesJvm;
/*     */ import kotlin.reflect.jvm.ReflectJvmMapping;
/*     */ import org.springframework.core.DefaultParameterNameDiscoverer;
/*     */ import org.springframework.core.KotlinDetector;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.core.ParameterNameDiscoverer;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.ConcurrentReferenceHashMap;
/*     */ import org.springframework.util.ReflectionUtils;
/*     */ import org.springframework.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class BeanUtils
/*     */ {
/*  76 */   private static final ParameterNameDiscoverer parameterNameDiscoverer = (ParameterNameDiscoverer)new DefaultParameterNameDiscoverer();
/*     */ 
/*     */ 
/*     */   
/*  80 */   private static final Set<Class<?>> unknownEditorTypes = Collections.newSetFromMap((Map<Class<?>, Boolean>)new ConcurrentReferenceHashMap(64));
/*     */   
/*     */   private static final Map<Class<?>, Object> DEFAULT_TYPE_VALUES;
/*     */   
/*     */   static {
/*  85 */     Map<Class<?>, Object> values = new HashMap<>();
/*  86 */     values.put(boolean.class, Boolean.valueOf(false));
/*  87 */     values.put(byte.class, Byte.valueOf((byte)0));
/*  88 */     values.put(short.class, Short.valueOf((short)0));
/*  89 */     values.put(int.class, Integer.valueOf(0));
/*  90 */     values.put(long.class, Long.valueOf(0L));
/*  91 */     values.put(float.class, Float.valueOf(0.0F));
/*  92 */     values.put(double.class, Double.valueOf(0.0D));
/*  93 */     values.put(char.class, Character.valueOf(false));
/*  94 */     DEFAULT_TYPE_VALUES = Collections.unmodifiableMap(values);
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
/*     */   public static <T> T instantiate(Class<T> clazz) throws BeanInstantiationException {
/* 109 */     Assert.notNull(clazz, "Class must not be null");
/* 110 */     if (clazz.isInterface()) {
/* 111 */       throw new BeanInstantiationException(clazz, "Specified class is an interface");
/*     */     }
/*     */     try {
/* 114 */       return clazz.newInstance();
/*     */     }
/* 116 */     catch (InstantiationException ex) {
/* 117 */       throw new BeanInstantiationException(clazz, "Is it an abstract class?", ex);
/*     */     }
/* 119 */     catch (IllegalAccessException ex) {
/* 120 */       throw new BeanInstantiationException(clazz, "Is the constructor accessible?", ex);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T instantiateClass(Class<T> clazz) throws BeanInstantiationException {
/* 141 */     Assert.notNull(clazz, "Class must not be null");
/* 142 */     if (clazz.isInterface()) {
/* 143 */       throw new BeanInstantiationException(clazz, "Specified class is an interface");
/*     */     }
/*     */     try {
/* 146 */       return instantiateClass(clazz.getDeclaredConstructor(new Class[0]), new Object[0]);
/*     */     }
/* 148 */     catch (NoSuchMethodException ex) {
/* 149 */       Constructor<T> ctor = findPrimaryConstructor(clazz);
/* 150 */       if (ctor != null) {
/* 151 */         return instantiateClass(ctor, new Object[0]);
/*     */       }
/* 153 */       throw new BeanInstantiationException(clazz, "No default constructor found", ex);
/*     */     }
/* 155 */     catch (LinkageError err) {
/* 156 */       throw new BeanInstantiationException(clazz, "Unresolvable class definition", err);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T instantiateClass(Class<?> clazz, Class<T> assignableTo) throws BeanInstantiationException {
/* 175 */     Assert.isAssignable(assignableTo, clazz);
/* 176 */     return instantiateClass((Class)clazz);
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
/*     */   public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws BeanInstantiationException {
/* 192 */     Assert.notNull(ctor, "Constructor must not be null");
/*     */     try {
/* 194 */       ReflectionUtils.makeAccessible(ctor);
/* 195 */       if (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(ctor.getDeclaringClass())) {
/* 196 */         return KotlinDelegate.instantiateClass(ctor, args);
/*     */       }
/*     */       
/* 199 */       Class<?>[] parameterTypes = ctor.getParameterTypes();
/* 200 */       Assert.isTrue((args.length <= parameterTypes.length), "Can't specify more arguments than constructor parameters");
/* 201 */       Object[] argsWithDefaultValues = new Object[args.length];
/* 202 */       for (int i = 0; i < args.length; i++) {
/* 203 */         if (args[i] == null) {
/* 204 */           Class<?> parameterType = parameterTypes[i];
/* 205 */           argsWithDefaultValues[i] = parameterType.isPrimitive() ? DEFAULT_TYPE_VALUES.get(parameterType) : null;
/*     */         } else {
/*     */           
/* 208 */           argsWithDefaultValues[i] = args[i];
/*     */         } 
/*     */       } 
/* 211 */       return ctor.newInstance(argsWithDefaultValues);
/*     */     
/*     */     }
/* 214 */     catch (InstantiationException ex) {
/* 215 */       throw new BeanInstantiationException(ctor, "Is it an abstract class?", ex);
/*     */     }
/* 217 */     catch (IllegalAccessException ex) {
/* 218 */       throw new BeanInstantiationException(ctor, "Is the constructor accessible?", ex);
/*     */     }
/* 220 */     catch (IllegalArgumentException ex) {
/* 221 */       throw new BeanInstantiationException(ctor, "Illegal arguments for constructor", ex);
/*     */     }
/* 223 */     catch (InvocationTargetException ex) {
/* 224 */       throw new BeanInstantiationException(ctor, "Constructor threw exception", ex.getTargetException());
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
/*     */   public static <T> Constructor<T> getResolvableConstructor(Class<T> clazz) {
/* 240 */     Constructor<T> ctor = findPrimaryConstructor(clazz);
/* 241 */     if (ctor != null) {
/* 242 */       return ctor;
/*     */     }
/*     */     
/* 245 */     Constructor[] arrayOfConstructor = (Constructor[])clazz.getConstructors();
/* 246 */     if (arrayOfConstructor.length == 1)
/*     */     {
/* 248 */       return arrayOfConstructor[0];
/*     */     }
/* 250 */     if (arrayOfConstructor.length == 0) {
/* 251 */       arrayOfConstructor = (Constructor[])clazz.getDeclaredConstructors();
/* 252 */       if (arrayOfConstructor.length == 1)
/*     */       {
/* 254 */         return arrayOfConstructor[0];
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 260 */       return clazz.getDeclaredConstructor(new Class[0]);
/*     */     }
/* 262 */     catch (NoSuchMethodException noSuchMethodException) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 267 */       throw new IllegalStateException("No primary or single unique constructor found for " + clazz);
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
/*     */   @Nullable
/*     */   public static <T> Constructor<T> findPrimaryConstructor(Class<T> clazz) {
/* 281 */     Assert.notNull(clazz, "Class must not be null");
/* 282 */     if (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(clazz)) {
/* 283 */       return KotlinDelegate.findPrimaryConstructor(clazz);
/*     */     }
/* 285 */     return null;
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
/*     */   @Nullable
/*     */   public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
/*     */     try {
/* 305 */       return clazz.getMethod(methodName, paramTypes);
/*     */     }
/* 307 */     catch (NoSuchMethodException ex) {
/* 308 */       return findDeclaredMethod(clazz, methodName, paramTypes);
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
/*     */   public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
/*     */     try {
/* 326 */       return clazz.getDeclaredMethod(methodName, paramTypes);
/*     */     }
/* 328 */     catch (NoSuchMethodException ex) {
/* 329 */       if (clazz.getSuperclass() != null) {
/* 330 */         return findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
/*     */       }
/* 332 */       return null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static Method findMethodWithMinimalParameters(Class<?> clazz, String methodName) throws IllegalArgumentException {
/* 355 */     Method targetMethod = findMethodWithMinimalParameters(clazz.getMethods(), methodName);
/* 356 */     if (targetMethod == null) {
/* 357 */       targetMethod = findDeclaredMethodWithMinimalParameters(clazz, methodName);
/*     */     }
/* 359 */     return targetMethod;
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
/*     */   @Nullable
/*     */   public static Method findDeclaredMethodWithMinimalParameters(Class<?> clazz, String methodName) throws IllegalArgumentException {
/* 378 */     Method targetMethod = findMethodWithMinimalParameters(clazz.getDeclaredMethods(), methodName);
/* 379 */     if (targetMethod == null && clazz.getSuperclass() != null) {
/* 380 */       targetMethod = findDeclaredMethodWithMinimalParameters(clazz.getSuperclass(), methodName);
/*     */     }
/* 382 */     return targetMethod;
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
/*     */   public static Method findMethodWithMinimalParameters(Method[] methods, String methodName) throws IllegalArgumentException {
/* 398 */     Method targetMethod = null;
/* 399 */     int numMethodsFoundWithCurrentMinimumArgs = 0;
/* 400 */     for (Method method : methods) {
/* 401 */       if (method.getName().equals(methodName)) {
/* 402 */         int numParams = method.getParameterCount();
/* 403 */         if (targetMethod == null || numParams < targetMethod.getParameterCount()) {
/* 404 */           targetMethod = method;
/* 405 */           numMethodsFoundWithCurrentMinimumArgs = 1;
/*     */         }
/* 407 */         else if (!method.isBridge() && targetMethod.getParameterCount() == numParams) {
/* 408 */           if (targetMethod.isBridge()) {
/*     */             
/* 410 */             targetMethod = method;
/*     */           }
/*     */           else {
/*     */             
/* 414 */             numMethodsFoundWithCurrentMinimumArgs++;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 419 */     if (numMethodsFoundWithCurrentMinimumArgs > 1) {
/* 420 */       throw new IllegalArgumentException("Cannot resolve method '" + methodName + "' to a unique method. Attempted to resolve to overloaded method with the least number of parameters but there were " + numMethodsFoundWithCurrentMinimumArgs + " candidates.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 425 */     return targetMethod;
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
/*     */   @Nullable
/*     */   public static Method resolveSignature(String signature, Class<?> clazz) {
/* 448 */     Assert.hasText(signature, "'signature' must not be empty");
/* 449 */     Assert.notNull(clazz, "Class must not be null");
/* 450 */     int startParen = signature.indexOf('(');
/* 451 */     int endParen = signature.indexOf(')');
/* 452 */     if (startParen > -1 && endParen == -1) {
/* 453 */       throw new IllegalArgumentException("Invalid method signature '" + signature + "': expected closing ')' for args list");
/*     */     }
/*     */     
/* 456 */     if (startParen == -1 && endParen > -1) {
/* 457 */       throw new IllegalArgumentException("Invalid method signature '" + signature + "': expected opening '(' for args list");
/*     */     }
/*     */     
/* 460 */     if (startParen == -1) {
/* 461 */       return findMethodWithMinimalParameters(clazz, signature);
/*     */     }
/*     */     
/* 464 */     String methodName = signature.substring(0, startParen);
/*     */     
/* 466 */     String[] parameterTypeNames = StringUtils.commaDelimitedListToStringArray(signature.substring(startParen + 1, endParen));
/* 467 */     Class<?>[] parameterTypes = new Class[parameterTypeNames.length];
/* 468 */     for (int i = 0; i < parameterTypeNames.length; i++) {
/* 469 */       String parameterTypeName = parameterTypeNames[i].trim();
/*     */       try {
/* 471 */         parameterTypes[i] = ClassUtils.forName(parameterTypeName, clazz.getClassLoader());
/*     */       }
/* 473 */       catch (Throwable ex) {
/* 474 */         throw new IllegalArgumentException("Invalid method signature: unable to resolve type [" + parameterTypeName + "] for argument " + i + ". Root cause: " + ex);
/*     */       } 
/*     */     } 
/*     */     
/* 478 */     return findMethod(clazz, methodName, parameterTypes);
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
/*     */   public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws BeansException {
/* 490 */     return CachedIntrospectionResults.forClass(clazz).getPropertyDescriptors();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName) throws BeansException {
/* 502 */     return CachedIntrospectionResults.forClass(clazz).getPropertyDescriptor(propertyName);
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
/*     */   public static PropertyDescriptor findPropertyForMethod(Method method) throws BeansException {
/* 516 */     return findPropertyForMethod(method, method.getDeclaringClass());
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
/*     */   public static PropertyDescriptor findPropertyForMethod(Method method, Class<?> clazz) throws BeansException {
/* 531 */     Assert.notNull(method, "Method must not be null");
/* 532 */     PropertyDescriptor[] pds = getPropertyDescriptors(clazz);
/* 533 */     for (PropertyDescriptor pd : pds) {
/* 534 */       if (method.equals(pd.getReadMethod()) || method.equals(pd.getWriteMethod())) {
/* 535 */         return pd;
/*     */       }
/*     */     } 
/* 538 */     return null;
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
/*     */   public static PropertyEditor findEditorByConvention(@Nullable Class<?> targetType) {
/* 552 */     if (targetType == null || targetType.isArray() || unknownEditorTypes.contains(targetType)) {
/* 553 */       return null;
/*     */     }
/*     */     
/* 556 */     ClassLoader cl = targetType.getClassLoader();
/* 557 */     if (cl == null) {
/*     */       try {
/* 559 */         cl = ClassLoader.getSystemClassLoader();
/* 560 */         if (cl == null) {
/* 561 */           return null;
/*     */         }
/*     */       }
/* 564 */       catch (Throwable ex) {
/*     */         
/* 566 */         return null;
/*     */       } 
/*     */     }
/*     */     
/* 570 */     String targetTypeName = targetType.getName();
/* 571 */     String editorName = targetTypeName + "Editor";
/*     */     try {
/* 573 */       Class<?> editorClass = cl.loadClass(editorName);
/* 574 */       if (editorClass != null) {
/* 575 */         if (!PropertyEditor.class.isAssignableFrom(editorClass)) {
/* 576 */           unknownEditorTypes.add(targetType);
/* 577 */           return null;
/*     */         } 
/* 579 */         return (PropertyEditor)instantiateClass(editorClass);
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 584 */     catch (ClassNotFoundException classNotFoundException) {}
/*     */ 
/*     */     
/* 587 */     unknownEditorTypes.add(targetType);
/* 588 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Class<?> findPropertyType(String propertyName, @Nullable Class<?>... beanClasses) {
/* 599 */     if (beanClasses != null) {
/* 600 */       for (Class<?> beanClass : beanClasses) {
/* 601 */         PropertyDescriptor pd = getPropertyDescriptor(beanClass, propertyName);
/* 602 */         if (pd != null) {
/* 603 */           return pd.getPropertyType();
/*     */         }
/*     */       } 
/*     */     }
/* 607 */     return Object.class;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MethodParameter getWriteMethodParameter(PropertyDescriptor pd) {
/* 617 */     if (pd instanceof GenericTypeAwarePropertyDescriptor) {
/* 618 */       return new MethodParameter(((GenericTypeAwarePropertyDescriptor)pd).getWriteMethodParameter());
/*     */     }
/*     */     
/* 621 */     Method writeMethod = pd.getWriteMethod();
/* 622 */     Assert.state((writeMethod != null), "No write method available");
/* 623 */     return new MethodParameter(writeMethod, 0);
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
/*     */   public static String[] getParameterNames(Constructor<?> ctor) {
/* 639 */     ConstructorProperties cp = ctor.<ConstructorProperties>getAnnotation(ConstructorProperties.class);
/* 640 */     String[] paramNames = (cp != null) ? cp.value() : parameterNameDiscoverer.getParameterNames(ctor);
/* 641 */     Assert.state((paramNames != null), () -> "Cannot resolve parameter names for constructor " + ctor);
/* 642 */     Assert.state((paramNames.length == ctor.getParameterCount()), () -> "Invalid number of parameter names: " + paramNames.length + " for constructor " + ctor);
/*     */     
/* 644 */     return paramNames;
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
/*     */   public static boolean isSimpleProperty(Class<?> type) {
/* 660 */     Assert.notNull(type, "'type' must not be null");
/* 661 */     return (isSimpleValueType(type) || (type.isArray() && isSimpleValueType(type.getComponentType())));
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
/*     */   public static boolean isSimpleValueType(Class<?> type) {
/* 674 */     return (Void.class != type && void.class != type && (
/* 675 */       ClassUtils.isPrimitiveOrWrapper(type) || Enum.class
/* 676 */       .isAssignableFrom(type) || CharSequence.class
/* 677 */       .isAssignableFrom(type) || Number.class
/* 678 */       .isAssignableFrom(type) || Date.class
/* 679 */       .isAssignableFrom(type) || Temporal.class
/* 680 */       .isAssignableFrom(type) || URI.class == type || URL.class == type || Locale.class == type || Class.class == type));
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
/*     */   public static void copyProperties(Object source, Object target) throws BeansException {
/* 719 */     copyProperties(source, target, null, (String[])null);
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
/*     */   public static void copyProperties(Object source, Object target, Class<?> editable) throws BeansException {
/* 740 */     copyProperties(source, target, editable, (String[])null);
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
/*     */   public static void copyProperties(Object source, Object target, String... ignoreProperties) throws BeansException {
/* 761 */     copyProperties(source, target, null, ignoreProperties);
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
/*     */   private static void copyProperties(Object source, Object target, @Nullable Class<?> editable, @Nullable String... ignoreProperties) throws BeansException {
/* 782 */     Assert.notNull(source, "Source must not be null");
/* 783 */     Assert.notNull(target, "Target must not be null");
/*     */     
/* 785 */     Class<?> actualEditable = target.getClass();
/* 786 */     if (editable != null) {
/* 787 */       if (!editable.isInstance(target)) {
/* 788 */         throw new IllegalArgumentException("Target class [" + target.getClass().getName() + "] not assignable to Editable class [" + editable
/* 789 */             .getName() + "]");
/*     */       }
/* 791 */       actualEditable = editable;
/*     */     } 
/* 793 */     PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
/* 794 */     List<String> ignoreList = (ignoreProperties != null) ? Arrays.<String>asList(ignoreProperties) : null;
/*     */     
/* 796 */     for (PropertyDescriptor targetPd : targetPds) {
/* 797 */       Method writeMethod = targetPd.getWriteMethod();
/* 798 */       if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
/* 799 */         PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
/* 800 */         if (sourcePd != null) {
/* 801 */           Method readMethod = sourcePd.getReadMethod();
/* 802 */           if (readMethod != null) {
/* 803 */             ResolvableType sourceResolvableType = ResolvableType.forMethodReturnType(readMethod);
/* 804 */             ResolvableType targetResolvableType = ResolvableType.forMethodParameter(writeMethod, 0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 810 */             boolean isAssignable = (sourceResolvableType.hasUnresolvableGenerics() || targetResolvableType.hasUnresolvableGenerics()) ? ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType()) : targetResolvableType.isAssignableFrom(sourceResolvableType);
/*     */             
/* 812 */             if (isAssignable) {
/*     */               try {
/* 814 */                 if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
/* 815 */                   readMethod.setAccessible(true);
/*     */                 }
/* 817 */                 Object value = readMethod.invoke(source, new Object[0]);
/* 818 */                 if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
/* 819 */                   writeMethod.setAccessible(true);
/*     */                 }
/* 821 */                 writeMethod.invoke(target, new Object[] { value });
/*     */               }
/* 823 */               catch (Throwable ex) {
/* 824 */                 throw new FatalBeanException("Could not copy property '" + targetPd
/* 825 */                     .getName() + "' from source to target", ex);
/*     */               } 
/*     */             }
/*     */           } 
/*     */         } 
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
/*     */   private static class KotlinDelegate
/*     */   {
/*     */     @Nullable
/*     */     public static <T> Constructor<T> findPrimaryConstructor(Class<T> clazz) {
/*     */       try {
/* 849 */         KFunction<T> primaryCtor = KClasses.getPrimaryConstructor(JvmClassMappingKt.getKotlinClass(clazz));
/* 850 */         if (primaryCtor == null) {
/* 851 */           return null;
/*     */         }
/* 853 */         Constructor<T> constructor = ReflectJvmMapping.getJavaConstructor(primaryCtor);
/* 854 */         if (constructor == null) {
/* 855 */           throw new IllegalStateException("Failed to find Java constructor for Kotlin primary constructor: " + clazz
/* 856 */               .getName());
/*     */         }
/* 858 */         return constructor;
/*     */       }
/* 860 */       catch (UnsupportedOperationException ex) {
/* 861 */         return null;
/*     */       } 
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
/*     */     public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
/* 874 */       KFunction<T> kotlinConstructor = ReflectJvmMapping.getKotlinFunction(ctor);
/* 875 */       if (kotlinConstructor == null) {
/* 876 */         return ctor.newInstance(args);
/*     */       }
/*     */       
/* 879 */       if (!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) {
/* 880 */         KCallablesJvm.setAccessible((KCallable)kotlinConstructor, true);
/*     */       }
/*     */       
/* 883 */       List<KParameter> parameters = kotlinConstructor.getParameters();
/* 884 */       Map<KParameter, Object> argParameters = CollectionUtils.newHashMap(parameters.size());
/* 885 */       Assert.isTrue((args.length <= parameters.size()), "Number of provided arguments should be less of equals than number of constructor parameters");
/*     */       
/* 887 */       for (int i = 0; i < args.length; i++) {
/* 888 */         if (!((KParameter)parameters.get(i)).isOptional() || args[i] != null) {
/* 889 */           argParameters.put(parameters.get(i), args[i]);
/*     */         }
/*     */       } 
/* 892 */       return (T)kotlinConstructor.callBy(argParameters);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/BeanUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */