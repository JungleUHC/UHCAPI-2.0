/*     */ package org.springframework.beans;
/*     */ 
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.security.ProtectionDomain;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.core.SpringProperties;
/*     */ import org.springframework.core.convert.TypeDescriptor;
/*     */ import org.springframework.core.io.support.SpringFactoriesLoader;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.ConcurrentReferenceHashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CachedIntrospectionResults
/*     */ {
/*     */   public static final String IGNORE_BEANINFO_PROPERTY_NAME = "spring.beaninfo.ignore";
/*  99 */   private static final PropertyDescriptor[] EMPTY_PROPERTY_DESCRIPTOR_ARRAY = new PropertyDescriptor[0];
/*     */ 
/*     */ 
/*     */   
/* 103 */   private static final boolean shouldIntrospectorIgnoreBeaninfoClasses = SpringProperties.getFlag("spring.beaninfo.ignore");
/*     */ 
/*     */   
/* 106 */   private static final List<BeanInfoFactory> beanInfoFactories = SpringFactoriesLoader.loadFactories(BeanInfoFactory.class, CachedIntrospectionResults.class
/* 107 */       .getClassLoader());
/*     */   
/* 109 */   private static final Log logger = LogFactory.getLog(CachedIntrospectionResults.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 116 */   static final Set<ClassLoader> acceptedClassLoaders = Collections.newSetFromMap(new ConcurrentHashMap<>(16));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 122 */   static final ConcurrentMap<Class<?>, CachedIntrospectionResults> strongClassCache = new ConcurrentHashMap<>(64);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 129 */   static final ConcurrentMap<Class<?>, CachedIntrospectionResults> softClassCache = (ConcurrentMap<Class<?>, CachedIntrospectionResults>)new ConcurrentReferenceHashMap(64);
/*     */ 
/*     */ 
/*     */   
/*     */   private final BeanInfo beanInfo;
/*     */ 
/*     */ 
/*     */   
/*     */   private final Map<String, PropertyDescriptor> propertyDescriptors;
/*     */ 
/*     */ 
/*     */   
/*     */   private final ConcurrentMap<PropertyDescriptor, TypeDescriptor> typeDescriptorCache;
/*     */ 
/*     */ 
/*     */   
/*     */   public static void acceptClassLoader(@Nullable ClassLoader classLoader) {
/* 146 */     if (classLoader != null) {
/* 147 */       acceptedClassLoaders.add(classLoader);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void clearClassLoader(@Nullable ClassLoader classLoader) {
/* 158 */     acceptedClassLoaders.removeIf(registeredLoader -> isUnderneathClassLoader(registeredLoader, classLoader));
/*     */     
/* 160 */     strongClassCache.keySet().removeIf(beanClass -> isUnderneathClassLoader(beanClass.getClassLoader(), classLoader));
/*     */     
/* 162 */     softClassCache.keySet().removeIf(beanClass -> isUnderneathClassLoader(beanClass.getClassLoader(), classLoader));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static CachedIntrospectionResults forClass(Class<?> beanClass) throws BeansException {
/*     */     ConcurrentMap<Class<?>, CachedIntrospectionResults> classCacheToUse;
/* 173 */     CachedIntrospectionResults results = strongClassCache.get(beanClass);
/* 174 */     if (results != null) {
/* 175 */       return results;
/*     */     }
/* 177 */     results = softClassCache.get(beanClass);
/* 178 */     if (results != null) {
/* 179 */       return results;
/*     */     }
/*     */     
/* 182 */     results = new CachedIntrospectionResults(beanClass);
/*     */ 
/*     */     
/* 185 */     if (ClassUtils.isCacheSafe(beanClass, CachedIntrospectionResults.class.getClassLoader()) || 
/* 186 */       isClassLoaderAccepted(beanClass.getClassLoader())) {
/* 187 */       classCacheToUse = strongClassCache;
/*     */     } else {
/*     */       
/* 190 */       if (logger.isDebugEnabled()) {
/* 191 */         logger.debug("Not strongly caching class [" + beanClass.getName() + "] because it is not cache-safe");
/*     */       }
/* 193 */       classCacheToUse = softClassCache;
/*     */     } 
/*     */     
/* 196 */     CachedIntrospectionResults existing = classCacheToUse.putIfAbsent(beanClass, results);
/* 197 */     return (existing != null) ? existing : results;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isClassLoaderAccepted(ClassLoader classLoader) {
/* 208 */     for (ClassLoader acceptedLoader : acceptedClassLoaders) {
/* 209 */       if (isUnderneathClassLoader(classLoader, acceptedLoader)) {
/* 210 */         return true;
/*     */       }
/*     */     } 
/* 213 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isUnderneathClassLoader(@Nullable ClassLoader candidate, @Nullable ClassLoader parent) {
/* 223 */     if (candidate == parent) {
/* 224 */       return true;
/*     */     }
/* 226 */     if (candidate == null) {
/* 227 */       return false;
/*     */     }
/* 229 */     ClassLoader classLoaderToCheck = candidate;
/* 230 */     while (classLoaderToCheck != null) {
/* 231 */       classLoaderToCheck = classLoaderToCheck.getParent();
/* 232 */       if (classLoaderToCheck == parent) {
/* 233 */         return true;
/*     */       }
/*     */     } 
/* 236 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
/* 246 */     for (BeanInfoFactory beanInfoFactory : beanInfoFactories) {
/* 247 */       BeanInfo beanInfo = beanInfoFactory.getBeanInfo(beanClass);
/* 248 */       if (beanInfo != null) {
/* 249 */         return beanInfo;
/*     */       }
/*     */     } 
/* 252 */     return shouldIntrospectorIgnoreBeaninfoClasses ? 
/* 253 */       Introspector.getBeanInfo(beanClass, 3) : 
/* 254 */       Introspector.getBeanInfo(beanClass);
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
/*     */   private CachedIntrospectionResults(Class<?> beanClass) throws BeansException {
/*     */     try {
/* 275 */       if (logger.isTraceEnabled()) {
/* 276 */         logger.trace("Getting BeanInfo for class [" + beanClass.getName() + "]");
/*     */       }
/* 278 */       this.beanInfo = getBeanInfo(beanClass);
/*     */       
/* 280 */       if (logger.isTraceEnabled()) {
/* 281 */         logger.trace("Caching PropertyDescriptors for class [" + beanClass.getName() + "]");
/*     */       }
/* 283 */       this.propertyDescriptors = new LinkedHashMap<>();
/*     */       
/* 285 */       Set<String> readMethodNames = new HashSet<>();
/*     */ 
/*     */       
/* 288 */       PropertyDescriptor[] pds = this.beanInfo.getPropertyDescriptors();
/* 289 */       for (PropertyDescriptor pd : pds) {
/* 290 */         if (Class.class != beanClass || "name".equals(pd.getName()) || pd.getName().endsWith("Name"))
/*     */         {
/*     */ 
/*     */           
/* 294 */           if (pd.getPropertyType() == null || (!ClassLoader.class.isAssignableFrom(pd.getPropertyType()) && 
/* 295 */             !ProtectionDomain.class.isAssignableFrom(pd.getPropertyType()))) {
/*     */ 
/*     */ 
/*     */             
/* 299 */             if (logger.isTraceEnabled()) {
/* 300 */               logger.trace("Found bean property '" + pd.getName() + "'" + (
/* 301 */                   (pd.getPropertyType() != null) ? (" of type [" + pd.getPropertyType().getName() + "]") : "") + (
/* 302 */                   (pd.getPropertyEditorClass() != null) ? ("; editor [" + pd
/* 303 */                   .getPropertyEditorClass().getName() + "]") : ""));
/*     */             }
/* 305 */             pd = buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
/* 306 */             this.propertyDescriptors.put(pd.getName(), pd);
/* 307 */             Method readMethod = pd.getReadMethod();
/* 308 */             if (readMethod != null) {
/* 309 */               readMethodNames.add(readMethod.getName());
/*     */             }
/*     */           } 
/*     */         }
/*     */       } 
/*     */       
/* 315 */       Class<?> currClass = beanClass;
/* 316 */       while (currClass != null && currClass != Object.class) {
/* 317 */         introspectInterfaces(beanClass, currClass, readMethodNames);
/* 318 */         currClass = currClass.getSuperclass();
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 324 */       introspectPlainAccessors(beanClass, readMethodNames);
/*     */       
/* 326 */       this.typeDescriptorCache = (ConcurrentMap<PropertyDescriptor, TypeDescriptor>)new ConcurrentReferenceHashMap();
/*     */     }
/* 328 */     catch (IntrospectionException ex) {
/* 329 */       throw new FatalBeanException("Failed to obtain BeanInfo for class [" + beanClass.getName() + "]", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void introspectInterfaces(Class<?> beanClass, Class<?> currClass, Set<String> readMethodNames) throws IntrospectionException {
/* 336 */     for (Class<?> ifc : currClass.getInterfaces()) {
/* 337 */       if (!ClassUtils.isJavaLanguageInterface(ifc)) {
/* 338 */         for (PropertyDescriptor pd : getBeanInfo(ifc).getPropertyDescriptors()) {
/* 339 */           PropertyDescriptor existingPd = this.propertyDescriptors.get(pd.getName());
/* 340 */           if (existingPd == null || (existingPd
/* 341 */             .getReadMethod() == null && pd.getReadMethod() != null)) {
/*     */ 
/*     */             
/* 344 */             pd = buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
/* 345 */             if (pd.getPropertyType() == null || (!ClassLoader.class.isAssignableFrom(pd.getPropertyType()) && 
/* 346 */               !ProtectionDomain.class.isAssignableFrom(pd.getPropertyType()))) {
/*     */ 
/*     */ 
/*     */               
/* 350 */               this.propertyDescriptors.put(pd.getName(), pd);
/* 351 */               Method readMethod = pd.getReadMethod();
/* 352 */               if (readMethod != null)
/* 353 */                 readMethodNames.add(readMethod.getName()); 
/*     */             } 
/*     */           } 
/*     */         } 
/* 357 */         introspectInterfaces(ifc, ifc, readMethodNames);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void introspectPlainAccessors(Class<?> beanClass, Set<String> readMethodNames) throws IntrospectionException {
/* 365 */     for (Method method : beanClass.getMethods()) {
/* 366 */       if (!this.propertyDescriptors.containsKey(method.getName()) && 
/* 367 */         !readMethodNames.contains(method.getName()) && isPlainAccessor(method)) {
/* 368 */         this.propertyDescriptors.put(method.getName(), new GenericTypeAwarePropertyDescriptor(beanClass, method
/* 369 */               .getName(), method, null, null));
/* 370 */         readMethodNames.add(method.getName());
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isPlainAccessor(Method method) {
/* 376 */     if (method.getParameterCount() > 0 || method.getReturnType() == void.class || method
/* 377 */       .getDeclaringClass() == Object.class || Modifier.isStatic(method.getModifiers())) {
/* 378 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 382 */       method.getDeclaringClass().getDeclaredField(method.getName());
/* 383 */       return true;
/*     */     }
/* 385 */     catch (Exception ex) {
/* 386 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   BeanInfo getBeanInfo() {
/* 392 */     return this.beanInfo;
/*     */   }
/*     */   
/*     */   Class<?> getBeanClass() {
/* 396 */     return this.beanInfo.getBeanDescriptor().getBeanClass();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   PropertyDescriptor getPropertyDescriptor(String name) {
/* 401 */     PropertyDescriptor pd = this.propertyDescriptors.get(name);
/* 402 */     if (pd == null && StringUtils.hasLength(name)) {
/*     */       
/* 404 */       pd = this.propertyDescriptors.get(StringUtils.uncapitalize(name));
/* 405 */       if (pd == null) {
/* 406 */         pd = this.propertyDescriptors.get(StringUtils.capitalize(name));
/*     */       }
/*     */     } 
/* 409 */     return pd;
/*     */   }
/*     */   
/*     */   PropertyDescriptor[] getPropertyDescriptors() {
/* 413 */     return (PropertyDescriptor[])this.propertyDescriptors.values().toArray((Object[])EMPTY_PROPERTY_DESCRIPTOR_ARRAY);
/*     */   }
/*     */   
/*     */   private PropertyDescriptor buildGenericTypeAwarePropertyDescriptor(Class<?> beanClass, PropertyDescriptor pd) {
/*     */     try {
/* 418 */       return new GenericTypeAwarePropertyDescriptor(beanClass, pd.getName(), pd.getReadMethod(), pd
/* 419 */           .getWriteMethod(), pd.getPropertyEditorClass());
/*     */     }
/* 421 */     catch (IntrospectionException ex) {
/* 422 */       throw new FatalBeanException("Failed to re-introspect class [" + beanClass.getName() + "]", ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   TypeDescriptor addTypeDescriptor(PropertyDescriptor pd, TypeDescriptor td) {
/* 427 */     TypeDescriptor existing = this.typeDescriptorCache.putIfAbsent(pd, td);
/* 428 */     return (existing != null) ? existing : td;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   TypeDescriptor getTypeDescriptor(PropertyDescriptor pd) {
/* 433 */     return this.typeDescriptorCache.get(pd);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/CachedIntrospectionResults.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */