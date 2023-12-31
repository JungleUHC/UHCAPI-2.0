/*    */ package org.springframework.web.context.support;
/*    */ 
/*    */ import javax.servlet.ServletConfig;
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
/*    */ public class ServletConfigPropertySource
/*    */   extends EnumerablePropertySource<ServletConfig>
/*    */ {
/*    */   public ServletConfigPropertySource(String name, ServletConfig servletConfig) {
/* 36 */     super(name, servletConfig);
/*    */   }
/*    */ 
/*    */   
/*    */   public String[] getPropertyNames() {
/* 41 */     return StringUtils.toStringArray(((ServletConfig)this.source).getInitParameterNames());
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public String getProperty(String name) {
/* 47 */     return ((ServletConfig)this.source).getInitParameter(name);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/ServletConfigPropertySource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */