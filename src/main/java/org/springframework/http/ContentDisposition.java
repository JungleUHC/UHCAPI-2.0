/*     */ package org.springframework.http;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.time.ZonedDateTime;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.time.format.DateTimeParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Base64;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ import org.springframework.util.StreamUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ContentDisposition
/*     */ {
/*  52 */   private static final Pattern BASE64_ENCODED_PATTERN = Pattern.compile("=\\?([0-9a-zA-Z-_]+)\\?B\\?([+/0-9a-zA-Z]+=*)\\?=");
/*     */ 
/*     */   
/*     */   private static final String INVALID_HEADER_FIELD_PARAMETER_FORMAT = "Invalid header field parameter format (as defined in RFC 5987)";
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final String type;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final String name;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final String filename;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final Charset charset;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final Long size;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final ZonedDateTime creationDate;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final ZonedDateTime modificationDate;
/*     */   
/*     */   @Nullable
/*     */   private final ZonedDateTime readDate;
/*     */ 
/*     */   
/*     */   private ContentDisposition(@Nullable String type, @Nullable String name, @Nullable String filename, @Nullable Charset charset, @Nullable Long size, @Nullable ZonedDateTime creationDate, @Nullable ZonedDateTime modificationDate, @Nullable ZonedDateTime readDate) {
/*  90 */     this.type = type;
/*  91 */     this.name = name;
/*  92 */     this.filename = filename;
/*  93 */     this.charset = charset;
/*  94 */     this.size = size;
/*  95 */     this.creationDate = creationDate;
/*  96 */     this.modificationDate = modificationDate;
/*  97 */     this.readDate = readDate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAttachment() {
/* 106 */     return (this.type != null && this.type.equalsIgnoreCase("attachment"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFormData() {
/* 114 */     return (this.type != null && this.type.equalsIgnoreCase("form-data"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInline() {
/* 122 */     return (this.type != null && this.type.equalsIgnoreCase("inline"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getType() {
/* 133 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getName() {
/* 141 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getFilename() {
/* 151 */     return this.filename;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Charset getCharset() {
/* 159 */     return this.charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public Long getSize() {
/* 171 */     return this.size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public ZonedDateTime getCreationDate() {
/* 183 */     return this.creationDate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public ZonedDateTime getModificationDate() {
/* 195 */     return this.modificationDate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public ZonedDateTime getReadDate() {
/* 207 */     return this.readDate;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 212 */     if (this == other) {
/* 213 */       return true;
/*     */     }
/* 215 */     if (!(other instanceof ContentDisposition)) {
/* 216 */       return false;
/*     */     }
/* 218 */     ContentDisposition otherCd = (ContentDisposition)other;
/* 219 */     return (ObjectUtils.nullSafeEquals(this.type, otherCd.type) && 
/* 220 */       ObjectUtils.nullSafeEquals(this.name, otherCd.name) && 
/* 221 */       ObjectUtils.nullSafeEquals(this.filename, otherCd.filename) && 
/* 222 */       ObjectUtils.nullSafeEquals(this.charset, otherCd.charset) && 
/* 223 */       ObjectUtils.nullSafeEquals(this.size, otherCd.size) && 
/* 224 */       ObjectUtils.nullSafeEquals(this.creationDate, otherCd.creationDate) && 
/* 225 */       ObjectUtils.nullSafeEquals(this.modificationDate, otherCd.modificationDate) && 
/* 226 */       ObjectUtils.nullSafeEquals(this.readDate, otherCd.readDate));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 231 */     int result = ObjectUtils.nullSafeHashCode(this.type);
/* 232 */     result = 31 * result + ObjectUtils.nullSafeHashCode(this.name);
/* 233 */     result = 31 * result + ObjectUtils.nullSafeHashCode(this.filename);
/* 234 */     result = 31 * result + ObjectUtils.nullSafeHashCode(this.charset);
/* 235 */     result = 31 * result + ObjectUtils.nullSafeHashCode(this.size);
/* 236 */     result = 31 * result + ((this.creationDate != null) ? this.creationDate.hashCode() : 0);
/* 237 */     result = 31 * result + ((this.modificationDate != null) ? this.modificationDate.hashCode() : 0);
/* 238 */     result = 31 * result + ((this.readDate != null) ? this.readDate.hashCode() : 0);
/* 239 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 248 */     StringBuilder sb = new StringBuilder();
/* 249 */     if (this.type != null) {
/* 250 */       sb.append(this.type);
/*     */     }
/* 252 */     if (this.name != null) {
/* 253 */       sb.append("; name=\"");
/* 254 */       sb.append(this.name).append('"');
/*     */     } 
/* 256 */     if (this.filename != null) {
/* 257 */       if (this.charset == null || StandardCharsets.US_ASCII.equals(this.charset)) {
/* 258 */         sb.append("; filename=\"");
/* 259 */         sb.append(escapeQuotationsInFilename(this.filename)).append('"');
/*     */       } else {
/*     */         
/* 262 */         sb.append("; filename*=");
/* 263 */         sb.append(encodeFilename(this.filename, this.charset));
/*     */       } 
/*     */     }
/* 266 */     if (this.size != null) {
/* 267 */       sb.append("; size=");
/* 268 */       sb.append(this.size);
/*     */     } 
/* 270 */     if (this.creationDate != null) {
/* 271 */       sb.append("; creation-date=\"");
/* 272 */       sb.append(DateTimeFormatter.RFC_1123_DATE_TIME.format(this.creationDate));
/* 273 */       sb.append('"');
/*     */     } 
/* 275 */     if (this.modificationDate != null) {
/* 276 */       sb.append("; modification-date=\"");
/* 277 */       sb.append(DateTimeFormatter.RFC_1123_DATE_TIME.format(this.modificationDate));
/* 278 */       sb.append('"');
/*     */     } 
/* 280 */     if (this.readDate != null) {
/* 281 */       sb.append("; read-date=\"");
/* 282 */       sb.append(DateTimeFormatter.RFC_1123_DATE_TIME.format(this.readDate));
/* 283 */       sb.append('"');
/*     */     } 
/* 285 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Builder attachment() {
/* 294 */     return builder("attachment");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Builder formData() {
/* 302 */     return builder("form-data");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Builder inline() {
/* 310 */     return builder("inline");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Builder builder(String type) {
/* 320 */     return new BuilderImpl(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ContentDisposition empty() {
/* 327 */     return new ContentDisposition("", null, null, null, null, null, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ContentDisposition parse(String contentDisposition) {
/* 337 */     List<String> parts = tokenize(contentDisposition);
/* 338 */     String type = parts.get(0);
/* 339 */     String name = null;
/* 340 */     String filename = null;
/* 341 */     Charset charset = null;
/* 342 */     Long size = null;
/* 343 */     ZonedDateTime creationDate = null;
/* 344 */     ZonedDateTime modificationDate = null;
/* 345 */     ZonedDateTime readDate = null;
/* 346 */     for (int i = 1; i < parts.size(); i++) {
/* 347 */       String part = parts.get(i);
/* 348 */       int eqIndex = part.indexOf('=');
/* 349 */       if (eqIndex != -1) {
/* 350 */         String attribute = part.substring(0, eqIndex);
/*     */ 
/*     */         
/* 353 */         String value = (part.startsWith("\"", eqIndex + 1) && part.endsWith("\"")) ? part.substring(eqIndex + 2, part.length() - 1) : part.substring(eqIndex + 1);
/* 354 */         if (attribute.equals("name")) {
/* 355 */           name = value;
/*     */         }
/* 357 */         else if (attribute.equals("filename*")) {
/* 358 */           int idx1 = value.indexOf('\'');
/* 359 */           int idx2 = value.indexOf('\'', idx1 + 1);
/* 360 */           if (idx1 != -1 && idx2 != -1) {
/* 361 */             charset = Charset.forName(value.substring(0, idx1).trim());
/* 362 */             Assert.isTrue((StandardCharsets.UTF_8.equals(charset) || StandardCharsets.ISO_8859_1.equals(charset)), "Charset should be UTF-8 or ISO-8859-1");
/*     */             
/* 364 */             filename = decodeFilename(value.substring(idx2 + 1), charset);
/*     */           }
/*     */           else {
/*     */             
/* 368 */             filename = decodeFilename(value, StandardCharsets.US_ASCII);
/*     */           }
/*     */         
/* 371 */         } else if (attribute.equals("filename") && filename == null) {
/* 372 */           if (value.startsWith("=?")) {
/* 373 */             Matcher matcher = BASE64_ENCODED_PATTERN.matcher(value);
/* 374 */             if (matcher.find()) {
/* 375 */               String match1 = matcher.group(1);
/* 376 */               String match2 = matcher.group(2);
/* 377 */               filename = new String(Base64.getDecoder().decode(match2), Charset.forName(match1));
/*     */             } else {
/*     */               
/* 380 */               filename = value;
/*     */             } 
/*     */           } else {
/*     */             
/* 384 */             filename = value;
/*     */           }
/*     */         
/* 387 */         } else if (attribute.equals("size")) {
/* 388 */           size = Long.valueOf(Long.parseLong(value));
/*     */         }
/* 390 */         else if (attribute.equals("creation-date")) {
/*     */           try {
/* 392 */             creationDate = ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME);
/*     */           }
/* 394 */           catch (DateTimeParseException dateTimeParseException) {}
/*     */ 
/*     */         
/*     */         }
/* 398 */         else if (attribute.equals("modification-date")) {
/*     */           try {
/* 400 */             modificationDate = ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME);
/*     */           }
/* 402 */           catch (DateTimeParseException dateTimeParseException) {}
/*     */ 
/*     */         
/*     */         }
/* 406 */         else if (attribute.equals("read-date")) {
/*     */           try {
/* 408 */             readDate = ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME);
/*     */           }
/* 410 */           catch (DateTimeParseException dateTimeParseException) {}
/*     */         }
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 416 */         throw new IllegalArgumentException("Invalid content disposition format");
/*     */       } 
/*     */     } 
/* 419 */     return new ContentDisposition(type, name, filename, charset, size, creationDate, modificationDate, readDate);
/*     */   }
/*     */   
/*     */   private static List<String> tokenize(String headerValue) {
/* 423 */     int index = headerValue.indexOf(';');
/* 424 */     String type = ((index >= 0) ? headerValue.substring(0, index) : headerValue).trim();
/* 425 */     if (type.isEmpty()) {
/* 426 */       throw new IllegalArgumentException("Content-Disposition header must not be empty");
/*     */     }
/* 428 */     List<String> parts = new ArrayList<>();
/* 429 */     parts.add(type);
/* 430 */     if (index >= 0) {
/*     */       do {
/* 432 */         int nextIndex = index + 1;
/* 433 */         boolean quoted = false;
/* 434 */         boolean escaped = false;
/* 435 */         while (nextIndex < headerValue.length()) {
/* 436 */           char ch = headerValue.charAt(nextIndex);
/* 437 */           if (ch == ';') {
/* 438 */             if (!quoted) {
/*     */               break;
/*     */             }
/*     */           }
/* 442 */           else if (!escaped && ch == '"') {
/* 443 */             quoted = !quoted;
/*     */           } 
/* 445 */           escaped = (!escaped && ch == '\\');
/* 446 */           nextIndex++;
/*     */         } 
/* 448 */         String part = headerValue.substring(index + 1, nextIndex).trim();
/* 449 */         if (!part.isEmpty()) {
/* 450 */           parts.add(part);
/*     */         }
/* 452 */         index = nextIndex;
/*     */       }
/* 454 */       while (index < headerValue.length());
/*     */     }
/* 456 */     return parts;
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
/*     */   private static String decodeFilename(String filename, Charset charset) {
/* 468 */     Assert.notNull(filename, "'input' String` should not be null");
/* 469 */     Assert.notNull(charset, "'charset' should not be null");
/* 470 */     byte[] value = filename.getBytes(charset);
/* 471 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 472 */     int index = 0;
/* 473 */     while (index < value.length) {
/* 474 */       byte b = value[index];
/* 475 */       if (isRFC5987AttrChar(b)) {
/* 476 */         baos.write((char)b);
/* 477 */         index++; continue;
/*     */       } 
/* 479 */       if (b == 37 && index < value.length - 2) {
/* 480 */         char[] array = { (char)value[index + 1], (char)value[index + 2] };
/*     */         try {
/* 482 */           baos.write(Integer.parseInt(String.valueOf(array), 16));
/*     */         }
/* 484 */         catch (NumberFormatException ex) {
/* 485 */           throw new IllegalArgumentException("Invalid header field parameter format (as defined in RFC 5987)", ex);
/*     */         } 
/* 487 */         index += 3;
/*     */         continue;
/*     */       } 
/* 490 */       throw new IllegalArgumentException("Invalid header field parameter format (as defined in RFC 5987)");
/*     */     } 
/*     */     
/* 493 */     return StreamUtils.copyToString(baos, charset);
/*     */   }
/*     */   
/*     */   private static boolean isRFC5987AttrChar(byte c) {
/* 497 */     return ((c >= 48 && c <= 57) || (c >= 97 && c <= 122) || (c >= 65 && c <= 90) || c == 33 || c == 35 || c == 36 || c == 38 || c == 43 || c == 45 || c == 46 || c == 94 || c == 95 || c == 96 || c == 124 || c == 126);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String escapeQuotationsInFilename(String filename) {
/* 503 */     if (filename.indexOf('"') == -1 && filename.indexOf('\\') == -1) {
/* 504 */       return filename;
/*     */     }
/* 506 */     boolean escaped = false;
/* 507 */     StringBuilder sb = new StringBuilder();
/* 508 */     for (int i = 0; i < filename.length(); i++) {
/* 509 */       char c = filename.charAt(i);
/* 510 */       if (!escaped && c == '"') {
/* 511 */         sb.append("\\\"");
/*     */       } else {
/*     */         
/* 514 */         sb.append(c);
/*     */       } 
/* 516 */       escaped = (!escaped && c == '\\');
/*     */     } 
/*     */     
/* 519 */     if (escaped) {
/* 520 */       sb.deleteCharAt(sb.length() - 1);
/*     */     }
/* 522 */     return sb.toString();
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
/*     */   private static String encodeFilename(String input, Charset charset) {
/* 534 */     Assert.notNull(input, "`input` is required");
/* 535 */     Assert.notNull(charset, "`charset` is required");
/* 536 */     Assert.isTrue(!StandardCharsets.US_ASCII.equals(charset), "ASCII does not require encoding");
/* 537 */     Assert.isTrue((StandardCharsets.UTF_8.equals(charset) || StandardCharsets.ISO_8859_1.equals(charset)), "Only UTF-8 and ISO-8859-1 supported.");
/* 538 */     byte[] source = input.getBytes(charset);
/* 539 */     int len = source.length;
/* 540 */     StringBuilder sb = new StringBuilder(len << 1);
/* 541 */     sb.append(charset.name());
/* 542 */     sb.append("''");
/* 543 */     for (byte b : source) {
/* 544 */       if (isRFC5987AttrChar(b)) {
/* 545 */         sb.append((char)b);
/*     */       } else {
/*     */         
/* 548 */         sb.append('%');
/* 549 */         char hex1 = Character.toUpperCase(Character.forDigit(b >> 4 & 0xF, 16));
/* 550 */         char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
/* 551 */         sb.append(hex1);
/* 552 */         sb.append(hex2);
/*     */       } 
/*     */     } 
/* 555 */     return sb.toString();
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
/*     */   private static class BuilderImpl
/*     */     implements Builder
/*     */   {
/*     */     private final String type;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private String name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private String filename;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private Charset charset;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private Long size;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private ZonedDateTime creationDate;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private ZonedDateTime modificationDate;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private ZonedDateTime readDate;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public BuilderImpl(String type) {
/* 658 */       Assert.hasText(type, "'type' must not be not empty");
/* 659 */       this.type = type;
/*     */     }
/*     */ 
/*     */     
/*     */     public ContentDisposition.Builder name(String name) {
/* 664 */       this.name = name;
/* 665 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ContentDisposition.Builder filename(String filename) {
/* 670 */       Assert.hasText(filename, "No filename");
/* 671 */       this.filename = filename;
/* 672 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ContentDisposition.Builder filename(String filename, Charset charset) {
/* 677 */       Assert.hasText(filename, "No filename");
/* 678 */       this.filename = filename;
/* 679 */       this.charset = charset;
/* 680 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ContentDisposition.Builder size(Long size) {
/* 686 */       this.size = size;
/* 687 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ContentDisposition.Builder creationDate(ZonedDateTime creationDate) {
/* 693 */       this.creationDate = creationDate;
/* 694 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ContentDisposition.Builder modificationDate(ZonedDateTime modificationDate) {
/* 700 */       this.modificationDate = modificationDate;
/* 701 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ContentDisposition.Builder readDate(ZonedDateTime readDate) {
/* 707 */       this.readDate = readDate;
/* 708 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ContentDisposition build() {
/* 713 */       return new ContentDisposition(this.type, this.name, this.filename, this.charset, this.size, this.creationDate, this.modificationDate, this.readDate);
/*     */     }
/*     */   }
/*     */   
/*     */   public static interface Builder {
/*     */     Builder name(String param1String);
/*     */     
/*     */     Builder filename(String param1String);
/*     */     
/*     */     Builder filename(String param1String, Charset param1Charset);
/*     */     
/*     */     @Deprecated
/*     */     Builder size(Long param1Long);
/*     */     
/*     */     @Deprecated
/*     */     Builder creationDate(ZonedDateTime param1ZonedDateTime);
/*     */     
/*     */     @Deprecated
/*     */     Builder modificationDate(ZonedDateTime param1ZonedDateTime);
/*     */     
/*     */     @Deprecated
/*     */     Builder readDate(ZonedDateTime param1ZonedDateTime);
/*     */     
/*     */     ContentDisposition build();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/ContentDisposition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */