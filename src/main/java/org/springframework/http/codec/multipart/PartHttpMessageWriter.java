/*    */ package org.springframework.http.codec.multipart;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.reactivestreams.Publisher;
/*    */ import org.springframework.core.ResolvableType;
/*    */ import org.springframework.core.codec.Hints;
/*    */ import org.springframework.core.io.buffer.DataBuffer;
/*    */ import org.springframework.core.io.buffer.DataBufferFactory;
/*    */ import org.springframework.core.io.buffer.DataBufferUtils;
/*    */ import org.springframework.core.io.buffer.PooledDataBuffer;
/*    */ import org.springframework.http.HttpHeaders;
/*    */ import org.springframework.http.MediaType;
/*    */ import org.springframework.http.ReactiveHttpOutputMessage;
/*    */ import org.springframework.http.codec.HttpMessageWriter;
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.util.MultiValueMap;
/*    */ import reactor.core.publisher.Flux;
/*    */ import reactor.core.publisher.Mono;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PartHttpMessageWriter
/*    */   extends MultipartWriterSupport
/*    */   implements HttpMessageWriter<Part>
/*    */ {
/*    */   public PartHttpMessageWriter() {
/* 49 */     super(MultipartHttpMessageReader.MIME_TYPES);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Mono<Void> write(Publisher<? extends Part> parts, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage outputMessage, Map<String, Object> hints) {
/* 58 */     byte[] boundary = generateMultipartBoundary();
/*    */     
/* 60 */     mediaType = getMultipartMediaType(mediaType, boundary);
/* 61 */     outputMessage.getHeaders().setContentType(mediaType);
/*    */     
/* 63 */     if (this.logger.isDebugEnabled()) {
/* 64 */       this.logger.debug(Hints.getLogPrefix(hints) + "Encoding Publisher<Part>");
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 70 */     Flux<DataBuffer> body = Flux.from(parts).concatMap(part -> encodePart(boundary, part, outputMessage.bufferFactory())).concatWith((Publisher)generateLastLine(boundary, outputMessage.bufferFactory())).doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);
/*    */     
/* 72 */     if (this.logger.isDebugEnabled()) {
/* 73 */       body = body.doOnNext(buffer -> Hints.touchDataBuffer(buffer, hints, this.logger));
/*    */     }
/*    */     
/* 76 */     return outputMessage.writeWith((Publisher)body);
/*    */   }
/*    */   
/*    */   private <T> Flux<DataBuffer> encodePart(byte[] boundary, Part part, DataBufferFactory bufferFactory) {
/* 80 */     HttpHeaders headers = new HttpHeaders((MultiValueMap)part.headers());
/*    */     
/* 82 */     String name = part.name();
/* 83 */     if (!headers.containsKey("Content-Disposition")) {
/* 84 */       headers.setContentDispositionFormData(name, (part instanceof FilePart) ? ((FilePart)part)
/* 85 */           .filename() : null);
/*    */     }
/*    */     
/* 88 */     return Flux.concat(new Publisher[] { (Publisher)
/* 89 */           generateBoundaryLine(boundary, bufferFactory), (Publisher)
/* 90 */           generatePartHeaders(headers, bufferFactory), (Publisher)part
/* 91 */           .content(), (Publisher)
/* 92 */           generateNewLine(bufferFactory) });
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/multipart/PartHttpMessageWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */