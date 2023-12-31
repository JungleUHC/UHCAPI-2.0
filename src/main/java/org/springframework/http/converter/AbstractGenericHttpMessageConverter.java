/*     */ package org.springframework.http.converter;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.lang.reflect.Type;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpOutputMessage;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.StreamingHttpOutputMessage;
/*     */ import org.springframework.lang.Nullable;
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
/*     */ public abstract class AbstractGenericHttpMessageConverter<T>
/*     */   extends AbstractHttpMessageConverter<T>
/*     */   implements GenericHttpMessageConverter<T>
/*     */ {
/*     */   protected AbstractGenericHttpMessageConverter() {}
/*     */   
/*     */   protected AbstractGenericHttpMessageConverter(MediaType supportedMediaType) {
/*  52 */     super(supportedMediaType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractGenericHttpMessageConverter(MediaType... supportedMediaTypes) {
/*  60 */     super(supportedMediaTypes);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean supports(Class<?> clazz) {
/*  66 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
/*  71 */     return (type instanceof Class) ? canRead((Class)type, mediaType) : canRead(mediaType);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canWrite(@Nullable Type type, Class<?> clazz, @Nullable MediaType mediaType) {
/*  76 */     return canWrite(clazz, mediaType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void write(T t, @Nullable Type type, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
/*  87 */     final HttpHeaders headers = outputMessage.getHeaders();
/*  88 */     addDefaultHeaders(headers, t, contentType);
/*     */     
/*  90 */     if (outputMessage instanceof StreamingHttpOutputMessage) {
/*  91 */       StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage)outputMessage;
/*  92 */       streamingOutputMessage.setBody(outputStream -> writeInternal((T)t, type, new HttpOutputMessage()
/*     */             {
/*     */               public OutputStream getBody() {
/*  95 */                 return outputStream;
/*     */               }
/*     */               
/*     */               public HttpHeaders getHeaders() {
/*  99 */                 return headers;
/*     */               }
/*     */             }));
/*     */     } else {
/*     */       
/* 104 */       writeInternal(t, type, outputMessage);
/* 105 */       outputMessage.getBody().flush();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeInternal(T t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
/* 113 */     writeInternal(t, (Type)null, outputMessage);
/*     */   }
/*     */   
/*     */   protected abstract void writeInternal(T paramT, @Nullable Type paramType, HttpOutputMessage paramHttpOutputMessage) throws IOException, HttpMessageNotWritableException;
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/AbstractGenericHttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */