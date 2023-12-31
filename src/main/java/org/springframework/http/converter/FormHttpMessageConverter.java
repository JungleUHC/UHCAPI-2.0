/*     */ package org.springframework.http.converter;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URLDecoder;
/*     */ import java.net.URLEncoder;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.mail.internet.MimeUtility;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.http.HttpEntity;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpInputMessage;
/*     */ import org.springframework.http.HttpOutputMessage;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.StreamingHttpOutputMessage;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MimeTypeUtils;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import org.springframework.util.StreamUtils;
/*     */ import org.springframework.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FormHttpMessageConverter
/*     */   implements HttpMessageConverter<MultiValueMap<String, ?>>
/*     */ {
/* 161 */   public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
/*     */   
/* 163 */   private static final MediaType DEFAULT_FORM_DATA_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_FORM_URLENCODED, DEFAULT_CHARSET);
/*     */ 
/*     */ 
/*     */   
/* 167 */   private List<MediaType> supportedMediaTypes = new ArrayList<>();
/*     */   
/* 169 */   private List<HttpMessageConverter<?>> partConverters = new ArrayList<>();
/*     */   
/* 171 */   private Charset charset = DEFAULT_CHARSET;
/*     */   
/*     */   @Nullable
/*     */   private Charset multipartCharset;
/*     */ 
/*     */   
/*     */   public FormHttpMessageConverter() {
/* 178 */     this.supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
/* 179 */     this.supportedMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
/* 180 */     this.supportedMediaTypes.add(MediaType.MULTIPART_MIXED);
/*     */     
/* 182 */     this.partConverters.add(new ByteArrayHttpMessageConverter());
/* 183 */     this.partConverters.add(new StringHttpMessageConverter());
/* 184 */     this.partConverters.add(new ResourceHttpMessageConverter());
/*     */     
/* 186 */     applyDefaultCharset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
/* 196 */     Assert.notNull(supportedMediaTypes, "'supportedMediaTypes' must not be null");
/*     */     
/* 198 */     this.supportedMediaTypes = new ArrayList<>(supportedMediaTypes);
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
/*     */   public void addSupportedMediaTypes(MediaType... supportedMediaTypes) {
/* 210 */     Assert.notNull(supportedMediaTypes, "'supportedMediaTypes' must not be null");
/* 211 */     Assert.noNullElements((Object[])supportedMediaTypes, "'supportedMediaTypes' must not contain null elements");
/* 212 */     Collections.addAll(this.supportedMediaTypes, supportedMediaTypes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<MediaType> getSupportedMediaTypes() {
/* 222 */     return Collections.unmodifiableList(this.supportedMediaTypes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPartConverters(List<HttpMessageConverter<?>> partConverters) {
/* 230 */     Assert.notEmpty(partConverters, "'partConverters' must not be empty");
/* 231 */     this.partConverters = partConverters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<HttpMessageConverter<?>> getPartConverters() {
/* 240 */     return Collections.unmodifiableList(this.partConverters);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addPartConverter(HttpMessageConverter<?> partConverter) {
/* 248 */     Assert.notNull(partConverter, "'partConverter' must not be null");
/* 249 */     this.partConverters.add(partConverter);
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
/*     */   public void setCharset(@Nullable Charset charset) {
/* 266 */     if (charset != this.charset) {
/* 267 */       this.charset = (charset != null) ? charset : DEFAULT_CHARSET;
/* 268 */       applyDefaultCharset();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void applyDefaultCharset() {
/* 276 */     for (HttpMessageConverter<?> candidate : this.partConverters) {
/* 277 */       if (candidate instanceof AbstractHttpMessageConverter) {
/* 278 */         AbstractHttpMessageConverter<?> converter = (AbstractHttpMessageConverter)candidate;
/*     */         
/* 280 */         if (converter.getDefaultCharset() != null) {
/* 281 */           converter.setDefaultCharset(this.charset);
/*     */         }
/*     */       } 
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
/*     */   public void setMultipartCharset(Charset charset) {
/* 298 */     this.multipartCharset = charset;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
/* 304 */     if (!MultiValueMap.class.isAssignableFrom(clazz)) {
/* 305 */       return false;
/*     */     }
/* 307 */     if (mediaType == null) {
/* 308 */       return true;
/*     */     }
/* 310 */     for (MediaType supportedMediaType : getSupportedMediaTypes()) {
/* 311 */       if (supportedMediaType.getType().equalsIgnoreCase("multipart")) {
/*     */         continue;
/*     */       }
/*     */       
/* 315 */       if (supportedMediaType.includes(mediaType)) {
/* 316 */         return true;
/*     */       }
/*     */     } 
/* 319 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
/* 324 */     if (!MultiValueMap.class.isAssignableFrom(clazz)) {
/* 325 */       return false;
/*     */     }
/* 327 */     if (mediaType == null || MediaType.ALL.equals(mediaType)) {
/* 328 */       return true;
/*     */     }
/* 330 */     for (MediaType supportedMediaType : getSupportedMediaTypes()) {
/* 331 */       if (supportedMediaType.isCompatibleWith(mediaType)) {
/* 332 */         return true;
/*     */       }
/*     */     } 
/* 335 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MultiValueMap<String, String> read(@Nullable Class<? extends MultiValueMap<String, ?>> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
/* 342 */     MediaType contentType = inputMessage.getHeaders().getContentType();
/*     */     
/* 344 */     Charset charset = (contentType != null && contentType.getCharset() != null) ? contentType.getCharset() : this.charset;
/* 345 */     String body = StreamUtils.copyToString(inputMessage.getBody(), charset);
/*     */     
/* 347 */     String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
/* 348 */     LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap(pairs.length);
/* 349 */     for (String pair : pairs) {
/* 350 */       int idx = pair.indexOf('=');
/* 351 */       if (idx == -1) {
/* 352 */         linkedMultiValueMap.add(URLDecoder.decode(pair, charset.name()), null);
/*     */       } else {
/*     */         
/* 355 */         String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
/* 356 */         String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
/* 357 */         linkedMultiValueMap.add(name, value);
/*     */       } 
/*     */     } 
/* 360 */     return (MultiValueMap<String, String>)linkedMultiValueMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(MultiValueMap<String, ?> map, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
/* 368 */     if (isMultipart(map, contentType)) {
/* 369 */       writeMultipart((MultiValueMap)map, contentType, outputMessage);
/*     */     } else {
/*     */       
/* 372 */       writeForm((MultiValueMap)map, contentType, outputMessage);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isMultipart(MultiValueMap<String, ?> map, @Nullable MediaType contentType) {
/* 378 */     if (contentType != null) {
/* 379 */       return contentType.getType().equalsIgnoreCase("multipart");
/*     */     }
/* 381 */     for (List<?> values : (Iterable<List<?>>)map.values()) {
/* 382 */       for (Object value : values) {
/* 383 */         if (value != null && !(value instanceof String)) {
/* 384 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 388 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeForm(MultiValueMap<String, Object> formData, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException {
/* 394 */     contentType = getFormContentType(contentType);
/* 395 */     outputMessage.getHeaders().setContentType(contentType);
/*     */     
/* 397 */     Charset charset = contentType.getCharset();
/* 398 */     Assert.notNull(charset, "No charset");
/*     */     
/* 400 */     byte[] bytes = serializeForm(formData, charset).getBytes(charset);
/* 401 */     outputMessage.getHeaders().setContentLength(bytes.length);
/*     */     
/* 403 */     if (outputMessage instanceof StreamingHttpOutputMessage) {
/* 404 */       StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage)outputMessage;
/* 405 */       streamingOutputMessage.setBody(outputStream -> StreamUtils.copy(bytes, outputStream));
/*     */     } else {
/*     */       
/* 408 */       StreamUtils.copy(bytes, outputMessage.getBody());
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
/*     */   protected MediaType getFormContentType(@Nullable MediaType contentType) {
/* 424 */     if (contentType == null) {
/* 425 */       return DEFAULT_FORM_DATA_MEDIA_TYPE;
/*     */     }
/* 427 */     if (contentType.getCharset() == null) {
/* 428 */       return new MediaType(contentType, this.charset);
/*     */     }
/*     */     
/* 431 */     return contentType;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String serializeForm(MultiValueMap<String, Object> formData, Charset charset) {
/* 436 */     StringBuilder builder = new StringBuilder();
/* 437 */     formData.forEach((name, values) -> {
/*     */           if (name == null) {
/*     */             Assert.isTrue(CollectionUtils.isEmpty(values), "Null name in form data: " + formData);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             return;
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           values.forEach(());
/*     */         });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 459 */     return builder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeMultipart(MultiValueMap<String, Object> parts, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException {
/* 469 */     if (contentType == null) {
/* 470 */       contentType = MediaType.MULTIPART_FORM_DATA;
/*     */     }
/*     */     
/* 473 */     Map<String, String> parameters = new LinkedHashMap<>(contentType.getParameters().size() + 2);
/* 474 */     parameters.putAll(contentType.getParameters());
/*     */     
/* 476 */     byte[] boundary = generateMultipartBoundary();
/* 477 */     if (!isFilenameCharsetSet() && 
/* 478 */       !this.charset.equals(StandardCharsets.UTF_8) && 
/* 479 */       !this.charset.equals(StandardCharsets.US_ASCII)) {
/* 480 */       parameters.put("charset", this.charset.name());
/*     */     }
/*     */     
/* 483 */     parameters.put("boundary", new String(boundary, StandardCharsets.US_ASCII));
/*     */ 
/*     */     
/* 486 */     contentType = new MediaType(contentType, parameters);
/* 487 */     outputMessage.getHeaders().setContentType(contentType);
/*     */     
/* 489 */     if (outputMessage instanceof StreamingHttpOutputMessage) {
/* 490 */       StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage)outputMessage;
/* 491 */       streamingOutputMessage.setBody(outputStream -> {
/*     */             writeParts(outputStream, parts, boundary);
/*     */             
/*     */             writeEnd(outputStream, boundary);
/*     */           });
/*     */     } else {
/* 497 */       writeParts(outputMessage.getBody(), parts, boundary);
/* 498 */       writeEnd(outputMessage.getBody(), boundary);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isFilenameCharsetSet() {
/* 508 */     return (this.multipartCharset != null);
/*     */   }
/*     */   
/*     */   private void writeParts(OutputStream os, MultiValueMap<String, Object> parts, byte[] boundary) throws IOException {
/* 512 */     for (Map.Entry<String, List<Object>> entry : (Iterable<Map.Entry<String, List<Object>>>)parts.entrySet()) {
/* 513 */       String name = entry.getKey();
/* 514 */       for (Object part : entry.getValue()) {
/* 515 */         if (part != null) {
/* 516 */           writeBoundary(os, boundary);
/* 517 */           writePart(name, getHttpEntity(part), os);
/* 518 */           writeNewLine(os);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void writePart(String name, HttpEntity<?> partEntity, OutputStream os) throws IOException {
/* 526 */     Object partBody = partEntity.getBody();
/* 527 */     if (partBody == null) {
/* 528 */       throw new IllegalStateException("Empty body for part '" + name + "': " + partEntity);
/*     */     }
/* 530 */     Class<?> partType = partBody.getClass();
/* 531 */     HttpHeaders partHeaders = partEntity.getHeaders();
/* 532 */     MediaType partContentType = partHeaders.getContentType();
/* 533 */     for (HttpMessageConverter<?> messageConverter : this.partConverters) {
/* 534 */       if (messageConverter.canWrite(partType, partContentType)) {
/* 535 */         Charset charset = isFilenameCharsetSet() ? StandardCharsets.US_ASCII : this.charset;
/* 536 */         HttpOutputMessage multipartMessage = new MultipartHttpOutputMessage(os, charset);
/* 537 */         multipartMessage.getHeaders().setContentDispositionFormData(name, getFilename(partBody));
/* 538 */         if (!partHeaders.isEmpty()) {
/* 539 */           multipartMessage.getHeaders().putAll((Map)partHeaders);
/*     */         }
/* 541 */         messageConverter.write(partBody, partContentType, multipartMessage);
/*     */         return;
/*     */       } 
/*     */     } 
/* 545 */     throw new HttpMessageNotWritableException("Could not write request: no suitable HttpMessageConverter found for request type [" + partType
/* 546 */         .getName() + "]");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] generateMultipartBoundary() {
/* 555 */     return MimeTypeUtils.generateMultipartBoundary();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpEntity<?> getHttpEntity(Object part) {
/* 565 */     return (part instanceof HttpEntity) ? (HttpEntity)part : new HttpEntity(part);
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
/*     */   @Nullable
/*     */   protected String getFilename(Object part) {
/* 578 */     if (part instanceof Resource) {
/* 579 */       Resource resource = (Resource)part;
/* 580 */       String filename = resource.getFilename();
/* 581 */       if (filename != null && this.multipartCharset != null) {
/* 582 */         filename = MimeDelegate.encode(filename, this.multipartCharset.name());
/*     */       }
/* 584 */       return filename;
/*     */     } 
/*     */     
/* 587 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeBoundary(OutputStream os, byte[] boundary) throws IOException {
/* 593 */     os.write(45);
/* 594 */     os.write(45);
/* 595 */     os.write(boundary);
/* 596 */     writeNewLine(os);
/*     */   }
/*     */   
/*     */   private static void writeEnd(OutputStream os, byte[] boundary) throws IOException {
/* 600 */     os.write(45);
/* 601 */     os.write(45);
/* 602 */     os.write(boundary);
/* 603 */     os.write(45);
/* 604 */     os.write(45);
/* 605 */     writeNewLine(os);
/*     */   }
/*     */   
/*     */   private static void writeNewLine(OutputStream os) throws IOException {
/* 609 */     os.write(13);
/* 610 */     os.write(10);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class MultipartHttpOutputMessage
/*     */     implements HttpOutputMessage
/*     */   {
/*     */     private final OutputStream outputStream;
/*     */ 
/*     */     
/*     */     private final Charset charset;
/*     */ 
/*     */     
/* 624 */     private final HttpHeaders headers = new HttpHeaders();
/*     */     
/*     */     private boolean headersWritten = false;
/*     */     
/*     */     public MultipartHttpOutputMessage(OutputStream outputStream, Charset charset) {
/* 629 */       this.outputStream = outputStream;
/* 630 */       this.charset = charset;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHeaders getHeaders() {
/* 635 */       return this.headersWritten ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
/*     */     }
/*     */ 
/*     */     
/*     */     public OutputStream getBody() throws IOException {
/* 640 */       writeHeaders();
/* 641 */       return this.outputStream;
/*     */     }
/*     */     
/*     */     private void writeHeaders() throws IOException {
/* 645 */       if (!this.headersWritten) {
/* 646 */         for (Map.Entry<String, List<String>> entry : (Iterable<Map.Entry<String, List<String>>>)this.headers.entrySet()) {
/* 647 */           byte[] headerName = getBytes(entry.getKey());
/* 648 */           for (String headerValueString : entry.getValue()) {
/* 649 */             byte[] headerValue = getBytes(headerValueString);
/* 650 */             this.outputStream.write(headerName);
/* 651 */             this.outputStream.write(58);
/* 652 */             this.outputStream.write(32);
/* 653 */             this.outputStream.write(headerValue);
/* 654 */             FormHttpMessageConverter.writeNewLine(this.outputStream);
/*     */           } 
/*     */         } 
/* 657 */         FormHttpMessageConverter.writeNewLine(this.outputStream);
/* 658 */         this.headersWritten = true;
/*     */       } 
/*     */     }
/*     */     
/*     */     private byte[] getBytes(String name) {
/* 663 */       return name.getBytes(this.charset);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class MimeDelegate
/*     */   {
/*     */     public static String encode(String value, String charset) {
/*     */       try {
/* 675 */         return MimeUtility.encodeText(value, charset, null);
/*     */       }
/* 677 */       catch (UnsupportedEncodingException ex) {
/* 678 */         throw new IllegalStateException(ex);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/FormHttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */