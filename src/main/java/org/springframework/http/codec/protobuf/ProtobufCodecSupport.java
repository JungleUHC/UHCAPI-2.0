/*    */ package org.springframework.http.codec.protobuf;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import org.springframework.lang.Nullable;
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
/*    */ public abstract class ProtobufCodecSupport
/*    */ {
/* 34 */   static final List<MimeType> MIME_TYPES = Collections.unmodifiableList(
/* 35 */       Arrays.asList(new MimeType[] { new MimeType("application", "x-protobuf"), new MimeType("application", "octet-stream"), new MimeType("application", "vnd.google.protobuf") }));
/*    */ 
/*    */   
/*    */   static final String DELIMITED_KEY = "delimited";
/*    */ 
/*    */   
/*    */   static final String DELIMITED_VALUE = "true";
/*    */ 
/*    */ 
/*    */   
/*    */   protected boolean supportsMimeType(@Nullable MimeType mimeType) {
/* 46 */     return (mimeType == null || MIME_TYPES.stream().anyMatch(m -> m.isCompatibleWith(mimeType)));
/*    */   }
/*    */   
/*    */   protected List<MimeType> getMimeTypes() {
/* 50 */     return MIME_TYPES;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/protobuf/ProtobufCodecSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */