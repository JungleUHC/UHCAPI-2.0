/*     */ package org.springframework.beans.factory.annotation;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.lang.reflect.Executable;
/*     */ import java.lang.reflect.Parameter;
/*     */ import org.springframework.beans.BeansException;
/*     */ import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
/*     */ import org.springframework.beans.factory.config.DependencyDescriptor;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.core.annotation.AnnotatedElementUtils;
/*     */ import org.springframework.core.annotation.SynthesizingMethodParameter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ParameterResolutionDelegate
/*     */ {
/*  47 */   private static final AnnotatedElement EMPTY_ANNOTATED_ELEMENT = new AnnotatedElement()
/*     */     {
/*     */       @Nullable
/*     */       public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
/*  51 */         return null;
/*     */       }
/*     */       
/*     */       public Annotation[] getAnnotations() {
/*  55 */         return new Annotation[0];
/*     */       }
/*     */       
/*     */       public Annotation[] getDeclaredAnnotations() {
/*  59 */         return new Annotation[0];
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isAutowirable(Parameter parameter, int parameterIndex) {
/*  83 */     Assert.notNull(parameter, "Parameter must not be null");
/*  84 */     AnnotatedElement annotatedParameter = getEffectiveAnnotatedParameter(parameter, parameterIndex);
/*  85 */     return (AnnotatedElementUtils.hasAnnotation(annotatedParameter, Autowired.class) || 
/*  86 */       AnnotatedElementUtils.hasAnnotation(annotatedParameter, Qualifier.class) || 
/*  87 */       AnnotatedElementUtils.hasAnnotation(annotatedParameter, Value.class));
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
/*     */   @Nullable
/*     */   public static Object resolveDependency(Parameter parameter, int parameterIndex, Class<?> containingClass, AutowireCapableBeanFactory beanFactory) throws BeansException {
/* 124 */     Assert.notNull(parameter, "Parameter must not be null");
/* 125 */     Assert.notNull(containingClass, "Containing class must not be null");
/* 126 */     Assert.notNull(beanFactory, "AutowireCapableBeanFactory must not be null");
/*     */     
/* 128 */     AnnotatedElement annotatedParameter = getEffectiveAnnotatedParameter(parameter, parameterIndex);
/* 129 */     Autowired autowired = (Autowired)AnnotatedElementUtils.findMergedAnnotation(annotatedParameter, Autowired.class);
/* 130 */     boolean required = (autowired == null || autowired.required());
/*     */     
/* 132 */     SynthesizingMethodParameter synthesizingMethodParameter = SynthesizingMethodParameter.forExecutable(parameter
/* 133 */         .getDeclaringExecutable(), parameterIndex);
/* 134 */     DependencyDescriptor descriptor = new DependencyDescriptor((MethodParameter)synthesizingMethodParameter, required);
/* 135 */     descriptor.setContainingClass(containingClass);
/* 136 */     return beanFactory.resolveDependency(descriptor, null);
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
/*     */   private static AnnotatedElement getEffectiveAnnotatedParameter(Parameter parameter, int index) {
/* 161 */     Executable executable = parameter.getDeclaringExecutable();
/* 162 */     if (executable instanceof java.lang.reflect.Constructor && ClassUtils.isInnerClass(executable.getDeclaringClass()) && (executable
/* 163 */       .getParameterAnnotations()).length == executable.getParameterCount() - 1)
/*     */     {
/*     */       
/* 166 */       return (index == 0) ? EMPTY_ANNOTATED_ELEMENT : executable.getParameters()[index - 1];
/*     */     }
/* 168 */     return parameter;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/annotation/ParameterResolutionDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */