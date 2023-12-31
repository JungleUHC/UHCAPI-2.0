/*     */ package org.springframework.beans;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.convert.TypeDescriptor;
/*     */ import org.springframework.lang.Nullable;
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
/*     */ public class DirectFieldAccessor
/*     */   extends AbstractNestablePropertyAccessor
/*     */ {
/*  50 */   private final Map<String, FieldPropertyHandler> fieldMap = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DirectFieldAccessor(Object object) {
/*  58 */     super(object);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected DirectFieldAccessor(Object object, String nestedPath, DirectFieldAccessor parent) {
/*  69 */     super(object, nestedPath, parent);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected FieldPropertyHandler getLocalPropertyHandler(String propertyName) {
/*  76 */     FieldPropertyHandler propertyHandler = this.fieldMap.get(propertyName);
/*  77 */     if (propertyHandler == null) {
/*  78 */       Field field = ReflectionUtils.findField(getWrappedClass(), propertyName);
/*  79 */       if (field != null) {
/*  80 */         propertyHandler = new FieldPropertyHandler(field);
/*  81 */         this.fieldMap.put(propertyName, propertyHandler);
/*     */       } 
/*     */     } 
/*  84 */     return propertyHandler;
/*     */   }
/*     */ 
/*     */   
/*     */   protected DirectFieldAccessor newNestedPropertyAccessor(Object object, String nestedPath) {
/*  89 */     return new DirectFieldAccessor(object, nestedPath, this);
/*     */   }
/*     */ 
/*     */   
/*     */   protected NotWritablePropertyException createNotWritablePropertyException(String propertyName) {
/*  94 */     PropertyMatches matches = PropertyMatches.forField(propertyName, getRootClass());
/*  95 */     throw new NotWritablePropertyException(getRootClass(), getNestedPath() + propertyName, matches
/*  96 */         .buildErrorMessage(), matches.getPossibleMatches());
/*     */   }
/*     */   
/*     */   private class FieldPropertyHandler
/*     */     extends AbstractNestablePropertyAccessor.PropertyHandler
/*     */   {
/*     */     private final Field field;
/*     */     
/*     */     public FieldPropertyHandler(Field field) {
/* 105 */       super(field.getType(), true, true);
/* 106 */       this.field = field;
/*     */     }
/*     */ 
/*     */     
/*     */     public TypeDescriptor toTypeDescriptor() {
/* 111 */       return new TypeDescriptor(this.field);
/*     */     }
/*     */ 
/*     */     
/*     */     public ResolvableType getResolvableType() {
/* 116 */       return ResolvableType.forField(this.field);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public TypeDescriptor nested(int level) {
/* 122 */       return TypeDescriptor.nested(this.field, level);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Object getValue() throws Exception {
/*     */       try {
/* 129 */         ReflectionUtils.makeAccessible(this.field);
/* 130 */         return this.field.get(DirectFieldAccessor.this.getWrappedInstance());
/*     */       
/*     */       }
/* 133 */       catch (IllegalAccessException ex) {
/* 134 */         throw new InvalidPropertyException(DirectFieldAccessor.this.getWrappedClass(), this.field
/* 135 */             .getName(), "Field is not accessible", ex);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void setValue(@Nullable Object value) throws Exception {
/*     */       try {
/* 142 */         ReflectionUtils.makeAccessible(this.field);
/* 143 */         this.field.set(DirectFieldAccessor.this.getWrappedInstance(), value);
/*     */       }
/* 145 */       catch (IllegalAccessException ex) {
/* 146 */         throw new InvalidPropertyException(DirectFieldAccessor.this.getWrappedClass(), this.field.getName(), "Field is not accessible", ex);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/DirectFieldAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */