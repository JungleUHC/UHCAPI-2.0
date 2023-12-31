/*     */ package com.fasterxml.jackson.core;
/*     */ 
/*     */ import com.fasterxml.jackson.core.io.ContentReference;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonLocation
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 2L;
/*     */   @Deprecated
/*     */   public static final int MAX_CONTENT_SNIPPET = 500;
/*  38 */   public static final JsonLocation NA = new JsonLocation(ContentReference.unknown(), -1L, -1L, -1, -1);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final long _totalBytes;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final long _totalChars;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int _lineNr;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int _columnNr;
/*     */ 
/*     */ 
/*     */   
/*     */   protected final ContentReference _contentReference;
/*     */ 
/*     */ 
/*     */   
/*     */   protected transient String _sourceDescription;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonLocation(ContentReference contentRef, long totalChars, int lineNr, int colNr) {
/*  72 */     this(contentRef, -1L, totalChars, lineNr, colNr);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonLocation(ContentReference contentRef, long totalBytes, long totalChars, int lineNr, int columnNr) {
/*  79 */     if (contentRef == null) {
/*  80 */       contentRef = ContentReference.unknown();
/*     */     }
/*  82 */     this._contentReference = contentRef;
/*  83 */     this._totalBytes = totalBytes;
/*  84 */     this._totalChars = totalChars;
/*  85 */     this._lineNr = lineNr;
/*  86 */     this._columnNr = columnNr;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public JsonLocation(Object srcRef, long totalChars, int lineNr, int columnNr) {
/*  91 */     this(_wrap(srcRef), totalChars, lineNr, columnNr);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JsonLocation(Object srcRef, long totalBytes, long totalChars, int lineNr, int columnNr) {
/*  97 */     this(_wrap(srcRef), totalBytes, totalChars, lineNr, columnNr);
/*     */   }
/*     */   
/*     */   protected static ContentReference _wrap(Object srcRef) {
/* 101 */     if (srcRef instanceof ContentReference) {
/* 102 */       return (ContentReference)srcRef;
/*     */     }
/* 104 */     return ContentReference.construct(false, srcRef);
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
/*     */   public ContentReference contentReference() {
/* 126 */     return this._contentReference;
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
/*     */   @Deprecated
/*     */   public Object getSourceRef() {
/* 143 */     return this._contentReference.getRawContent();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLineNr() {
/* 152 */     return this._lineNr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getColumnNr() {
/* 160 */     return this._columnNr;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCharOffset() {
/* 166 */     return this._totalChars;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getByteOffset() {
/* 172 */     return this._totalBytes;
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
/*     */   public String sourceDescription() {
/* 190 */     if (this._sourceDescription == null) {
/* 191 */       this._sourceDescription = this._contentReference.buildSourceDescription();
/*     */     }
/* 193 */     return this._sourceDescription;
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
/*     */   public String offsetDescription() {
/* 206 */     return appendOffsetDescription(new StringBuilder(40)).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringBuilder appendOffsetDescription(StringBuilder sb) {
/* 216 */     if (this._contentReference.hasTextualContent()) {
/* 217 */       sb.append("line: ");
/*     */       
/* 219 */       if (this._lineNr >= 0) {
/* 220 */         sb.append(this._lineNr);
/*     */       } else {
/* 222 */         sb.append("UNKNOWN");
/*     */       } 
/* 224 */       sb.append(", column: ");
/* 225 */       if (this._columnNr >= 0) {
/* 226 */         sb.append(this._columnNr);
/*     */       } else {
/* 228 */         sb.append("UNKNOWN");
/*     */       
/*     */       }
/*     */ 
/*     */     
/*     */     }
/* 234 */     else if (this._lineNr > 0) {
/* 235 */       sb.append("line: ").append(this._lineNr);
/* 236 */       if (this._columnNr > 0) {
/* 237 */         sb.append(", column: ");
/* 238 */         sb.append(this._columnNr);
/*     */       } 
/*     */     } else {
/* 241 */       sb.append("byte offset: #");
/*     */ 
/*     */       
/* 244 */       if (this._totalBytes >= 0L) {
/* 245 */         sb.append(this._totalBytes);
/*     */       } else {
/* 247 */         sb.append("UNKNOWN");
/*     */       } 
/*     */     } 
/*     */     
/* 251 */     return sb;
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
/*     */   public int hashCode() {
/* 263 */     int hash = (this._contentReference == null) ? 1 : 2;
/* 264 */     hash ^= this._lineNr;
/* 265 */     hash += this._columnNr;
/* 266 */     hash ^= (int)this._totalChars;
/* 267 */     hash += (int)this._totalBytes;
/* 268 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 274 */     if (other == this) return true; 
/* 275 */     if (other == null) return false; 
/* 276 */     if (!(other instanceof JsonLocation)) return false; 
/* 277 */     JsonLocation otherLoc = (JsonLocation)other;
/*     */     
/* 279 */     if (this._contentReference == null) {
/* 280 */       if (otherLoc._contentReference != null) return false; 
/* 281 */     } else if (!this._contentReference.equals(otherLoc._contentReference)) {
/* 282 */       return false;
/*     */     } 
/*     */     
/* 285 */     return (this._lineNr == otherLoc._lineNr && this._columnNr == otherLoc._columnNr && this._totalChars == otherLoc._totalChars && this._totalBytes == otherLoc._totalBytes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 295 */     String srcDesc = sourceDescription();
/*     */ 
/*     */ 
/*     */     
/* 299 */     StringBuilder sb = (new StringBuilder(40 + srcDesc.length())).append("[Source: ").append(srcDesc).append("; ");
/* 300 */     return appendOffsetDescription(sb)
/* 301 */       .append(']')
/* 302 */       .toString();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/JsonLocation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */