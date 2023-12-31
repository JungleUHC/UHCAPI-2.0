/*     */ package org.springframework.http.codec.json;
/*     */ 
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import kotlinx.serialization.DeserializationStrategy;
/*     */ import kotlinx.serialization.KSerializer;
/*     */ import kotlinx.serialization.SerializersKt;
/*     */ import kotlinx.serialization.descriptors.PolymorphicKind;
/*     */ import kotlinx.serialization.descriptors.SerialDescriptor;
/*     */ import kotlinx.serialization.json.Json;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.AbstractDecoder;
/*     */ import org.springframework.core.codec.StringDecoder;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.http.MediaType;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class KotlinSerializationJsonDecoder
/*     */   extends AbstractDecoder<Object>
/*     */ {
/*  61 */   private static final Map<Type, KSerializer<Object>> serializerCache = (Map<Type, KSerializer<Object>>)new ConcurrentReferenceHashMap();
/*     */ 
/*     */   
/*     */   private final Json json;
/*     */   
/*  66 */   private final StringDecoder stringDecoder = StringDecoder.allMimeTypes(StringDecoder.DEFAULT_DELIMITERS, false);
/*     */ 
/*     */   
/*     */   public KotlinSerializationJsonDecoder() {
/*  70 */     this((Json)Json.Default);
/*     */   }
/*     */   
/*     */   public KotlinSerializationJsonDecoder(Json json) {
/*  74 */     super(new MimeType[] { (MimeType)MediaType.APPLICATION_JSON, (MimeType)new MediaType("application", "*+json") });
/*  75 */     this.json = json;
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
/*     */   public void setMaxInMemorySize(int byteCount) {
/*  90 */     this.stringDecoder.setMaxInMemorySize(byteCount);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxInMemorySize() {
/*  97 */     return this.stringDecoder.getMaxInMemorySize();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
/*     */     try {
/* 104 */       serializer(elementType.getType());
/* 105 */       return (super.canDecode(elementType, mimeType) && !CharSequence.class.isAssignableFrom(elementType.toClass()));
/*     */     }
/* 107 */     catch (Exception ex) {
/* 108 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Flux<Object> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
/* 116 */     return Flux.error(new UnsupportedOperationException());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<Object> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
/* 123 */     return this.stringDecoder
/* 124 */       .decodeToMono(inputStream, elementType, mimeType, hints)
/* 125 */       .map(jsonText -> this.json.decodeFromString((DeserializationStrategy)serializer(elementType.getType()), jsonText));
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
/* 138 */     KSerializer<Object> serializer = serializerCache.get(type);
/* 139 */     if (serializer == null) {
/* 140 */       serializer = SerializersKt.serializer(type);
/* 141 */       if (hasPolymorphism(serializer.getDescriptor(), new HashSet<>())) {
/* 142 */         throw new UnsupportedOperationException("Open polymorphic serialization is not supported yet");
/*     */       }
/* 144 */       serializerCache.put(type, serializer);
/*     */     } 
/* 146 */     return serializer;
/*     */   }
/*     */   
/*     */   private boolean hasPolymorphism(SerialDescriptor descriptor, Set<String> alreadyProcessed) {
/* 150 */     alreadyProcessed.add(descriptor.getSerialName());
/* 151 */     if (descriptor.getKind().equals(PolymorphicKind.OPEN.INSTANCE)) {
/* 152 */       return true;
/*     */     }
/* 154 */     for (int i = 0; i < descriptor.getElementsCount(); i++) {
/* 155 */       SerialDescriptor elementDescriptor = descriptor.getElementDescriptor(i);
/* 156 */       if (!alreadyProcessed.contains(elementDescriptor.getSerialName()) && hasPolymorphism(elementDescriptor, alreadyProcessed)) {
/* 157 */         return true;
/*     */       }
/*     */     } 
/* 160 */     return false;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/json/KotlinSerializationJsonDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */