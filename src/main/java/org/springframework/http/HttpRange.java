/*     */ package org.springframework.http;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.StringJoiner;
/*     */ import org.springframework.core.io.InputStreamResource;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.core.io.support.ResourceRegion;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ import org.springframework.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class HttpRange
/*     */ {
/*     */   private static final int MAX_RANGES = 100;
/*     */   private static final String BYTE_RANGE_PREFIX = "bytes=";
/*     */   
/*     */   public ResourceRegion toResourceRegion(Resource resource) {
/*  63 */     Assert.isTrue((resource.getClass() != InputStreamResource.class), "Cannot convert an InputStreamResource to a ResourceRegion");
/*     */     
/*  65 */     long contentLength = getLengthFor(resource);
/*  66 */     long start = getRangeStart(contentLength);
/*  67 */     long end = getRangeEnd(contentLength);
/*  68 */     Assert.isTrue((start < contentLength), "'position' exceeds the resource length " + contentLength);
/*  69 */     return new ResourceRegion(resource, start, end - start + 1L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract long getRangeStart(long paramLong);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract long getRangeEnd(long paramLong);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpRange createByteRange(long firstBytePos) {
/*  94 */     return new ByteRange(firstBytePos, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpRange createByteRange(long firstBytePos, long lastBytePos) {
/* 105 */     return new ByteRange(firstBytePos, Long.valueOf(lastBytePos));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpRange createSuffixRange(long suffixLength) {
/* 115 */     return new SuffixByteRange(suffixLength);
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
/*     */   public static List<HttpRange> parseRanges(@Nullable String ranges) {
/* 127 */     if (!StringUtils.hasLength(ranges)) {
/* 128 */       return Collections.emptyList();
/*     */     }
/* 130 */     if (!ranges.startsWith("bytes=")) {
/* 131 */       throw new IllegalArgumentException("Range '" + ranges + "' does not start with 'bytes='");
/*     */     }
/* 133 */     ranges = ranges.substring("bytes=".length());
/*     */     
/* 135 */     String[] tokens = StringUtils.tokenizeToStringArray(ranges, ",");
/* 136 */     if (tokens.length > 100) {
/* 137 */       throw new IllegalArgumentException("Too many ranges: " + tokens.length);
/*     */     }
/* 139 */     List<HttpRange> result = new ArrayList<>(tokens.length);
/* 140 */     for (String token : tokens) {
/* 141 */       result.add(parseRange(token));
/*     */     }
/* 143 */     return result;
/*     */   }
/*     */   
/*     */   private static HttpRange parseRange(String range) {
/* 147 */     Assert.hasLength(range, "Range String must not be empty");
/* 148 */     int dashIdx = range.indexOf('-');
/* 149 */     if (dashIdx > 0) {
/* 150 */       long firstPos = Long.parseLong(range.substring(0, dashIdx));
/* 151 */       if (dashIdx < range.length() - 1) {
/* 152 */         Long lastPos = Long.valueOf(Long.parseLong(range.substring(dashIdx + 1)));
/* 153 */         return new ByteRange(firstPos, lastPos);
/*     */       } 
/*     */       
/* 156 */       return new ByteRange(firstPos, null);
/*     */     } 
/*     */     
/* 159 */     if (dashIdx == 0) {
/* 160 */       long suffixLength = Long.parseLong(range.substring(1));
/* 161 */       return new SuffixByteRange(suffixLength);
/*     */     } 
/*     */     
/* 164 */     throw new IllegalArgumentException("Range '" + range + "' does not contain \"-\"");
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
/*     */   public static List<ResourceRegion> toResourceRegions(List<HttpRange> ranges, Resource resource) {
/* 178 */     if (CollectionUtils.isEmpty(ranges)) {
/* 179 */       return Collections.emptyList();
/*     */     }
/* 181 */     List<ResourceRegion> regions = new ArrayList<>(ranges.size());
/* 182 */     for (HttpRange range : ranges) {
/* 183 */       regions.add(range.toResourceRegion(resource));
/*     */     }
/* 185 */     if (ranges.size() > 1) {
/* 186 */       long length = getLengthFor(resource);
/* 187 */       long total = 0L;
/* 188 */       for (ResourceRegion region : regions) {
/* 189 */         total += region.getCount();
/*     */       }
/* 191 */       if (total >= length) {
/* 192 */         throw new IllegalArgumentException("The sum of all ranges (" + total + ") should be less than the resource length (" + length + ")");
/*     */       }
/*     */     } 
/*     */     
/* 196 */     return regions;
/*     */   }
/*     */   
/*     */   private static long getLengthFor(Resource resource) {
/*     */     try {
/* 201 */       long contentLength = resource.contentLength();
/* 202 */       Assert.isTrue((contentLength > 0L), "Resource content length should be > 0");
/* 203 */       return contentLength;
/*     */     }
/* 205 */     catch (IOException ex) {
/* 206 */       throw new IllegalArgumentException("Failed to obtain Resource content length", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(Collection<HttpRange> ranges) {
/* 217 */     Assert.notEmpty(ranges, "Ranges Collection must not be empty");
/* 218 */     StringJoiner builder = new StringJoiner(", ", "bytes=", "");
/* 219 */     for (HttpRange range : ranges) {
/* 220 */       builder.add(range.toString());
/*     */     }
/* 222 */     return builder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class ByteRange
/*     */     extends HttpRange
/*     */   {
/*     */     private final long firstPos;
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private final Long lastPos;
/*     */ 
/*     */ 
/*     */     
/*     */     public ByteRange(long firstPos, @Nullable Long lastPos) {
/* 240 */       assertPositions(firstPos, lastPos);
/* 241 */       this.firstPos = firstPos;
/* 242 */       this.lastPos = lastPos;
/*     */     }
/*     */     
/*     */     private void assertPositions(long firstBytePos, @Nullable Long lastBytePos) {
/* 246 */       if (firstBytePos < 0L) {
/* 247 */         throw new IllegalArgumentException("Invalid first byte position: " + firstBytePos);
/*     */       }
/* 249 */       if (lastBytePos != null && lastBytePos.longValue() < firstBytePos) {
/* 250 */         throw new IllegalArgumentException("firstBytePosition=" + firstBytePos + " should be less then or equal to lastBytePosition=" + lastBytePos);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long getRangeStart(long length) {
/* 257 */       return this.firstPos;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getRangeEnd(long length) {
/* 262 */       if (this.lastPos != null && this.lastPos.longValue() < length) {
/* 263 */         return this.lastPos.longValue();
/*     */       }
/*     */       
/* 266 */       return length - 1L;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object other) {
/* 272 */       if (this == other) {
/* 273 */         return true;
/*     */       }
/* 275 */       if (!(other instanceof ByteRange)) {
/* 276 */         return false;
/*     */       }
/* 278 */       ByteRange otherRange = (ByteRange)other;
/* 279 */       return (this.firstPos == otherRange.firstPos && 
/* 280 */         ObjectUtils.nullSafeEquals(this.lastPos, otherRange.lastPos));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 285 */       return ObjectUtils.nullSafeHashCode(Long.valueOf(this.firstPos)) * 31 + 
/* 286 */         ObjectUtils.nullSafeHashCode(this.lastPos);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 291 */       StringBuilder builder = new StringBuilder();
/* 292 */       builder.append(this.firstPos);
/* 293 */       builder.append('-');
/* 294 */       if (this.lastPos != null) {
/* 295 */         builder.append(this.lastPos);
/*     */       }
/* 297 */       return builder.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class SuffixByteRange
/*     */     extends HttpRange
/*     */   {
/*     */     private final long suffixLength;
/*     */ 
/*     */ 
/*     */     
/*     */     public SuffixByteRange(long suffixLength) {
/* 312 */       if (suffixLength < 0L) {
/* 313 */         throw new IllegalArgumentException("Invalid suffix length: " + suffixLength);
/*     */       }
/* 315 */       this.suffixLength = suffixLength;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getRangeStart(long length) {
/* 320 */       if (this.suffixLength < length) {
/* 321 */         return length - this.suffixLength;
/*     */       }
/*     */       
/* 324 */       return 0L;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long getRangeEnd(long length) {
/* 330 */       return length - 1L;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object other) {
/* 335 */       if (this == other) {
/* 336 */         return true;
/*     */       }
/* 338 */       if (!(other instanceof SuffixByteRange)) {
/* 339 */         return false;
/*     */       }
/* 341 */       SuffixByteRange otherRange = (SuffixByteRange)other;
/* 342 */       return (this.suffixLength == otherRange.suffixLength);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 347 */       return Long.hashCode(this.suffixLength);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 352 */       return "-" + this.suffixLength;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/HttpRange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */