/*     */ package org.springframework.beans;
/*     */ 
/*     */ import java.beans.PropertyEditor;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.file.Path;
/*     */ import java.time.ZoneId;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Currency;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TimeZone;
/*     */ import java.util.UUID;
/*     */ import java.util.regex.Pattern;
/*     */ import org.springframework.beans.propertyeditors.ByteArrayPropertyEditor;
/*     */ import org.springframework.beans.propertyeditors.CharArrayPropertyEditor;
/*     */ import org.springframework.beans.propertyeditors.CharacterEditor;
/*     */ import org.springframework.beans.propertyeditors.CharsetEditor;
/*     */ import org.springframework.beans.propertyeditors.ClassArrayEditor;
/*     */ import org.springframework.beans.propertyeditors.ClassEditor;
/*     */ import org.springframework.beans.propertyeditors.CurrencyEditor;
/*     */ import org.springframework.beans.propertyeditors.CustomBooleanEditor;
/*     */ import org.springframework.beans.propertyeditors.CustomCollectionEditor;
/*     */ import org.springframework.beans.propertyeditors.CustomMapEditor;
/*     */ import org.springframework.beans.propertyeditors.CustomNumberEditor;
/*     */ import org.springframework.beans.propertyeditors.FileEditor;
/*     */ import org.springframework.beans.propertyeditors.InputSourceEditor;
/*     */ import org.springframework.beans.propertyeditors.InputStreamEditor;
/*     */ import org.springframework.beans.propertyeditors.LocaleEditor;
/*     */ import org.springframework.beans.propertyeditors.PathEditor;
/*     */ import org.springframework.beans.propertyeditors.PatternEditor;
/*     */ import org.springframework.beans.propertyeditors.PropertiesEditor;
/*     */ import org.springframework.beans.propertyeditors.ReaderEditor;
/*     */ import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
/*     */ import org.springframework.beans.propertyeditors.TimeZoneEditor;
/*     */ import org.springframework.beans.propertyeditors.URIEditor;
/*     */ import org.springframework.beans.propertyeditors.URLEditor;
/*     */ import org.springframework.beans.propertyeditors.UUIDEditor;
/*     */ import org.springframework.beans.propertyeditors.ZoneIdEditor;
/*     */ import org.springframework.core.SpringProperties;
/*     */ import org.springframework.core.convert.ConversionService;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.core.io.support.ResourceArrayPropertyEditor;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.xml.sax.InputSource;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PropertyEditorRegistrySupport
/*     */   implements PropertyEditorRegistry
/*     */ {
/* 101 */   private static final boolean shouldIgnoreXml = SpringProperties.getFlag("spring.xml.ignore");
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private ConversionService conversionService;
/*     */ 
/*     */   
/*     */   private boolean defaultEditorsActive = false;
/*     */ 
/*     */   
/*     */   private boolean configValueEditorsActive = false;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Map<Class<?>, PropertyEditor> defaultEditors;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Map<Class<?>, PropertyEditor> overriddenDefaultEditors;
/*     */   
/*     */   @Nullable
/*     */   private Map<Class<?>, PropertyEditor> customEditors;
/*     */   
/*     */   @Nullable
/*     */   private Map<String, CustomEditorHolder> customEditorsForPath;
/*     */   
/*     */   @Nullable
/*     */   private Map<Class<?>, PropertyEditor> customEditorCache;
/*     */ 
/*     */   
/*     */   public void setConversionService(@Nullable ConversionService conversionService) {
/* 132 */     this.conversionService = conversionService;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public ConversionService getConversionService() {
/* 140 */     return this.conversionService;
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
/*     */   protected void registerDefaultEditors() {
/* 153 */     this.defaultEditorsActive = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void useConfigValueEditors() {
/* 164 */     this.configValueEditorsActive = true;
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
/*     */   public void overrideDefaultEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
/* 177 */     if (this.overriddenDefaultEditors == null) {
/* 178 */       this.overriddenDefaultEditors = new HashMap<>();
/*     */     }
/* 180 */     this.overriddenDefaultEditors.put(requiredType, propertyEditor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public PropertyEditor getDefaultEditor(Class<?> requiredType) {
/* 192 */     if (!this.defaultEditorsActive) {
/* 193 */       return null;
/*     */     }
/* 195 */     if (this.overriddenDefaultEditors != null) {
/* 196 */       PropertyEditor editor = this.overriddenDefaultEditors.get(requiredType);
/* 197 */       if (editor != null) {
/* 198 */         return editor;
/*     */       }
/*     */     } 
/* 201 */     if (this.defaultEditors == null) {
/* 202 */       createDefaultEditors();
/*     */     }
/* 204 */     return this.defaultEditors.get(requiredType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void createDefaultEditors() {
/* 211 */     this.defaultEditors = new HashMap<>(64);
/*     */ 
/*     */ 
/*     */     
/* 215 */     this.defaultEditors.put(Charset.class, new CharsetEditor());
/* 216 */     this.defaultEditors.put(Class.class, new ClassEditor());
/* 217 */     this.defaultEditors.put(Class[].class, new ClassArrayEditor());
/* 218 */     this.defaultEditors.put(Currency.class, new CurrencyEditor());
/* 219 */     this.defaultEditors.put(File.class, new FileEditor());
/* 220 */     this.defaultEditors.put(InputStream.class, new InputStreamEditor());
/* 221 */     if (!shouldIgnoreXml) {
/* 222 */       this.defaultEditors.put(InputSource.class, new InputSourceEditor());
/*     */     }
/* 224 */     this.defaultEditors.put(Locale.class, new LocaleEditor());
/* 225 */     this.defaultEditors.put(Path.class, new PathEditor());
/* 226 */     this.defaultEditors.put(Pattern.class, new PatternEditor());
/* 227 */     this.defaultEditors.put(Properties.class, new PropertiesEditor());
/* 228 */     this.defaultEditors.put(Reader.class, new ReaderEditor());
/* 229 */     this.defaultEditors.put(Resource[].class, new ResourceArrayPropertyEditor());
/* 230 */     this.defaultEditors.put(TimeZone.class, new TimeZoneEditor());
/* 231 */     this.defaultEditors.put(URI.class, new URIEditor());
/* 232 */     this.defaultEditors.put(URL.class, new URLEditor());
/* 233 */     this.defaultEditors.put(UUID.class, new UUIDEditor());
/* 234 */     this.defaultEditors.put(ZoneId.class, new ZoneIdEditor());
/*     */ 
/*     */ 
/*     */     
/* 238 */     this.defaultEditors.put(Collection.class, new CustomCollectionEditor(Collection.class));
/* 239 */     this.defaultEditors.put(Set.class, new CustomCollectionEditor(Set.class));
/* 240 */     this.defaultEditors.put(SortedSet.class, new CustomCollectionEditor(SortedSet.class));
/* 241 */     this.defaultEditors.put(List.class, new CustomCollectionEditor(List.class));
/* 242 */     this.defaultEditors.put(SortedMap.class, new CustomMapEditor(SortedMap.class));
/*     */ 
/*     */     
/* 245 */     this.defaultEditors.put(byte[].class, new ByteArrayPropertyEditor());
/* 246 */     this.defaultEditors.put(char[].class, new CharArrayPropertyEditor());
/*     */ 
/*     */     
/* 249 */     this.defaultEditors.put(char.class, new CharacterEditor(false));
/* 250 */     this.defaultEditors.put(Character.class, new CharacterEditor(true));
/*     */ 
/*     */     
/* 253 */     this.defaultEditors.put(boolean.class, new CustomBooleanEditor(false));
/* 254 */     this.defaultEditors.put(Boolean.class, new CustomBooleanEditor(true));
/*     */ 
/*     */ 
/*     */     
/* 258 */     this.defaultEditors.put(byte.class, new CustomNumberEditor(Byte.class, false));
/* 259 */     this.defaultEditors.put(Byte.class, new CustomNumberEditor(Byte.class, true));
/* 260 */     this.defaultEditors.put(short.class, new CustomNumberEditor(Short.class, false));
/* 261 */     this.defaultEditors.put(Short.class, new CustomNumberEditor(Short.class, true));
/* 262 */     this.defaultEditors.put(int.class, new CustomNumberEditor(Integer.class, false));
/* 263 */     this.defaultEditors.put(Integer.class, new CustomNumberEditor(Integer.class, true));
/* 264 */     this.defaultEditors.put(long.class, new CustomNumberEditor(Long.class, false));
/* 265 */     this.defaultEditors.put(Long.class, new CustomNumberEditor(Long.class, true));
/* 266 */     this.defaultEditors.put(float.class, new CustomNumberEditor(Float.class, false));
/* 267 */     this.defaultEditors.put(Float.class, new CustomNumberEditor(Float.class, true));
/* 268 */     this.defaultEditors.put(double.class, new CustomNumberEditor(Double.class, false));
/* 269 */     this.defaultEditors.put(Double.class, new CustomNumberEditor(Double.class, true));
/* 270 */     this.defaultEditors.put(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
/* 271 */     this.defaultEditors.put(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));
/*     */ 
/*     */     
/* 274 */     if (this.configValueEditorsActive) {
/* 275 */       StringArrayPropertyEditor sae = new StringArrayPropertyEditor();
/* 276 */       this.defaultEditors.put(String[].class, sae);
/* 277 */       this.defaultEditors.put(short[].class, sae);
/* 278 */       this.defaultEditors.put(int[].class, sae);
/* 279 */       this.defaultEditors.put(long[].class, sae);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void copyDefaultEditorsTo(PropertyEditorRegistrySupport target) {
/* 288 */     target.defaultEditorsActive = this.defaultEditorsActive;
/* 289 */     target.configValueEditorsActive = this.configValueEditorsActive;
/* 290 */     target.defaultEditors = this.defaultEditors;
/* 291 */     target.overriddenDefaultEditors = this.overriddenDefaultEditors;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
/* 301 */     registerCustomEditor(requiredType, null, propertyEditor);
/*     */   }
/*     */ 
/*     */   
/*     */   public void registerCustomEditor(@Nullable Class<?> requiredType, @Nullable String propertyPath, PropertyEditor propertyEditor) {
/* 306 */     if (requiredType == null && propertyPath == null) {
/* 307 */       throw new IllegalArgumentException("Either requiredType or propertyPath is required");
/*     */     }
/* 309 */     if (propertyPath != null) {
/* 310 */       if (this.customEditorsForPath == null) {
/* 311 */         this.customEditorsForPath = new LinkedHashMap<>(16);
/*     */       }
/* 313 */       this.customEditorsForPath.put(propertyPath, new CustomEditorHolder(propertyEditor, requiredType));
/*     */     } else {
/*     */       
/* 316 */       if (this.customEditors == null) {
/* 317 */         this.customEditors = new LinkedHashMap<>(16);
/*     */       }
/* 319 */       this.customEditors.put(requiredType, propertyEditor);
/* 320 */       this.customEditorCache = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public PropertyEditor findCustomEditor(@Nullable Class<?> requiredType, @Nullable String propertyPath) {
/* 327 */     Class<?> requiredTypeToUse = requiredType;
/* 328 */     if (propertyPath != null) {
/* 329 */       if (this.customEditorsForPath != null) {
/*     */         
/* 331 */         PropertyEditor editor = getCustomEditor(propertyPath, requiredType);
/* 332 */         if (editor == null) {
/* 333 */           List<String> strippedPaths = new ArrayList<>();
/* 334 */           addStrippedPropertyPaths(strippedPaths, "", propertyPath);
/* 335 */           for (Iterator<String> it = strippedPaths.iterator(); it.hasNext() && editor == null; ) {
/* 336 */             String strippedPath = it.next();
/* 337 */             editor = getCustomEditor(strippedPath, requiredType);
/*     */           } 
/*     */         } 
/* 340 */         if (editor != null) {
/* 341 */           return editor;
/*     */         }
/*     */       } 
/* 344 */       if (requiredType == null) {
/* 345 */         requiredTypeToUse = getPropertyType(propertyPath);
/*     */       }
/*     */     } 
/*     */     
/* 349 */     return getCustomEditor(requiredTypeToUse);
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
/*     */   public boolean hasCustomEditorForElement(@Nullable Class<?> elementType, @Nullable String propertyPath) {
/* 362 */     if (propertyPath != null && this.customEditorsForPath != null) {
/* 363 */       for (Map.Entry<String, CustomEditorHolder> entry : this.customEditorsForPath.entrySet()) {
/* 364 */         if (PropertyAccessorUtils.matchesProperty(entry.getKey(), propertyPath) && ((CustomEditorHolder)entry
/* 365 */           .getValue()).getPropertyEditor(elementType) != null) {
/* 366 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 371 */     return (elementType != null && this.customEditors != null && this.customEditors.containsKey(elementType));
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
/*     */   @Nullable
/*     */   protected Class<?> getPropertyType(String propertyPath) {
/* 387 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private PropertyEditor getCustomEditor(String propertyName, @Nullable Class<?> requiredType) {
/* 399 */     CustomEditorHolder holder = (this.customEditorsForPath != null) ? this.customEditorsForPath.get(propertyName) : null;
/* 400 */     return (holder != null) ? holder.getPropertyEditor(requiredType) : null;
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
/*     */   private PropertyEditor getCustomEditor(@Nullable Class<?> requiredType) {
/* 413 */     if (requiredType == null || this.customEditors == null) {
/* 414 */       return null;
/*     */     }
/*     */     
/* 417 */     PropertyEditor editor = this.customEditors.get(requiredType);
/* 418 */     if (editor == null) {
/*     */       
/* 420 */       if (this.customEditorCache != null) {
/* 421 */         editor = this.customEditorCache.get(requiredType);
/*     */       }
/* 423 */       if (editor == null)
/*     */       {
/* 425 */         for (Map.Entry<Class<?>, PropertyEditor> entry : this.customEditors.entrySet()) {
/* 426 */           if (editor != null) {
/*     */             break;
/*     */           }
/* 429 */           Class<?> key = entry.getKey();
/* 430 */           if (key.isAssignableFrom(requiredType)) {
/* 431 */             editor = entry.getValue();
/*     */ 
/*     */             
/* 434 */             if (this.customEditorCache == null) {
/* 435 */               this.customEditorCache = new HashMap<>();
/*     */             }
/* 437 */             this.customEditorCache.put(requiredType, editor);
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/* 442 */     return editor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected Class<?> guessPropertyTypeFromEditors(String propertyName) {
/* 453 */     if (this.customEditorsForPath != null) {
/* 454 */       CustomEditorHolder editorHolder = this.customEditorsForPath.get(propertyName);
/* 455 */       if (editorHolder == null) {
/* 456 */         List<String> strippedPaths = new ArrayList<>();
/* 457 */         addStrippedPropertyPaths(strippedPaths, "", propertyName);
/* 458 */         for (Iterator<String> it = strippedPaths.iterator(); it.hasNext() && editorHolder == null; ) {
/* 459 */           String strippedName = it.next();
/* 460 */           editorHolder = this.customEditorsForPath.get(strippedName);
/*     */         } 
/*     */       } 
/* 463 */       if (editorHolder != null) {
/* 464 */         return editorHolder.getRegisteredType();
/*     */       }
/*     */     } 
/* 467 */     return null;
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
/*     */   protected void copyCustomEditorsTo(PropertyEditorRegistry target, @Nullable String nestedProperty) {
/* 479 */     String actualPropertyName = (nestedProperty != null) ? PropertyAccessorUtils.getPropertyName(nestedProperty) : null;
/* 480 */     if (this.customEditors != null) {
/* 481 */       this.customEditors.forEach(target::registerCustomEditor);
/*     */     }
/* 483 */     if (this.customEditorsForPath != null) {
/* 484 */       this.customEditorsForPath.forEach((editorPath, editorHolder) -> {
/*     */             if (nestedProperty != null) {
/*     */               int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(editorPath);
/*     */               if (pos != -1) {
/*     */                 String editorNestedProperty = editorPath.substring(0, pos);
/*     */                 String editorNestedPath = editorPath.substring(pos + 1);
/*     */                 if (editorNestedProperty.equals(nestedProperty) || editorNestedProperty.equals(actualPropertyName)) {
/*     */                   target.registerCustomEditor(editorHolder.getRegisteredType(), editorNestedPath, editorHolder.getPropertyEditor());
/*     */                 }
/*     */               } 
/*     */             } else {
/*     */               target.registerCustomEditor(editorHolder.getRegisteredType(), editorPath, editorHolder.getPropertyEditor());
/*     */             } 
/*     */           });
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
/*     */   private void addStrippedPropertyPaths(List<String> strippedPaths, String nestedPath, String propertyPath) {
/* 513 */     int startIndex = propertyPath.indexOf('[');
/* 514 */     if (startIndex != -1) {
/* 515 */       int endIndex = propertyPath.indexOf(']');
/* 516 */       if (endIndex != -1) {
/* 517 */         String prefix = propertyPath.substring(0, startIndex);
/* 518 */         String key = propertyPath.substring(startIndex, endIndex + 1);
/* 519 */         String suffix = propertyPath.substring(endIndex + 1);
/*     */         
/* 521 */         strippedPaths.add(nestedPath + prefix + suffix);
/*     */         
/* 523 */         addStrippedPropertyPaths(strippedPaths, nestedPath + prefix, suffix);
/*     */         
/* 525 */         addStrippedPropertyPaths(strippedPaths, nestedPath + prefix + key, suffix);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class CustomEditorHolder
/*     */   {
/*     */     private final PropertyEditor propertyEditor;
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private final Class<?> registeredType;
/*     */ 
/*     */ 
/*     */     
/*     */     private CustomEditorHolder(PropertyEditor propertyEditor, @Nullable Class<?> registeredType) {
/* 543 */       this.propertyEditor = propertyEditor;
/* 544 */       this.registeredType = registeredType;
/*     */     }
/*     */     
/*     */     private PropertyEditor getPropertyEditor() {
/* 548 */       return this.propertyEditor;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     private Class<?> getRegisteredType() {
/* 553 */       return this.registeredType;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private PropertyEditor getPropertyEditor(@Nullable Class<?> requiredType) {
/* 564 */       if (this.registeredType == null || (requiredType != null && (
/*     */         
/* 566 */         ClassUtils.isAssignable(this.registeredType, requiredType) || 
/* 567 */         ClassUtils.isAssignable(requiredType, this.registeredType))) || (requiredType == null && 
/*     */         
/* 569 */         !Collection.class.isAssignableFrom(this.registeredType) && !this.registeredType.isArray())) {
/* 570 */         return this.propertyEditor;
/*     */       }
/*     */       
/* 573 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/PropertyEditorRegistrySupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */