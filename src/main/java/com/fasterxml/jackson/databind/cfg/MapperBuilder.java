/*     */ package com.fasterxml.jackson.databind.cfg;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonAutoDetect;
/*     */ import com.fasterxml.jackson.annotation.JsonInclude;
/*     */ import com.fasterxml.jackson.annotation.JsonSetter;
/*     */ import com.fasterxml.jackson.annotation.JsonTypeInfo;
/*     */ import com.fasterxml.jackson.annotation.PropertyAccessor;
/*     */ import com.fasterxml.jackson.core.Base64Variant;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.PrettyPrinter;
/*     */ import com.fasterxml.jackson.core.StreamReadFeature;
/*     */ import com.fasterxml.jackson.core.StreamWriteFeature;
/*     */ import com.fasterxml.jackson.core.TokenStreamFactory;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.InjectableValues;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.Module;
/*     */ import com.fasterxml.jackson.databind.ObjectMapper;
/*     */ import com.fasterxml.jackson.databind.PropertyNamingStrategy;
/*     */ import com.fasterxml.jackson.databind.SerializationFeature;
/*     */ import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
/*     */ import com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy;
/*     */ import com.fasterxml.jackson.databind.introspect.DefaultAccessorNamingStrategy;
/*     */ import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
/*     */ import com.fasterxml.jackson.databind.jsontype.NamedType;
/*     */ import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
/*     */ import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*     */ import com.fasterxml.jackson.databind.node.JsonNodeFactory;
/*     */ import com.fasterxml.jackson.databind.ser.FilterProvider;
/*     */ import com.fasterxml.jackson.databind.ser.SerializerFactory;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.text.DateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.ServiceLoader;
/*     */ import java.util.TimeZone;
/*     */ import java.util.function.Consumer;
/*     */ 
/*     */ public abstract class MapperBuilder<M extends ObjectMapper, B extends MapperBuilder<M, B>> {
/*     */   protected final M _mapper;
/*     */   
/*     */   protected MapperBuilder(M mapper) {
/*  51 */     this._mapper = mapper;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public M build() {
/*  62 */     return this._mapper;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEnabled(MapperFeature f) {
/*  72 */     return this._mapper.isEnabled(f);
/*     */   }
/*     */   public boolean isEnabled(DeserializationFeature f) {
/*  75 */     return this._mapper.isEnabled(f);
/*     */   }
/*     */   public boolean isEnabled(SerializationFeature f) {
/*  78 */     return this._mapper.isEnabled(f);
/*     */   }
/*     */   
/*     */   public boolean isEnabled(JsonParser.Feature f) {
/*  82 */     return this._mapper.isEnabled(f);
/*     */   }
/*     */   public boolean isEnabled(JsonGenerator.Feature f) {
/*  85 */     return this._mapper.isEnabled(f);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TokenStreamFactory streamFactory() {
/*  95 */     return (TokenStreamFactory)this._mapper.tokenStreamFactory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B enable(MapperFeature... features) {
/* 106 */     this._mapper.enable(features);
/* 107 */     return _this();
/*     */   }
/*     */ 
/*     */   
/*     */   public B disable(MapperFeature... features) {
/* 112 */     this._mapper.disable(features);
/* 113 */     return _this();
/*     */   }
/*     */ 
/*     */   
/*     */   public B configure(MapperFeature feature, boolean state) {
/* 118 */     this._mapper.configure(feature, state);
/* 119 */     return _this();
/*     */   }
/*     */   
/*     */   public B enable(SerializationFeature... features) {
/* 123 */     for (SerializationFeature f : features) {
/* 124 */       this._mapper.enable(f);
/*     */     }
/* 126 */     return _this();
/*     */   }
/*     */   
/*     */   public B disable(SerializationFeature... features) {
/* 130 */     for (SerializationFeature f : features) {
/* 131 */       this._mapper.disable(f);
/*     */     }
/* 133 */     return _this();
/*     */   }
/*     */   
/*     */   public B configure(SerializationFeature feature, boolean state) {
/* 137 */     this._mapper.configure(feature, state);
/* 138 */     return _this();
/*     */   }
/*     */   
/*     */   public B enable(DeserializationFeature... features) {
/* 142 */     for (DeserializationFeature f : features) {
/* 143 */       this._mapper.enable(f);
/*     */     }
/* 145 */     return _this();
/*     */   }
/*     */   
/*     */   public B disable(DeserializationFeature... features) {
/* 149 */     for (DeserializationFeature f : features) {
/* 150 */       this._mapper.disable(f);
/*     */     }
/* 152 */     return _this();
/*     */   }
/*     */   
/*     */   public B configure(DeserializationFeature feature, boolean state) {
/* 156 */     this._mapper.configure(feature, state);
/* 157 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B enable(JsonParser.Feature... features) {
/* 167 */     this._mapper.enable(features);
/* 168 */     return _this();
/*     */   }
/*     */   
/*     */   public B disable(JsonParser.Feature... features) {
/* 172 */     this._mapper.disable(features);
/* 173 */     return _this();
/*     */   }
/*     */   
/*     */   public B configure(JsonParser.Feature feature, boolean state) {
/* 177 */     this._mapper.configure(feature, state);
/* 178 */     return _this();
/*     */   }
/*     */   
/*     */   public B enable(JsonGenerator.Feature... features) {
/* 182 */     this._mapper.enable(features);
/* 183 */     return _this();
/*     */   }
/*     */   
/*     */   public B disable(JsonGenerator.Feature... features) {
/* 187 */     this._mapper.disable(features);
/* 188 */     return _this();
/*     */   }
/*     */   
/*     */   public B configure(JsonGenerator.Feature feature, boolean state) {
/* 192 */     this._mapper.configure(feature, state);
/* 193 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B enable(StreamReadFeature... features) {
/* 203 */     for (StreamReadFeature f : features) {
/* 204 */       this._mapper.enable(new JsonParser.Feature[] { f.mappedFeature() });
/*     */     } 
/* 206 */     return _this();
/*     */   }
/*     */   
/*     */   public B disable(StreamReadFeature... features) {
/* 210 */     for (StreamReadFeature f : features) {
/* 211 */       this._mapper.disable(new JsonParser.Feature[] { f.mappedFeature() });
/*     */     } 
/* 213 */     return _this();
/*     */   }
/*     */   
/*     */   public B configure(StreamReadFeature feature, boolean state) {
/* 217 */     this._mapper.configure(feature.mappedFeature(), state);
/* 218 */     return _this();
/*     */   }
/*     */   
/*     */   public B enable(StreamWriteFeature... features) {
/* 222 */     for (StreamWriteFeature f : features) {
/* 223 */       this._mapper.enable(new JsonGenerator.Feature[] { f.mappedFeature() });
/*     */     } 
/* 225 */     return _this();
/*     */   }
/*     */   
/*     */   public B disable(StreamWriteFeature... features) {
/* 229 */     for (StreamWriteFeature f : features) {
/* 230 */       this._mapper.disable(new JsonGenerator.Feature[] { f.mappedFeature() });
/*     */     } 
/* 232 */     return _this();
/*     */   }
/*     */   
/*     */   public B configure(StreamWriteFeature feature, boolean state) {
/* 236 */     this._mapper.configure(feature.mappedFeature(), state);
/* 237 */     return _this();
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
/*     */   public B withConfigOverride(Class<?> forType, Consumer<MutableConfigOverride> handler) {
/* 258 */     handler.accept(this._mapper.configOverride(forType));
/* 259 */     return _this();
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
/*     */   public B withCoercionConfig(LogicalType forType, Consumer<MutableCoercionConfig> handler) {
/* 286 */     handler.accept(this._mapper.coercionConfigFor(forType));
/* 287 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B withCoercionConfig(Class<?> forType, Consumer<MutableCoercionConfig> handler) {
/* 298 */     handler.accept(this._mapper.coercionConfigFor(forType));
/* 299 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B withCoercionConfigDefaults(Consumer<MutableCoercionConfig> handler) {
/* 308 */     handler.accept(this._mapper.coercionConfigDefaults());
/* 309 */     return _this();
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
/*     */   public B addModule(Module module) {
/* 327 */     this._mapper.registerModule(module);
/* 328 */     return _this();
/*     */   }
/*     */ 
/*     */   
/*     */   public B addModules(Module... modules) {
/* 333 */     for (Module module : modules) {
/* 334 */       addModule(module);
/*     */     }
/* 336 */     return _this();
/*     */   }
/*     */ 
/*     */   
/*     */   public B addModules(Iterable<? extends Module> modules) {
/* 341 */     for (Module module : modules) {
/* 342 */       addModule(module);
/*     */     }
/* 344 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<Module> findModules() {
/* 355 */     return findModules(null);
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
/*     */   public static List<Module> findModules(ClassLoader classLoader) {
/* 367 */     ArrayList<Module> modules = new ArrayList<>();
/* 368 */     ServiceLoader<Module> loader = secureGetServiceLoader(Module.class, classLoader);
/* 369 */     for (Module module : loader) {
/* 370 */       modules.add(module);
/*     */     }
/* 372 */     return modules;
/*     */   }
/*     */   
/*     */   private static <T> ServiceLoader<T> secureGetServiceLoader(final Class<T> clazz, final ClassLoader classLoader) {
/* 376 */     SecurityManager sm = System.getSecurityManager();
/* 377 */     if (sm == null) {
/* 378 */       return (classLoader == null) ? 
/* 379 */         ServiceLoader.<T>load(clazz) : ServiceLoader.<T>load(clazz, classLoader);
/*     */     }
/* 381 */     return AccessController.<ServiceLoader<T>>doPrivileged((PrivilegedAction)new PrivilegedAction<ServiceLoader<ServiceLoader<T>>>()
/*     */         {
/*     */           public ServiceLoader<T> run() {
/* 384 */             return (classLoader == null) ? 
/* 385 */               ServiceLoader.<T>load(clazz) : ServiceLoader.<T>load(clazz, classLoader);
/*     */           }
/*     */         });
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
/*     */   public B findAndAddModules() {
/* 401 */     return addModules(findModules());
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
/*     */   public B annotationIntrospector(AnnotationIntrospector intr) {
/* 421 */     this._mapper.setAnnotationIntrospector(intr);
/* 422 */     return _this();
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
/*     */   public B defaultAttributes(ContextAttributes attrs) {
/* 438 */     this._mapper.setDefaultAttributes(attrs);
/* 439 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B typeFactory(TypeFactory f) {
/* 449 */     this._mapper.setTypeFactory(f);
/* 450 */     return _this();
/*     */   }
/*     */   
/*     */   public B subtypeResolver(SubtypeResolver r) {
/* 454 */     this._mapper.setSubtypeResolver(r);
/* 455 */     return _this();
/*     */   }
/*     */   
/*     */   public B visibility(VisibilityChecker<?> vc) {
/* 459 */     this._mapper.setVisibility(vc);
/* 460 */     return _this();
/*     */   }
/*     */   
/*     */   public B visibility(PropertyAccessor forMethod, JsonAutoDetect.Visibility visibility) {
/* 464 */     this._mapper.setVisibility(forMethod, visibility);
/* 465 */     return _this();
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
/*     */   public B handlerInstantiator(HandlerInstantiator hi) {
/* 478 */     this._mapper.setHandlerInstantiator(hi);
/* 479 */     return _this();
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
/*     */   public B propertyNamingStrategy(PropertyNamingStrategy s) {
/* 491 */     this._mapper.setPropertyNamingStrategy(s);
/* 492 */     return _this();
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
/*     */   public B accessorNaming(AccessorNamingStrategy.Provider s) {
/*     */     DefaultAccessorNamingStrategy.Provider provider;
/* 506 */     if (s == null) {
/* 507 */       provider = new DefaultAccessorNamingStrategy.Provider();
/*     */     }
/* 509 */     this._mapper.setAccessorNaming((AccessorNamingStrategy.Provider)provider);
/* 510 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B serializerFactory(SerializerFactory f) {
/* 520 */     this._mapper.setSerializerFactory(f);
/* 521 */     return _this();
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
/*     */   public B filterProvider(FilterProvider prov) {
/* 533 */     this._mapper.setFilterProvider(prov);
/* 534 */     return _this();
/*     */   }
/*     */   
/*     */   public B defaultPrettyPrinter(PrettyPrinter pp) {
/* 538 */     this._mapper.setDefaultPrettyPrinter(pp);
/* 539 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B injectableValues(InjectableValues v) {
/* 549 */     this._mapper.setInjectableValues(v);
/* 550 */     return _this();
/*     */   }
/*     */   
/*     */   public B nodeFactory(JsonNodeFactory f) {
/* 554 */     this._mapper.setNodeFactory(f);
/* 555 */     return _this();
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
/*     */   public B constructorDetector(ConstructorDetector cd) {
/* 567 */     this._mapper.setConstructorDetector(cd);
/* 568 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B addHandler(DeserializationProblemHandler h) {
/* 577 */     this._mapper.addHandler(h);
/* 578 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B clearProblemHandlers() {
/* 586 */     this._mapper.clearProblemHandlers();
/* 587 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B defaultSetterInfo(JsonSetter.Value v) {
/* 597 */     this._mapper.setDefaultSetterInfo(v);
/* 598 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B defaultMergeable(Boolean b) {
/* 607 */     this._mapper.setDefaultMergeable(b);
/* 608 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B defaultLeniency(Boolean b) {
/* 617 */     this._mapper.setDefaultLeniency(b);
/* 618 */     return _this();
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
/*     */   public B defaultDateFormat(DateFormat df) {
/* 634 */     this._mapper.setDateFormat(df);
/* 635 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B defaultTimeZone(TimeZone tz) {
/* 643 */     this._mapper.setTimeZone(tz);
/* 644 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B defaultLocale(Locale locale) {
/* 652 */     this._mapper.setLocale(locale);
/* 653 */     return _this();
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
/*     */   public B defaultBase64Variant(Base64Variant v) {
/* 671 */     this._mapper.setBase64Variant(v);
/* 672 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B serializationInclusion(JsonInclude.Include incl) {
/* 683 */     this._mapper.setSerializationInclusion(incl);
/* 684 */     return _this();
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
/*     */   public B defaultPropertyInclusion(JsonInclude.Value incl) {
/* 697 */     this._mapper.setDefaultPropertyInclusion(incl);
/* 698 */     return _this();
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
/*     */   
/*     */   public B addMixIn(Class<?> target, Class<?> mixinSource) {
/* 729 */     this._mapper.addMixIn(target, mixinSource);
/* 730 */     return _this();
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
/*     */   public B removeMixIn(Class<?> target) {
/* 750 */     this._mapper.addMixIn(target, null);
/* 751 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B registerSubtypes(Class<?>... subtypes) {
/* 761 */     this._mapper.registerSubtypes(subtypes);
/* 762 */     return _this();
/*     */   }
/*     */   
/*     */   public B registerSubtypes(NamedType... subtypes) {
/* 766 */     this._mapper.registerSubtypes(subtypes);
/* 767 */     return _this();
/*     */   }
/*     */   
/*     */   public B registerSubtypes(Collection<Class<?>> subtypes) {
/* 771 */     this._mapper.registerSubtypes(subtypes);
/* 772 */     return _this();
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
/*     */   public B polymorphicTypeValidator(PolymorphicTypeValidator ptv) {
/* 789 */     this._mapper.setPolymorphicTypeValidator(ptv);
/* 790 */     return _this();
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
/*     */   public B activateDefaultTyping(PolymorphicTypeValidator subtypeValidator) {
/* 809 */     this._mapper.activateDefaultTyping(subtypeValidator);
/* 810 */     return _this();
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
/*     */   public B activateDefaultTyping(PolymorphicTypeValidator subtypeValidator, ObjectMapper.DefaultTyping dti) {
/* 824 */     this._mapper.activateDefaultTyping(subtypeValidator, dti);
/* 825 */     return _this();
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
/*     */   public B activateDefaultTyping(PolymorphicTypeValidator subtypeValidator, ObjectMapper.DefaultTyping applicability, JsonTypeInfo.As includeAs) {
/* 846 */     this._mapper.activateDefaultTyping(subtypeValidator, applicability, includeAs);
/* 847 */     return _this();
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
/*     */   public B activateDefaultTypingAsProperty(PolymorphicTypeValidator subtypeValidator, ObjectMapper.DefaultTyping applicability, String propertyName) {
/* 864 */     this._mapper.activateDefaultTypingAsProperty(subtypeValidator, applicability, propertyName);
/* 865 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B deactivateDefaultTyping() {
/* 875 */     this._mapper.deactivateDefaultTyping();
/* 876 */     return _this();
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
/*     */   public B setDefaultTyping(TypeResolverBuilder<?> typer) {
/* 897 */     this._mapper.setDefaultTyping(typer);
/* 898 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final B _this() {
/* 909 */     return (B)this;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/cfg/MapperBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */