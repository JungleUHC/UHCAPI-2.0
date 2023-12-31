/*     */ package org.springframework.http.codec.multipart;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.stream.Collectors;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.Hints;
/*     */ import org.springframework.core.log.LogFormatUtils;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.ReactiveHttpInputMessage;
/*     */ import org.springframework.http.codec.HttpMessageReader;
/*     */ import org.springframework.http.codec.LoggingCodecSupport;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
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
/*     */ public class MultipartHttpMessageReader
/*     */   extends LoggingCodecSupport
/*     */   implements HttpMessageReader<MultiValueMap<String, Part>>
/*     */ {
/*  56 */   private static final ResolvableType MULTIPART_VALUE_TYPE = ResolvableType.forClassWithGenerics(MultiValueMap.class, new Class[] { String.class, Part.class });
/*     */ 
/*     */   
/*  59 */   static final List<MediaType> MIME_TYPES = Collections.unmodifiableList(Arrays.asList(new MediaType[] { MediaType.MULTIPART_FORM_DATA, MediaType.MULTIPART_MIXED, MediaType.MULTIPART_RELATED }));
/*     */ 
/*     */   
/*     */   private final HttpMessageReader<Part> partReader;
/*     */ 
/*     */ 
/*     */   
/*     */   public MultipartHttpMessageReader(HttpMessageReader<Part> partReader) {
/*  67 */     Assert.notNull(partReader, "'partReader' is required");
/*  68 */     this.partReader = partReader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpMessageReader<Part> getPartReader() {
/*  77 */     return this.partReader;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<MediaType> getReadableMediaTypes() {
/*  82 */     return MIME_TYPES;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
/*  87 */     if (MULTIPART_VALUE_TYPE.isAssignableFrom(elementType)) {
/*  88 */       if (mediaType == null) {
/*  89 */         return true;
/*     */       }
/*  91 */       for (MediaType supportedMediaType : MIME_TYPES) {
/*  92 */         if (supportedMediaType.isCompatibleWith(mediaType)) {
/*  93 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/*  97 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Flux<MultiValueMap<String, Part>> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
/* 105 */     return Flux.from((Publisher)readMono(elementType, message, hints));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<MultiValueMap<String, Part>> readMono(ResolvableType elementType, ReactiveHttpInputMessage inputMessage, Map<String, Object> hints) {
/* 114 */     Map<String, Object> allHints = Hints.merge(hints, Hints.SUPPRESS_LOGGING_HINT, Boolean.valueOf(true));
/*     */     
/* 116 */     return this.partReader.read(elementType, inputMessage, allHints)
/* 117 */       .collectMultimap(Part::name)
/* 118 */       .doOnNext(map -> LogFormatUtils.traceDebug(this.logger, ()))
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 124 */       .map(this::toMultiValueMap);
/*     */   }
/*     */   
/*     */   private LinkedMultiValueMap<String, Part> toMultiValueMap(Map<String, Collection<Part>> map) {
/* 128 */     return new LinkedMultiValueMap((Map)map.entrySet().stream()
/* 129 */         .collect(Collectors.toMap(Map.Entry::getKey, e -> toList((Collection<Part>)e.getValue()))));
/*     */   }
/*     */   
/*     */   private List<Part> toList(Collection<Part> collection) {
/* 133 */     return (collection instanceof List) ? (List<Part>)collection : new ArrayList<>(collection);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/multipart/MultipartHttpMessageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */