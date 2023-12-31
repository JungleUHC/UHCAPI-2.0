/*      */ package org.springframework.util;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.nio.charset.Charset;
/*      */ import java.util.ArrayDeque;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Deque;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.StringJoiner;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TimeZone;
/*      */ import org.springframework.lang.Nullable;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class StringUtils
/*      */ {
/*   64 */   private static final String[] EMPTY_STRING_ARRAY = new String[0];
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final String FOLDER_SEPARATOR = "/";
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final char FOLDER_SEPARATOR_CHAR = '/';
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final String WINDOWS_FOLDER_SEPARATOR = "\\";
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final String TOP_PATH = "..";
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final String CURRENT_PATH = ".";
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final char EXTENSION_SEPARATOR = '.';
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static boolean isEmpty(@Nullable Object str) {
/*  101 */     return (str == null || "".equals(str));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean hasLength(@Nullable CharSequence str) {
/*  121 */     return (str != null && str.length() > 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean hasLength(@Nullable String str) {
/*  134 */     return (str != null && !str.isEmpty());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean hasText(@Nullable CharSequence str) {
/*  157 */     return (str != null && str.length() > 0 && containsText(str));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean hasText(@Nullable String str) {
/*  173 */     return (str != null && !str.isEmpty() && containsText(str));
/*      */   }
/*      */   
/*      */   private static boolean containsText(CharSequence str) {
/*  177 */     int strLen = str.length();
/*  178 */     for (int i = 0; i < strLen; i++) {
/*  179 */       if (!Character.isWhitespace(str.charAt(i))) {
/*  180 */         return true;
/*      */       }
/*      */     } 
/*  183 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean containsWhitespace(@Nullable CharSequence str) {
/*  194 */     if (!hasLength(str)) {
/*  195 */       return false;
/*      */     }
/*      */     
/*  198 */     int strLen = str.length();
/*  199 */     for (int i = 0; i < strLen; i++) {
/*  200 */       if (Character.isWhitespace(str.charAt(i))) {
/*  201 */         return true;
/*      */       }
/*      */     } 
/*  204 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean containsWhitespace(@Nullable String str) {
/*  215 */     return containsWhitespace(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String trimWhitespace(String str) {
/*  225 */     if (!hasLength(str)) {
/*  226 */       return str;
/*      */     }
/*      */     
/*  229 */     int beginIndex = 0;
/*  230 */     int endIndex = str.length() - 1;
/*      */     
/*  232 */     while (beginIndex <= endIndex && Character.isWhitespace(str.charAt(beginIndex))) {
/*  233 */       beginIndex++;
/*      */     }
/*      */     
/*  236 */     while (endIndex > beginIndex && Character.isWhitespace(str.charAt(endIndex))) {
/*  237 */       endIndex--;
/*      */     }
/*      */     
/*  240 */     return str.substring(beginIndex, endIndex + 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String trimAllWhitespace(String str) {
/*  251 */     if (!hasLength(str)) {
/*  252 */       return str;
/*      */     }
/*      */     
/*  255 */     int len = str.length();
/*  256 */     StringBuilder sb = new StringBuilder(str.length());
/*  257 */     for (int i = 0; i < len; i++) {
/*  258 */       char c = str.charAt(i);
/*  259 */       if (!Character.isWhitespace(c)) {
/*  260 */         sb.append(c);
/*      */       }
/*      */     } 
/*  263 */     return sb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String trimLeadingWhitespace(String str) {
/*  273 */     if (!hasLength(str)) {
/*  274 */       return str;
/*      */     }
/*      */     
/*  277 */     int beginIdx = 0;
/*  278 */     while (beginIdx < str.length() && Character.isWhitespace(str.charAt(beginIdx))) {
/*  279 */       beginIdx++;
/*      */     }
/*  281 */     return str.substring(beginIdx);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String trimTrailingWhitespace(String str) {
/*  291 */     if (!hasLength(str)) {
/*  292 */       return str;
/*      */     }
/*      */     
/*  295 */     int endIdx = str.length() - 1;
/*  296 */     while (endIdx >= 0 && Character.isWhitespace(str.charAt(endIdx))) {
/*  297 */       endIdx--;
/*      */     }
/*  299 */     return str.substring(0, endIdx + 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String trimLeadingCharacter(String str, char leadingCharacter) {
/*  309 */     if (!hasLength(str)) {
/*  310 */       return str;
/*      */     }
/*      */     
/*  313 */     int beginIdx = 0;
/*  314 */     while (beginIdx < str.length() && leadingCharacter == str.charAt(beginIdx)) {
/*  315 */       beginIdx++;
/*      */     }
/*  317 */     return str.substring(beginIdx);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String trimTrailingCharacter(String str, char trailingCharacter) {
/*  327 */     if (!hasLength(str)) {
/*  328 */       return str;
/*      */     }
/*      */     
/*  331 */     int endIdx = str.length() - 1;
/*  332 */     while (endIdx >= 0 && trailingCharacter == str.charAt(endIdx)) {
/*  333 */       endIdx--;
/*      */     }
/*  335 */     return str.substring(0, endIdx + 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean matchesCharacter(@Nullable String str, char singleCharacter) {
/*  345 */     return (str != null && str.length() == 1 && str.charAt(0) == singleCharacter);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean startsWithIgnoreCase(@Nullable String str, @Nullable String prefix) {
/*  356 */     return (str != null && prefix != null && str.length() >= prefix.length() && str
/*  357 */       .regionMatches(true, 0, prefix, 0, prefix.length()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean endsWithIgnoreCase(@Nullable String str, @Nullable String suffix) {
/*  368 */     return (str != null && suffix != null && str.length() >= suffix.length() && str
/*  369 */       .regionMatches(true, str.length() - suffix.length(), suffix, 0, suffix.length()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
/*  380 */     if (index + substring.length() > str.length()) {
/*  381 */       return false;
/*      */     }
/*  383 */     for (int i = 0; i < substring.length(); i++) {
/*  384 */       if (str.charAt(index + i) != substring.charAt(i)) {
/*  385 */         return false;
/*      */       }
/*      */     } 
/*  388 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int countOccurrencesOf(String str, String sub) {
/*  397 */     if (!hasLength(str) || !hasLength(sub)) {
/*  398 */       return 0;
/*      */     }
/*      */     
/*  401 */     int count = 0;
/*  402 */     int pos = 0;
/*      */     int idx;
/*  404 */     while ((idx = str.indexOf(sub, pos)) != -1) {
/*  405 */       count++;
/*  406 */       pos = idx + sub.length();
/*      */     } 
/*  408 */     return count;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String replace(String inString, String oldPattern, @Nullable String newPattern) {
/*  419 */     if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
/*  420 */       return inString;
/*      */     }
/*  422 */     int index = inString.indexOf(oldPattern);
/*  423 */     if (index == -1)
/*      */     {
/*  425 */       return inString;
/*      */     }
/*      */     
/*  428 */     int capacity = inString.length();
/*  429 */     if (newPattern.length() > oldPattern.length()) {
/*  430 */       capacity += 16;
/*      */     }
/*  432 */     StringBuilder sb = new StringBuilder(capacity);
/*      */     
/*  434 */     int pos = 0;
/*  435 */     int patLen = oldPattern.length();
/*  436 */     while (index >= 0) {
/*  437 */       sb.append(inString, pos, index);
/*  438 */       sb.append(newPattern);
/*  439 */       pos = index + patLen;
/*  440 */       index = inString.indexOf(oldPattern, pos);
/*      */     } 
/*      */ 
/*      */     
/*  444 */     sb.append(inString, pos, inString.length());
/*  445 */     return sb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String delete(String inString, String pattern) {
/*  455 */     return replace(inString, pattern, "");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String deleteAny(String inString, @Nullable String charsToDelete) {
/*  466 */     if (!hasLength(inString) || !hasLength(charsToDelete)) {
/*  467 */       return inString;
/*      */     }
/*      */     
/*  470 */     int lastCharIndex = 0;
/*  471 */     char[] result = new char[inString.length()];
/*  472 */     for (int i = 0; i < inString.length(); i++) {
/*  473 */       char c = inString.charAt(i);
/*  474 */       if (charsToDelete.indexOf(c) == -1) {
/*  475 */         result[lastCharIndex++] = c;
/*      */       }
/*      */     } 
/*  478 */     if (lastCharIndex == inString.length()) {
/*  479 */       return inString;
/*      */     }
/*  481 */     return new String(result, 0, lastCharIndex);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public static String quote(@Nullable String str) {
/*  496 */     return (str != null) ? ("'" + str + "'") : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public static Object quoteIfString(@Nullable Object obj) {
/*  508 */     return (obj instanceof String) ? quote((String)obj) : obj;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String unqualify(String qualifiedName) {
/*  517 */     return unqualify(qualifiedName, '.');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String unqualify(String qualifiedName, char separator) {
/*  527 */     return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String capitalize(String str) {
/*  538 */     return changeFirstCharacterCase(str, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String uncapitalize(String str) {
/*  549 */     return changeFirstCharacterCase(str, false);
/*      */   }
/*      */   private static String changeFirstCharacterCase(String str, boolean capitalize) {
/*      */     char updatedChar;
/*  553 */     if (!hasLength(str)) {
/*  554 */       return str;
/*      */     }
/*      */     
/*  557 */     char baseChar = str.charAt(0);
/*      */     
/*  559 */     if (capitalize) {
/*  560 */       updatedChar = Character.toUpperCase(baseChar);
/*      */     } else {
/*      */       
/*  563 */       updatedChar = Character.toLowerCase(baseChar);
/*      */     } 
/*  565 */     if (baseChar == updatedChar) {
/*  566 */       return str;
/*      */     }
/*      */     
/*  569 */     char[] chars = str.toCharArray();
/*  570 */     chars[0] = updatedChar;
/*  571 */     return new String(chars);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public static String getFilename(@Nullable String path) {
/*  582 */     if (path == null) {
/*  583 */       return null;
/*      */     }
/*      */     
/*  586 */     int separatorIndex = path.lastIndexOf('/');
/*  587 */     return (separatorIndex != -1) ? path.substring(separatorIndex + 1) : path;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public static String getFilenameExtension(@Nullable String path) {
/*  598 */     if (path == null) {
/*  599 */       return null;
/*      */     }
/*      */     
/*  602 */     int extIndex = path.lastIndexOf('.');
/*  603 */     if (extIndex == -1) {
/*  604 */       return null;
/*      */     }
/*      */     
/*  607 */     int folderIndex = path.lastIndexOf('/');
/*  608 */     if (folderIndex > extIndex) {
/*  609 */       return null;
/*      */     }
/*      */     
/*  612 */     return path.substring(extIndex + 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String stripFilenameExtension(String path) {
/*  622 */     int extIndex = path.lastIndexOf('.');
/*  623 */     if (extIndex == -1) {
/*  624 */       return path;
/*      */     }
/*      */     
/*  627 */     int folderIndex = path.lastIndexOf('/');
/*  628 */     if (folderIndex > extIndex) {
/*  629 */       return path;
/*      */     }
/*      */     
/*  632 */     return path.substring(0, extIndex);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String applyRelativePath(String path, String relativePath) {
/*  644 */     int separatorIndex = path.lastIndexOf('/');
/*  645 */     if (separatorIndex != -1) {
/*  646 */       String newPath = path.substring(0, separatorIndex);
/*  647 */       if (!relativePath.startsWith("/")) {
/*  648 */         newPath = newPath + '/';
/*      */       }
/*  650 */       return newPath + relativePath;
/*      */     } 
/*      */     
/*  653 */     return relativePath;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String cleanPath(String path) {
/*  669 */     if (!hasLength(path)) {
/*  670 */       return path;
/*      */     }
/*      */     
/*  673 */     String normalizedPath = replace(path, "\\", "/");
/*  674 */     String pathToUse = normalizedPath;
/*      */ 
/*      */     
/*  677 */     if (pathToUse.indexOf('.') == -1) {
/*  678 */       return pathToUse;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  685 */     int prefixIndex = pathToUse.indexOf(':');
/*  686 */     String prefix = "";
/*  687 */     if (prefixIndex != -1) {
/*  688 */       prefix = pathToUse.substring(0, prefixIndex + 1);
/*  689 */       if (prefix.contains("/")) {
/*  690 */         prefix = "";
/*      */       } else {
/*      */         
/*  693 */         pathToUse = pathToUse.substring(prefixIndex + 1);
/*      */       } 
/*      */     } 
/*  696 */     if (pathToUse.startsWith("/")) {
/*  697 */       prefix = prefix + "/";
/*  698 */       pathToUse = pathToUse.substring(1);
/*      */     } 
/*      */     
/*  701 */     String[] pathArray = delimitedListToStringArray(pathToUse, "/");
/*      */     
/*  703 */     Deque<String> pathElements = new ArrayDeque<>(pathArray.length);
/*  704 */     int tops = 0;
/*      */     int i;
/*  706 */     for (i = pathArray.length - 1; i >= 0; i--) {
/*  707 */       String element = pathArray[i];
/*  708 */       if (!".".equals(element))
/*      */       {
/*      */         
/*  711 */         if ("..".equals(element)) {
/*      */           
/*  713 */           tops++;
/*      */         
/*      */         }
/*  716 */         else if (tops > 0) {
/*      */           
/*  718 */           tops--;
/*      */         }
/*      */         else {
/*      */           
/*  722 */           pathElements.addFirst(element);
/*      */         } 
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  728 */     if (pathArray.length == pathElements.size()) {
/*  729 */       return normalizedPath;
/*      */     }
/*      */     
/*  732 */     for (i = 0; i < tops; i++) {
/*  733 */       pathElements.addFirst("..");
/*      */     }
/*      */     
/*  736 */     if (pathElements.size() == 1 && ((String)pathElements.getLast()).isEmpty() && !prefix.endsWith("/")) {
/*  737 */       pathElements.addFirst(".");
/*      */     }
/*      */     
/*  740 */     String joined = collectionToDelimitedString(pathElements, "/");
/*      */     
/*  742 */     return prefix.isEmpty() ? joined : (prefix + joined);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean pathEquals(String path1, String path2) {
/*  752 */     return cleanPath(path1).equals(cleanPath(path2));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String uriDecode(String source, Charset charset) {
/*  771 */     int length = source.length();
/*  772 */     if (length == 0) {
/*  773 */       return source;
/*      */     }
/*  775 */     Assert.notNull(charset, "Charset must not be null");
/*      */     
/*  777 */     ByteArrayOutputStream baos = new ByteArrayOutputStream(length);
/*  778 */     boolean changed = false;
/*  779 */     for (int i = 0; i < length; i++) {
/*  780 */       int ch = source.charAt(i);
/*  781 */       if (ch == 37) {
/*  782 */         if (i + 2 < length) {
/*  783 */           char hex1 = source.charAt(i + 1);
/*  784 */           char hex2 = source.charAt(i + 2);
/*  785 */           int u = Character.digit(hex1, 16);
/*  786 */           int l = Character.digit(hex2, 16);
/*  787 */           if (u == -1 || l == -1) {
/*  788 */             throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
/*      */           }
/*  790 */           baos.write((char)((u << 4) + l));
/*  791 */           i += 2;
/*  792 */           changed = true;
/*      */         } else {
/*      */           
/*  795 */           throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
/*      */         } 
/*      */       } else {
/*      */         
/*  799 */         baos.write(ch);
/*      */       } 
/*      */     } 
/*  802 */     return changed ? StreamUtils.copyToString(baos, charset) : source;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public static Locale parseLocale(String localeValue) {
/*  820 */     String[] tokens = tokenizeLocaleSource(localeValue);
/*  821 */     if (tokens.length == 1) {
/*  822 */       validateLocalePart(localeValue);
/*  823 */       Locale resolved = Locale.forLanguageTag(localeValue);
/*  824 */       if (resolved.getLanguage().length() > 0) {
/*  825 */         return resolved;
/*      */       }
/*      */     } 
/*  828 */     return parseLocaleTokens(localeValue, tokens);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public static Locale parseLocaleString(String localeString) {
/*  847 */     return parseLocaleTokens(localeString, tokenizeLocaleSource(localeString));
/*      */   }
/*      */   
/*      */   private static String[] tokenizeLocaleSource(String localeSource) {
/*  851 */     return tokenizeToStringArray(localeSource, "_ ", false, false);
/*      */   }
/*      */   
/*      */   @Nullable
/*      */   private static Locale parseLocaleTokens(String localeString, String[] tokens) {
/*  856 */     String language = (tokens.length > 0) ? tokens[0] : "";
/*  857 */     String country = (tokens.length > 1) ? tokens[1] : "";
/*  858 */     validateLocalePart(language);
/*  859 */     validateLocalePart(country);
/*      */     
/*  861 */     String variant = "";
/*  862 */     if (tokens.length > 2) {
/*      */ 
/*      */       
/*  865 */       int endIndexOfCountryCode = localeString.indexOf(country, language.length()) + country.length();
/*      */       
/*  867 */       variant = trimLeadingWhitespace(localeString.substring(endIndexOfCountryCode));
/*  868 */       if (variant.startsWith("_")) {
/*  869 */         variant = trimLeadingCharacter(variant, '_');
/*      */       }
/*      */     } 
/*      */     
/*  873 */     if (variant.isEmpty() && country.startsWith("#")) {
/*  874 */       variant = country;
/*  875 */       country = "";
/*      */     } 
/*      */     
/*  878 */     return (language.length() > 0) ? new Locale(language, country, variant) : null;
/*      */   }
/*      */   
/*      */   private static void validateLocalePart(String localePart) {
/*  882 */     for (int i = 0; i < localePart.length(); i++) {
/*  883 */       char ch = localePart.charAt(i);
/*  884 */       if (ch != ' ' && ch != '_' && ch != '-' && ch != '#' && !Character.isLetterOrDigit(ch)) {
/*  885 */         throw new IllegalArgumentException("Locale part \"" + localePart + "\" contains invalid characters");
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String toLanguageTag(Locale locale) {
/*  900 */     return locale.getLanguage() + (hasText(locale.getCountry()) ? ("-" + locale.getCountry()) : "");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static TimeZone parseTimeZoneString(String timeZoneString) {
/*  911 */     TimeZone timeZone = TimeZone.getTimeZone(timeZoneString);
/*  912 */     if ("GMT".equals(timeZone.getID()) && !timeZoneString.startsWith("GMT"))
/*      */     {
/*  914 */       throw new IllegalArgumentException("Invalid time zone specification '" + timeZoneString + "'");
/*      */     }
/*  916 */     return timeZone;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] toStringArray(@Nullable Collection<String> collection) {
/*  932 */     return !CollectionUtils.isEmpty(collection) ? collection.<String>toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] toStringArray(@Nullable Enumeration<String> enumeration) {
/*  943 */     return (enumeration != null) ? toStringArray(Collections.list(enumeration)) : EMPTY_STRING_ARRAY;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] addStringToArray(@Nullable String[] array, String str) {
/*  955 */     if (ObjectUtils.isEmpty((Object[])array)) {
/*  956 */       return new String[] { str };
/*      */     }
/*      */     
/*  959 */     String[] newArr = new String[array.length + 1];
/*  960 */     System.arraycopy(array, 0, newArr, 0, array.length);
/*  961 */     newArr[array.length] = str;
/*  962 */     return newArr;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public static String[] concatenateStringArrays(@Nullable String[] array1, @Nullable String[] array2) {
/*  975 */     if (ObjectUtils.isEmpty((Object[])array1)) {
/*  976 */       return array2;
/*      */     }
/*  978 */     if (ObjectUtils.isEmpty((Object[])array2)) {
/*  979 */       return array1;
/*      */     }
/*      */     
/*  982 */     String[] newArr = new String[array1.length + array2.length];
/*  983 */     System.arraycopy(array1, 0, newArr, 0, array1.length);
/*  984 */     System.arraycopy(array2, 0, newArr, array1.length, array2.length);
/*  985 */     return newArr;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   @Nullable
/*      */   public static String[] mergeStringArrays(@Nullable String[] array1, @Nullable String[] array2) {
/* 1003 */     if (ObjectUtils.isEmpty((Object[])array1)) {
/* 1004 */       return array2;
/*      */     }
/* 1006 */     if (ObjectUtils.isEmpty((Object[])array2)) {
/* 1007 */       return array1;
/*      */     }
/*      */     
/* 1010 */     List<String> result = new ArrayList<>(Arrays.asList(array1));
/* 1011 */     for (String str : array2) {
/* 1012 */       if (!result.contains(str)) {
/* 1013 */         result.add(str);
/*      */       }
/*      */     } 
/* 1016 */     return toStringArray(result);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] sortStringArray(String[] array) {
/* 1025 */     if (ObjectUtils.isEmpty((Object[])array)) {
/* 1026 */       return array;
/*      */     }
/*      */     
/* 1029 */     Arrays.sort((Object[])array);
/* 1030 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] trimArrayElements(String[] array) {
/* 1040 */     if (ObjectUtils.isEmpty((Object[])array)) {
/* 1041 */       return array;
/*      */     }
/*      */     
/* 1044 */     String[] result = new String[array.length];
/* 1045 */     for (int i = 0; i < array.length; i++) {
/* 1046 */       String element = array[i];
/* 1047 */       result[i] = (element != null) ? element.trim() : null;
/*      */     } 
/* 1049 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] removeDuplicateStrings(String[] array) {
/* 1059 */     if (ObjectUtils.isEmpty((Object[])array)) {
/* 1060 */       return array;
/*      */     }
/*      */     
/* 1063 */     Set<String> set = new LinkedHashSet<>(Arrays.asList(array));
/* 1064 */     return toStringArray(set);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public static String[] split(@Nullable String toSplit, @Nullable String delimiter) {
/* 1078 */     if (!hasLength(toSplit) || !hasLength(delimiter)) {
/* 1079 */       return null;
/*      */     }
/* 1081 */     int offset = toSplit.indexOf(delimiter);
/* 1082 */     if (offset < 0) {
/* 1083 */       return null;
/*      */     }
/*      */     
/* 1086 */     String beforeDelimiter = toSplit.substring(0, offset);
/* 1087 */     String afterDelimiter = toSplit.substring(offset + delimiter.length());
/* 1088 */     return new String[] { beforeDelimiter, afterDelimiter };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter) {
/* 1103 */     return splitArrayElementsIntoProperties(array, delimiter, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter, @Nullable String charsToDelete) {
/* 1124 */     if (ObjectUtils.isEmpty((Object[])array)) {
/* 1125 */       return null;
/*      */     }
/*      */     
/* 1128 */     Properties result = new Properties();
/* 1129 */     for (String element : array) {
/* 1130 */       if (charsToDelete != null) {
/* 1131 */         element = deleteAny(element, charsToDelete);
/*      */       }
/* 1133 */       String[] splittedElement = split(element, delimiter);
/* 1134 */       if (splittedElement != null)
/*      */       {
/*      */         
/* 1137 */         result.setProperty(splittedElement[0].trim(), splittedElement[1].trim()); } 
/*      */     } 
/* 1139 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] tokenizeToStringArray(@Nullable String str, String delimiters) {
/* 1159 */     return tokenizeToStringArray(str, delimiters, true, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] tokenizeToStringArray(@Nullable String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
/* 1184 */     if (str == null) {
/* 1185 */       return EMPTY_STRING_ARRAY;
/*      */     }
/*      */     
/* 1188 */     StringTokenizer st = new StringTokenizer(str, delimiters);
/* 1189 */     List<String> tokens = new ArrayList<>();
/* 1190 */     while (st.hasMoreTokens()) {
/* 1191 */       String token = st.nextToken();
/* 1192 */       if (trimTokens) {
/* 1193 */         token = token.trim();
/*      */       }
/* 1195 */       if (!ignoreEmptyTokens || token.length() > 0) {
/* 1196 */         tokens.add(token);
/*      */       }
/*      */     } 
/* 1199 */     return toStringArray(tokens);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] delimitedListToStringArray(@Nullable String str, @Nullable String delimiter) {
/* 1216 */     return delimitedListToStringArray(str, delimiter, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] delimitedListToStringArray(@Nullable String str, @Nullable String delimiter, @Nullable String charsToDelete) {
/* 1237 */     if (str == null) {
/* 1238 */       return EMPTY_STRING_ARRAY;
/*      */     }
/* 1240 */     if (delimiter == null) {
/* 1241 */       return new String[] { str };
/*      */     }
/*      */     
/* 1244 */     List<String> result = new ArrayList<>();
/* 1245 */     if (delimiter.isEmpty()) {
/* 1246 */       for (int i = 0; i < str.length(); i++) {
/* 1247 */         result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
/*      */       }
/*      */     } else {
/*      */       
/* 1251 */       int pos = 0;
/*      */       int delPos;
/* 1253 */       while ((delPos = str.indexOf(delimiter, pos)) != -1) {
/* 1254 */         result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
/* 1255 */         pos = delPos + delimiter.length();
/*      */       } 
/* 1257 */       if (str.length() > 0 && pos <= str.length())
/*      */       {
/* 1259 */         result.add(deleteAny(str.substring(pos), charsToDelete));
/*      */       }
/*      */     } 
/* 1262 */     return toStringArray(result);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] commaDelimitedListToStringArray(@Nullable String str) {
/* 1272 */     return delimitedListToStringArray(str, ",");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Set<String> commaDelimitedListToSet(@Nullable String str) {
/* 1284 */     String[] tokens = commaDelimitedListToStringArray(str);
/* 1285 */     return new LinkedHashSet<>(Arrays.asList(tokens));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String collectionToDelimitedString(@Nullable Collection<?> coll, String delim, String prefix, String suffix) {
/* 1300 */     if (CollectionUtils.isEmpty(coll)) {
/* 1301 */       return "";
/*      */     }
/*      */     
/* 1304 */     int totalLength = coll.size() * (prefix.length() + suffix.length()) + (coll.size() - 1) * delim.length();
/* 1305 */     for (Object element : coll) {
/* 1306 */       totalLength += String.valueOf(element).length();
/*      */     }
/*      */     
/* 1309 */     StringBuilder sb = new StringBuilder(totalLength);
/* 1310 */     Iterator<?> it = coll.iterator();
/* 1311 */     while (it.hasNext()) {
/* 1312 */       sb.append(prefix).append(it.next()).append(suffix);
/* 1313 */       if (it.hasNext()) {
/* 1314 */         sb.append(delim);
/*      */       }
/*      */     } 
/* 1317 */     return sb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String collectionToDelimitedString(@Nullable Collection<?> coll, String delim) {
/* 1328 */     return collectionToDelimitedString(coll, delim, "", "");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String collectionToCommaDelimitedString(@Nullable Collection<?> coll) {
/* 1338 */     return collectionToDelimitedString(coll, ",");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String arrayToDelimitedString(@Nullable Object[] arr, String delim) {
/* 1349 */     if (ObjectUtils.isEmpty(arr)) {
/* 1350 */       return "";
/*      */     }
/* 1352 */     if (arr.length == 1) {
/* 1353 */       return ObjectUtils.nullSafeToString(arr[0]);
/*      */     }
/*      */     
/* 1356 */     StringJoiner sj = new StringJoiner(delim);
/* 1357 */     for (Object elem : arr) {
/* 1358 */       sj.add(String.valueOf(elem));
/*      */     }
/* 1360 */     return sj.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String arrayToCommaDelimitedString(@Nullable Object[] arr) {
/* 1371 */     return arrayToDelimitedString(arr, ",");
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/util/StringUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */