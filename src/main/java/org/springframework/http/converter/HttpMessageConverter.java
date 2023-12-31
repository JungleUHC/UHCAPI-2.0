/*    */ package org.springframework.http.converter;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import org.springframework.http.HttpInputMessage;
/*    */ import org.springframework.http.HttpOutputMessage;
/*    */ import org.springframework.http.MediaType;
/*    */ import org.springframework.lang.Nullable;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface HttpMessageConverter<T>
/*    */ {
/*    */   boolean canRead(Class<?> paramClass, @Nullable MediaType paramMediaType);
/*    */   
/*    */   boolean canWrite(Class<?> paramClass, @Nullable MediaType paramMediaType);
/*    */   
/*    */   List<MediaType> getSupportedMediaTypes();
/*    */   
/*    */   default List<MediaType> getSupportedMediaTypes(Class<?> clazz) {
/* 78 */     return (canRead(clazz, null) || canWrite(clazz, null)) ? 
/* 79 */       getSupportedMediaTypes() : Collections.<MediaType>emptyList();
/*    */   }
/*    */   
/*    */   T read(Class<? extends T> paramClass, HttpInputMessage paramHttpInputMessage) throws IOException, HttpMessageNotReadableException;
/*    */   
/*    */   void write(T paramT, @Nullable MediaType paramMediaType, HttpOutputMessage paramHttpOutputMessage) throws IOException, HttpMessageNotWritableException;
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/HttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */