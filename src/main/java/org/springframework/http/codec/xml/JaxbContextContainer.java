/*    */ package org.springframework.http.codec.xml;
/*    */ 
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import java.util.concurrent.ConcurrentMap;
/*    */ import javax.xml.bind.JAXBContext;
/*    */ import javax.xml.bind.JAXBException;
/*    */ import javax.xml.bind.Marshaller;
/*    */ import javax.xml.bind.Unmarshaller;
/*    */ import org.springframework.core.codec.CodecException;
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
/*    */ final class JaxbContextContainer
/*    */ {
/* 38 */   private final ConcurrentMap<Class<?>, JAXBContext> jaxbContexts = new ConcurrentHashMap<>(64);
/*    */ 
/*    */   
/*    */   public Marshaller createMarshaller(Class<?> clazz) throws CodecException, JAXBException {
/* 42 */     JAXBContext jaxbContext = getJaxbContext(clazz);
/* 43 */     return jaxbContext.createMarshaller();
/*    */   }
/*    */   
/*    */   public Unmarshaller createUnmarshaller(Class<?> clazz) throws CodecException, JAXBException {
/* 47 */     JAXBContext jaxbContext = getJaxbContext(clazz);
/* 48 */     return jaxbContext.createUnmarshaller();
/*    */   }
/*    */   
/*    */   private JAXBContext getJaxbContext(Class<?> clazz) throws CodecException {
/* 52 */     return this.jaxbContexts.computeIfAbsent(clazz, key -> {
/*    */           
/*    */           try {
/*    */             return JAXBContext.newInstance(new Class[] { clazz });
/* 56 */           } catch (JAXBException ex) {
/*    */             throw new CodecException("Could not create JAXBContext for class [" + clazz + "]: " + ex.getMessage(), ex);
/*    */           } 
/*    */         });
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/xml/JaxbContextContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */