/*    */ package org.springframework.http.codec.json;
/*    */ 
/*    */ import com.fasterxml.jackson.databind.ObjectMapper;
/*    */ import com.fasterxml.jackson.dataformat.smile.SmileFactory;
/*    */ import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
/*    */ import org.springframework.util.Assert;
/*    */ import org.springframework.util.MimeType;
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
/*    */ public class Jackson2SmileDecoder
/*    */   extends AbstractJackson2Decoder
/*    */ {
/* 37 */   private static final MimeType[] DEFAULT_SMILE_MIME_TYPES = new MimeType[] { new MimeType("application", "x-jackson-smile"), new MimeType("application", "*+x-jackson-smile") };
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Jackson2SmileDecoder() {
/* 43 */     this(Jackson2ObjectMapperBuilder.smile().build(), DEFAULT_SMILE_MIME_TYPES);
/*    */   }
/*    */   
/*    */   public Jackson2SmileDecoder(ObjectMapper mapper, MimeType... mimeTypes) {
/* 47 */     super(mapper, mimeTypes);
/* 48 */     Assert.isAssignable(SmileFactory.class, mapper.getFactory().getClass());
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/json/Jackson2SmileDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */