/*     */ package org.springframework.beans;
/*     */ 
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
/*     */ public abstract class AbstractPropertyAccessor
/*     */   extends TypeConverterSupport
/*     */   implements ConfigurablePropertyAccessor
/*     */ {
/*     */   private boolean extractOldValueForEditor = false;
/*     */   private boolean autoGrowNestedPaths = false;
/*     */   boolean suppressNotWritablePropertyException = false;
/*     */   
/*     */   public void setExtractOldValueForEditor(boolean extractOldValueForEditor) {
/*  48 */     this.extractOldValueForEditor = extractOldValueForEditor;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isExtractOldValueForEditor() {
/*  53 */     return this.extractOldValueForEditor;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
/*  58 */     this.autoGrowNestedPaths = autoGrowNestedPaths;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAutoGrowNestedPaths() {
/*  63 */     return this.autoGrowNestedPaths;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPropertyValue(PropertyValue pv) throws BeansException {
/*  69 */     setPropertyValue(pv.getName(), pv.getValue());
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPropertyValues(Map<?, ?> map) throws BeansException {
/*  74 */     setPropertyValues(new MutablePropertyValues(map));
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPropertyValues(PropertyValues pvs) throws BeansException {
/*  79 */     setPropertyValues(pvs, false, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown) throws BeansException {
/*  84 */     setPropertyValues(pvs, ignoreUnknown, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid) throws BeansException {
/*  91 */     List<PropertyAccessException> propertyAccessExceptions = null;
/*     */     
/*  93 */     List<PropertyValue> propertyValues = (pvs instanceof MutablePropertyValues) ? ((MutablePropertyValues)pvs).getPropertyValueList() : Arrays.<PropertyValue>asList(pvs.getPropertyValues());
/*     */     
/*  95 */     if (ignoreUnknown) {
/*  96 */       this.suppressNotWritablePropertyException = true;
/*     */     }
/*     */     try {
/*  99 */       for (PropertyValue pv : propertyValues) {
/*     */ 
/*     */         
/*     */         try {
/*     */           
/* 104 */           setPropertyValue(pv);
/*     */         }
/* 106 */         catch (NotWritablePropertyException ex) {
/* 107 */           if (!ignoreUnknown) {
/* 108 */             throw ex;
/*     */           
/*     */           }
/*     */         }
/* 112 */         catch (NullValueInNestedPathException ex) {
/* 113 */           if (!ignoreInvalid) {
/* 114 */             throw ex;
/*     */           
/*     */           }
/*     */         }
/* 118 */         catch (PropertyAccessException ex) {
/* 119 */           if (propertyAccessExceptions == null) {
/* 120 */             propertyAccessExceptions = new ArrayList<>();
/*     */           }
/* 122 */           propertyAccessExceptions.add(ex);
/*     */         } 
/*     */       } 
/*     */     } finally {
/*     */       
/* 127 */       if (ignoreUnknown) {
/* 128 */         this.suppressNotWritablePropertyException = false;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 133 */     if (propertyAccessExceptions != null) {
/* 134 */       PropertyAccessException[] paeArray = propertyAccessExceptions.<PropertyAccessException>toArray(new PropertyAccessException[0]);
/* 135 */       throw new PropertyBatchUpdateException(paeArray);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Class<?> getPropertyType(String propertyPath) {
/* 144 */     return null;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public abstract Object getPropertyValue(String paramString) throws BeansException;
/*     */   
/*     */   public abstract void setPropertyValue(String paramString, @Nullable Object paramObject) throws BeansException;
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/AbstractPropertyAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */