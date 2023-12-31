/*      */ package org.springframework.beans;
/*      */ 
/*      */ import java.beans.PropertyChangeEvent;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Optional;
/*      */ import java.util.Set;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.apache.commons.logging.LogFactory;
/*      */ import org.springframework.core.CollectionFactory;
/*      */ import org.springframework.core.ResolvableType;
/*      */ import org.springframework.core.convert.ConversionException;
/*      */ import org.springframework.core.convert.ConverterNotFoundException;
/*      */ import org.springframework.core.convert.TypeDescriptor;
/*      */ import org.springframework.lang.Nullable;
/*      */ import org.springframework.util.Assert;
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
/*      */ public abstract class AbstractNestablePropertyAccessor
/*      */   extends AbstractPropertyAccessor
/*      */ {
/*   77 */   private static final Log logger = LogFactory.getLog(AbstractNestablePropertyAccessor.class);
/*      */   
/*   79 */   private int autoGrowCollectionLimit = Integer.MAX_VALUE;
/*      */   
/*      */   @Nullable
/*      */   Object wrappedObject;
/*      */   
/*   84 */   private String nestedPath = "";
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   Object rootObject;
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private Map<String, AbstractNestablePropertyAccessor> nestedPropertyAccessors;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected AbstractNestablePropertyAccessor() {
/*  100 */     this(true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected AbstractNestablePropertyAccessor(boolean registerDefaultEditors) {
/*  110 */     if (registerDefaultEditors) {
/*  111 */       registerDefaultEditors();
/*      */     }
/*  113 */     this.typeConverterDelegate = new TypeConverterDelegate(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected AbstractNestablePropertyAccessor(Object object) {
/*  121 */     registerDefaultEditors();
/*  122 */     setWrappedInstance(object);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected AbstractNestablePropertyAccessor(Class<?> clazz) {
/*  130 */     registerDefaultEditors();
/*  131 */     setWrappedInstance(BeanUtils.instantiateClass(clazz));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected AbstractNestablePropertyAccessor(Object object, String nestedPath, Object rootObject) {
/*  142 */     registerDefaultEditors();
/*  143 */     setWrappedInstance(object, nestedPath, rootObject);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected AbstractNestablePropertyAccessor(Object object, String nestedPath, AbstractNestablePropertyAccessor parent) {
/*  154 */     setWrappedInstance(object, nestedPath, parent.getWrappedInstance());
/*  155 */     setExtractOldValueForEditor(parent.isExtractOldValueForEditor());
/*  156 */     setAutoGrowNestedPaths(parent.isAutoGrowNestedPaths());
/*  157 */     setAutoGrowCollectionLimit(parent.getAutoGrowCollectionLimit());
/*  158 */     setConversionService(parent.getConversionService());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutoGrowCollectionLimit(int autoGrowCollectionLimit) {
/*  167 */     this.autoGrowCollectionLimit = autoGrowCollectionLimit;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getAutoGrowCollectionLimit() {
/*  174 */     return this.autoGrowCollectionLimit;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setWrappedInstance(Object object) {
/*  183 */     setWrappedInstance(object, "", (Object)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setWrappedInstance(Object object, @Nullable String nestedPath, @Nullable Object rootObject) {
/*  194 */     this.wrappedObject = ObjectUtils.unwrapOptional(object);
/*  195 */     Assert.notNull(this.wrappedObject, "Target object must not be null");
/*  196 */     this.nestedPath = (nestedPath != null) ? nestedPath : "";
/*  197 */     this.rootObject = !this.nestedPath.isEmpty() ? rootObject : this.wrappedObject;
/*  198 */     this.nestedPropertyAccessors = null;
/*  199 */     this.typeConverterDelegate = new TypeConverterDelegate(this, this.wrappedObject);
/*      */   }
/*      */   
/*      */   public final Object getWrappedInstance() {
/*  203 */     Assert.state((this.wrappedObject != null), "No wrapped object");
/*  204 */     return this.wrappedObject;
/*      */   }
/*      */   
/*      */   public final Class<?> getWrappedClass() {
/*  208 */     return getWrappedInstance().getClass();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final String getNestedPath() {
/*  215 */     return this.nestedPath;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Object getRootInstance() {
/*  223 */     Assert.state((this.rootObject != null), "No root object");
/*  224 */     return this.rootObject;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Class<?> getRootClass() {
/*  232 */     return getRootInstance().getClass();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setPropertyValue(String propertyName, @Nullable Object value) throws BeansException {
/*      */     AbstractNestablePropertyAccessor nestedPa;
/*      */     try {
/*  239 */       nestedPa = getPropertyAccessorForPropertyPath(propertyName);
/*      */     }
/*  241 */     catch (NotReadablePropertyException ex) {
/*  242 */       throw new NotWritablePropertyException(getRootClass(), this.nestedPath + propertyName, "Nested property in path '" + propertyName + "' does not exist", ex);
/*      */     } 
/*      */     
/*  245 */     PropertyTokenHolder tokens = getPropertyNameTokens(getFinalPath(nestedPa, propertyName));
/*  246 */     nestedPa.setPropertyValue(tokens, new PropertyValue(propertyName, value));
/*      */   }
/*      */ 
/*      */   
/*      */   public void setPropertyValue(PropertyValue pv) throws BeansException {
/*  251 */     PropertyTokenHolder tokens = (PropertyTokenHolder)pv.resolvedTokens;
/*  252 */     if (tokens == null) {
/*  253 */       AbstractNestablePropertyAccessor nestedPa; String propertyName = pv.getName();
/*      */       
/*      */       try {
/*  256 */         nestedPa = getPropertyAccessorForPropertyPath(propertyName);
/*      */       }
/*  258 */       catch (NotReadablePropertyException ex) {
/*  259 */         throw new NotWritablePropertyException(getRootClass(), this.nestedPath + propertyName, "Nested property in path '" + propertyName + "' does not exist", ex);
/*      */       } 
/*      */       
/*  262 */       tokens = getPropertyNameTokens(getFinalPath(nestedPa, propertyName));
/*  263 */       if (nestedPa == this) {
/*  264 */         (pv.getOriginalPropertyValue()).resolvedTokens = tokens;
/*      */       }
/*  266 */       nestedPa.setPropertyValue(tokens, pv);
/*      */     } else {
/*      */       
/*  269 */       setPropertyValue(tokens, pv);
/*      */     } 
/*      */   }
/*      */   
/*      */   protected void setPropertyValue(PropertyTokenHolder tokens, PropertyValue pv) throws BeansException {
/*  274 */     if (tokens.keys != null) {
/*  275 */       processKeyedProperty(tokens, pv);
/*      */     } else {
/*      */       
/*  278 */       processLocalProperty(tokens, pv);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void processKeyedProperty(PropertyTokenHolder tokens, PropertyValue pv) {
/*  284 */     Object propValue = getPropertyHoldingValue(tokens);
/*  285 */     PropertyHandler ph = getLocalPropertyHandler(tokens.actualName);
/*  286 */     if (ph == null) {
/*  287 */       throw new InvalidPropertyException(
/*  288 */           getRootClass(), this.nestedPath + tokens.actualName, "No property handler found");
/*      */     }
/*  290 */     Assert.state((tokens.keys != null), "No token keys");
/*  291 */     String lastKey = tokens.keys[tokens.keys.length - 1];
/*      */     
/*  293 */     if (propValue.getClass().isArray()) {
/*  294 */       Class<?> requiredType = propValue.getClass().getComponentType();
/*  295 */       int arrayIndex = Integer.parseInt(lastKey);
/*  296 */       Object oldValue = null;
/*      */       try {
/*  298 */         if (isExtractOldValueForEditor() && arrayIndex < Array.getLength(propValue)) {
/*  299 */           oldValue = Array.get(propValue, arrayIndex);
/*      */         }
/*  301 */         Object convertedValue = convertIfNecessary(tokens.canonicalName, oldValue, pv.getValue(), requiredType, ph
/*  302 */             .nested(tokens.keys.length));
/*  303 */         int length = Array.getLength(propValue);
/*  304 */         if (arrayIndex >= length && arrayIndex < this.autoGrowCollectionLimit) {
/*  305 */           Class<?> componentType = propValue.getClass().getComponentType();
/*  306 */           Object newArray = Array.newInstance(componentType, arrayIndex + 1);
/*  307 */           System.arraycopy(propValue, 0, newArray, 0, length);
/*  308 */           int lastKeyIndex = tokens.canonicalName.lastIndexOf('[');
/*  309 */           String propName = tokens.canonicalName.substring(0, lastKeyIndex);
/*  310 */           setPropertyValue(propName, newArray);
/*  311 */           propValue = getPropertyValue(propName);
/*      */         } 
/*  313 */         Array.set(propValue, arrayIndex, convertedValue);
/*      */       }
/*  315 */       catch (IndexOutOfBoundsException ex) {
/*  316 */         throw new InvalidPropertyException(getRootClass(), this.nestedPath + tokens.canonicalName, "Invalid array index in property path '" + tokens.canonicalName + "'", ex);
/*      */       
/*      */       }
/*      */     
/*      */     }
/*  321 */     else if (propValue instanceof List) {
/*  322 */       Class<?> requiredType = ph.getCollectionType(tokens.keys.length);
/*  323 */       List<Object> list = (List<Object>)propValue;
/*  324 */       int index = Integer.parseInt(lastKey);
/*  325 */       Object oldValue = null;
/*  326 */       if (isExtractOldValueForEditor() && index < list.size()) {
/*  327 */         oldValue = list.get(index);
/*      */       }
/*  329 */       Object convertedValue = convertIfNecessary(tokens.canonicalName, oldValue, pv.getValue(), requiredType, ph
/*  330 */           .nested(tokens.keys.length));
/*  331 */       int size = list.size();
/*  332 */       if (index >= size && index < this.autoGrowCollectionLimit) {
/*  333 */         for (int i = size; i < index; i++) {
/*      */           try {
/*  335 */             list.add(null);
/*      */           }
/*  337 */           catch (NullPointerException ex) {
/*  338 */             throw new InvalidPropertyException(getRootClass(), this.nestedPath + tokens.canonicalName, "Cannot set element with index " + index + " in List of size " + size + ", accessed using property path '" + tokens.canonicalName + "': List does not support filling up gaps with null elements");
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*  344 */         list.add(convertedValue);
/*      */       } else {
/*      */         
/*      */         try {
/*  348 */           list.set(index, convertedValue);
/*      */         }
/*  350 */         catch (IndexOutOfBoundsException ex) {
/*  351 */           throw new InvalidPropertyException(getRootClass(), this.nestedPath + tokens.canonicalName, "Invalid list index in property path '" + tokens.canonicalName + "'", ex);
/*      */         }
/*      */       
/*      */       }
/*      */     
/*      */     }
/*  357 */     else if (propValue instanceof Map) {
/*  358 */       Class<?> mapKeyType = ph.getMapKeyType(tokens.keys.length);
/*  359 */       Class<?> mapValueType = ph.getMapValueType(tokens.keys.length);
/*  360 */       Map<Object, Object> map = (Map<Object, Object>)propValue;
/*      */ 
/*      */       
/*  363 */       TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(mapKeyType);
/*  364 */       Object convertedMapKey = convertIfNecessary((String)null, (Object)null, lastKey, mapKeyType, typeDescriptor);
/*  365 */       Object oldValue = null;
/*  366 */       if (isExtractOldValueForEditor()) {
/*  367 */         oldValue = map.get(convertedMapKey);
/*      */       }
/*      */ 
/*      */       
/*  371 */       Object convertedMapValue = convertIfNecessary(tokens.canonicalName, oldValue, pv.getValue(), mapValueType, ph
/*  372 */           .nested(tokens.keys.length));
/*  373 */       map.put(convertedMapKey, convertedMapValue);
/*      */     }
/*      */     else {
/*      */       
/*  377 */       throw new InvalidPropertyException(getRootClass(), this.nestedPath + tokens.canonicalName, "Property referenced in indexed property path '" + tokens.canonicalName + "' is neither an array nor a List nor a Map; returned value was [" + propValue + "]");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private Object getPropertyHoldingValue(PropertyTokenHolder tokens) {
/*      */     Object propValue;
/*  385 */     Assert.state((tokens.keys != null), "No token keys");
/*  386 */     PropertyTokenHolder getterTokens = new PropertyTokenHolder(tokens.actualName);
/*  387 */     getterTokens.canonicalName = tokens.canonicalName;
/*  388 */     getterTokens.keys = new String[tokens.keys.length - 1];
/*  389 */     System.arraycopy(tokens.keys, 0, getterTokens.keys, 0, tokens.keys.length - 1);
/*      */ 
/*      */     
/*      */     try {
/*  393 */       propValue = getPropertyValue(getterTokens);
/*      */     }
/*  395 */     catch (NotReadablePropertyException ex) {
/*  396 */       throw new NotWritablePropertyException(getRootClass(), this.nestedPath + tokens.canonicalName, "Cannot access indexed value in property referenced in indexed property path '" + tokens.canonicalName + "'", ex);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  401 */     if (propValue == null)
/*      */     {
/*  403 */       if (isAutoGrowNestedPaths()) {
/*  404 */         int lastKeyIndex = tokens.canonicalName.lastIndexOf('[');
/*  405 */         getterTokens.canonicalName = tokens.canonicalName.substring(0, lastKeyIndex);
/*  406 */         propValue = setDefaultValue(getterTokens);
/*      */       } else {
/*      */         
/*  409 */         throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + tokens.canonicalName, "Cannot access indexed value in property referenced in indexed property path '" + tokens.canonicalName + "': returned null");
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*  414 */     return propValue;
/*      */   }
/*      */   
/*      */   private void processLocalProperty(PropertyTokenHolder tokens, PropertyValue pv) {
/*  418 */     PropertyHandler ph = getLocalPropertyHandler(tokens.actualName);
/*  419 */     if (ph == null || !ph.isWritable()) {
/*  420 */       if (pv.isOptional()) {
/*  421 */         if (logger.isDebugEnabled()) {
/*  422 */           logger.debug("Ignoring optional value for property '" + tokens.actualName + "' - property not found on bean class [" + 
/*  423 */               getRootClass().getName() + "]");
/*      */         }
/*      */         return;
/*      */       } 
/*  427 */       if (this.suppressNotWritablePropertyException) {
/*      */         return;
/*      */       }
/*      */ 
/*      */       
/*  432 */       throw createNotWritablePropertyException(tokens.canonicalName);
/*      */     } 
/*      */     
/*  435 */     Object oldValue = null;
/*      */     try {
/*  437 */       Object originalValue = pv.getValue();
/*  438 */       Object valueToApply = originalValue;
/*  439 */       if (!Boolean.FALSE.equals(pv.conversionNecessary)) {
/*  440 */         if (pv.isConverted()) {
/*  441 */           valueToApply = pv.getConvertedValue();
/*      */         } else {
/*      */           
/*  444 */           if (isExtractOldValueForEditor() && ph.isReadable()) {
/*      */             try {
/*  446 */               oldValue = ph.getValue();
/*      */             }
/*  448 */             catch (Exception ex) {
/*  449 */               if (ex instanceof PrivilegedActionException) {
/*  450 */                 ex = ((PrivilegedActionException)ex).getException();
/*      */               }
/*  452 */               if (logger.isDebugEnabled()) {
/*  453 */                 logger.debug("Could not read previous value of property '" + this.nestedPath + tokens.canonicalName + "'", ex);
/*      */               }
/*      */             } 
/*      */           }
/*      */           
/*  458 */           valueToApply = convertForProperty(tokens.canonicalName, oldValue, originalValue, ph
/*  459 */               .toTypeDescriptor());
/*      */         } 
/*  461 */         (pv.getOriginalPropertyValue()).conversionNecessary = Boolean.valueOf((valueToApply != originalValue));
/*      */       } 
/*  463 */       ph.setValue(valueToApply);
/*      */     }
/*  465 */     catch (TypeMismatchException ex) {
/*  466 */       throw ex;
/*      */     }
/*  468 */     catch (InvocationTargetException ex) {
/*      */       
/*  470 */       PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(getRootInstance(), this.nestedPath + tokens.canonicalName, oldValue, pv.getValue());
/*  471 */       if (ex.getTargetException() instanceof ClassCastException) {
/*  472 */         throw new TypeMismatchException(propertyChangeEvent, ph.getPropertyType(), ex.getTargetException());
/*      */       }
/*      */       
/*  475 */       Throwable cause = ex.getTargetException();
/*  476 */       if (cause instanceof java.lang.reflect.UndeclaredThrowableException)
/*      */       {
/*  478 */         cause = cause.getCause();
/*      */       }
/*  480 */       throw new MethodInvocationException(propertyChangeEvent, cause);
/*      */     
/*      */     }
/*  483 */     catch (Exception ex) {
/*      */       
/*  485 */       PropertyChangeEvent pce = new PropertyChangeEvent(getRootInstance(), this.nestedPath + tokens.canonicalName, oldValue, pv.getValue());
/*  486 */       throw new MethodInvocationException(pce, ex);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public Class<?> getPropertyType(String propertyName) throws BeansException {
/*      */     try {
/*  494 */       PropertyHandler ph = getPropertyHandler(propertyName);
/*  495 */       if (ph != null) {
/*  496 */         return ph.getPropertyType();
/*      */       }
/*      */ 
/*      */       
/*  500 */       Object value = getPropertyValue(propertyName);
/*  501 */       if (value != null) {
/*  502 */         return value.getClass();
/*      */       }
/*      */ 
/*      */       
/*  506 */       Class<?> editorType = guessPropertyTypeFromEditors(propertyName);
/*  507 */       if (editorType != null) {
/*  508 */         return editorType;
/*      */       
/*      */       }
/*      */     }
/*  512 */     catch (InvalidPropertyException invalidPropertyException) {}
/*      */ 
/*      */     
/*  515 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public TypeDescriptor getPropertyTypeDescriptor(String propertyName) throws BeansException {
/*      */     try {
/*  522 */       AbstractNestablePropertyAccessor nestedPa = getPropertyAccessorForPropertyPath(propertyName);
/*  523 */       String finalPath = getFinalPath(nestedPa, propertyName);
/*  524 */       PropertyTokenHolder tokens = getPropertyNameTokens(finalPath);
/*  525 */       PropertyHandler ph = nestedPa.getLocalPropertyHandler(tokens.actualName);
/*  526 */       if (ph != null) {
/*  527 */         if (tokens.keys != null) {
/*  528 */           if (ph.isReadable() || ph.isWritable()) {
/*  529 */             return ph.nested(tokens.keys.length);
/*      */           
/*      */           }
/*      */         }
/*  533 */         else if (ph.isReadable() || ph.isWritable()) {
/*  534 */           return ph.toTypeDescriptor();
/*      */         }
/*      */       
/*      */       }
/*      */     }
/*  539 */     catch (InvalidPropertyException invalidPropertyException) {}
/*      */ 
/*      */     
/*  542 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isReadableProperty(String propertyName) {
/*      */     try {
/*  548 */       PropertyHandler ph = getPropertyHandler(propertyName);
/*  549 */       if (ph != null) {
/*  550 */         return ph.isReadable();
/*      */       }
/*      */ 
/*      */       
/*  554 */       getPropertyValue(propertyName);
/*  555 */       return true;
/*      */     
/*      */     }
/*  558 */     catch (InvalidPropertyException invalidPropertyException) {
/*      */ 
/*      */       
/*  561 */       return false;
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean isWritableProperty(String propertyName) {
/*      */     try {
/*  567 */       PropertyHandler ph = getPropertyHandler(propertyName);
/*  568 */       if (ph != null) {
/*  569 */         return ph.isWritable();
/*      */       }
/*      */ 
/*      */       
/*  573 */       getPropertyValue(propertyName);
/*  574 */       return true;
/*      */     
/*      */     }
/*  577 */     catch (InvalidPropertyException invalidPropertyException) {
/*      */ 
/*      */       
/*  580 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private Object convertIfNecessary(@Nullable String propertyName, @Nullable Object oldValue, @Nullable Object newValue, @Nullable Class<?> requiredType, @Nullable TypeDescriptor td) throws TypeMismatchException {
/*  588 */     Assert.state((this.typeConverterDelegate != null), "No TypeConverterDelegate");
/*      */     try {
/*  590 */       return this.typeConverterDelegate.convertIfNecessary(propertyName, oldValue, newValue, requiredType, td);
/*      */     }
/*  592 */     catch (ConverterNotFoundException|IllegalStateException ex) {
/*      */       
/*  594 */       PropertyChangeEvent pce = new PropertyChangeEvent(getRootInstance(), this.nestedPath + propertyName, oldValue, newValue);
/*  595 */       throw new ConversionNotSupportedException(pce, requiredType, ex);
/*      */     }
/*  597 */     catch (ConversionException|IllegalArgumentException ex) {
/*      */       
/*  599 */       PropertyChangeEvent pce = new PropertyChangeEvent(getRootInstance(), this.nestedPath + propertyName, oldValue, newValue);
/*  600 */       throw new TypeMismatchException(pce, requiredType, ex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   protected Object convertForProperty(String propertyName, @Nullable Object oldValue, @Nullable Object newValue, TypeDescriptor td) throws TypeMismatchException {
/*  609 */     return convertIfNecessary(propertyName, oldValue, newValue, td.getType(), td);
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public Object getPropertyValue(String propertyName) throws BeansException {
/*  615 */     AbstractNestablePropertyAccessor nestedPa = getPropertyAccessorForPropertyPath(propertyName);
/*  616 */     PropertyTokenHolder tokens = getPropertyNameTokens(getFinalPath(nestedPa, propertyName));
/*  617 */     return nestedPa.getPropertyValue(tokens);
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   protected Object getPropertyValue(PropertyTokenHolder tokens) throws BeansException {
/*  623 */     String propertyName = tokens.canonicalName;
/*  624 */     String actualName = tokens.actualName;
/*  625 */     PropertyHandler ph = getLocalPropertyHandler(actualName);
/*  626 */     if (ph == null || !ph.isReadable()) {
/*  627 */       throw new NotReadablePropertyException(getRootClass(), this.nestedPath + propertyName);
/*      */     }
/*      */     try {
/*  630 */       Object value = ph.getValue();
/*  631 */       if (tokens.keys != null) {
/*  632 */         if (value == null) {
/*  633 */           if (isAutoGrowNestedPaths()) {
/*  634 */             value = setDefaultValue(new PropertyTokenHolder(tokens.actualName));
/*      */           } else {
/*      */             
/*  637 */             throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + propertyName, "Cannot access indexed value of property referenced in indexed property path '" + propertyName + "': returned null");
/*      */           } 
/*      */         }
/*      */ 
/*      */         
/*  642 */         StringBuilder indexedPropertyName = new StringBuilder(tokens.actualName);
/*      */         
/*  644 */         for (int i = 0; i < tokens.keys.length; i++) {
/*  645 */           String key = tokens.keys[i];
/*  646 */           if (value == null) {
/*  647 */             throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + propertyName, "Cannot access indexed value of property referenced in indexed property path '" + propertyName + "': returned null");
/*      */           }
/*      */ 
/*      */           
/*  651 */           if (value.getClass().isArray()) {
/*  652 */             int index = Integer.parseInt(key);
/*  653 */             value = growArrayIfNecessary(value, index, indexedPropertyName.toString());
/*  654 */             value = Array.get(value, index);
/*      */           }
/*  656 */           else if (value instanceof List) {
/*  657 */             int index = Integer.parseInt(key);
/*  658 */             List<Object> list = (List<Object>)value;
/*  659 */             growCollectionIfNecessary(list, index, indexedPropertyName.toString(), ph, i + 1);
/*  660 */             value = list.get(index);
/*      */           }
/*  662 */           else if (value instanceof Set) {
/*      */             
/*  664 */             Set<Object> set = (Set<Object>)value;
/*  665 */             int index = Integer.parseInt(key);
/*  666 */             if (index < 0 || index >= set.size()) {
/*  667 */               throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName, "Cannot get element with index " + index + " from Set of size " + set
/*      */                   
/*  669 */                   .size() + ", accessed using property path '" + propertyName + "'");
/*      */             }
/*  671 */             Iterator<Object> it = set.iterator();
/*  672 */             for (int j = 0; it.hasNext(); j++) {
/*  673 */               Object elem = it.next();
/*  674 */               if (j == index) {
/*  675 */                 value = elem;
/*      */                 
/*      */                 break;
/*      */               } 
/*      */             } 
/*  680 */           } else if (value instanceof Map) {
/*  681 */             Map<Object, Object> map = (Map<Object, Object>)value;
/*  682 */             Class<?> mapKeyType = ph.getResolvableType().getNested(i + 1).asMap().resolveGeneric(new int[] { 0 });
/*      */ 
/*      */             
/*  685 */             TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(mapKeyType);
/*  686 */             Object convertedMapKey = convertIfNecessary((String)null, (Object)null, key, mapKeyType, typeDescriptor);
/*  687 */             value = map.get(convertedMapKey);
/*      */           } else {
/*      */             
/*  690 */             throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName, "Property referenced in indexed property path '" + propertyName + "' is neither an array nor a List nor a Set nor a Map; returned value was [" + value + "]");
/*      */           } 
/*      */ 
/*      */           
/*  694 */           indexedPropertyName.append("[").append(key).append("]");
/*      */         } 
/*      */       } 
/*  697 */       return value;
/*      */     }
/*  699 */     catch (IndexOutOfBoundsException ex) {
/*  700 */       throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName, "Index of out of bounds in property path '" + propertyName + "'", ex);
/*      */     
/*      */     }
/*  703 */     catch (NumberFormatException|TypeMismatchException ex) {
/*  704 */       throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName, "Invalid index in property path '" + propertyName + "'", ex);
/*      */     
/*      */     }
/*  707 */     catch (InvocationTargetException ex) {
/*  708 */       throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName, "Getter for property '" + actualName + "' threw exception", ex);
/*      */     
/*      */     }
/*  711 */     catch (Exception ex) {
/*  712 */       throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName, "Illegal attempt to get property '" + actualName + "' threw exception", ex);
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
/*      */   @Nullable
/*      */   protected PropertyHandler getPropertyHandler(String propertyName) throws BeansException {
/*  728 */     Assert.notNull(propertyName, "Property name must not be null");
/*  729 */     AbstractNestablePropertyAccessor nestedPa = getPropertyAccessorForPropertyPath(propertyName);
/*  730 */     return nestedPa.getLocalPropertyHandler(getFinalPath(nestedPa, propertyName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   protected abstract PropertyHandler getLocalPropertyHandler(String paramString);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected abstract AbstractNestablePropertyAccessor newNestedPropertyAccessor(Object paramObject, String paramString);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected abstract NotWritablePropertyException createNotWritablePropertyException(String paramString);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Object growArrayIfNecessary(Object array, int index, String name) {
/*  758 */     if (!isAutoGrowNestedPaths()) {
/*  759 */       return array;
/*      */     }
/*  761 */     int length = Array.getLength(array);
/*  762 */     if (index >= length && index < this.autoGrowCollectionLimit) {
/*  763 */       Class<?> componentType = array.getClass().getComponentType();
/*  764 */       Object newArray = Array.newInstance(componentType, index + 1);
/*  765 */       System.arraycopy(array, 0, newArray, 0, length);
/*  766 */       for (int i = length; i < Array.getLength(newArray); i++) {
/*  767 */         Array.set(newArray, i, newValue(componentType, (TypeDescriptor)null, name));
/*      */       }
/*  769 */       setPropertyValue(name, newArray);
/*  770 */       Object defaultValue = getPropertyValue(name);
/*  771 */       Assert.state((defaultValue != null), "Default value must not be null");
/*  772 */       return defaultValue;
/*      */     } 
/*      */     
/*  775 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void growCollectionIfNecessary(Collection<Object> collection, int index, String name, PropertyHandler ph, int nestingLevel) {
/*  782 */     if (!isAutoGrowNestedPaths()) {
/*      */       return;
/*      */     }
/*  785 */     int size = collection.size();
/*  786 */     if (index >= size && index < this.autoGrowCollectionLimit) {
/*  787 */       Class<?> elementType = ph.getResolvableType().getNested(nestingLevel).asCollection().resolveGeneric(new int[0]);
/*  788 */       if (elementType != null) {
/*  789 */         for (int i = collection.size(); i < index + 1; i++) {
/*  790 */           collection.add(newValue(elementType, (TypeDescriptor)null, name));
/*      */         }
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String getFinalPath(AbstractNestablePropertyAccessor pa, String nestedPath) {
/*  803 */     if (pa == this) {
/*  804 */       return nestedPath;
/*      */     }
/*  806 */     return nestedPath.substring(PropertyAccessorUtils.getLastNestedPropertySeparatorIndex(nestedPath) + 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected AbstractNestablePropertyAccessor getPropertyAccessorForPropertyPath(String propertyPath) {
/*  815 */     int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(propertyPath);
/*      */     
/*  817 */     if (pos > -1) {
/*  818 */       String nestedProperty = propertyPath.substring(0, pos);
/*  819 */       String nestedPath = propertyPath.substring(pos + 1);
/*  820 */       AbstractNestablePropertyAccessor nestedPa = getNestedPropertyAccessor(nestedProperty);
/*  821 */       return nestedPa.getPropertyAccessorForPropertyPath(nestedPath);
/*      */     } 
/*      */     
/*  824 */     return this;
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
/*      */   private AbstractNestablePropertyAccessor getNestedPropertyAccessor(String nestedProperty) {
/*  837 */     if (this.nestedPropertyAccessors == null) {
/*  838 */       this.nestedPropertyAccessors = new HashMap<>();
/*      */     }
/*      */     
/*  841 */     PropertyTokenHolder tokens = getPropertyNameTokens(nestedProperty);
/*  842 */     String canonicalName = tokens.canonicalName;
/*  843 */     Object value = getPropertyValue(tokens);
/*  844 */     if (value == null || (value instanceof Optional && !((Optional)value).isPresent())) {
/*  845 */       if (isAutoGrowNestedPaths()) {
/*  846 */         value = setDefaultValue(tokens);
/*      */       } else {
/*      */         
/*  849 */         throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + canonicalName);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*  854 */     AbstractNestablePropertyAccessor nestedPa = this.nestedPropertyAccessors.get(canonicalName);
/*  855 */     if (nestedPa == null || nestedPa.getWrappedInstance() != ObjectUtils.unwrapOptional(value)) {
/*  856 */       if (logger.isTraceEnabled()) {
/*  857 */         logger.trace("Creating new nested " + getClass().getSimpleName() + " for property '" + canonicalName + "'");
/*      */       }
/*  859 */       nestedPa = newNestedPropertyAccessor(value, this.nestedPath + canonicalName + ".");
/*      */       
/*  861 */       copyDefaultEditorsTo(nestedPa);
/*  862 */       copyCustomEditorsTo(nestedPa, canonicalName);
/*  863 */       this.nestedPropertyAccessors.put(canonicalName, nestedPa);
/*      */     
/*      */     }
/*  866 */     else if (logger.isTraceEnabled()) {
/*  867 */       logger.trace("Using cached nested property accessor for property '" + canonicalName + "'");
/*      */     } 
/*      */     
/*  870 */     return nestedPa;
/*      */   }
/*      */   
/*      */   private Object setDefaultValue(PropertyTokenHolder tokens) {
/*  874 */     PropertyValue pv = createDefaultPropertyValue(tokens);
/*  875 */     setPropertyValue(tokens, pv);
/*  876 */     Object defaultValue = getPropertyValue(tokens);
/*  877 */     Assert.state((defaultValue != null), "Default value must not be null");
/*  878 */     return defaultValue;
/*      */   }
/*      */   
/*      */   private PropertyValue createDefaultPropertyValue(PropertyTokenHolder tokens) {
/*  882 */     TypeDescriptor desc = getPropertyTypeDescriptor(tokens.canonicalName);
/*  883 */     if (desc == null) {
/*  884 */       throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + tokens.canonicalName, "Could not determine property type for auto-growing a default value");
/*      */     }
/*      */     
/*  887 */     Object defaultValue = newValue(desc.getType(), desc, tokens.canonicalName);
/*  888 */     return new PropertyValue(tokens.canonicalName, defaultValue);
/*      */   }
/*      */   
/*      */   private Object newValue(Class<?> type, @Nullable TypeDescriptor desc, String name) {
/*      */     try {
/*  893 */       if (type.isArray()) {
/*  894 */         Class<?> componentType = type.getComponentType();
/*      */         
/*  896 */         if (componentType.isArray()) {
/*  897 */           Object array = Array.newInstance(componentType, 1);
/*  898 */           Array.set(array, 0, Array.newInstance(componentType.getComponentType(), 0));
/*  899 */           return array;
/*      */         } 
/*      */         
/*  902 */         return Array.newInstance(componentType, 0);
/*      */       } 
/*      */       
/*  905 */       if (Collection.class.isAssignableFrom(type)) {
/*  906 */         TypeDescriptor elementDesc = (desc != null) ? desc.getElementTypeDescriptor() : null;
/*  907 */         return CollectionFactory.createCollection(type, (elementDesc != null) ? elementDesc.getType() : null, 16);
/*      */       } 
/*  909 */       if (Map.class.isAssignableFrom(type)) {
/*  910 */         TypeDescriptor keyDesc = (desc != null) ? desc.getMapKeyTypeDescriptor() : null;
/*  911 */         return CollectionFactory.createMap(type, (keyDesc != null) ? keyDesc.getType() : null, 16);
/*      */       } 
/*      */       
/*  914 */       Constructor<?> ctor = type.getDeclaredConstructor(new Class[0]);
/*  915 */       if (Modifier.isPrivate(ctor.getModifiers())) {
/*  916 */         throw new IllegalAccessException("Auto-growing not allowed with private constructor: " + ctor);
/*      */       }
/*  918 */       return BeanUtils.instantiateClass(ctor, new Object[0]);
/*      */     
/*      */     }
/*  921 */     catch (Throwable ex) {
/*  922 */       throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + name, "Could not instantiate property type [" + type
/*  923 */           .getName() + "] to auto-grow nested property path", ex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private PropertyTokenHolder getPropertyNameTokens(String propertyName) {
/*  933 */     String actualName = null;
/*  934 */     List<String> keys = new ArrayList<>(2);
/*  935 */     int searchIndex = 0;
/*  936 */     while (searchIndex != -1) {
/*  937 */       int keyStart = propertyName.indexOf("[", searchIndex);
/*  938 */       searchIndex = -1;
/*  939 */       if (keyStart != -1) {
/*  940 */         int keyEnd = getPropertyNameKeyEnd(propertyName, keyStart + "[".length());
/*  941 */         if (keyEnd != -1) {
/*  942 */           if (actualName == null) {
/*  943 */             actualName = propertyName.substring(0, keyStart);
/*      */           }
/*  945 */           String key = propertyName.substring(keyStart + "[".length(), keyEnd);
/*  946 */           if ((key.length() > 1 && key.startsWith("'") && key.endsWith("'")) || (key
/*  947 */             .startsWith("\"") && key.endsWith("\""))) {
/*  948 */             key = key.substring(1, key.length() - 1);
/*      */           }
/*  950 */           keys.add(key);
/*  951 */           searchIndex = keyEnd + "]".length();
/*      */         } 
/*      */       } 
/*      */     } 
/*  955 */     PropertyTokenHolder tokens = new PropertyTokenHolder((actualName != null) ? actualName : propertyName);
/*  956 */     if (!keys.isEmpty()) {
/*  957 */       tokens
/*  958 */         .canonicalName = tokens.canonicalName + "[" + StringUtils.collectionToDelimitedString(keys, "][") + "]";
/*      */       
/*  960 */       tokens.keys = StringUtils.toStringArray(keys);
/*      */     } 
/*  962 */     return tokens;
/*      */   }
/*      */   
/*      */   private int getPropertyNameKeyEnd(String propertyName, int startIndex) {
/*  966 */     int unclosedPrefixes = 0;
/*  967 */     int length = propertyName.length();
/*  968 */     for (int i = startIndex; i < length; i++) {
/*  969 */       switch (propertyName.charAt(i)) {
/*      */         
/*      */         case '[':
/*  972 */           unclosedPrefixes++;
/*      */           break;
/*      */         case ']':
/*  975 */           if (unclosedPrefixes == 0)
/*      */           {
/*      */             
/*  978 */             return i;
/*      */           }
/*      */ 
/*      */ 
/*      */           
/*  983 */           unclosedPrefixes--;
/*      */           break;
/*      */       } 
/*      */     
/*      */     } 
/*  988 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/*  994 */     String className = getClass().getName();
/*  995 */     if (this.wrappedObject == null) {
/*  996 */       return className + ": no wrapped object set";
/*      */     }
/*  998 */     return className + ": wrapping object [" + ObjectUtils.identityToString(this.wrappedObject) + ']';
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static abstract class PropertyHandler
/*      */   {
/*      */     private final Class<?> propertyType;
/*      */ 
/*      */     
/*      */     private final boolean readable;
/*      */     
/*      */     private final boolean writable;
/*      */ 
/*      */     
/*      */     public PropertyHandler(Class<?> propertyType, boolean readable, boolean writable) {
/* 1014 */       this.propertyType = propertyType;
/* 1015 */       this.readable = readable;
/* 1016 */       this.writable = writable;
/*      */     }
/*      */     
/*      */     public Class<?> getPropertyType() {
/* 1020 */       return this.propertyType;
/*      */     }
/*      */     
/*      */     public boolean isReadable() {
/* 1024 */       return this.readable;
/*      */     }
/*      */     
/*      */     public boolean isWritable() {
/* 1028 */       return this.writable;
/*      */     }
/*      */     
/*      */     public abstract TypeDescriptor toTypeDescriptor();
/*      */     
/*      */     public abstract ResolvableType getResolvableType();
/*      */     
/*      */     @Nullable
/*      */     public Class<?> getMapKeyType(int nestingLevel) {
/* 1037 */       return getResolvableType().getNested(nestingLevel).asMap().resolveGeneric(new int[] { 0 });
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     public Class<?> getMapValueType(int nestingLevel) {
/* 1042 */       return getResolvableType().getNested(nestingLevel).asMap().resolveGeneric(new int[] { 1 });
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     public Class<?> getCollectionType(int nestingLevel) {
/* 1047 */       return getResolvableType().getNested(nestingLevel).asCollection().resolveGeneric(new int[0]);
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     public abstract TypeDescriptor nested(int param1Int);
/*      */     
/*      */     @Nullable
/*      */     public abstract Object getValue() throws Exception;
/*      */     
/*      */     public abstract void setValue(@Nullable Object param1Object) throws Exception;
/*      */   }
/*      */   
/*      */   protected static class PropertyTokenHolder {
/*      */     public String actualName;
/*      */     public String canonicalName;
/*      */     @Nullable
/*      */     public String[] keys;
/*      */     
/*      */     public PropertyTokenHolder(String name) {
/* 1066 */       this.actualName = name;
/* 1067 */       this.canonicalName = name;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/AbstractNestablePropertyAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */