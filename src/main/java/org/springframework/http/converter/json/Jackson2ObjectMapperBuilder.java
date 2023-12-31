/*     */ package org.springframework.http.converter.json;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonAutoDetect;
/*     */ import com.fasterxml.jackson.annotation.JsonInclude;
/*     */ import com.fasterxml.jackson.annotation.PropertyAccessor;
/*     */ import com.fasterxml.jackson.core.JsonFactory;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.Module;
/*     */ import com.fasterxml.jackson.databind.ObjectMapper;
/*     */ import com.fasterxml.jackson.databind.PropertyNamingStrategy;
/*     */ import com.fasterxml.jackson.databind.SerializationFeature;
/*     */ import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*     */ import com.fasterxml.jackson.databind.module.SimpleModule;
/*     */ import com.fasterxml.jackson.databind.ser.FilterProvider;
/*     */ import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
/*     */ import com.fasterxml.jackson.dataformat.smile.SmileFactory;
/*     */ import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
/*     */ import com.fasterxml.jackson.dataformat.xml.XmlFactory;
/*     */ import com.fasterxml.jackson.dataformat.xml.XmlMapper;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Function;
/*     */ import org.springframework.beans.BeanUtils;
/*     */ import org.springframework.beans.FatalBeanException;
/*     */ import org.springframework.context.ApplicationContext;
/*     */ import org.springframework.core.KotlinDetector;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.util.xml.StaxUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Jackson2ObjectMapperBuilder
/*     */ {
/* 106 */   private final Map<Class<?>, Class<?>> mixIns = new LinkedHashMap<>();
/*     */   
/* 108 */   private final Map<Class<?>, JsonSerializer<?>> serializers = new LinkedHashMap<>();
/*     */   
/* 110 */   private final Map<Class<?>, JsonDeserializer<?>> deserializers = new LinkedHashMap<>();
/*     */   
/* 112 */   private final Map<PropertyAccessor, JsonAutoDetect.Visibility> visibilities = new LinkedHashMap<>();
/*     */   
/* 114 */   private final Map<Object, Boolean> features = new LinkedHashMap<>();
/*     */   
/*     */   private boolean createXmlMapper = false;
/*     */   
/*     */   @Nullable
/*     */   private JsonFactory factory;
/*     */   
/*     */   @Nullable
/*     */   private DateFormat dateFormat;
/*     */   
/*     */   @Nullable
/*     */   private Locale locale;
/*     */   
/*     */   @Nullable
/*     */   private TimeZone timeZone;
/*     */   
/*     */   @Nullable
/*     */   private AnnotationIntrospector annotationIntrospector;
/*     */   
/*     */   @Nullable
/*     */   private PropertyNamingStrategy propertyNamingStrategy;
/*     */   
/*     */   @Nullable
/*     */   private TypeResolverBuilder<?> defaultTyping;
/*     */   
/*     */   @Nullable
/*     */   private JsonInclude.Value serializationInclusion;
/*     */   
/*     */   @Nullable
/*     */   private FilterProvider filters;
/*     */   
/*     */   @Nullable
/*     */   private List<Module> modules;
/*     */   
/*     */   @Nullable
/*     */   private Class<? extends Module>[] moduleClasses;
/*     */   
/*     */   private boolean findModulesViaServiceLoader = false;
/*     */   
/*     */   private boolean findWellKnownModules = true;
/*     */   
/* 155 */   private ClassLoader moduleClassLoader = getClass().getClassLoader();
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private HandlerInstantiator handlerInstantiator;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private ApplicationContext applicationContext;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Boolean defaultUseWrapper;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Consumer<ObjectMapper> configurer;
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder createXmlMapper(boolean createXmlMapper) {
/* 176 */     this.createXmlMapper = createXmlMapper;
/* 177 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder factory(JsonFactory factory) {
/* 186 */     this.factory = factory;
/* 187 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder dateFormat(DateFormat dateFormat) {
/* 197 */     this.dateFormat = dateFormat;
/* 198 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder simpleDateFormat(String format) {
/* 208 */     this.dateFormat = new SimpleDateFormat(format);
/* 209 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder locale(Locale locale) {
/* 218 */     this.locale = locale;
/* 219 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder locale(String localeString) {
/* 229 */     this.locale = StringUtils.parseLocale(localeString);
/* 230 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder timeZone(TimeZone timeZone) {
/* 239 */     this.timeZone = timeZone;
/* 240 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder timeZone(String timeZoneString) {
/* 250 */     this.timeZone = StringUtils.parseTimeZoneString(timeZoneString);
/* 251 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder annotationIntrospector(AnnotationIntrospector annotationIntrospector) {
/* 258 */     this.annotationIntrospector = annotationIntrospector;
/* 259 */     return this;
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
/*     */   public Jackson2ObjectMapperBuilder annotationIntrospector(Function<AnnotationIntrospector, AnnotationIntrospector> pairingFunction) {
/* 275 */     this.annotationIntrospector = pairingFunction.apply(this.annotationIntrospector);
/* 276 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder propertyNamingStrategy(PropertyNamingStrategy propertyNamingStrategy) {
/* 284 */     this.propertyNamingStrategy = propertyNamingStrategy;
/* 285 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder defaultTyping(TypeResolverBuilder<?> typeResolverBuilder) {
/* 293 */     this.defaultTyping = typeResolverBuilder;
/* 294 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder serializationInclusion(JsonInclude.Include inclusion) {
/* 302 */     return serializationInclusion(JsonInclude.Value.construct(inclusion, inclusion));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder serializationInclusion(JsonInclude.Value serializationInclusion) {
/* 311 */     this.serializationInclusion = serializationInclusion;
/* 312 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder filters(FilterProvider filters) {
/* 321 */     this.filters = filters;
/* 322 */     return this;
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
/*     */   public Jackson2ObjectMapperBuilder mixIn(Class<?> target, Class<?> mixinSource) {
/* 334 */     this.mixIns.put(target, mixinSource);
/* 335 */     return this;
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
/*     */   public Jackson2ObjectMapperBuilder mixIns(Map<Class<?>, Class<?>> mixIns) {
/* 347 */     this.mixIns.putAll(mixIns);
/* 348 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder serializers(JsonSerializer<?>... serializers) {
/* 357 */     for (JsonSerializer<?> serializer : serializers) {
/* 358 */       Class<?> handledType = serializer.handledType();
/* 359 */       if (handledType == null || handledType == Object.class) {
/* 360 */         throw new IllegalArgumentException("Unknown handled type in " + serializer.getClass().getName());
/*     */       }
/* 362 */       this.serializers.put(serializer.handledType(), serializer);
/*     */     } 
/* 364 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder serializerByType(Class<?> type, JsonSerializer<?> serializer) {
/* 373 */     this.serializers.put(type, serializer);
/* 374 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder serializersByType(Map<Class<?>, JsonSerializer<?>> serializers) {
/* 382 */     this.serializers.putAll(serializers);
/* 383 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder deserializers(JsonDeserializer<?>... deserializers) {
/* 393 */     for (JsonDeserializer<?> deserializer : deserializers) {
/* 394 */       Class<?> handledType = deserializer.handledType();
/* 395 */       if (handledType == null || handledType == Object.class) {
/* 396 */         throw new IllegalArgumentException("Unknown handled type in " + deserializer.getClass().getName());
/*     */       }
/* 398 */       this.deserializers.put(deserializer.handledType(), deserializer);
/*     */     } 
/* 400 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder deserializerByType(Class<?> type, JsonDeserializer<?> deserializer) {
/* 408 */     this.deserializers.put(type, deserializer);
/* 409 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder deserializersByType(Map<Class<?>, JsonDeserializer<?>> deserializers) {
/* 416 */     this.deserializers.putAll(deserializers);
/* 417 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder autoDetectFields(boolean autoDetectFields) {
/* 424 */     this.features.put(MapperFeature.AUTO_DETECT_FIELDS, Boolean.valueOf(autoDetectFields));
/* 425 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder autoDetectGettersSetters(boolean autoDetectGettersSetters) {
/* 434 */     this.features.put(MapperFeature.AUTO_DETECT_GETTERS, Boolean.valueOf(autoDetectGettersSetters));
/* 435 */     this.features.put(MapperFeature.AUTO_DETECT_SETTERS, Boolean.valueOf(autoDetectGettersSetters));
/* 436 */     this.features.put(MapperFeature.AUTO_DETECT_IS_GETTERS, Boolean.valueOf(autoDetectGettersSetters));
/* 437 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder defaultViewInclusion(boolean defaultViewInclusion) {
/* 444 */     this.features.put(MapperFeature.DEFAULT_VIEW_INCLUSION, Boolean.valueOf(defaultViewInclusion));
/* 445 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder failOnUnknownProperties(boolean failOnUnknownProperties) {
/* 452 */     this.features.put(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.valueOf(failOnUnknownProperties));
/* 453 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder failOnEmptyBeans(boolean failOnEmptyBeans) {
/* 460 */     this.features.put(SerializationFeature.FAIL_ON_EMPTY_BEANS, Boolean.valueOf(failOnEmptyBeans));
/* 461 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder indentOutput(boolean indentOutput) {
/* 468 */     this.features.put(SerializationFeature.INDENT_OUTPUT, Boolean.valueOf(indentOutput));
/* 469 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder defaultUseWrapper(boolean defaultUseWrapper) {
/* 478 */     this.defaultUseWrapper = Boolean.valueOf(defaultUseWrapper);
/* 479 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder visibility(PropertyAccessor accessor, JsonAutoDetect.Visibility visibility) {
/* 489 */     this.visibilities.put(accessor, visibility);
/* 490 */     return this;
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
/*     */   public Jackson2ObjectMapperBuilder featuresToEnable(Object... featuresToEnable) {
/* 502 */     for (Object feature : featuresToEnable) {
/* 503 */       this.features.put(feature, Boolean.TRUE);
/*     */     }
/* 505 */     return this;
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
/*     */   public Jackson2ObjectMapperBuilder featuresToDisable(Object... featuresToDisable) {
/* 517 */     for (Object feature : featuresToDisable) {
/* 518 */       this.features.put(feature, Boolean.FALSE);
/*     */     }
/* 520 */     return this;
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
/*     */   public Jackson2ObjectMapperBuilder modules(Module... modules) {
/* 537 */     return modules(Arrays.asList(modules));
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
/*     */   public Jackson2ObjectMapperBuilder modules(List<Module> modules) {
/* 553 */     this.modules = new ArrayList<>(modules);
/* 554 */     this.findModulesViaServiceLoader = false;
/* 555 */     this.findWellKnownModules = false;
/* 556 */     return this;
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
/*     */   public Jackson2ObjectMapperBuilder modulesToInstall(Module... modules) {
/* 573 */     this.modules = Arrays.asList(modules);
/* 574 */     this.findWellKnownModules = true;
/* 575 */     return this;
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
/*     */   @SafeVarargs
/*     */   public final Jackson2ObjectMapperBuilder modulesToInstall(Class<? extends Module>... modules) {
/* 594 */     this.moduleClasses = modules;
/* 595 */     this.findWellKnownModules = true;
/* 596 */     return this;
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
/*     */   public Jackson2ObjectMapperBuilder findModulesViaServiceLoader(boolean findModules) {
/* 608 */     this.findModulesViaServiceLoader = findModules;
/* 609 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder moduleClassLoader(ClassLoader moduleClassLoader) {
/* 616 */     this.moduleClassLoader = moduleClassLoader;
/* 617 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder handlerInstantiator(HandlerInstantiator handlerInstantiator) {
/* 627 */     this.handlerInstantiator = handlerInstantiator;
/* 628 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jackson2ObjectMapperBuilder applicationContext(ApplicationContext applicationContext) {
/* 638 */     this.applicationContext = applicationContext;
/* 639 */     return this;
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
/*     */   public Jackson2ObjectMapperBuilder postConfigurer(Consumer<ObjectMapper> configurer) {
/* 651 */     this.configurer = (this.configurer != null) ? this.configurer.andThen(configurer) : configurer;
/* 652 */     return this;
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
/*     */   public <T extends ObjectMapper> T build() {
/*     */     ObjectMapper mapper;
/* 666 */     if (this.createXmlMapper) {
/*     */ 
/*     */       
/* 669 */       mapper = (this.defaultUseWrapper != null) ? (new XmlObjectMapperInitializer()).create(this.defaultUseWrapper.booleanValue(), this.factory) : (new XmlObjectMapperInitializer()).create(this.factory);
/*     */     } else {
/*     */       
/* 672 */       mapper = (this.factory != null) ? new ObjectMapper(this.factory) : new ObjectMapper();
/*     */     } 
/* 674 */     configure(mapper);
/* 675 */     return (T)mapper;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void configure(ObjectMapper objectMapper) {
/* 684 */     Assert.notNull(objectMapper, "ObjectMapper must not be null");
/*     */     
/* 686 */     LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
/* 687 */     if (this.findModulesViaServiceLoader) {
/* 688 */       ObjectMapper.findModules(this.moduleClassLoader).forEach(module -> registerModule(module, modulesToRegister));
/*     */     }
/* 690 */     else if (this.findWellKnownModules) {
/* 691 */       registerWellKnownModulesIfAvailable((MultiValueMap<Object, Module>)linkedMultiValueMap);
/*     */     } 
/*     */     
/* 694 */     if (this.modules != null) {
/* 695 */       this.modules.forEach(module -> registerModule(module, modulesToRegister));
/*     */     }
/* 697 */     if (this.moduleClasses != null) {
/* 698 */       for (Class<? extends Module> moduleClass : this.moduleClasses) {
/* 699 */         registerModule((Module)BeanUtils.instantiateClass(moduleClass), (MultiValueMap<Object, Module>)linkedMultiValueMap);
/*     */       }
/*     */     }
/* 702 */     List<Module> modules = new ArrayList<>();
/* 703 */     for (List<Module> nestedModules : (Iterable<List<Module>>)linkedMultiValueMap.values()) {
/* 704 */       modules.addAll(nestedModules);
/*     */     }
/* 706 */     objectMapper.registerModules(modules);
/*     */     
/* 708 */     if (this.dateFormat != null) {
/* 709 */       objectMapper.setDateFormat(this.dateFormat);
/*     */     }
/* 711 */     if (this.locale != null) {
/* 712 */       objectMapper.setLocale(this.locale);
/*     */     }
/* 714 */     if (this.timeZone != null) {
/* 715 */       objectMapper.setTimeZone(this.timeZone);
/*     */     }
/*     */     
/* 718 */     if (this.annotationIntrospector != null) {
/* 719 */       objectMapper.setAnnotationIntrospector(this.annotationIntrospector);
/*     */     }
/* 721 */     if (this.propertyNamingStrategy != null) {
/* 722 */       objectMapper.setPropertyNamingStrategy(this.propertyNamingStrategy);
/*     */     }
/* 724 */     if (this.defaultTyping != null) {
/* 725 */       objectMapper.setDefaultTyping(this.defaultTyping);
/*     */     }
/* 727 */     if (this.serializationInclusion != null) {
/* 728 */       objectMapper.setDefaultPropertyInclusion(this.serializationInclusion);
/*     */     }
/*     */     
/* 731 */     if (this.filters != null) {
/* 732 */       objectMapper.setFilterProvider(this.filters);
/*     */     }
/*     */     
/* 735 */     this.mixIns.forEach(objectMapper::addMixIn);
/*     */     
/* 737 */     if (!this.serializers.isEmpty() || !this.deserializers.isEmpty()) {
/* 738 */       SimpleModule module = new SimpleModule();
/* 739 */       addSerializers(module);
/* 740 */       addDeserializers(module);
/* 741 */       objectMapper.registerModule((Module)module);
/*     */     } 
/*     */     
/* 744 */     this.visibilities.forEach(objectMapper::setVisibility);
/*     */     
/* 746 */     customizeDefaultFeatures(objectMapper);
/* 747 */     this.features.forEach((feature, enabled) -> configureFeature(objectMapper, feature, enabled.booleanValue()));
/*     */     
/* 749 */     if (this.handlerInstantiator != null) {
/* 750 */       objectMapper.setHandlerInstantiator(this.handlerInstantiator);
/*     */     }
/* 752 */     else if (this.applicationContext != null) {
/* 753 */       objectMapper.setHandlerInstantiator(new SpringHandlerInstantiator(this.applicationContext
/* 754 */             .getAutowireCapableBeanFactory()));
/*     */     } 
/*     */     
/* 757 */     if (this.configurer != null) {
/* 758 */       this.configurer.accept(objectMapper);
/*     */     }
/*     */   }
/*     */   
/*     */   private void registerModule(Module module, MultiValueMap<Object, Module> modulesToRegister) {
/* 763 */     if (module.getTypeId() == null) {
/* 764 */       modulesToRegister.add(SimpleModule.class.getName(), module);
/*     */     } else {
/*     */       
/* 767 */       modulesToRegister.set(module.getTypeId(), module);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void customizeDefaultFeatures(ObjectMapper objectMapper) {
/* 775 */     if (!this.features.containsKey(MapperFeature.DEFAULT_VIEW_INCLUSION)) {
/* 776 */       configureFeature(objectMapper, MapperFeature.DEFAULT_VIEW_INCLUSION, false);
/*     */     }
/* 778 */     if (!this.features.containsKey(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
/* 779 */       configureFeature(objectMapper, DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private <T> void addSerializers(SimpleModule module) {
/* 785 */     this.serializers.forEach((type, serializer) -> module.addSerializer(type, serializer));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private <T> void addDeserializers(SimpleModule module) {
/* 791 */     this.deserializers.forEach((type, deserializer) -> module.addDeserializer(type, deserializer));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void configureFeature(ObjectMapper objectMapper, Object feature, boolean enabled) {
/* 797 */     if (feature instanceof JsonParser.Feature) {
/* 798 */       objectMapper.configure((JsonParser.Feature)feature, enabled);
/*     */     }
/* 800 */     else if (feature instanceof JsonGenerator.Feature) {
/* 801 */       objectMapper.configure((JsonGenerator.Feature)feature, enabled);
/*     */     }
/* 803 */     else if (feature instanceof SerializationFeature) {
/* 804 */       objectMapper.configure((SerializationFeature)feature, enabled);
/*     */     }
/* 806 */     else if (feature instanceof DeserializationFeature) {
/* 807 */       objectMapper.configure((DeserializationFeature)feature, enabled);
/*     */     }
/* 809 */     else if (feature instanceof MapperFeature) {
/* 810 */       objectMapper.configure((MapperFeature)feature, enabled);
/*     */     } else {
/*     */       
/* 813 */       throw new FatalBeanException("Unknown feature class: " + feature.getClass().getName());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void registerWellKnownModulesIfAvailable(MultiValueMap<Object, Module> modulesToRegister) {
/*     */     try {
/* 821 */       Class<? extends Module> jdk8ModuleClass = ClassUtils.forName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module", this.moduleClassLoader);
/* 822 */       Module jdk8Module = (Module)BeanUtils.instantiateClass(jdk8ModuleClass);
/* 823 */       modulesToRegister.set(jdk8Module.getTypeId(), jdk8Module);
/*     */     }
/* 825 */     catch (ClassNotFoundException classNotFoundException) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 831 */       Class<? extends Module> javaTimeModuleClass = ClassUtils.forName("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule", this.moduleClassLoader);
/* 832 */       Module javaTimeModule = (Module)BeanUtils.instantiateClass(javaTimeModuleClass);
/* 833 */       modulesToRegister.set(javaTimeModule.getTypeId(), javaTimeModule);
/*     */     }
/* 835 */     catch (ClassNotFoundException classNotFoundException) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 840 */     if (ClassUtils.isPresent("org.joda.time.YearMonth", this.moduleClassLoader)) {
/*     */       
/*     */       try {
/* 843 */         Class<? extends Module> jodaModuleClass = ClassUtils.forName("com.fasterxml.jackson.datatype.joda.JodaModule", this.moduleClassLoader);
/* 844 */         Module jodaModule = (Module)BeanUtils.instantiateClass(jodaModuleClass);
/* 845 */         modulesToRegister.set(jodaModule.getTypeId(), jodaModule);
/*     */       }
/* 847 */       catch (ClassNotFoundException classNotFoundException) {}
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 853 */     if (KotlinDetector.isKotlinPresent()) {
/*     */       
/*     */       try {
/* 856 */         Class<? extends Module> kotlinModuleClass = ClassUtils.forName("com.fasterxml.jackson.module.kotlin.KotlinModule", this.moduleClassLoader);
/* 857 */         Module kotlinModule = (Module)BeanUtils.instantiateClass(kotlinModuleClass);
/* 858 */         modulesToRegister.set(kotlinModule.getTypeId(), kotlinModule);
/*     */       }
/* 860 */       catch (ClassNotFoundException classNotFoundException) {}
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
/*     */   public static Jackson2ObjectMapperBuilder json() {
/* 874 */     return new Jackson2ObjectMapperBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Jackson2ObjectMapperBuilder xml() {
/* 882 */     return (new Jackson2ObjectMapperBuilder()).createXmlMapper(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Jackson2ObjectMapperBuilder smile() {
/* 891 */     return (new Jackson2ObjectMapperBuilder()).factory((new SmileFactoryInitializer()).create());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Jackson2ObjectMapperBuilder cbor() {
/* 900 */     return (new Jackson2ObjectMapperBuilder()).factory((new CborFactoryInitializer()).create());
/*     */   }
/*     */   
/*     */   private static class XmlObjectMapperInitializer {
/*     */     private XmlObjectMapperInitializer() {}
/*     */     
/*     */     public ObjectMapper create(@Nullable JsonFactory factory) {
/* 907 */       if (factory != null) {
/* 908 */         return (ObjectMapper)new XmlMapper((XmlFactory)factory);
/*     */       }
/*     */       
/* 911 */       return (ObjectMapper)new XmlMapper(StaxUtils.createDefensiveInputFactory());
/*     */     }
/*     */ 
/*     */     
/*     */     public ObjectMapper create(boolean defaultUseWrapper, @Nullable JsonFactory factory) {
/* 916 */       JacksonXmlModule module = new JacksonXmlModule();
/* 917 */       module.setDefaultUseWrapper(defaultUseWrapper);
/* 918 */       if (factory != null) {
/* 919 */         return (ObjectMapper)new XmlMapper((XmlFactory)factory, module);
/*     */       }
/*     */       
/* 922 */       return (ObjectMapper)new XmlMapper(new XmlFactory(StaxUtils.createDefensiveInputFactory()), module);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class SmileFactoryInitializer
/*     */   {
/*     */     private SmileFactoryInitializer() {}
/*     */     
/*     */     public JsonFactory create() {
/* 931 */       return (JsonFactory)new SmileFactory();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class CborFactoryInitializer {
/*     */     private CborFactoryInitializer() {}
/*     */     
/*     */     public JsonFactory create() {
/* 939 */       return (JsonFactory)new CBORFactory();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/json/Jackson2ObjectMapperBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */