/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Executable;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.Set;
/*     */ import org.springframework.beans.factory.ObjectFactory;
/*     */ import org.springframework.beans.factory.config.TypedStringValue;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class AutowireUtils
/*     */ {
/*     */   public static final Comparator<Executable> EXECUTABLE_COMPARATOR;
/*     */   
/*     */   static {
/*  54 */     EXECUTABLE_COMPARATOR = ((e1, e2) -> {
/*     */         int result = Boolean.compare(Modifier.isPublic(e2.getModifiers()), Modifier.isPublic(e1.getModifiers()));
/*     */         return (result != 0) ? result : Integer.compare(e2.getParameterCount(), e1.getParameterCount());
/*     */       });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void sortConstructors(Constructor<?>[] constructors) {
/*  68 */     Arrays.sort((Executable[])constructors, EXECUTABLE_COMPARATOR);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void sortFactoryMethods(Method[] factoryMethods) {
/*  79 */     Arrays.sort((Executable[])factoryMethods, EXECUTABLE_COMPARATOR);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isExcludedFromDependencyCheck(PropertyDescriptor pd) {
/*  89 */     Method wm = pd.getWriteMethod();
/*  90 */     if (wm == null) {
/*  91 */       return false;
/*     */     }
/*  93 */     if (!wm.getDeclaringClass().getName().contains("$$"))
/*     */     {
/*  95 */       return false;
/*     */     }
/*     */ 
/*     */     
/*  99 */     Class<?> superclass = wm.getDeclaringClass().getSuperclass();
/* 100 */     return !ClassUtils.hasMethod(superclass, wm);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isSetterDefinedInInterface(PropertyDescriptor pd, Set<Class<?>> interfaces) {
/* 111 */     Method setter = pd.getWriteMethod();
/* 112 */     if (setter != null) {
/* 113 */       Class<?> targetClass = setter.getDeclaringClass();
/* 114 */       for (Class<?> ifc : interfaces) {
/* 115 */         if (ifc.isAssignableFrom(targetClass) && ClassUtils.hasMethod(ifc, setter)) {
/* 116 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 120 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object resolveAutowiringValue(Object autowiringValue, Class<?> requiredType) {
/* 131 */     if (autowiringValue instanceof ObjectFactory && !requiredType.isInstance(autowiringValue)) {
/* 132 */       ObjectFactory<?> factory = (ObjectFactory)autowiringValue;
/* 133 */       if (autowiringValue instanceof Serializable && requiredType.isInterface()) {
/* 134 */         autowiringValue = Proxy.newProxyInstance(requiredType.getClassLoader(), new Class[] { requiredType }, new ObjectFactoryDelegatingInvocationHandler(factory));
/*     */       }
/*     */       else {
/*     */         
/* 138 */         return factory.getObject();
/*     */       } 
/*     */     } 
/* 141 */     return autowiringValue;
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
/*     */   public static Class<?> resolveReturnTypeForFactoryMethod(Method method, Object[] args, @Nullable ClassLoader classLoader) {
/* 178 */     Assert.notNull(method, "Method must not be null");
/* 179 */     Assert.notNull(args, "Argument array must not be null");
/*     */     
/* 181 */     TypeVariable[] arrayOfTypeVariable = (TypeVariable[])method.getTypeParameters();
/* 182 */     Type genericReturnType = method.getGenericReturnType();
/* 183 */     Type[] methodParameterTypes = method.getGenericParameterTypes();
/* 184 */     Assert.isTrue((args.length == methodParameterTypes.length), "Argument array does not match parameter count");
/*     */ 
/*     */ 
/*     */     
/* 188 */     boolean locallyDeclaredTypeVariableMatchesReturnType = false;
/* 189 */     for (TypeVariable<Method> currentTypeVariable : arrayOfTypeVariable) {
/* 190 */       if (currentTypeVariable.equals(genericReturnType)) {
/* 191 */         locallyDeclaredTypeVariableMatchesReturnType = true;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 196 */     if (locallyDeclaredTypeVariableMatchesReturnType) {
/* 197 */       for (int i = 0; i < methodParameterTypes.length; i++) {
/* 198 */         Type methodParameterType = methodParameterTypes[i];
/* 199 */         Object arg = args[i];
/* 200 */         if (methodParameterType.equals(genericReturnType)) {
/* 201 */           if (arg instanceof TypedStringValue) {
/* 202 */             TypedStringValue typedValue = (TypedStringValue)arg;
/* 203 */             if (typedValue.hasTargetType()) {
/* 204 */               return typedValue.getTargetType();
/*     */             }
/*     */             try {
/* 207 */               Class<?> resolvedType = typedValue.resolveTargetType(classLoader);
/* 208 */               if (resolvedType != null) {
/* 209 */                 return resolvedType;
/*     */               }
/*     */             }
/* 212 */             catch (ClassNotFoundException ex) {
/* 213 */               throw new IllegalStateException("Failed to resolve value type [" + typedValue
/* 214 */                   .getTargetTypeName() + "] for factory method argument", ex);
/*     */             }
/*     */           
/* 217 */           } else if (arg != null && !(arg instanceof org.springframework.beans.BeanMetadataElement)) {
/*     */             
/* 219 */             return arg.getClass();
/*     */           } 
/* 221 */           return method.getReturnType();
/*     */         } 
/* 223 */         if (methodParameterType instanceof ParameterizedType) {
/* 224 */           ParameterizedType parameterizedType = (ParameterizedType)methodParameterType;
/* 225 */           Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
/* 226 */           for (Type typeArg : actualTypeArguments) {
/* 227 */             if (typeArg.equals(genericReturnType)) {
/* 228 */               if (arg instanceof Class) {
/* 229 */                 return (Class)arg;
/*     */               }
/*     */               
/* 232 */               String className = null;
/* 233 */               if (arg instanceof String) {
/* 234 */                 className = (String)arg;
/*     */               }
/* 236 */               else if (arg instanceof TypedStringValue) {
/* 237 */                 TypedStringValue typedValue = (TypedStringValue)arg;
/* 238 */                 String targetTypeName = typedValue.getTargetTypeName();
/* 239 */                 if (targetTypeName == null || Class.class.getName().equals(targetTypeName)) {
/* 240 */                   className = typedValue.getValue();
/*     */                 }
/*     */               } 
/* 243 */               if (className != null) {
/*     */                 try {
/* 245 */                   return ClassUtils.forName(className, classLoader);
/*     */                 }
/* 247 */                 catch (ClassNotFoundException ex) {
/* 248 */                   throw new IllegalStateException("Could not resolve class name [" + arg + "] for factory method argument", ex);
/*     */                 } 
/*     */               }
/*     */ 
/*     */ 
/*     */               
/* 254 */               return method.getReturnType();
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 263 */     return method.getReturnType();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class ObjectFactoryDelegatingInvocationHandler
/*     */     implements InvocationHandler, Serializable
/*     */   {
/*     */     private final ObjectFactory<?> objectFactory;
/*     */ 
/*     */ 
/*     */     
/*     */     ObjectFactoryDelegatingInvocationHandler(ObjectFactory<?> objectFactory) {
/* 276 */       this.objectFactory = objectFactory;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/* 281 */       switch (method.getName()) {
/*     */         
/*     */         case "equals":
/* 284 */           return Boolean.valueOf((proxy == args[0]));
/*     */         
/*     */         case "hashCode":
/* 287 */           return Integer.valueOf(System.identityHashCode(proxy));
/*     */         case "toString":
/* 289 */           return this.objectFactory.toString();
/*     */       } 
/*     */       try {
/* 292 */         return method.invoke(this.objectFactory.getObject(), args);
/*     */       }
/* 294 */       catch (InvocationTargetException ex) {
/* 295 */         throw ex.getTargetException();
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/AutowireUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */