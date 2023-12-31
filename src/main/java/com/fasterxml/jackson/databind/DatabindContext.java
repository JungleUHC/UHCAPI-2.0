/*     */ package com.fasterxml.jackson.databind;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.annotation.ObjectIdGenerator;
/*     */ import com.fasterxml.jackson.annotation.ObjectIdResolver;
/*     */ import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
/*     */ import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
/*     */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.Converter;
/*     */ import java.lang.reflect.Type;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class DatabindContext
/*     */ {
/*     */   private static final int MAX_ERROR_STR_LEN = 500;
/*     */   
/*     */   public abstract MapperConfig<?> getConfig();
/*     */   
/*     */   public abstract AnnotationIntrospector getAnnotationIntrospector();
/*     */   
/*     */   public abstract boolean isEnabled(MapperFeature paramMapperFeature);
/*     */   
/*     */   public abstract boolean canOverrideAccessModifiers();
/*     */   
/*     */   public abstract Class<?> getActiveView();
/*     */   
/*     */   public abstract Locale getLocale();
/*     */   
/*     */   public abstract TimeZone getTimeZone();
/*     */   
/*     */   public abstract JsonFormat.Value getDefaultPropertyFormat(Class<?> paramClass);
/*     */   
/*     */   public abstract Object getAttribute(Object paramObject);
/*     */   
/*     */   public abstract DatabindContext setAttribute(Object paramObject1, Object paramObject2);
/*     */   
/*     */   public JavaType constructType(Type type) {
/* 148 */     if (type == null) {
/* 149 */       return null;
/*     */     }
/* 151 */     return getTypeFactory().constructType(type);
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
/*     */   public abstract JavaType constructSpecializedType(JavaType paramJavaType, Class<?> paramClass);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType resolveSubType(JavaType baseType, String subClassName) throws JsonMappingException {
/* 177 */     if (subClassName.indexOf('<') > 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 182 */       JavaType t = getTypeFactory().constructFromCanonical(subClassName);
/* 183 */       if (t.isTypeOrSubTypeOf(baseType.getRawClass())) {
/* 184 */         return t;
/*     */       }
/*     */     } else {
/*     */       Class<?> cls;
/*     */       try {
/* 189 */         cls = getTypeFactory().findClass(subClassName);
/* 190 */       } catch (ClassNotFoundException e) {
/* 191 */         return null;
/* 192 */       } catch (Exception e) {
/* 193 */         throw invalidTypeIdException(baseType, subClassName, String.format("problem: (%s) %s", new Object[] { e
/*     */                 
/* 195 */                 .getClass().getName(), 
/* 196 */                 ClassUtil.exceptionMessage(e) }));
/*     */       } 
/* 198 */       if (baseType.isTypeOrSuperTypeOf(cls)) {
/* 199 */         return getTypeFactory().constructSpecializedType(baseType, cls);
/*     */       }
/*     */     } 
/* 202 */     throw invalidTypeIdException(baseType, subClassName, "Not a subtype");
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
/*     */   public JavaType resolveAndValidateSubType(JavaType baseType, String subClass, PolymorphicTypeValidator ptv) throws JsonMappingException {
/*     */     Class<?> cls;
/* 216 */     int ltIndex = subClass.indexOf('<');
/* 217 */     if (ltIndex > 0) {
/* 218 */       return _resolveAndValidateGeneric(baseType, subClass, ptv, ltIndex);
/*     */     }
/* 220 */     MapperConfig<?> config = getConfig();
/* 221 */     PolymorphicTypeValidator.Validity vld = ptv.validateSubClassName(config, baseType, subClass);
/* 222 */     if (vld == PolymorphicTypeValidator.Validity.DENIED) {
/* 223 */       return _throwSubtypeNameNotAllowed(baseType, subClass, ptv);
/*     */     }
/*     */     
/*     */     try {
/* 227 */       cls = getTypeFactory().findClass(subClass);
/* 228 */     } catch (ClassNotFoundException e) {
/* 229 */       return null;
/* 230 */     } catch (Exception e) {
/* 231 */       throw invalidTypeIdException(baseType, subClass, String.format("problem: (%s) %s", new Object[] { e
/*     */               
/* 233 */               .getClass().getName(), 
/* 234 */               ClassUtil.exceptionMessage(e) }));
/*     */     } 
/* 236 */     if (!baseType.isTypeOrSuperTypeOf(cls)) {
/* 237 */       return _throwNotASubtype(baseType, subClass);
/*     */     }
/* 239 */     JavaType subType = config.getTypeFactory().constructSpecializedType(baseType, cls);
/*     */     
/* 241 */     if (vld == PolymorphicTypeValidator.Validity.INDETERMINATE) {
/* 242 */       vld = ptv.validateSubType(config, baseType, subType);
/* 243 */       if (vld != PolymorphicTypeValidator.Validity.ALLOWED) {
/* 244 */         return _throwSubtypeClassNotAllowed(baseType, subClass, ptv);
/*     */       }
/*     */     } 
/* 247 */     return subType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private JavaType _resolveAndValidateGeneric(JavaType baseType, String subClass, PolymorphicTypeValidator ptv, int ltIndex) throws JsonMappingException {
/* 254 */     MapperConfig<?> config = getConfig();
/*     */ 
/*     */ 
/*     */     
/* 258 */     PolymorphicTypeValidator.Validity vld = ptv.validateSubClassName(config, baseType, subClass.substring(0, ltIndex));
/* 259 */     if (vld == PolymorphicTypeValidator.Validity.DENIED) {
/* 260 */       return _throwSubtypeNameNotAllowed(baseType, subClass, ptv);
/*     */     }
/* 262 */     JavaType subType = getTypeFactory().constructFromCanonical(subClass);
/* 263 */     if (!subType.isTypeOrSubTypeOf(baseType.getRawClass())) {
/* 264 */       return _throwNotASubtype(baseType, subClass);
/*     */     }
/*     */     
/* 267 */     if (vld != PolymorphicTypeValidator.Validity.ALLOWED && 
/* 268 */       ptv.validateSubType(config, baseType, subType) != PolymorphicTypeValidator.Validity.ALLOWED) {
/* 269 */       return _throwSubtypeClassNotAllowed(baseType, subClass, ptv);
/*     */     }
/*     */     
/* 272 */     return subType;
/*     */   }
/*     */   
/*     */   protected <T> T _throwNotASubtype(JavaType baseType, String subType) throws JsonMappingException {
/* 276 */     throw invalidTypeIdException(baseType, subType, "Not a subtype");
/*     */   }
/*     */ 
/*     */   
/*     */   protected <T> T _throwSubtypeNameNotAllowed(JavaType baseType, String subType, PolymorphicTypeValidator ptv) throws JsonMappingException {
/* 281 */     throw invalidTypeIdException(baseType, subType, "Configured `PolymorphicTypeValidator` (of type " + 
/* 282 */         ClassUtil.classNameOf(ptv) + ") denied resolution");
/*     */   }
/*     */ 
/*     */   
/*     */   protected <T> T _throwSubtypeClassNotAllowed(JavaType baseType, String subType, PolymorphicTypeValidator ptv) throws JsonMappingException {
/* 287 */     throw invalidTypeIdException(baseType, subType, "Configured `PolymorphicTypeValidator` (of type " + 
/* 288 */         ClassUtil.classNameOf(ptv) + ") denied resolution");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract JsonMappingException invalidTypeIdException(JavaType paramJavaType, String paramString1, String paramString2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract TypeFactory getTypeFactory();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectIdGenerator<?> objectIdGeneratorInstance(Annotated annotated, ObjectIdInfo objectIdInfo) throws JsonMappingException {
/* 317 */     Class<?> implClass = objectIdInfo.getGeneratorType();
/* 318 */     MapperConfig<?> config = getConfig();
/* 319 */     HandlerInstantiator hi = config.getHandlerInstantiator();
/* 320 */     ObjectIdGenerator<?> gen = (hi == null) ? null : hi.objectIdGeneratorInstance(config, annotated, implClass);
/* 321 */     if (gen == null) {
/* 322 */       gen = (ObjectIdGenerator)ClassUtil.createInstance(implClass, config
/* 323 */           .canOverrideAccessModifiers());
/*     */     }
/* 325 */     return gen.forScope(objectIdInfo.getScope());
/*     */   }
/*     */ 
/*     */   
/*     */   public ObjectIdResolver objectIdResolverInstance(Annotated annotated, ObjectIdInfo objectIdInfo) {
/* 330 */     Class<? extends ObjectIdResolver> implClass = objectIdInfo.getResolverType();
/* 331 */     MapperConfig<?> config = getConfig();
/* 332 */     HandlerInstantiator hi = config.getHandlerInstantiator();
/* 333 */     ObjectIdResolver resolver = (hi == null) ? null : hi.resolverIdGeneratorInstance(config, annotated, implClass);
/* 334 */     if (resolver == null) {
/* 335 */       resolver = (ObjectIdResolver)ClassUtil.createInstance(implClass, config.canOverrideAccessModifiers());
/*     */     }
/*     */     
/* 338 */     return resolver;
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
/*     */   public Converter<Object, Object> converterInstance(Annotated annotated, Object converterDef) throws JsonMappingException {
/* 352 */     if (converterDef == null) {
/* 353 */       return null;
/*     */     }
/* 355 */     if (converterDef instanceof Converter) {
/* 356 */       return (Converter<Object, Object>)converterDef;
/*     */     }
/* 358 */     if (!(converterDef instanceof Class)) {
/* 359 */       throw new IllegalStateException("AnnotationIntrospector returned Converter definition of type " + converterDef
/* 360 */           .getClass().getName() + "; expected type Converter or Class<Converter> instead");
/*     */     }
/* 362 */     Class<?> converterClass = (Class)converterDef;
/*     */     
/* 364 */     if (converterClass == Converter.None.class || ClassUtil.isBogusClass(converterClass)) {
/* 365 */       return null;
/*     */     }
/* 367 */     if (!Converter.class.isAssignableFrom(converterClass)) {
/* 368 */       throw new IllegalStateException("AnnotationIntrospector returned Class " + converterClass
/* 369 */           .getName() + "; expected Class<Converter>");
/*     */     }
/* 371 */     MapperConfig<?> config = getConfig();
/* 372 */     HandlerInstantiator hi = config.getHandlerInstantiator();
/* 373 */     Converter<?, ?> conv = (hi == null) ? null : hi.converterInstance(config, annotated, converterClass);
/* 374 */     if (conv == null) {
/* 375 */       conv = (Converter<?, ?>)ClassUtil.createInstance(converterClass, config
/* 376 */           .canOverrideAccessModifiers());
/*     */     }
/* 378 */     return (Converter)conv;
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
/*     */   public abstract <T> T reportBadDefinition(JavaType paramJavaType, String paramString) throws JsonMappingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T reportBadDefinition(Class<?> type, String msg) throws JsonMappingException {
/* 400 */     return reportBadDefinition(constructType(type), msg);
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
/*     */   protected final String _format(String msg, Object... msgArgs) {
/* 413 */     if (msgArgs.length > 0) {
/* 414 */       return String.format(msg, msgArgs);
/*     */     }
/* 416 */     return msg;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String _truncate(String desc) {
/* 423 */     if (desc == null) {
/* 424 */       return "";
/*     */     }
/* 426 */     if (desc.length() <= 500) {
/* 427 */       return desc;
/*     */     }
/* 429 */     return desc.substring(0, 500) + "]...[" + desc.substring(desc.length() - 500);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String _quotedString(String desc) {
/* 436 */     if (desc == null) {
/* 437 */       return "[N/A]";
/*     */     }
/*     */     
/* 440 */     return String.format("\"%s\"", new Object[] { _truncate(desc) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String _colonConcat(String msgBase, String extra) {
/* 447 */     if (extra == null) {
/* 448 */       return msgBase;
/*     */     }
/* 450 */     return msgBase + ": " + extra;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String _desc(String desc) {
/* 457 */     if (desc == null) {
/* 458 */       return "[N/A]";
/*     */     }
/*     */     
/* 461 */     return _truncate(desc);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/DatabindContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */