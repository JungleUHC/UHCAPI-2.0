/*    */ package org.springframework.http.codec.json;
/*    */ 
/*    */ import com.fasterxml.jackson.databind.ObjectMapper;
/*    */ import com.fasterxml.jackson.dataformat.smile.SmileFactory;
/*    */ import java.util.Collections;
/*    */ import org.springframework.http.MediaType;
/*    */ import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
/*    */ import org.springframework.lang.Nullable;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Jackson2SmileEncoder
/*    */   extends AbstractJackson2Encoder
/*    */ {
/* 43 */   private static final MimeType[] DEFAULT_SMILE_MIME_TYPES = new MimeType[] { new MimeType("application", "x-jackson-smile"), new MimeType("application", "*+x-jackson-smile") };
/*    */ 
/*    */ 
/*    */   
/* 47 */   private static final byte[] STREAM_SEPARATOR = new byte[0];
/*    */ 
/*    */   
/*    */   public Jackson2SmileEncoder() {
/* 51 */     this(Jackson2ObjectMapperBuilder.smile().build(), DEFAULT_SMILE_MIME_TYPES);
/*    */   }
/*    */   
/*    */   public Jackson2SmileEncoder(ObjectMapper mapper, MimeType... mimeTypes) {
/* 55 */     super(mapper, mimeTypes);
/* 56 */     Assert.isAssignable(SmileFactory.class, mapper.getFactory().getClass());
/* 57 */     setStreamingMediaTypes(Collections.singletonList(new MediaType("application", "stream+x-jackson-smile")));
/*    */   }
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
/*    */   @Nullable
/*    */   protected byte[] getStreamingMediaTypeSeparator(@Nullable MimeType mimeType) {
/* 71 */     for (MediaType streamingMediaType : getStreamingMediaTypes()) {
/* 72 */       if (streamingMediaType.isCompatibleWith(mimeType)) {
/* 73 */         return STREAM_SEPARATOR;
/*    */       }
/*    */     } 
/* 76 */     return null;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/json/Jackson2SmileEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */