/*     */ package org.springframework.http.converter.xml;
/*     */ 
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import javax.xml.bind.JAXBContext;
/*     */ import javax.xml.bind.JAXBException;
/*     */ import javax.xml.bind.Marshaller;
/*     */ import javax.xml.bind.Unmarshaller;
/*     */ import org.springframework.http.converter.HttpMessageConversionException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractJaxb2HttpMessageConverter<T>
/*     */   extends AbstractXmlHttpMessageConverter<T>
/*     */ {
/*  40 */   private final ConcurrentMap<Class<?>, JAXBContext> jaxbContexts = new ConcurrentHashMap<>(64);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Marshaller createMarshaller(Class<?> clazz) {
/*     */     try {
/*  51 */       JAXBContext jaxbContext = getJaxbContext(clazz);
/*  52 */       Marshaller marshaller = jaxbContext.createMarshaller();
/*  53 */       customizeMarshaller(marshaller);
/*  54 */       return marshaller;
/*     */     }
/*  56 */     catch (JAXBException ex) {
/*  57 */       throw new HttpMessageConversionException("Could not create Marshaller for class [" + clazz + "]: " + ex
/*  58 */           .getMessage(), ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void customizeMarshaller(Marshaller marshaller) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Unmarshaller createUnmarshaller(Class<?> clazz) {
/*     */     try {
/*  80 */       JAXBContext jaxbContext = getJaxbContext(clazz);
/*  81 */       Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
/*  82 */       customizeUnmarshaller(unmarshaller);
/*  83 */       return unmarshaller;
/*     */     }
/*  85 */     catch (JAXBException ex) {
/*  86 */       throw new HttpMessageConversionException("Could not create Unmarshaller for class [" + clazz + "]: " + ex
/*  87 */           .getMessage(), ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void customizeUnmarshaller(Unmarshaller unmarshaller) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JAXBContext getJaxbContext(Class<?> clazz) {
/* 108 */     return this.jaxbContexts.computeIfAbsent(clazz, key -> {
/*     */           
/*     */           try {
/*     */             return JAXBContext.newInstance(new Class[] { clazz });
/* 112 */           } catch (JAXBException ex) {
/*     */             throw new HttpMessageConversionException("Could not create JAXBContext for class [" + clazz + "]: " + ex.getMessage(), ex);
/*     */           } 
/*     */         });
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/xml/AbstractJaxb2HttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */