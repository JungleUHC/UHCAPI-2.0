/*     */ package org.springframework.beans;
/*     */ 
/*     */ import java.awt.Image;
/*     */ import java.beans.BeanDescriptor;
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.EventSetDescriptor;
/*     */ import java.beans.IndexedPropertyDescriptor;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.MethodDescriptor;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ExtendedBeanInfo
/*     */   implements BeanInfo
/*     */ {
/*  82 */   private static final Log logger = LogFactory.getLog(ExtendedBeanInfo.class);
/*     */   
/*     */   private final BeanInfo delegate;
/*     */   
/*  86 */   private final Set<PropertyDescriptor> propertyDescriptors = new TreeSet<>(new PropertyDescriptorComparator());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExtendedBeanInfo(BeanInfo delegate) {
/* 100 */     this.delegate = delegate;
/* 101 */     for (PropertyDescriptor pd : delegate.getPropertyDescriptors()) {
/*     */       try {
/* 103 */         this.propertyDescriptors.add((pd instanceof IndexedPropertyDescriptor) ? new SimpleIndexedPropertyDescriptor((IndexedPropertyDescriptor)pd) : new SimplePropertyDescriptor(pd));
/*     */ 
/*     */       
/*     */       }
/* 107 */       catch (IntrospectionException ex) {
/*     */         
/* 109 */         if (logger.isDebugEnabled()) {
/* 110 */           logger.debug("Ignoring invalid bean property '" + pd.getName() + "': " + ex.getMessage());
/*     */         }
/*     */       } 
/*     */     } 
/* 114 */     MethodDescriptor[] methodDescriptors = delegate.getMethodDescriptors();
/* 115 */     if (methodDescriptors != null) {
/* 116 */       for (Method method : findCandidateWriteMethods(methodDescriptors)) {
/*     */         try {
/* 118 */           handleCandidateWriteMethod(method);
/*     */         }
/* 120 */         catch (IntrospectionException ex) {
/*     */           
/* 122 */           if (logger.isDebugEnabled()) {
/* 123 */             logger.debug("Ignoring candidate write method [" + method + "]: " + ex.getMessage());
/*     */           }
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private List<Method> findCandidateWriteMethods(MethodDescriptor[] methodDescriptors) {
/* 132 */     List<Method> matches = new ArrayList<>();
/* 133 */     for (MethodDescriptor methodDescriptor : methodDescriptors) {
/* 134 */       Method method = methodDescriptor.getMethod();
/* 135 */       if (isCandidateWriteMethod(method)) {
/* 136 */         matches.add(method);
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 142 */     matches.sort((m1, m2) -> m2.toString().compareTo(m1.toString()));
/* 143 */     return matches;
/*     */   }
/*     */   
/*     */   public static boolean isCandidateWriteMethod(Method method) {
/* 147 */     String methodName = method.getName();
/* 148 */     int nParams = method.getParameterCount();
/* 149 */     return (methodName.length() > 3 && methodName.startsWith("set") && Modifier.isPublic(method.getModifiers()) && (
/* 150 */       !void.class.isAssignableFrom(method.getReturnType()) || Modifier.isStatic(method.getModifiers())) && (nParams == 1 || (nParams == 2 && int.class == method
/* 151 */       .getParameterTypes()[0])));
/*     */   }
/*     */   
/*     */   private void handleCandidateWriteMethod(Method method) throws IntrospectionException {
/* 155 */     int nParams = method.getParameterCount();
/* 156 */     String propertyName = propertyNameFor(method);
/* 157 */     Class<?> propertyType = method.getParameterTypes()[nParams - 1];
/* 158 */     PropertyDescriptor existingPd = findExistingPropertyDescriptor(propertyName, propertyType);
/* 159 */     if (nParams == 1) {
/* 160 */       if (existingPd == null) {
/* 161 */         this.propertyDescriptors.add(new SimplePropertyDescriptor(propertyName, null, method));
/*     */       } else {
/*     */         
/* 164 */         existingPd.setWriteMethod(method);
/*     */       }
/*     */     
/* 167 */     } else if (nParams == 2) {
/* 168 */       if (existingPd == null) {
/* 169 */         this.propertyDescriptors.add(new SimpleIndexedPropertyDescriptor(propertyName, null, null, null, method));
/*     */       
/*     */       }
/* 172 */       else if (existingPd instanceof IndexedPropertyDescriptor) {
/* 173 */         ((IndexedPropertyDescriptor)existingPd).setIndexedWriteMethod(method);
/*     */       } else {
/*     */         
/* 176 */         this.propertyDescriptors.remove(existingPd);
/* 177 */         this.propertyDescriptors.add(new SimpleIndexedPropertyDescriptor(propertyName, existingPd
/* 178 */               .getReadMethod(), existingPd.getWriteMethod(), null, method));
/*     */       } 
/*     */     } else {
/*     */       
/* 182 */       throw new IllegalArgumentException("Write method must have exactly 1 or 2 parameters: " + method);
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private PropertyDescriptor findExistingPropertyDescriptor(String propertyName, Class<?> propertyType) {
/* 188 */     for (PropertyDescriptor pd : this.propertyDescriptors) {
/*     */       
/* 190 */       String candidateName = pd.getName();
/* 191 */       if (pd instanceof IndexedPropertyDescriptor) {
/* 192 */         IndexedPropertyDescriptor ipd = (IndexedPropertyDescriptor)pd;
/* 193 */         Class<?> clazz = ipd.getIndexedPropertyType();
/* 194 */         if (candidateName.equals(propertyName) && (clazz
/* 195 */           .equals(propertyType) || clazz.equals(propertyType.getComponentType()))) {
/* 196 */           return pd;
/*     */         }
/*     */         continue;
/*     */       } 
/* 200 */       Class<?> candidateType = pd.getPropertyType();
/* 201 */       if (candidateName.equals(propertyName) && (candidateType
/* 202 */         .equals(propertyType) || propertyType.equals(candidateType.getComponentType()))) {
/* 203 */         return pd;
/*     */       }
/*     */     } 
/*     */     
/* 207 */     return null;
/*     */   }
/*     */   
/*     */   private String propertyNameFor(Method method) {
/* 211 */     return Introspector.decapitalize(method.getName().substring(3));
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
/*     */   public PropertyDescriptor[] getPropertyDescriptors() {
/* 223 */     return this.propertyDescriptors.<PropertyDescriptor>toArray(new PropertyDescriptor[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public BeanInfo[] getAdditionalBeanInfo() {
/* 228 */     return this.delegate.getAdditionalBeanInfo();
/*     */   }
/*     */ 
/*     */   
/*     */   public BeanDescriptor getBeanDescriptor() {
/* 233 */     return this.delegate.getBeanDescriptor();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDefaultEventIndex() {
/* 238 */     return this.delegate.getDefaultEventIndex();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDefaultPropertyIndex() {
/* 243 */     return this.delegate.getDefaultPropertyIndex();
/*     */   }
/*     */ 
/*     */   
/*     */   public EventSetDescriptor[] getEventSetDescriptors() {
/* 248 */     return this.delegate.getEventSetDescriptors();
/*     */   }
/*     */ 
/*     */   
/*     */   public Image getIcon(int iconKind) {
/* 253 */     return this.delegate.getIcon(iconKind);
/*     */   }
/*     */ 
/*     */   
/*     */   public MethodDescriptor[] getMethodDescriptors() {
/* 258 */     return this.delegate.getMethodDescriptors();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class SimplePropertyDescriptor
/*     */     extends PropertyDescriptor
/*     */   {
/*     */     @Nullable
/*     */     private Method readMethod;
/*     */     
/*     */     @Nullable
/*     */     private Method writeMethod;
/*     */     
/*     */     @Nullable
/*     */     private Class<?> propertyType;
/*     */     
/*     */     @Nullable
/*     */     private Class<?> propertyEditorClass;
/*     */ 
/*     */     
/*     */     public SimplePropertyDescriptor(PropertyDescriptor original) throws IntrospectionException {
/* 280 */       this(original.getName(), original.getReadMethod(), original.getWriteMethod());
/* 281 */       PropertyDescriptorUtils.copyNonMethodProperties(original, this);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public SimplePropertyDescriptor(String propertyName, @Nullable Method readMethod, Method writeMethod) throws IntrospectionException {
/* 287 */       super(propertyName, null, null);
/* 288 */       this.readMethod = readMethod;
/* 289 */       this.writeMethod = writeMethod;
/* 290 */       this.propertyType = PropertyDescriptorUtils.findPropertyType(readMethod, writeMethod);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Method getReadMethod() {
/* 296 */       return this.readMethod;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setReadMethod(@Nullable Method readMethod) {
/* 301 */       this.readMethod = readMethod;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Method getWriteMethod() {
/* 307 */       return this.writeMethod;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setWriteMethod(@Nullable Method writeMethod) {
/* 312 */       this.writeMethod = writeMethod;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Class<?> getPropertyType() {
/* 318 */       if (this.propertyType == null) {
/*     */         try {
/* 320 */           this.propertyType = PropertyDescriptorUtils.findPropertyType(this.readMethod, this.writeMethod);
/*     */         }
/* 322 */         catch (IntrospectionException introspectionException) {}
/*     */       }
/*     */ 
/*     */       
/* 326 */       return this.propertyType;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Class<?> getPropertyEditorClass() {
/* 332 */       return this.propertyEditorClass;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setPropertyEditorClass(@Nullable Class<?> propertyEditorClass) {
/* 337 */       this.propertyEditorClass = propertyEditorClass;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object other) {
/* 342 */       return (this == other || (other instanceof PropertyDescriptor && 
/* 343 */         PropertyDescriptorUtils.equals(this, (PropertyDescriptor)other)));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 348 */       return ObjectUtils.nullSafeHashCode(getReadMethod()) * 29 + ObjectUtils.nullSafeHashCode(getWriteMethod());
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 353 */       return String.format("%s[name=%s, propertyType=%s, readMethod=%s, writeMethod=%s]", new Object[] {
/* 354 */             getClass().getSimpleName(), getName(), getPropertyType(), this.readMethod, this.writeMethod
/*     */           });
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class SimpleIndexedPropertyDescriptor
/*     */     extends IndexedPropertyDescriptor
/*     */   {
/*     */     @Nullable
/*     */     private Method readMethod;
/*     */     
/*     */     @Nullable
/*     */     private Method writeMethod;
/*     */     
/*     */     @Nullable
/*     */     private Class<?> propertyType;
/*     */     
/*     */     @Nullable
/*     */     private Method indexedReadMethod;
/*     */     
/*     */     @Nullable
/*     */     private Method indexedWriteMethod;
/*     */     
/*     */     @Nullable
/*     */     private Class<?> indexedPropertyType;
/*     */     
/*     */     @Nullable
/*     */     private Class<?> propertyEditorClass;
/*     */ 
/*     */     
/*     */     public SimpleIndexedPropertyDescriptor(IndexedPropertyDescriptor original) throws IntrospectionException {
/* 386 */       this(original.getName(), original.getReadMethod(), original.getWriteMethod(), original
/* 387 */           .getIndexedReadMethod(), original.getIndexedWriteMethod());
/* 388 */       PropertyDescriptorUtils.copyNonMethodProperties(original, this);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public SimpleIndexedPropertyDescriptor(String propertyName, @Nullable Method readMethod, @Nullable Method writeMethod, @Nullable Method indexedReadMethod, Method indexedWriteMethod) throws IntrospectionException {
/* 395 */       super(propertyName, (Method)null, (Method)null, (Method)null, (Method)null);
/* 396 */       this.readMethod = readMethod;
/* 397 */       this.writeMethod = writeMethod;
/* 398 */       this.propertyType = PropertyDescriptorUtils.findPropertyType(readMethod, writeMethod);
/* 399 */       this.indexedReadMethod = indexedReadMethod;
/* 400 */       this.indexedWriteMethod = indexedWriteMethod;
/* 401 */       this.indexedPropertyType = PropertyDescriptorUtils.findIndexedPropertyType(propertyName, this.propertyType, indexedReadMethod, indexedWriteMethod);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Method getReadMethod() {
/* 408 */       return this.readMethod;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setReadMethod(@Nullable Method readMethod) {
/* 413 */       this.readMethod = readMethod;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Method getWriteMethod() {
/* 419 */       return this.writeMethod;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setWriteMethod(@Nullable Method writeMethod) {
/* 424 */       this.writeMethod = writeMethod;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Class<?> getPropertyType() {
/* 430 */       if (this.propertyType == null) {
/*     */         try {
/* 432 */           this.propertyType = PropertyDescriptorUtils.findPropertyType(this.readMethod, this.writeMethod);
/*     */         }
/* 434 */         catch (IntrospectionException introspectionException) {}
/*     */       }
/*     */ 
/*     */       
/* 438 */       return this.propertyType;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Method getIndexedReadMethod() {
/* 444 */       return this.indexedReadMethod;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setIndexedReadMethod(@Nullable Method indexedReadMethod) throws IntrospectionException {
/* 449 */       this.indexedReadMethod = indexedReadMethod;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Method getIndexedWriteMethod() {
/* 455 */       return this.indexedWriteMethod;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setIndexedWriteMethod(@Nullable Method indexedWriteMethod) throws IntrospectionException {
/* 460 */       this.indexedWriteMethod = indexedWriteMethod;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Class<?> getIndexedPropertyType() {
/* 466 */       if (this.indexedPropertyType == null) {
/*     */         try {
/* 468 */           this.indexedPropertyType = PropertyDescriptorUtils.findIndexedPropertyType(
/* 469 */               getName(), getPropertyType(), this.indexedReadMethod, this.indexedWriteMethod);
/*     */         }
/* 471 */         catch (IntrospectionException introspectionException) {}
/*     */       }
/*     */ 
/*     */       
/* 475 */       return this.indexedPropertyType;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Class<?> getPropertyEditorClass() {
/* 481 */       return this.propertyEditorClass;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setPropertyEditorClass(@Nullable Class<?> propertyEditorClass) {
/* 486 */       this.propertyEditorClass = propertyEditorClass;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object other) {
/* 494 */       if (this == other) {
/* 495 */         return true;
/*     */       }
/* 497 */       if (!(other instanceof IndexedPropertyDescriptor)) {
/* 498 */         return false;
/*     */       }
/* 500 */       IndexedPropertyDescriptor otherPd = (IndexedPropertyDescriptor)other;
/* 501 */       return (ObjectUtils.nullSafeEquals(getIndexedReadMethod(), otherPd.getIndexedReadMethod()) && 
/* 502 */         ObjectUtils.nullSafeEquals(getIndexedWriteMethod(), otherPd.getIndexedWriteMethod()) && 
/* 503 */         ObjectUtils.nullSafeEquals(getIndexedPropertyType(), otherPd.getIndexedPropertyType()) && 
/* 504 */         PropertyDescriptorUtils.equals(this, otherPd));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 509 */       int hashCode = ObjectUtils.nullSafeHashCode(getReadMethod());
/* 510 */       hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(getWriteMethod());
/* 511 */       hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(getIndexedReadMethod());
/* 512 */       hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(getIndexedWriteMethod());
/* 513 */       return hashCode;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 518 */       return String.format("%s[name=%s, propertyType=%s, indexedPropertyType=%s, readMethod=%s, writeMethod=%s, indexedReadMethod=%s, indexedWriteMethod=%s]", new Object[] {
/*     */             
/* 520 */             getClass().getSimpleName(), getName(), getPropertyType(), getIndexedPropertyType(), this.readMethod, this.writeMethod, this.indexedReadMethod, this.indexedWriteMethod
/*     */           });
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class PropertyDescriptorComparator
/*     */     implements Comparator<PropertyDescriptor>
/*     */   {
/*     */     public int compare(PropertyDescriptor desc1, PropertyDescriptor desc2) {
/* 535 */       String left = desc1.getName();
/* 536 */       String right = desc2.getName();
/* 537 */       byte[] leftBytes = left.getBytes();
/* 538 */       byte[] rightBytes = right.getBytes();
/* 539 */       for (int i = 0; i < left.length(); i++) {
/* 540 */         if (right.length() == i) {
/* 541 */           return 1;
/*     */         }
/* 543 */         int result = leftBytes[i] - rightBytes[i];
/* 544 */         if (result != 0) {
/* 545 */           return result;
/*     */         }
/*     */       } 
/* 548 */       return left.length() - right.length();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/ExtendedBeanInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */