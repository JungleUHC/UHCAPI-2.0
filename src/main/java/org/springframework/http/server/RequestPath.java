/*    */ package org.springframework.http.server;
/*    */ 
/*    */ import java.net.URI;
/*    */ import org.springframework.lang.Nullable;
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
/*    */ public interface RequestPath
/*    */   extends PathContainer
/*    */ {
/*    */   PathContainer contextPath();
/*    */   
/*    */   PathContainer pathWithinApplication();
/*    */   
/*    */   RequestPath modifyContextPath(String paramString);
/*    */   
/*    */   static RequestPath parse(URI uri, @Nullable String contextPath) {
/* 68 */     return parse(uri.getRawPath(), contextPath);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static RequestPath parse(String rawPath, @Nullable String contextPath) {
/* 79 */     return new DefaultRequestPath(rawPath, contextPath);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/RequestPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */