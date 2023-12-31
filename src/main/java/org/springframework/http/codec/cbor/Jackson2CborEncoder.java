/*    */ package org.springframework.http.codec.cbor;
/*    */ 
/*    */ import com.fasterxml.jackson.databind.ObjectMapper;
/*    */ import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
/*    */ import java.util.Map;
/*    */ import org.reactivestreams.Publisher;
/*    */ import org.springframework.core.ResolvableType;
/*    */ import org.springframework.core.io.buffer.DataBuffer;
/*    */ import org.springframework.core.io.buffer.DataBufferFactory;
/*    */ import org.springframework.http.MediaType;
/*    */ import org.springframework.http.codec.json.AbstractJackson2Encoder;
/*    */ import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
/*    */ import org.springframework.util.Assert;
/*    */ import org.springframework.util.MimeType;
/*    */ import reactor.core.publisher.Flux;
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
/*    */ 
/*    */ 
/*    */ public class Jackson2CborEncoder
/*    */   extends AbstractJackson2Encoder
/*    */ {
/*    */   public Jackson2CborEncoder() {
/* 47 */     this(Jackson2ObjectMapperBuilder.cbor().build(), new MimeType[] { (MimeType)MediaType.APPLICATION_CBOR });
/*    */   }
/*    */   
/*    */   public Jackson2CborEncoder(ObjectMapper mapper, MimeType... mimeTypes) {
/* 51 */     super(mapper, mimeTypes);
/* 52 */     Assert.isAssignable(CBORFactory.class, mapper.getFactory().getClass());
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Flux<DataBuffer> encode(Publisher<?> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, MimeType mimeType, Map<String, Object> hints) {
/* 58 */     throw new UnsupportedOperationException("Does not support stream encoding yet");
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/cbor/Jackson2CborEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */