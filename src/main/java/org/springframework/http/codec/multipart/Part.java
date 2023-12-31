/*    */ package org.springframework.http.codec.multipart;
/*    */ 
/*    */ import org.springframework.core.io.buffer.DataBuffer;
/*    */ import org.springframework.http.HttpHeaders;
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
/*    */ public interface Part
/*    */ {
/*    */   String name();
/*    */   
/*    */   HttpHeaders headers();
/*    */   
/*    */   Flux<DataBuffer> content();
/*    */   
/*    */   default Mono<Void> delete() {
/* 67 */     return Mono.empty();
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/multipart/Part.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */