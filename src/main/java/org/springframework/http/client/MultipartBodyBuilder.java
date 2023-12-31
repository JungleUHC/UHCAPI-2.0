/*     */ package org.springframework.http.client;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Consumer;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.ParameterizedTypeReference;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.ResolvableTypeProvider;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.http.HttpEntity;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.codec.multipart.Part;
/*     */ import org.springframework.lang.NonNull;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MultipartBodyBuilder
/*     */ {
/*  91 */   private final LinkedMultiValueMap<String, DefaultPartBuilder> parts = new LinkedMultiValueMap();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PartBuilder part(String name, Object part) {
/* 116 */     return part(name, part, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PartBuilder part(String name, Object part, @Nullable MediaType contentType) {
/*     */     Object partBody;
/* 127 */     Assert.hasLength(name, "'name' must not be empty");
/* 128 */     Assert.notNull(part, "'part' must not be null");
/*     */     
/* 130 */     if (part instanceof Part) {
/* 131 */       Part partObject = (Part)part;
/* 132 */       PartBuilder partBuilder = asyncPart(name, partObject.content(), DataBuffer.class);
/* 133 */       if (!partObject.headers().isEmpty()) {
/* 134 */         partBuilder.headers(headers -> {
/*     */               headers.putAll((Map)partObject.headers());
/*     */               
/*     */               String filename = headers.getContentDisposition().getFilename();
/*     */               headers.setContentDispositionFormData(name, filename);
/*     */             });
/*     */       }
/* 141 */       if (contentType != null) {
/* 142 */         partBuilder.contentType(contentType);
/*     */       }
/* 144 */       return partBuilder;
/*     */     } 
/*     */     
/* 147 */     if (part instanceof PublisherEntity) {
/* 148 */       PublisherPartBuilder<?, ?> publisherPartBuilder = new PublisherPartBuilder<>(name, (PublisherEntity<?, ?>)part);
/* 149 */       if (contentType != null) {
/* 150 */         publisherPartBuilder.contentType(contentType);
/*     */       }
/* 152 */       this.parts.add(name, publisherPartBuilder);
/* 153 */       return publisherPartBuilder;
/*     */     } 
/*     */ 
/*     */     
/* 157 */     HttpHeaders partHeaders = null;
/* 158 */     if (part instanceof HttpEntity) {
/* 159 */       partBody = ((HttpEntity)part).getBody();
/* 160 */       partHeaders = new HttpHeaders();
/* 161 */       partHeaders.putAll((Map)((HttpEntity)part).getHeaders());
/*     */     } else {
/*     */       
/* 164 */       partBody = part;
/*     */     } 
/*     */     
/* 167 */     if (partBody instanceof Publisher) {
/* 168 */       throw new IllegalArgumentException("Use asyncPart(String, Publisher, Class) or asyncPart(String, Publisher, ParameterizedTypeReference) or or MultipartBodyBuilder.PublisherEntity");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 174 */     DefaultPartBuilder builder = new DefaultPartBuilder(name, partHeaders, partBody);
/* 175 */     if (contentType != null) {
/* 176 */       builder.contentType(contentType);
/*     */     }
/* 178 */     this.parts.add(name, builder);
/* 179 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, P extends Publisher<T>> PartBuilder asyncPart(String name, P publisher, Class<T> elementClass) {
/* 190 */     Assert.hasLength(name, "'name' must not be empty");
/* 191 */     Assert.notNull(publisher, "'publisher' must not be null");
/* 192 */     Assert.notNull(elementClass, "'elementClass' must not be null");
/*     */     
/* 194 */     PublisherPartBuilder<T, P> builder = (PublisherPartBuilder)new PublisherPartBuilder<>(name, null, (Publisher<T>)publisher, elementClass);
/* 195 */     this.parts.add(name, builder);
/* 196 */     return builder;
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
/*     */   public <T, P extends Publisher<T>> PartBuilder asyncPart(String name, P publisher, ParameterizedTypeReference<T> typeReference) {
/* 210 */     Assert.hasLength(name, "'name' must not be empty");
/* 211 */     Assert.notNull(publisher, "'publisher' must not be null");
/* 212 */     Assert.notNull(typeReference, "'typeReference' must not be null");
/*     */     
/* 214 */     PublisherPartBuilder<T, P> builder = (PublisherPartBuilder)new PublisherPartBuilder<>(name, null, (Publisher<T>)publisher, typeReference);
/* 215 */     this.parts.add(name, builder);
/* 216 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MultiValueMap<String, HttpEntity<?>> build() {
/* 223 */     LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap(this.parts.size());
/* 224 */     for (Map.Entry<String, List<DefaultPartBuilder>> entry : (Iterable<Map.Entry<String, List<DefaultPartBuilder>>>)this.parts.entrySet()) {
/* 225 */       for (DefaultPartBuilder builder : entry.getValue()) {
/* 226 */         HttpEntity<?> entity = builder.build();
/* 227 */         linkedMultiValueMap.add(entry.getKey(), entity);
/*     */       } 
/*     */     } 
/* 230 */     return (MultiValueMap<String, HttpEntity<?>>)linkedMultiValueMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface PartBuilder
/*     */   {
/*     */     PartBuilder contentType(MediaType param1MediaType);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     PartBuilder filename(String param1String);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     PartBuilder header(String param1String, String... param1VarArgs);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     PartBuilder headers(Consumer<HttpHeaders> param1Consumer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class DefaultPartBuilder
/*     */     implements PartBuilder
/*     */   {
/*     */     private final String name;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     protected HttpHeaders headers;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     protected final Object body;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public DefaultPartBuilder(String name, @Nullable HttpHeaders headers, @Nullable Object body) {
/* 286 */       this.name = name;
/* 287 */       this.headers = headers;
/* 288 */       this.body = body;
/*     */     }
/*     */ 
/*     */     
/*     */     public MultipartBodyBuilder.PartBuilder contentType(MediaType contentType) {
/* 293 */       initHeadersIfNecessary().setContentType(contentType);
/* 294 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public MultipartBodyBuilder.PartBuilder filename(String filename) {
/* 299 */       initHeadersIfNecessary().setContentDispositionFormData(this.name, filename);
/* 300 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public MultipartBodyBuilder.PartBuilder header(String headerName, String... headerValues) {
/* 305 */       initHeadersIfNecessary().addAll(headerName, Arrays.asList(headerValues));
/* 306 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public MultipartBodyBuilder.PartBuilder headers(Consumer<HttpHeaders> headersConsumer) {
/* 311 */       headersConsumer.accept(initHeadersIfNecessary());
/* 312 */       return this;
/*     */     }
/*     */     
/*     */     private HttpHeaders initHeadersIfNecessary() {
/* 316 */       if (this.headers == null) {
/* 317 */         this.headers = new HttpHeaders();
/*     */       }
/* 319 */       return this.headers;
/*     */     }
/*     */     
/*     */     public HttpEntity<?> build() {
/* 323 */       return new HttpEntity(this.body, (MultiValueMap)this.headers);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class PublisherPartBuilder<S, P extends Publisher<S>>
/*     */     extends DefaultPartBuilder
/*     */   {
/*     */     private final ResolvableType resolvableType;
/*     */     
/*     */     public PublisherPartBuilder(String name, @Nullable HttpHeaders headers, P body, Class<S> elementClass) {
/* 333 */       super(name, headers, body);
/* 334 */       this.resolvableType = ResolvableType.forClass(elementClass);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public PublisherPartBuilder(String name, @Nullable HttpHeaders headers, P body, ParameterizedTypeReference<S> typeRef) {
/* 340 */       super(name, headers, body);
/* 341 */       this.resolvableType = ResolvableType.forType(typeRef);
/*     */     }
/*     */     
/*     */     public PublisherPartBuilder(String name, MultipartBodyBuilder.PublisherEntity<S, P> other) {
/* 345 */       super(name, other.getHeaders(), other.getBody());
/* 346 */       this.resolvableType = other.getResolvableType();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public HttpEntity<?> build() {
/* 352 */       Publisher publisher = (Publisher)this.body;
/* 353 */       Assert.state((publisher != null), "Publisher must not be null");
/* 354 */       return new MultipartBodyBuilder.PublisherEntity<>((MultiValueMap<String, String>)this.headers, publisher, this.resolvableType);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final class PublisherEntity<T, P extends Publisher<T>>
/*     */     extends HttpEntity<P>
/*     */     implements ResolvableTypeProvider
/*     */   {
/*     */     private final ResolvableType resolvableType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     PublisherEntity(@Nullable MultiValueMap<String, String> headers, P publisher, ResolvableType resolvableType) {
/* 374 */       super(publisher, headers);
/* 375 */       Assert.notNull(publisher, "'publisher' must not be null");
/* 376 */       Assert.notNull(resolvableType, "'resolvableType' must not be null");
/* 377 */       this.resolvableType = resolvableType;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NonNull
/*     */     public ResolvableType getResolvableType() {
/* 386 */       return this.resolvableType;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/MultipartBodyBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */