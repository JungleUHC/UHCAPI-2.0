/*     */ package org.springframework.beans.support;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.beans.BeanWrapperImpl;
/*     */ import org.springframework.beans.BeansException;
/*     */ import org.springframework.lang.Nullable;
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
/*     */ public class PropertyComparator<T>
/*     */   implements Comparator<T>
/*     */ {
/*  43 */   protected final Log logger = LogFactory.getLog(getClass());
/*     */ 
/*     */ 
/*     */   
/*     */   private final SortDefinition sortDefinition;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PropertyComparator(SortDefinition sortDefinition) {
/*  53 */     this.sortDefinition = sortDefinition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PropertyComparator(String property, boolean ignoreCase, boolean ascending) {
/*  63 */     this.sortDefinition = new MutableSortDefinition(property, ignoreCase, ascending);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final SortDefinition getSortDefinition() {
/*  70 */     return this.sortDefinition;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int compare(T o1, T o2) {
/*     */     int result;
/*  77 */     Object v1 = getPropertyValue(o1);
/*  78 */     Object v2 = getPropertyValue(o2);
/*  79 */     if (this.sortDefinition.isIgnoreCase() && v1 instanceof String && v2 instanceof String) {
/*  80 */       v1 = ((String)v1).toLowerCase();
/*  81 */       v2 = ((String)v2).toLowerCase();
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  88 */       if (v1 != null) {
/*  89 */         result = (v2 != null) ? ((Comparable<Object>)v1).compareTo(v2) : -1;
/*     */       } else {
/*     */         
/*  92 */         result = (v2 != null) ? 1 : 0;
/*     */       }
/*     */     
/*  95 */     } catch (RuntimeException ex) {
/*  96 */       if (this.logger.isDebugEnabled()) {
/*  97 */         this.logger.debug("Could not sort objects [" + o1 + "] and [" + o2 + "]", ex);
/*     */       }
/*  99 */       return 0;
/*     */     } 
/*     */     
/* 102 */     return this.sortDefinition.isAscending() ? result : -result;
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
/*     */   private Object getPropertyValue(Object obj) {
/*     */     try {
/* 116 */       BeanWrapperImpl beanWrapper = new BeanWrapperImpl(false);
/* 117 */       beanWrapper.setWrappedInstance(obj);
/* 118 */       return beanWrapper.getPropertyValue(this.sortDefinition.getProperty());
/*     */     }
/* 120 */     catch (BeansException ex) {
/* 121 */       this.logger.debug("PropertyComparator could not access property - treating as null for sorting", (Throwable)ex);
/* 122 */       return null;
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
/*     */   public static void sort(List<?> source, SortDefinition sortDefinition) throws BeansException {
/* 136 */     if (StringUtils.hasText(sortDefinition.getProperty())) {
/* 137 */       source.sort(new PropertyComparator(sortDefinition));
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
/*     */   public static void sort(Object[] source, SortDefinition sortDefinition) throws BeansException {
/* 150 */     if (StringUtils.hasText(sortDefinition.getProperty()))
/* 151 */       Arrays.sort(source, new PropertyComparator(sortDefinition)); 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/support/PropertyComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */