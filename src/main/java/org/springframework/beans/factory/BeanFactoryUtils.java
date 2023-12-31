/*     */ package org.springframework.beans.factory;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.springframework.beans.BeansException;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
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
/*     */ public abstract class BeanFactoryUtils
/*     */ {
/*     */   public static final String GENERATED_BEAN_NAME_SEPARATOR = "#";
/*  60 */   private static final Map<String, String> transformedBeanNameCache = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isFactoryDereference(@Nullable String name) {
/*  71 */     return (name != null && name.startsWith("&"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String transformedBeanName(String name) {
/*  82 */     Assert.notNull(name, "'name' must not be null");
/*  83 */     if (!name.startsWith("&")) {
/*  84 */       return name;
/*     */     }
/*  86 */     return transformedBeanNameCache.computeIfAbsent(name, beanName -> {
/*     */           while (true) {
/*     */             beanName = beanName.substring("&".length());
/*     */             if (!beanName.startsWith("&")) {
/*     */               return beanName;
/*     */             }
/*     */           } 
/*     */         });
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
/*     */   public static boolean isGeneratedBeanName(@Nullable String name) {
/* 105 */     return (name != null && name.contains("#"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String originalBeanName(String name) {
/* 116 */     Assert.notNull(name, "'name' must not be null");
/* 117 */     int separatorIndex = name.indexOf("#");
/* 118 */     return (separatorIndex != -1) ? name.substring(0, separatorIndex) : name;
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
/*     */   public static int countBeansIncludingAncestors(ListableBeanFactory lbf) {
/* 134 */     return (beanNamesIncludingAncestors(lbf)).length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String[] beanNamesIncludingAncestors(ListableBeanFactory lbf) {
/* 144 */     return beanNamesForTypeIncludingAncestors(lbf, Object.class);
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
/*     */   public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, ResolvableType type) {
/* 162 */     Assert.notNull(lbf, "ListableBeanFactory must not be null");
/* 163 */     String[] result = lbf.getBeanNamesForType(type);
/* 164 */     if (lbf instanceof HierarchicalBeanFactory) {
/* 165 */       HierarchicalBeanFactory hbf = (HierarchicalBeanFactory)lbf;
/* 166 */       if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
/* 167 */         String[] parentResult = beanNamesForTypeIncludingAncestors((ListableBeanFactory)hbf
/* 168 */             .getParentBeanFactory(), type);
/* 169 */         result = mergeNamesWithParent(result, parentResult, hbf);
/*     */       } 
/*     */     } 
/* 172 */     return result;
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
/*     */   public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
/* 200 */     Assert.notNull(lbf, "ListableBeanFactory must not be null");
/* 201 */     String[] result = lbf.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
/* 202 */     if (lbf instanceof HierarchicalBeanFactory) {
/* 203 */       HierarchicalBeanFactory hbf = (HierarchicalBeanFactory)lbf;
/* 204 */       if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
/* 205 */         String[] parentResult = beanNamesForTypeIncludingAncestors((ListableBeanFactory)hbf
/* 206 */             .getParentBeanFactory(), type, includeNonSingletons, allowEagerInit);
/* 207 */         result = mergeNamesWithParent(result, parentResult, hbf);
/*     */       } 
/*     */     } 
/* 210 */     return result;
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
/*     */   public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, Class<?> type) {
/* 227 */     Assert.notNull(lbf, "ListableBeanFactory must not be null");
/* 228 */     String[] result = lbf.getBeanNamesForType(type);
/* 229 */     if (lbf instanceof HierarchicalBeanFactory) {
/* 230 */       HierarchicalBeanFactory hbf = (HierarchicalBeanFactory)lbf;
/* 231 */       if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
/* 232 */         String[] parentResult = beanNamesForTypeIncludingAncestors((ListableBeanFactory)hbf
/* 233 */             .getParentBeanFactory(), type);
/* 234 */         result = mergeNamesWithParent(result, parentResult, hbf);
/*     */       } 
/*     */     } 
/* 237 */     return result;
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
/*     */   public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
/* 264 */     Assert.notNull(lbf, "ListableBeanFactory must not be null");
/* 265 */     String[] result = lbf.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
/* 266 */     if (lbf instanceof HierarchicalBeanFactory) {
/* 267 */       HierarchicalBeanFactory hbf = (HierarchicalBeanFactory)lbf;
/* 268 */       if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
/* 269 */         String[] parentResult = beanNamesForTypeIncludingAncestors((ListableBeanFactory)hbf
/* 270 */             .getParentBeanFactory(), type, includeNonSingletons, allowEagerInit);
/* 271 */         result = mergeNamesWithParent(result, parentResult, hbf);
/*     */       } 
/*     */     } 
/* 274 */     return result;
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
/*     */   public static String[] beanNamesForAnnotationIncludingAncestors(ListableBeanFactory lbf, Class<? extends Annotation> annotationType) {
/* 290 */     Assert.notNull(lbf, "ListableBeanFactory must not be null");
/* 291 */     String[] result = lbf.getBeanNamesForAnnotation(annotationType);
/* 292 */     if (lbf instanceof HierarchicalBeanFactory) {
/* 293 */       HierarchicalBeanFactory hbf = (HierarchicalBeanFactory)lbf;
/* 294 */       if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
/* 295 */         String[] parentResult = beanNamesForAnnotationIncludingAncestors((ListableBeanFactory)hbf
/* 296 */             .getParentBeanFactory(), annotationType);
/* 297 */         result = mergeNamesWithParent(result, parentResult, hbf);
/*     */       } 
/*     */     } 
/* 300 */     return result;
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
/*     */   public static <T> Map<String, T> beansOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type) throws BeansException {
/* 327 */     Assert.notNull(lbf, "ListableBeanFactory must not be null");
/* 328 */     Map<String, T> result = new LinkedHashMap<>(4);
/* 329 */     result.putAll(lbf.getBeansOfType(type));
/* 330 */     if (lbf instanceof HierarchicalBeanFactory) {
/* 331 */       HierarchicalBeanFactory hbf = (HierarchicalBeanFactory)lbf;
/* 332 */       if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
/* 333 */         Map<String, T> parentResult = beansOfTypeIncludingAncestors((ListableBeanFactory)hbf
/* 334 */             .getParentBeanFactory(), type);
/* 335 */         parentResult.forEach((beanName, beanInstance) -> {
/*     */               if (!result.containsKey(beanName) && !hbf.containsLocalBean(beanName)) {
/*     */                 result.put(beanName, beanInstance);
/*     */               }
/*     */             });
/*     */       } 
/*     */     } 
/* 342 */     return result;
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
/*     */   public static <T> Map<String, T> beansOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
/* 376 */     Assert.notNull(lbf, "ListableBeanFactory must not be null");
/* 377 */     Map<String, T> result = new LinkedHashMap<>(4);
/* 378 */     result.putAll(lbf.getBeansOfType(type, includeNonSingletons, allowEagerInit));
/* 379 */     if (lbf instanceof HierarchicalBeanFactory) {
/* 380 */       HierarchicalBeanFactory hbf = (HierarchicalBeanFactory)lbf;
/* 381 */       if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
/* 382 */         Map<String, T> parentResult = beansOfTypeIncludingAncestors((ListableBeanFactory)hbf
/* 383 */             .getParentBeanFactory(), type, includeNonSingletons, allowEagerInit);
/* 384 */         parentResult.forEach((beanName, beanInstance) -> {
/*     */               if (!result.containsKey(beanName) && !hbf.containsLocalBean(beanName)) {
/*     */                 result.put(beanName, beanInstance);
/*     */               }
/*     */             });
/*     */       } 
/*     */     } 
/* 391 */     return result;
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
/*     */   public static <T> T beanOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type) throws BeansException {
/* 420 */     Map<String, T> beansOfType = beansOfTypeIncludingAncestors(lbf, type);
/* 421 */     return uniqueBean(type, beansOfType);
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
/*     */   public static <T> T beanOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
/* 458 */     Map<String, T> beansOfType = beansOfTypeIncludingAncestors(lbf, type, includeNonSingletons, allowEagerInit);
/* 459 */     return uniqueBean(type, beansOfType);
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
/*     */   public static <T> T beanOfType(ListableBeanFactory lbf, Class<T> type) throws BeansException {
/* 480 */     Assert.notNull(lbf, "ListableBeanFactory must not be null");
/* 481 */     Map<String, T> beansOfType = lbf.getBeansOfType(type);
/* 482 */     return uniqueBean(type, beansOfType);
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
/*     */   public static <T> T beanOfType(ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
/* 514 */     Assert.notNull(lbf, "ListableBeanFactory must not be null");
/* 515 */     Map<String, T> beansOfType = lbf.getBeansOfType(type, includeNonSingletons, allowEagerInit);
/* 516 */     return uniqueBean(type, beansOfType);
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
/*     */   private static String[] mergeNamesWithParent(String[] result, String[] parentResult, HierarchicalBeanFactory hbf) {
/* 529 */     if (parentResult.length == 0) {
/* 530 */       return result;
/*     */     }
/* 532 */     List<String> merged = new ArrayList<>(result.length + parentResult.length);
/* 533 */     merged.addAll(Arrays.asList(result));
/* 534 */     for (String beanName : parentResult) {
/* 535 */       if (!merged.contains(beanName) && !hbf.containsLocalBean(beanName)) {
/* 536 */         merged.add(beanName);
/*     */       }
/*     */     } 
/* 539 */     return StringUtils.toStringArray(merged);
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
/*     */   private static <T> T uniqueBean(Class<T> type, Map<String, T> matchingBeans) {
/* 551 */     int count = matchingBeans.size();
/* 552 */     if (count == 1) {
/* 553 */       return matchingBeans.values().iterator().next();
/*     */     }
/* 555 */     if (count > 1) {
/* 556 */       throw new NoUniqueBeanDefinitionException(type, matchingBeans.keySet());
/*     */     }
/*     */     
/* 559 */     throw new NoSuchBeanDefinitionException(type);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/BeanFactoryUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */