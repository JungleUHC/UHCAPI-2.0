/*     */ package org.springframework.remoting.httpinvoker;
/*     */ 
/*     */ import com.sun.net.httpserver.HttpExchange;
/*     */ import com.sun.net.httpserver.HttpHandler;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import org.springframework.lang.UsesSunHttpServer;
/*     */ import org.springframework.remoting.rmi.RemoteInvocationSerializingExporter;
/*     */ import org.springframework.remoting.support.RemoteInvocation;
/*     */ import org.springframework.remoting.support.RemoteInvocationResult;
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
/*     */ @Deprecated
/*     */ @UsesSunHttpServer
/*     */ public class SimpleHttpInvokerServiceExporter
/*     */   extends RemoteInvocationSerializingExporter
/*     */   implements HttpHandler
/*     */ {
/*     */   public void handle(HttpExchange exchange) throws IOException {
/*     */     try {
/*  72 */       RemoteInvocation invocation = readRemoteInvocation(exchange);
/*  73 */       RemoteInvocationResult result = invokeAndCreateResult(invocation, getProxy());
/*  74 */       writeRemoteInvocationResult(exchange, result);
/*  75 */       exchange.close();
/*     */     }
/*  77 */     catch (ClassNotFoundException ex) {
/*  78 */       exchange.sendResponseHeaders(500, -1L);
/*  79 */       this.logger.error("Class not found during deserialization", ex);
/*     */     } 
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
/*     */   protected RemoteInvocation readRemoteInvocation(HttpExchange exchange) throws IOException, ClassNotFoundException {
/*  95 */     return readRemoteInvocation(exchange, exchange.getRequestBody());
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
/*     */   protected RemoteInvocation readRemoteInvocation(HttpExchange exchange, InputStream is) throws IOException, ClassNotFoundException {
/* 114 */     ObjectInputStream ois = createObjectInputStream(decorateInputStream(exchange, is));
/* 115 */     return doReadRemoteInvocation(ois);
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
/*     */   protected InputStream decorateInputStream(HttpExchange exchange, InputStream is) throws IOException {
/* 129 */     return is;
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
/*     */   protected void writeRemoteInvocationResult(HttpExchange exchange, RemoteInvocationResult result) throws IOException {
/* 141 */     exchange.getResponseHeaders().set("Content-Type", getContentType());
/* 142 */     exchange.sendResponseHeaders(200, 0L);
/* 143 */     writeRemoteInvocationResult(exchange, result, exchange.getResponseBody());
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
/*     */   protected void writeRemoteInvocationResult(HttpExchange exchange, RemoteInvocationResult result, OutputStream os) throws IOException {
/* 163 */     ObjectOutputStream oos = createObjectOutputStream(decorateOutputStream(exchange, os));
/* 164 */     doWriteRemoteInvocationResult(result, oos);
/* 165 */     oos.flush();
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
/*     */   protected OutputStream decorateOutputStream(HttpExchange exchange, OutputStream os) throws IOException {
/* 179 */     return os;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/remoting/httpinvoker/SimpleHttpInvokerServiceExporter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */