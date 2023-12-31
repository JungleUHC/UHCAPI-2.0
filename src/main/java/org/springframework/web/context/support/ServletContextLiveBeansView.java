/*    */ package org.springframework.web.context.support;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import java.util.LinkedHashSet;
/*    */ import java.util.Set;
/*    */ import javax.servlet.ServletContext;
/*    */ import org.springframework.context.ConfigurableApplicationContext;
/*    */ import org.springframework.context.support.LiveBeansView;
/*    */ import org.springframework.util.Assert;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class ServletContextLiveBeansView
/*    */   extends LiveBeansView
/*    */ {
/*    */   private final ServletContext servletContext;
/*    */   
/*    */   public ServletContextLiveBeansView(ServletContext servletContext) {
/* 47 */     Assert.notNull(servletContext, "ServletContext must not be null");
/* 48 */     this.servletContext = servletContext;
/*    */   }
/*    */ 
/*    */   
/*    */   protected Set<ConfigurableApplicationContext> findApplicationContexts() {
/* 53 */     Set<ConfigurableApplicationContext> contexts = new LinkedHashSet<>();
/* 54 */     Enumeration<String> attrNames = this.servletContext.getAttributeNames();
/* 55 */     while (attrNames.hasMoreElements()) {
/* 56 */       String attrName = attrNames.nextElement();
/* 57 */       Object attrValue = this.servletContext.getAttribute(attrName);
/* 58 */       if (attrValue instanceof ConfigurableApplicationContext) {
/* 59 */         contexts.add((ConfigurableApplicationContext)attrValue);
/*    */       }
/*    */     } 
/* 62 */     return contexts;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/ServletContextLiveBeansView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */