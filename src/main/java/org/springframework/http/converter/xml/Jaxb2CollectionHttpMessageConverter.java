/*     */ package org.springframework.http.converter.xml;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ import javax.xml.bind.JAXBException;
/*     */ import javax.xml.bind.UnmarshalException;
/*     */ import javax.xml.bind.Unmarshaller;
/*     */ import javax.xml.bind.annotation.XmlRootElement;
/*     */ import javax.xml.bind.annotation.XmlType;
/*     */ import javax.xml.stream.XMLInputFactory;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import javax.xml.transform.Result;
/*     */ import javax.xml.transform.Source;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpInputMessage;
/*     */ import org.springframework.http.HttpOutputMessage;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.converter.GenericHttpMessageConverter;
/*     */ import org.springframework.http.converter.HttpMessageConversionException;
/*     */ import org.springframework.http.converter.HttpMessageNotReadableException;
/*     */ import org.springframework.http.converter.HttpMessageNotWritableException;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ReflectionUtils;
/*     */ import org.springframework.util.xml.StaxUtils;
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
/*     */ public class Jaxb2CollectionHttpMessageConverter<T extends Collection>
/*     */   extends AbstractJaxb2HttpMessageConverter<T>
/*     */   implements GenericHttpMessageConverter<T>
/*     */ {
/*  68 */   private final XMLInputFactory inputFactory = createXmlInputFactory();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
/*  77 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
/*  88 */     if (!(type instanceof ParameterizedType)) {
/*  89 */       return false;
/*     */     }
/*  91 */     ParameterizedType parameterizedType = (ParameterizedType)type;
/*  92 */     if (!(parameterizedType.getRawType() instanceof Class)) {
/*  93 */       return false;
/*     */     }
/*  95 */     Class<?> rawType = (Class)parameterizedType.getRawType();
/*  96 */     if (!Collection.class.isAssignableFrom(rawType)) {
/*  97 */       return false;
/*     */     }
/*  99 */     if ((parameterizedType.getActualTypeArguments()).length != 1) {
/* 100 */       return false;
/*     */     }
/* 102 */     Type typeArgument = parameterizedType.getActualTypeArguments()[0];
/* 103 */     if (!(typeArgument instanceof Class)) {
/* 104 */       return false;
/*     */     }
/* 106 */     Class<?> typeArgumentClass = (Class)typeArgument;
/* 107 */     return ((typeArgumentClass.isAnnotationPresent((Class)XmlRootElement.class) || typeArgumentClass
/* 108 */       .isAnnotationPresent((Class)XmlType.class)) && canRead(mediaType));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
/* 117 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canWrite(@Nullable Type type, @Nullable Class<?> clazz, @Nullable MediaType mediaType) {
/* 126 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean supports(Class<?> clazz) {
/* 132 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected T readFromSource(Class<? extends T> clazz, HttpHeaders headers, Source source) throws Exception {
/* 138 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
/* 146 */     ParameterizedType parameterizedType = (ParameterizedType)type;
/* 147 */     T result = createCollection((Class)parameterizedType.getRawType());
/* 148 */     Class<?> elementClass = (Class)parameterizedType.getActualTypeArguments()[0];
/*     */     
/*     */     try {
/* 151 */       Unmarshaller unmarshaller = createUnmarshaller(elementClass);
/* 152 */       XMLStreamReader streamReader = this.inputFactory.createXMLStreamReader(inputMessage.getBody());
/* 153 */       int event = moveToFirstChildOfRootElement(streamReader);
/*     */       
/* 155 */       while (event != 8) {
/* 156 */         if (elementClass.isAnnotationPresent((Class)XmlRootElement.class)) {
/* 157 */           result.add(unmarshaller.unmarshal(streamReader));
/*     */         }
/* 159 */         else if (elementClass.isAnnotationPresent((Class)XmlType.class)) {
/* 160 */           result.add(unmarshaller.unmarshal(streamReader, elementClass).getValue());
/*     */         }
/*     */         else {
/*     */           
/* 164 */           throw new HttpMessageNotReadableException("Cannot unmarshal to [" + elementClass + "]", inputMessage);
/*     */         } 
/*     */         
/* 167 */         event = moveToNextElement(streamReader);
/*     */       } 
/* 169 */       return result;
/*     */     }
/* 171 */     catch (XMLStreamException ex) {
/* 172 */       throw new HttpMessageNotReadableException("Failed to read XML stream: " + ex
/* 173 */           .getMessage(), ex, inputMessage);
/*     */     }
/* 175 */     catch (UnmarshalException ex) {
/* 176 */       throw new HttpMessageNotReadableException("Could not unmarshal to [" + elementClass + "]: " + ex
/* 177 */           .getMessage(), ex, inputMessage);
/*     */     }
/* 179 */     catch (JAXBException ex) {
/* 180 */       throw new HttpMessageConversionException("Invalid JAXB setup: " + ex.getMessage(), ex);
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
/*     */   protected T createCollection(Class<?> collectionClass) {
/* 192 */     if (!collectionClass.isInterface()) {
/*     */       try {
/* 194 */         return (T)ReflectionUtils.accessibleConstructor(collectionClass, new Class[0]).newInstance(new Object[0]);
/*     */       }
/* 196 */       catch (Throwable ex) {
/* 197 */         throw new IllegalArgumentException("Could not instantiate collection class: " + collectionClass
/* 198 */             .getName(), ex);
/*     */       } 
/*     */     }
/* 201 */     if (List.class == collectionClass) {
/* 202 */       return (T)new ArrayList();
/*     */     }
/* 204 */     if (SortedSet.class == collectionClass) {
/* 205 */       return (T)new TreeSet();
/*     */     }
/*     */     
/* 208 */     return (T)new LinkedHashSet();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private int moveToFirstChildOfRootElement(XMLStreamReader streamReader) throws XMLStreamException {
/* 214 */     int event = streamReader.next();
/* 215 */     while (event != 1) {
/* 216 */       event = streamReader.next();
/*     */     }
/*     */ 
/*     */     
/* 220 */     event = streamReader.next();
/* 221 */     while (event != 1 && event != 8) {
/* 222 */       event = streamReader.next();
/*     */     }
/* 224 */     return event;
/*     */   }
/*     */   
/*     */   private int moveToNextElement(XMLStreamReader streamReader) throws XMLStreamException {
/* 228 */     int event = streamReader.getEventType();
/* 229 */     while (event != 1 && event != 8) {
/* 230 */       event = streamReader.next();
/*     */     }
/* 232 */     return event;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(T t, @Nullable Type type, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
/* 239 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeToResult(T t, HttpHeaders headers, Result result) throws Exception {
/* 244 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected XMLInputFactory createXmlInputFactory() {
/* 256 */     return StaxUtils.createDefensiveInputFactory();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/xml/Jaxb2CollectionHttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */