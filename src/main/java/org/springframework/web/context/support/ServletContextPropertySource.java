/*    */ package org.springframework.web.context.support;
/*    */ 
/*    */ import javax.servlet.ServletContext;
/*    */ import org.springframework.core.env.EnumerablePropertySource;
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.util.StringUtils;
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
/*    */ public class ServletContextPropertySource
/*    */   extends EnumerablePropertySource<ServletContext>
/*    */ {
/*    */   public ServletContextPropertySource(String name, ServletContext servletContext) {
/* 36 */     super(name, servletContext);
/*    */   }
/*    */ 
/*    */   
/*    */   public String[] getPropertyNames() {
/* 41 */     return StringUtils.toStringArray(((ServletContext)this.source).getInitParameterNames());
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public String getProperty(String name) {
/* 47 */     return ((ServletContext)this.source).getInitParameter(name);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/ServletContextPropertySource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */