/*     */ package org.springframework.http.converter.xml;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.StringReader;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.stream.XMLInputFactory;
/*     */ import javax.xml.stream.XMLResolver;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import javax.xml.transform.Result;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.TransformerException;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.sax.SAXSource;
/*     */ import javax.xml.transform.stax.StAXSource;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import org.springframework.http.HttpInputMessage;
/*     */ import org.springframework.http.HttpOutputMessage;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.converter.AbstractHttpMessageConverter;
/*     */ import org.springframework.http.converter.HttpMessageNotReadableException;
/*     */ import org.springframework.http.converter.HttpMessageNotWritableException;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.StreamUtils;
/*     */ import org.w3c.dom.Document;
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
/*     */ public class SourceHttpMessageConverter<T extends Source>
/*     */   extends AbstractHttpMessageConverter<T>
/*     */ {
/*     */   private static final EntityResolver NO_OP_ENTITY_RESOLVER = (publicId, systemId) -> new InputSource(new StringReader(""));
/*     */   private static final XMLResolver NO_OP_XML_RESOLVER = (publicID, systemID, base, ns) -> StreamUtils.emptyInput();
/*  76 */   private static final Set<Class<?>> SUPPORTED_CLASSES = new HashSet<>(8);
/*     */   
/*     */   static {
/*  79 */     SUPPORTED_CLASSES.add(DOMSource.class);
/*  80 */     SUPPORTED_CLASSES.add(SAXSource.class);
/*  81 */     SUPPORTED_CLASSES.add(StAXSource.class);
/*  82 */     SUPPORTED_CLASSES.add(StreamSource.class);
/*  83 */     SUPPORTED_CLASSES.add(Source.class);
/*     */   }
/*     */ 
/*     */   
/*  87 */   private final TransformerFactory transformerFactory = TransformerFactory.newInstance();
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean supportDtd = false;
/*     */ 
/*     */   
/*     */   private boolean processExternalEntities = false;
/*     */ 
/*     */ 
/*     */   
/*     */   public SourceHttpMessageConverter() {
/*  99 */     super(new MediaType[] { MediaType.APPLICATION_XML, MediaType.TEXT_XML, new MediaType("application", "*+xml") });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSupportDtd(boolean supportDtd) {
/* 108 */     this.supportDtd = supportDtd;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSupportDtd() {
/* 115 */     return this.supportDtd;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProcessExternalEntities(boolean processExternalEntities) {
/* 125 */     this.processExternalEntities = processExternalEntities;
/* 126 */     if (processExternalEntities) {
/* 127 */       this.supportDtd = true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isProcessExternalEntities() {
/* 135 */     return this.processExternalEntities;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean supports(Class<?> clazz) {
/* 141 */     return SUPPORTED_CLASSES.contains(clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
/* 149 */     InputStream body = StreamUtils.nonClosing(inputMessage.getBody());
/* 150 */     if (DOMSource.class == clazz) {
/* 151 */       return (T)readDOMSource(body, inputMessage);
/*     */     }
/* 153 */     if (SAXSource.class == clazz) {
/* 154 */       return (T)readSAXSource(body, inputMessage);
/*     */     }
/* 156 */     if (StAXSource.class == clazz) {
/* 157 */       return (T)readStAXSource(body, inputMessage);
/*     */     }
/* 159 */     if (StreamSource.class == clazz || Source.class == clazz) {
/* 160 */       return (T)readStreamSource(body);
/*     */     }
/*     */     
/* 163 */     throw new HttpMessageNotReadableException("Could not read class [" + clazz + "]. Only DOMSource, SAXSource, StAXSource, and StreamSource are supported.", inputMessage);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private DOMSource readDOMSource(InputStream body, HttpInputMessage inputMessage) throws IOException {
/*     */     try {
/* 170 */       DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
/* 171 */       documentBuilderFactory.setNamespaceAware(true);
/* 172 */       documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", 
/* 173 */           !isSupportDtd());
/* 174 */       documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", 
/* 175 */           isProcessExternalEntities());
/* 176 */       DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
/* 177 */       if (!isProcessExternalEntities()) {
/* 178 */         documentBuilder.setEntityResolver(NO_OP_ENTITY_RESOLVER);
/*     */       }
/* 180 */       Document document = documentBuilder.parse(body);
/* 181 */       return new DOMSource(document);
/*     */     }
/* 183 */     catch (NullPointerException ex) {
/* 184 */       if (!isSupportDtd()) {
/* 185 */         throw new HttpMessageNotReadableException("NPE while unmarshalling: This can happen due to the presence of DTD declarations which are disabled.", ex, inputMessage);
/*     */       }
/*     */       
/* 188 */       throw ex;
/*     */     }
/* 190 */     catch (ParserConfigurationException ex) {
/* 191 */       throw new HttpMessageNotReadableException("Could not set feature: " + ex
/* 192 */           .getMessage(), ex, inputMessage);
/*     */     }
/* 194 */     catch (SAXException ex) {
/* 195 */       throw new HttpMessageNotReadableException("Could not parse document: " + ex
/* 196 */           .getMessage(), ex, inputMessage);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private SAXSource readSAXSource(InputStream body, HttpInputMessage inputMessage) throws IOException {
/*     */     try {
/* 203 */       XMLReader xmlReader = XMLReaderFactory.createXMLReader();
/* 204 */       xmlReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", !isSupportDtd());
/* 205 */       xmlReader.setFeature("http://xml.org/sax/features/external-general-entities", isProcessExternalEntities());
/* 206 */       if (!isProcessExternalEntities()) {
/* 207 */         xmlReader.setEntityResolver(NO_OP_ENTITY_RESOLVER);
/*     */       }
/* 209 */       byte[] bytes = StreamUtils.copyToByteArray(body);
/* 210 */       return new SAXSource(xmlReader, new InputSource(new ByteArrayInputStream(bytes)));
/*     */     }
/* 212 */     catch (SAXException ex) {
/* 213 */       throw new HttpMessageNotReadableException("Could not parse document: " + ex
/* 214 */           .getMessage(), ex, inputMessage);
/*     */     } 
/*     */   }
/*     */   
/*     */   private Source readStAXSource(InputStream body, HttpInputMessage inputMessage) {
/*     */     try {
/* 220 */       XMLInputFactory inputFactory = XMLInputFactory.newInstance();
/* 221 */       inputFactory.setProperty("javax.xml.stream.supportDTD", Boolean.valueOf(isSupportDtd()));
/* 222 */       inputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.valueOf(isProcessExternalEntities()));
/* 223 */       if (!isProcessExternalEntities()) {
/* 224 */         inputFactory.setXMLResolver(NO_OP_XML_RESOLVER);
/*     */       }
/* 226 */       XMLStreamReader streamReader = inputFactory.createXMLStreamReader(body);
/* 227 */       return new StAXSource(streamReader);
/*     */     }
/* 229 */     catch (XMLStreamException ex) {
/* 230 */       throw new HttpMessageNotReadableException("Could not parse document: " + ex
/* 231 */           .getMessage(), ex, inputMessage);
/*     */     } 
/*     */   }
/*     */   
/*     */   private StreamSource readStreamSource(InputStream body) throws IOException {
/* 236 */     byte[] bytes = StreamUtils.copyToByteArray(body);
/* 237 */     return new StreamSource(new ByteArrayInputStream(bytes));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected Long getContentLength(T t, @Nullable MediaType contentType) {
/* 243 */     if (t instanceof DOMSource) {
/*     */       try {
/* 245 */         CountingOutputStream os = new CountingOutputStream();
/* 246 */         transform((Source)t, new StreamResult(os));
/* 247 */         return Long.valueOf(os.count);
/*     */       }
/* 249 */       catch (TransformerException transformerException) {}
/*     */     }
/*     */ 
/*     */     
/* 253 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeInternal(T t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
/*     */     try {
/* 260 */       Result result = new StreamResult(outputMessage.getBody());
/* 261 */       transform((Source)t, result);
/*     */     }
/* 263 */     catch (TransformerException ex) {
/* 264 */       throw new HttpMessageNotWritableException("Could not transform [" + t + "] to output message", ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void transform(Source source, Result result) throws TransformerException {
/* 269 */     this.transformerFactory.newTransformer().transform(source, result);
/*     */   }
/*     */   
/*     */   private static class CountingOutputStream
/*     */     extends OutputStream
/*     */   {
/* 275 */     long count = 0L;
/*     */ 
/*     */     
/*     */     public void write(int b) throws IOException {
/* 279 */       this.count++;
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(byte[] b) throws IOException {
/* 284 */       this.count += b.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(byte[] b, int off, int len) throws IOException {
/* 289 */       this.count += len;
/*     */     }
/*     */     
/*     */     private CountingOutputStream() {}
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/xml/SourceHttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */