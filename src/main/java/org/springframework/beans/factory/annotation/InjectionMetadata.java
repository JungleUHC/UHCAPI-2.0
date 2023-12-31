/*     */ package org.springframework.beans.factory.annotation;
/*     */ 
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
/*     */ import org.springframework.beans.MutablePropertyValues;
/*     */ import org.springframework.beans.PropertyValues;
/*     */ import org.springframework.beans.factory.support.RootBeanDefinition;
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
/*     */ public class InjectionMetadata
/*     */ {
/*  52 */   public static final InjectionMetadata EMPTY = new InjectionMetadata(Object.class, Collections.emptyList())
/*     */     {
/*     */       protected boolean needsRefresh(Class<?> clazz) {
/*  55 */         return false;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public void checkConfigMembers(RootBeanDefinition beanDefinition) {}
/*     */ 
/*     */ 
/*     */       
/*     */       public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) {}
/*     */ 
/*     */ 
/*     */       
/*     */       public void clear(@Nullable PropertyValues pvs) {}
/*     */     };
/*     */ 
/*     */ 
/*     */   
/*     */   private final Class<?> targetClass;
/*     */ 
/*     */   
/*     */   private final Collection<InjectedElement> injectedElements;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private volatile Set<InjectedElement> checkedElements;
/*     */ 
/*     */ 
/*     */   
/*     */   public InjectionMetadata(Class<?> targetClass, Collection<InjectedElement> elements) {
/*  86 */     this.targetClass = targetClass;
/*  87 */     this.injectedElements = elements;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean needsRefresh(Class<?> clazz) {
/*  98 */     return (this.targetClass != clazz);
/*     */   }
/*     */   
/*     */   public void checkConfigMembers(RootBeanDefinition beanDefinition) {
/* 102 */     Set<InjectedElement> checkedElements = new LinkedHashSet<>(this.injectedElements.size());
/* 103 */     for (InjectedElement element : this.injectedElements) {
/* 104 */       Member member = element.getMember();
/* 105 */       if (!beanDefinition.isExternallyManagedConfigMember(member)) {
/* 106 */         beanDefinition.registerExternallyManagedConfigMember(member);
/* 107 */         checkedElements.add(element);
/*     */       } 
/*     */     } 
/* 110 */     this.checkedElements = checkedElements;
/*     */   }
/*     */   
/*     */   public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
/* 114 */     Collection<InjectedElement> checkedElements = this.checkedElements;
/* 115 */     Collection<InjectedElement> elementsToIterate = (checkedElements != null) ? checkedElements : this.injectedElements;
/*     */     
/* 117 */     if (!elementsToIterate.isEmpty()) {
/* 118 */       for (InjectedElement element : elementsToIterate) {
/* 119 */         element.inject(target, beanName, pvs);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear(@Nullable PropertyValues pvs) {
/* 129 */     Collection<InjectedElement> checkedElements = this.checkedElements;
/* 130 */     Collection<InjectedElement> elementsToIterate = (checkedElements != null) ? checkedElements : this.injectedElements;
/*     */     
/* 132 */     if (!elementsToIterate.isEmpty()) {
/* 133 */       for (InjectedElement element : elementsToIterate) {
/* 134 */         element.clearPropertySkipping(pvs);
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
/*     */   public static InjectionMetadata forElements(Collection<InjectedElement> elements, Class<?> clazz) {
/* 148 */     return elements.isEmpty() ? new InjectionMetadata(clazz, Collections.emptyList()) : new InjectionMetadata(clazz, elements);
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
/*     */   public static boolean needsRefresh(@Nullable InjectionMetadata metadata, Class<?> clazz) {
/* 160 */     return (metadata == null || metadata.needsRefresh(clazz));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static abstract class InjectedElement
/*     */   {
/*     */     protected final Member member;
/*     */ 
/*     */     
/*     */     protected final boolean isField;
/*     */     
/*     */     @Nullable
/*     */     protected final PropertyDescriptor pd;
/*     */     
/*     */     @Nullable
/*     */     protected volatile Boolean skip;
/*     */ 
/*     */     
/*     */     protected InjectedElement(Member member, @Nullable PropertyDescriptor pd) {
/* 180 */       this.member = member;
/* 181 */       this.isField = member instanceof Field;
/* 182 */       this.pd = pd;
/*     */     }
/*     */     
/*     */     public final Member getMember() {
/* 186 */       return this.member;
/*     */     }
/*     */     
/*     */     protected final Class<?> getResourceType() {
/* 190 */       if (this.isField) {
/* 191 */         return ((Field)this.member).getType();
/*     */       }
/* 193 */       if (this.pd != null) {
/* 194 */         return this.pd.getPropertyType();
/*     */       }
/*     */       
/* 197 */       return ((Method)this.member).getParameterTypes()[0];
/*     */     }
/*     */ 
/*     */     
/*     */     protected final void checkResourceType(Class<?> resourceType) {
/* 202 */       if (this.isField) {
/* 203 */         Class<?> fieldType = ((Field)this.member).getType();
/* 204 */         if (!resourceType.isAssignableFrom(fieldType) && !fieldType.isAssignableFrom(resourceType)) {
/* 205 */           throw new IllegalStateException("Specified field type [" + fieldType + "] is incompatible with resource type [" + resourceType
/* 206 */               .getName() + "]");
/*     */         }
/*     */       }
/*     */       else {
/*     */         
/* 211 */         Class<?> paramType = (this.pd != null) ? this.pd.getPropertyType() : ((Method)this.member).getParameterTypes()[0];
/* 212 */         if (!resourceType.isAssignableFrom(paramType) && !paramType.isAssignableFrom(resourceType)) {
/* 213 */           throw new IllegalStateException("Specified parameter type [" + paramType + "] is incompatible with resource type [" + resourceType
/* 214 */               .getName() + "]");
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void inject(Object target, @Nullable String requestingBeanName, @Nullable PropertyValues pvs) throws Throwable {
/* 225 */       if (this.isField) {
/* 226 */         Field field = (Field)this.member;
/* 227 */         ReflectionUtils.makeAccessible(field);
/* 228 */         field.set(target, getResourceToInject(target, requestingBeanName));
/*     */       } else {
/*     */         
/* 231 */         if (checkPropertySkipping(pvs)) {
/*     */           return;
/*     */         }
/*     */         try {
/* 235 */           Method method = (Method)this.member;
/* 236 */           ReflectionUtils.makeAccessible(method);
/* 237 */           method.invoke(target, new Object[] { getResourceToInject(target, requestingBeanName) });
/*     */         }
/* 239 */         catch (InvocationTargetException ex) {
/* 240 */           throw ex.getTargetException();
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected boolean checkPropertySkipping(@Nullable PropertyValues pvs) {
/* 251 */       Boolean skip = this.skip;
/* 252 */       if (skip != null) {
/* 253 */         return skip.booleanValue();
/*     */       }
/* 255 */       if (pvs == null) {
/* 256 */         this.skip = Boolean.valueOf(false);
/* 257 */         return false;
/*     */       } 
/* 259 */       synchronized (pvs) {
/* 260 */         skip = this.skip;
/* 261 */         if (skip != null) {
/* 262 */           return skip.booleanValue();
/*     */         }
/* 264 */         if (this.pd != null) {
/* 265 */           if (pvs.contains(this.pd.getName())) {
/*     */             
/* 267 */             this.skip = Boolean.valueOf(true);
/* 268 */             return true;
/*     */           } 
/* 270 */           if (pvs instanceof MutablePropertyValues) {
/* 271 */             ((MutablePropertyValues)pvs).registerProcessedProperty(this.pd.getName());
/*     */           }
/*     */         } 
/* 274 */         this.skip = Boolean.valueOf(false);
/* 275 */         return false;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void clearPropertySkipping(@Nullable PropertyValues pvs) {
/* 284 */       if (pvs == null) {
/*     */         return;
/*     */       }
/* 287 */       synchronized (pvs) {
/* 288 */         if (Boolean.FALSE.equals(this.skip) && this.pd != null && pvs instanceof MutablePropertyValues) {
/* 289 */           ((MutablePropertyValues)pvs).clearProcessedProperty(this.pd.getName());
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     protected Object getResourceToInject(Object target, @Nullable String requestingBeanName) {
/* 299 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object other) {
/* 304 */       if (this == other) {
/* 305 */         return true;
/*     */       }
/* 307 */       if (!(other instanceof InjectedElement)) {
/* 308 */         return false;
/*     */       }
/* 310 */       InjectedElement otherElement = (InjectedElement)other;
/* 311 */       return this.member.equals(otherElement.member);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 316 */       return this.member.getClass().hashCode() * 29 + this.member.getName().hashCode();
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 321 */       return getClass().getSimpleName() + " for " + this.member;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/annotation/InjectionMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */