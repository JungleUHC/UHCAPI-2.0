/*     */ package org.springframework.http.converter.xml;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import javax.xml.bind.JAXBElement;
/*     */ import javax.xml.bind.JAXBException;
/*     */ import javax.xml.bind.MarshalException;
/*     */ import javax.xml.bind.Marshaller;
/*     */ import javax.xml.bind.PropertyException;
/*     */ import javax.xml.bind.UnmarshalException;
/*     */ import javax.xml.bind.Unmarshaller;
/*     */ import javax.xml.bind.annotation.XmlRootElement;
/*     */ import javax.xml.bind.annotation.XmlType;
/*     */ import javax.xml.transform.Result;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.sax.SAXSource;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import org.springframework.core.annotation.AnnotationUtils;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.converter.HttpMessageConversionException;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.xml.sax.EntityResolver;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.XMLReader;
/*     */ import org.xml.sax.helpers.XMLReaderFactory;
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
/*     */ public class Jaxb2RootElementHttpMessageConverter
/*     */   extends AbstractJaxb2HttpMessageConverter<Object>
/*     */ {
/*     */   private boolean supportDtd = false;
/*     */   private boolean processExternalEntities = false;
/*     */   
/*     */   public void setSupportDtd(boolean supportDtd) {
/*  76 */     this.supportDtd = supportDtd;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSupportDtd() {
/*  83 */     return this.supportDtd;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProcessExternalEntities(boolean processExternalEntities) {
/*  93 */     this.processExternalEntities = processExternalEntities;
/*  94 */     if (processExternalEntities) {
/*  95 */       this.supportDtd = true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isProcessExternalEntities() {
/* 103 */     return this.processExternalEntities;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
/* 109 */     return ((clazz.isAnnotationPresent((Class)XmlRootElement.class) || clazz.isAnnotationPresent((Class)XmlType.class)) && 
/* 110 */       canRead(mediaType));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
/* 115 */     return (AnnotationUtils.findAnnotation(clazz, XmlRootElement.class) != null && canWrite(mediaType));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean supports(Class<?> clazz) {
/* 121 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object readFromSource(Class<?> clazz, HttpHeaders headers, Source source) throws Exception {
/*     */     try {
/* 127 */       source = processSource(source);
/* 128 */       Unmarshaller unmarshaller = createUnmarshaller(clazz);
/* 129 */       if (clazz.isAnnotationPresent((Class)XmlRootElement.class)) {
/* 130 */         return unmarshaller.unmarshal(source);
/*     */       }
/*     */       
/* 133 */       JAXBElement<?> jaxbElement = unmarshaller.unmarshal(source, clazz);
/* 134 */       return jaxbElement.getValue();
/*     */     
/*     */     }
/* 137 */     catch (NullPointerException ex) {
/* 138 */       if (!isSupportDtd()) {
/* 139 */         throw new IllegalStateException("NPE while unmarshalling. This can happen due to the presence of DTD declarations which are disabled.", ex);
/*     */       }
/*     */       
/* 142 */       throw ex;
/*     */     }
/* 144 */     catch (UnmarshalException ex) {
/* 145 */       throw ex;
/*     */     }
/* 147 */     catch (JAXBException ex) {
/* 148 */       throw new HttpMessageConversionException("Invalid JAXB setup: " + ex.getMessage(), ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected Source processSource(Source source) {
/* 154 */     if (source instanceof StreamSource) {
/* 155 */       StreamSource streamSource = (StreamSource)source;
/* 156 */       InputSource inputSource = new InputSource(streamSource.getInputStream());
/*     */       try {
/* 158 */         XMLReader xmlReader = XMLReaderFactory.createXMLReader();
/* 159 */         xmlReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", !isSupportDtd());
/* 160 */         String featureName = "http://xml.org/sax/features/external-general-entities";
/* 161 */         xmlReader.setFeature(featureName, isProcessExternalEntities());
/* 162 */         if (!isProcessExternalEntities()) {
/* 163 */           xmlReader.setEntityResolver(NO_OP_ENTITY_RESOLVER);
/*     */         }
/* 165 */         return new SAXSource(xmlReader, inputSource);
/*     */       }
/* 167 */       catch (SAXException ex) {
/* 168 */         this.logger.warn("Processing of external entities could not be disabled", ex);
/* 169 */         return source;
/*     */       } 
/*     */     } 
/*     */     
/* 173 */     return source;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeToResult(Object o, HttpHeaders headers, Result result) throws Exception {
/*     */     try {
/* 180 */       Class<?> clazz = ClassUtils.getUserClass(o);
/* 181 */       Marshaller marshaller = createMarshaller(clazz);
/* 182 */       setCharset(headers.getContentType(), marshaller);
/* 183 */       marshaller.marshal(o, result);
/*     */     }
/* 185 */     catch (MarshalException ex) {
/* 186 */       throw ex;
/*     */     }
/* 188 */     catch (JAXBException ex) {
/* 189 */       throw new HttpMessageConversionException("Invalid JAXB setup: " + ex.getMessage(), ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void setCharset(@Nullable MediaType contentType, Marshaller marshaller) throws PropertyException {
/* 194 */     if (contentType != null && contentType.getCharset() != null)
/* 195 */       marshaller.setProperty("jaxb.encoding", contentType.getCharset().name()); 
/*     */   }
/*     */   
/*     */   private static final EntityResolver NO_OP_ENTITY_RESOLVER = (publicId, systemId) -> new InputSource(new StringReader(""));
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/xml/Jaxb2RootElementHttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */