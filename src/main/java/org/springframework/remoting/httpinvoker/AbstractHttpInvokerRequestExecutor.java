/*     */ package org.springframework.remoting.httpinvoker;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.rmi.RemoteException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.beans.factory.BeanClassLoaderAware;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.remoting.rmi.CodebaseAwareObjectInputStream;
/*     */ import org.springframework.remoting.support.RemoteInvocation;
/*     */ import org.springframework.remoting.support.RemoteInvocationResult;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
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
/*     */ public abstract class AbstractHttpInvokerRequestExecutor
/*     */   implements HttpInvokerRequestExecutor, BeanClassLoaderAware
/*     */ {
/*     */   public static final String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";
/*     */   private static final int SERIALIZED_INVOCATION_BYTE_ARRAY_INITIAL_SIZE = 1024;
/*     */   protected static final String HTTP_METHOD_POST = "POST";
/*     */   protected static final String HTTP_HEADER_ACCEPT_LANGUAGE = "Accept-Language";
/*     */   protected static final String HTTP_HEADER_ACCEPT_ENCODING = "Accept-Encoding";
/*     */   protected static final String HTTP_HEADER_CONTENT_ENCODING = "Content-Encoding";
/*     */   protected static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
/*     */   protected static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";
/*     */   protected static final String ENCODING_GZIP = "gzip";
/*  74 */   protected final Log logger = LogFactory.getLog(getClass());
/*     */   
/*  76 */   private String contentType = "application/x-java-serialized-object";
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean acceptGzipEncoding = true;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private ClassLoader beanClassLoader;
/*     */ 
/*     */ 
/*     */   
/*     */   public void setContentType(String contentType) {
/*  89 */     Assert.notNull(contentType, "'contentType' must not be null");
/*  90 */     this.contentType = contentType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getContentType() {
/*  97 */     return this.contentType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAcceptGzipEncoding(boolean acceptGzipEncoding) {
/* 107 */     this.acceptGzipEncoding = acceptGzipEncoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAcceptGzipEncoding() {
/* 115 */     return this.acceptGzipEncoding;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBeanClassLoader(ClassLoader classLoader) {
/* 120 */     this.beanClassLoader = classLoader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected ClassLoader getBeanClassLoader() {
/* 128 */     return this.beanClassLoader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final RemoteInvocationResult executeRequest(HttpInvokerClientConfiguration config, RemoteInvocation invocation) throws Exception {
/* 136 */     ByteArrayOutputStream baos = getByteArrayOutputStream(invocation);
/* 137 */     if (this.logger.isDebugEnabled()) {
/* 138 */       this.logger.debug("Sending HTTP invoker request for service at [" + config.getServiceUrl() + "], with size " + baos
/* 139 */           .size());
/*     */     }
/* 141 */     return doExecuteRequest(config, baos);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ByteArrayOutputStream getByteArrayOutputStream(RemoteInvocation invocation) throws IOException {
/* 151 */     ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
/* 152 */     writeRemoteInvocation(invocation, baos);
/* 153 */     return baos;
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
/*     */   protected void writeRemoteInvocation(RemoteInvocation invocation, OutputStream os) throws IOException {
/* 170 */     try (ObjectOutputStream oos = new ObjectOutputStream(decorateOutputStream(os))) {
/* 171 */       doWriteRemoteInvocation(invocation, oos);
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
/*     */   protected OutputStream decorateOutputStream(OutputStream os) throws IOException {
/* 184 */     return os;
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
/*     */   protected void doWriteRemoteInvocation(RemoteInvocation invocation, ObjectOutputStream oos) throws IOException {
/* 199 */     oos.writeObject(invocation);
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
/*     */ 
/*     */   
/*     */   protected abstract RemoteInvocationResult doExecuteRequest(HttpInvokerClientConfiguration paramHttpInvokerClientConfiguration, ByteArrayOutputStream paramByteArrayOutputStream) throws Exception;
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
/*     */   protected RemoteInvocationResult readRemoteInvocationResult(InputStream is, @Nullable String codebaseUrl) throws IOException, ClassNotFoundException {
/* 240 */     try (ObjectInputStream ois = createObjectInputStream(decorateInputStream(is), codebaseUrl)) {
/* 241 */       return doReadRemoteInvocationResult(ois);
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
/*     */   protected InputStream decorateInputStream(InputStream is) throws IOException {
/* 254 */     return is;
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
/*     */   protected ObjectInputStream createObjectInputStream(InputStream is, @Nullable String codebaseUrl) throws IOException {
/* 268 */     return (ObjectInputStream)new CodebaseAwareObjectInputStream(is, getBeanClassLoader(), codebaseUrl);
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
/*     */   protected RemoteInvocationResult doReadRemoteInvocationResult(ObjectInputStream ois) throws IOException, ClassNotFoundException {
/* 287 */     Object obj = ois.readObject();
/* 288 */     if (!(obj instanceof RemoteInvocationResult)) {
/* 289 */       throw new RemoteException("Deserialized object needs to be assignable to type [" + RemoteInvocationResult.class
/* 290 */           .getName() + "]: " + ClassUtils.getDescriptiveType(obj));
/*     */     }
/* 292 */     return (RemoteInvocationResult)obj;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/remoting/httpinvoker/AbstractHttpInvokerRequestExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */