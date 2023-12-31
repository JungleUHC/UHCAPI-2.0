/*    */ package org.springframework.http.converter.xml;
/*    */ 
/*    */ import com.fasterxml.jackson.databind.ObjectMapper;
/*    */ import com.fasterxml.jackson.dataformat.xml.XmlMapper;
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import org.springframework.http.MediaType;
/*    */ import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
/*    */ import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
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
/*    */ public class MappingJackson2XmlHttpMessageConverter
/*    */   extends AbstractJackson2HttpMessageConverter
/*    */ {
/*    */   public MappingJackson2XmlHttpMessageConverter() {
/* 52 */     this(Jackson2ObjectMapperBuilder.xml().build());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MappingJackson2XmlHttpMessageConverter(ObjectMapper objectMapper) {
/* 62 */     super(objectMapper, new MediaType[] { new MediaType("application", "xml", StandardCharsets.UTF_8), new MediaType("text", "xml", StandardCharsets.UTF_8), new MediaType("application", "*+xml", StandardCharsets.UTF_8) });
/*    */ 
/*    */     
/* 65 */     Assert.isInstanceOf(XmlMapper.class, objectMapper, "XmlMapper required");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setObjectMapper(ObjectMapper objectMapper) {
/* 75 */     Assert.isInstanceOf(XmlMapper.class, objectMapper, "XmlMapper required");
/* 76 */     super.setObjectMapper(objectMapper);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/xml/MappingJackson2XmlHttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */