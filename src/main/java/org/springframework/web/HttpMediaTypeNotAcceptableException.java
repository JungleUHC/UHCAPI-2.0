/*    */ package org.springframework.web;
/*    */ 
/*    */ import java.util.List;
/*    */ import org.springframework.http.MediaType;
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
/*    */ public class HttpMediaTypeNotAcceptableException
/*    */   extends HttpMediaTypeException
/*    */ {
/*    */   public HttpMediaTypeNotAcceptableException(String message) {
/* 37 */     super(message);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpMediaTypeNotAcceptableException(List<MediaType> supportedMediaTypes) {
/* 45 */     super("Could not find acceptable representation", supportedMediaTypes);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/HttpMediaTypeNotAcceptableException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */