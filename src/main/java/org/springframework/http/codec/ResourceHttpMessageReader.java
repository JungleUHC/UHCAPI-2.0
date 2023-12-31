/*    */ package org.springframework.http.codec;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.springframework.core.ResolvableType;
/*    */ import org.springframework.core.codec.Decoder;
/*    */ import org.springframework.core.codec.Hints;
/*    */ import org.springframework.core.codec.ResourceDecoder;
/*    */ import org.springframework.core.io.Resource;
/*    */ import org.springframework.http.ReactiveHttpInputMessage;
/*    */ import org.springframework.http.server.reactive.ServerHttpRequest;
/*    */ import org.springframework.http.server.reactive.ServerHttpResponse;
/*    */ import org.springframework.util.StringUtils;
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
/*    */ public class ResourceHttpMessageReader
/*    */   extends DecoderHttpMessageReader<Resource>
/*    */ {
/*    */   public ResourceHttpMessageReader() {
/* 41 */     super((Decoder<Resource>)new ResourceDecoder());
/*    */   }
/*    */   
/*    */   public ResourceHttpMessageReader(ResourceDecoder resourceDecoder) {
/* 45 */     super((Decoder<Resource>)resourceDecoder);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected Map<String, Object> getReadHints(ResolvableType elementType, ReactiveHttpInputMessage message) {
/* 51 */     String filename = message.getHeaders().getContentDisposition().getFilename();
/* 52 */     return StringUtils.hasText(filename) ? 
/* 53 */       Hints.from(ResourceDecoder.FILENAME_HINT, filename) : Hints.none();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected Map<String, Object> getReadHints(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response) {
/* 60 */     return getReadHints(elementType, (ReactiveHttpInputMessage)request);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/ResourceHttpMessageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */