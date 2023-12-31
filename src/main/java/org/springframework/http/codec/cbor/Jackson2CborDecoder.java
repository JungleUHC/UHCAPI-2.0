/*    */ package org.springframework.http.codec.cbor;
/*    */ 
/*    */ import com.fasterxml.jackson.databind.ObjectMapper;
/*    */ import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
/*    */ import java.util.Map;
/*    */ import org.reactivestreams.Publisher;
/*    */ import org.springframework.core.ResolvableType;
/*    */ import org.springframework.core.io.buffer.DataBuffer;
/*    */ import org.springframework.http.MediaType;
/*    */ import org.springframework.http.codec.json.AbstractJackson2Decoder;
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
/*    */ public class Jackson2CborDecoder
/*    */   extends AbstractJackson2Decoder
/*    */ {
/*    */   public Jackson2CborDecoder() {
/* 46 */     this(Jackson2ObjectMapperBuilder.cbor().build(), new MimeType[] { (MimeType)MediaType.APPLICATION_CBOR });
/*    */   }
/*    */   
/*    */   public Jackson2CborDecoder(ObjectMapper mapper, MimeType... mimeTypes) {
/* 50 */     super(mapper, mimeTypes);
/* 51 */     Assert.isAssignable(CBORFactory.class, mapper.getFactory().getClass());
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Flux<Object> decode(Publisher<DataBuffer> input, ResolvableType elementType, MimeType mimeType, Map<String, Object> hints) {
/* 57 */     throw new UnsupportedOperationException("Does not support stream decoding yet");
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/cbor/Jackson2CborDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */