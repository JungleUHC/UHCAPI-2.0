/*     */ package org.springframework.http.codec.xml;
/*     */ 
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Map;
/*     */ import java.util.function.Function;
/*     */ import javax.xml.bind.JAXBException;
/*     */ import javax.xml.bind.MarshalException;
/*     */ import javax.xml.bind.Marshaller;
/*     */ import javax.xml.bind.annotation.XmlRootElement;
/*     */ import javax.xml.bind.annotation.XmlType;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.AbstractSingleValueEncoder;
/*     */ import org.springframework.core.codec.CodecException;
/*     */ import org.springframework.core.codec.EncodingException;
/*     */ import org.springframework.core.codec.Hints;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.core.log.LogFormatUtils;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.MimeType;
/*     */ import org.springframework.util.MimeTypeUtils;
/*     */ import reactor.core.publisher.Flux;
/*     */ import reactor.core.publisher.Mono;
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
/*     */ public class Jaxb2XmlEncoder
/*     */   extends AbstractSingleValueEncoder<Object>
/*     */ {
/*  62 */   private final JaxbContextContainer jaxbContexts = new JaxbContextContainer();
/*     */   
/*  64 */   private Function<Marshaller, Marshaller> marshallerProcessor = Function.identity();
/*     */ 
/*     */   
/*     */   public Jaxb2XmlEncoder() {
/*  68 */     super(new MimeType[] { MimeTypeUtils.APPLICATION_XML, MimeTypeUtils.TEXT_XML, (MimeType)new MediaType("application", "*+xml") });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMarshallerProcessor(Function<Marshaller, Marshaller> processor) {
/*  78 */     this.marshallerProcessor = this.marshallerProcessor.andThen(processor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Function<Marshaller, Marshaller> getMarshallerProcessor() {
/*  86 */     return this.marshallerProcessor;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
/*  92 */     if (super.canEncode(elementType, mimeType)) {
/*  93 */       Class<?> outputClass = elementType.toClass();
/*  94 */       return (outputClass.isAnnotationPresent((Class)XmlRootElement.class) || outputClass
/*  95 */         .isAnnotationPresent((Class)XmlType.class));
/*     */     } 
/*     */     
/*  98 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Flux<DataBuffer> encode(Object value, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
/* 107 */     return Mono.fromCallable(() -> encodeValue(value, bufferFactory, valueType, mimeType, hints)).flux();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DataBuffer encodeValue(Object value, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
/* 114 */     if (!Hints.isLoggingSuppressed(hints)) {
/* 115 */       LogFormatUtils.traceDebug(this.logger, traceOn -> {
/*     */             String formatted = LogFormatUtils.formatValue(value, !traceOn.booleanValue());
/*     */             
/*     */             return Hints.getLogPrefix(hints) + "Encoding [" + formatted + "]";
/*     */           });
/*     */     }
/* 121 */     boolean release = true;
/* 122 */     DataBuffer buffer = bufferFactory.allocateBuffer(1024);
/*     */     try {
/* 124 */       OutputStream outputStream = buffer.asOutputStream();
/* 125 */       Class<?> clazz = ClassUtils.getUserClass(value);
/* 126 */       Marshaller marshaller = initMarshaller(clazz);
/* 127 */       marshaller.marshal(value, outputStream);
/* 128 */       release = false;
/* 129 */       return buffer;
/*     */     }
/* 131 */     catch (MarshalException ex) {
/* 132 */       throw new EncodingException("Could not marshal " + value.getClass() + " to XML", ex);
/*     */     }
/* 134 */     catch (JAXBException ex) {
/* 135 */       throw new CodecException("Invalid JAXB configuration", ex);
/*     */     } finally {
/*     */       
/* 138 */       if (release) {
/* 139 */         DataBufferUtils.release(buffer);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private Marshaller initMarshaller(Class<?> clazz) throws CodecException, JAXBException {
/* 145 */     Marshaller marshaller = this.jaxbContexts.createMarshaller(clazz);
/* 146 */     marshaller.setProperty("jaxb.encoding", StandardCharsets.UTF_8.name());
/* 147 */     marshaller = this.marshallerProcessor.apply(marshaller);
/* 148 */     return marshaller;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/xml/Jaxb2XmlEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */