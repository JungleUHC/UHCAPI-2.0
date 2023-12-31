/*     */ package org.springframework.beans.factory.xml;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.springframework.beans.BeanUtils;
/*     */ import org.springframework.beans.factory.BeanDefinitionStoreException;
/*     */ import org.springframework.beans.factory.parsing.EmptyReaderEventListener;
/*     */ import org.springframework.beans.factory.parsing.FailFastProblemReporter;
/*     */ import org.springframework.beans.factory.parsing.NullSourceExtractor;
/*     */ import org.springframework.beans.factory.parsing.ProblemReporter;
/*     */ import org.springframework.beans.factory.parsing.ReaderEventListener;
/*     */ import org.springframework.beans.factory.parsing.SourceExtractor;
/*     */ import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
/*     */ import org.springframework.beans.factory.support.BeanDefinitionRegistry;
/*     */ import org.springframework.core.Constants;
/*     */ import org.springframework.core.NamedThreadLocal;
/*     */ import org.springframework.core.io.DescriptiveResource;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.core.io.ResourceLoader;
/*     */ import org.springframework.core.io.support.EncodedResource;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.xml.SimpleSaxErrorHandler;
/*     */ import org.springframework.util.xml.XmlValidationModeDetector;
/*     */ import org.w3c.dom.Document;
/*     */ import org.xml.sax.EntityResolver;
/*     */ import org.xml.sax.ErrorHandler;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXParseException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class XmlBeanDefinitionReader
/*     */   extends AbstractBeanDefinitionReader
/*     */ {
/*     */   public static final int VALIDATION_NONE = 0;
/*     */   public static final int VALIDATION_AUTO = 1;
/*     */   public static final int VALIDATION_DTD = 2;
/*     */   public static final int VALIDATION_XSD = 3;
/* 103 */   private static final Constants constants = new Constants(XmlBeanDefinitionReader.class);
/*     */   
/* 105 */   private int validationMode = 1;
/*     */   
/*     */   private boolean namespaceAware = false;
/*     */   
/* 109 */   private Class<? extends BeanDefinitionDocumentReader> documentReaderClass = (Class)DefaultBeanDefinitionDocumentReader.class;
/*     */ 
/*     */   
/* 112 */   private ProblemReporter problemReporter = (ProblemReporter)new FailFastProblemReporter();
/*     */   
/* 114 */   private ReaderEventListener eventListener = (ReaderEventListener)new EmptyReaderEventListener();
/*     */   
/* 116 */   private SourceExtractor sourceExtractor = (SourceExtractor)new NullSourceExtractor();
/*     */   
/*     */   @Nullable
/*     */   private NamespaceHandlerResolver namespaceHandlerResolver;
/*     */   
/* 121 */   private DocumentLoader documentLoader = new DefaultDocumentLoader();
/*     */   
/*     */   @Nullable
/*     */   private EntityResolver entityResolver;
/*     */   
/* 126 */   private ErrorHandler errorHandler = (ErrorHandler)new SimpleSaxErrorHandler(this.logger);
/*     */   
/* 128 */   private final XmlValidationModeDetector validationModeDetector = new XmlValidationModeDetector();
/*     */   
/* 130 */   private final ThreadLocal<Set<EncodedResource>> resourcesCurrentlyBeingLoaded = (ThreadLocal<Set<EncodedResource>>)new NamedThreadLocal<Set<EncodedResource>>("XML bean definition resources currently being loaded")
/*     */     {
/*     */       protected Set<EncodedResource> initialValue()
/*     */       {
/* 134 */         return new HashSet<>(4);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
/* 145 */     super(registry);
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
/*     */   public void setValidating(boolean validating) {
/* 157 */     this.validationMode = validating ? 1 : 0;
/* 158 */     this.namespaceAware = !validating;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValidationModeName(String validationModeName) {
/* 166 */     setValidationMode(constants.asNumber(validationModeName).intValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValidationMode(int validationMode) {
/* 176 */     this.validationMode = validationMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getValidationMode() {
/* 183 */     return this.validationMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNamespaceAware(boolean namespaceAware) {
/* 194 */     this.namespaceAware = namespaceAware;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNamespaceAware() {
/* 201 */     return this.namespaceAware;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProblemReporter(@Nullable ProblemReporter problemReporter) {
/* 211 */     this.problemReporter = (problemReporter != null) ? problemReporter : (ProblemReporter)new FailFastProblemReporter();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEventListener(@Nullable ReaderEventListener eventListener) {
/* 221 */     this.eventListener = (eventListener != null) ? eventListener : (ReaderEventListener)new EmptyReaderEventListener();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSourceExtractor(@Nullable SourceExtractor sourceExtractor) {
/* 231 */     this.sourceExtractor = (sourceExtractor != null) ? sourceExtractor : (SourceExtractor)new NullSourceExtractor();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNamespaceHandlerResolver(@Nullable NamespaceHandlerResolver namespaceHandlerResolver) {
/* 240 */     this.namespaceHandlerResolver = namespaceHandlerResolver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDocumentLoader(@Nullable DocumentLoader documentLoader) {
/* 249 */     this.documentLoader = (documentLoader != null) ? documentLoader : new DefaultDocumentLoader();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEntityResolver(@Nullable EntityResolver entityResolver) {
/* 258 */     this.entityResolver = entityResolver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected EntityResolver getEntityResolver() {
/* 266 */     if (this.entityResolver == null) {
/*     */       
/* 268 */       ResourceLoader resourceLoader = getResourceLoader();
/* 269 */       if (resourceLoader != null) {
/* 270 */         this.entityResolver = new ResourceEntityResolver(resourceLoader);
/*     */       } else {
/*     */         
/* 273 */         this.entityResolver = new DelegatingEntityResolver(getBeanClassLoader());
/*     */       } 
/*     */     } 
/* 276 */     return this.entityResolver;
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
/*     */   public void setErrorHandler(ErrorHandler errorHandler) {
/* 288 */     this.errorHandler = errorHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDocumentReaderClass(Class<? extends BeanDefinitionDocumentReader> documentReaderClass) {
/* 298 */     this.documentReaderClass = documentReaderClass;
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
/*     */   public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
/* 310 */     return loadBeanDefinitions(new EncodedResource(resource));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
/* 321 */     Assert.notNull(encodedResource, "EncodedResource must not be null");
/* 322 */     if (this.logger.isTraceEnabled()) {
/* 323 */       this.logger.trace("Loading XML bean definitions from " + encodedResource);
/*     */     }
/*     */     
/* 326 */     Set<EncodedResource> currentResources = this.resourcesCurrentlyBeingLoaded.get();
/*     */     
/* 328 */     if (!currentResources.add(encodedResource)) {
/* 329 */       throw new BeanDefinitionStoreException("Detected cyclic loading of " + encodedResource + " - check your import definitions!");
/*     */     }
/*     */ 
/*     */     
/* 333 */     try (InputStream inputStream = encodedResource.getResource().getInputStream()) {
/* 334 */       InputSource inputSource = new InputSource(inputStream);
/* 335 */       if (encodedResource.getEncoding() != null) {
/* 336 */         inputSource.setEncoding(encodedResource.getEncoding());
/*     */       }
/* 338 */       return doLoadBeanDefinitions(inputSource, encodedResource.getResource());
/*     */     }
/* 340 */     catch (IOException ex) {
/* 341 */       throw new BeanDefinitionStoreException("IOException parsing XML document from " + encodedResource
/* 342 */           .getResource(), ex);
/*     */     } finally {
/*     */       
/* 345 */       currentResources.remove(encodedResource);
/* 346 */       if (currentResources.isEmpty()) {
/* 347 */         this.resourcesCurrentlyBeingLoaded.remove();
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
/*     */   public int loadBeanDefinitions(InputSource inputSource) throws BeanDefinitionStoreException {
/* 359 */     return loadBeanDefinitions(inputSource, "resource loaded through SAX InputSource");
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
/*     */   public int loadBeanDefinitions(InputSource inputSource, @Nullable String resourceDescription) throws BeanDefinitionStoreException {
/* 373 */     return doLoadBeanDefinitions(inputSource, (Resource)new DescriptiveResource(resourceDescription));
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
/*     */   protected int doLoadBeanDefinitions(InputSource inputSource, Resource resource) throws BeanDefinitionStoreException {
/*     */     try {
/* 390 */       Document doc = doLoadDocument(inputSource, resource);
/* 391 */       int count = registerBeanDefinitions(doc, resource);
/* 392 */       if (this.logger.isDebugEnabled()) {
/* 393 */         this.logger.debug("Loaded " + count + " bean definitions from " + resource);
/*     */       }
/* 395 */       return count;
/*     */     }
/* 397 */     catch (BeanDefinitionStoreException ex) {
/* 398 */       throw ex;
/*     */     }
/* 400 */     catch (SAXParseException ex) {
/* 401 */       throw new XmlBeanDefinitionStoreException(resource.getDescription(), "Line " + ex
/* 402 */           .getLineNumber() + " in XML document from " + resource + " is invalid", ex);
/*     */     }
/* 404 */     catch (SAXException ex) {
/* 405 */       throw new XmlBeanDefinitionStoreException(resource.getDescription(), "XML document from " + resource + " is invalid", ex);
/*     */     
/*     */     }
/* 408 */     catch (ParserConfigurationException ex) {
/* 409 */       throw new BeanDefinitionStoreException(resource.getDescription(), "Parser configuration exception parsing XML from " + resource, ex);
/*     */     
/*     */     }
/* 412 */     catch (IOException ex) {
/* 413 */       throw new BeanDefinitionStoreException(resource.getDescription(), "IOException parsing XML document from " + resource, ex);
/*     */     
/*     */     }
/* 416 */     catch (Throwable ex) {
/* 417 */       throw new BeanDefinitionStoreException(resource.getDescription(), "Unexpected exception parsing XML document from " + resource, ex);
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
/*     */   
/*     */   protected Document doLoadDocument(InputSource inputSource, Resource resource) throws Exception {
/* 432 */     return this.documentLoader.loadDocument(inputSource, getEntityResolver(), this.errorHandler, 
/* 433 */         getValidationModeForResource(resource), isNamespaceAware());
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
/*     */   protected int getValidationModeForResource(Resource resource) {
/* 445 */     int validationModeToUse = getValidationMode();
/* 446 */     if (validationModeToUse != 1) {
/* 447 */       return validationModeToUse;
/*     */     }
/* 449 */     int detectedMode = detectValidationMode(resource);
/* 450 */     if (detectedMode != 1) {
/* 451 */       return detectedMode;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 456 */     return 3;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int detectValidationMode(Resource resource) {
/*     */     InputStream inputStream;
/* 467 */     if (resource.isOpen()) {
/* 468 */       throw new BeanDefinitionStoreException("Passed-in Resource [" + resource + "] contains an open stream: cannot determine validation mode automatically. Either pass in a Resource that is able to create fresh streams, or explicitly specify the validationMode on your XmlBeanDefinitionReader instance.");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 477 */       inputStream = resource.getInputStream();
/*     */     }
/* 479 */     catch (IOException ex) {
/* 480 */       throw new BeanDefinitionStoreException("Unable to determine validation mode for [" + resource + "]: cannot open InputStream. Did you attempt to load directly from a SAX InputSource without specifying the validationMode on your XmlBeanDefinitionReader instance?", ex);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 487 */       return this.validationModeDetector.detectValidationMode(inputStream);
/*     */     }
/* 489 */     catch (IOException ex) {
/* 490 */       throw new BeanDefinitionStoreException("Unable to determine validation mode for [" + resource + "]: an error occurred whilst reading from the InputStream.", ex);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int registerBeanDefinitions(Document doc, Resource resource) throws BeanDefinitionStoreException {
/* 509 */     BeanDefinitionDocumentReader documentReader = createBeanDefinitionDocumentReader();
/* 510 */     int countBefore = getRegistry().getBeanDefinitionCount();
/* 511 */     documentReader.registerBeanDefinitions(doc, createReaderContext(resource));
/* 512 */     return getRegistry().getBeanDefinitionCount() - countBefore;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanDefinitionDocumentReader createBeanDefinitionDocumentReader() {
/* 522 */     return (BeanDefinitionDocumentReader)BeanUtils.instantiateClass(this.documentReaderClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XmlReaderContext createReaderContext(Resource resource) {
/* 529 */     return new XmlReaderContext(resource, this.problemReporter, this.eventListener, this.sourceExtractor, this, 
/* 530 */         getNamespaceHandlerResolver());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NamespaceHandlerResolver getNamespaceHandlerResolver() {
/* 538 */     if (this.namespaceHandlerResolver == null) {
/* 539 */       this.namespaceHandlerResolver = createDefaultNamespaceHandlerResolver();
/*     */     }
/* 541 */     return this.namespaceHandlerResolver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected NamespaceHandlerResolver createDefaultNamespaceHandlerResolver() {
/* 550 */     ClassLoader cl = (getResourceLoader() != null) ? getResourceLoader().getClassLoader() : getBeanClassLoader();
/* 551 */     return new DefaultNamespaceHandlerResolver(cl);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/xml/XmlBeanDefinitionReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */