/*     */ package com.fasterxml.jackson.databind.cfg;
/*     */ 
/*     */ import com.fasterxml.jackson.core.Base64Variant;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.PropertyNamingStrategy;
/*     */ import com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
/*     */ import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
/*     */ import com.fasterxml.jackson.databind.introspect.DefaultAccessorNamingStrategy;
/*     */ import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*     */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*     */ import com.fasterxml.jackson.databind.util.StdDateFormat;
/*     */ import java.io.Serializable;
/*     */ import java.text.DateFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class BaseSettings
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  38 */   private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final TypeFactory _typeFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final ClassIntrospector _classIntrospector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final AnnotationIntrospector _annotationIntrospector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final PropertyNamingStrategy _propertyNamingStrategy;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final AccessorNamingStrategy.Provider _accessorNaming;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final TypeResolverBuilder<?> _typeResolverBuilder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final PolymorphicTypeValidator _typeValidator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final DateFormat _dateFormat;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final HandlerInstantiator _handlerInstantiator;
/*     */ 
/*     */ 
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected final TimeZone _timeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Base64Variant _defaultBase64;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BaseSettings(ClassIntrospector ci, AnnotationIntrospector ai, PropertyNamingStrategy pns, TypeFactory tf, TypeResolverBuilder<?> typer, DateFormat dateFormat, HandlerInstantiator hi, Locale locale, TimeZone tz, Base64Variant defaultBase64, PolymorphicTypeValidator ptv, AccessorNamingStrategy.Provider accNaming) {
/* 161 */     this._classIntrospector = ci;
/* 162 */     this._annotationIntrospector = ai;
/* 163 */     this._propertyNamingStrategy = pns;
/* 164 */     this._typeFactory = tf;
/* 165 */     this._typeResolverBuilder = typer;
/* 166 */     this._dateFormat = dateFormat;
/* 167 */     this._handlerInstantiator = hi;
/* 168 */     this._locale = locale;
/* 169 */     this._timeZone = tz;
/* 170 */     this._defaultBase64 = defaultBase64;
/* 171 */     this._typeValidator = ptv;
/* 172 */     this._accessorNaming = accNaming;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public BaseSettings(ClassIntrospector ci, AnnotationIntrospector ai, PropertyNamingStrategy pns, TypeFactory tf, TypeResolverBuilder<?> typer, DateFormat dateFormat, HandlerInstantiator hi, Locale locale, TimeZone tz, Base64Variant defaultBase64, PolymorphicTypeValidator ptv) {
/* 182 */     this(ci, ai, pns, tf, typer, dateFormat, hi, locale, tz, defaultBase64, ptv, (AccessorNamingStrategy.Provider)new DefaultAccessorNamingStrategy.Provider());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BaseSettings copy() {
/* 193 */     return new BaseSettings(this._classIntrospector.copy(), this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
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
/*     */   public BaseSettings withClassIntrospector(ClassIntrospector ci) {
/* 214 */     if (this._classIntrospector == ci) {
/* 215 */       return this;
/*     */     }
/* 217 */     return new BaseSettings(ci, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BaseSettings withAnnotationIntrospector(AnnotationIntrospector ai) {
/* 223 */     if (this._annotationIntrospector == ai) {
/* 224 */       return this;
/*     */     }
/* 226 */     return new BaseSettings(this._classIntrospector, ai, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BaseSettings withInsertedAnnotationIntrospector(AnnotationIntrospector ai) {
/* 232 */     return withAnnotationIntrospector(AnnotationIntrospectorPair.create(ai, this._annotationIntrospector));
/*     */   }
/*     */   
/*     */   public BaseSettings withAppendedAnnotationIntrospector(AnnotationIntrospector ai) {
/* 236 */     return withAnnotationIntrospector(AnnotationIntrospectorPair.create(this._annotationIntrospector, ai));
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
/*     */   public BaseSettings withPropertyNamingStrategy(PropertyNamingStrategy pns) {
/* 250 */     if (this._propertyNamingStrategy == pns) {
/* 251 */       return this;
/*     */     }
/* 253 */     return new BaseSettings(this._classIntrospector, this._annotationIntrospector, pns, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BaseSettings withAccessorNaming(AccessorNamingStrategy.Provider p) {
/* 260 */     if (this._accessorNaming == p) {
/* 261 */       return this;
/*     */     }
/* 263 */     return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, p);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BaseSettings withTypeFactory(TypeFactory tf) {
/* 269 */     if (this._typeFactory == tf) {
/* 270 */       return this;
/*     */     }
/* 272 */     return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, tf, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BaseSettings withTypeResolverBuilder(TypeResolverBuilder<?> typer) {
/* 278 */     if (this._typeResolverBuilder == typer) {
/* 279 */       return this;
/*     */     }
/* 281 */     return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, typer, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BaseSettings withDateFormat(DateFormat df) {
/* 287 */     if (this._dateFormat == df) {
/* 288 */       return this;
/*     */     }
/*     */ 
/*     */     
/* 292 */     if (df != null && hasExplicitTimeZone()) {
/* 293 */       df = _force(df, this._timeZone);
/*     */     }
/* 295 */     return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, df, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BaseSettings withHandlerInstantiator(HandlerInstantiator hi) {
/* 301 */     if (this._handlerInstantiator == hi) {
/* 302 */       return this;
/*     */     }
/* 304 */     return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, hi, this._locale, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BaseSettings with(Locale l) {
/* 310 */     if (this._locale == l) {
/* 311 */       return this;
/*     */     }
/* 313 */     return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, l, this._timeZone, this._defaultBase64, this._typeValidator, this._accessorNaming);
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
/*     */   public BaseSettings with(TimeZone tz) {
/* 325 */     if (tz == this._timeZone) {
/* 326 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 334 */     DateFormat df = _force(this._dateFormat, (tz == null) ? DEFAULT_TIMEZONE : tz);
/* 335 */     return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, df, this._handlerInstantiator, this._locale, tz, this._defaultBase64, this._typeValidator, this._accessorNaming);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BaseSettings with(Base64Variant base64) {
/* 345 */     if (base64 == this._defaultBase64) {
/* 346 */       return this;
/*     */     }
/* 348 */     return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, base64, this._typeValidator, this._accessorNaming);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BaseSettings with(PolymorphicTypeValidator v) {
/* 358 */     if (v == this._typeValidator) {
/* 359 */       return this;
/*     */     }
/* 361 */     return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64, v, this._accessorNaming);
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
/*     */   public ClassIntrospector getClassIntrospector() {
/* 374 */     return this._classIntrospector;
/*     */   }
/*     */   
/*     */   public AnnotationIntrospector getAnnotationIntrospector() {
/* 378 */     return this._annotationIntrospector;
/*     */   }
/*     */   
/*     */   public PropertyNamingStrategy getPropertyNamingStrategy() {
/* 382 */     return this._propertyNamingStrategy;
/*     */   }
/*     */   
/*     */   public AccessorNamingStrategy.Provider getAccessorNaming() {
/* 386 */     return this._accessorNaming;
/*     */   }
/*     */   
/*     */   public TypeFactory getTypeFactory() {
/* 390 */     return this._typeFactory;
/*     */   }
/*     */   
/*     */   public TypeResolverBuilder<?> getTypeResolverBuilder() {
/* 394 */     return this._typeResolverBuilder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PolymorphicTypeValidator getPolymorphicTypeValidator() {
/* 401 */     return this._typeValidator;
/*     */   }
/*     */   
/*     */   public DateFormat getDateFormat() {
/* 405 */     return this._dateFormat;
/*     */   }
/*     */   
/*     */   public HandlerInstantiator getHandlerInstantiator() {
/* 409 */     return this._handlerInstantiator;
/*     */   }
/*     */   
/*     */   public Locale getLocale() {
/* 413 */     return this._locale;
/*     */   }
/*     */   
/*     */   public TimeZone getTimeZone() {
/* 417 */     TimeZone tz = this._timeZone;
/* 418 */     return (tz == null) ? DEFAULT_TIMEZONE : tz;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasExplicitTimeZone() {
/* 429 */     return (this._timeZone != null);
/*     */   }
/*     */   
/*     */   public Base64Variant getBase64Variant() {
/* 433 */     return this._defaultBase64;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DateFormat _force(DateFormat df, TimeZone tz) {
/* 444 */     if (df instanceof StdDateFormat) {
/* 445 */       return (DateFormat)((StdDateFormat)df).withTimeZone(tz);
/*     */     }
/*     */     
/* 448 */     df = (DateFormat)df.clone();
/* 449 */     df.setTimeZone(tz);
/* 450 */     return df;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/cfg/BaseSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */