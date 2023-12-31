/*     */ package org.springframework.remoting.jaxws;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.util.concurrent.Executor;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.ws.Service;
/*     */ import javax.xml.ws.WebServiceFeature;
/*     */ import javax.xml.ws.handler.HandlerResolver;
/*     */ import org.springframework.core.io.Resource;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LocalJaxWsServiceFactory
/*     */ {
/*     */   @Nullable
/*     */   private URL wsdlDocumentUrl;
/*     */   @Nullable
/*     */   private String namespaceUri;
/*     */   @Nullable
/*     */   private String serviceName;
/*     */   @Nullable
/*     */   private WebServiceFeature[] serviceFeatures;
/*     */   @Nullable
/*     */   private Executor executor;
/*     */   @Nullable
/*     */   private HandlerResolver handlerResolver;
/*     */   
/*     */   public void setWsdlDocumentUrl(@Nullable URL wsdlDocumentUrl) {
/*  72 */     this.wsdlDocumentUrl = wsdlDocumentUrl;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWsdlDocumentResource(Resource wsdlDocumentResource) throws IOException {
/*  80 */     Assert.notNull(wsdlDocumentResource, "WSDL Resource must not be null");
/*  81 */     this.wsdlDocumentUrl = wsdlDocumentResource.getURL();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public URL getWsdlDocumentUrl() {
/*  89 */     return this.wsdlDocumentUrl;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNamespaceUri(@Nullable String namespaceUri) {
/*  97 */     this.namespaceUri = (namespaceUri != null) ? namespaceUri.trim() : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getNamespaceUri() {
/* 105 */     return this.namespaceUri;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setServiceName(@Nullable String serviceName) {
/* 113 */     this.serviceName = serviceName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getServiceName() {
/* 121 */     return this.serviceName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setServiceFeatures(WebServiceFeature... serviceFeatures) {
/* 131 */     this.serviceFeatures = serviceFeatures;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExecutor(Executor executor) {
/* 140 */     this.executor = executor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHandlerResolver(HandlerResolver handlerResolver) {
/* 149 */     this.handlerResolver = handlerResolver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Service createJaxWsService() {
/*     */     Service service;
/* 159 */     Assert.notNull(this.serviceName, "No service name specified");
/*     */ 
/*     */     
/* 162 */     if (this.serviceFeatures != null) {
/*     */ 
/*     */       
/* 165 */       service = (this.wsdlDocumentUrl != null) ? Service.create(this.wsdlDocumentUrl, getQName(this.serviceName), this.serviceFeatures) : Service.create(getQName(this.serviceName), this.serviceFeatures);
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 170 */       service = (this.wsdlDocumentUrl != null) ? Service.create(this.wsdlDocumentUrl, getQName(this.serviceName)) : Service.create(getQName(this.serviceName));
/*     */     } 
/*     */     
/* 173 */     if (this.executor != null) {
/* 174 */       service.setExecutor(this.executor);
/*     */     }
/* 176 */     if (this.handlerResolver != null) {
/* 177 */       service.setHandlerResolver(this.handlerResolver);
/*     */     }
/*     */     
/* 180 */     return service;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected QName getQName(String name) {
/* 189 */     return (getNamespaceUri() != null) ? new QName(getNamespaceUri(), name) : new QName(name);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/remoting/jaxws/LocalJaxWsServiceFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */