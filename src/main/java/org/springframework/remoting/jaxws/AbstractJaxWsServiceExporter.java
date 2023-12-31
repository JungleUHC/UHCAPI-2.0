/*     */ package org.springframework.remoting.jaxws;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Executor;
/*     */ import javax.jws.WebService;
/*     */ import javax.xml.ws.Endpoint;
/*     */ import javax.xml.ws.WebServiceFeature;
/*     */ import javax.xml.ws.WebServiceProvider;
/*     */ import org.springframework.beans.factory.BeanFactory;
/*     */ import org.springframework.beans.factory.BeanFactoryAware;
/*     */ import org.springframework.beans.factory.CannotLoadBeanClassException;
/*     */ import org.springframework.beans.factory.DisposableBean;
/*     */ import org.springframework.beans.factory.InitializingBean;
/*     */ import org.springframework.beans.factory.ListableBeanFactory;
/*     */ import org.springframework.beans.factory.config.ConfigurableBeanFactory;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractJaxWsServiceExporter
/*     */   implements BeanFactoryAware, InitializingBean, DisposableBean
/*     */ {
/*     */   @Nullable
/*     */   private Map<String, Object> endpointProperties;
/*     */   @Nullable
/*     */   private Executor executor;
/*     */   @Nullable
/*     */   private String bindingType;
/*     */   @Nullable
/*     */   private WebServiceFeature[] endpointFeatures;
/*     */   @Nullable
/*     */   private ListableBeanFactory beanFactory;
/*  70 */   private final Set<Endpoint> publishedEndpoints = new LinkedHashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEndpointProperties(Map<String, Object> endpointProperties) {
/*  81 */     this.endpointProperties = endpointProperties;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExecutor(Executor executor) {
/*  90 */     this.executor = executor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBindingType(String bindingType) {
/*  98 */     this.bindingType = bindingType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEndpointFeatures(WebServiceFeature... endpointFeatures) {
/* 107 */     this.endpointFeatures = endpointFeatures;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBeanFactory(BeanFactory beanFactory) {
/* 115 */     if (!(beanFactory instanceof ListableBeanFactory)) {
/* 116 */       throw new IllegalStateException(getClass().getSimpleName() + " requires a ListableBeanFactory");
/*     */     }
/* 118 */     this.beanFactory = (ListableBeanFactory)beanFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void afterPropertiesSet() throws Exception {
/* 128 */     publishEndpoints();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void publishEndpoints() {
/* 137 */     Assert.state((this.beanFactory != null), "No BeanFactory set");
/*     */     
/* 139 */     Set<String> beanNames = new LinkedHashSet<>(this.beanFactory.getBeanDefinitionCount());
/* 140 */     Collections.addAll(beanNames, this.beanFactory.getBeanDefinitionNames());
/* 141 */     if (this.beanFactory instanceof ConfigurableBeanFactory) {
/* 142 */       Collections.addAll(beanNames, ((ConfigurableBeanFactory)this.beanFactory).getSingletonNames());
/*     */     }
/*     */     
/* 145 */     for (String beanName : beanNames) {
/*     */       try {
/* 147 */         Class<?> type = this.beanFactory.getType(beanName);
/* 148 */         if (type != null && !type.isInterface()) {
/* 149 */           WebService wsAnnotation = type.<WebService>getAnnotation(WebService.class);
/* 150 */           WebServiceProvider wsProviderAnnotation = type.<WebServiceProvider>getAnnotation(WebServiceProvider.class);
/* 151 */           if (wsAnnotation != null || wsProviderAnnotation != null) {
/* 152 */             Endpoint endpoint = createEndpoint(this.beanFactory.getBean(beanName));
/* 153 */             if (this.endpointProperties != null) {
/* 154 */               endpoint.setProperties(this.endpointProperties);
/*     */             }
/* 156 */             if (this.executor != null) {
/* 157 */               endpoint.setExecutor(this.executor);
/*     */             }
/* 159 */             if (wsAnnotation != null) {
/* 160 */               publishEndpoint(endpoint, wsAnnotation);
/*     */             } else {
/*     */               
/* 163 */               publishEndpoint(endpoint, wsProviderAnnotation);
/*     */             } 
/* 165 */             this.publishedEndpoints.add(endpoint);
/*     */           }
/*     */         
/*     */         } 
/* 169 */       } catch (CannotLoadBeanClassException cannotLoadBeanClassException) {}
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
/*     */   protected Endpoint createEndpoint(Object bean) {
/* 183 */     return (this.endpointFeatures != null) ? 
/* 184 */       Endpoint.create(this.bindingType, bean, this.endpointFeatures) : 
/* 185 */       Endpoint.create(this.bindingType, bean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void publishEndpoint(Endpoint paramEndpoint, WebService paramWebService);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void publishEndpoint(Endpoint paramEndpoint, WebServiceProvider paramWebServiceProvider);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void destroy() {
/* 209 */     for (Endpoint endpoint : this.publishedEndpoints)
/* 210 */       endpoint.stop(); 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/remoting/jaxws/AbstractJaxWsServiceExporter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */