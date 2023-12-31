/*    */ package org.springframework.http.codec.support;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.springframework.http.codec.ClientCodecConfigurer;
/*    */ import org.springframework.http.codec.CodecConfigurer;
/*    */ import org.springframework.http.codec.HttpMessageWriter;
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
/*    */ public class DefaultClientCodecConfigurer
/*    */   extends BaseCodecConfigurer
/*    */   implements ClientCodecConfigurer
/*    */ {
/*    */   public DefaultClientCodecConfigurer() {
/* 35 */     super(new ClientDefaultCodecsImpl());
/* 36 */     ((ClientDefaultCodecsImpl)defaultCodecs()).setPartWritersSupplier(this::getPartWriters);
/*    */   }
/*    */   
/*    */   private DefaultClientCodecConfigurer(DefaultClientCodecConfigurer other) {
/* 40 */     super(other);
/* 41 */     ((ClientDefaultCodecsImpl)defaultCodecs()).setPartWritersSupplier(this::getPartWriters);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public ClientCodecConfigurer.ClientDefaultCodecs defaultCodecs() {
/* 47 */     return (ClientCodecConfigurer.ClientDefaultCodecs)super.defaultCodecs();
/*    */   }
/*    */ 
/*    */   
/*    */   public DefaultClientCodecConfigurer clone() {
/* 52 */     return new DefaultClientCodecConfigurer(this);
/*    */   }
/*    */ 
/*    */   
/*    */   protected BaseDefaultCodecs cloneDefaultCodecs() {
/* 57 */     return new ClientDefaultCodecsImpl((ClientDefaultCodecsImpl)defaultCodecs());
/*    */   }
/*    */   
/*    */   private List<HttpMessageWriter<?>> getPartWriters() {
/* 61 */     List<HttpMessageWriter<?>> result = new ArrayList<>();
/* 62 */     result.addAll(this.customCodecs.getTypedWriters().keySet());
/* 63 */     result.addAll(this.defaultCodecs.getBaseTypedWriters());
/* 64 */     result.addAll(this.customCodecs.getObjectWriters().keySet());
/* 65 */     result.addAll(this.defaultCodecs.getBaseObjectWriters());
/* 66 */     result.addAll(this.defaultCodecs.getCatchAllWriters());
/* 67 */     return result;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/support/DefaultClientCodecConfigurer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */