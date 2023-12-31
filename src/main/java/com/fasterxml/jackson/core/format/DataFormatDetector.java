/*     */ package com.fasterxml.jackson.core.format;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonFactory;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Collection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DataFormatDetector
/*     */ {
/*     */   public static final int DEFAULT_MAX_INPUT_LOOKAHEAD = 64;
/*     */   protected final JsonFactory[] _detectors;
/*     */   protected final MatchStrength _optimalMatch;
/*     */   protected final MatchStrength _minimalMatch;
/*     */   protected final int _maxInputLookahead;
/*     */   
/*     */   public DataFormatDetector(JsonFactory... detectors) {
/*  58 */     this(detectors, MatchStrength.SOLID_MATCH, MatchStrength.WEAK_MATCH, 64);
/*     */   }
/*     */ 
/*     */   
/*     */   public DataFormatDetector(Collection<JsonFactory> detectors) {
/*  63 */     this(detectors.<JsonFactory>toArray(new JsonFactory[0]));
/*     */   }
/*     */ 
/*     */   
/*     */   private DataFormatDetector(JsonFactory[] detectors, MatchStrength optMatch, MatchStrength minMatch, int maxInputLookahead) {
/*  68 */     this._detectors = detectors;
/*  69 */     this._optimalMatch = optMatch;
/*  70 */     this._minimalMatch = minMatch;
/*  71 */     this._maxInputLookahead = maxInputLookahead;
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
/*     */   public DataFormatDetector withOptimalMatch(MatchStrength optMatch) {
/*  84 */     if (optMatch == this._optimalMatch) {
/*  85 */       return this;
/*     */     }
/*  87 */     return new DataFormatDetector(this._detectors, optMatch, this._minimalMatch, this._maxInputLookahead);
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
/*     */   public DataFormatDetector withMinimalMatch(MatchStrength minMatch) {
/*  99 */     if (minMatch == this._minimalMatch) {
/* 100 */       return this;
/*     */     }
/* 102 */     return new DataFormatDetector(this._detectors, this._optimalMatch, minMatch, this._maxInputLookahead);
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
/*     */   public DataFormatDetector withMaxInputLookahead(int lookaheadBytes) {
/* 114 */     if (lookaheadBytes == this._maxInputLookahead) {
/* 115 */       return this;
/*     */     }
/* 117 */     return new DataFormatDetector(this._detectors, this._optimalMatch, this._minimalMatch, lookaheadBytes);
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
/*     */   public DataFormatMatcher findFormat(InputStream in) throws IOException {
/* 139 */     return _findFormat(new InputAccessor.Std(in, new byte[this._maxInputLookahead]));
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
/*     */   public DataFormatMatcher findFormat(byte[] fullInputData) throws IOException {
/* 154 */     return _findFormat(new InputAccessor.Std(fullInputData));
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
/*     */   public DataFormatMatcher findFormat(byte[] fullInputData, int offset, int len) throws IOException {
/* 173 */     return _findFormat(new InputAccessor.Std(fullInputData, offset, len));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 183 */     StringBuilder sb = new StringBuilder();
/* 184 */     sb.append('[');
/* 185 */     int len = this._detectors.length;
/* 186 */     if (len > 0) {
/* 187 */       sb.append(this._detectors[0].getFormatName());
/* 188 */       for (int i = 1; i < len; i++) {
/* 189 */         sb.append(", ");
/* 190 */         sb.append(this._detectors[i].getFormatName());
/*     */       } 
/*     */     } 
/* 193 */     sb.append(']');
/* 194 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DataFormatMatcher _findFormat(InputAccessor.Std acc) throws IOException {
/* 204 */     JsonFactory bestMatch = null;
/* 205 */     MatchStrength bestMatchStrength = null;
/* 206 */     for (JsonFactory f : this._detectors) {
/* 207 */       acc.reset();
/* 208 */       MatchStrength strength = f.hasFormat(acc);
/*     */       
/* 210 */       if (strength != null && strength.ordinal() >= this._minimalMatch.ordinal())
/*     */       {
/*     */ 
/*     */         
/* 214 */         if (bestMatch == null || 
/* 215 */           bestMatchStrength.ordinal() < strength.ordinal()) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 220 */           bestMatch = f;
/* 221 */           bestMatchStrength = strength;
/* 222 */           if (strength.ordinal() >= this._optimalMatch.ordinal())
/*     */             break; 
/*     */         }  } 
/*     */     } 
/* 226 */     return acc.createMatcher(bestMatch, bestMatchStrength);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/format/DataFormatDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */