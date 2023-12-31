/*    */ package org.springframework.core.convert.support;
/*    */ 
/*    */ import java.nio.charset.Charset;
/*    */ import org.springframework.core.convert.converter.Converter;
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
/*    */ class StringToCharsetConverter
/*    */   implements Converter<String, Charset>
/*    */ {
/*    */   public Charset convert(String source) {
/* 33 */     return Charset.forName(source);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/convert/support/StringToCharsetConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */