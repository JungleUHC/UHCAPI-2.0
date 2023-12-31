/*     */ package com.fasterxml.jackson.core.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonLocation;
/*     */ import com.fasterxml.jackson.core.JsonParseException;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import java.util.HashSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DupDetector
/*     */ {
/*     */   protected final Object _source;
/*     */   protected String _firstName;
/*     */   protected String _secondName;
/*     */   protected HashSet<String> _seen;
/*     */   
/*     */   private DupDetector(Object src) {
/*  37 */     this._source = src;
/*     */   }
/*     */   
/*     */   public static DupDetector rootDetector(JsonParser p) {
/*  41 */     return new DupDetector(p);
/*     */   }
/*     */   
/*     */   public static DupDetector rootDetector(JsonGenerator g) {
/*  45 */     return new DupDetector(g);
/*     */   }
/*     */   
/*     */   public DupDetector child() {
/*  49 */     return new DupDetector(this._source);
/*     */   }
/*     */   
/*     */   public void reset() {
/*  53 */     this._firstName = null;
/*  54 */     this._secondName = null;
/*  55 */     this._seen = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonLocation findLocation() {
/*  60 */     if (this._source instanceof JsonParser) {
/*  61 */       return ((JsonParser)this._source).getCurrentLocation();
/*     */     }
/*     */     
/*  64 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getSource() {
/*  73 */     return this._source;
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
/*     */   public boolean isDup(String name) throws JsonParseException {
/*  90 */     if (this._firstName == null) {
/*  91 */       this._firstName = name;
/*  92 */       return false;
/*     */     } 
/*  94 */     if (name.equals(this._firstName)) {
/*  95 */       return true;
/*     */     }
/*  97 */     if (this._secondName == null) {
/*  98 */       this._secondName = name;
/*  99 */       return false;
/*     */     } 
/* 101 */     if (name.equals(this._secondName)) {
/* 102 */       return true;
/*     */     }
/* 104 */     if (this._seen == null) {
/* 105 */       this._seen = new HashSet<String>(16);
/* 106 */       this._seen.add(this._firstName);
/* 107 */       this._seen.add(this._secondName);
/*     */     } 
/* 109 */     return !this._seen.add(name);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/json/DupDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */