/*     */ package org.springframework.http.codec.json;
/*     */ 
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import kotlinx.serialization.KSerializer;
/*     */ import kotlinx.serialization.SerializationStrategy;
/*     */ import kotlinx.serialization.SerializersKt;
/*     */ import kotlinx.serialization.descriptors.PolymorphicKind;
/*     */ import kotlinx.serialization.descriptors.SerialDescriptor;
/*     */ import kotlinx.serialization.json.Json;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.AbstractEncoder;
/*     */ import org.springframework.core.codec.CharSequenceEncoder;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.codec.ServerSentEvent;
/*     */ import org.springframework.lang.Nullable;
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
/*     */ public class KotlinSerializationJsonEncoder
/*     */   extends AbstractEncoder<Object>
/*     */ {
/*  60 */   private static final Map<Type, KSerializer<Object>> serializerCache = (Map<Type, KSerializer<Object>>)new ConcurrentReferenceHashMap();
/*     */ 
/*     */   
/*     */   private final Json json;
/*     */   
/*  65 */   private final CharSequenceEncoder charSequenceEncoder = CharSequenceEncoder.allMimeTypes();
/*     */ 
/*     */   
/*     */   public KotlinSerializationJsonEncoder() {
/*  69 */     this((Json)Json.Default);
/*     */   }
/*     */   
/*     */   public KotlinSerializationJsonEncoder(Json json) {
/*  73 */     super(new MimeType[] { (MimeType)MediaType.APPLICATION_JSON, (MimeType)new MediaType("application", "*+json") });
/*  74 */     this.json = json;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
/*     */     try {
/*  81 */       serializer(elementType.getType());
/*  82 */       return (super.canEncode(elementType, mimeType) && !String.class.isAssignableFrom(elementType.toClass()) && 
/*  83 */         !ServerSentEvent.class.isAssignableFrom(elementType.toClass()));
/*     */     }
/*  85 */     catch (Exception ex) {
/*  86 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Flux<DataBuffer> encode(Publisher<?> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
/*  94 */     if (inputStream instanceof Mono) {
/*  95 */       return Mono.from(inputStream)
/*  96 */         .map(value -> encodeValue(value, bufferFactory, elementType, mimeType, hints))
/*  97 */         .flux();
/*     */     }
/*     */     
/* 100 */     ResolvableType listType = ResolvableType.forClassWithGenerics(List.class, new ResolvableType[] { elementType });
/* 101 */     return Flux.from(inputStream)
/* 102 */       .collectList()
/* 103 */       .map(list -> encodeValue(list, bufferFactory, listType, mimeType, hints))
/* 104 */       .flux();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DataBuffer encodeValue(Object value, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
/* 112 */     String json = this.json.encodeToString((SerializationStrategy)serializer(valueType.getType()), value);
/* 113 */     return this.charSequenceEncoder.encodeValue(json, bufferFactory, valueType, mimeType, null);
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
/*     */   private KSerializer<Object> serializer(Type type) {
/* 126 */     KSerializer<Object> serializer = serializerCache.get(type);
/* 127 */     if (serializer == null) {
/* 128 */       serializer = SerializersKt.serializer(type);
/* 129 */       if (hasPolymorphism(serializer.getDescriptor(), new HashSet<>())) {
/* 130 */         throw new UnsupportedOperationException("Open polymorphic serialization is not supported yet");
/*     */       }
/* 132 */       serializerCache.put(type, serializer);
/*     */     } 
/* 134 */     return serializer;
/*     */   }
/*     */   
/*     */   private boolean hasPolymorphism(SerialDescriptor descriptor, Set<String> alreadyProcessed) {
/* 138 */     alreadyProcessed.add(descriptor.getSerialName());
/* 139 */     if (descriptor.getKind().equals(PolymorphicKind.OPEN.INSTANCE)) {
/* 140 */       return true;
/*     */     }
/* 142 */     for (int i = 0; i < descriptor.getElementsCount(); i++) {
/* 143 */       SerialDescriptor elementDescriptor = descriptor.getElementDescriptor(i);
/* 144 */       if (!alreadyProcessed.contains(elementDescriptor.getSerialName()) && hasPolymorphism(elementDescriptor, alreadyProcessed)) {
/* 145 */         return true;
/*     */       }
/*     */     } 
/* 148 */     return false;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/json/KotlinSerializationJsonEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */