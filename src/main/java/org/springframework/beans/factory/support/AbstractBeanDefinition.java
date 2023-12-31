/*      */ package org.springframework.beans.factory.support;
/*      */ 
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.util.Arrays;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.function.Supplier;
/*      */ import org.springframework.beans.BeanMetadataAttributeAccessor;
/*      */ import org.springframework.beans.MutablePropertyValues;
/*      */ import org.springframework.beans.PropertyValues;
/*      */ import org.springframework.beans.factory.config.BeanDefinition;
/*      */ import org.springframework.beans.factory.config.ConstructorArgumentValues;
/*      */ import org.springframework.core.AttributeAccessor;
/*      */ import org.springframework.core.ResolvableType;
/*      */ import org.springframework.core.io.DescriptiveResource;
/*      */ import org.springframework.core.io.Resource;
/*      */ import org.springframework.lang.Nullable;
/*      */ import org.springframework.util.Assert;
/*      */ import org.springframework.util.ClassUtils;
/*      */ import org.springframework.util.ObjectUtils;
/*      */ import org.springframework.util.StringUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class AbstractBeanDefinition
/*      */   extends BeanMetadataAttributeAccessor
/*      */   implements BeanDefinition, Cloneable
/*      */ {
/*      */   public static final String SCOPE_DEFAULT = "";
/*      */   public static final int AUTOWIRE_NO = 0;
/*      */   public static final int AUTOWIRE_BY_NAME = 1;
/*      */   public static final int AUTOWIRE_BY_TYPE = 2;
/*      */   public static final int AUTOWIRE_CONSTRUCTOR = 3;
/*      */   @Deprecated
/*      */   public static final int AUTOWIRE_AUTODETECT = 4;
/*      */   public static final int DEPENDENCY_CHECK_NONE = 0;
/*      */   public static final int DEPENDENCY_CHECK_OBJECTS = 1;
/*      */   public static final int DEPENDENCY_CHECK_SIMPLE = 2;
/*      */   public static final int DEPENDENCY_CHECK_ALL = 3;
/*      */   public static final String INFER_METHOD = "(inferred)";
/*      */   @Nullable
/*      */   private volatile Object beanClass;
/*      */   @Nullable
/*  144 */   private String scope = "";
/*      */ 
/*      */   
/*      */   private boolean abstractFlag = false;
/*      */   
/*      */   @Nullable
/*      */   private Boolean lazyInit;
/*      */   
/*  152 */   private int autowireMode = 0;
/*      */   
/*  154 */   private int dependencyCheck = 0;
/*      */   
/*      */   @Nullable
/*      */   private String[] dependsOn;
/*      */   
/*      */   private boolean autowireCandidate = true;
/*      */   
/*      */   private boolean primary = false;
/*      */   
/*  163 */   private final Map<String, AutowireCandidateQualifier> qualifiers = new LinkedHashMap<>();
/*      */   
/*      */   @Nullable
/*      */   private Supplier<?> instanceSupplier;
/*      */   
/*      */   private boolean nonPublicAccessAllowed = true;
/*      */   
/*      */   private boolean lenientConstructorResolution = true;
/*      */   
/*      */   @Nullable
/*      */   private String factoryBeanName;
/*      */   
/*      */   @Nullable
/*      */   private String factoryMethodName;
/*      */   
/*      */   @Nullable
/*      */   private ConstructorArgumentValues constructorArgumentValues;
/*      */   
/*      */   @Nullable
/*      */   private MutablePropertyValues propertyValues;
/*      */   
/*  184 */   private MethodOverrides methodOverrides = new MethodOverrides();
/*      */   
/*      */   @Nullable
/*      */   private String initMethodName;
/*      */   
/*      */   @Nullable
/*      */   private String destroyMethodName;
/*      */   
/*      */   private boolean enforceInitMethod = true;
/*      */   
/*      */   private boolean enforceDestroyMethod = true;
/*      */   
/*      */   private boolean synthetic = false;
/*      */   
/*  198 */   private int role = 0;
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private String description;
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private Resource resource;
/*      */ 
/*      */ 
/*      */   
/*      */   protected AbstractBeanDefinition() {
/*  211 */     this((ConstructorArgumentValues)null, (MutablePropertyValues)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected AbstractBeanDefinition(@Nullable ConstructorArgumentValues cargs, @Nullable MutablePropertyValues pvs) {
/*  219 */     this.constructorArgumentValues = cargs;
/*  220 */     this.propertyValues = pvs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected AbstractBeanDefinition(BeanDefinition original) {
/*  229 */     setParentName(original.getParentName());
/*  230 */     setBeanClassName(original.getBeanClassName());
/*  231 */     setScope(original.getScope());
/*  232 */     setAbstract(original.isAbstract());
/*  233 */     setFactoryBeanName(original.getFactoryBeanName());
/*  234 */     setFactoryMethodName(original.getFactoryMethodName());
/*  235 */     setRole(original.getRole());
/*  236 */     setSource(original.getSource());
/*  237 */     copyAttributesFrom((AttributeAccessor)original);
/*      */     
/*  239 */     if (original instanceof AbstractBeanDefinition) {
/*  240 */       AbstractBeanDefinition originalAbd = (AbstractBeanDefinition)original;
/*  241 */       if (originalAbd.hasBeanClass()) {
/*  242 */         setBeanClass(originalAbd.getBeanClass());
/*      */       }
/*  244 */       if (originalAbd.hasConstructorArgumentValues()) {
/*  245 */         setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
/*      */       }
/*  247 */       if (originalAbd.hasPropertyValues()) {
/*  248 */         setPropertyValues(new MutablePropertyValues((PropertyValues)original.getPropertyValues()));
/*      */       }
/*  250 */       if (originalAbd.hasMethodOverrides()) {
/*  251 */         setMethodOverrides(new MethodOverrides(originalAbd.getMethodOverrides()));
/*      */       }
/*  253 */       Boolean lazyInit = originalAbd.getLazyInit();
/*  254 */       if (lazyInit != null) {
/*  255 */         setLazyInit(lazyInit.booleanValue());
/*      */       }
/*  257 */       setAutowireMode(originalAbd.getAutowireMode());
/*  258 */       setDependencyCheck(originalAbd.getDependencyCheck());
/*  259 */       setDependsOn(originalAbd.getDependsOn());
/*  260 */       setAutowireCandidate(originalAbd.isAutowireCandidate());
/*  261 */       setPrimary(originalAbd.isPrimary());
/*  262 */       copyQualifiersFrom(originalAbd);
/*  263 */       setInstanceSupplier(originalAbd.getInstanceSupplier());
/*  264 */       setNonPublicAccessAllowed(originalAbd.isNonPublicAccessAllowed());
/*  265 */       setLenientConstructorResolution(originalAbd.isLenientConstructorResolution());
/*  266 */       setInitMethodName(originalAbd.getInitMethodName());
/*  267 */       setEnforceInitMethod(originalAbd.isEnforceInitMethod());
/*  268 */       setDestroyMethodName(originalAbd.getDestroyMethodName());
/*  269 */       setEnforceDestroyMethod(originalAbd.isEnforceDestroyMethod());
/*  270 */       setSynthetic(originalAbd.isSynthetic());
/*  271 */       setResource(originalAbd.getResource());
/*      */     } else {
/*      */       
/*  274 */       setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
/*  275 */       setPropertyValues(new MutablePropertyValues((PropertyValues)original.getPropertyValues()));
/*  276 */       setLazyInit(original.isLazyInit());
/*  277 */       setResourceDescription(original.getResourceDescription());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void overrideFrom(BeanDefinition other) {
/*  299 */     if (StringUtils.hasLength(other.getBeanClassName())) {
/*  300 */       setBeanClassName(other.getBeanClassName());
/*      */     }
/*  302 */     if (StringUtils.hasLength(other.getScope())) {
/*  303 */       setScope(other.getScope());
/*      */     }
/*  305 */     setAbstract(other.isAbstract());
/*  306 */     if (StringUtils.hasLength(other.getFactoryBeanName())) {
/*  307 */       setFactoryBeanName(other.getFactoryBeanName());
/*      */     }
/*  309 */     if (StringUtils.hasLength(other.getFactoryMethodName())) {
/*  310 */       setFactoryMethodName(other.getFactoryMethodName());
/*      */     }
/*  312 */     setRole(other.getRole());
/*  313 */     setSource(other.getSource());
/*  314 */     copyAttributesFrom((AttributeAccessor)other);
/*      */     
/*  316 */     if (other instanceof AbstractBeanDefinition) {
/*  317 */       AbstractBeanDefinition otherAbd = (AbstractBeanDefinition)other;
/*  318 */       if (otherAbd.hasBeanClass()) {
/*  319 */         setBeanClass(otherAbd.getBeanClass());
/*      */       }
/*  321 */       if (otherAbd.hasConstructorArgumentValues()) {
/*  322 */         getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
/*      */       }
/*  324 */       if (otherAbd.hasPropertyValues()) {
/*  325 */         getPropertyValues().addPropertyValues((PropertyValues)other.getPropertyValues());
/*      */       }
/*  327 */       if (otherAbd.hasMethodOverrides()) {
/*  328 */         getMethodOverrides().addOverrides(otherAbd.getMethodOverrides());
/*      */       }
/*  330 */       Boolean lazyInit = otherAbd.getLazyInit();
/*  331 */       if (lazyInit != null) {
/*  332 */         setLazyInit(lazyInit.booleanValue());
/*      */       }
/*  334 */       setAutowireMode(otherAbd.getAutowireMode());
/*  335 */       setDependencyCheck(otherAbd.getDependencyCheck());
/*  336 */       setDependsOn(otherAbd.getDependsOn());
/*  337 */       setAutowireCandidate(otherAbd.isAutowireCandidate());
/*  338 */       setPrimary(otherAbd.isPrimary());
/*  339 */       copyQualifiersFrom(otherAbd);
/*  340 */       setInstanceSupplier(otherAbd.getInstanceSupplier());
/*  341 */       setNonPublicAccessAllowed(otherAbd.isNonPublicAccessAllowed());
/*  342 */       setLenientConstructorResolution(otherAbd.isLenientConstructorResolution());
/*  343 */       if (otherAbd.getInitMethodName() != null) {
/*  344 */         setInitMethodName(otherAbd.getInitMethodName());
/*  345 */         setEnforceInitMethod(otherAbd.isEnforceInitMethod());
/*      */       } 
/*  347 */       if (otherAbd.getDestroyMethodName() != null) {
/*  348 */         setDestroyMethodName(otherAbd.getDestroyMethodName());
/*  349 */         setEnforceDestroyMethod(otherAbd.isEnforceDestroyMethod());
/*      */       } 
/*  351 */       setSynthetic(otherAbd.isSynthetic());
/*  352 */       setResource(otherAbd.getResource());
/*      */     } else {
/*      */       
/*  355 */       getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
/*  356 */       getPropertyValues().addPropertyValues((PropertyValues)other.getPropertyValues());
/*  357 */       setLazyInit(other.isLazyInit());
/*  358 */       setResourceDescription(other.getResourceDescription());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void applyDefaults(BeanDefinitionDefaults defaults) {
/*  368 */     Boolean lazyInit = defaults.getLazyInit();
/*  369 */     if (lazyInit != null) {
/*  370 */       setLazyInit(lazyInit.booleanValue());
/*      */     }
/*  372 */     setAutowireMode(defaults.getAutowireMode());
/*  373 */     setDependencyCheck(defaults.getDependencyCheck());
/*  374 */     setInitMethodName(defaults.getInitMethodName());
/*  375 */     setEnforceInitMethod(false);
/*  376 */     setDestroyMethodName(defaults.getDestroyMethodName());
/*  377 */     setEnforceDestroyMethod(false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBeanClassName(@Nullable String beanClassName) {
/*  386 */     this.beanClass = beanClassName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getBeanClassName() {
/*  395 */     Object beanClassObject = this.beanClass;
/*  396 */     if (beanClassObject instanceof Class) {
/*  397 */       return ((Class)beanClassObject).getName();
/*      */     }
/*      */     
/*  400 */     return (String)beanClassObject;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBeanClass(@Nullable Class<?> beanClass) {
/*  409 */     this.beanClass = beanClass;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Class<?> getBeanClass() throws IllegalStateException {
/*  434 */     Object beanClassObject = this.beanClass;
/*  435 */     if (beanClassObject == null) {
/*  436 */       throw new IllegalStateException("No bean class specified on bean definition");
/*      */     }
/*  438 */     if (!(beanClassObject instanceof Class)) {
/*  439 */       throw new IllegalStateException("Bean class name [" + beanClassObject + "] has not been resolved into an actual Class");
/*      */     }
/*      */     
/*  442 */     return (Class)beanClassObject;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasBeanClass() {
/*  452 */     return this.beanClass instanceof Class;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public Class<?> resolveBeanClass(@Nullable ClassLoader classLoader) throws ClassNotFoundException {
/*  465 */     String className = getBeanClassName();
/*  466 */     if (className == null) {
/*  467 */       return null;
/*      */     }
/*  469 */     Class<?> resolvedClass = ClassUtils.forName(className, classLoader);
/*  470 */     this.beanClass = resolvedClass;
/*  471 */     return resolvedClass;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResolvableType getResolvableType() {
/*  481 */     return hasBeanClass() ? ResolvableType.forClass(getBeanClass()) : ResolvableType.NONE;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setScope(@Nullable String scope) {
/*  496 */     this.scope = scope;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getScope() {
/*  505 */     return this.scope;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSingleton() {
/*  515 */     return ("singleton".equals(this.scope) || "".equals(this.scope));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isPrototype() {
/*  525 */     return "prototype".equals(this.scope);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAbstract(boolean abstractFlag) {
/*  535 */     this.abstractFlag = abstractFlag;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAbstract() {
/*  544 */     return this.abstractFlag;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLazyInit(boolean lazyInit) {
/*  554 */     this.lazyInit = Boolean.valueOf(lazyInit);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isLazyInit() {
/*  564 */     return (this.lazyInit != null && this.lazyInit.booleanValue());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public Boolean getLazyInit() {
/*  575 */     return this.lazyInit;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutowireMode(int autowireMode) {
/*  592 */     this.autowireMode = autowireMode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getAutowireMode() {
/*  599 */     return this.autowireMode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getResolvedAutowireMode() {
/*  610 */     if (this.autowireMode == 4) {
/*      */ 
/*      */ 
/*      */       
/*  614 */       Constructor[] arrayOfConstructor = (Constructor[])getBeanClass().getConstructors();
/*  615 */       for (Constructor<?> constructor : arrayOfConstructor) {
/*  616 */         if (constructor.getParameterCount() == 0) {
/*  617 */           return 2;
/*      */         }
/*      */       } 
/*  620 */       return 3;
/*      */     } 
/*      */     
/*  623 */     return this.autowireMode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDependencyCheck(int dependencyCheck) {
/*  637 */     this.dependencyCheck = dependencyCheck;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDependencyCheck() {
/*  644 */     return this.dependencyCheck;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDependsOn(@Nullable String... dependsOn) {
/*  656 */     this.dependsOn = dependsOn;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String[] getDependsOn() {
/*  665 */     return this.dependsOn;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutowireCandidate(boolean autowireCandidate) {
/*  679 */     this.autowireCandidate = autowireCandidate;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAutowireCandidate() {
/*  687 */     return this.autowireCandidate;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPrimary(boolean primary) {
/*  697 */     this.primary = primary;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isPrimary() {
/*  705 */     return this.primary;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addQualifier(AutowireCandidateQualifier qualifier) {
/*  714 */     this.qualifiers.put(qualifier.getTypeName(), qualifier);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasQualifier(String typeName) {
/*  721 */     return this.qualifiers.containsKey(typeName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public AutowireCandidateQualifier getQualifier(String typeName) {
/*  729 */     return this.qualifiers.get(typeName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Set<AutowireCandidateQualifier> getQualifiers() {
/*  737 */     return new LinkedHashSet<>(this.qualifiers.values());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void copyQualifiersFrom(AbstractBeanDefinition source) {
/*  745 */     Assert.notNull(source, "Source must not be null");
/*  746 */     this.qualifiers.putAll(source.qualifiers);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setInstanceSupplier(@Nullable Supplier<?> instanceSupplier) {
/*  760 */     this.instanceSupplier = instanceSupplier;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public Supplier<?> getInstanceSupplier() {
/*  769 */     return this.instanceSupplier;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNonPublicAccessAllowed(boolean nonPublicAccessAllowed) {
/*  784 */     this.nonPublicAccessAllowed = nonPublicAccessAllowed;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isNonPublicAccessAllowed() {
/*  791 */     return this.nonPublicAccessAllowed;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLenientConstructorResolution(boolean lenientConstructorResolution) {
/*  801 */     this.lenientConstructorResolution = lenientConstructorResolution;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isLenientConstructorResolution() {
/*  808 */     return this.lenientConstructorResolution;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setFactoryBeanName(@Nullable String factoryBeanName) {
/*  818 */     this.factoryBeanName = factoryBeanName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getFactoryBeanName() {
/*  827 */     return this.factoryBeanName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setFactoryMethodName(@Nullable String factoryMethodName) {
/*  840 */     this.factoryMethodName = factoryMethodName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getFactoryMethodName() {
/*  849 */     return this.factoryMethodName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
/*  856 */     this.constructorArgumentValues = constructorArgumentValues;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ConstructorArgumentValues getConstructorArgumentValues() {
/*  864 */     if (this.constructorArgumentValues == null) {
/*  865 */       this.constructorArgumentValues = new ConstructorArgumentValues();
/*      */     }
/*  867 */     return this.constructorArgumentValues;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasConstructorArgumentValues() {
/*  875 */     return (this.constructorArgumentValues != null && !this.constructorArgumentValues.isEmpty());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPropertyValues(MutablePropertyValues propertyValues) {
/*  882 */     this.propertyValues = propertyValues;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MutablePropertyValues getPropertyValues() {
/*  890 */     if (this.propertyValues == null) {
/*  891 */       this.propertyValues = new MutablePropertyValues();
/*      */     }
/*  893 */     return this.propertyValues;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasPropertyValues() {
/*  902 */     return (this.propertyValues != null && !this.propertyValues.isEmpty());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMethodOverrides(MethodOverrides methodOverrides) {
/*  909 */     this.methodOverrides = methodOverrides;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MethodOverrides getMethodOverrides() {
/*  918 */     return this.methodOverrides;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasMethodOverrides() {
/*  926 */     return !this.methodOverrides.isEmpty();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setInitMethodName(@Nullable String initMethodName) {
/*  935 */     this.initMethodName = initMethodName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getInitMethodName() {
/*  944 */     return this.initMethodName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setEnforceInitMethod(boolean enforceInitMethod) {
/*  957 */     this.enforceInitMethod = enforceInitMethod;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEnforceInitMethod() {
/*  965 */     return this.enforceInitMethod;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDestroyMethodName(@Nullable String destroyMethodName) {
/*  974 */     this.destroyMethodName = destroyMethodName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getDestroyMethodName() {
/*  983 */     return this.destroyMethodName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setEnforceDestroyMethod(boolean enforceDestroyMethod) {
/*  996 */     this.enforceDestroyMethod = enforceDestroyMethod;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEnforceDestroyMethod() {
/* 1004 */     return this.enforceDestroyMethod;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSynthetic(boolean synthetic) {
/* 1013 */     this.synthetic = synthetic;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSynthetic() {
/* 1021 */     return this.synthetic;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRole(int role) {
/* 1029 */     this.role = role;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getRole() {
/* 1037 */     return this.role;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDescription(@Nullable String description) {
/* 1045 */     this.description = description;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getDescription() {
/* 1054 */     return this.description;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setResource(@Nullable Resource resource) {
/* 1062 */     this.resource = resource;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public Resource getResource() {
/* 1070 */     return this.resource;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setResourceDescription(@Nullable String resourceDescription) {
/* 1078 */     this.resource = (resourceDescription != null) ? (Resource)new DescriptiveResource(resourceDescription) : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getResourceDescription() {
/* 1088 */     return (this.resource != null) ? this.resource.getDescription() : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setOriginatingBeanDefinition(BeanDefinition originatingBd) {
/* 1095 */     this.resource = (Resource)new BeanDefinitionResource(originatingBd);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public BeanDefinition getOriginatingBeanDefinition() {
/* 1107 */     return (this.resource instanceof BeanDefinitionResource) ? ((BeanDefinitionResource)this.resource)
/* 1108 */       .getBeanDefinition() : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void validate() throws BeanDefinitionValidationException {
/* 1116 */     if (hasMethodOverrides() && getFactoryMethodName() != null) {
/* 1117 */       throw new BeanDefinitionValidationException("Cannot combine factory method with container-generated method overrides: the factory method must create the concrete bean instance.");
/*      */     }
/*      */ 
/*      */     
/* 1121 */     if (hasBeanClass()) {
/* 1122 */       prepareMethodOverrides();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void prepareMethodOverrides() throws BeanDefinitionValidationException {
/* 1133 */     if (hasMethodOverrides()) {
/* 1134 */       getMethodOverrides().getOverrides().forEach(this::prepareMethodOverride);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void prepareMethodOverride(MethodOverride mo) throws BeanDefinitionValidationException {
/* 1146 */     int count = ClassUtils.getMethodCountForName(getBeanClass(), mo.getMethodName());
/* 1147 */     if (count == 0) {
/* 1148 */       throw new BeanDefinitionValidationException("Invalid method override: no method with name '" + mo
/* 1149 */           .getMethodName() + "' on class [" + 
/* 1150 */           getBeanClassName() + "]");
/*      */     }
/* 1152 */     if (count == 1)
/*      */     {
/* 1154 */       mo.setOverloaded(false);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object clone() {
/* 1166 */     return cloneBeanDefinition();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract AbstractBeanDefinition cloneBeanDefinition();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equals(@Nullable Object other) {
/* 1178 */     if (this == other) {
/* 1179 */       return true;
/*      */     }
/* 1181 */     if (!(other instanceof AbstractBeanDefinition)) {
/* 1182 */       return false;
/*      */     }
/* 1184 */     AbstractBeanDefinition that = (AbstractBeanDefinition)other;
/* 1185 */     return (ObjectUtils.nullSafeEquals(getBeanClassName(), that.getBeanClassName()) && 
/* 1186 */       ObjectUtils.nullSafeEquals(this.scope, that.scope) && this.abstractFlag == that.abstractFlag && this.lazyInit == that.lazyInit && this.autowireMode == that.autowireMode && this.dependencyCheck == that.dependencyCheck && 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1191 */       Arrays.equals((Object[])this.dependsOn, (Object[])that.dependsOn) && this.autowireCandidate == that.autowireCandidate && 
/*      */       
/* 1193 */       ObjectUtils.nullSafeEquals(this.qualifiers, that.qualifiers) && this.primary == that.primary && this.nonPublicAccessAllowed == that.nonPublicAccessAllowed && this.lenientConstructorResolution == that.lenientConstructorResolution && 
/*      */ 
/*      */ 
/*      */       
/* 1197 */       equalsConstructorArgumentValues(that) && 
/* 1198 */       equalsPropertyValues(that) && 
/* 1199 */       ObjectUtils.nullSafeEquals(this.methodOverrides, that.methodOverrides) && 
/* 1200 */       ObjectUtils.nullSafeEquals(this.factoryBeanName, that.factoryBeanName) && 
/* 1201 */       ObjectUtils.nullSafeEquals(this.factoryMethodName, that.factoryMethodName) && 
/* 1202 */       ObjectUtils.nullSafeEquals(this.initMethodName, that.initMethodName) && this.enforceInitMethod == that.enforceInitMethod && 
/*      */       
/* 1204 */       ObjectUtils.nullSafeEquals(this.destroyMethodName, that.destroyMethodName) && this.enforceDestroyMethod == that.enforceDestroyMethod && this.synthetic == that.synthetic && this.role == that.role && super
/*      */ 
/*      */ 
/*      */       
/* 1208 */       .equals(other));
/*      */   }
/*      */   
/*      */   private boolean equalsConstructorArgumentValues(AbstractBeanDefinition other) {
/* 1212 */     if (!hasConstructorArgumentValues()) {
/* 1213 */       return !other.hasConstructorArgumentValues();
/*      */     }
/* 1215 */     return ObjectUtils.nullSafeEquals(this.constructorArgumentValues, other.constructorArgumentValues);
/*      */   }
/*      */   
/*      */   private boolean equalsPropertyValues(AbstractBeanDefinition other) {
/* 1219 */     if (!hasPropertyValues()) {
/* 1220 */       return !other.hasPropertyValues();
/*      */     }
/* 1222 */     return ObjectUtils.nullSafeEquals(this.propertyValues, other.propertyValues);
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/* 1227 */     int hashCode = ObjectUtils.nullSafeHashCode(getBeanClassName());
/* 1228 */     hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.scope);
/* 1229 */     if (hasConstructorArgumentValues()) {
/* 1230 */       hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.constructorArgumentValues);
/*      */     }
/* 1232 */     if (hasPropertyValues()) {
/* 1233 */       hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.propertyValues);
/*      */     }
/* 1235 */     hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryBeanName);
/* 1236 */     hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryMethodName);
/* 1237 */     hashCode = 29 * hashCode + super.hashCode();
/* 1238 */     return hashCode;
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1243 */     StringBuilder sb = new StringBuilder("class [");
/* 1244 */     sb.append(getBeanClassName()).append(']');
/* 1245 */     sb.append("; scope=").append(this.scope);
/* 1246 */     sb.append("; abstract=").append(this.abstractFlag);
/* 1247 */     sb.append("; lazyInit=").append(this.lazyInit);
/* 1248 */     sb.append("; autowireMode=").append(this.autowireMode);
/* 1249 */     sb.append("; dependencyCheck=").append(this.dependencyCheck);
/* 1250 */     sb.append("; autowireCandidate=").append(this.autowireCandidate);
/* 1251 */     sb.append("; primary=").append(this.primary);
/* 1252 */     sb.append("; factoryBeanName=").append(this.factoryBeanName);
/* 1253 */     sb.append("; factoryMethodName=").append(this.factoryMethodName);
/* 1254 */     sb.append("; initMethodName=").append(this.initMethodName);
/* 1255 */     sb.append("; destroyMethodName=").append(this.destroyMethodName);
/* 1256 */     if (this.resource != null) {
/* 1257 */       sb.append("; defined in ").append(this.resource.getDescription());
/*      */     }
/* 1259 */     return sb.toString();
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/AbstractBeanDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */