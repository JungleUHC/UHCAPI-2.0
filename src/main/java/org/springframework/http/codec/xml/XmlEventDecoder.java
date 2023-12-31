/*     */ package org.springframework.http.codec.xml;
/*     */ 
/*     */ import com.fasterxml.aalto.AsyncByteBufferFeeder;
/*     */ import com.fasterxml.aalto.AsyncXMLInputFactory;
/*     */ import com.fasterxml.aalto.AsyncXMLStreamReader;
/*     */ import com.fasterxml.aalto.evt.EventAllocatorImpl;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Supplier;
/*     */ import javax.xml.stream.XMLInputFactory;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import javax.xml.stream.events.XMLEvent;
/*     */ import javax.xml.stream.util.XMLEventAllocator;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.AbstractDecoder;
/*     */ import org.springframework.core.codec.DecodingException;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferLimitException;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.MimeType;
/*     */ import org.springframework.util.MimeTypeUtils;
/*     */ import org.springframework.util.xml.StaxUtils;
/*     */ import reactor.core.publisher.Flux;
/*     */ import reactor.core.publisher.SignalType;
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
/*     */ public class XmlEventDecoder
/*     */   extends AbstractDecoder<XMLEvent>
/*     */ {
/*  87 */   private static final XMLInputFactory inputFactory = StaxUtils.createDefensiveInputFactory();
/*     */   
/*  89 */   private static final boolean aaltoPresent = ClassUtils.isPresent("com.fasterxml.aalto.AsyncXMLStreamReader", XmlEventDecoder.class
/*  90 */       .getClassLoader());
/*     */   
/*  92 */   boolean useAalto = aaltoPresent;
/*     */   
/*  94 */   private int maxInMemorySize = 262144;
/*     */ 
/*     */   
/*     */   public XmlEventDecoder() {
/*  98 */     super(new MimeType[] { MimeTypeUtils.APPLICATION_XML, MimeTypeUtils.TEXT_XML, (MimeType)new MediaType("application", "*+xml") });
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
/*     */   public void setMaxInMemorySize(int byteCount) {
/* 112 */     this.maxInMemorySize = byteCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxInMemorySize() {
/* 120 */     return this.maxInMemorySize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Flux<XMLEvent> decode(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
/* 129 */     if (this.useAalto) {
/* 130 */       AaltoDataBufferToXmlEvent mapper = new AaltoDataBufferToXmlEvent(this.maxInMemorySize);
/* 131 */       return Flux.from(input)
/* 132 */         .flatMapIterable(mapper)
/* 133 */         .doFinally(signalType -> mapper.endOfInput());
/*     */     } 
/*     */     
/* 136 */     return DataBufferUtils.join(input, this.maxInMemorySize)
/* 137 */       .flatMapIterable(buffer -> {
/*     */           try {
/*     */             InputStream is = buffer.asInputStream();
/*     */             
/*     */             Iterator eventReader = inputFactory.createXMLEventReader(is);
/*     */             List<XMLEvent> result = new ArrayList<>();
/*     */             eventReader.forEachRemaining(());
/*     */             return result;
/* 145 */           } catch (XMLStreamException ex) {
/*     */             throw new DecodingException(ex.getMessage(), ex);
/*     */           } finally {
/*     */             DataBufferUtils.release(buffer);
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class AaltoDataBufferToXmlEvent
/*     */     implements Function<DataBuffer, List<? extends XMLEvent>>
/*     */   {
/* 162 */     private static final AsyncXMLInputFactory inputFactory = (AsyncXMLInputFactory)StaxUtils.createDefensiveInputFactory(com.fasterxml.aalto.stax.InputFactoryImpl::new);
/*     */     
/* 164 */     private final AsyncXMLStreamReader<AsyncByteBufferFeeder> streamReader = inputFactory
/* 165 */       .createAsyncForByteBuffer();
/*     */     
/* 167 */     private final XMLEventAllocator eventAllocator = (XMLEventAllocator)EventAllocatorImpl.getDefaultInstance();
/*     */     
/*     */     private final int maxInMemorySize;
/*     */     
/*     */     private int byteCount;
/*     */     
/*     */     private int elementDepth;
/*     */ 
/*     */     
/*     */     public AaltoDataBufferToXmlEvent(int maxInMemorySize) {
/* 177 */       this.maxInMemorySize = maxInMemorySize;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public List<? extends XMLEvent> apply(DataBuffer dataBuffer) {
/*     */       try {
/* 184 */         increaseByteCount(dataBuffer);
/* 185 */         ((AsyncByteBufferFeeder)this.streamReader.getInputFeeder()).feedInput(dataBuffer.asByteBuffer());
/* 186 */         List<XMLEvent> events = new ArrayList<>();
/*     */         
/* 188 */         while (this.streamReader.next() != 257) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 193 */           XMLEvent event = this.eventAllocator.allocate((XMLStreamReader)this.streamReader);
/* 194 */           events.add(event);
/* 195 */           if (event.isEndDocument()) {
/*     */             break;
/*     */           }
/* 198 */           checkDepthAndResetByteCount(event);
/*     */         } 
/*     */         
/* 201 */         if (this.maxInMemorySize > 0 && this.byteCount > this.maxInMemorySize) {
/* 202 */           raiseLimitException();
/*     */         }
/* 204 */         return events;
/*     */       }
/* 206 */       catch (XMLStreamException ex) {
/* 207 */         throw new DecodingException(ex.getMessage(), ex);
/*     */       } finally {
/*     */         
/* 210 */         DataBufferUtils.release(dataBuffer);
/*     */       } 
/*     */     }
/*     */     
/*     */     private void increaseByteCount(DataBuffer dataBuffer) {
/* 215 */       if (this.maxInMemorySize > 0) {
/* 216 */         if (dataBuffer.readableByteCount() > Integer.MAX_VALUE - this.byteCount) {
/* 217 */           raiseLimitException();
/*     */         } else {
/*     */           
/* 220 */           this.byteCount += dataBuffer.readableByteCount();
/*     */         } 
/*     */       }
/*     */     }
/*     */     
/*     */     private void checkDepthAndResetByteCount(XMLEvent event) {
/* 226 */       if (this.maxInMemorySize > 0) {
/* 227 */         if (event.isStartElement()) {
/* 228 */           this.byteCount = (this.elementDepth == 1) ? 0 : this.byteCount;
/* 229 */           this.elementDepth++;
/*     */         }
/* 231 */         else if (event.isEndElement()) {
/* 232 */           this.elementDepth--;
/* 233 */           this.byteCount = (this.elementDepth == 1) ? 0 : this.byteCount;
/*     */         } 
/*     */       }
/*     */     }
/*     */     
/*     */     private void raiseLimitException() {
/* 239 */       throw new DataBufferLimitException("Exceeded limit on max bytes per XML top-level node: " + this.maxInMemorySize);
/*     */     }
/*     */ 
/*     */     
/*     */     public void endOfInput() {
/* 244 */       ((AsyncByteBufferFeeder)this.streamReader.getInputFeeder()).endOfInput();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/xml/XmlEventDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */