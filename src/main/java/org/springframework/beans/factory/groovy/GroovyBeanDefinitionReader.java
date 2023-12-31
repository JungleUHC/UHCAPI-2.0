/*     */ package org.springframework.beans.factory.groovy;
/*     */ 
/*     */ import groovy.lang.Binding;
/*     */ import groovy.lang.Closure;
/*     */ import groovy.lang.GroovyObject;
/*     */ import groovy.lang.GroovyObjectSupport;
/*     */ import groovy.lang.GroovyShell;
/*     */ import groovy.lang.GroovySystem;
/*     */ import groovy.lang.MetaClass;
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.codehaus.groovy.runtime.DefaultGroovyMethods;
/*     */ import org.codehaus.groovy.runtime.InvokerHelper;
/*     */ import org.springframework.beans.MutablePropertyValues;
/*     */ import org.springframework.beans.factory.BeanDefinitionStoreException;
/*     */ import org.springframework.beans.factory.config.BeanDefinition;
/*     */ import org.springframework.beans.factory.config.RuntimeBeanReference;
/*     */ import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
/*     */ import org.springframework.beans.factory.parsing.Location;
/*     */ import org.springframework.beans.factory.parsing.Problem;
/*     */ import org.springframework.beans.factory.support.AbstractBeanDefinition;
/*     */ import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
/*     */ import org.springframework.beans.factory.support.BeanDefinitionRegistry;
/*     */ import org.springframework.beans.factory.support.GenericBeanDefinition;
/*     */ import org.springframework.beans.factory.support.ManagedList;
/*     */ import org.springframework.beans.factory.support.ManagedMap;
/*     */ import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
/*     */ import org.springframework.beans.factory.xml.NamespaceHandler;
/*     */ import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
/*     */ import org.springframework.beans.factory.xml.XmlReaderContext;
/*     */ import org.springframework.core.io.DescriptiveResource;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.core.io.support.EncodedResource;
/*     */ import org.springframework.util.ObjectUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GroovyBeanDefinitionReader
/*     */   extends AbstractBeanDefinitionReader
/*     */   implements GroovyObject
/*     */ {
/*     */   private final XmlBeanDefinitionReader standardXmlBeanDefinitionReader;
/*     */   private final XmlBeanDefinitionReader groovyDslXmlBeanDefinitionReader;
/* 146 */   private final Map<String, String> namespaces = new HashMap<>();
/*     */   
/* 148 */   private final Map<String, DeferredProperty> deferredProperties = new HashMap<>();
/*     */   
/* 150 */   private MetaClass metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(getClass());
/*     */ 
/*     */ 
/*     */   
/*     */   private Binding binding;
/*     */ 
/*     */ 
/*     */   
/*     */   private GroovyBeanDefinitionWrapper currentBeanDefinition;
/*     */ 
/*     */ 
/*     */   
/*     */   public GroovyBeanDefinitionReader(BeanDefinitionRegistry registry) {
/* 163 */     super(registry);
/* 164 */     this.standardXmlBeanDefinitionReader = new XmlBeanDefinitionReader(registry);
/* 165 */     this.groovyDslXmlBeanDefinitionReader = new XmlBeanDefinitionReader(registry);
/* 166 */     this.groovyDslXmlBeanDefinitionReader.setValidating(false);
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
/*     */   public GroovyBeanDefinitionReader(XmlBeanDefinitionReader xmlBeanDefinitionReader) {
/* 179 */     super(xmlBeanDefinitionReader.getRegistry());
/* 180 */     this.standardXmlBeanDefinitionReader = new XmlBeanDefinitionReader(xmlBeanDefinitionReader.getRegistry());
/* 181 */     this.groovyDslXmlBeanDefinitionReader = xmlBeanDefinitionReader;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMetaClass(MetaClass metaClass) {
/* 187 */     this.metaClass = metaClass;
/*     */   }
/*     */ 
/*     */   
/*     */   public MetaClass getMetaClass() {
/* 192 */     return this.metaClass;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBinding(Binding binding) {
/* 200 */     this.binding = binding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Binding getBinding() {
/* 207 */     return this.binding;
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
/*     */   public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
/* 223 */     return loadBeanDefinitions(new EncodedResource(resource));
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
/*     */   public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
/* 237 */     String filename = encodedResource.getResource().getFilename();
/* 238 */     if (StringUtils.endsWithIgnoreCase(filename, ".xml")) {
/* 239 */       return this.standardXmlBeanDefinitionReader.loadBeanDefinitions(encodedResource);
/*     */     }
/*     */     
/* 242 */     if (this.logger.isTraceEnabled()) {
/* 243 */       this.logger.trace("Loading Groovy bean definitions from " + encodedResource);
/*     */     }
/*     */ 
/*     */     
/* 247 */     Closure<Object> beans = new Closure<Object>(this)
/*     */       {
/*     */         public Object call(Object... args) {
/* 250 */           GroovyBeanDefinitionReader.this.invokeBeanDefiningClosure((Closure)args[0]);
/* 251 */           return null;
/*     */         }
/*     */       };
/* 254 */     Binding binding = new Binding()
/*     */       {
/*     */         public void setVariable(String name, Object value) {
/* 257 */           if (GroovyBeanDefinitionReader.this.currentBeanDefinition != null) {
/* 258 */             GroovyBeanDefinitionReader.this.applyPropertyToBeanDefinition(name, value);
/*     */           } else {
/*     */             
/* 261 */             super.setVariable(name, value);
/*     */           } 
/*     */         }
/*     */       };
/* 265 */     binding.setVariable("beans", beans);
/*     */     
/* 267 */     int countBefore = getRegistry().getBeanDefinitionCount();
/*     */     try {
/* 269 */       GroovyShell shell = new GroovyShell(getBeanClassLoader(), binding);
/* 270 */       shell.evaluate(encodedResource.getReader(), "beans");
/*     */     }
/* 272 */     catch (Throwable ex) {
/* 273 */       throw new BeanDefinitionParsingException(new Problem("Error evaluating Groovy script: " + ex.getMessage(), new Location(encodedResource
/* 274 */               .getResource()), null, ex));
/*     */     } 
/*     */     
/* 277 */     int count = getRegistry().getBeanDefinitionCount() - countBefore;
/* 278 */     if (this.logger.isDebugEnabled()) {
/* 279 */       this.logger.debug("Loaded " + count + " bean definitions from " + encodedResource);
/*     */     }
/* 281 */     return count;
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
/*     */   public GroovyBeanDefinitionReader beans(Closure<?> closure) {
/* 293 */     return invokeBeanDefiningClosure(closure);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GenericBeanDefinition bean(Class<?> type) {
/* 302 */     GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
/* 303 */     beanDefinition.setBeanClass(type);
/* 304 */     return beanDefinition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractBeanDefinition bean(Class<?> type, Object... args) {
/* 314 */     GroovyBeanDefinitionWrapper current = this.currentBeanDefinition;
/*     */     try {
/* 316 */       Closure<?> callable = null;
/* 317 */       Collection<Object> constructorArgs = null;
/* 318 */       if (!ObjectUtils.isEmpty(args)) {
/* 319 */         int index = args.length;
/* 320 */         Object lastArg = args[index - 1];
/* 321 */         if (lastArg instanceof Closure) {
/* 322 */           callable = (Closure)lastArg;
/* 323 */           index--;
/*     */         } 
/* 325 */         constructorArgs = resolveConstructorArguments(args, 0, index);
/*     */       } 
/* 327 */       this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(null, type, constructorArgs);
/* 328 */       if (callable != null) {
/* 329 */         callable.call(this.currentBeanDefinition);
/*     */       }
/* 331 */       return this.currentBeanDefinition.getBeanDefinition();
/*     */     } finally {
/*     */       
/* 334 */       this.currentBeanDefinition = current;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void xmlns(Map<String, String> definition) {
/* 343 */     if (!definition.isEmpty()) {
/* 344 */       for (Map.Entry<String, String> entry : definition.entrySet()) {
/* 345 */         String namespace = entry.getKey();
/* 346 */         String uri = entry.getValue();
/* 347 */         if (uri == null) {
/* 348 */           throw new IllegalArgumentException("Namespace definition must supply a non-null URI");
/*     */         }
/*     */         
/* 351 */         NamespaceHandler namespaceHandler = this.groovyDslXmlBeanDefinitionReader.getNamespaceHandlerResolver().resolve(uri);
/* 352 */         if (namespaceHandler == null) {
/* 353 */           throw new BeanDefinitionParsingException(new Problem("No namespace handler found for URI: " + uri, new Location(new DescriptiveResource("Groovy"))));
/*     */         }
/*     */         
/* 356 */         this.namespaces.put(namespace, uri);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void importBeans(String resourcePattern) throws IOException {
/* 367 */     loadBeanDefinitions(resourcePattern);
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
/*     */   public Object invokeMethod(String name, Object arg) {
/* 379 */     Object[] args = (Object[])arg;
/* 380 */     if ("beans".equals(name) && args.length == 1 && args[0] instanceof Closure) {
/* 381 */       return beans((Closure)args[0]);
/*     */     }
/* 383 */     if ("ref".equals(name)) {
/*     */       String refName;
/* 385 */       if (args[0] == null) {
/* 386 */         throw new IllegalArgumentException("Argument to ref() is not a valid bean or was not found");
/*     */       }
/* 388 */       if (args[0] instanceof RuntimeBeanReference) {
/* 389 */         refName = ((RuntimeBeanReference)args[0]).getBeanName();
/*     */       } else {
/*     */         
/* 392 */         refName = args[0].toString();
/*     */       } 
/* 394 */       boolean parentRef = false;
/* 395 */       if (args.length > 1 && args[1] instanceof Boolean) {
/* 396 */         parentRef = ((Boolean)args[1]).booleanValue();
/*     */       }
/* 398 */       return new RuntimeBeanReference(refName, parentRef);
/*     */     } 
/* 400 */     if (this.namespaces.containsKey(name) && args.length > 0 && args[0] instanceof Closure) {
/* 401 */       GroovyDynamicElementReader reader = createDynamicElementReader(name);
/* 402 */       reader.invokeMethod("doCall", args);
/*     */     } else {
/* 404 */       if (args.length > 0 && args[0] instanceof Closure)
/*     */       {
/* 406 */         return invokeBeanDefiningMethod(name, args);
/*     */       }
/* 408 */       if (args.length > 0 && (args[0] instanceof Class || args[0] instanceof RuntimeBeanReference || args[0] instanceof Map))
/*     */       {
/* 410 */         return invokeBeanDefiningMethod(name, args);
/*     */       }
/* 412 */       if (args.length > 1 && args[args.length - 1] instanceof Closure)
/* 413 */         return invokeBeanDefiningMethod(name, args); 
/*     */     } 
/* 415 */     MetaClass mc = DefaultGroovyMethods.getMetaClass(getRegistry());
/* 416 */     if (!mc.respondsTo(getRegistry(), name, args).isEmpty()) {
/* 417 */       return mc.invokeMethod(getRegistry(), name, args);
/*     */     }
/* 419 */     return this;
/*     */   }
/*     */   
/*     */   private boolean addDeferredProperty(String property, Object newValue) {
/* 423 */     if (newValue instanceof List || newValue instanceof Map) {
/* 424 */       this.deferredProperties.put(this.currentBeanDefinition.getBeanName() + '.' + property, new DeferredProperty(this.currentBeanDefinition, property, newValue));
/*     */       
/* 426 */       return true;
/*     */     } 
/* 428 */     return false;
/*     */   }
/*     */   
/*     */   private void finalizeDeferredProperties() {
/* 432 */     for (DeferredProperty dp : this.deferredProperties.values()) {
/* 433 */       if (dp.value instanceof List) {
/* 434 */         dp.value = manageListIfNecessary((List)dp.value);
/*     */       }
/* 436 */       else if (dp.value instanceof Map) {
/* 437 */         dp.value = manageMapIfNecessary((Map<?, ?>)dp.value);
/*     */       } 
/* 439 */       dp.apply();
/*     */     } 
/* 441 */     this.deferredProperties.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected GroovyBeanDefinitionReader invokeBeanDefiningClosure(Closure<?> callable) {
/* 450 */     callable.setDelegate(this);
/* 451 */     callable.call();
/* 452 */     finalizeDeferredProperties();
/* 453 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private GroovyBeanDefinitionWrapper invokeBeanDefiningMethod(String beanName, Object[] args) {
/* 464 */     boolean hasClosureArgument = args[args.length - 1] instanceof Closure;
/* 465 */     if (args[0] instanceof Class) {
/* 466 */       Class<?> beanClass = (Class)args[0];
/* 467 */       if (hasClosureArgument) {
/* 468 */         if (args.length - 1 != 1) {
/* 469 */           this
/* 470 */             .currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, beanClass, resolveConstructorArguments(args, 1, args.length - 1));
/*     */         } else {
/*     */           
/* 473 */           this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, beanClass);
/*     */         } 
/*     */       } else {
/*     */         
/* 477 */         this
/* 478 */           .currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, beanClass, resolveConstructorArguments(args, 1, args.length));
/*     */       }
/*     */     
/* 481 */     } else if (args[0] instanceof RuntimeBeanReference) {
/* 482 */       this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
/* 483 */       this.currentBeanDefinition.getBeanDefinition().setFactoryBeanName(((RuntimeBeanReference)args[0]).getBeanName());
/*     */     }
/* 485 */     else if (args[0] instanceof Map) {
/*     */       
/* 487 */       if (args.length > 1 && args[1] instanceof Class) {
/*     */         
/* 489 */         List<Object> constructorArgs = resolveConstructorArguments(args, 2, hasClosureArgument ? (args.length - 1) : args.length);
/* 490 */         this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, (Class)args[1], constructorArgs);
/* 491 */         Map<?, ?> namedArgs = (Map<?, ?>)args[0];
/* 492 */         for (Map.Entry<?, ?> entity : namedArgs.entrySet()) {
/* 493 */           String propName = (String)entity.getKey();
/* 494 */           setProperty(propName, entity.getValue());
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 499 */         this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
/*     */         
/* 501 */         Map.Entry<?, ?> factoryBeanEntry = ((Map<?, ?>)args[0]).entrySet().iterator().next();
/*     */ 
/*     */         
/* 504 */         int constructorArgsTest = hasClosureArgument ? 2 : 1;
/*     */         
/* 506 */         if (args.length > constructorArgsTest) {
/*     */           
/* 508 */           int endOfConstructArgs = hasClosureArgument ? (args.length - 1) : args.length;
/* 509 */           this
/* 510 */             .currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, null, resolveConstructorArguments(args, 1, endOfConstructArgs));
/*     */         } else {
/*     */           
/* 513 */           this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
/*     */         } 
/* 515 */         this.currentBeanDefinition.getBeanDefinition().setFactoryBeanName(factoryBeanEntry.getKey().toString());
/* 516 */         this.currentBeanDefinition.getBeanDefinition().setFactoryMethodName(factoryBeanEntry.getValue().toString());
/*     */       }
/*     */     
/*     */     }
/* 520 */     else if (args[0] instanceof Closure) {
/* 521 */       this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
/* 522 */       this.currentBeanDefinition.getBeanDefinition().setAbstract(true);
/*     */     }
/*     */     else {
/*     */       
/* 526 */       List<Object> constructorArgs = resolveConstructorArguments(args, 0, hasClosureArgument ? (args.length - 1) : args.length);
/* 527 */       this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, null, constructorArgs);
/*     */     } 
/*     */     
/* 530 */     if (hasClosureArgument) {
/* 531 */       Closure<?> callable = (Closure)args[args.length - 1];
/* 532 */       callable.setDelegate(this);
/* 533 */       callable.setResolveStrategy(1);
/* 534 */       callable.call(this.currentBeanDefinition);
/*     */     } 
/*     */     
/* 537 */     GroovyBeanDefinitionWrapper beanDefinition = this.currentBeanDefinition;
/* 538 */     this.currentBeanDefinition = null;
/* 539 */     beanDefinition.getBeanDefinition().setAttribute(GroovyBeanDefinitionWrapper.class.getName(), beanDefinition);
/* 540 */     getRegistry().registerBeanDefinition(beanName, (BeanDefinition)beanDefinition.getBeanDefinition());
/* 541 */     return beanDefinition;
/*     */   }
/*     */   
/*     */   protected List<Object> resolveConstructorArguments(Object[] args, int start, int end) {
/* 545 */     Object[] constructorArgs = Arrays.copyOfRange(args, start, end);
/* 546 */     for (int i = 0; i < constructorArgs.length; i++) {
/* 547 */       if (constructorArgs[i] instanceof groovy.lang.GString) {
/* 548 */         constructorArgs[i] = constructorArgs[i].toString();
/*     */       }
/* 550 */       else if (constructorArgs[i] instanceof List) {
/* 551 */         constructorArgs[i] = manageListIfNecessary((List)constructorArgs[i]);
/*     */       }
/* 553 */       else if (constructorArgs[i] instanceof Map) {
/* 554 */         constructorArgs[i] = manageMapIfNecessary((Map<?, ?>)constructorArgs[i]);
/*     */       } 
/*     */     } 
/* 557 */     return Arrays.asList(constructorArgs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object manageMapIfNecessary(Map<?, ?> map) {
/* 567 */     boolean containsRuntimeRefs = false;
/* 568 */     for (Object element : map.values()) {
/* 569 */       if (element instanceof RuntimeBeanReference) {
/* 570 */         containsRuntimeRefs = true;
/*     */         break;
/*     */       } 
/*     */     } 
/* 574 */     if (containsRuntimeRefs) {
/* 575 */       ManagedMap<?, ?> managedMap = new ManagedMap();
/* 576 */       managedMap.putAll(map);
/* 577 */       return managedMap;
/*     */     } 
/* 579 */     return map;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object manageListIfNecessary(List<?> list) {
/* 589 */     boolean containsRuntimeRefs = false;
/* 590 */     for (Object element : list) {
/* 591 */       if (element instanceof RuntimeBeanReference) {
/* 592 */         containsRuntimeRefs = true;
/*     */         break;
/*     */       } 
/*     */     } 
/* 596 */     if (containsRuntimeRefs) {
/* 597 */       ManagedList<?> managedList = new ManagedList();
/* 598 */       managedList.addAll(list);
/* 599 */       return managedList;
/*     */     } 
/* 601 */     return list;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProperty(String name, Object value) {
/* 610 */     if (this.currentBeanDefinition != null) {
/* 611 */       applyPropertyToBeanDefinition(name, value);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void applyPropertyToBeanDefinition(String name, Object value) {
/* 616 */     if (value instanceof groovy.lang.GString) {
/* 617 */       value = value.toString();
/*     */     }
/* 619 */     if (addDeferredProperty(name, value)) {
/*     */       return;
/*     */     }
/* 622 */     if (value instanceof Closure) {
/* 623 */       GroovyBeanDefinitionWrapper current = this.currentBeanDefinition;
/*     */       try {
/* 625 */         Closure<?> callable = (Closure)value;
/* 626 */         Class<?> parameterType = callable.getParameterTypes()[0];
/* 627 */         if (Object.class == parameterType) {
/* 628 */           this.currentBeanDefinition = new GroovyBeanDefinitionWrapper("");
/* 629 */           callable.call(this.currentBeanDefinition);
/*     */         } else {
/*     */           
/* 632 */           this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(null, parameterType);
/* 633 */           callable.call(null);
/*     */         } 
/*     */         
/* 636 */         value = this.currentBeanDefinition.getBeanDefinition();
/*     */       } finally {
/*     */         
/* 639 */         this.currentBeanDefinition = current;
/*     */       } 
/*     */     } 
/* 642 */     this.currentBeanDefinition.addProperty(name, value);
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
/*     */   public Object getProperty(String name) {
/* 657 */     Binding binding = getBinding();
/* 658 */     if (binding != null && binding.hasVariable(name)) {
/* 659 */       return binding.getVariable(name);
/*     */     }
/*     */     
/* 662 */     if (this.namespaces.containsKey(name)) {
/* 663 */       return createDynamicElementReader(name);
/*     */     }
/* 665 */     if (getRegistry().containsBeanDefinition(name)) {
/*     */       
/* 667 */       GroovyBeanDefinitionWrapper beanDefinition = (GroovyBeanDefinitionWrapper)getRegistry().getBeanDefinition(name).getAttribute(GroovyBeanDefinitionWrapper.class.getName());
/* 668 */       if (beanDefinition != null) {
/* 669 */         return new GroovyRuntimeBeanReference(name, beanDefinition, false);
/*     */       }
/*     */       
/* 672 */       return new RuntimeBeanReference(name, false);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 677 */     if (this.currentBeanDefinition != null) {
/* 678 */       MutablePropertyValues pvs = this.currentBeanDefinition.getBeanDefinition().getPropertyValues();
/* 679 */       if (pvs.contains(name)) {
/* 680 */         return pvs.get(name);
/*     */       }
/*     */       
/* 683 */       DeferredProperty dp = this.deferredProperties.get(this.currentBeanDefinition.getBeanName() + name);
/* 684 */       if (dp != null) {
/* 685 */         return dp.value;
/*     */       }
/*     */       
/* 688 */       return getMetaClass().getProperty(this, name);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 693 */     return getMetaClass().getProperty(this, name);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private GroovyDynamicElementReader createDynamicElementReader(String namespace) {
/* 699 */     XmlReaderContext readerContext = this.groovyDslXmlBeanDefinitionReader.createReaderContext((Resource)new DescriptiveResource("Groovy"));
/*     */     
/* 701 */     BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(readerContext);
/* 702 */     boolean decorating = (this.currentBeanDefinition != null);
/* 703 */     if (!decorating) {
/* 704 */       this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(namespace);
/*     */     }
/* 706 */     return new GroovyDynamicElementReader(namespace, this.namespaces, delegate, this.currentBeanDefinition, decorating)
/*     */       {
/*     */         protected void afterInvocation() {
/* 709 */           if (!this.decorating) {
/* 710 */             GroovyBeanDefinitionReader.this.currentBeanDefinition = null;
/*     */           }
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class DeferredProperty
/*     */   {
/*     */     private final GroovyBeanDefinitionWrapper beanDefinition;
/*     */ 
/*     */     
/*     */     private final String name;
/*     */ 
/*     */     
/*     */     public Object value;
/*     */ 
/*     */ 
/*     */     
/*     */     public DeferredProperty(GroovyBeanDefinitionWrapper beanDefinition, String name, Object value) {
/* 732 */       this.beanDefinition = beanDefinition;
/* 733 */       this.name = name;
/* 734 */       this.value = value;
/*     */     }
/*     */     
/*     */     public void apply() {
/* 738 */       this.beanDefinition.addProperty(this.name, this.value);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private class GroovyRuntimeBeanReference
/*     */     extends RuntimeBeanReference
/*     */     implements GroovyObject
/*     */   {
/*     */     private final GroovyBeanDefinitionWrapper beanDefinition;
/*     */     
/*     */     private MetaClass metaClass;
/*     */ 
/*     */     
/*     */     public GroovyRuntimeBeanReference(String beanName, GroovyBeanDefinitionWrapper beanDefinition, boolean toParent) {
/* 753 */       super(beanName, toParent);
/* 754 */       this.beanDefinition = beanDefinition;
/* 755 */       this.metaClass = InvokerHelper.getMetaClass(this);
/*     */     }
/*     */ 
/*     */     
/*     */     public MetaClass getMetaClass() {
/* 760 */       return this.metaClass;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getProperty(String property) {
/* 765 */       if (property.equals("beanName")) {
/* 766 */         return getBeanName();
/*     */       }
/* 768 */       if (property.equals("source")) {
/* 769 */         return getSource();
/*     */       }
/* 771 */       if (this.beanDefinition != null) {
/* 772 */         return new GroovyPropertyValue(property, this.beanDefinition
/* 773 */             .getBeanDefinition().getPropertyValues().get(property));
/*     */       }
/*     */       
/* 776 */       return this.metaClass.getProperty(this, property);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Object invokeMethod(String name, Object args) {
/* 782 */       return this.metaClass.invokeMethod(this, name, args);
/*     */     }
/*     */ 
/*     */     
/*     */     public void setMetaClass(MetaClass metaClass) {
/* 787 */       this.metaClass = metaClass;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setProperty(String property, Object newValue) {
/* 792 */       if (!GroovyBeanDefinitionReader.this.addDeferredProperty(property, newValue)) {
/* 793 */         this.beanDefinition.getBeanDefinition().getPropertyValues().add(property, newValue);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private class GroovyPropertyValue
/*     */       extends GroovyObjectSupport
/*     */     {
/*     */       private final String propertyName;
/*     */ 
/*     */       
/*     */       private final Object propertyValue;
/*     */ 
/*     */       
/*     */       public GroovyPropertyValue(String propertyName, Object propertyValue) {
/* 809 */         this.propertyName = propertyName;
/* 810 */         this.propertyValue = propertyValue;
/*     */       }
/*     */ 
/*     */       
/*     */       public void leftShift(Object value) {
/* 815 */         InvokerHelper.invokeMethod(this.propertyValue, "leftShift", value);
/* 816 */         updateDeferredProperties(value);
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean add(Object value) {
/* 821 */         boolean retVal = ((Boolean)InvokerHelper.invokeMethod(this.propertyValue, "add", value)).booleanValue();
/* 822 */         updateDeferredProperties(value);
/* 823 */         return retVal;
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean addAll(Collection<?> values) {
/* 828 */         boolean retVal = ((Boolean)InvokerHelper.invokeMethod(this.propertyValue, "addAll", values)).booleanValue();
/* 829 */         for (Object value : values) {
/* 830 */           updateDeferredProperties(value);
/*     */         }
/* 832 */         return retVal;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object invokeMethod(String name, Object args) {
/* 837 */         return InvokerHelper.invokeMethod(this.propertyValue, name, args);
/*     */       }
/*     */ 
/*     */       
/*     */       public Object getProperty(String name) {
/* 842 */         return InvokerHelper.getProperty(this.propertyValue, name);
/*     */       }
/*     */ 
/*     */       
/*     */       public void setProperty(String name, Object value) {
/* 847 */         InvokerHelper.setProperty(this.propertyValue, name, value);
/*     */       }
/*     */       
/*     */       private void updateDeferredProperties(Object value) {
/* 851 */         if (value instanceof RuntimeBeanReference)
/* 852 */           GroovyBeanDefinitionReader.this.deferredProperties.put(GroovyBeanDefinitionReader.GroovyRuntimeBeanReference.this.beanDefinition.getBeanName(), new GroovyBeanDefinitionReader.DeferredProperty(GroovyBeanDefinitionReader.GroovyRuntimeBeanReference.this
/* 853 */                 .beanDefinition, this.propertyName, this.propertyValue)); 
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/groovy/GroovyBeanDefinitionReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */