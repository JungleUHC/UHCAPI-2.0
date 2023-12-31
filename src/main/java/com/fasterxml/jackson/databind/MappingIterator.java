/*     */ package com.fasterxml.jackson.databind;
/*     */ 
/*     */ import com.fasterxml.jackson.core.FormatSchema;
/*     */ import com.fasterxml.jackson.core.JsonLocation;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ public class MappingIterator<T> implements Iterator<T>, Closeable {
/*  16 */   protected static final MappingIterator<?> EMPTY_ITERATOR = new MappingIterator(null, null, null, null, false, null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int STATE_CLOSED = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int STATE_NEED_RESYNC = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int STATE_MAY_HAVE_VALUE = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int STATE_HAS_VALUE = 3;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JavaType _type;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final DeserializationContext _context;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonDeserializer<T> _deserializer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonParser _parser;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonStreamContext _seqContext;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final T _updatedValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final boolean _closeParser;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _state;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MappingIterator(JavaType type, JsonParser p, DeserializationContext ctxt, JsonDeserializer<?> deser, boolean managedParser, Object valueToUpdate) {
/* 122 */     this._type = type;
/* 123 */     this._parser = p;
/* 124 */     this._context = ctxt;
/* 125 */     this._deserializer = (JsonDeserializer)deser;
/* 126 */     this._closeParser = managedParser;
/* 127 */     if (valueToUpdate == null) {
/* 128 */       this._updatedValue = null;
/*     */     } else {
/* 130 */       this._updatedValue = (T)valueToUpdate;
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
/*     */ 
/*     */     
/* 143 */     if (p == null) {
/* 144 */       this._seqContext = null;
/* 145 */       this._state = 0;
/*     */     } else {
/* 147 */       JsonStreamContext sctxt = p.getParsingContext();
/* 148 */       if (managedParser && p.isExpectedStartArrayToken()) {
/*     */         
/* 150 */         p.clearCurrentToken();
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 155 */         JsonToken t = p.currentToken();
/* 156 */         if (t == JsonToken.START_OBJECT || t == JsonToken.START_ARRAY) {
/* 157 */           sctxt = sctxt.getParent();
/*     */         }
/*     */       } 
/* 160 */       this._seqContext = sctxt;
/* 161 */       this._state = 2;
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
/*     */   public static <T> MappingIterator<T> emptyIterator() {
/* 173 */     return (MappingIterator)EMPTY_ITERATOR;
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
/*     */   public boolean hasNext() {
/*     */     try {
/* 186 */       return hasNextValue();
/* 187 */     } catch (JsonMappingException e) {
/* 188 */       return ((Boolean)_handleMappingException(e)).booleanValue();
/* 189 */     } catch (IOException e) {
/* 190 */       return ((Boolean)_handleIOException(e)).booleanValue();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T next() {
/*     */     try {
/* 199 */       return nextValue();
/* 200 */     } catch (JsonMappingException e) {
/* 201 */       return _handleMappingException(e);
/* 202 */     } catch (IOException e) {
/* 203 */       return _handleIOException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove() {
/* 209 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 214 */     if (this._state != 0) {
/* 215 */       this._state = 0;
/* 216 */       if (this._parser != null) {
/* 217 */         this._parser.close();
/*     */       }
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
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasNextValue() throws IOException {
/*     */     JsonToken t;
/* 234 */     switch (this._state) {
/*     */       case 0:
/* 236 */         return false;
/*     */       case 1:
/* 238 */         _resync();
/*     */       
/*     */       case 2:
/* 241 */         if (this._parser == null) {
/* 242 */           return false;
/*     */         }
/* 244 */         t = this._parser.currentToken();
/* 245 */         if (t == null) {
/* 246 */           t = this._parser.nextToken();
/*     */           
/* 248 */           if (t == null || t == JsonToken.END_ARRAY) {
/* 249 */             this._state = 0;
/* 250 */             if (this._closeParser) {
/* 251 */               this._parser.close();
/*     */             }
/* 253 */             return false;
/*     */           } 
/*     */         } 
/* 256 */         this._state = 3;
/* 257 */         return true;
/*     */     } 
/*     */ 
/*     */     
/* 261 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public T nextValue() throws IOException {
/* 266 */     switch (this._state) {
/*     */       case 0:
/* 268 */         return _throwNoSuchElement();
/*     */       case 1:
/*     */       case 2:
/* 271 */         if (!hasNextValue()) {
/* 272 */           return _throwNoSuchElement();
/*     */         }
/*     */         break;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 279 */     int nextState = 1;
/*     */     try {
/*     */       T value;
/* 282 */       if (this._updatedValue == null) {
/* 283 */         value = this._deserializer.deserialize(this._parser, this._context);
/*     */       } else {
/* 285 */         this._deserializer.deserialize(this._parser, this._context, this._updatedValue);
/* 286 */         value = this._updatedValue;
/*     */       } 
/* 288 */       nextState = 2;
/* 289 */       return value;
/*     */     } finally {
/* 291 */       this._state = nextState;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 296 */       this._parser.clearCurrentToken();
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
/*     */   
/*     */   public List<T> readAll() throws IOException {
/* 309 */     return readAll(new ArrayList<>());
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
/*     */   public <L extends List<? super T>> L readAll(L resultList) throws IOException {
/* 322 */     while (hasNextValue()) {
/* 323 */       resultList.add(nextValue());
/*     */     }
/* 325 */     return resultList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <C extends java.util.Collection<? super T>> C readAll(C results) throws IOException {
/* 336 */     while (hasNextValue()) {
/* 337 */       results.add(nextValue());
/*     */     }
/* 339 */     return results;
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
/*     */   public JsonParser getParser() {
/* 354 */     return this._parser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FormatSchema getParserSchema() {
/* 365 */     return this._parser.getSchema();
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
/*     */   public JsonLocation getCurrentLocation() {
/* 379 */     return this._parser.getCurrentLocation();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _resync() throws IOException {
/* 390 */     JsonParser p = this._parser;
/*     */     
/* 392 */     if (p.getParsingContext() == this._seqContext) {
/*     */       return;
/*     */     }
/*     */     
/*     */     while (true) {
/* 397 */       JsonToken t = p.nextToken();
/* 398 */       if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
/* 399 */         if (p.getParsingContext() == this._seqContext) {
/* 400 */           p.clearCurrentToken(); return;
/*     */         }  continue;
/*     */       } 
/* 403 */       if (t == JsonToken.START_ARRAY || t == JsonToken.START_OBJECT) {
/* 404 */         p.skipChildren(); continue;
/* 405 */       }  if (t == null) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   protected <R> R _throwNoSuchElement() {
/* 412 */     throw new NoSuchElementException();
/*     */   }
/*     */   
/*     */   protected <R> R _handleMappingException(JsonMappingException e) {
/* 416 */     throw new RuntimeJsonMappingException(e.getMessage(), e);
/*     */   }
/*     */   
/*     */   protected <R> R _handleIOException(IOException e) {
/* 420 */     throw new RuntimeException(e.getMessage(), e);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/MappingIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */