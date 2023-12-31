/*     */ package org.springframework.http.codec.xml;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.Function;
/*     */ import javax.xml.bind.JAXBElement;
/*     */ import javax.xml.bind.JAXBException;
/*     */ import javax.xml.bind.UnmarshalException;
/*     */ import javax.xml.bind.Unmarshaller;
/*     */ import javax.xml.bind.annotation.XmlRootElement;
/*     */ import javax.xml.bind.annotation.XmlSchema;
/*     */ import javax.xml.bind.annotation.XmlType;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.stream.XMLEventReader;
/*     */ import javax.xml.stream.XMLInputFactory;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.events.XMLEvent;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.AbstractDecoder;
/*     */ import org.springframework.core.codec.CodecException;
/*     */ import org.springframework.core.codec.DecodingException;
/*     */ import org.springframework.core.codec.Hints;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.core.log.LogFormatUtils;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.MimeType;
/*     */ import org.springframework.util.MimeTypeUtils;
/*     */ import org.springframework.util.xml.StaxUtils;
/*     */ import reactor.core.Exceptions;
/*     */ import reactor.core.publisher.Flux;
/*     */ import reactor.core.publisher.Mono;
/*     */ import reactor.core.publisher.SynchronousSink;
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
/*     */ public class Jaxb2XmlDecoder
/*     */   extends AbstractDecoder<Object>
/*     */ {
/*     */   private static final String JAXB_DEFAULT_ANNOTATION_VALUE = "##default";
/*  83 */   private static final XMLInputFactory inputFactory = StaxUtils.createDefensiveInputFactory();
/*     */ 
/*     */   
/*  86 */   private final XmlEventDecoder xmlEventDecoder = new XmlEventDecoder();
/*     */   
/*  88 */   private final JaxbContextContainer jaxbContexts = new JaxbContextContainer();
/*     */   
/*  90 */   private Function<Unmarshaller, Unmarshaller> unmarshallerProcessor = Function.identity();
/*     */   
/*  92 */   private int maxInMemorySize = 262144;
/*     */ 
/*     */   
/*     */   public Jaxb2XmlDecoder() {
/*  96 */     super(new MimeType[] { MimeTypeUtils.APPLICATION_XML, MimeTypeUtils.TEXT_XML, (MimeType)new MediaType("application", "*+xml") });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jaxb2XmlDecoder(MimeType... supportedMimeTypes) {
/* 105 */     super(supportedMimeTypes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUnmarshallerProcessor(Function<Unmarshaller, Unmarshaller> processor) {
/* 115 */     this.unmarshallerProcessor = this.unmarshallerProcessor.andThen(processor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Function<Unmarshaller, Unmarshaller> getUnmarshallerProcessor() {
/* 123 */     return this.unmarshallerProcessor;
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
/*     */   
/*     */   public void setMaxInMemorySize(int byteCount) {
/* 136 */     this.maxInMemorySize = byteCount;
/* 137 */     this.xmlEventDecoder.setMaxInMemorySize(byteCount);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxInMemorySize() {
/* 145 */     return this.maxInMemorySize;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
/* 151 */     Class<?> outputClass = elementType.toClass();
/* 152 */     return ((outputClass.isAnnotationPresent((Class)XmlRootElement.class) || outputClass
/* 153 */       .isAnnotationPresent((Class)XmlType.class)) && super.canDecode(elementType, mimeType));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Flux<Object> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
/* 160 */     Flux<XMLEvent> xmlEventFlux = this.xmlEventDecoder.decode(inputStream, 
/* 161 */         ResolvableType.forClass(XMLEvent.class), mimeType, hints);
/*     */     
/* 163 */     Class<?> outputClass = elementType.toClass();
/* 164 */     QName typeName = toQName(outputClass);
/* 165 */     Flux<List<XMLEvent>> splitEvents = split(xmlEventFlux, typeName);
/*     */     
/* 167 */     return splitEvents.map(events -> {
/*     */           Object value = unmarshal(events, outputClass);
/*     */           LogFormatUtils.traceDebug(this.logger, ());
/*     */           return value;
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<Object> decodeToMono(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
/* 182 */     return DataBufferUtils.join(input, this.maxInMemorySize)
/* 183 */       .map(dataBuffer -> decode(dataBuffer, elementType, mimeType, hints));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object decode(DataBuffer dataBuffer, ResolvableType targetType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) throws DecodingException {
/*     */     try {
/* 192 */       Iterator eventReader = inputFactory.createXMLEventReader(dataBuffer.asInputStream());
/* 193 */       List<XMLEvent> events = new ArrayList<>();
/* 194 */       eventReader.forEachRemaining(event -> events.add((XMLEvent)event));
/* 195 */       return unmarshal(events, targetType.toClass());
/*     */     }
/* 197 */     catch (XMLStreamException ex) {
/* 198 */       throw new DecodingException(ex.getMessage(), ex);
/*     */     }
/* 200 */     catch (Throwable ex) {
/* 201 */       Throwable cause = ex.getCause();
/* 202 */       if (cause instanceof XMLStreamException) {
/* 203 */         throw new DecodingException(cause.getMessage(), cause);
/*     */       }
/*     */       
/* 206 */       throw Exceptions.propagate(ex);
/*     */     }
/*     */     finally {
/*     */       
/* 210 */       DataBufferUtils.release(dataBuffer);
/*     */     } 
/*     */   }
/*     */   
/*     */   private Object unmarshal(List<XMLEvent> events, Class<?> outputClass) {
/*     */     try {
/* 216 */       Unmarshaller unmarshaller = initUnmarshaller(outputClass);
/* 217 */       XMLEventReader eventReader = StaxUtils.createXMLEventReader(events);
/* 218 */       if (outputClass.isAnnotationPresent((Class)XmlRootElement.class)) {
/* 219 */         return unmarshaller.unmarshal(eventReader);
/*     */       }
/*     */       
/* 222 */       JAXBElement<?> jaxbElement = unmarshaller.unmarshal(eventReader, outputClass);
/* 223 */       return jaxbElement.getValue();
/*     */     
/*     */     }
/* 226 */     catch (UnmarshalException ex) {
/* 227 */       throw new DecodingException("Could not unmarshal XML to " + outputClass, ex);
/*     */     }
/* 229 */     catch (JAXBException ex) {
/* 230 */       throw new CodecException("Invalid JAXB configuration", ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private Unmarshaller initUnmarshaller(Class<?> outputClass) throws CodecException, JAXBException {
/* 235 */     Unmarshaller unmarshaller = this.jaxbContexts.createUnmarshaller(outputClass);
/* 236 */     return this.unmarshallerProcessor.apply(unmarshaller);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   QName toQName(Class<?> outputClass) {
/*     */     String localPart;
/*     */     String namespaceUri;
/* 247 */     if (outputClass.isAnnotationPresent((Class)XmlRootElement.class)) {
/* 248 */       XmlRootElement annotation = outputClass.<XmlRootElement>getAnnotation(XmlRootElement.class);
/* 249 */       localPart = annotation.name();
/* 250 */       namespaceUri = annotation.namespace();
/*     */     }
/* 252 */     else if (outputClass.isAnnotationPresent((Class)XmlType.class)) {
/* 253 */       XmlType annotation = outputClass.<XmlType>getAnnotation(XmlType.class);
/* 254 */       localPart = annotation.name();
/* 255 */       namespaceUri = annotation.namespace();
/*     */     } else {
/*     */       
/* 258 */       throw new IllegalArgumentException("Output class [" + outputClass.getName() + "] is neither annotated with @XmlRootElement nor @XmlType");
/*     */     } 
/*     */ 
/*     */     
/* 262 */     if ("##default".equals(localPart)) {
/* 263 */       localPart = ClassUtils.getShortNameAsProperty(outputClass);
/*     */     }
/* 265 */     if ("##default".equals(namespaceUri)) {
/* 266 */       Package outputClassPackage = outputClass.getPackage();
/* 267 */       if (outputClassPackage != null && outputClassPackage.isAnnotationPresent((Class)XmlSchema.class)) {
/* 268 */         XmlSchema annotation = outputClassPackage.<XmlSchema>getAnnotation(XmlSchema.class);
/* 269 */         namespaceUri = annotation.namespace();
/*     */       } else {
/*     */         
/* 272 */         namespaceUri = "";
/*     */       } 
/*     */     } 
/* 275 */     return new QName(namespaceUri, localPart);
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
/*     */   Flux<List<XMLEvent>> split(Flux<XMLEvent> xmlEventFlux, QName desiredName) {
/* 302 */     return xmlEventFlux.handle(new SplitHandler(desiredName));
/*     */   }
/*     */ 
/*     */   
/*     */   private static class SplitHandler
/*     */     implements BiConsumer<XMLEvent, SynchronousSink<List<XMLEvent>>>
/*     */   {
/*     */     private final QName desiredName;
/*     */     
/*     */     @Nullable
/*     */     private List<XMLEvent> events;
/* 313 */     private int elementDepth = 0;
/*     */     
/* 315 */     private int barrier = Integer.MAX_VALUE;
/*     */     
/*     */     public SplitHandler(QName desiredName) {
/* 318 */       this.desiredName = desiredName;
/*     */     }
/*     */ 
/*     */     
/*     */     public void accept(XMLEvent event, SynchronousSink<List<XMLEvent>> sink) {
/* 323 */       if (event.isStartElement()) {
/* 324 */         if (this.barrier == Integer.MAX_VALUE) {
/* 325 */           QName startElementName = event.asStartElement().getName();
/* 326 */           if (this.desiredName.equals(startElementName)) {
/* 327 */             this.events = new ArrayList<>();
/* 328 */             this.barrier = this.elementDepth;
/*     */           } 
/*     */         } 
/* 331 */         this.elementDepth++;
/*     */       } 
/* 333 */       if (this.elementDepth > this.barrier) {
/* 334 */         Assert.state((this.events != null), "No XMLEvent List");
/* 335 */         this.events.add(event);
/*     */       } 
/* 337 */       if (event.isEndElement()) {
/* 338 */         this.elementDepth--;
/* 339 */         if (this.elementDepth == this.barrier) {
/* 340 */           this.barrier = Integer.MAX_VALUE;
/* 341 */           Assert.state((this.events != null), "No XMLEvent List");
/* 342 */           sink.next(this.events);
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/xml/Jaxb2XmlDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */