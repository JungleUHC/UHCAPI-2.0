/*    */ package org.springframework.web.accept;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import org.springframework.http.MediaType;
/*    */ import org.springframework.util.Assert;
/*    */ import org.springframework.web.context.request.NativeWebRequest;
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
/*    */ public class FixedContentNegotiationStrategy
/*    */   implements ContentNegotiationStrategy
/*    */ {
/*    */   private final List<MediaType> contentTypes;
/*    */   
/*    */   public FixedContentNegotiationStrategy(MediaType contentType) {
/* 41 */     this(Collections.singletonList(contentType));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FixedContentNegotiationStrategy(List<MediaType> contentTypes) {
/* 52 */     Assert.notNull(contentTypes, "'contentTypes' must not be null");
/* 53 */     this.contentTypes = Collections.unmodifiableList(contentTypes);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<MediaType> getContentTypes() {
/* 61 */     return this.contentTypes;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public List<MediaType> resolveMediaTypes(NativeWebRequest request) {
/* 67 */     return this.contentTypes;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/accept/FixedContentNegotiationStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */