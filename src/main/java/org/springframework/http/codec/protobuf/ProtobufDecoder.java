/*     */ package org.springframework.http.codec.protobuf;
/*     */ 
/*     */ import com.google.protobuf.CodedInputStream;
/*     */ import com.google.protobuf.ExtensionRegistry;
/*     */ import com.google.protobuf.ExtensionRegistryLite;
/*     */ import com.google.protobuf.Message;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.function.Function;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.Decoder;
/*     */ import org.springframework.core.codec.DecodingException;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferLimitException;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ConcurrentReferenceHashMap;
/*     */ import org.springframework.util.MimeType;
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
/*     */ public class ProtobufDecoder
/*     */   extends ProtobufCodecSupport
/*     */   implements Decoder<Message>
/*     */ {
/*     */   protected static final int DEFAULT_MESSAGE_MAX_SIZE = 262144;
/*  79 */   private static final ConcurrentMap<Class<?>, Method> methodCache = (ConcurrentMap<Class<?>, Method>)new ConcurrentReferenceHashMap();
/*     */ 
/*     */   
/*     */   private final ExtensionRegistry extensionRegistry;
/*     */   
/*  84 */   private int maxMessageSize = 262144;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ProtobufDecoder() {
/*  91 */     this(ExtensionRegistry.newInstance());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ProtobufDecoder(ExtensionRegistry extensionRegistry) {
/* 100 */     Assert.notNull(extensionRegistry, "ExtensionRegistry must not be null");
/* 101 */     this.extensionRegistry = extensionRegistry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxMessageSize(int maxMessageSize) {
/* 111 */     this.maxMessageSize = maxMessageSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxMessageSize() {
/* 119 */     return this.maxMessageSize;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
/* 125 */     return (Message.class.isAssignableFrom(elementType.toClass()) && supportsMimeType(mimeType));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Flux<Message> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
/* 132 */     MessageDecoderFunction decoderFunction = new MessageDecoderFunction(elementType, this.maxMessageSize);
/*     */ 
/*     */     
/* 135 */     return Flux.from(inputStream)
/* 136 */       .flatMapIterable(decoderFunction)
/* 137 */       .doOnTerminate(decoderFunction::discard);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<Message> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
/* 144 */     return DataBufferUtils.join(inputStream, this.maxMessageSize)
/* 145 */       .map(dataBuffer -> decode(dataBuffer, elementType, mimeType, hints));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Message decode(DataBuffer dataBuffer, ResolvableType targetType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) throws DecodingException {
/*     */     try {
/* 153 */       Message.Builder builder = getMessageBuilder(targetType.toClass());
/* 154 */       ByteBuffer buffer = dataBuffer.asByteBuffer();
/* 155 */       builder.mergeFrom(CodedInputStream.newInstance(buffer), (ExtensionRegistryLite)this.extensionRegistry);
/* 156 */       return builder.build();
/*     */     }
/* 158 */     catch (IOException ex) {
/* 159 */       throw new DecodingException("I/O error while parsing input stream", ex);
/*     */     }
/* 161 */     catch (Exception ex) {
/* 162 */       throw new DecodingException("Could not read Protobuf message: " + ex.getMessage(), ex);
/*     */     } finally {
/*     */       
/* 165 */       DataBufferUtils.release(dataBuffer);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Message.Builder getMessageBuilder(Class<?> clazz) throws Exception {
/* 175 */     Method method = methodCache.get(clazz);
/* 176 */     if (method == null) {
/* 177 */       method = clazz.getMethod("newBuilder", new Class[0]);
/* 178 */       methodCache.put(clazz, method);
/*     */     } 
/* 180 */     return (Message.Builder)method.invoke(clazz, new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<MimeType> getDecodableMimeTypes() {
/* 185 */     return getMimeTypes();
/*     */   }
/*     */ 
/*     */   
/*     */   private class MessageDecoderFunction
/*     */     implements Function<DataBuffer, Iterable<? extends Message>>
/*     */   {
/*     */     private final ResolvableType elementType;
/*     */     
/*     */     private final int maxMessageSize;
/*     */     
/*     */     @Nullable
/*     */     private DataBuffer output;
/*     */     
/*     */     private int messageBytesToRead;
/*     */     
/*     */     private int offset;
/*     */     
/*     */     public MessageDecoderFunction(ResolvableType elementType, int maxMessageSize) {
/* 204 */       this.elementType = elementType;
/* 205 */       this.maxMessageSize = maxMessageSize;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Iterable<? extends Message> apply(DataBuffer input) {
/*     */       try {
/* 212 */         List<Message> messages = new ArrayList<>();
/*     */ 
/*     */ 
/*     */         
/*     */         while (true) {
/* 217 */           if (this.output == null) {
/* 218 */             if (!readMessageSize(input)) {
/* 219 */               return messages;
/*     */             }
/* 221 */             if (this.maxMessageSize > 0 && this.messageBytesToRead > this.maxMessageSize) {
/* 222 */               throw new DataBufferLimitException("The number of bytes to read for message (" + this.messageBytesToRead + ") exceeds the configured limit (" + this.maxMessageSize + ")");
/*     */             }
/*     */ 
/*     */ 
/*     */             
/* 227 */             this.output = input.factory().allocateBuffer(this.messageBytesToRead);
/*     */           } 
/*     */           
/* 230 */           int chunkBytesToRead = Math.min(this.messageBytesToRead, input.readableByteCount());
/* 231 */           int remainingBytesToRead = input.readableByteCount() - chunkBytesToRead;
/*     */           
/* 233 */           byte[] bytesToWrite = new byte[chunkBytesToRead];
/* 234 */           input.read(bytesToWrite, 0, chunkBytesToRead);
/* 235 */           this.output.write(bytesToWrite);
/* 236 */           this.messageBytesToRead -= chunkBytesToRead;
/*     */           
/* 238 */           if (this.messageBytesToRead == 0) {
/* 239 */             CodedInputStream stream = CodedInputStream.newInstance(this.output.asByteBuffer());
/* 240 */             DataBufferUtils.release(this.output);
/* 241 */             this.output = null;
/*     */ 
/*     */             
/* 244 */             Message message = ProtobufDecoder.getMessageBuilder(this.elementType.toClass()).mergeFrom(stream, (ExtensionRegistryLite)ProtobufDecoder.this.extensionRegistry).build();
/* 245 */             messages.add(message);
/*     */           } 
/* 247 */           if (remainingBytesToRead <= 0)
/* 248 */             return messages; 
/*     */         } 
/* 250 */       } catch (DecodingException ex) {
/* 251 */         throw ex;
/*     */       }
/* 253 */       catch (IOException ex) {
/* 254 */         throw new DecodingException("I/O error while parsing input stream", ex);
/*     */       }
/* 256 */       catch (Exception ex) {
/* 257 */         throw new DecodingException("Could not read Protobuf message: " + ex.getMessage(), ex);
/*     */       } finally {
/*     */         
/* 260 */         DataBufferUtils.release(input);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private boolean readMessageSize(DataBuffer input) {
/* 273 */       if (this.offset == 0) {
/* 274 */         if (input.readableByteCount() == 0) {
/* 275 */           return false;
/*     */         }
/* 277 */         int firstByte = input.read();
/* 278 */         if ((firstByte & 0x80) == 0) {
/* 279 */           this.messageBytesToRead = firstByte;
/* 280 */           return true;
/*     */         } 
/* 282 */         this.messageBytesToRead = firstByte & 0x7F;
/* 283 */         this.offset = 7;
/*     */       } 
/*     */       
/* 286 */       if (this.offset < 32) {
/* 287 */         for (; this.offset < 32; this.offset += 7) {
/* 288 */           if (input.readableByteCount() == 0) {
/* 289 */             return false;
/*     */           }
/* 291 */           int b = input.read();
/* 292 */           this.messageBytesToRead |= (b & 0x7F) << this.offset;
/* 293 */           if ((b & 0x80) == 0) {
/* 294 */             this.offset = 0;
/* 295 */             return true;
/*     */           } 
/*     */         } 
/*     */       }
/*     */       
/* 300 */       for (; this.offset < 64; this.offset += 7) {
/* 301 */         if (input.readableByteCount() == 0) {
/* 302 */           return false;
/*     */         }
/* 304 */         int b = input.read();
/* 305 */         if ((b & 0x80) == 0) {
/* 306 */           this.offset = 0;
/* 307 */           return true;
/*     */         } 
/*     */       } 
/* 310 */       this.offset = 0;
/* 311 */       throw new DecodingException("Cannot parse message size: malformed varint");
/*     */     }
/*     */     
/*     */     public void discard() {
/* 315 */       if (this.output != null)
/* 316 */         DataBufferUtils.release(this.output); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/protobuf/ProtobufDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */