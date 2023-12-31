/*     */ package org.springframework.http.converter;
/*     */ 
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.springframework.core.io.ByteArrayResource;
/*     */ import org.springframework.core.io.InputStreamResource;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.http.HttpInputMessage;
/*     */ import org.springframework.http.HttpOutputMessage;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.MediaTypeFactory;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.StreamUtils;
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
/*     */ public class ResourceHttpMessageConverter
/*     */   extends AbstractHttpMessageConverter<Resource>
/*     */ {
/*     */   private final boolean supportsReadStreaming;
/*     */   
/*     */   public ResourceHttpMessageConverter() {
/*  56 */     super(MediaType.ALL);
/*  57 */     this.supportsReadStreaming = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResourceHttpMessageConverter(boolean supportsReadStreaming) {
/*  67 */     super(MediaType.ALL);
/*  68 */     this.supportsReadStreaming = supportsReadStreaming;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean supports(Class<?> clazz) {
/*  74 */     return Resource.class.isAssignableFrom(clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Resource readInternal(Class<? extends Resource> clazz, final HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
/*  81 */     if (this.supportsReadStreaming && InputStreamResource.class == clazz) {
/*  82 */       return (Resource)new InputStreamResource(inputMessage.getBody())
/*     */         {
/*     */           public String getFilename() {
/*  85 */             return inputMessage.getHeaders().getContentDisposition().getFilename();
/*     */           }
/*     */           
/*     */           public long contentLength() throws IOException {
/*  89 */             long length = inputMessage.getHeaders().getContentLength();
/*  90 */             return (length != -1L) ? length : super.contentLength();
/*     */           }
/*     */         };
/*     */     }
/*  94 */     if (Resource.class == clazz || ByteArrayResource.class.isAssignableFrom(clazz)) {
/*  95 */       byte[] body = StreamUtils.copyToByteArray(inputMessage.getBody());
/*  96 */       return (Resource)new ByteArrayResource(body)
/*     */         {
/*     */           @Nullable
/*     */           public String getFilename() {
/* 100 */             return inputMessage.getHeaders().getContentDisposition().getFilename();
/*     */           }
/*     */         };
/*     */     } 
/*     */     
/* 105 */     throw new HttpMessageNotReadableException("Unsupported resource class: " + clazz, inputMessage);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected MediaType getDefaultContentType(Resource resource) {
/* 111 */     return MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Long getContentLength(Resource resource, @Nullable MediaType contentType) throws IOException {
/* 118 */     if (InputStreamResource.class == resource.getClass()) {
/* 119 */       return null;
/*     */     }
/* 121 */     long contentLength = resource.contentLength();
/* 122 */     return (contentLength < 0L) ? null : Long.valueOf(contentLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeInternal(Resource resource, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
/* 129 */     writeContent(resource, outputMessage);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeContent(Resource resource, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
/*     */     try {
/* 137 */       InputStream in = resource.getInputStream();
/*     */       
/* 139 */       try { StreamUtils.copy(in, outputMessage.getBody()); }
/*     */       
/* 141 */       catch (NullPointerException nullPointerException)
/*     */       
/*     */       { 
/*     */         try {
/*     */           
/* 146 */           in.close();
/*     */         }
/* 148 */         catch (Throwable throwable) {} } finally { try { in.close(); } catch (Throwable throwable) {}
/*     */          }
/*     */ 
/*     */     
/*     */     }
/* 153 */     catch (FileNotFoundException fileNotFoundException) {}
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/ResourceHttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */