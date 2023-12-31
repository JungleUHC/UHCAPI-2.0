/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonParserSequence
/*     */   extends JsonParserDelegate
/*     */ {
/*     */   protected final JsonParser[] _parsers;
/*     */   protected final boolean _checkForExistingToken;
/*     */   protected int _nextParserIndex;
/*     */   protected boolean _hasToken;
/*     */   
/*     */   @Deprecated
/*     */   protected JsonParserSequence(JsonParser[] parsers) {
/*  60 */     this(false, parsers);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonParserSequence(boolean checkForExistingToken, JsonParser[] parsers) {
/*  66 */     super(parsers[0]);
/*  67 */     this._checkForExistingToken = checkForExistingToken;
/*  68 */     this._hasToken = (checkForExistingToken && this.delegate.hasCurrentToken());
/*  69 */     this._parsers = parsers;
/*  70 */     this._nextParserIndex = 1;
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
/*     */   public static JsonParserSequence createFlattened(boolean checkForExistingToken, JsonParser first, JsonParser second) {
/*  92 */     if (!(first instanceof JsonParserSequence) && !(second instanceof JsonParserSequence)) {
/*  93 */       return new JsonParserSequence(checkForExistingToken, new JsonParser[] { first, second });
/*     */     }
/*     */     
/*  96 */     ArrayList<JsonParser> p = new ArrayList<JsonParser>();
/*  97 */     if (first instanceof JsonParserSequence) {
/*  98 */       ((JsonParserSequence)first).addFlattenedActiveParsers(p);
/*     */     } else {
/* 100 */       p.add(first);
/*     */     } 
/* 102 */     if (second instanceof JsonParserSequence) {
/* 103 */       ((JsonParserSequence)second).addFlattenedActiveParsers(p);
/*     */     } else {
/* 105 */       p.add(second);
/*     */     } 
/* 107 */     return new JsonParserSequence(checkForExistingToken, p
/* 108 */         .<JsonParser>toArray(new JsonParser[p.size()]));
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static JsonParserSequence createFlattened(JsonParser first, JsonParser second) {
/* 113 */     return createFlattened(false, first, second);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void addFlattenedActiveParsers(List<JsonParser> listToAddIn) {
/* 119 */     for (int i = this._nextParserIndex - 1, len = this._parsers.length; i < len; i++) {
/* 120 */       JsonParser p = this._parsers[i];
/* 121 */       if (p instanceof JsonParserSequence) {
/* 122 */         ((JsonParserSequence)p).addFlattenedActiveParsers(listToAddIn);
/*     */       } else {
/* 124 */         listToAddIn.add(p);
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
/*     */   public void close() throws IOException {
/*     */     
/* 138 */     do { this.delegate.close(); } while (switchToNext());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonToken nextToken() throws IOException {
/* 144 */     if (this.delegate == null) {
/* 145 */       return null;
/*     */     }
/* 147 */     if (this._hasToken) {
/* 148 */       this._hasToken = false;
/* 149 */       return this.delegate.currentToken();
/*     */     } 
/* 151 */     JsonToken t = this.delegate.nextToken();
/* 152 */     if (t == null) {
/* 153 */       return switchAndReturnNext();
/*     */     }
/* 155 */     return t;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonParser skipChildren() throws IOException {
/* 166 */     if (this.delegate.currentToken() != JsonToken.START_OBJECT && this.delegate
/* 167 */       .currentToken() != JsonToken.START_ARRAY) {
/* 168 */       return this;
/*     */     }
/* 170 */     int open = 1;
/*     */ 
/*     */ 
/*     */     
/*     */     while (true) {
/* 175 */       JsonToken t = nextToken();
/* 176 */       if (t == null) {
/* 177 */         return this;
/*     */       }
/* 179 */       if (t.isStructStart()) {
/* 180 */         open++; continue;
/* 181 */       }  if (t.isStructEnd() && 
/* 182 */         --open == 0) {
/* 183 */         return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int containedParsersCount() {
/* 203 */     return this._parsers.length;
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
/*     */   protected boolean switchToNext() {
/* 223 */     if (this._nextParserIndex < this._parsers.length) {
/* 224 */       this.delegate = this._parsers[this._nextParserIndex++];
/* 225 */       return true;
/*     */     } 
/* 227 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   protected JsonToken switchAndReturnNext() throws IOException {
/* 232 */     while (this._nextParserIndex < this._parsers.length) {
/* 233 */       this.delegate = this._parsers[this._nextParserIndex++];
/* 234 */       if (this._checkForExistingToken && this.delegate.hasCurrentToken()) {
/* 235 */         return this.delegate.getCurrentToken();
/*     */       }
/* 237 */       JsonToken t = this.delegate.nextToken();
/* 238 */       if (t != null) {
/* 239 */         return t;
/*     */       }
/*     */     } 
/* 242 */     return null;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/util/JsonParserSequence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */