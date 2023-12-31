/*    */ package org.springframework.web.context.support;
/*    */ 
/*    */ import java.util.Map;
/*    */ import javax.servlet.ServletContext;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.web.context.ServletContextAware;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ServletContextAttributeExporter
/*    */   implements ServletContextAware
/*    */ {
/* 51 */   protected final Log logger = LogFactory.getLog(getClass());
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   private Map<String, Object> attributes;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setAttributes(Map<String, Object> attributes) {
/* 66 */     this.attributes = attributes;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setServletContext(ServletContext servletContext) {
/* 71 */     if (this.attributes != null)
/* 72 */       for (Map.Entry<String, Object> entry : this.attributes.entrySet()) {
/* 73 */         String attributeName = entry.getKey();
/* 74 */         if (this.logger.isDebugEnabled() && 
/* 75 */           servletContext.getAttribute(attributeName) != null) {
/* 76 */           this.logger.debug("Replacing existing ServletContext attribute with name '" + attributeName + "'");
/*    */         }
/*    */         
/* 79 */         servletContext.setAttribute(attributeName, entry.getValue());
/* 80 */         if (this.logger.isTraceEnabled())
/* 81 */           this.logger.trace("Exported ServletContext attribute with name '" + attributeName + "'"); 
/*    */       }  
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/ServletContextAttributeExporter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */