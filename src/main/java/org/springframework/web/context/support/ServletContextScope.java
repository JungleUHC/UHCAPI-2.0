/*     */ package org.springframework.web.context.support;
/*     */ 
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import javax.servlet.ServletContext;
/*     */ import org.springframework.beans.factory.DisposableBean;
/*     */ import org.springframework.beans.factory.ObjectFactory;
/*     */ import org.springframework.beans.factory.config.Scope;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServletContextScope
/*     */   implements Scope, DisposableBean
/*     */ {
/*     */   private final ServletContext servletContext;
/*  54 */   private final Map<String, Runnable> destructionCallbacks = new LinkedHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletContextScope(ServletContext servletContext) {
/*  62 */     Assert.notNull(servletContext, "ServletContext must not be null");
/*  63 */     this.servletContext = servletContext;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object get(String name, ObjectFactory<?> objectFactory) {
/*  69 */     Object scopedObject = this.servletContext.getAttribute(name);
/*  70 */     if (scopedObject == null) {
/*  71 */       scopedObject = objectFactory.getObject();
/*  72 */       this.servletContext.setAttribute(name, scopedObject);
/*     */     } 
/*  74 */     return scopedObject;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object remove(String name) {
/*  80 */     Object scopedObject = this.servletContext.getAttribute(name);
/*  81 */     if (scopedObject != null) {
/*  82 */       synchronized (this.destructionCallbacks) {
/*  83 */         this.destructionCallbacks.remove(name);
/*     */       } 
/*  85 */       this.servletContext.removeAttribute(name);
/*  86 */       return scopedObject;
/*     */     } 
/*     */     
/*  89 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerDestructionCallback(String name, Runnable callback) {
/*  95 */     synchronized (this.destructionCallbacks) {
/*  96 */       this.destructionCallbacks.put(name, callback);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object resolveContextualObject(String key) {
/* 103 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getConversationId() {
/* 109 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void destroy() {
/* 120 */     synchronized (this.destructionCallbacks) {
/* 121 */       for (Runnable runnable : this.destructionCallbacks.values()) {
/* 122 */         runnable.run();
/*     */       }
/* 124 */       this.destructionCallbacks.clear();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/ServletContextScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */