/*     */ package com.fasterxml.jackson.core.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ContentReference
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  35 */   protected static final ContentReference UNKNOWN_CONTENT = new ContentReference(false, null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_MAX_CONTENT_SNIPPET = 500;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final transient Object _rawContent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int _offset;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int _length;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final boolean _isContentTextual;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ContentReference(boolean isContentTextual, Object rawContent) {
/*  80 */     this(isContentTextual, rawContent, -1, -1);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected ContentReference(boolean isContentTextual, Object rawContent, int offset, int length) {
/*  86 */     this._isContentTextual = isContentTextual;
/*  87 */     this._rawContent = rawContent;
/*  88 */     this._offset = offset;
/*  89 */     this._length = length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ContentReference unknown() {
/* 100 */     return UNKNOWN_CONTENT;
/*     */   }
/*     */   
/*     */   public static ContentReference construct(boolean isContentTextual, Object rawContent) {
/* 104 */     return new ContentReference(isContentTextual, rawContent);
/*     */   }
/*     */ 
/*     */   
/*     */   public static ContentReference construct(boolean isContentTextual, Object rawContent, int offset, int length) {
/* 109 */     return new ContentReference(isContentTextual, rawContent, offset, length);
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
/*     */   public static ContentReference rawReference(boolean isContentTextual, Object rawContent) {
/* 128 */     if (rawContent instanceof ContentReference) {
/* 129 */       return (ContentReference)rawContent;
/*     */     }
/* 131 */     return new ContentReference(isContentTextual, rawContent);
/*     */   }
/*     */   
/*     */   public static ContentReference rawReference(Object rawContent) {
/* 135 */     return rawReference(false, rawContent);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void readObject(ObjectInputStream in) throws IOException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeObject(ObjectOutputStream out) throws IOException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object readResolve() {
/* 156 */     return UNKNOWN_CONTENT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasTextualContent() {
/* 166 */     return this._isContentTextual;
/*     */   }
/*     */   
/*     */   public Object getRawContent() {
/* 170 */     return this._rawContent;
/*     */   }
/*     */   
/* 173 */   public int contentOffset() { return this._offset; } public int contentLength() {
/* 174 */     return this._length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int maxContentSnippetLength() {
/* 184 */     return 500;
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
/*     */   public String buildSourceDescription() {
/* 200 */     return appendSourceDescription(new StringBuilder(200)).toString();
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
/*     */   public StringBuilder appendSourceDescription(StringBuilder sb) {
/* 213 */     Object srcRef = getRawContent();
/*     */     
/* 215 */     if (srcRef == null) {
/* 216 */       sb.append("UNKNOWN");
/* 217 */       return sb;
/*     */     } 
/*     */ 
/*     */     
/* 221 */     Class<?> srcType = (srcRef instanceof Class) ? (Class)srcRef : srcRef.getClass();
/* 222 */     String tn = srcType.getName();
/*     */     
/* 224 */     if (tn.startsWith("java.")) {
/* 225 */       tn = srcType.getSimpleName();
/* 226 */     } else if (srcRef instanceof byte[]) {
/* 227 */       tn = "byte[]";
/* 228 */     } else if (srcRef instanceof char[]) {
/* 229 */       tn = "char[]";
/*     */     } 
/* 231 */     sb.append('(').append(tn).append(')');
/*     */ 
/*     */ 
/*     */     
/* 235 */     if (hasTextualContent()) {
/* 236 */       String trimmed, unitStr = " chars";
/*     */ 
/*     */ 
/*     */       
/* 240 */       int maxLen = maxContentSnippetLength();
/* 241 */       int[] offsets = { contentOffset(), contentLength() };
/*     */       
/* 243 */       if (srcRef instanceof CharSequence) {
/* 244 */         trimmed = _truncate((CharSequence)srcRef, offsets, maxLen);
/* 245 */       } else if (srcRef instanceof char[]) {
/* 246 */         trimmed = _truncate((char[])srcRef, offsets, maxLen);
/* 247 */       } else if (srcRef instanceof byte[]) {
/* 248 */         trimmed = _truncate((byte[])srcRef, offsets, maxLen);
/* 249 */         unitStr = " bytes";
/*     */       } else {
/* 251 */         trimmed = null;
/*     */       } 
/* 253 */       if (trimmed != null) {
/* 254 */         _append(sb, trimmed);
/* 255 */         if (offsets[1] > maxLen) {
/* 256 */           sb.append("[truncated ").append(offsets[1] - maxLen).append(unitStr).append(']');
/*     */         }
/*     */       }
/*     */     
/*     */     }
/* 261 */     else if (srcRef instanceof byte[]) {
/* 262 */       int length = contentLength();
/*     */       
/* 264 */       if (length < 0) {
/* 265 */         length = ((byte[])srcRef).length;
/*     */       }
/* 267 */       sb.append('[')
/* 268 */         .append(length)
/* 269 */         .append(" bytes]");
/*     */     } 
/*     */     
/* 272 */     return sb;
/*     */   }
/*     */   
/*     */   protected String _truncate(CharSequence cs, int[] offsets, int maxSnippetLen) {
/* 276 */     _truncateOffsets(offsets, cs.length());
/* 277 */     int start = offsets[0];
/* 278 */     int length = Math.min(offsets[1], maxSnippetLen);
/* 279 */     return cs.subSequence(start, start + length).toString();
/*     */   }
/*     */   
/*     */   protected String _truncate(char[] cs, int[] offsets, int maxSnippetLen) {
/* 283 */     _truncateOffsets(offsets, cs.length);
/* 284 */     int start = offsets[0];
/* 285 */     int length = Math.min(offsets[1], maxSnippetLen);
/* 286 */     return new String(cs, start, length);
/*     */   }
/*     */   
/*     */   protected String _truncate(byte[] b, int[] offsets, int maxSnippetLen) {
/* 290 */     _truncateOffsets(offsets, b.length);
/* 291 */     int start = offsets[0];
/* 292 */     int length = Math.min(offsets[1], maxSnippetLen);
/* 293 */     return new String(b, start, length, Charset.forName("UTF-8"));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _truncateOffsets(int[] offsets, int actualLength) {
/* 299 */     int start = offsets[0];
/*     */     
/* 301 */     if (start < 0) {
/* 302 */       start = 0;
/* 303 */     } else if (start >= actualLength) {
/* 304 */       start = actualLength;
/*     */     } 
/* 306 */     offsets[0] = start;
/*     */ 
/*     */     
/* 309 */     int length = offsets[1];
/* 310 */     int maxLength = actualLength - start;
/* 311 */     if (length < 0 || length > maxLength) {
/* 312 */       offsets[1] = maxLength;
/*     */     }
/*     */   }
/*     */   
/*     */   protected int _append(StringBuilder sb, String content) {
/* 317 */     sb.append('"');
/*     */     
/* 319 */     for (int i = 0, end = content.length(); i < end; i++) {
/*     */ 
/*     */ 
/*     */       
/* 323 */       char ch = content.charAt(i);
/* 324 */       if (!Character.isISOControl(ch) || !_appendEscaped(sb, ch)) {
/* 325 */         sb.append(ch);
/*     */       }
/*     */     } 
/* 328 */     sb.append('"');
/* 329 */     return content.length();
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean _appendEscaped(StringBuilder sb, int ctrlChar) {
/* 334 */     if (ctrlChar == 13 || ctrlChar == 10) {
/* 335 */       return false;
/*     */     }
/* 337 */     sb.append('\\');
/* 338 */     sb.append('u');
/* 339 */     sb.append(CharTypes.hexToChar(ctrlChar >> 12 & 0xF));
/* 340 */     sb.append(CharTypes.hexToChar(ctrlChar >> 8 & 0xF));
/* 341 */     sb.append(CharTypes.hexToChar(ctrlChar >> 4 & 0xF));
/* 342 */     sb.append(CharTypes.hexToChar(ctrlChar & 0xF));
/* 343 */     return true;
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
/*     */   public boolean equals(Object other) {
/* 358 */     if (other == this) return true; 
/* 359 */     if (other == null) return false; 
/* 360 */     if (!(other instanceof ContentReference)) return false; 
/* 361 */     ContentReference otherSrc = (ContentReference)other;
/*     */ 
/*     */     
/* 364 */     if (this._offset != otherSrc._offset || this._length != otherSrc._length)
/*     */     {
/* 366 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 372 */     Object otherRaw = otherSrc._rawContent;
/*     */     
/* 374 */     if (this._rawContent == null)
/* 375 */       return (otherRaw == null); 
/* 376 */     if (otherRaw == null) {
/* 377 */       return false;
/*     */     }
/*     */     
/* 380 */     if (this._rawContent instanceof java.io.File || this._rawContent instanceof java.net.URL || this._rawContent instanceof java.net.URI)
/*     */     {
/*     */ 
/*     */       
/* 384 */       return this._rawContent.equals(otherRaw);
/*     */     }
/* 386 */     return (this._rawContent == otherSrc._rawContent);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 392 */     return Objects.hashCode(this._rawContent);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/io/ContentReference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */