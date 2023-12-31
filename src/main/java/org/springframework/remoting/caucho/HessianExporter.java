/*     */ package org.springframework.remoting.caucho;
/*     */ 
/*     */ import com.caucho.hessian.io.AbstractHessianInput;
/*     */ import com.caucho.hessian.io.AbstractHessianOutput;
/*     */ import com.caucho.hessian.io.Hessian2Input;
/*     */ import com.caucho.hessian.io.Hessian2Output;
/*     */ import com.caucho.hessian.io.HessianDebugInputStream;
/*     */ import com.caucho.hessian.io.HessianDebugOutputStream;
/*     */ import com.caucho.hessian.io.HessianInput;
/*     */ import com.caucho.hessian.io.HessianOutput;
/*     */ import com.caucho.hessian.io.HessianRemoteResolver;
/*     */ import com.caucho.hessian.io.SerializerFactory;
/*     */ import com.caucho.hessian.server.HessianSkeleton;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Writer;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.springframework.beans.factory.InitializingBean;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.remoting.support.RemoteExporter;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CommonsLogWriter;
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
/*     */ public class HessianExporter
/*     */   extends RemoteExporter
/*     */   implements InitializingBean
/*     */ {
/*     */   public static final String CONTENT_TYPE_HESSIAN = "application/x-hessian";
/*  67 */   private SerializerFactory serializerFactory = new SerializerFactory();
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private HessianRemoteResolver remoteResolver;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Log debugLogger;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private HessianSkeleton skeleton;
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSerializerFactory(@Nullable SerializerFactory serializerFactory) {
/*  86 */     this.serializerFactory = (serializerFactory != null) ? serializerFactory : new SerializerFactory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSendCollectionType(boolean sendCollectionType) {
/*  94 */     this.serializerFactory.setSendCollectionType(sendCollectionType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAllowNonSerializable(boolean allowNonSerializable) {
/* 102 */     this.serializerFactory.setAllowNonSerializable(allowNonSerializable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRemoteResolver(HessianRemoteResolver remoteResolver) {
/* 110 */     this.remoteResolver = remoteResolver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDebug(boolean debug) {
/* 119 */     this.debugLogger = debug ? this.logger : null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void afterPropertiesSet() {
/* 125 */     prepare();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void prepare() {
/* 132 */     checkService();
/* 133 */     checkServiceInterface();
/* 134 */     this.skeleton = new HessianSkeleton(getProxyForService(), getServiceInterface());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void invoke(InputStream inputStream, OutputStream outputStream) throws Throwable {
/* 145 */     Assert.notNull(this.skeleton, "Hessian exporter has not been initialized");
/* 146 */     doInvoke(this.skeleton, inputStream, outputStream);
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
/*     */   protected void doInvoke(HessianSkeleton skeleton, InputStream inputStream, OutputStream outputStream) throws Throwable {
/* 159 */     ClassLoader originalClassLoader = overrideThreadContextClassLoader(); try {
/*     */       HessianDebugInputStream hessianDebugInputStream; BufferedInputStream bufferedInputStream; HessianDebugOutputStream hessianDebugOutputStream; HessianInput hessianInput; HessianOutput hessianOutput;
/* 161 */       InputStream isToUse = inputStream;
/* 162 */       OutputStream osToUse = outputStream;
/*     */       
/* 164 */       if (this.debugLogger != null && this.debugLogger.isDebugEnabled()) {
/* 165 */         try (PrintWriter debugWriter = new PrintWriter((Writer)new CommonsLogWriter(this.debugLogger))) {
/*     */           
/* 167 */           HessianDebugInputStream dis = new HessianDebugInputStream(inputStream, debugWriter);
/*     */           
/* 169 */           HessianDebugOutputStream dos = new HessianDebugOutputStream(outputStream, debugWriter);
/* 170 */           dis.startTop2();
/* 171 */           dos.startTop2();
/* 172 */           hessianDebugInputStream = dis;
/* 173 */           hessianDebugOutputStream = dos;
/*     */         } 
/*     */       }
/*     */       
/* 177 */       if (!hessianDebugInputStream.markSupported()) {
/* 178 */         bufferedInputStream = new BufferedInputStream((InputStream)hessianDebugInputStream);
/* 179 */         bufferedInputStream.mark(1);
/*     */       } 
/*     */       
/* 182 */       int code = bufferedInputStream.read();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 189 */       if (code == 72) {
/*     */         
/* 191 */         int major = bufferedInputStream.read();
/* 192 */         int minor = bufferedInputStream.read();
/* 193 */         if (major != 2) {
/* 194 */           throw new IOException("Version " + major + '.' + minor + " is not understood");
/*     */         }
/* 196 */         Hessian2Input hessian2Input = new Hessian2Input(bufferedInputStream);
/* 197 */         Hessian2Output hessian2Output = new Hessian2Output((OutputStream)hessianDebugOutputStream);
/* 198 */         hessian2Input.readCall();
/*     */       }
/* 200 */       else if (code == 67) {
/*     */         
/* 202 */         bufferedInputStream.reset();
/* 203 */         Hessian2Input hessian2Input = new Hessian2Input(bufferedInputStream);
/* 204 */         Hessian2Output hessian2Output = new Hessian2Output((OutputStream)hessianDebugOutputStream);
/* 205 */         hessian2Input.readCall();
/*     */       }
/* 207 */       else if (code == 99) {
/*     */         
/* 209 */         int major = bufferedInputStream.read();
/* 210 */         int minor = bufferedInputStream.read();
/* 211 */         hessianInput = new HessianInput(bufferedInputStream);
/* 212 */         if (major >= 2) {
/* 213 */           Hessian2Output hessian2Output = new Hessian2Output((OutputStream)hessianDebugOutputStream);
/*     */         } else {
/*     */           
/* 216 */           hessianOutput = new HessianOutput((OutputStream)hessianDebugOutputStream);
/*     */         } 
/*     */       } else {
/*     */         
/* 220 */         throw new IOException("Expected 'H'/'C' (Hessian 2.0) or 'c' (Hessian 1.0) in hessian input at " + code);
/*     */       } 
/*     */       
/* 223 */       hessianInput.setSerializerFactory(this.serializerFactory);
/* 224 */       hessianOutput.setSerializerFactory(this.serializerFactory);
/* 225 */       if (this.remoteResolver != null) {
/* 226 */         hessianInput.setRemoteResolver(this.remoteResolver);
/*     */       }
/*     */       
/*     */       try {
/* 230 */         skeleton.invoke((AbstractHessianInput)hessianInput, (AbstractHessianOutput)hessianOutput);
/*     */       } finally {
/*     */         
/*     */         try {
/* 234 */           hessianInput.close();
/* 235 */           bufferedInputStream.close();
/*     */         }
/* 237 */         catch (IOException iOException) {}
/*     */ 
/*     */         
/*     */         try {
/* 241 */           hessianOutput.close();
/* 242 */           hessianDebugOutputStream.close();
/*     */         }
/* 244 */         catch (IOException iOException) {}
/*     */       }
/*     */     
/*     */     }
/*     */     finally {
/*     */       
/* 250 */       resetThreadContextClassLoader(originalClassLoader);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/remoting/caucho/HessianExporter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */