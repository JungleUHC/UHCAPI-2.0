/*     */ package com.fasterxml.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.PropertyName;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
/*     */ import com.fasterxml.jackson.databind.util.NameTransformer;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BeanPropertyMap
/*     */   implements Iterable<SettableBeanProperty>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 2L;
/*     */   protected final boolean _caseInsensitive;
/*     */   private int _hashMask;
/*     */   private int _size;
/*     */   private int _spillCount;
/*     */   private Object[] _hashArea;
/*     */   private final SettableBeanProperty[] _propsInOrder;
/*     */   private final Map<String, List<PropertyName>> _aliasDefs;
/*     */   private final Map<String, String> _aliasMapping;
/*     */   private final Locale _locale;
/*     */   
/*     */   public BeanPropertyMap(boolean caseInsensitive, Collection<SettableBeanProperty> props, Map<String, List<PropertyName>> aliasDefs, Locale locale) {
/*  99 */     this._caseInsensitive = caseInsensitive;
/* 100 */     this._propsInOrder = props.<SettableBeanProperty>toArray(new SettableBeanProperty[props.size()]);
/* 101 */     this._aliasDefs = aliasDefs;
/* 102 */     this._locale = locale;
/* 103 */     this._aliasMapping = _buildAliasMapping(aliasDefs, caseInsensitive, locale);
/* 104 */     init(props);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public BeanPropertyMap(boolean caseInsensitive, Collection<SettableBeanProperty> props, Map<String, List<PropertyName>> aliasDefs) {
/* 114 */     this(caseInsensitive, props, aliasDefs, Locale.getDefault());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private BeanPropertyMap(BeanPropertyMap src, SettableBeanProperty newProp, int hashIndex, int orderedIndex) {
/* 125 */     this._caseInsensitive = src._caseInsensitive;
/* 126 */     this._locale = src._locale;
/* 127 */     this._hashMask = src._hashMask;
/* 128 */     this._size = src._size;
/* 129 */     this._spillCount = src._spillCount;
/* 130 */     this._aliasDefs = src._aliasDefs;
/* 131 */     this._aliasMapping = src._aliasMapping;
/*     */ 
/*     */     
/* 134 */     this._hashArea = Arrays.copyOf(src._hashArea, src._hashArea.length);
/* 135 */     this._propsInOrder = Arrays.<SettableBeanProperty>copyOf(src._propsInOrder, src._propsInOrder.length);
/* 136 */     this._hashArea[hashIndex] = newProp;
/* 137 */     this._propsInOrder[orderedIndex] = newProp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private BeanPropertyMap(BeanPropertyMap src, SettableBeanProperty newProp, String key, int slot) {
/* 148 */     this._caseInsensitive = src._caseInsensitive;
/* 149 */     this._locale = src._locale;
/* 150 */     this._hashMask = src._hashMask;
/* 151 */     this._size = src._size;
/* 152 */     this._spillCount = src._spillCount;
/* 153 */     this._aliasDefs = src._aliasDefs;
/* 154 */     this._aliasMapping = src._aliasMapping;
/*     */ 
/*     */     
/* 157 */     this._hashArea = Arrays.copyOf(src._hashArea, src._hashArea.length);
/* 158 */     int last = src._propsInOrder.length;
/*     */     
/* 160 */     this._propsInOrder = Arrays.<SettableBeanProperty>copyOf(src._propsInOrder, last + 1);
/* 161 */     this._propsInOrder[last] = newProp;
/*     */     
/* 163 */     int hashSize = this._hashMask + 1;
/* 164 */     int ix = slot << 1;
/*     */ 
/*     */     
/* 167 */     if (this._hashArea[ix] != null) {
/*     */       
/* 169 */       ix = hashSize + (slot >> 1) << 1;
/* 170 */       if (this._hashArea[ix] != null) {
/*     */         
/* 172 */         ix = (hashSize + (hashSize >> 1) << 1) + this._spillCount;
/* 173 */         this._spillCount += 2;
/* 174 */         if (ix >= this._hashArea.length) {
/* 175 */           this._hashArea = Arrays.copyOf(this._hashArea, this._hashArea.length + 4);
/*     */         }
/*     */       } 
/*     */     } 
/* 179 */     this._hashArea[ix] = key;
/* 180 */     this._hashArea[ix + 1] = newProp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanPropertyMap(BeanPropertyMap base, boolean caseInsensitive) {
/* 188 */     this._caseInsensitive = caseInsensitive;
/* 189 */     this._locale = base._locale;
/* 190 */     this._aliasDefs = base._aliasDefs;
/* 191 */     this._aliasMapping = base._aliasMapping;
/*     */ 
/*     */     
/* 194 */     this._propsInOrder = Arrays.<SettableBeanProperty>copyOf(base._propsInOrder, base._propsInOrder.length);
/* 195 */     init(Arrays.asList(this._propsInOrder));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanPropertyMap withCaseInsensitivity(boolean state) {
/* 206 */     if (this._caseInsensitive == state) {
/* 207 */       return this;
/*     */     }
/* 209 */     return new BeanPropertyMap(this, state);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void init(Collection<SettableBeanProperty> props) {
/* 214 */     this._size = props.size();
/*     */ 
/*     */     
/* 217 */     int hashSize = findSize(this._size);
/* 218 */     this._hashMask = hashSize - 1;
/*     */ 
/*     */     
/* 221 */     int alloc = (hashSize + (hashSize >> 1)) * 2;
/* 222 */     Object[] hashed = new Object[alloc];
/* 223 */     int spillCount = 0;
/*     */     
/* 225 */     for (SettableBeanProperty prop : props) {
/*     */       
/* 227 */       if (prop == null) {
/*     */         continue;
/*     */       }
/*     */       
/* 231 */       String key = getPropertyName(prop);
/* 232 */       int slot = _hashCode(key);
/* 233 */       int ix = slot << 1;
/*     */ 
/*     */       
/* 236 */       if (hashed[ix] != null) {
/*     */         
/* 238 */         ix = hashSize + (slot >> 1) << 1;
/* 239 */         if (hashed[ix] != null) {
/*     */           
/* 241 */           ix = (hashSize + (hashSize >> 1) << 1) + spillCount;
/* 242 */           spillCount += 2;
/* 243 */           if (ix >= hashed.length) {
/* 244 */             hashed = Arrays.copyOf(hashed, hashed.length + 4);
/*     */           }
/*     */         } 
/*     */       } 
/* 248 */       hashed[ix] = key;
/* 249 */       hashed[ix + 1] = prop;
/*     */     } 
/*     */ 
/*     */     
/* 253 */     this._hashArea = hashed;
/* 254 */     this._spillCount = spillCount;
/*     */   }
/*     */ 
/*     */   
/*     */   private static final int findSize(int size) {
/* 259 */     if (size <= 5) {
/* 260 */       return 8;
/*     */     }
/* 262 */     if (size <= 12) {
/* 263 */       return 16;
/*     */     }
/* 265 */     int needed = size + (size >> 2);
/* 266 */     int result = 32;
/* 267 */     while (result < needed) {
/* 268 */       result += result;
/*     */     }
/* 270 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BeanPropertyMap construct(MapperConfig<?> config, Collection<SettableBeanProperty> props, Map<String, List<PropertyName>> aliasMapping, boolean caseInsensitive) {
/* 280 */     return new BeanPropertyMap(caseInsensitive, props, aliasMapping, config
/*     */         
/* 282 */         .getLocale());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static BeanPropertyMap construct(MapperConfig<?> config, Collection<SettableBeanProperty> props, Map<String, List<PropertyName>> aliasMapping) {
/* 293 */     return new BeanPropertyMap(config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES), props, aliasMapping, config
/*     */         
/* 295 */         .getLocale());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static BeanPropertyMap construct(Collection<SettableBeanProperty> props, boolean caseInsensitive, Map<String, List<PropertyName>> aliasMapping) {
/* 304 */     return new BeanPropertyMap(caseInsensitive, props, aliasMapping);
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
/*     */   public BeanPropertyMap withProperty(SettableBeanProperty newProp) {
/* 317 */     String key = getPropertyName(newProp);
/*     */     
/* 319 */     for (int i = 1, end = this._hashArea.length; i < end; i += 2) {
/* 320 */       SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
/* 321 */       if (prop != null && prop.getName().equals(key)) {
/* 322 */         return new BeanPropertyMap(this, newProp, i, _findFromOrdered(prop));
/*     */       }
/*     */     } 
/*     */     
/* 326 */     int slot = _hashCode(key);
/*     */     
/* 328 */     return new BeanPropertyMap(this, newProp, key, slot);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanPropertyMap assignIndexes() {
/* 334 */     int index = 0;
/* 335 */     for (int i = 1, end = this._hashArea.length; i < end; i += 2) {
/* 336 */       SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
/* 337 */       if (prop != null) {
/* 338 */         prop.assignIndex(index++);
/*     */       }
/*     */     } 
/* 341 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanPropertyMap renameAll(NameTransformer transformer) {
/* 350 */     if (transformer == null || transformer == NameTransformer.NOP) {
/* 351 */       return this;
/*     */     }
/*     */     
/* 354 */     int len = this._propsInOrder.length;
/* 355 */     ArrayList<SettableBeanProperty> newProps = new ArrayList<>(len);
/*     */     
/* 357 */     for (int i = 0; i < len; i++) {
/* 358 */       SettableBeanProperty prop = this._propsInOrder[i];
/*     */ 
/*     */       
/* 361 */       if (prop == null) {
/* 362 */         newProps.add(prop);
/*     */       } else {
/*     */         
/* 365 */         newProps.add(_rename(prop, transformer));
/*     */       } 
/*     */     } 
/*     */     
/* 369 */     return new BeanPropertyMap(this._caseInsensitive, newProps, this._aliasDefs, this._locale);
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
/*     */   public BeanPropertyMap withoutProperties(Collection<String> toExclude) {
/* 387 */     return withoutProperties(toExclude, null);
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
/*     */   public BeanPropertyMap withoutProperties(Collection<String> toExclude, Collection<String> toInclude) {
/* 400 */     if ((toExclude == null || toExclude.isEmpty()) && toInclude == null) {
/* 401 */       return this;
/*     */     }
/* 403 */     int len = this._propsInOrder.length;
/* 404 */     ArrayList<SettableBeanProperty> newProps = new ArrayList<>(len);
/*     */     
/* 406 */     for (int i = 0; i < len; i++) {
/* 407 */       SettableBeanProperty prop = this._propsInOrder[i];
/*     */ 
/*     */ 
/*     */       
/* 411 */       if (prop != null && 
/* 412 */         !IgnorePropertiesUtil.shouldIgnore(prop.getName(), toExclude, toInclude)) {
/* 413 */         newProps.add(prop);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 418 */     return new BeanPropertyMap(this._caseInsensitive, newProps, this._aliasDefs, this._locale);
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
/*     */   public void replace(SettableBeanProperty origProp, SettableBeanProperty newProp) {
/* 430 */     int i = 1;
/* 431 */     int end = this._hashArea.length;
/*     */     
/* 433 */     for (;; i += 2) {
/* 434 */       if (i >= end) {
/* 435 */         throw new NoSuchElementException("No entry '" + origProp.getName() + "' found, can't replace");
/*     */       }
/* 437 */       if (this._hashArea[i] == origProp) {
/* 438 */         this._hashArea[i] = newProp;
/*     */         break;
/*     */       } 
/*     */     } 
/* 442 */     this._propsInOrder[_findFromOrdered(origProp)] = newProp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(SettableBeanProperty propToRm) {
/* 451 */     ArrayList<SettableBeanProperty> props = new ArrayList<>(this._size);
/* 452 */     String key = getPropertyName(propToRm);
/* 453 */     boolean found = false;
/*     */     
/* 455 */     for (int i = 1, end = this._hashArea.length; i < end; i += 2) {
/* 456 */       SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
/* 457 */       if (prop == null) {
/*     */         continue;
/*     */       }
/* 460 */       if (!found) {
/*     */ 
/*     */         
/* 463 */         found = key.equals(this._hashArea[i - 1]);
/* 464 */         if (found) {
/*     */           
/* 466 */           this._propsInOrder[_findFromOrdered(prop)] = null;
/*     */           continue;
/*     */         } 
/*     */       } 
/* 470 */       props.add(prop); continue;
/*     */     } 
/* 472 */     if (!found) {
/* 473 */       throw new NoSuchElementException("No entry '" + propToRm.getName() + "' found, can't remove");
/*     */     }
/* 475 */     init(props);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 484 */     return this._size;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCaseInsensitive() {
/* 490 */     return this._caseInsensitive;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasAliases() {
/* 497 */     return !this._aliasDefs.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<SettableBeanProperty> iterator() {
/* 505 */     return _properties().iterator();
/*     */   }
/*     */   
/*     */   private List<SettableBeanProperty> _properties() {
/* 509 */     ArrayList<SettableBeanProperty> p = new ArrayList<>(this._size);
/* 510 */     for (int i = 1, end = this._hashArea.length; i < end; i += 2) {
/* 511 */       SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
/* 512 */       if (prop != null) {
/* 513 */         p.add(prop);
/*     */       }
/*     */     } 
/* 516 */     return p;
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
/*     */   public SettableBeanProperty[] getPropertiesInInsertionOrder() {
/* 528 */     return this._propsInOrder;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String getPropertyName(SettableBeanProperty prop) {
/* 534 */     return this._caseInsensitive ? prop.getName().toLowerCase(this._locale) : prop.getName();
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
/*     */   public SettableBeanProperty find(int index) {
/* 550 */     for (int i = 1, end = this._hashArea.length; i < end; i += 2) {
/* 551 */       SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
/* 552 */       if (prop != null && index == prop.getPropertyIndex()) {
/* 553 */         return prop;
/*     */       }
/*     */     } 
/* 556 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public SettableBeanProperty find(String key) {
/* 561 */     if (key == null) {
/* 562 */       throw new IllegalArgumentException("Cannot pass null property name");
/*     */     }
/* 564 */     if (this._caseInsensitive) {
/* 565 */       key = key.toLowerCase(this._locale);
/*     */     }
/*     */ 
/*     */     
/* 569 */     int slot = key.hashCode() & this._hashMask;
/*     */ 
/*     */ 
/*     */     
/* 573 */     int ix = slot << 1;
/* 574 */     Object match = this._hashArea[ix];
/* 575 */     if (match == key || key.equals(match)) {
/* 576 */       return (SettableBeanProperty)this._hashArea[ix + 1];
/*     */     }
/* 578 */     return _find2(key, slot, match);
/*     */   }
/*     */ 
/*     */   
/*     */   private final SettableBeanProperty _find2(String key, int slot, Object match) {
/* 583 */     if (match == null)
/*     */     {
/* 585 */       return _findWithAlias(this._aliasMapping.get(key));
/*     */     }
/*     */     
/* 588 */     int hashSize = this._hashMask + 1;
/* 589 */     int ix = hashSize + (slot >> 1) << 1;
/* 590 */     match = this._hashArea[ix];
/* 591 */     if (key.equals(match)) {
/* 592 */       return (SettableBeanProperty)this._hashArea[ix + 1];
/*     */     }
/* 594 */     if (match != null) {
/* 595 */       int i = hashSize + (hashSize >> 1) << 1;
/* 596 */       for (int end = i + this._spillCount; i < end; i += 2) {
/* 597 */         match = this._hashArea[i];
/* 598 */         if (match == key || key.equals(match)) {
/* 599 */           return (SettableBeanProperty)this._hashArea[i + 1];
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 604 */     return _findWithAlias(this._aliasMapping.get(key));
/*     */   }
/*     */ 
/*     */   
/*     */   private SettableBeanProperty _findWithAlias(String keyFromAlias) {
/* 609 */     if (keyFromAlias == null) {
/* 610 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 614 */     int slot = _hashCode(keyFromAlias);
/* 615 */     int ix = slot << 1;
/* 616 */     Object match = this._hashArea[ix];
/* 617 */     if (keyFromAlias.equals(match)) {
/* 618 */       return (SettableBeanProperty)this._hashArea[ix + 1];
/*     */     }
/* 620 */     if (match == null) {
/* 621 */       return null;
/*     */     }
/* 623 */     return _find2ViaAlias(keyFromAlias, slot, match);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private SettableBeanProperty _find2ViaAlias(String key, int slot, Object match) {
/* 629 */     int hashSize = this._hashMask + 1;
/* 630 */     int ix = hashSize + (slot >> 1) << 1;
/* 631 */     match = this._hashArea[ix];
/* 632 */     if (key.equals(match)) {
/* 633 */       return (SettableBeanProperty)this._hashArea[ix + 1];
/*     */     }
/* 635 */     if (match != null) {
/* 636 */       int i = hashSize + (hashSize >> 1) << 1;
/* 637 */       for (int end = i + this._spillCount; i < end; i += 2) {
/* 638 */         match = this._hashArea[i];
/* 639 */         if (match == key || key.equals(match)) {
/* 640 */           return (SettableBeanProperty)this._hashArea[i + 1];
/*     */         }
/*     */       } 
/*     */     } 
/* 644 */     return null;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean findDeserializeAndSet(JsonParser p, DeserializationContext ctxt, Object bean, String key) throws IOException {
/* 665 */     SettableBeanProperty prop = find(key);
/* 666 */     if (prop == null) {
/* 667 */       return false;
/*     */     }
/*     */     try {
/* 670 */       prop.deserializeAndSet(p, ctxt, bean);
/* 671 */     } catch (Exception e) {
/* 672 */       wrapAndThrow(e, bean, key, ctxt);
/*     */     } 
/* 674 */     return true;
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
/*     */   public String toString() {
/* 686 */     StringBuilder sb = new StringBuilder();
/* 687 */     sb.append("Properties=[");
/* 688 */     int count = 0;
/*     */     
/* 690 */     Iterator<SettableBeanProperty> it = iterator();
/* 691 */     while (it.hasNext()) {
/* 692 */       SettableBeanProperty prop = it.next();
/* 693 */       if (count++ > 0) {
/* 694 */         sb.append(", ");
/*     */       }
/* 696 */       sb.append(prop.getName());
/* 697 */       sb.append('(');
/* 698 */       sb.append(prop.getType());
/* 699 */       sb.append(')');
/*     */     } 
/* 701 */     sb.append(']');
/* 702 */     if (!this._aliasDefs.isEmpty()) {
/* 703 */       sb.append("(aliases: ");
/* 704 */       sb.append(this._aliasDefs);
/* 705 */       sb.append(")");
/*     */     } 
/* 707 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SettableBeanProperty _rename(SettableBeanProperty prop, NameTransformer xf) {
/* 718 */     if (prop == null) {
/* 719 */       return prop;
/*     */     }
/* 721 */     String newName = xf.transform(prop.getName());
/* 722 */     prop = prop.withSimpleName(newName);
/* 723 */     JsonDeserializer<?> deser = prop.getValueDeserializer();
/* 724 */     if (deser != null) {
/*     */ 
/*     */       
/* 727 */       JsonDeserializer<Object> newDeser = deser.unwrappingDeserializer(xf);
/* 728 */       if (newDeser != deser) {
/* 729 */         prop = prop.withValueDeserializer(newDeser);
/*     */       }
/*     */     } 
/* 732 */     return prop;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt) throws IOException {
/* 739 */     while (t instanceof java.lang.reflect.InvocationTargetException && t.getCause() != null) {
/* 740 */       t = t.getCause();
/*     */     }
/*     */     
/* 743 */     ClassUtil.throwIfError(t);
/*     */     
/* 745 */     boolean wrap = (ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
/*     */     
/* 747 */     if (t instanceof IOException) {
/* 748 */       if (!wrap || !(t instanceof com.fasterxml.jackson.core.JacksonException)) {
/* 749 */         throw (IOException)t;
/*     */       }
/* 751 */     } else if (!wrap) {
/* 752 */       ClassUtil.throwIfRTE(t);
/*     */     } 
/* 754 */     throw JsonMappingException.wrapWithPath(t, bean, fieldName);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int _findFromOrdered(SettableBeanProperty prop) {
/* 793 */     for (int i = 0, end = this._propsInOrder.length; i < end; i++) {
/* 794 */       if (this._propsInOrder[i] == prop) {
/* 795 */         return i;
/*     */       }
/*     */     } 
/* 798 */     throw new IllegalStateException("Illegal state: property '" + prop.getName() + "' missing from _propsInOrder");
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
/*     */   private final int _hashCode(String key) {
/* 812 */     return key.hashCode() & this._hashMask;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Map<String, String> _buildAliasMapping(Map<String, List<PropertyName>> defs, boolean caseInsensitive, Locale loc) {
/* 819 */     if (defs == null || defs.isEmpty()) {
/* 820 */       return Collections.emptyMap();
/*     */     }
/* 822 */     Map<String, String> aliases = new HashMap<>();
/* 823 */     for (Map.Entry<String, List<PropertyName>> entry : defs.entrySet()) {
/* 824 */       String key = entry.getKey();
/* 825 */       if (caseInsensitive) {
/* 826 */         key = key.toLowerCase(loc);
/*     */       }
/* 828 */       for (PropertyName pn : entry.getValue()) {
/* 829 */         String mapped = pn.getSimpleName();
/* 830 */         if (caseInsensitive) {
/* 831 */           mapped = mapped.toLowerCase(loc);
/*     */         }
/* 833 */         aliases.put(mapped, key);
/*     */       } 
/*     */     } 
/* 836 */     return aliases;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/impl/BeanPropertyMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */