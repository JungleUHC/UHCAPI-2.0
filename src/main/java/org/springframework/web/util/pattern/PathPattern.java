/*     */ package org.springframework.web.util.pattern;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.StringJoiner;
/*     */ import org.springframework.http.server.PathContainer;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.MultiValueMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PathPattern
/*     */   implements Comparable<PathPattern>
/*     */ {
/*  82 */   private static final PathContainer EMPTY_PATH = PathContainer.parsePath("");
/*     */   public static final Comparator<PathPattern> SPECIFICITY_COMPARATOR;
/*     */   private final String patternString;
/*     */   private final PathPatternParser parser;
/*     */   private final PathContainer.Options pathOptions;
/*     */   private final boolean matchOptionalTrailingSeparator;
/*     */   private final boolean caseSensitive;
/*     */   @Nullable
/*     */   private final PathElement head;
/*     */   private int capturedVariableCount;
/*     */   private int normalizedLength;
/*     */   
/*     */   static {
/*  95 */     SPECIFICITY_COMPARATOR = Comparator.nullsLast(
/*     */         
/*  97 */         Comparator.<PathPattern>comparingInt(p -> p.isCatchAll() ? 1 : 0)
/*  98 */         .thenComparingInt(p -> p.isCatchAll() ? scoreByNormalizedLength(p) : 0)
/*  99 */         .thenComparingInt(PathPattern::getScore)
/* 100 */         .thenComparingInt(PathPattern::scoreByNormalizedLength));
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
/*     */   private boolean endsWithSeparatorWildcard = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int score;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean catchAll = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   PathPattern(String patternText, PathPatternParser parser, @Nullable PathElement head) {
/* 154 */     this.patternString = patternText;
/* 155 */     this.parser = parser;
/* 156 */     this.pathOptions = parser.getPathOptions();
/* 157 */     this.matchOptionalTrailingSeparator = parser.isMatchOptionalTrailingSeparator();
/* 158 */     this.caseSensitive = parser.isCaseSensitive();
/* 159 */     this.head = head;
/*     */ 
/*     */     
/* 162 */     PathElement elem = head;
/* 163 */     while (elem != null) {
/* 164 */       this.capturedVariableCount += elem.getCaptureCount();
/* 165 */       this.normalizedLength += elem.getNormalizedLength();
/* 166 */       this.score += elem.getScore();
/* 167 */       if (elem instanceof CaptureTheRestPathElement || elem instanceof WildcardTheRestPathElement) {
/* 168 */         this.catchAll = true;
/*     */       }
/* 170 */       if (elem instanceof SeparatorPathElement && elem.next instanceof WildcardPathElement && elem.next.next == null) {
/* 171 */         this.endsWithSeparatorWildcard = true;
/*     */       }
/* 173 */       elem = elem.next;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPatternString() {
/* 182 */     return this.patternString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasPatternSyntax() {
/* 192 */     return (this.score > 0 || this.catchAll || this.patternString.indexOf('?') != -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(PathContainer pathContainer) {
/* 201 */     if (this.head == null) {
/* 202 */       return (!hasLength(pathContainer) || (this.matchOptionalTrailingSeparator && 
/* 203 */         pathContainerIsJustSeparator(pathContainer)));
/*     */     }
/* 205 */     if (!hasLength(pathContainer)) {
/* 206 */       if (this.head instanceof WildcardTheRestPathElement || this.head instanceof CaptureTheRestPathElement) {
/* 207 */         pathContainer = EMPTY_PATH;
/*     */       } else {
/*     */         
/* 210 */         return false;
/*     */       } 
/*     */     }
/* 213 */     MatchingContext matchingContext = new MatchingContext(pathContainer, false);
/* 214 */     return this.head.matches(0, matchingContext);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public PathMatchInfo matchAndExtract(PathContainer pathContainer) {
/* 225 */     if (this.head == null) {
/* 226 */       return (hasLength(pathContainer) && (!this.matchOptionalTrailingSeparator || 
/* 227 */         !pathContainerIsJustSeparator(pathContainer))) ? null : PathMatchInfo
/* 228 */         .EMPTY;
/*     */     }
/* 230 */     if (!hasLength(pathContainer)) {
/* 231 */       if (this.head instanceof WildcardTheRestPathElement || this.head instanceof CaptureTheRestPathElement) {
/* 232 */         pathContainer = EMPTY_PATH;
/*     */       } else {
/*     */         
/* 235 */         return null;
/*     */       } 
/*     */     }
/* 238 */     MatchingContext matchingContext = new MatchingContext(pathContainer, true);
/* 239 */     return this.head.matches(0, matchingContext) ? matchingContext.getPathMatchResult() : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public PathRemainingMatchInfo matchStartOfPath(PathContainer pathContainer) {
/*     */     PathContainer pathMatched, pathRemaining;
/* 251 */     if (this.head == null) {
/* 252 */       return new PathRemainingMatchInfo(EMPTY_PATH, pathContainer);
/*     */     }
/* 254 */     if (!hasLength(pathContainer)) {
/* 255 */       return null;
/*     */     }
/*     */     
/* 258 */     MatchingContext matchingContext = new MatchingContext(pathContainer, true);
/* 259 */     matchingContext.setMatchAllowExtraPath();
/* 260 */     boolean matches = this.head.matches(0, matchingContext);
/* 261 */     if (!matches) {
/* 262 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 267 */     if (matchingContext.remainingPathIndex == pathContainer.elements().size()) {
/* 268 */       pathMatched = pathContainer;
/* 269 */       pathRemaining = EMPTY_PATH;
/*     */     } else {
/*     */       
/* 272 */       pathMatched = pathContainer.subPath(0, matchingContext.remainingPathIndex);
/* 273 */       pathRemaining = pathContainer.subPath(matchingContext.remainingPathIndex);
/*     */     } 
/* 275 */     return new PathRemainingMatchInfo(pathMatched, pathRemaining, matchingContext.getPathMatchResult());
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
/*     */   public PathContainer extractPathWithinPattern(PathContainer path) {
/* 299 */     List<PathContainer.Element> pathElements = path.elements();
/* 300 */     int pathElementsCount = pathElements.size();
/*     */     
/* 302 */     int startIndex = 0;
/*     */     
/* 304 */     PathElement elem = this.head;
/* 305 */     while (elem != null && 
/* 306 */       elem.getWildcardCount() == 0 && elem.getCaptureCount() == 0) {
/*     */ 
/*     */       
/* 309 */       elem = elem.next;
/* 310 */       startIndex++;
/*     */     } 
/* 312 */     if (elem == null)
/*     */     {
/* 314 */       return PathContainer.parsePath("");
/*     */     }
/*     */ 
/*     */     
/* 318 */     while (startIndex < pathElementsCount && pathElements.get(startIndex) instanceof PathContainer.Separator) {
/* 319 */       startIndex++;
/*     */     }
/*     */     
/* 322 */     int endIndex = pathElements.size();
/*     */     
/* 324 */     while (endIndex > 0 && pathElements.get(endIndex - 1) instanceof PathContainer.Separator) {
/* 325 */       endIndex--;
/*     */     }
/*     */     
/* 328 */     boolean multipleAdjacentSeparators = false;
/* 329 */     for (int i = startIndex; i < endIndex - 1; i++) {
/* 330 */       if (pathElements.get(i) instanceof PathContainer.Separator && pathElements.get(i + 1) instanceof PathContainer.Separator) {
/* 331 */         multipleAdjacentSeparators = true;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 336 */     PathContainer resultPath = null;
/* 337 */     if (multipleAdjacentSeparators) {
/*     */       
/* 339 */       StringBuilder sb = new StringBuilder();
/* 340 */       int j = startIndex;
/* 341 */       while (j < endIndex) {
/* 342 */         PathContainer.Element e = pathElements.get(j++);
/* 343 */         sb.append(e.value());
/* 344 */         if (e instanceof PathContainer.Separator) {
/* 345 */           while (j < endIndex && pathElements.get(j) instanceof PathContainer.Separator) {
/* 346 */             j++;
/*     */           }
/*     */         }
/*     */       } 
/* 350 */       resultPath = PathContainer.parsePath(sb.toString(), this.pathOptions);
/*     */     }
/* 352 */     else if (startIndex >= endIndex) {
/* 353 */       resultPath = PathContainer.parsePath("");
/*     */     } else {
/*     */       
/* 356 */       resultPath = path.subPath(startIndex, endIndex);
/*     */     } 
/* 358 */     return resultPath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(@Nullable PathPattern otherPattern) {
/* 368 */     int result = SPECIFICITY_COMPARATOR.compare(this, otherPattern);
/* 369 */     return (result == 0 && otherPattern != null) ? this.patternString
/* 370 */       .compareTo(otherPattern.patternString) : result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PathPattern combine(PathPattern pattern2string) {
/* 378 */     if (!StringUtils.hasLength(this.patternString)) {
/* 379 */       if (!StringUtils.hasLength(pattern2string.patternString)) {
/* 380 */         return this.parser.parse("");
/*     */       }
/*     */       
/* 383 */       return pattern2string;
/*     */     } 
/*     */     
/* 386 */     if (!StringUtils.hasLength(pattern2string.patternString)) {
/* 387 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 395 */     if (!this.patternString.equals(pattern2string.patternString) && this.capturedVariableCount == 0 && 
/* 396 */       matches(PathContainer.parsePath(pattern2string.patternString))) {
/* 397 */       return pattern2string;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 402 */     if (this.endsWithSeparatorWildcard) {
/* 403 */       return this.parser.parse(concat(this.patternString
/* 404 */             .substring(0, this.patternString.length() - 2), pattern2string.patternString));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 410 */     int starDotPos1 = this.patternString.indexOf("*.");
/* 411 */     if (this.capturedVariableCount != 0 || starDotPos1 == -1 || getSeparator() == '.') {
/* 412 */       return this.parser.parse(concat(this.patternString, pattern2string.patternString));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 417 */     String firstExtension = this.patternString.substring(starDotPos1 + 1);
/* 418 */     String p2string = pattern2string.patternString;
/* 419 */     int dotPos2 = p2string.indexOf('.');
/* 420 */     String file2 = (dotPos2 == -1) ? p2string : p2string.substring(0, dotPos2);
/* 421 */     String secondExtension = (dotPos2 == -1) ? "" : p2string.substring(dotPos2);
/* 422 */     boolean firstExtensionWild = (firstExtension.equals(".*") || firstExtension.isEmpty());
/* 423 */     boolean secondExtensionWild = (secondExtension.equals(".*") || secondExtension.isEmpty());
/* 424 */     if (!firstExtensionWild && !secondExtensionWild) {
/* 425 */       throw new IllegalArgumentException("Cannot combine patterns: " + this.patternString + " and " + pattern2string);
/*     */     }
/*     */     
/* 428 */     return this.parser.parse(file2 + (firstExtensionWild ? secondExtension : firstExtension));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 433 */     if (!(other instanceof PathPattern)) {
/* 434 */       return false;
/*     */     }
/* 436 */     PathPattern otherPattern = (PathPattern)other;
/* 437 */     return (this.patternString.equals(otherPattern.getPatternString()) && 
/* 438 */       getSeparator() == otherPattern.getSeparator() && this.caseSensitive == otherPattern.caseSensitive);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 444 */     return (this.patternString.hashCode() + getSeparator()) * 17 + (this.caseSensitive ? 1 : 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 449 */     return this.patternString;
/*     */   }
/*     */ 
/*     */   
/*     */   int getScore() {
/* 454 */     return this.score;
/*     */   }
/*     */   
/*     */   boolean isCatchAll() {
/* 458 */     return this.catchAll;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getNormalizedLength() {
/* 468 */     return this.normalizedLength;
/*     */   }
/*     */   
/*     */   char getSeparator() {
/* 472 */     return this.pathOptions.separator();
/*     */   }
/*     */   
/*     */   int getCapturedVariableCount() {
/* 476 */     return this.capturedVariableCount;
/*     */   }
/*     */   
/*     */   String toChainString() {
/* 480 */     StringJoiner stringJoiner = new StringJoiner(" ");
/* 481 */     PathElement pe = this.head;
/* 482 */     while (pe != null) {
/* 483 */       stringJoiner.add(pe.toString());
/* 484 */       pe = pe.next;
/*     */     } 
/* 486 */     return stringJoiner.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String computePatternString() {
/* 494 */     StringBuilder sb = new StringBuilder();
/* 495 */     PathElement pe = this.head;
/* 496 */     while (pe != null) {
/* 497 */       sb.append(pe.getChars());
/* 498 */       pe = pe.next;
/*     */     } 
/* 500 */     return sb.toString();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   PathElement getHeadSection() {
/* 505 */     return this.head;
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
/*     */   private String concat(String path1, String path2) {
/* 517 */     boolean path1EndsWithSeparator = (path1.charAt(path1.length() - 1) == getSeparator());
/* 518 */     boolean path2StartsWithSeparator = (path2.charAt(0) == getSeparator());
/* 519 */     if (path1EndsWithSeparator && path2StartsWithSeparator) {
/* 520 */       return path1 + path2.substring(1);
/*     */     }
/* 522 */     if (path1EndsWithSeparator || path2StartsWithSeparator) {
/* 523 */       return path1 + path2;
/*     */     }
/*     */     
/* 526 */     return path1 + getSeparator() + path2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean hasLength(@Nullable PathContainer container) {
/* 536 */     return (container != null && container.elements().size() > 0);
/*     */   }
/*     */   
/*     */   private static int scoreByNormalizedLength(PathPattern pattern) {
/* 540 */     return -pattern.getNormalizedLength();
/*     */   }
/*     */   
/*     */   private boolean pathContainerIsJustSeparator(PathContainer pathContainer) {
/* 544 */     return (pathContainer.value().length() == 1 && pathContainer
/* 545 */       .value().charAt(0) == getSeparator());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class PathMatchInfo
/*     */   {
/* 555 */     private static final PathMatchInfo EMPTY = new PathMatchInfo(Collections.emptyMap(), Collections.emptyMap());
/*     */     
/*     */     private final Map<String, String> uriVariables;
/*     */     
/*     */     private final Map<String, MultiValueMap<String, String>> matrixVariables;
/*     */     
/*     */     PathMatchInfo(Map<String, String> uriVars, @Nullable Map<String, MultiValueMap<String, String>> matrixVars) {
/* 562 */       this.uriVariables = Collections.unmodifiableMap(uriVars);
/* 563 */       this
/* 564 */         .matrixVariables = (matrixVars != null) ? Collections.<String, MultiValueMap<String, String>>unmodifiableMap(matrixVars) : Collections.<String, MultiValueMap<String, String>>emptyMap();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Map<String, String> getUriVariables() {
/* 571 */       return this.uriVariables;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Map<String, MultiValueMap<String, String>> getMatrixVariables() {
/* 579 */       return this.matrixVariables;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 584 */       return "PathMatchInfo[uriVariables=" + this.uriVariables + ", matrixVariables=" + this.matrixVariables + "]";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class PathRemainingMatchInfo
/*     */   {
/*     */     private final PathContainer pathMatched;
/*     */ 
/*     */ 
/*     */     
/*     */     private final PathContainer pathRemaining;
/*     */ 
/*     */     
/*     */     private final PathPattern.PathMatchInfo pathMatchInfo;
/*     */ 
/*     */ 
/*     */     
/*     */     PathRemainingMatchInfo(PathContainer pathMatched, PathContainer pathRemaining) {
/* 605 */       this(pathMatched, pathRemaining, PathPattern.PathMatchInfo.EMPTY);
/*     */     }
/*     */ 
/*     */     
/*     */     PathRemainingMatchInfo(PathContainer pathMatched, PathContainer pathRemaining, PathPattern.PathMatchInfo pathMatchInfo) {
/* 610 */       this.pathRemaining = pathRemaining;
/* 611 */       this.pathMatched = pathMatched;
/* 612 */       this.pathMatchInfo = pathMatchInfo;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public PathContainer getPathMatched() {
/* 620 */       return this.pathMatched;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public PathContainer getPathRemaining() {
/* 627 */       return this.pathRemaining;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Map<String, String> getUriVariables() {
/* 635 */       return this.pathMatchInfo.getUriVariables();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Map<String, MultiValueMap<String, String>> getMatrixVariables() {
/* 642 */       return this.pathMatchInfo.getMatrixVariables();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   class MatchingContext
/*     */   {
/*     */     final PathContainer candidate;
/*     */ 
/*     */     
/*     */     final List<PathContainer.Element> pathElements;
/*     */ 
/*     */     
/*     */     final int pathLength;
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private Map<String, String> extractedUriVariables;
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private Map<String, MultiValueMap<String, String>> extractedMatrixVariables;
/*     */ 
/*     */     
/*     */     boolean extractingVariables;
/*     */ 
/*     */     
/*     */     boolean determineRemainingPath = false;
/*     */     
/*     */     int remainingPathIndex;
/*     */ 
/*     */     
/*     */     public MatchingContext(PathContainer pathContainer, boolean extractVariables) {
/* 676 */       this.candidate = pathContainer;
/* 677 */       this.pathElements = pathContainer.elements();
/* 678 */       this.pathLength = this.pathElements.size();
/* 679 */       this.extractingVariables = extractVariables;
/*     */     }
/*     */     
/*     */     public void setMatchAllowExtraPath() {
/* 683 */       this.determineRemainingPath = true;
/*     */     }
/*     */     
/*     */     public boolean isMatchOptionalTrailingSeparator() {
/* 687 */       return PathPattern.this.matchOptionalTrailingSeparator;
/*     */     }
/*     */     
/*     */     public void set(String key, String value, MultiValueMap<String, String> parameters) {
/* 691 */       if (this.extractedUriVariables == null) {
/* 692 */         this.extractedUriVariables = new HashMap<>();
/*     */       }
/* 694 */       this.extractedUriVariables.put(key, value);
/*     */       
/* 696 */       if (!parameters.isEmpty()) {
/* 697 */         if (this.extractedMatrixVariables == null) {
/* 698 */           this.extractedMatrixVariables = new HashMap<>();
/*     */         }
/* 700 */         this.extractedMatrixVariables.put(key, CollectionUtils.unmodifiableMultiValueMap(parameters));
/*     */       } 
/*     */     }
/*     */     
/*     */     public PathPattern.PathMatchInfo getPathMatchResult() {
/* 705 */       if (this.extractedUriVariables == null) {
/* 706 */         return PathPattern.PathMatchInfo.EMPTY;
/*     */       }
/*     */       
/* 709 */       return new PathPattern.PathMatchInfo(this.extractedUriVariables, this.extractedMatrixVariables);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     boolean isSeparator(int pathIndex) {
/* 719 */       return this.pathElements.get(pathIndex) instanceof PathContainer.Separator;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     String pathElementValue(int pathIndex) {
/* 728 */       PathContainer.Element element = (pathIndex < this.pathLength) ? this.pathElements.get(pathIndex) : null;
/* 729 */       if (element instanceof PathContainer.PathSegment) {
/* 730 */         return ((PathContainer.PathSegment)element).valueToMatch();
/*     */       }
/* 732 */       return "";
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/pattern/PathPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */