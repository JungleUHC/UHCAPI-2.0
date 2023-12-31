/*    */ package org.springframework.web.context.request.async;
/*    */ 
/*    */ import java.util.concurrent.Callable;
/*    */ import org.springframework.web.context.request.NativeWebRequest;
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
/*    */ @Deprecated
/*    */ public abstract class CallableProcessingInterceptorAdapter
/*    */   implements CallableProcessingInterceptor
/*    */ {
/*    */   public <T> void beforeConcurrentHandling(NativeWebRequest request, Callable<T> task) throws Exception {}
/*    */   
/*    */   public <T> void preProcess(NativeWebRequest request, Callable<T> task) throws Exception {}
/*    */   
/*    */   public <T> void postProcess(NativeWebRequest request, Callable<T> task, Object concurrentResult) throws Exception {}
/*    */   
/*    */   public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
/* 49 */     return RESULT_NONE;
/*    */   }
/*    */ 
/*    */   
/*    */   public <T> Object handleError(NativeWebRequest request, Callable<T> task, Throwable t) throws Exception {
/* 54 */     return RESULT_NONE;
/*    */   }
/*    */   
/*    */   public <T> void afterCompletion(NativeWebRequest request, Callable<T> task) throws Exception {}
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/request/async/CallableProcessingInterceptorAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */