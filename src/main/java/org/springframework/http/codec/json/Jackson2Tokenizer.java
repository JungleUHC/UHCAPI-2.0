/*     */ package org.springframework.http.codec.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonFactory;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonProcessingException;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.async.ByteArrayFeeder;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.ObjectMapper;
/*     */ import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
/*     */ import com.fasterxml.jackson.databind.util.TokenBuffer;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.codec.DecodingException;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferLimitException;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import reactor.core.Exceptions;
/*     */ import reactor.core.publisher.Flux;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class Jackson2Tokenizer
/*     */ {
/*     */   private final JsonParser parser;
/*     */   private final DeserializationContext deserializationContext;
/*     */   private final boolean tokenizeArrayElements;
/*     */   private final boolean forceUseOfBigDecimal;
/*     */   private final int maxInMemorySize;
/*     */   private int objectDepth;
/*     */   private int arrayDepth;
/*     */   private int byteCount;
/*     */   private TokenBuffer tokenBuffer;
/*     */   private final ByteArrayFeeder inputFeeder;
/*     */   
/*     */   private Jackson2Tokenizer(JsonParser parser, DeserializationContext deserializationContext, boolean tokenizeArrayElements, boolean forceUseOfBigDecimal, int maxInMemorySize) {
/*  80 */     this.parser = parser;
/*  81 */     this.deserializationContext = deserializationContext;
/*  82 */     this.tokenizeArrayElements = tokenizeArrayElements;
/*  83 */     this.forceUseOfBigDecimal = forceUseOfBigDecimal;
/*  84 */     this.inputFeeder = (ByteArrayFeeder)this.parser.getNonBlockingInputFeeder();
/*  85 */     this.maxInMemorySize = maxInMemorySize;
/*  86 */     this.tokenBuffer = createToken();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private List<TokenBuffer> tokenize(DataBuffer dataBuffer) {
/*  92 */     int bufferSize = dataBuffer.readableByteCount();
/*  93 */     byte[] bytes = new byte[bufferSize];
/*  94 */     dataBuffer.read(bytes);
/*  95 */     DataBufferUtils.release(dataBuffer);
/*     */     
/*     */     try {
/*  98 */       this.inputFeeder.feedInput(bytes, 0, bytes.length);
/*  99 */       List<TokenBuffer> result = parseTokenBufferFlux();
/* 100 */       assertInMemorySize(bufferSize, result);
/* 101 */       return result;
/*     */     }
/* 103 */     catch (JsonProcessingException ex) {
/* 104 */       throw new DecodingException("JSON decoding error: " + ex.getOriginalMessage(), ex);
/*     */     }
/* 106 */     catch (IOException ex) {
/* 107 */       throw Exceptions.propagate(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private Flux<TokenBuffer> endOfInput() {
/* 112 */     return Flux.defer(() -> {
/*     */           this.inputFeeder.endOfInput();
/*     */           
/*     */           try {
/*     */             return (Publisher)Flux.fromIterable(parseTokenBufferFlux());
/* 117 */           } catch (JsonProcessingException ex) {
/*     */             
/*     */             throw new DecodingException("JSON decoding error: " + ex.getOriginalMessage(), ex);
/* 120 */           } catch (IOException ex) {
/*     */             throw Exceptions.propagate(ex);
/*     */           } 
/*     */         });
/*     */   }
/*     */   
/*     */   private List<TokenBuffer> parseTokenBufferFlux() throws IOException {
/* 127 */     List<TokenBuffer> result = new ArrayList<>();
/*     */ 
/*     */     
/* 130 */     boolean previousNull = false;
/* 131 */     while (!this.parser.isClosed()) {
/* 132 */       JsonToken token = this.parser.nextToken();
/* 133 */       if (token == JsonToken.NOT_AVAILABLE || (token == null && previousNull)) {
/*     */         break;
/*     */       }
/*     */       
/* 137 */       if (token == null) {
/* 138 */         previousNull = true;
/*     */         
/*     */         continue;
/*     */       } 
/* 142 */       previousNull = false;
/*     */       
/* 144 */       updateDepth(token);
/* 145 */       if (!this.tokenizeArrayElements) {
/* 146 */         processTokenNormal(token, result);
/*     */         continue;
/*     */       } 
/* 149 */       processTokenArray(token, result);
/*     */     } 
/*     */     
/* 152 */     return result;
/*     */   }
/*     */   
/*     */   private void updateDepth(JsonToken token) {
/* 156 */     switch (token) {
/*     */       case START_OBJECT:
/* 158 */         this.objectDepth++;
/*     */         break;
/*     */       case END_OBJECT:
/* 161 */         this.objectDepth--;
/*     */         break;
/*     */       case START_ARRAY:
/* 164 */         this.arrayDepth++;
/*     */         break;
/*     */       case END_ARRAY:
/* 167 */         this.arrayDepth--;
/*     */         break;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void processTokenNormal(JsonToken token, List<TokenBuffer> result) throws IOException {
/* 173 */     this.tokenBuffer.copyCurrentEvent(this.parser);
/*     */     
/* 175 */     if ((token.isStructEnd() || token.isScalarValue()) && this.objectDepth == 0 && this.arrayDepth == 0) {
/* 176 */       result.add(this.tokenBuffer);
/* 177 */       this.tokenBuffer = createToken();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void processTokenArray(JsonToken token, List<TokenBuffer> result) throws IOException {
/* 182 */     if (!isTopLevelArrayToken(token)) {
/* 183 */       this.tokenBuffer.copyCurrentEvent(this.parser);
/*     */     }
/*     */     
/* 186 */     if (this.objectDepth == 0 && (this.arrayDepth == 0 || this.arrayDepth == 1) && (token == JsonToken.END_OBJECT || token
/* 187 */       .isScalarValue())) {
/* 188 */       result.add(this.tokenBuffer);
/* 189 */       this.tokenBuffer = createToken();
/*     */     } 
/*     */   }
/*     */   
/*     */   private TokenBuffer createToken() {
/* 194 */     TokenBuffer tokenBuffer = new TokenBuffer(this.parser, this.deserializationContext);
/* 195 */     tokenBuffer.forceUseOfBigDecimal(this.forceUseOfBigDecimal);
/* 196 */     return tokenBuffer;
/*     */   }
/*     */   
/*     */   private boolean isTopLevelArrayToken(JsonToken token) {
/* 200 */     return (this.objectDepth == 0 && ((token == JsonToken.START_ARRAY && this.arrayDepth == 1) || (token == JsonToken.END_ARRAY && this.arrayDepth == 0)));
/*     */   }
/*     */ 
/*     */   
/*     */   private void assertInMemorySize(int currentBufferSize, List<TokenBuffer> result) {
/* 205 */     if (this.maxInMemorySize >= 0) {
/* 206 */       if (!result.isEmpty()) {
/* 207 */         this.byteCount = 0;
/*     */       }
/* 209 */       else if (currentBufferSize > Integer.MAX_VALUE - this.byteCount) {
/* 210 */         raiseLimitException();
/*     */       } else {
/*     */         
/* 213 */         this.byteCount += currentBufferSize;
/* 214 */         if (this.byteCount > this.maxInMemorySize) {
/* 215 */           raiseLimitException();
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void raiseLimitException() {
/* 222 */     throw new DataBufferLimitException("Exceeded limit on max bytes per JSON object: " + this.maxInMemorySize);
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
/*     */   public static Flux<TokenBuffer> tokenize(Flux<DataBuffer> dataBuffers, JsonFactory jsonFactory, ObjectMapper objectMapper, boolean tokenizeArrays, boolean forceUseOfBigDecimal, int maxInMemorySize) {
/*     */     try {
/*     */       DefaultDeserializationContext defaultDeserializationContext;
/* 243 */       JsonParser parser = jsonFactory.createNonBlockingByteArrayParser();
/* 244 */       DeserializationContext context = objectMapper.getDeserializationContext();
/* 245 */       if (context instanceof DefaultDeserializationContext) {
/* 246 */         defaultDeserializationContext = ((DefaultDeserializationContext)context).createInstance(objectMapper
/* 247 */             .getDeserializationConfig(), parser, objectMapper.getInjectableValues());
/*     */       }
/* 249 */       Jackson2Tokenizer tokenizer = new Jackson2Tokenizer(parser, (DeserializationContext)defaultDeserializationContext, tokenizeArrays, forceUseOfBigDecimal, maxInMemorySize);
/*     */       
/* 251 */       return dataBuffers.concatMapIterable(tokenizer::tokenize).concatWith((Publisher)tokenizer.endOfInput());
/*     */     }
/* 253 */     catch (IOException ex) {
/* 254 */       return Flux.error(ex);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/json/Jackson2Tokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */