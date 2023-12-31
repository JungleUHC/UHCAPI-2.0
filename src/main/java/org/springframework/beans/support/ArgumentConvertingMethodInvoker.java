/*     */ package org.springframework.beans.support;
/*     */ 
/*     */ import java.beans.PropertyEditor;
/*     */ import java.lang.reflect.Method;
/*     */ import org.springframework.beans.PropertyEditorRegistry;
/*     */ import org.springframework.beans.SimpleTypeConverter;
/*     */ import org.springframework.beans.TypeConverter;
/*     */ import org.springframework.beans.TypeMismatchException;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.MethodInvoker;
/*     */ import org.springframework.util.ReflectionUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ArgumentConvertingMethodInvoker
/*     */   extends MethodInvoker
/*     */ {
/*     */   @Nullable
/*     */   private TypeConverter typeConverter;
/*     */   private boolean useDefaultConverter = true;
/*     */   
/*     */   public void setTypeConverter(@Nullable TypeConverter typeConverter) {
/*  59 */     this.typeConverter = typeConverter;
/*  60 */     this.useDefaultConverter = (typeConverter == null);
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
/*     */   public TypeConverter getTypeConverter() {
/*  72 */     if (this.typeConverter == null && this.useDefaultConverter) {
/*  73 */       this.typeConverter = getDefaultTypeConverter();
/*     */     }
/*  75 */     return this.typeConverter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TypeConverter getDefaultTypeConverter() {
/*  86 */     return (TypeConverter)new SimpleTypeConverter();
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
/*     */   public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
/* 100 */     TypeConverter converter = getTypeConverter();
/* 101 */     if (!(converter instanceof PropertyEditorRegistry)) {
/* 102 */       throw new IllegalStateException("TypeConverter does not implement PropertyEditorRegistry interface: " + converter);
/*     */     }
/*     */     
/* 105 */     ((PropertyEditorRegistry)converter).registerCustomEditor(requiredType, propertyEditor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Method findMatchingMethod() {
/* 115 */     Method matchingMethod = super.findMatchingMethod();
/*     */     
/* 117 */     if (matchingMethod == null)
/*     */     {
/* 119 */       matchingMethod = doFindMatchingMethod(getArguments());
/*     */     }
/* 121 */     if (matchingMethod == null)
/*     */     {
/* 123 */       matchingMethod = doFindMatchingMethod(new Object[] { getArguments() });
/*     */     }
/* 125 */     return matchingMethod;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected Method doFindMatchingMethod(Object[] arguments) {
/* 136 */     TypeConverter converter = getTypeConverter();
/* 137 */     if (converter != null) {
/* 138 */       String targetMethod = getTargetMethod();
/* 139 */       Method matchingMethod = null;
/* 140 */       int argCount = arguments.length;
/* 141 */       Class<?> targetClass = getTargetClass();
/* 142 */       Assert.state((targetClass != null), "No target class set");
/* 143 */       Method[] candidates = ReflectionUtils.getAllDeclaredMethods(targetClass);
/* 144 */       int minTypeDiffWeight = Integer.MAX_VALUE;
/* 145 */       Object[] argumentsToUse = null;
/* 146 */       for (Method candidate : candidates) {
/* 147 */         if (candidate.getName().equals(targetMethod)) {
/*     */           
/* 149 */           int parameterCount = candidate.getParameterCount();
/* 150 */           if (parameterCount == argCount) {
/* 151 */             Class<?>[] paramTypes = candidate.getParameterTypes();
/* 152 */             Object[] convertedArguments = new Object[argCount];
/* 153 */             boolean match = true;
/* 154 */             for (int j = 0; j < argCount && match; j++) {
/*     */               
/*     */               try {
/* 157 */                 convertedArguments[j] = converter.convertIfNecessary(arguments[j], paramTypes[j]);
/*     */               }
/* 159 */               catch (TypeMismatchException ex) {
/*     */                 
/* 161 */                 match = false;
/*     */               } 
/*     */             } 
/* 164 */             if (match) {
/* 165 */               int typeDiffWeight = getTypeDifferenceWeight(paramTypes, convertedArguments);
/* 166 */               if (typeDiffWeight < minTypeDiffWeight) {
/* 167 */                 minTypeDiffWeight = typeDiffWeight;
/* 168 */                 matchingMethod = candidate;
/* 169 */                 argumentsToUse = convertedArguments;
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 175 */       if (matchingMethod != null) {
/* 176 */         setArguments(argumentsToUse);
/* 177 */         return matchingMethod;
/*     */       } 
/*     */     } 
/* 180 */     return null;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/support/ArgumentConvertingMethodInvoker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */