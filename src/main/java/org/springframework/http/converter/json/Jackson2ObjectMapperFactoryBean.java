/*     */ package org.springframework.http.converter.json;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonInclude;
/*     */ import com.fasterxml.jackson.core.JsonFactory;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.Module;
/*     */ import com.fasterxml.jackson.databind.ObjectMapper;
/*     */ import com.fasterxml.jackson.databind.PropertyNamingStrategy;
/*     */ import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*     */ import com.fasterxml.jackson.databind.ser.FilterProvider;
/*     */ import java.text.DateFormat;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ import org.springframework.beans.factory.BeanClassLoaderAware;
/*     */ import org.springframework.beans.factory.FactoryBean;
/*     */ import org.springframework.beans.factory.InitializingBean;
/*     */ import org.springframework.context.ApplicationContext;
/*     */ import org.springframework.context.ApplicationContextAware;
/*     */ import org.springframework.lang.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Jackson2ObjectMapperFactoryBean
/*     */   implements FactoryBean<ObjectMapper>, BeanClassLoaderAware, ApplicationContextAware, InitializingBean
/*     */ {
/* 145 */   private final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private ObjectMapper objectMapper;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setObjectMapper(ObjectMapper objectMapper) {
/* 156 */     this.objectMapper = objectMapper;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCreateXmlMapper(boolean createXmlMapper) {
/* 165 */     this.builder.createXmlMapper(createXmlMapper);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFactory(JsonFactory factory) {
/* 174 */     this.builder.factory(factory);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDateFormat(DateFormat dateFormat) {
/* 184 */     this.builder.dateFormat(dateFormat);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSimpleDateFormat(String format) {
/* 194 */     this.builder.simpleDateFormat(format);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLocale(Locale locale) {
/* 203 */     this.builder.locale(locale);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTimeZone(TimeZone timeZone) {
/* 212 */     this.builder.timeZone(timeZone);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAnnotationIntrospector(AnnotationIntrospector annotationIntrospector) {
/* 219 */     this.builder.annotationIntrospector(annotationIntrospector);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPropertyNamingStrategy(PropertyNamingStrategy propertyNamingStrategy) {
/* 228 */     this.builder.propertyNamingStrategy(propertyNamingStrategy);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDefaultTyping(TypeResolverBuilder<?> typeResolverBuilder) {
/* 236 */     this.builder.defaultTyping(typeResolverBuilder);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSerializationInclusion(JsonInclude.Include serializationInclusion) {
/* 244 */     this.builder.serializationInclusion(serializationInclusion);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFilters(FilterProvider filters) {
/* 253 */     this.builder.filters(filters);
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
/*     */   public void setMixIns(Map<Class<?>, Class<?>> mixIns) {
/* 265 */     this.builder.mixIns(mixIns);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSerializers(JsonSerializer<?>... serializers) {
/* 274 */     this.builder.serializers(serializers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSerializersByType(Map<Class<?>, JsonSerializer<?>> serializers) {
/* 282 */     this.builder.serializersByType(serializers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDeserializers(JsonDeserializer<?>... deserializers) {
/* 292 */     this.builder.deserializers(deserializers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDeserializersByType(Map<Class<?>, JsonDeserializer<?>> deserializers) {
/* 299 */     this.builder.deserializersByType(deserializers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAutoDetectFields(boolean autoDetectFields) {
/* 306 */     this.builder.autoDetectFields(autoDetectFields);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAutoDetectGettersSetters(boolean autoDetectGettersSetters) {
/* 315 */     this.builder.autoDetectGettersSetters(autoDetectGettersSetters);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDefaultViewInclusion(boolean defaultViewInclusion) {
/* 323 */     this.builder.defaultViewInclusion(defaultViewInclusion);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFailOnUnknownProperties(boolean failOnUnknownProperties) {
/* 331 */     this.builder.failOnUnknownProperties(failOnUnknownProperties);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFailOnEmptyBeans(boolean failOnEmptyBeans) {
/* 338 */     this.builder.failOnEmptyBeans(failOnEmptyBeans);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIndentOutput(boolean indentOutput) {
/* 345 */     this.builder.indentOutput(indentOutput);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDefaultUseWrapper(boolean defaultUseWrapper) {
/* 354 */     this.builder.defaultUseWrapper(defaultUseWrapper);
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
/*     */   public void setFeaturesToEnable(Object... featuresToEnable) {
/* 366 */     this.builder.featuresToEnable(featuresToEnable);
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
/*     */   public void setFeaturesToDisable(Object... featuresToDisable) {
/* 378 */     this.builder.featuresToDisable(featuresToDisable);
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
/*     */   public void setModules(List<Module> modules) {
/* 392 */     this.builder.modules(modules);
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
/*     */   @SafeVarargs
/*     */   public final void setModulesToInstall(Class<? extends Module>... modules) {
/* 408 */     this.builder.modulesToInstall(modules);
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
/*     */   public void setFindModulesViaServiceLoader(boolean findModules) {
/* 421 */     this.builder.findModulesViaServiceLoader(findModules);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBeanClassLoader(ClassLoader beanClassLoader) {
/* 426 */     this.builder.moduleClassLoader(beanClassLoader);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHandlerInstantiator(HandlerInstantiator handlerInstantiator) {
/* 437 */     this.builder.handlerInstantiator(handlerInstantiator);
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
/*     */   public void setApplicationContext(ApplicationContext applicationContext) {
/* 450 */     this.builder.applicationContext(applicationContext);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void afterPropertiesSet() {
/* 456 */     if (this.objectMapper != null) {
/* 457 */       this.builder.configure(this.objectMapper);
/*     */     } else {
/*     */       
/* 460 */       this.objectMapper = this.builder.build();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public ObjectMapper getObject() {
/* 470 */     return this.objectMapper;
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> getObjectType() {
/* 475 */     return (this.objectMapper != null) ? this.objectMapper.getClass() : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSingleton() {
/* 480 */     return true;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/json/Jackson2ObjectMapperFactoryBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */