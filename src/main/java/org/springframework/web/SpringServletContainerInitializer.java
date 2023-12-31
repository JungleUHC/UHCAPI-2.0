/*     */ package org.springframework.web;
/*     */ 
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.servlet.ServletContainerInitializer;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.annotation.HandlesTypes;
/*     */ import org.springframework.core.annotation.AnnotationAwareOrderComparator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @HandlesTypes({WebApplicationInitializer.class})
/*     */ public class SpringServletContainerInitializer
/*     */   implements ServletContainerInitializer
/*     */ {
/*     */   public void onStartup(@Nullable Set<Class<?>> webAppInitializerClasses, ServletContext servletContext) throws ServletException {
/* 146 */     List<WebApplicationInitializer> initializers = Collections.emptyList();
/*     */     
/* 148 */     if (webAppInitializerClasses != null) {
/* 149 */       initializers = new ArrayList<>(webAppInitializerClasses.size());
/* 150 */       for (Class<?> waiClass : webAppInitializerClasses) {
/*     */ 
/*     */         
/* 153 */         if (!waiClass.isInterface() && !Modifier.isAbstract(waiClass.getModifiers()) && WebApplicationInitializer.class
/* 154 */           .isAssignableFrom(waiClass)) {
/*     */           try {
/* 156 */             initializers.add(
/* 157 */                 ReflectionUtils.accessibleConstructor(waiClass, new Class[0]).newInstance(new Object[0]));
/*     */           }
/* 159 */           catch (Throwable ex) {
/* 160 */             throw new ServletException("Failed to instantiate WebApplicationInitializer class", ex);
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 166 */     if (initializers.isEmpty()) {
/* 167 */       servletContext.log("No Spring WebApplicationInitializer types detected on classpath");
/*     */       
/*     */       return;
/*     */     } 
/* 171 */     servletContext.log(initializers.size() + " Spring WebApplicationInitializers detected on classpath");
/* 172 */     AnnotationAwareOrderComparator.sort(initializers);
/* 173 */     for (WebApplicationInitializer initializer : initializers)
/* 174 */       initializer.onStartup(servletContext); 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/SpringServletContainerInitializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */