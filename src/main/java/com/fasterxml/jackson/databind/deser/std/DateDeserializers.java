/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.core.JacksonException;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.cfg.CoercionAction;
/*     */ import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import com.fasterxml.jackson.databind.util.StdDateFormat;
/*     */ import java.io.IOException;
/*     */ import java.sql.Date;
/*     */ import java.sql.Timestamp;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.HashSet;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class DateDeserializers {
/*  27 */   private static final HashSet<String> _utilClasses = new HashSet<>();
/*     */   static {
/*  29 */     _utilClasses.add("java.util.Calendar");
/*  30 */     _utilClasses.add("java.util.GregorianCalendar");
/*  31 */     _utilClasses.add("java.util.Date");
/*     */   }
/*     */ 
/*     */   
/*     */   public static JsonDeserializer<?> find(Class<?> rawType, String clsName) {
/*  36 */     if (_utilClasses.contains(clsName)) {
/*     */       
/*  38 */       if (rawType == Calendar.class) {
/*  39 */         return new CalendarDeserializer();
/*     */       }
/*  41 */       if (rawType == Date.class) {
/*  42 */         return DateDeserializer.instance;
/*     */       }
/*  44 */       if (rawType == GregorianCalendar.class) {
/*  45 */         return new CalendarDeserializer((Class)GregorianCalendar.class);
/*     */       }
/*     */     } 
/*  48 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean hasDeserializerFor(Class<?> rawType) {
/*  53 */     return _utilClasses.contains(rawType.getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static abstract class DateBasedDeserializer<T>
/*     */     extends StdScalarDeserializer<T>
/*     */     implements ContextualDeserializer
/*     */   {
/*     */     protected final DateFormat _customFormat;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected final String _formatString;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected DateBasedDeserializer(Class<?> clz) {
/*  78 */       super(clz);
/*  79 */       this._customFormat = null;
/*  80 */       this._formatString = null;
/*     */     }
/*     */ 
/*     */     
/*     */     protected DateBasedDeserializer(DateBasedDeserializer<T> base, DateFormat format, String formatStr) {
/*  85 */       super(base._valueClass);
/*  86 */       this._customFormat = format;
/*  87 */       this._formatString = formatStr;
/*     */     }
/*     */ 
/*     */     
/*     */     protected abstract DateBasedDeserializer<T> withDateFormat(DateFormat param1DateFormat, String param1String);
/*     */     
/*     */     public LogicalType logicalType() {
/*  94 */       return LogicalType.DateTime;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/* 102 */       JsonFormat.Value format = findFormatOverrides(ctxt, property, 
/* 103 */           handledType());
/*     */       
/* 105 */       if (format != null) {
/* 106 */         TimeZone tz = format.getTimeZone();
/* 107 */         Boolean lenient = format.getLenient();
/*     */ 
/*     */         
/* 110 */         if (format.hasPattern()) {
/* 111 */           String pattern = format.getPattern();
/* 112 */           Locale loc = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
/* 113 */           SimpleDateFormat df = new SimpleDateFormat(pattern, loc);
/* 114 */           if (tz == null) {
/* 115 */             tz = ctxt.getTimeZone();
/*     */           }
/* 117 */           df.setTimeZone(tz);
/* 118 */           if (lenient != null) {
/* 119 */             df.setLenient(lenient.booleanValue());
/*     */           }
/* 121 */           return withDateFormat(df, pattern);
/*     */         } 
/*     */         
/* 124 */         if (tz != null) {
/* 125 */           StdDateFormat stdDateFormat; DateFormat dateFormat1, df = ctxt.getConfig().getDateFormat();
/*     */           
/* 127 */           if (df.getClass() == StdDateFormat.class) {
/* 128 */             Locale loc = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
/* 129 */             StdDateFormat std = (StdDateFormat)df;
/* 130 */             std = std.withTimeZone(tz);
/* 131 */             std = std.withLocale(loc);
/* 132 */             if (lenient != null) {
/* 133 */               std = std.withLenient(lenient);
/*     */             }
/* 135 */             stdDateFormat = std;
/*     */           } else {
/*     */             
/* 138 */             dateFormat1 = (DateFormat)stdDateFormat.clone();
/* 139 */             dateFormat1.setTimeZone(tz);
/* 140 */             if (lenient != null) {
/* 141 */               dateFormat1.setLenient(lenient.booleanValue());
/*     */             }
/*     */           } 
/* 144 */           return withDateFormat(dateFormat1, this._formatString);
/*     */         } 
/*     */         
/* 147 */         if (lenient != null) {
/* 148 */           StdDateFormat stdDateFormat; DateFormat dateFormat1, df = ctxt.getConfig().getDateFormat();
/* 149 */           String pattern = this._formatString;
/*     */           
/* 151 */           if (df.getClass() == StdDateFormat.class) {
/* 152 */             StdDateFormat std = (StdDateFormat)df;
/* 153 */             std = std.withLenient(lenient);
/* 154 */             stdDateFormat = std;
/* 155 */             pattern = std.toPattern();
/*     */           } else {
/*     */             
/* 158 */             dateFormat1 = (DateFormat)stdDateFormat.clone();
/* 159 */             dateFormat1.setLenient(lenient.booleanValue());
/* 160 */             if (dateFormat1 instanceof SimpleDateFormat) {
/* 161 */               ((SimpleDateFormat)dateFormat1).toPattern();
/*     */             }
/*     */           } 
/* 164 */           if (pattern == null) {
/* 165 */             pattern = "[unknown]";
/*     */           }
/* 167 */           return withDateFormat(dateFormat1, pattern);
/*     */         } 
/*     */       } 
/* 170 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected Date _parseDate(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 177 */       if (this._customFormat != null && 
/* 178 */         p.hasToken(JsonToken.VALUE_STRING)) {
/* 179 */         String str = p.getText().trim();
/* 180 */         if (str.isEmpty()) {
/* 181 */           CoercionAction act = _checkFromStringCoercion(ctxt, str);
/* 182 */           switch (act) {
/*     */             case AsEmpty:
/* 184 */               return new Date(0L);
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 189 */           return null;
/*     */         } 
/* 191 */         synchronized (this._customFormat) {
/*     */           
/* 193 */           return this._customFormat.parse(str);
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 201 */       return super._parseDate(p, ctxt);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @JacksonStdImpl
/*     */   public static class CalendarDeserializer
/*     */     extends DateBasedDeserializer<Calendar>
/*     */   {
/*     */     protected final Constructor<Calendar> _defaultCtor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CalendarDeserializer() {
/* 223 */       super(Calendar.class);
/* 224 */       this._defaultCtor = null;
/*     */     }
/*     */ 
/*     */     
/*     */     public CalendarDeserializer(Class<? extends Calendar> cc) {
/* 229 */       super(cc);
/* 230 */       this._defaultCtor = ClassUtil.findConstructor(cc, false);
/*     */     }
/*     */     
/*     */     public CalendarDeserializer(CalendarDeserializer src, DateFormat df, String formatString) {
/* 234 */       super(src, df, formatString);
/* 235 */       this._defaultCtor = src._defaultCtor;
/*     */     }
/*     */ 
/*     */     
/*     */     protected CalendarDeserializer withDateFormat(DateFormat df, String formatString) {
/* 240 */       return new CalendarDeserializer(this, df, formatString);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getEmptyValue(DeserializationContext ctxt) {
/* 245 */       GregorianCalendar cal = new GregorianCalendar();
/* 246 */       cal.setTimeInMillis(0L);
/* 247 */       return cal;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Calendar deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 253 */       Date d = _parseDate(p, ctxt);
/* 254 */       if (d == null) {
/* 255 */         return null;
/*     */       }
/* 257 */       if (this._defaultCtor == null) {
/* 258 */         return ctxt.constructCalendar(d);
/*     */       }
/*     */       try {
/* 261 */         Calendar c = this._defaultCtor.newInstance(new Object[0]);
/* 262 */         c.setTimeInMillis(d.getTime());
/* 263 */         TimeZone tz = ctxt.getTimeZone();
/* 264 */         if (tz != null) {
/* 265 */           c.setTimeZone(tz);
/*     */         }
/* 267 */         return c;
/* 268 */       } catch (Exception e) {
/* 269 */         return (Calendar)ctxt.handleInstantiationProblem(handledType(), d, e);
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
/*     */   @JacksonStdImpl
/*     */   public static class DateDeserializer
/*     */     extends DateBasedDeserializer<Date>
/*     */   {
/* 284 */     public static final DateDeserializer instance = new DateDeserializer();
/*     */     public DateDeserializer() {
/* 286 */       super(Date.class);
/*     */     } public DateDeserializer(DateDeserializer base, DateFormat df, String formatString) {
/* 288 */       super(base, df, formatString);
/*     */     }
/*     */ 
/*     */     
/*     */     protected DateDeserializer withDateFormat(DateFormat df, String formatString) {
/* 293 */       return new DateDeserializer(this, df, formatString);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getEmptyValue(DeserializationContext ctxt) {
/* 298 */       return new Date(0L);
/*     */     }
/*     */ 
/*     */     
/*     */     public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 303 */       return _parseDate(p, ctxt);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class SqlDateDeserializer
/*     */     extends DateBasedDeserializer<Date>
/*     */   {
/*     */     public SqlDateDeserializer() {
/* 314 */       super(Date.class);
/*     */     } public SqlDateDeserializer(SqlDateDeserializer src, DateFormat df, String formatString) {
/* 316 */       super(src, df, formatString);
/*     */     }
/*     */ 
/*     */     
/*     */     protected SqlDateDeserializer withDateFormat(DateFormat df, String formatString) {
/* 321 */       return new SqlDateDeserializer(this, df, formatString);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getEmptyValue(DeserializationContext ctxt) {
/* 326 */       return new Date(0L);
/*     */     }
/*     */ 
/*     */     
/*     */     public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 331 */       Date d = _parseDate(p, ctxt);
/* 332 */       return (d == null) ? null : new Date(d.getTime());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class TimestampDeserializer
/*     */     extends DateBasedDeserializer<Timestamp>
/*     */   {
/*     */     public TimestampDeserializer() {
/* 345 */       super(Timestamp.class);
/*     */     } public TimestampDeserializer(TimestampDeserializer src, DateFormat df, String formatString) {
/* 347 */       super(src, df, formatString);
/*     */     }
/*     */ 
/*     */     
/*     */     protected TimestampDeserializer withDateFormat(DateFormat df, String formatString) {
/* 352 */       return new TimestampDeserializer(this, df, formatString);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getEmptyValue(DeserializationContext ctxt) {
/* 357 */       return new Timestamp(0L);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Timestamp deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 363 */       Date d = _parseDate(p, ctxt);
/* 364 */       return (d == null) ? null : new Timestamp(d.getTime());
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/DateDeserializers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */