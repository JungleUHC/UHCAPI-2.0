/*     */ package org.springframework.http.converter;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import org.springframework.core.convert.ConversionService;
/*     */ import org.springframework.http.HttpInputMessage;
/*     */ import org.springframework.http.HttpOutputMessage;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
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
/*     */ public class ObjectToStringHttpMessageConverter
/*     */   extends AbstractHttpMessageConverter<Object>
/*     */ {
/*     */   private final ConversionService conversionService;
/*     */   private final StringHttpMessageConverter stringHttpMessageConverter;
/*     */   
/*     */   public ObjectToStringHttpMessageConverter(ConversionService conversionService) {
/*  66 */     this(conversionService, StringHttpMessageConverter.DEFAULT_CHARSET);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectToStringHttpMessageConverter(ConversionService conversionService, Charset defaultCharset) {
/*  75 */     super(defaultCharset, new MediaType[] { MediaType.TEXT_PLAIN });
/*     */     
/*  77 */     Assert.notNull(conversionService, "ConversionService is required");
/*  78 */     this.conversionService = conversionService;
/*  79 */     this.stringHttpMessageConverter = new StringHttpMessageConverter(defaultCharset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWriteAcceptCharset(boolean writeAcceptCharset) {
/*  87 */     this.stringHttpMessageConverter.setWriteAcceptCharset(writeAcceptCharset);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
/*  93 */     return (canRead(mediaType) && this.conversionService.canConvert(String.class, clazz));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
/*  98 */     return (canWrite(mediaType) && this.conversionService.canConvert(clazz, String.class));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean supports(Class<?> clazz) {
/* 104 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
/* 111 */     String value = this.stringHttpMessageConverter.readInternal(String.class, inputMessage);
/* 112 */     Object result = this.conversionService.convert(value, clazz);
/* 113 */     if (result == null) {
/* 114 */       throw new HttpMessageNotReadableException("Unexpected null conversion result for '" + value + "' to " + clazz, inputMessage);
/*     */     }
/*     */ 
/*     */     
/* 118 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException {
/* 123 */     String value = (String)this.conversionService.convert(obj, String.class);
/* 124 */     if (value != null) {
/* 125 */       this.stringHttpMessageConverter.writeInternal(value, outputMessage);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected Long getContentLength(Object obj, @Nullable MediaType contentType) {
/* 131 */     String value = (String)this.conversionService.convert(obj, String.class);
/* 132 */     if (value == null) {
/* 133 */       return Long.valueOf(0L);
/*     */     }
/* 135 */     return this.stringHttpMessageConverter.getContentLength(value, contentType);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/ObjectToStringHttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */