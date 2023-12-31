/*     */ package org.springframework.http.codec;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URLDecoder;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.Hints;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.core.log.LogFormatUtils;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.ReactiveHttpInputMessage;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import org.springframework.util.StringUtils;
/*     */ import reactor.core.publisher.Flux;
/*     */ import reactor.core.publisher.Mono;
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
/*     */ public class FormHttpMessageReader
/*     */   extends LoggingCodecSupport
/*     */   implements HttpMessageReader<MultiValueMap<String, String>>
/*     */ {
/*  58 */   public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
/*     */ 
/*     */   
/*  61 */   private static final ResolvableType MULTIVALUE_STRINGS_TYPE = ResolvableType.forClassWithGenerics(MultiValueMap.class, new Class[] { String.class, String.class });
/*     */ 
/*     */   
/*  64 */   private Charset defaultCharset = DEFAULT_CHARSET;
/*     */   
/*  66 */   private int maxInMemorySize = 262144;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDefaultCharset(Charset charset) {
/*  75 */     Assert.notNull(charset, "Charset must not be null");
/*  76 */     this.defaultCharset = charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Charset getDefaultCharset() {
/*  83 */     return this.defaultCharset;
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
/*     */   public void setMaxInMemorySize(int byteCount) {
/*  95 */     this.maxInMemorySize = byteCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxInMemorySize() {
/* 103 */     return this.maxInMemorySize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
/* 111 */     boolean multiValueUnresolved = (elementType.hasUnresolvableGenerics() && MultiValueMap.class.isAssignableFrom(elementType.toClass()));
/*     */     
/* 113 */     return ((MULTIVALUE_STRINGS_TYPE.isAssignableFrom(elementType) || multiValueUnresolved) && (mediaType == null || MediaType.APPLICATION_FORM_URLENCODED
/* 114 */       .isCompatibleWith(mediaType)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Flux<MultiValueMap<String, String>> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
/* 121 */     return Flux.from((Publisher)readMono(elementType, message, hints));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<MultiValueMap<String, String>> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
/* 128 */     MediaType contentType = message.getHeaders().getContentType();
/* 129 */     Charset charset = getMediaTypeCharset(contentType);
/*     */     
/* 131 */     return DataBufferUtils.join((Publisher)message.getBody(), this.maxInMemorySize)
/* 132 */       .map(buffer -> {
/*     */           CharBuffer charBuffer = charset.decode(buffer.asByteBuffer());
/*     */           String body = charBuffer.toString();
/*     */           DataBufferUtils.release(buffer);
/*     */           MultiValueMap<String, String> formData = parseFormData(charset, body);
/*     */           logFormData(formData, hints);
/*     */           return formData;
/*     */         });
/*     */   }
/*     */   
/*     */   private void logFormData(MultiValueMap<String, String> formData, Map<String, Object> hints) {
/* 143 */     LogFormatUtils.traceDebug(this.logger, traceOn -> Hints.getLogPrefix(hints) + "Read " + (isEnableLoggingRequestDetails() ? LogFormatUtils.formatValue(formData, !traceOn.booleanValue()) : ("form fields " + formData.keySet() + " (content masked)")));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Charset getMediaTypeCharset(@Nullable MediaType mediaType) {
/* 150 */     if (mediaType != null && mediaType.getCharset() != null) {
/* 151 */       return mediaType.getCharset();
/*     */     }
/*     */     
/* 154 */     return getDefaultCharset();
/*     */   }
/*     */ 
/*     */   
/*     */   private MultiValueMap<String, String> parseFormData(Charset charset, String body) {
/* 159 */     String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
/* 160 */     LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap(pairs.length);
/*     */     try {
/* 162 */       for (String pair : pairs) {
/* 163 */         int idx = pair.indexOf('=');
/* 164 */         if (idx == -1) {
/* 165 */           linkedMultiValueMap.add(URLDecoder.decode(pair, charset.name()), null);
/*     */         } else {
/*     */           
/* 168 */           String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
/* 169 */           String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
/* 170 */           linkedMultiValueMap.add(name, value);
/*     */         }
/*     */       
/*     */       } 
/* 174 */     } catch (UnsupportedEncodingException ex) {
/* 175 */       throw new IllegalStateException(ex);
/*     */     } 
/* 177 */     return (MultiValueMap<String, String>)linkedMultiValueMap;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<MediaType> getReadableMediaTypes() {
/* 182 */     return Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/FormHttpMessageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */