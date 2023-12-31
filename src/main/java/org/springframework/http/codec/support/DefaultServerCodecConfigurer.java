/*    */ package org.springframework.http.codec.support;
/*    */ 
/*    */ import java.util.List;
/*    */ import org.springframework.http.codec.CodecConfigurer;
/*    */ import org.springframework.http.codec.ServerCodecConfigurer;
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
/*    */ public class DefaultServerCodecConfigurer
/*    */   extends BaseCodecConfigurer
/*    */   implements ServerCodecConfigurer
/*    */ {
/*    */   public DefaultServerCodecConfigurer() {
/* 31 */     super(new ServerDefaultCodecsImpl());
/*    */   }
/*    */   
/*    */   private DefaultServerCodecConfigurer(BaseCodecConfigurer other) {
/* 35 */     super(other);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public ServerCodecConfigurer.ServerDefaultCodecs defaultCodecs() {
/* 41 */     return (ServerCodecConfigurer.ServerDefaultCodecs)super.defaultCodecs();
/*    */   }
/*    */ 
/*    */   
/*    */   public DefaultServerCodecConfigurer clone() {
/* 46 */     return new DefaultServerCodecConfigurer(this);
/*    */   }
/*    */ 
/*    */   
/*    */   protected BaseDefaultCodecs cloneDefaultCodecs() {
/* 51 */     return new ServerDefaultCodecsImpl((ServerDefaultCodecsImpl)defaultCodecs());
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/support/DefaultServerCodecConfigurer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */