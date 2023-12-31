/*     */ package com.fasterxml.jackson.core;
/*     */ 
/*     */ import com.fasterxml.jackson.core.io.NumberInput;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonPointer
/*     */ {
/*     */   public static final char SEPARATOR = '/';
/*  34 */   protected static final JsonPointer EMPTY = new JsonPointer();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonPointer _nextSegment;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected volatile JsonPointer _head;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String _asString;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String _matchingPropertyName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int _matchingElementIndex;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonPointer() {
/*  78 */     this._nextSegment = null;
/*  79 */     this._matchingPropertyName = "";
/*  80 */     this._matchingElementIndex = -1;
/*  81 */     this._asString = "";
/*     */   }
/*     */ 
/*     */   
/*     */   protected JsonPointer(String fullString, String segment, JsonPointer next) {
/*  86 */     this._asString = fullString;
/*  87 */     this._nextSegment = next;
/*     */     
/*  89 */     this._matchingPropertyName = segment;
/*     */     
/*  91 */     this._matchingElementIndex = _parseIndex(segment);
/*     */   }
/*     */ 
/*     */   
/*     */   protected JsonPointer(String fullString, String segment, int matchIndex, JsonPointer next) {
/*  96 */     this._asString = fullString;
/*  97 */     this._nextSegment = next;
/*  98 */     this._matchingPropertyName = segment;
/*  99 */     this._matchingElementIndex = matchIndex;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonPointer compile(String expr) throws IllegalArgumentException {
/* 124 */     if (expr == null || expr.length() == 0) {
/* 125 */       return EMPTY;
/*     */     }
/*     */     
/* 128 */     if (expr.charAt(0) != '/') {
/* 129 */       throw new IllegalArgumentException("Invalid input: JSON Pointer expression must start with '/': \"" + expr + "\"");
/*     */     }
/* 131 */     return _parseTail(expr);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonPointer valueOf(String expr) {
/* 142 */     return compile(expr);
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
/*     */   public static JsonPointer empty() {
/* 155 */     return EMPTY;
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
/*     */   public static JsonPointer forPath(JsonStreamContext context, boolean includeRoot) {
/* 174 */     if (context == null) {
/* 175 */       return EMPTY;
/*     */     }
/*     */ 
/*     */     
/* 179 */     if (!context.hasPathSegment())
/*     */     {
/* 181 */       if (!includeRoot || !context.inRoot() || !context.hasCurrentIndex()) {
/* 182 */         context = context.getParent();
/*     */       }
/*     */     }
/* 185 */     JsonPointer tail = null;
/*     */     
/* 187 */     for (; context != null; context = context.getParent()) {
/* 188 */       if (context.inObject()) {
/* 189 */         String seg = context.getCurrentName();
/* 190 */         if (seg == null) {
/* 191 */           seg = "";
/*     */         }
/* 193 */         tail = new JsonPointer(_fullPath(tail, seg), seg, tail);
/* 194 */       } else if (context.inArray() || includeRoot) {
/* 195 */         int ix = context.getCurrentIndex();
/* 196 */         String ixStr = String.valueOf(ix);
/* 197 */         tail = new JsonPointer(_fullPath(tail, ixStr), ixStr, ix, tail);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 203 */     if (tail == null) {
/* 204 */       return EMPTY;
/*     */     }
/* 206 */     return tail;
/*     */   }
/*     */ 
/*     */   
/*     */   private static String _fullPath(JsonPointer tail, String segment) {
/* 211 */     if (tail == null) {
/* 212 */       StringBuilder stringBuilder = new StringBuilder(segment.length() + 1);
/* 213 */       stringBuilder.append('/');
/* 214 */       _appendEscaped(stringBuilder, segment);
/* 215 */       return stringBuilder.toString();
/*     */     } 
/* 217 */     String tailDesc = tail._asString;
/* 218 */     StringBuilder sb = new StringBuilder(segment.length() + 1 + tailDesc.length());
/* 219 */     sb.append('/');
/* 220 */     _appendEscaped(sb, segment);
/* 221 */     sb.append(tailDesc);
/* 222 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   private static void _appendEscaped(StringBuilder sb, String segment) {
/* 227 */     for (int i = 0, end = segment.length(); i < end; i++) {
/* 228 */       char c = segment.charAt(i);
/* 229 */       if (c == '/') {
/* 230 */         sb.append("~1");
/*     */       
/*     */       }
/* 233 */       else if (c == '~') {
/* 234 */         sb.append("~0");
/*     */       } else {
/*     */         
/* 237 */         sb.append(c);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches() {
/* 269 */     return (this._nextSegment == null);
/* 270 */   } public String getMatchingProperty() { return this._matchingPropertyName; } public int getMatchingIndex() {
/* 271 */     return this._matchingElementIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean mayMatchProperty() {
/* 277 */     return (this._matchingPropertyName != null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean mayMatchElement() {
/* 283 */     return (this._matchingElementIndex >= 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonPointer last() {
/* 292 */     JsonPointer current = this;
/* 293 */     if (current == EMPTY) {
/* 294 */       return null;
/*     */     }
/*     */     JsonPointer next;
/* 297 */     while ((next = current._nextSegment) != EMPTY) {
/* 298 */       current = next;
/*     */     }
/* 300 */     return current;
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
/*     */   public JsonPointer append(JsonPointer tail) {
/* 320 */     if (this == EMPTY) {
/* 321 */       return tail;
/*     */     }
/* 323 */     if (tail == EMPTY) {
/* 324 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 329 */     String currentJsonPointer = this._asString;
/* 330 */     if (currentJsonPointer.endsWith("/"))
/*     */     {
/* 332 */       currentJsonPointer = currentJsonPointer.substring(0, currentJsonPointer.length() - 1);
/*     */     }
/* 334 */     return compile(currentJsonPointer + tail._asString);
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
/*     */   public boolean matchesProperty(String name) {
/* 348 */     return (this._nextSegment != null && this._matchingPropertyName.equals(name));
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
/*     */   public JsonPointer matchProperty(String name) {
/* 363 */     if (this._nextSegment != null && this._matchingPropertyName.equals(name)) {
/* 364 */       return this._nextSegment;
/*     */     }
/* 366 */     return null;
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
/*     */   public boolean matchesElement(int index) {
/* 380 */     return (index == this._matchingElementIndex && index >= 0);
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
/*     */   public JsonPointer matchElement(int index) {
/* 397 */     if (index != this._matchingElementIndex || index < 0) {
/* 398 */       return null;
/*     */     }
/* 400 */     return this._nextSegment;
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
/*     */   public JsonPointer tail() {
/* 417 */     return this._nextSegment;
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
/*     */   public JsonPointer head() {
/* 437 */     JsonPointer h = this._head;
/* 438 */     if (h == null) {
/* 439 */       if (this != EMPTY) {
/* 440 */         h = _constructHead();
/*     */       }
/* 442 */       this._head = h;
/*     */     } 
/* 444 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 453 */     return this._asString; } public int hashCode() {
/* 454 */     return this._asString.hashCode();
/*     */   }
/*     */   public boolean equals(Object o) {
/* 457 */     if (o == this) return true; 
/* 458 */     if (o == null) return false; 
/* 459 */     if (!(o instanceof JsonPointer)) return false; 
/* 460 */     return this._asString.equals(((JsonPointer)o)._asString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int _parseIndex(String str) {
/* 470 */     int len = str.length();
/*     */ 
/*     */     
/* 473 */     if (len == 0 || len > 10) {
/* 474 */       return -1;
/*     */     }
/*     */     
/* 477 */     char c = str.charAt(0);
/* 478 */     if (c <= '0') {
/* 479 */       return (len == 1 && c == '0') ? 0 : -1;
/*     */     }
/* 481 */     if (c > '9') {
/* 482 */       return -1;
/*     */     }
/* 484 */     for (int i = 1; i < len; i++) {
/* 485 */       c = str.charAt(i);
/* 486 */       if (c > '9' || c < '0') {
/* 487 */         return -1;
/*     */       }
/*     */     } 
/* 490 */     if (len == 10) {
/* 491 */       long l = NumberInput.parseLong(str);
/* 492 */       if (l > 2147483647L) {
/* 493 */         return -1;
/*     */       }
/*     */     } 
/* 496 */     return NumberInput.parseInt(str);
/*     */   }
/*     */   
/*     */   protected static JsonPointer _parseTail(String input) {
/* 500 */     int end = input.length();
/*     */ 
/*     */     
/* 503 */     for (int i = 1; i < end; ) {
/* 504 */       char c = input.charAt(i);
/* 505 */       if (c == '/') {
/* 506 */         return new JsonPointer(input, input.substring(1, i), 
/* 507 */             _parseTail(input.substring(i)));
/*     */       }
/* 509 */       i++;
/*     */       
/* 511 */       if (c == '~' && i < end) {
/* 512 */         return _parseQuotedTail(input, i);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 517 */     return new JsonPointer(input, input.substring(1), EMPTY);
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
/*     */   protected static JsonPointer _parseQuotedTail(String input, int i) {
/* 530 */     int end = input.length();
/* 531 */     StringBuilder sb = new StringBuilder(Math.max(16, end));
/* 532 */     if (i > 2) {
/* 533 */       sb.append(input, 1, i - 1);
/*     */     }
/* 535 */     _appendEscape(sb, input.charAt(i++));
/* 536 */     while (i < end) {
/* 537 */       char c = input.charAt(i);
/* 538 */       if (c == '/') {
/* 539 */         return new JsonPointer(input, sb.toString(), 
/* 540 */             _parseTail(input.substring(i)));
/*     */       }
/* 542 */       i++;
/* 543 */       if (c == '~' && i < end) {
/* 544 */         _appendEscape(sb, input.charAt(i++));
/*     */         continue;
/*     */       } 
/* 547 */       sb.append(c);
/*     */     } 
/*     */     
/* 550 */     return new JsonPointer(input, sb.toString(), EMPTY);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonPointer _constructHead() {
/* 556 */     JsonPointer last = last();
/* 557 */     if (last == this) {
/* 558 */       return EMPTY;
/*     */     }
/*     */     
/* 561 */     int suffixLength = last._asString.length();
/* 562 */     JsonPointer next = this._nextSegment;
/* 563 */     return new JsonPointer(this._asString.substring(0, this._asString.length() - suffixLength), this._matchingPropertyName, this._matchingElementIndex, next
/* 564 */         ._constructHead(suffixLength, last));
/*     */   }
/*     */ 
/*     */   
/*     */   protected JsonPointer _constructHead(int suffixLength, JsonPointer last) {
/* 569 */     if (this == last) {
/* 570 */       return EMPTY;
/*     */     }
/* 572 */     JsonPointer next = this._nextSegment;
/* 573 */     String str = this._asString;
/* 574 */     return new JsonPointer(str.substring(0, str.length() - suffixLength), this._matchingPropertyName, this._matchingElementIndex, next
/* 575 */         ._constructHead(suffixLength, last));
/*     */   }
/*     */   
/*     */   private static void _appendEscape(StringBuilder sb, char c) {
/* 579 */     if (c == '0') {
/* 580 */       c = '~';
/* 581 */     } else if (c == '1') {
/* 582 */       c = '/';
/*     */     } else {
/* 584 */       sb.append('~');
/*     */     } 
/* 586 */     sb.append(c);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/JsonPointer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */