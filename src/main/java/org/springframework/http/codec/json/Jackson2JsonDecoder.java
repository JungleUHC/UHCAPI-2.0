/*    */ package org.springframework.http.codec.json;
/*    */ 
/*    */ import com.fasterxml.jackson.databind.ObjectMapper;
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import java.util.Arrays;
/*    */ import java.util.Map;
/*    */ import org.reactivestreams.Publisher;
/*    */ import org.springframework.core.ResolvableType;
/*    */ import org.springframework.core.codec.StringDecoder;
/*    */ import org.springframework.core.io.buffer.DataBuffer;
/*    */ import org.springframework.core.io.buffer.DefaultDataBufferFactory;
/*    */ import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.util.MimeType;
/*    */ import org.springframework.util.MimeTypeUtils;
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
/*    */ public class Jackson2JsonDecoder
/*    */   extends AbstractJackson2Decoder
/*    */ {
/* 48 */   private static final StringDecoder STRING_DECODER = StringDecoder.textPlainOnly(Arrays.asList(new String[] { ",", "\n" }, ), false);
/*    */   
/* 50 */   private static final ResolvableType STRING_TYPE = ResolvableType.forClass(String.class);
/*    */ 
/*    */   
/*    */   public Jackson2JsonDecoder() {
/* 54 */     super(Jackson2ObjectMapperBuilder.json().build(), new MimeType[0]);
/*    */   }
/*    */   
/*    */   public Jackson2JsonDecoder(ObjectMapper mapper, MimeType... mimeTypes) {
/* 58 */     super(mapper, mimeTypes);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected Flux<DataBuffer> processInput(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
/* 65 */     Flux<DataBuffer> flux = Flux.from(input);
/* 66 */     if (mimeType == null) {
/* 67 */       return flux;
/*    */     }
/*    */ 
/*    */     
/* 71 */     Charset charset = mimeType.getCharset();
/* 72 */     if (charset == null || StandardCharsets.UTF_8.equals(charset) || StandardCharsets.US_ASCII.equals(charset)) {
/* 73 */       return flux;
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 79 */     MimeType textMimeType = new MimeType(MimeTypeUtils.TEXT_PLAIN, charset);
/* 80 */     Flux<String> decoded = STRING_DECODER.decode(input, STRING_TYPE, textMimeType, null);
/* 81 */     return decoded.map(s -> DefaultDataBufferFactory.sharedInstance.wrap(s.getBytes(StandardCharsets.UTF_8)));
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/json/Jackson2JsonDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */