/*    */ package org.springframework.web.multipart;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import org.springframework.core.io.AbstractResource;
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.util.Assert;
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
/*    */ class MultipartFileResource
/*    */   extends AbstractResource
/*    */ {
/*    */   private final MultipartFile multipartFile;
/*    */   
/*    */   public MultipartFileResource(MultipartFile multipartFile) {
/* 41 */     Assert.notNull(multipartFile, "MultipartFile must not be null");
/* 42 */     this.multipartFile = multipartFile;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean exists() {
/* 51 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isOpen() {
/* 59 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public long contentLength() {
/* 64 */     return this.multipartFile.getSize();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getFilename() {
/* 69 */     return this.multipartFile.getOriginalFilename();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public InputStream getInputStream() throws IOException, IllegalStateException {
/* 78 */     return this.multipartFile.getInputStream();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getDescription() {
/* 86 */     return "MultipartFile resource [" + this.multipartFile.getName() + "]";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 92 */     return (this == other || (other instanceof MultipartFileResource && ((MultipartFileResource)other).multipartFile
/* 93 */       .equals(this.multipartFile)));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 98 */     return this.multipartFile.hashCode();
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/multipart/MultipartFileResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */