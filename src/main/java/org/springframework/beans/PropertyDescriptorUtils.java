/*     */ package org.springframework.beans;
/*     */ 
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Enumeration;
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
/*     */ abstract class PropertyDescriptorUtils
/*     */ {
/*     */   public static void copyNonMethodProperties(PropertyDescriptor source, PropertyDescriptor target) {
/*  39 */     target.setExpert(source.isExpert());
/*  40 */     target.setHidden(source.isHidden());
/*  41 */     target.setPreferred(source.isPreferred());
/*  42 */     target.setName(source.getName());
/*  43 */     target.setShortDescription(source.getShortDescription());
/*  44 */     target.setDisplayName(source.getDisplayName());
/*     */ 
/*     */     
/*  47 */     Enumeration<String> keys = source.attributeNames();
/*  48 */     while (keys.hasMoreElements()) {
/*  49 */       String key = keys.nextElement();
/*  50 */       target.setValue(key, source.getValue(key));
/*     */     } 
/*     */ 
/*     */     
/*  54 */     target.setPropertyEditorClass(source.getPropertyEditorClass());
/*  55 */     target.setBound(source.isBound());
/*  56 */     target.setConstrained(source.isConstrained());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static Class<?> findPropertyType(@Nullable Method readMethod, @Nullable Method writeMethod) throws IntrospectionException {
/*  66 */     Class<?> propertyType = null;
/*     */     
/*  68 */     if (readMethod != null) {
/*  69 */       if (readMethod.getParameterCount() != 0) {
/*  70 */         throw new IntrospectionException("Bad read method arg count: " + readMethod);
/*     */       }
/*  72 */       propertyType = readMethod.getReturnType();
/*  73 */       if (propertyType == void.class) {
/*  74 */         throw new IntrospectionException("Read method returns void: " + readMethod);
/*     */       }
/*     */     } 
/*     */     
/*  78 */     if (writeMethod != null) {
/*  79 */       Class<?>[] params = writeMethod.getParameterTypes();
/*  80 */       if (params.length != 1) {
/*  81 */         throw new IntrospectionException("Bad write method arg count: " + writeMethod);
/*     */       }
/*  83 */       if (propertyType != null) {
/*  84 */         if (propertyType.isAssignableFrom(params[0]))
/*     */         {
/*  86 */           propertyType = params[0];
/*     */         }
/*  88 */         else if (!params[0].isAssignableFrom(propertyType))
/*     */         {
/*     */ 
/*     */           
/*  92 */           throw new IntrospectionException("Type mismatch between read and write methods: " + readMethod + " - " + writeMethod);
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/*  97 */         propertyType = params[0];
/*     */       } 
/*     */     } 
/*     */     
/* 101 */     return propertyType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static Class<?> findIndexedPropertyType(String name, @Nullable Class<?> propertyType, @Nullable Method indexedReadMethod, @Nullable Method indexedWriteMethod) throws IntrospectionException {
/* 111 */     Class<?> indexedPropertyType = null;
/*     */     
/* 113 */     if (indexedReadMethod != null) {
/* 114 */       Class<?>[] params = indexedReadMethod.getParameterTypes();
/* 115 */       if (params.length != 1) {
/* 116 */         throw new IntrospectionException("Bad indexed read method arg count: " + indexedReadMethod);
/*     */       }
/* 118 */       if (params[0] != int.class) {
/* 119 */         throw new IntrospectionException("Non int index to indexed read method: " + indexedReadMethod);
/*     */       }
/* 121 */       indexedPropertyType = indexedReadMethod.getReturnType();
/* 122 */       if (indexedPropertyType == void.class) {
/* 123 */         throw new IntrospectionException("Indexed read method returns void: " + indexedReadMethod);
/*     */       }
/*     */     } 
/*     */     
/* 127 */     if (indexedWriteMethod != null) {
/* 128 */       Class<?>[] params = indexedWriteMethod.getParameterTypes();
/* 129 */       if (params.length != 2) {
/* 130 */         throw new IntrospectionException("Bad indexed write method arg count: " + indexedWriteMethod);
/*     */       }
/* 132 */       if (params[0] != int.class) {
/* 133 */         throw new IntrospectionException("Non int index to indexed write method: " + indexedWriteMethod);
/*     */       }
/* 135 */       if (indexedPropertyType != null) {
/* 136 */         if (indexedPropertyType.isAssignableFrom(params[1]))
/*     */         {
/* 138 */           indexedPropertyType = params[1];
/*     */         }
/* 140 */         else if (!params[1].isAssignableFrom(indexedPropertyType))
/*     */         {
/*     */ 
/*     */           
/* 144 */           throw new IntrospectionException("Type mismatch between indexed read and write methods: " + indexedReadMethod + " - " + indexedWriteMethod);
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 149 */         indexedPropertyType = params[1];
/*     */       } 
/*     */     } 
/*     */     
/* 153 */     if (propertyType != null && (!propertyType.isArray() || propertyType
/* 154 */       .getComponentType() != indexedPropertyType)) {
/* 155 */       throw new IntrospectionException("Type mismatch between indexed and non-indexed methods: " + indexedReadMethod + " - " + indexedWriteMethod);
/*     */     }
/*     */ 
/*     */     
/* 159 */     return indexedPropertyType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean equals(PropertyDescriptor pd, PropertyDescriptor otherPd) {
/* 169 */     return (ObjectUtils.nullSafeEquals(pd.getReadMethod(), otherPd.getReadMethod()) && 
/* 170 */       ObjectUtils.nullSafeEquals(pd.getWriteMethod(), otherPd.getWriteMethod()) && 
/* 171 */       ObjectUtils.nullSafeEquals(pd.getPropertyType(), otherPd.getPropertyType()) && 
/* 172 */       ObjectUtils.nullSafeEquals(pd.getPropertyEditorClass(), otherPd.getPropertyEditorClass()) && pd
/* 173 */       .isBound() == otherPd.isBound() && pd.isConstrained() == otherPd.isConstrained());
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/PropertyDescriptorUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */