/*     */ package com.fasterxml.jackson.databind.util;
/*     */ 
/*     */ import com.fasterxml.jackson.core.io.NumberInput;
/*     */ import java.text.DateFormat;
/*     */ import java.text.FieldPosition;
/*     */ import java.text.ParseException;
/*     */ import java.text.ParsePosition;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StdDateFormat
/*     */   extends DateFormat
/*     */ {
/*     */   protected static final String PATTERN_PLAIN_STR = "\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d";
/*  50 */   protected static final Pattern PATTERN_PLAIN = Pattern.compile("\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d"); protected static final Pattern PATTERN_ISO8601;
/*     */   public static final String DATE_FORMAT_STR_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
/*     */   
/*     */   static {
/*  54 */     Pattern p = null;
/*     */     try {
/*  56 */       p = Pattern.compile("\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d[T]\\d\\d[:]\\d\\d(?:[:]\\d\\d)?(\\.\\d+)?(Z|[+-]\\d\\d(?:[:]?\\d\\d)?)?");
/*     */ 
/*     */ 
/*     */     
/*     */     }
/*  61 */     catch (Throwable t) {
/*  62 */       throw new RuntimeException(t);
/*     */     } 
/*  64 */     PATTERN_ISO8601 = p;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final String DATE_FORMAT_STR_PLAIN = "yyyy-MM-dd";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final String DATE_FORMAT_STR_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  89 */   protected static final String[] ALL_FORMATS = new String[] { "yyyy-MM-dd'T'HH:mm:ss.SSSX", "yyyy-MM-dd'T'HH:mm:ss.SSS", "EEE, dd MMM yyyy HH:mm:ss zzz", "yyyy-MM-dd" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 102 */   protected static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");
/*     */ 
/*     */   
/* 105 */   protected static final Locale DEFAULT_LOCALE = Locale.US;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 116 */   protected static final DateFormat DATE_FORMAT_RFC1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", DEFAULT_LOCALE); static {
/* 117 */     DATE_FORMAT_RFC1123.setTimeZone(DEFAULT_TIMEZONE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 123 */   public static final StdDateFormat instance = new StdDateFormat();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 132 */   protected static final Calendar CALENDAR = new GregorianCalendar(DEFAULT_TIMEZONE, DEFAULT_LOCALE);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected transient TimeZone _timezone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Locale _locale;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Boolean _lenient;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private transient Calendar _calendar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private transient DateFormat _formatRFC1123;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean _tzSerializedWithColon = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StdDateFormat() {
/* 178 */     this._locale = DEFAULT_LOCALE;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public StdDateFormat(TimeZone tz, Locale loc) {
/* 183 */     this._timezone = tz;
/* 184 */     this._locale = loc;
/*     */   }
/*     */   
/*     */   protected StdDateFormat(TimeZone tz, Locale loc, Boolean lenient) {
/* 188 */     this(tz, loc, lenient, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected StdDateFormat(TimeZone tz, Locale loc, Boolean lenient, boolean formatTzOffsetWithColon) {
/* 196 */     this._timezone = tz;
/* 197 */     this._locale = loc;
/* 198 */     this._lenient = lenient;
/* 199 */     this._tzSerializedWithColon = formatTzOffsetWithColon;
/*     */   }
/*     */   
/*     */   public static TimeZone getDefaultTimeZone() {
/* 203 */     return DEFAULT_TIMEZONE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StdDateFormat withTimeZone(TimeZone tz) {
/* 211 */     if (tz == null) {
/* 212 */       tz = DEFAULT_TIMEZONE;
/*     */     }
/* 214 */     if (tz == this._timezone || tz.equals(this._timezone)) {
/* 215 */       return this;
/*     */     }
/* 217 */     return new StdDateFormat(tz, this._locale, this._lenient, this._tzSerializedWithColon);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StdDateFormat withLocale(Locale loc) {
/* 227 */     if (loc.equals(this._locale)) {
/* 228 */       return this;
/*     */     }
/* 230 */     return new StdDateFormat(this._timezone, loc, this._lenient, this._tzSerializedWithColon);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StdDateFormat withLenient(Boolean b) {
/* 241 */     if (_equals(b, this._lenient)) {
/* 242 */       return this;
/*     */     }
/* 244 */     return new StdDateFormat(this._timezone, this._locale, b, this._tzSerializedWithColon);
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
/*     */   public StdDateFormat withColonInTimeZone(boolean b) {
/* 261 */     if (this._tzSerializedWithColon == b) {
/* 262 */       return this;
/*     */     }
/* 264 */     return new StdDateFormat(this._timezone, this._locale, this._lenient, b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StdDateFormat clone() {
/* 271 */     return new StdDateFormat(this._timezone, this._locale, this._lenient, this._tzSerializedWithColon);
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
/*     */   @Deprecated
/*     */   public static DateFormat getISO8601Format(TimeZone tz, Locale loc) {
/* 285 */     DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", loc);
/* 286 */     df.setTimeZone(DEFAULT_TIMEZONE);
/* 287 */     return df;
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
/*     */   @Deprecated
/*     */   public static DateFormat getRFC1123Format(TimeZone tz, Locale loc) {
/* 301 */     return _cloneFormat(DATE_FORMAT_RFC1123, "EEE, dd MMM yyyy HH:mm:ss zzz", tz, loc, null);
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
/*     */   public TimeZone getTimeZone() {
/* 313 */     return this._timezone;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTimeZone(TimeZone tz) {
/* 321 */     if (!tz.equals(this._timezone)) {
/* 322 */       _clearFormats();
/* 323 */       this._timezone = tz;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLenient(boolean enabled) {
/* 334 */     Boolean newValue = Boolean.valueOf(enabled);
/* 335 */     if (!_equals(newValue, this._lenient)) {
/* 336 */       this._lenient = newValue;
/*     */       
/* 338 */       _clearFormats();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLenient() {
/* 345 */     return (this._lenient == null || this._lenient.booleanValue());
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
/*     */   public boolean isColonIncludedInTimeZone() {
/* 363 */     return this._tzSerializedWithColon;
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
/*     */   public Date parse(String dateStr) throws ParseException {
/* 375 */     dateStr = dateStr.trim();
/* 376 */     ParsePosition pos = new ParsePosition(0);
/* 377 */     Date dt = _parseDate(dateStr, pos);
/* 378 */     if (dt != null) {
/* 379 */       return dt;
/*     */     }
/* 381 */     StringBuilder sb = new StringBuilder();
/* 382 */     for (String f : ALL_FORMATS) {
/* 383 */       if (sb.length() > 0) {
/* 384 */         sb.append("\", \"");
/*     */       } else {
/* 386 */         sb.append('"');
/*     */       } 
/* 388 */       sb.append(f);
/*     */     } 
/* 390 */     sb.append('"');
/* 391 */     throw new ParseException(
/* 392 */         String.format("Cannot parse date \"%s\": not compatible with any of standard forms (%s)", new Object[] {
/* 393 */             dateStr, sb.toString() }), pos.getErrorIndex());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date parse(String dateStr, ParsePosition pos) {
/*     */     try {
/* 401 */       return _parseDate(dateStr, pos);
/* 402 */     } catch (ParseException parseException) {
/*     */ 
/*     */       
/* 405 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected Date _parseDate(String dateStr, ParsePosition pos) throws ParseException {
/* 410 */     if (looksLikeISO8601(dateStr)) {
/* 411 */       return parseAsISO8601(dateStr, pos);
/*     */     }
/*     */     
/* 414 */     int i = dateStr.length();
/* 415 */     while (--i >= 0) {
/* 416 */       char ch = dateStr.charAt(i);
/* 417 */       if (ch < '0' || ch > '9')
/*     */       {
/* 419 */         if (i > 0 || ch != '-') {
/*     */           break;
/*     */         }
/*     */       }
/*     */     } 
/* 424 */     if (i < 0 && (dateStr
/*     */       
/* 426 */       .charAt(0) == '-' || NumberInput.inLongRange(dateStr, false))) {
/* 427 */       return _parseDateFromLong(dateStr, pos);
/*     */     }
/*     */     
/* 430 */     return parseAsRFC1123(dateStr, pos);
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
/*     */   public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
/* 443 */     TimeZone tz = this._timezone;
/* 444 */     if (tz == null) {
/* 445 */       tz = DEFAULT_TIMEZONE;
/*     */     }
/* 447 */     _format(tz, this._locale, date, toAppendTo);
/* 448 */     return toAppendTo;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _format(TimeZone tz, Locale loc, Date date, StringBuffer buffer) {
/* 454 */     Calendar cal = _getCalendar(tz);
/* 455 */     cal.setTime(date);
/*     */     
/* 457 */     int year = cal.get(1);
/*     */ 
/*     */     
/* 460 */     if (cal.get(0) == 0) {
/* 461 */       _formatBCEYear(buffer, year);
/*     */     } else {
/* 463 */       if (year > 9999)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 469 */         buffer.append('+');
/*     */       }
/* 471 */       pad4(buffer, year);
/*     */     } 
/* 473 */     buffer.append('-');
/* 474 */     pad2(buffer, cal.get(2) + 1);
/* 475 */     buffer.append('-');
/* 476 */     pad2(buffer, cal.get(5));
/* 477 */     buffer.append('T');
/* 478 */     pad2(buffer, cal.get(11));
/* 479 */     buffer.append(':');
/* 480 */     pad2(buffer, cal.get(12));
/* 481 */     buffer.append(':');
/* 482 */     pad2(buffer, cal.get(13));
/* 483 */     buffer.append('.');
/* 484 */     pad3(buffer, cal.get(14));
/*     */     
/* 486 */     int offset = tz.getOffset(cal.getTimeInMillis());
/* 487 */     if (offset != 0) {
/* 488 */       int hours = Math.abs(offset / 60000 / 60);
/* 489 */       int minutes = Math.abs(offset / 60000 % 60);
/* 490 */       buffer.append((offset < 0) ? 45 : 43);
/* 491 */       pad2(buffer, hours);
/* 492 */       if (this._tzSerializedWithColon) {
/* 493 */         buffer.append(':');
/*     */       }
/* 495 */       pad2(buffer, minutes);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 502 */     else if (this._tzSerializedWithColon) {
/* 503 */       buffer.append("+00:00");
/*     */     } else {
/* 505 */       buffer.append("+0000");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _formatBCEYear(StringBuffer buffer, int bceYearNoSign) {
/* 513 */     if (bceYearNoSign == 1) {
/* 514 */       buffer.append("+0000");
/*     */       return;
/*     */     } 
/* 517 */     int isoYear = bceYearNoSign - 1;
/* 518 */     buffer.append('-');
/*     */ 
/*     */ 
/*     */     
/* 522 */     pad4(buffer, isoYear);
/*     */   }
/*     */   
/*     */   private static void pad2(StringBuffer buffer, int value) {
/* 526 */     int tens = value / 10;
/* 527 */     if (tens == 0) {
/* 528 */       buffer.append('0');
/*     */     } else {
/* 530 */       buffer.append((char)(48 + tens));
/* 531 */       value -= 10 * tens;
/*     */     } 
/* 533 */     buffer.append((char)(48 + value));
/*     */   }
/*     */   
/*     */   private static void pad3(StringBuffer buffer, int value) {
/* 537 */     int h = value / 100;
/* 538 */     if (h == 0) {
/* 539 */       buffer.append('0');
/*     */     } else {
/* 541 */       buffer.append((char)(48 + h));
/* 542 */       value -= h * 100;
/*     */     } 
/* 544 */     pad2(buffer, value);
/*     */   }
/*     */   
/*     */   private static void pad4(StringBuffer buffer, int value) {
/* 548 */     int h = value / 100;
/* 549 */     if (h == 0) {
/* 550 */       buffer.append('0').append('0');
/*     */     } else {
/* 552 */       if (h > 99) {
/* 553 */         buffer.append(h);
/*     */       } else {
/* 555 */         pad2(buffer, h);
/*     */       } 
/* 557 */       value -= 100 * h;
/*     */     } 
/* 559 */     pad2(buffer, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 570 */     return String.format("DateFormat %s: (timezone: %s, locale: %s, lenient: %s)", new Object[] {
/* 571 */           getClass().getName(), this._timezone, this._locale, this._lenient });
/*     */   }
/*     */   
/*     */   public String toPattern() {
/* 575 */     StringBuilder sb = new StringBuilder(100);
/* 576 */     sb.append("[one of: '")
/* 577 */       .append("yyyy-MM-dd'T'HH:mm:ss.SSSX")
/* 578 */       .append("', '")
/* 579 */       .append("EEE, dd MMM yyyy HH:mm:ss zzz")
/* 580 */       .append("' (");
/*     */     
/* 582 */     sb.append(Boolean.FALSE.equals(this._lenient) ? "strict" : "lenient")
/*     */       
/* 584 */       .append(")]");
/* 585 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 590 */     return (o == this);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 595 */     return System.identityHashCode(this);
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
/*     */   protected boolean looksLikeISO8601(String dateStr) {
/* 610 */     if (dateStr.length() >= 7 && 
/* 611 */       Character.isDigit(dateStr.charAt(0)) && 
/* 612 */       Character.isDigit(dateStr.charAt(3)) && dateStr
/* 613 */       .charAt(4) == '-' && 
/* 614 */       Character.isDigit(dateStr.charAt(5)))
/*     */     {
/* 616 */       return true;
/*     */     }
/* 618 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private Date _parseDateFromLong(String longStr, ParsePosition pos) throws ParseException {
/*     */     long ts;
/*     */     try {
/* 625 */       ts = NumberInput.parseLong(longStr);
/* 626 */     } catch (NumberFormatException e) {
/* 627 */       throw new ParseException(String.format("Timestamp value %s out of 64-bit value range", new Object[] { longStr }), pos
/*     */           
/* 629 */           .getErrorIndex());
/*     */     } 
/* 631 */     return new Date(ts);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Date parseAsISO8601(String dateStr, ParsePosition pos) throws ParseException {
/*     */     try {
/* 638 */       return _parseAsISO8601(dateStr, pos);
/* 639 */     } catch (IllegalArgumentException e) {
/* 640 */       throw new ParseException(String.format("Cannot parse date \"%s\", problem: %s", new Object[] { dateStr, e
/* 641 */               .getMessage()
/* 642 */             }), pos.getErrorIndex());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected Date _parseAsISO8601(String dateStr, ParsePosition bogus) throws IllegalArgumentException, ParseException {
/*     */     String formatStr;
/* 649 */     int totalLen = dateStr.length();
/*     */     
/* 651 */     TimeZone tz = DEFAULT_TIMEZONE;
/* 652 */     if (this._timezone != null && 'Z' != dateStr.charAt(totalLen - 1)) {
/* 653 */       tz = this._timezone;
/*     */     }
/* 655 */     Calendar cal = _getCalendar(tz);
/* 656 */     cal.clear();
/*     */     
/* 658 */     if (totalLen <= 10) {
/* 659 */       Matcher m = PATTERN_PLAIN.matcher(dateStr);
/* 660 */       if (m.matches()) {
/* 661 */         int year = _parse4D(dateStr, 0);
/* 662 */         int month = _parse2D(dateStr, 5) - 1;
/* 663 */         int day = _parse2D(dateStr, 8);
/*     */         
/* 665 */         cal.set(year, month, day, 0, 0, 0);
/* 666 */         cal.set(14, 0);
/* 667 */         return cal.getTime();
/*     */       } 
/* 669 */       formatStr = "yyyy-MM-dd";
/*     */     } else {
/* 671 */       Matcher m = PATTERN_ISO8601.matcher(dateStr);
/* 672 */       if (m.matches()) {
/*     */ 
/*     */         
/* 675 */         int seconds, start = m.start(2);
/* 676 */         int end = m.end(2);
/* 677 */         int len = end - start;
/* 678 */         if (len > 1) {
/*     */           
/* 680 */           int offsetSecs = _parse2D(dateStr, start + 1) * 3600;
/* 681 */           if (len >= 5) {
/* 682 */             offsetSecs += _parse2D(dateStr, end - 2) * 60;
/*     */           }
/* 684 */           if (dateStr.charAt(start) == '-') {
/* 685 */             offsetSecs *= -1000;
/*     */           } else {
/* 687 */             offsetSecs *= 1000;
/*     */           } 
/* 689 */           cal.set(15, offsetSecs);
/*     */           
/* 691 */           cal.set(16, 0);
/*     */         } 
/*     */         
/* 694 */         int year = _parse4D(dateStr, 0);
/* 695 */         int month = _parse2D(dateStr, 5) - 1;
/* 696 */         int day = _parse2D(dateStr, 8);
/*     */ 
/*     */         
/* 699 */         int hour = _parse2D(dateStr, 11);
/* 700 */         int minute = _parse2D(dateStr, 14);
/*     */ 
/*     */ 
/*     */         
/* 704 */         if (totalLen > 16 && dateStr.charAt(16) == ':') {
/* 705 */           seconds = _parse2D(dateStr, 17);
/*     */         } else {
/* 707 */           seconds = 0;
/*     */         } 
/* 709 */         cal.set(year, month, day, hour, minute, seconds);
/*     */ 
/*     */         
/* 712 */         start = m.start(1) + 1;
/* 713 */         end = m.end(1);
/* 714 */         int msecs = 0;
/* 715 */         if (start >= end) {
/* 716 */           cal.set(14, 0);
/*     */         } else {
/*     */           
/* 719 */           msecs = 0;
/* 720 */           int fractLen = end - start;
/* 721 */           switch (fractLen) {
/*     */             
/*     */             default:
/* 724 */               if (fractLen > 9) {
/* 725 */                 throw new ParseException(String.format("Cannot parse date \"%s\": invalid fractional seconds '%s'; can use at most 9 digits", new Object[] { dateStr, m
/*     */                         
/* 727 */                         .group(1).substring(1) }), start);
/*     */               }
/*     */ 
/*     */             
/*     */             case 3:
/* 732 */               msecs += dateStr.charAt(start + 2) - 48;
/*     */             case 2:
/* 734 */               msecs += 10 * (dateStr.charAt(start + 1) - 48);
/*     */             case 1:
/* 736 */               msecs += 100 * (dateStr.charAt(start) - 48);
/*     */               break;
/*     */             case 0:
/*     */               break;
/*     */           } 
/* 741 */           cal.set(14, msecs);
/*     */         } 
/* 743 */         return cal.getTime();
/*     */       } 
/* 745 */       formatStr = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
/*     */     } 
/*     */     
/* 748 */     throw new ParseException(
/* 749 */         String.format("Cannot parse date \"%s\": while it seems to fit format '%s', parsing fails (leniency? %s)", new Object[] { dateStr, formatStr, this._lenient }), 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int _parse4D(String str, int index) {
/* 757 */     return 1000 * (str.charAt(index) - 48) + 100 * (str
/* 758 */       .charAt(index + 1) - 48) + 10 * (str
/* 759 */       .charAt(index + 2) - 48) + str
/* 760 */       .charAt(index + 3) - 48;
/*     */   }
/*     */   
/*     */   private static int _parse2D(String str, int index) {
/* 764 */     return 10 * (str.charAt(index) - 48) + str
/* 765 */       .charAt(index + 1) - 48;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Date parseAsRFC1123(String dateStr, ParsePosition pos) {
/* 770 */     if (this._formatRFC1123 == null) {
/* 771 */       this._formatRFC1123 = _cloneFormat(DATE_FORMAT_RFC1123, "EEE, dd MMM yyyy HH:mm:ss zzz", this._timezone, this._locale, this._lenient);
/*     */     }
/*     */     
/* 774 */     return this._formatRFC1123.parse(dateStr, pos);
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
/*     */   private static final DateFormat _cloneFormat(DateFormat df, String format, TimeZone tz, Locale loc, Boolean lenient) {
/* 786 */     if (!loc.equals(DEFAULT_LOCALE)) {
/* 787 */       df = new SimpleDateFormat(format, loc);
/* 788 */       df.setTimeZone((tz == null) ? DEFAULT_TIMEZONE : tz);
/*     */     } else {
/* 790 */       df = (DateFormat)df.clone();
/* 791 */       if (tz != null) {
/* 792 */         df.setTimeZone(tz);
/*     */       }
/*     */     } 
/* 795 */     if (lenient != null) {
/* 796 */       df.setLenient(lenient.booleanValue());
/*     */     }
/* 798 */     return df;
/*     */   }
/*     */   
/*     */   protected void _clearFormats() {
/* 802 */     this._formatRFC1123 = null;
/*     */   }
/*     */   
/*     */   protected Calendar _getCalendar(TimeZone tz) {
/* 806 */     Calendar cal = this._calendar;
/* 807 */     if (cal == null) {
/* 808 */       this._calendar = cal = (Calendar)CALENDAR.clone();
/*     */     }
/* 810 */     if (!cal.getTimeZone().equals(tz)) {
/* 811 */       cal.setTimeZone(tz);
/*     */     }
/* 813 */     cal.setLenient(isLenient());
/* 814 */     return cal;
/*     */   }
/*     */   
/*     */   protected static <T> boolean _equals(T value1, T value2) {
/* 818 */     if (value1 == value2) {
/* 819 */       return true;
/*     */     }
/* 821 */     return (value1 != null && value1.equals(value2));
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/util/StdDateFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */