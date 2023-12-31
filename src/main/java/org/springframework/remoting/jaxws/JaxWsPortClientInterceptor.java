/*     */ package org.springframework.remoting.jaxws;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.jws.WebService;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.ws.BindingProvider;
/*     */ import javax.xml.ws.ProtocolException;
/*     */ import javax.xml.ws.Service;
/*     */ import javax.xml.ws.WebServiceException;
/*     */ import javax.xml.ws.WebServiceFeature;
/*     */ import javax.xml.ws.soap.SOAPFaultException;
/*     */ import org.aopalliance.intercept.MethodInterceptor;
/*     */ import org.aopalliance.intercept.MethodInvocation;
/*     */ import org.springframework.aop.support.AopUtils;
/*     */ import org.springframework.beans.factory.BeanClassLoaderAware;
/*     */ import org.springframework.beans.factory.InitializingBean;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.remoting.RemoteAccessException;
/*     */ import org.springframework.remoting.RemoteConnectFailureException;
/*     */ import org.springframework.remoting.RemoteLookupFailureException;
/*     */ import org.springframework.remoting.RemoteProxyFailureException;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JaxWsPortClientInterceptor
/*     */   extends LocalJaxWsServiceFactory
/*     */   implements MethodInterceptor, BeanClassLoaderAware, InitializingBean
/*     */ {
/*     */   @Nullable
/*     */   private Service jaxWsService;
/*     */   @Nullable
/*     */   private String portName;
/*     */   @Nullable
/*     */   private String username;
/*     */   @Nullable
/*     */   private String password;
/*     */   @Nullable
/*     */   private String endpointAddress;
/*     */   private boolean maintainSession;
/*     */   private boolean useSoapAction;
/*     */   @Nullable
/*     */   private String soapActionUri;
/*     */   @Nullable
/*     */   private Map<String, Object> customProperties;
/*     */   @Nullable
/*     */   private WebServiceFeature[] portFeatures;
/*     */   @Nullable
/*     */   private Class<?> serviceInterface;
/*     */   private boolean lookupServiceOnStartup = true;
/*     */   @Nullable
/* 103 */   private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
/*     */   
/*     */   @Nullable
/*     */   private QName portQName;
/*     */   
/*     */   @Nullable
/*     */   private Object portStub;
/*     */   
/* 111 */   private final Object preparationMonitor = new Object();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setJaxWsService(@Nullable Service jaxWsService) {
/* 124 */     this.jaxWsService = jaxWsService;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Service getJaxWsService() {
/* 132 */     return this.jaxWsService;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPortName(@Nullable String portName) {
/* 140 */     this.portName = portName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getPortName() {
/* 148 */     return this.portName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUsername(@Nullable String username) {
/* 156 */     this.username = username;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getUsername() {
/* 164 */     return this.username;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPassword(@Nullable String password) {
/* 172 */     this.password = password;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getPassword() {
/* 180 */     return this.password;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEndpointAddress(@Nullable String endpointAddress) {
/* 188 */     this.endpointAddress = endpointAddress;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getEndpointAddress() {
/* 196 */     return this.endpointAddress;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaintainSession(boolean maintainSession) {
/* 204 */     this.maintainSession = maintainSession;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isMaintainSession() {
/* 211 */     return this.maintainSession;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUseSoapAction(boolean useSoapAction) {
/* 219 */     this.useSoapAction = useSoapAction;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isUseSoapAction() {
/* 226 */     return this.useSoapAction;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSoapActionUri(@Nullable String soapActionUri) {
/* 234 */     this.soapActionUri = soapActionUri;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getSoapActionUri() {
/* 242 */     return this.soapActionUri;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCustomProperties(Map<String, Object> customProperties) {
/* 252 */     this.customProperties = customProperties;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, Object> getCustomProperties() {
/* 263 */     if (this.customProperties == null) {
/* 264 */       this.customProperties = new HashMap<>();
/*     */     }
/* 266 */     return this.customProperties;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addCustomProperty(String name, Object value) {
/* 276 */     getCustomProperties().put(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPortFeatures(WebServiceFeature... features) {
/* 287 */     this.portFeatures = features;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setServiceInterface(@Nullable Class<?> serviceInterface) {
/* 294 */     if (serviceInterface != null) {
/* 295 */       Assert.isTrue(serviceInterface.isInterface(), "'serviceInterface' must be an interface");
/*     */     }
/* 297 */     this.serviceInterface = serviceInterface;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Class<?> getServiceInterface() {
/* 305 */     return this.serviceInterface;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLookupServiceOnStartup(boolean lookupServiceOnStartup) {
/* 315 */     this.lookupServiceOnStartup = lookupServiceOnStartup;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBeanClassLoader(@Nullable ClassLoader classLoader) {
/* 324 */     this.beanClassLoader = classLoader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected ClassLoader getBeanClassLoader() {
/* 332 */     return this.beanClassLoader;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void afterPropertiesSet() {
/* 338 */     if (this.lookupServiceOnStartup) {
/* 339 */       prepare();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void prepare() {
/* 347 */     Class<?> ifc = getServiceInterface();
/* 348 */     Assert.notNull(ifc, "Property 'serviceInterface' is required");
/*     */     
/* 350 */     WebService ann = ifc.<WebService>getAnnotation(WebService.class);
/* 351 */     if (ann != null) {
/* 352 */       applyDefaultsFromAnnotation(ann);
/*     */     }
/*     */     
/* 355 */     Service serviceToUse = getJaxWsService();
/* 356 */     if (serviceToUse == null) {
/* 357 */       serviceToUse = createJaxWsService();
/*     */     }
/*     */     
/* 360 */     this.portQName = getQName((getPortName() != null) ? getPortName() : ifc.getName());
/* 361 */     Object stub = getPortStub(serviceToUse, (getPortName() != null) ? this.portQName : null);
/* 362 */     preparePortStub(stub);
/* 363 */     this.portStub = stub;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void applyDefaultsFromAnnotation(WebService ann) {
/* 374 */     if (getWsdlDocumentUrl() == null) {
/* 375 */       String wsdl = ann.wsdlLocation();
/* 376 */       if (StringUtils.hasText(wsdl)) {
/*     */         try {
/* 378 */           setWsdlDocumentUrl(new URL(wsdl));
/*     */         }
/* 380 */         catch (MalformedURLException ex) {
/* 381 */           throw new IllegalStateException("Encountered invalid @Service wsdlLocation value [" + wsdl + "]", ex);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 386 */     if (getNamespaceUri() == null) {
/* 387 */       String ns = ann.targetNamespace();
/* 388 */       if (StringUtils.hasText(ns)) {
/* 389 */         setNamespaceUri(ns);
/*     */       }
/*     */     } 
/* 392 */     if (getServiceName() == null) {
/* 393 */       String sn = ann.serviceName();
/* 394 */       if (StringUtils.hasText(sn)) {
/* 395 */         setServiceName(sn);
/*     */       }
/*     */     } 
/* 398 */     if (getPortName() == null) {
/* 399 */       String pn = ann.portName();
/* 400 */       if (StringUtils.hasText(pn)) {
/* 401 */         setPortName(pn);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isPrepared() {
/* 411 */     synchronized (this.preparationMonitor) {
/* 412 */       return (this.portStub != null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected final QName getPortQName() {
/* 423 */     return this.portQName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object getPortStub(Service service, @Nullable QName portQName) {
/* 434 */     if (this.portFeatures != null) {
/* 435 */       return (portQName != null) ? service.getPort(portQName, getServiceInterface(), this.portFeatures) : service
/* 436 */         .getPort(getServiceInterface(), this.portFeatures);
/*     */     }
/*     */     
/* 439 */     return (portQName != null) ? service.getPort(portQName, getServiceInterface()) : service
/* 440 */       .getPort(getServiceInterface());
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
/*     */   protected void preparePortStub(Object stub) {
/* 455 */     Map<String, Object> stubProperties = new HashMap<>();
/* 456 */     String username = getUsername();
/* 457 */     if (username != null) {
/* 458 */       stubProperties.put("javax.xml.ws.security.auth.username", username);
/*     */     }
/* 460 */     String password = getPassword();
/* 461 */     if (password != null) {
/* 462 */       stubProperties.put("javax.xml.ws.security.auth.password", password);
/*     */     }
/* 464 */     String endpointAddress = getEndpointAddress();
/* 465 */     if (endpointAddress != null) {
/* 466 */       stubProperties.put("javax.xml.ws.service.endpoint.address", endpointAddress);
/*     */     }
/* 468 */     if (isMaintainSession()) {
/* 469 */       stubProperties.put("javax.xml.ws.session.maintain", Boolean.TRUE);
/*     */     }
/* 471 */     if (isUseSoapAction()) {
/* 472 */       stubProperties.put("javax.xml.ws.soap.http.soapaction.use", Boolean.TRUE);
/*     */     }
/* 474 */     String soapActionUri = getSoapActionUri();
/* 475 */     if (soapActionUri != null) {
/* 476 */       stubProperties.put("javax.xml.ws.soap.http.soapaction.uri", soapActionUri);
/*     */     }
/* 478 */     stubProperties.putAll(getCustomProperties());
/* 479 */     if (!stubProperties.isEmpty()) {
/* 480 */       if (!(stub instanceof BindingProvider)) {
/* 481 */         throw new RemoteLookupFailureException("Port stub of class [" + stub.getClass().getName() + "] is not a customizable JAX-WS stub: it does not implement interface [javax.xml.ws.BindingProvider]");
/*     */       }
/*     */       
/* 484 */       ((BindingProvider)stub).getRequestContext().putAll(stubProperties);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected Object getPortStub() {
/* 494 */     return this.portStub;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object invoke(MethodInvocation invocation) throws Throwable {
/* 501 */     if (AopUtils.isToStringMethod(invocation.getMethod())) {
/* 502 */       return "JAX-WS proxy for port [" + getPortName() + "] of service [" + getServiceName() + "]";
/*     */     }
/*     */     
/* 505 */     synchronized (this.preparationMonitor) {
/* 506 */       if (!isPrepared()) {
/* 507 */         prepare();
/*     */       }
/*     */     } 
/* 510 */     return doInvoke(invocation);
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
/*     */   @Nullable
/*     */   protected Object doInvoke(MethodInvocation invocation) throws Throwable {
/*     */     try {
/* 524 */       return doInvoke(invocation, getPortStub());
/*     */     }
/* 526 */     catch (SOAPFaultException ex) {
/* 527 */       throw new JaxWsSoapFaultException(ex);
/*     */     }
/* 529 */     catch (ProtocolException ex) {
/* 530 */       throw new RemoteConnectFailureException("Could not connect to remote service [" + 
/* 531 */           getEndpointAddress() + "]", ex);
/*     */     }
/* 533 */     catch (WebServiceException ex) {
/* 534 */       throw new RemoteAccessException("Could not access remote service at [" + 
/* 535 */           getEndpointAddress() + "]", ex);
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
/*     */   @Nullable
/*     */   protected Object doInvoke(MethodInvocation invocation, @Nullable Object portStub) throws Throwable {
/* 549 */     Method method = invocation.getMethod();
/*     */     try {
/* 551 */       return method.invoke(portStub, invocation.getArguments());
/*     */     }
/* 553 */     catch (InvocationTargetException ex) {
/* 554 */       throw ex.getTargetException();
/*     */     }
/* 556 */     catch (Throwable ex) {
/* 557 */       throw new RemoteProxyFailureException("Invocation of stub method failed: " + method, ex);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/remoting/jaxws/JaxWsPortClientInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */