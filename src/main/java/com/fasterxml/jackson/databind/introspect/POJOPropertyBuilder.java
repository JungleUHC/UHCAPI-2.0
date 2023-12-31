/*      */ package com.fasterxml.jackson.databind.introspect;
/*      */ 
/*      */ import com.fasterxml.jackson.annotation.JsonInclude;
/*      */ import com.fasterxml.jackson.annotation.JsonProperty;
/*      */ import com.fasterxml.jackson.annotation.JsonSetter;
/*      */ import com.fasterxml.jackson.annotation.Nulls;
/*      */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*      */ import com.fasterxml.jackson.databind.JavaType;
/*      */ import com.fasterxml.jackson.databind.PropertyMetadata;
/*      */ import com.fasterxml.jackson.databind.PropertyName;
/*      */ import com.fasterxml.jackson.databind.cfg.ConfigOverride;
/*      */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*      */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*      */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Set;
/*      */ import java.util.stream.Collectors;
/*      */ 
/*      */ 
/*      */ public class POJOPropertyBuilder
/*      */   extends BeanPropertyDefinition
/*      */   implements Comparable<POJOPropertyBuilder>
/*      */ {
/*   32 */   private static final AnnotationIntrospector.ReferenceProperty NOT_REFEFERENCE_PROP = AnnotationIntrospector.ReferenceProperty.managed("");
/*      */ 
/*      */ 
/*      */   
/*      */   protected final boolean _forSerialization;
/*      */ 
/*      */ 
/*      */   
/*      */   protected final MapperConfig<?> _config;
/*      */ 
/*      */ 
/*      */   
/*      */   protected final AnnotationIntrospector _annotationIntrospector;
/*      */ 
/*      */ 
/*      */   
/*      */   protected final PropertyName _name;
/*      */ 
/*      */ 
/*      */   
/*      */   protected final PropertyName _internalName;
/*      */ 
/*      */ 
/*      */   
/*      */   protected Linked<AnnotatedField> _fields;
/*      */ 
/*      */ 
/*      */   
/*      */   protected Linked<AnnotatedParameter> _ctorParameters;
/*      */ 
/*      */   
/*      */   protected Linked<AnnotatedMethod> _getters;
/*      */ 
/*      */   
/*      */   protected Linked<AnnotatedMethod> _setters;
/*      */ 
/*      */   
/*      */   protected transient PropertyMetadata _metadata;
/*      */ 
/*      */   
/*      */   protected transient AnnotationIntrospector.ReferenceProperty _referenceInfo;
/*      */ 
/*      */ 
/*      */   
/*      */   public POJOPropertyBuilder(MapperConfig<?> config, AnnotationIntrospector ai, boolean forSerialization, PropertyName internalName) {
/*   77 */     this(config, ai, forSerialization, internalName, internalName);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected POJOPropertyBuilder(MapperConfig<?> config, AnnotationIntrospector ai, boolean forSerialization, PropertyName internalName, PropertyName name) {
/*   83 */     this._config = config;
/*   84 */     this._annotationIntrospector = ai;
/*   85 */     this._internalName = internalName;
/*   86 */     this._name = name;
/*   87 */     this._forSerialization = forSerialization;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected POJOPropertyBuilder(POJOPropertyBuilder src, PropertyName newName) {
/*   93 */     this._config = src._config;
/*   94 */     this._annotationIntrospector = src._annotationIntrospector;
/*   95 */     this._internalName = src._internalName;
/*   96 */     this._name = newName;
/*   97 */     this._fields = src._fields;
/*   98 */     this._ctorParameters = src._ctorParameters;
/*   99 */     this._getters = src._getters;
/*  100 */     this._setters = src._setters;
/*  101 */     this._forSerialization = src._forSerialization;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public POJOPropertyBuilder withName(PropertyName newName) {
/*  112 */     return new POJOPropertyBuilder(this, newName);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public POJOPropertyBuilder withSimpleName(String newSimpleName) {
/*  118 */     PropertyName newName = this._name.withSimpleName(newSimpleName);
/*  119 */     return (newName == this._name) ? this : new POJOPropertyBuilder(this, newName);
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
/*      */   public int compareTo(POJOPropertyBuilder other) {
/*  134 */     if (this._ctorParameters != null) {
/*  135 */       if (other._ctorParameters == null) {
/*  136 */         return -1;
/*      */       }
/*  138 */     } else if (other._ctorParameters != null) {
/*  139 */       return 1;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  144 */     return getName().compareTo(other.getName());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getName() {
/*  155 */     return (this._name == null) ? null : this._name.getSimpleName();
/*      */   }
/*      */ 
/*      */   
/*      */   public PropertyName getFullName() {
/*  160 */     return this._name;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasName(PropertyName name) {
/*  165 */     return this._name.equals(name);
/*      */   }
/*      */   
/*      */   public String getInternalName() {
/*  169 */     return this._internalName.getSimpleName();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PropertyName getWrapperName() {
/*  178 */     AnnotatedMember member = getPrimaryMember();
/*  179 */     return (member == null || this._annotationIntrospector == null) ? null : this._annotationIntrospector
/*  180 */       .findWrapperName(member);
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
/*      */   public boolean isExplicitlyIncluded() {
/*  193 */     return (_anyExplicits(this._fields) || 
/*  194 */       _anyExplicits(this._getters) || 
/*  195 */       _anyExplicits(this._setters) || 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  200 */       _anyExplicitNames(this._ctorParameters));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isExplicitlyNamed() {
/*  206 */     return (_anyExplicitNames(this._fields) || 
/*  207 */       _anyExplicitNames(this._getters) || 
/*  208 */       _anyExplicitNames(this._setters) || 
/*  209 */       _anyExplicitNames(this._ctorParameters));
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
/*      */   public PropertyMetadata getMetadata() {
/*  222 */     if (this._metadata == null) {
/*      */ 
/*      */       
/*  225 */       AnnotatedMember prim = getPrimaryMemberUnchecked();
/*      */       
/*  227 */       if (prim == null) {
/*  228 */         this._metadata = PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
/*      */       } else {
/*  230 */         Boolean b = this._annotationIntrospector.hasRequiredMarker(prim);
/*  231 */         String desc = this._annotationIntrospector.findPropertyDescription(prim);
/*  232 */         Integer idx = this._annotationIntrospector.findPropertyIndex(prim);
/*  233 */         String def = this._annotationIntrospector.findPropertyDefaultValue(prim);
/*      */         
/*  235 */         if (b == null && idx == null && def == null) {
/*  236 */           this
/*  237 */             ._metadata = (desc == null) ? PropertyMetadata.STD_REQUIRED_OR_OPTIONAL : PropertyMetadata.STD_REQUIRED_OR_OPTIONAL.withDescription(desc);
/*      */         } else {
/*  239 */           this._metadata = PropertyMetadata.construct(b, desc, idx, def);
/*      */         } 
/*  241 */         if (!this._forSerialization) {
/*  242 */           this._metadata = _getSetterInfo(this._metadata, prim);
/*      */         }
/*      */       } 
/*      */     } 
/*  246 */     return this._metadata;
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
/*      */   protected PropertyMetadata _getSetterInfo(PropertyMetadata metadata, AnnotatedMember primary) {
/*  258 */     boolean needMerge = true;
/*  259 */     Nulls valueNulls = null;
/*  260 */     Nulls contentNulls = null;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  265 */     AnnotatedMember acc = getAccessor();
/*      */     
/*  267 */     if (primary != null) {
/*      */       
/*  269 */       if (this._annotationIntrospector != null) {
/*  270 */         if (acc != null) {
/*  271 */           Boolean b = this._annotationIntrospector.findMergeInfo(primary);
/*  272 */           if (b != null) {
/*  273 */             needMerge = false;
/*  274 */             if (b.booleanValue()) {
/*  275 */               metadata = metadata.withMergeInfo(PropertyMetadata.MergeInfo.createForPropertyOverride(acc));
/*      */             }
/*      */           } 
/*      */         } 
/*  279 */         JsonSetter.Value setterInfo = this._annotationIntrospector.findSetterInfo(primary);
/*  280 */         if (setterInfo != null) {
/*  281 */           valueNulls = setterInfo.nonDefaultValueNulls();
/*  282 */           contentNulls = setterInfo.nonDefaultContentNulls();
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  287 */       if (needMerge || valueNulls == null || contentNulls == null) {
/*      */ 
/*      */ 
/*      */         
/*  291 */         Class<?> rawType = _rawTypeOf(primary);
/*  292 */         ConfigOverride co = this._config.getConfigOverride(rawType);
/*  293 */         JsonSetter.Value setterInfo = co.getSetterInfo();
/*  294 */         if (setterInfo != null) {
/*  295 */           if (valueNulls == null) {
/*  296 */             valueNulls = setterInfo.nonDefaultValueNulls();
/*      */           }
/*  298 */           if (contentNulls == null) {
/*  299 */             contentNulls = setterInfo.nonDefaultContentNulls();
/*      */           }
/*      */         } 
/*  302 */         if (needMerge && acc != null) {
/*  303 */           Boolean b = co.getMergeable();
/*  304 */           if (b != null) {
/*  305 */             needMerge = false;
/*  306 */             if (b.booleanValue()) {
/*  307 */               metadata = metadata.withMergeInfo(PropertyMetadata.MergeInfo.createForTypeOverride(acc));
/*      */             }
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*  313 */     if (needMerge || valueNulls == null || contentNulls == null) {
/*  314 */       JsonSetter.Value setterInfo = this._config.getDefaultSetterInfo();
/*  315 */       if (valueNulls == null) {
/*  316 */         valueNulls = setterInfo.nonDefaultValueNulls();
/*      */       }
/*  318 */       if (contentNulls == null) {
/*  319 */         contentNulls = setterInfo.nonDefaultContentNulls();
/*      */       }
/*  321 */       if (needMerge) {
/*  322 */         Boolean b = this._config.getDefaultMergeable();
/*  323 */         if (Boolean.TRUE.equals(b) && acc != null) {
/*  324 */           metadata = metadata.withMergeInfo(PropertyMetadata.MergeInfo.createForDefaults(acc));
/*      */         }
/*      */       } 
/*      */     } 
/*  328 */     if (valueNulls != null || contentNulls != null) {
/*  329 */       metadata = metadata.withNulls(valueNulls, contentNulls);
/*      */     }
/*  331 */     return metadata;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType getPrimaryType() {
/*  341 */     if (this._forSerialization) {
/*  342 */       AnnotatedMember annotatedMember = getGetter();
/*  343 */       if (annotatedMember == null) {
/*  344 */         annotatedMember = getField();
/*  345 */         if (annotatedMember == null)
/*      */         {
/*  347 */           return TypeFactory.unknownType();
/*      */         }
/*      */       } 
/*  350 */       return annotatedMember.getType();
/*      */     } 
/*  352 */     AnnotatedMember m = getConstructorParameter();
/*  353 */     if (m == null) {
/*  354 */       m = getSetter();
/*      */ 
/*      */       
/*  357 */       if (m != null) {
/*  358 */         return ((AnnotatedMethod)m).getParameterType(0);
/*      */       }
/*  360 */       m = getField();
/*      */     } 
/*      */     
/*  363 */     if (m == null) {
/*  364 */       m = getGetter();
/*  365 */       if (m == null) {
/*  366 */         return TypeFactory.unknownType();
/*      */       }
/*      */     } 
/*  369 */     return m.getType();
/*      */   }
/*      */ 
/*      */   
/*      */   public Class<?> getRawPrimaryType() {
/*  374 */     return getPrimaryType().getRawClass();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasGetter() {
/*  384 */     return (this._getters != null);
/*      */   }
/*      */   public boolean hasSetter() {
/*  387 */     return (this._setters != null);
/*      */   }
/*      */   public boolean hasField() {
/*  390 */     return (this._fields != null);
/*      */   }
/*      */   public boolean hasConstructorParameter() {
/*  393 */     return (this._ctorParameters != null);
/*      */   }
/*      */   
/*      */   public boolean couldDeserialize() {
/*  397 */     return (this._ctorParameters != null || this._setters != null || this._fields != null);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean couldSerialize() {
/*  402 */     return (this._getters != null || this._fields != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AnnotatedMethod getGetter() {
/*  409 */     Linked<AnnotatedMethod> curr = this._getters;
/*  410 */     if (curr == null) {
/*  411 */       return null;
/*      */     }
/*  413 */     Linked<AnnotatedMethod> next = curr.next;
/*  414 */     if (next == null) {
/*  415 */       return (AnnotatedMethod)curr.value;
/*      */     }
/*      */     
/*  418 */     for (; next != null; next = next.next) {
/*      */ 
/*      */ 
/*      */       
/*  422 */       Class<?> currClass = ((AnnotatedMethod)curr.value).getDeclaringClass();
/*  423 */       Class<?> nextClass = ((AnnotatedMethod)next.value).getDeclaringClass();
/*  424 */       if (currClass != nextClass) {
/*  425 */         if (currClass.isAssignableFrom(nextClass)) {
/*  426 */           curr = next;
/*      */           continue;
/*      */         } 
/*  429 */         if (nextClass.isAssignableFrom(currClass)) {
/*      */           continue;
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  439 */       int priNext = _getterPriority((AnnotatedMethod)next.value);
/*  440 */       int priCurr = _getterPriority((AnnotatedMethod)curr.value);
/*      */       
/*  442 */       if (priNext != priCurr) {
/*  443 */         if (priNext < priCurr) {
/*  444 */           curr = next;
/*      */         }
/*      */       } else {
/*      */         
/*  448 */         throw new IllegalArgumentException("Conflicting getter definitions for property \"" + getName() + "\": " + ((AnnotatedMethod)curr.value)
/*  449 */             .getFullName() + " vs " + ((AnnotatedMethod)next.value).getFullName());
/*      */       }  continue;
/*      */     } 
/*  452 */     this._getters = curr.withoutNext();
/*  453 */     return (AnnotatedMethod)curr.value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected AnnotatedMethod getGetterUnchecked() {
/*  462 */     Linked<AnnotatedMethod> curr = this._getters;
/*  463 */     if (curr == null) {
/*  464 */       return null;
/*      */     }
/*  466 */     return (AnnotatedMethod)curr.value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AnnotatedMethod getSetter() {
/*  473 */     Linked<AnnotatedMethod> curr = this._setters;
/*  474 */     if (curr == null) {
/*  475 */       return null;
/*      */     }
/*  477 */     Linked<AnnotatedMethod> next = curr.next;
/*  478 */     if (next == null) {
/*  479 */       return (AnnotatedMethod)curr.value;
/*      */     }
/*      */     
/*  482 */     for (; next != null; next = next.next) {
/*  483 */       AnnotatedMethod selected = _selectSetter((AnnotatedMethod)curr.value, (AnnotatedMethod)next.value);
/*  484 */       if (selected != curr.value)
/*      */       {
/*      */         
/*  487 */         if (selected == next.value) {
/*  488 */           curr = next;
/*      */         }
/*      */         else {
/*      */           
/*  492 */           return _selectSetterFromMultiple(curr, next);
/*      */         } 
/*      */       }
/*      */     } 
/*  496 */     this._setters = curr.withoutNext();
/*  497 */     return (AnnotatedMethod)curr.value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected AnnotatedMethod getSetterUnchecked() {
/*  506 */     Linked<AnnotatedMethod> curr = this._setters;
/*  507 */     if (curr == null) {
/*  508 */       return null;
/*      */     }
/*  510 */     return (AnnotatedMethod)curr.value;
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
/*      */   protected AnnotatedMethod _selectSetterFromMultiple(Linked<AnnotatedMethod> curr, Linked<AnnotatedMethod> next) {
/*  532 */     List<AnnotatedMethod> conflicts = new ArrayList<>();
/*  533 */     conflicts.add((AnnotatedMethod)curr.value);
/*  534 */     conflicts.add((AnnotatedMethod)next.value);
/*      */     
/*  536 */     next = next.next;
/*  537 */     for (; next != null; next = next.next) {
/*  538 */       AnnotatedMethod selected = _selectSetter((AnnotatedMethod)curr.value, (AnnotatedMethod)next.value);
/*  539 */       if (selected != curr.value)
/*      */       {
/*      */ 
/*      */         
/*  543 */         if (selected == next.value) {
/*      */           
/*  545 */           conflicts.clear();
/*  546 */           curr = next;
/*      */         }
/*      */         else {
/*      */           
/*  550 */           conflicts.add((AnnotatedMethod)next.value);
/*      */         } 
/*      */       }
/*      */     } 
/*  554 */     if (conflicts.isEmpty()) {
/*  555 */       this._setters = curr.withoutNext();
/*  556 */       return (AnnotatedMethod)curr.value;
/*      */     } 
/*      */ 
/*      */     
/*  560 */     String desc = conflicts.stream().map(AnnotatedMethod::getFullName).collect(Collectors.joining(" vs "));
/*  561 */     throw new IllegalArgumentException(String.format("Conflicting setter definitions for property \"%s\": %s", new Object[] {
/*      */             
/*  563 */             getName(), desc
/*      */           }));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected AnnotatedMethod _selectSetter(AnnotatedMethod currM, AnnotatedMethod nextM) {
/*  570 */     Class<?> currClass = currM.getDeclaringClass();
/*  571 */     Class<?> nextClass = nextM.getDeclaringClass();
/*  572 */     if (currClass != nextClass) {
/*  573 */       if (currClass.isAssignableFrom(nextClass)) {
/*  574 */         return nextM;
/*      */       }
/*  576 */       if (nextClass.isAssignableFrom(currClass)) {
/*  577 */         return currM;
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  588 */     int priNext = _setterPriority(nextM);
/*  589 */     int priCurr = _setterPriority(currM);
/*      */     
/*  591 */     if (priNext != priCurr) {
/*      */       
/*  593 */       if (priNext < priCurr) {
/*  594 */         return nextM;
/*      */       }
/*      */       
/*  597 */       return currM;
/*      */     } 
/*      */     
/*  600 */     return (this._annotationIntrospector == null) ? null : this._annotationIntrospector
/*  601 */       .resolveSetterConflict(this._config, currM, nextM);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public AnnotatedField getField() {
/*  607 */     if (this._fields == null) {
/*  608 */       return null;
/*      */     }
/*      */     
/*  611 */     AnnotatedField field = (AnnotatedField)this._fields.value;
/*  612 */     Linked<AnnotatedField> next = this._fields.next;
/*  613 */     for (; next != null; next = next.next) {
/*  614 */       AnnotatedField nextField = (AnnotatedField)next.value;
/*  615 */       Class<?> fieldClass = field.getDeclaringClass();
/*  616 */       Class<?> nextClass = nextField.getDeclaringClass();
/*  617 */       if (fieldClass != nextClass) {
/*  618 */         if (fieldClass.isAssignableFrom(nextClass)) {
/*  619 */           field = nextField;
/*      */           continue;
/*      */         } 
/*  622 */         if (nextClass.isAssignableFrom(fieldClass)) {
/*      */           continue;
/*      */         }
/*      */       } 
/*  626 */       throw new IllegalArgumentException("Multiple fields representing property \"" + getName() + "\": " + field
/*  627 */           .getFullName() + " vs " + nextField.getFullName());
/*      */     } 
/*  629 */     return field;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected AnnotatedField getFieldUnchecked() {
/*  638 */     Linked<AnnotatedField> curr = this._fields;
/*  639 */     if (curr == null) {
/*  640 */       return null;
/*      */     }
/*  642 */     return (AnnotatedField)curr.value;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public AnnotatedParameter getConstructorParameter() {
/*  648 */     if (this._ctorParameters == null) {
/*  649 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  659 */     Linked<AnnotatedParameter> curr = this._ctorParameters;
/*      */     while (true) {
/*  661 */       if (((AnnotatedParameter)curr.value).getOwner() instanceof AnnotatedConstructor) {
/*  662 */         return (AnnotatedParameter)curr.value;
/*      */       }
/*  664 */       curr = curr.next;
/*  665 */       if (curr == null)
/*  666 */         return (AnnotatedParameter)this._ctorParameters.value; 
/*      */     } 
/*      */   }
/*      */   
/*      */   public Iterator<AnnotatedParameter> getConstructorParameters() {
/*  671 */     if (this._ctorParameters == null) {
/*  672 */       return ClassUtil.emptyIterator();
/*      */     }
/*  674 */     return new MemberIterator<>(this._ctorParameters);
/*      */   }
/*      */ 
/*      */   
/*      */   public AnnotatedMember getPrimaryMember() {
/*  679 */     if (this._forSerialization) {
/*  680 */       return getAccessor();
/*      */     }
/*  682 */     AnnotatedMember m = getMutator();
/*      */     
/*  684 */     if (m == null) {
/*  685 */       m = getAccessor();
/*      */     }
/*  687 */     return m;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected AnnotatedMember getPrimaryMemberUnchecked() {
/*  695 */     if (this._forSerialization) {
/*      */       
/*  697 */       if (this._getters != null) {
/*  698 */         return (AnnotatedMember)this._getters.value;
/*      */       }
/*      */       
/*  701 */       if (this._fields != null) {
/*  702 */         return (AnnotatedMember)this._fields.value;
/*      */       }
/*  704 */       return null;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  710 */     if (this._ctorParameters != null) {
/*  711 */       return (AnnotatedMember)this._ctorParameters.value;
/*      */     }
/*      */     
/*  714 */     if (this._setters != null) {
/*  715 */       return (AnnotatedMember)this._setters.value;
/*      */     }
/*      */     
/*  718 */     if (this._fields != null) {
/*  719 */       return (AnnotatedMember)this._fields.value;
/*      */     }
/*      */ 
/*      */     
/*  723 */     if (this._getters != null) {
/*  724 */       return (AnnotatedMember)this._getters.value;
/*      */     }
/*  726 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   protected int _getterPriority(AnnotatedMethod m) {
/*  731 */     String name = m.getName();
/*      */     
/*  733 */     if (name.startsWith("get") && name.length() > 3)
/*      */     {
/*  735 */       return 1;
/*      */     }
/*  737 */     if (name.startsWith("is") && name.length() > 2) {
/*  738 */       return 2;
/*      */     }
/*  740 */     return 3;
/*      */   }
/*      */ 
/*      */   
/*      */   protected int _setterPriority(AnnotatedMethod m) {
/*  745 */     String name = m.getName();
/*  746 */     if (name.startsWith("set") && name.length() > 3)
/*      */     {
/*  748 */       return 1;
/*      */     }
/*  750 */     return 2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Class<?>[] findViews() {
/*  761 */     return fromMemberAnnotations((WithMember)new WithMember<Class<?>[]>()
/*      */         {
/*      */           public Class<?>[] withMember(AnnotatedMember member) {
/*  764 */             return POJOPropertyBuilder.this._annotationIntrospector.findViews(member);
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AnnotationIntrospector.ReferenceProperty findReferenceType() {
/*  773 */     AnnotationIntrospector.ReferenceProperty result = this._referenceInfo;
/*  774 */     if (result != null) {
/*  775 */       if (result == NOT_REFEFERENCE_PROP) {
/*  776 */         return null;
/*      */       }
/*  778 */       return result;
/*      */     } 
/*  780 */     result = fromMemberAnnotations(new WithMember<AnnotationIntrospector.ReferenceProperty>()
/*      */         {
/*      */           public AnnotationIntrospector.ReferenceProperty withMember(AnnotatedMember member) {
/*  783 */             return POJOPropertyBuilder.this._annotationIntrospector.findReferenceType(member);
/*      */           }
/*      */         });
/*  786 */     this._referenceInfo = (result == null) ? NOT_REFEFERENCE_PROP : result;
/*  787 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isTypeId() {
/*  792 */     Boolean b = fromMemberAnnotations(new WithMember<Boolean>()
/*      */         {
/*      */           public Boolean withMember(AnnotatedMember member) {
/*  795 */             return POJOPropertyBuilder.this._annotationIntrospector.isTypeId(member);
/*      */           }
/*      */         });
/*  798 */     return (b != null && b.booleanValue());
/*      */   }
/*      */ 
/*      */   
/*      */   public ObjectIdInfo findObjectIdInfo() {
/*  803 */     return fromMemberAnnotations(new WithMember<ObjectIdInfo>()
/*      */         {
/*      */           public ObjectIdInfo withMember(AnnotatedMember member) {
/*  806 */             ObjectIdInfo info = POJOPropertyBuilder.this._annotationIntrospector.findObjectIdInfo(member);
/*  807 */             if (info != null) {
/*  808 */               info = POJOPropertyBuilder.this._annotationIntrospector.findObjectReferenceInfo(member, info);
/*      */             }
/*  810 */             return info;
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonInclude.Value findInclusion() {
/*  817 */     AnnotatedMember a = getAccessor();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  823 */     JsonInclude.Value v = (this._annotationIntrospector == null) ? null : this._annotationIntrospector.findPropertyInclusion(a);
/*  824 */     return (v == null) ? JsonInclude.Value.empty() : v;
/*      */   }
/*      */   
/*      */   public JsonProperty.Access findAccess() {
/*  828 */     return fromMemberAnnotationsExcept(new WithMember<JsonProperty.Access>()
/*      */         {
/*      */           public JsonProperty.Access withMember(AnnotatedMember member) {
/*  831 */             return POJOPropertyBuilder.this._annotationIntrospector.findPropertyAccess(member);
/*      */           }
/*      */         },  JsonProperty.Access.AUTO);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addField(AnnotatedField a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
/*  843 */     this._fields = new Linked<>(a, this._fields, name, explName, visible, ignored);
/*      */   }
/*      */   
/*      */   public void addCtor(AnnotatedParameter a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
/*  847 */     this._ctorParameters = new Linked<>(a, this._ctorParameters, name, explName, visible, ignored);
/*      */   }
/*      */   
/*      */   public void addGetter(AnnotatedMethod a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
/*  851 */     this._getters = new Linked<>(a, this._getters, name, explName, visible, ignored);
/*      */   }
/*      */   
/*      */   public void addSetter(AnnotatedMethod a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
/*  855 */     this._setters = new Linked<>(a, this._setters, name, explName, visible, ignored);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addAll(POJOPropertyBuilder src) {
/*  864 */     this._fields = merge(this._fields, src._fields);
/*  865 */     this._ctorParameters = merge(this._ctorParameters, src._ctorParameters);
/*  866 */     this._getters = merge(this._getters, src._getters);
/*  867 */     this._setters = merge(this._setters, src._setters);
/*      */   }
/*      */ 
/*      */   
/*      */   private static <T> Linked<T> merge(Linked<T> chain1, Linked<T> chain2) {
/*  872 */     if (chain1 == null) {
/*  873 */       return chain2;
/*      */     }
/*  875 */     if (chain2 == null) {
/*  876 */       return chain1;
/*      */     }
/*  878 */     return chain1.append(chain2);
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
/*      */   public void removeIgnored() {
/*  893 */     this._fields = _removeIgnored(this._fields);
/*  894 */     this._getters = _removeIgnored(this._getters);
/*  895 */     this._setters = _removeIgnored(this._setters);
/*  896 */     this._ctorParameters = _removeIgnored(this._ctorParameters);
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public JsonProperty.Access removeNonVisible(boolean inferMutators) {
/*  901 */     return removeNonVisible(inferMutators, (POJOPropertiesCollector)null);
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
/*      */   public JsonProperty.Access removeNonVisible(boolean inferMutators, POJOPropertiesCollector parent) {
/*  917 */     JsonProperty.Access acc = findAccess();
/*  918 */     if (acc == null) {
/*  919 */       acc = JsonProperty.Access.AUTO;
/*      */     }
/*  921 */     switch (acc) {
/*      */ 
/*      */ 
/*      */       
/*      */       case READ_ONLY:
/*  926 */         if (parent != null) {
/*  927 */           parent._collectIgnorals(getName());
/*  928 */           for (PropertyName pn : findExplicitNames()) {
/*  929 */             parent._collectIgnorals(pn.getSimpleName());
/*      */           }
/*      */         } 
/*      */         
/*  933 */         this._setters = null;
/*  934 */         this._ctorParameters = null;
/*  935 */         if (!this._forSerialization) {
/*  936 */           this._fields = null;
/*      */         }
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
/*      */       case READ_WRITE:
/*  959 */         return acc;
/*      */       case WRITE_ONLY:
/*      */         this._getters = null; if (this._forSerialization)
/*      */           this._fields = null; 
/*      */     }  this._getters = _removeNonVisible(this._getters);
/*      */     this._ctorParameters = _removeNonVisible(this._ctorParameters);
/*      */     if (!inferMutators || this._getters == null) {
/*      */       this._fields = _removeNonVisible(this._fields);
/*      */       this._setters = _removeNonVisible(this._setters);
/*  968 */     }  } public void removeConstructors() { this._ctorParameters = null; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void trimByVisibility() {
/*  978 */     this._fields = _trimByVisibility(this._fields);
/*  979 */     this._getters = _trimByVisibility(this._getters);
/*  980 */     this._setters = _trimByVisibility(this._setters);
/*  981 */     this._ctorParameters = _trimByVisibility(this._ctorParameters);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void mergeAnnotations(boolean forSerialization) {
/*  987 */     if (forSerialization) {
/*  988 */       if (this._getters != null) {
/*  989 */         AnnotationMap ann = _mergeAnnotations(0, (Linked<? extends AnnotatedMember>[])new Linked[] { this._getters, this._fields, this._ctorParameters, this._setters });
/*  990 */         this._getters = _applyAnnotations(this._getters, ann);
/*  991 */       } else if (this._fields != null) {
/*  992 */         AnnotationMap ann = _mergeAnnotations(0, (Linked<? extends AnnotatedMember>[])new Linked[] { this._fields, this._ctorParameters, this._setters });
/*  993 */         this._fields = _applyAnnotations(this._fields, ann);
/*      */       }
/*      */     
/*  996 */     } else if (this._ctorParameters != null) {
/*  997 */       AnnotationMap ann = _mergeAnnotations(0, (Linked<? extends AnnotatedMember>[])new Linked[] { this._ctorParameters, this._setters, this._fields, this._getters });
/*  998 */       this._ctorParameters = _applyAnnotations(this._ctorParameters, ann);
/*  999 */     } else if (this._setters != null) {
/* 1000 */       AnnotationMap ann = _mergeAnnotations(0, (Linked<? extends AnnotatedMember>[])new Linked[] { this._setters, this._fields, this._getters });
/* 1001 */       this._setters = _applyAnnotations(this._setters, ann);
/* 1002 */     } else if (this._fields != null) {
/* 1003 */       AnnotationMap ann = _mergeAnnotations(0, (Linked<? extends AnnotatedMember>[])new Linked[] { this._fields, this._getters });
/* 1004 */       this._fields = _applyAnnotations(this._fields, ann);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private AnnotationMap _mergeAnnotations(int index, Linked<? extends AnnotatedMember>... nodes) {
/* 1012 */     AnnotationMap ann = _getAllAnnotations(nodes[index]);
/* 1013 */     while (++index < nodes.length) {
/* 1014 */       if (nodes[index] != null) {
/* 1015 */         return AnnotationMap.merge(ann, _mergeAnnotations(index, nodes));
/*      */       }
/*      */     } 
/* 1018 */     return ann;
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
/*      */   private <T extends AnnotatedMember> AnnotationMap _getAllAnnotations(Linked<T> node) {
/* 1031 */     AnnotationMap ann = ((AnnotatedMember)node.value).getAllAnnotations();
/* 1032 */     if (node.next != null) {
/* 1033 */       ann = AnnotationMap.merge(ann, _getAllAnnotations(node.next));
/*      */     }
/* 1035 */     return ann;
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
/*      */   private <T extends AnnotatedMember> Linked<T> _applyAnnotations(Linked<T> node, AnnotationMap ann) {
/* 1049 */     AnnotatedMember annotatedMember = (AnnotatedMember)((AnnotatedMember)node.value).withAnnotations(ann);
/* 1050 */     if (node.next != null) {
/* 1051 */       node = node.withNext(_applyAnnotations(node.next, ann));
/*      */     }
/* 1053 */     return node.withValue((T)annotatedMember);
/*      */   }
/*      */ 
/*      */   
/*      */   private <T> Linked<T> _removeIgnored(Linked<T> node) {
/* 1058 */     if (node == null) {
/* 1059 */       return node;
/*      */     }
/* 1061 */     return node.withoutIgnored();
/*      */   }
/*      */ 
/*      */   
/*      */   private <T> Linked<T> _removeNonVisible(Linked<T> node) {
/* 1066 */     if (node == null) {
/* 1067 */       return node;
/*      */     }
/* 1069 */     return node.withoutNonVisible();
/*      */   }
/*      */ 
/*      */   
/*      */   private <T> Linked<T> _trimByVisibility(Linked<T> node) {
/* 1074 */     if (node == null) {
/* 1075 */       return node;
/*      */     }
/* 1077 */     return node.trimByVisibility();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private <T> boolean _anyExplicits(Linked<T> n) {
/* 1088 */     for (; n != null; n = n.next) {
/* 1089 */       if (n.name != null && n.name.hasSimpleName()) {
/* 1090 */         return true;
/*      */       }
/*      */     } 
/* 1093 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private <T> boolean _anyExplicitNames(Linked<T> n) {
/* 1098 */     for (; n != null; n = n.next) {
/* 1099 */       if (n.name != null && n.isNameExplicit) {
/* 1100 */         return true;
/*      */       }
/*      */     } 
/* 1103 */     return false;
/*      */   }
/*      */   
/*      */   public boolean anyVisible() {
/* 1107 */     return (_anyVisible(this._fields) || 
/* 1108 */       _anyVisible(this._getters) || 
/* 1109 */       _anyVisible(this._setters) || 
/* 1110 */       _anyVisible(this._ctorParameters));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private <T> boolean _anyVisible(Linked<T> n) {
/* 1116 */     for (; n != null; n = n.next) {
/* 1117 */       if (n.isVisible) {
/* 1118 */         return true;
/*      */       }
/*      */     } 
/* 1121 */     return false;
/*      */   }
/*      */   
/*      */   public boolean anyIgnorals() {
/* 1125 */     return (_anyIgnorals(this._fields) || 
/* 1126 */       _anyIgnorals(this._getters) || 
/* 1127 */       _anyIgnorals(this._setters) || 
/* 1128 */       _anyIgnorals(this._ctorParameters));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private <T> boolean _anyIgnorals(Linked<T> n) {
/* 1134 */     for (; n != null; n = n.next) {
/* 1135 */       if (n.isMarkedIgnored) {
/* 1136 */         return true;
/*      */       }
/*      */     } 
/* 1139 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Set<PropertyName> findExplicitNames() {
/* 1150 */     Set<PropertyName> renamed = null;
/* 1151 */     renamed = _findExplicitNames((Linked)this._fields, renamed);
/* 1152 */     renamed = _findExplicitNames((Linked)this._getters, renamed);
/* 1153 */     renamed = _findExplicitNames((Linked)this._setters, renamed);
/* 1154 */     renamed = _findExplicitNames((Linked)this._ctorParameters, renamed);
/* 1155 */     if (renamed == null) {
/* 1156 */       return Collections.emptySet();
/*      */     }
/* 1158 */     return renamed;
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
/*      */   public Collection<POJOPropertyBuilder> explode(Collection<PropertyName> newNames) {
/* 1171 */     HashMap<PropertyName, POJOPropertyBuilder> props = new HashMap<>();
/* 1172 */     _explode(newNames, props, this._fields);
/* 1173 */     _explode(newNames, props, this._getters);
/* 1174 */     _explode(newNames, props, this._setters);
/* 1175 */     _explode(newNames, props, this._ctorParameters);
/* 1176 */     return props.values();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void _explode(Collection<PropertyName> newNames, Map<PropertyName, POJOPropertyBuilder> props, Linked<?> accessors) {
/* 1184 */     Linked<?> firstAcc = accessors;
/* 1185 */     for (Linked<?> node = accessors; node != null; node = node.next) {
/* 1186 */       PropertyName name = node.name;
/* 1187 */       if (!node.isNameExplicit || name == null) {
/*      */         
/* 1189 */         if (node.isVisible)
/*      */         {
/*      */ 
/*      */           
/* 1193 */           throw new IllegalStateException("Conflicting/ambiguous property name definitions (implicit name " + 
/* 1194 */               ClassUtil.name(this._name) + "): found multiple explicit names: " + newNames + ", but also implicit accessor: " + node);
/*      */         }
/*      */       } else {
/* 1197 */         POJOPropertyBuilder prop = props.get(name);
/* 1198 */         if (prop == null) {
/* 1199 */           prop = new POJOPropertyBuilder(this._config, this._annotationIntrospector, this._forSerialization, this._internalName, name);
/*      */           
/* 1201 */           props.put(name, prop);
/*      */         } 
/*      */         
/* 1204 */         if (firstAcc == this._fields) {
/* 1205 */           Linked<AnnotatedField> n2 = (Linked)node;
/* 1206 */           prop._fields = n2.withNext(prop._fields);
/* 1207 */         } else if (firstAcc == this._getters) {
/* 1208 */           Linked<AnnotatedMethod> n2 = (Linked)node;
/* 1209 */           prop._getters = n2.withNext(prop._getters);
/* 1210 */         } else if (firstAcc == this._setters) {
/* 1211 */           Linked<AnnotatedMethod> n2 = (Linked)node;
/* 1212 */           prop._setters = n2.withNext(prop._setters);
/* 1213 */         } else if (firstAcc == this._ctorParameters) {
/* 1214 */           Linked<AnnotatedParameter> n2 = (Linked)node;
/* 1215 */           prop._ctorParameters = n2.withNext(prop._ctorParameters);
/*      */         } else {
/* 1217 */           throw new IllegalStateException("Internal error: mismatched accessors, property: " + this);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private Set<PropertyName> _findExplicitNames(Linked<? extends AnnotatedMember> node, Set<PropertyName> renamed) {
/* 1225 */     for (; node != null; node = node.next) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1232 */       if (node.isNameExplicit && node.name != null) {
/*      */ 
/*      */         
/* 1235 */         if (renamed == null) {
/* 1236 */           renamed = new HashSet<>();
/*      */         }
/* 1238 */         renamed.add(node.name);
/*      */       } 
/* 1240 */     }  return renamed;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1247 */     StringBuilder sb = new StringBuilder();
/* 1248 */     sb.append("[Property '").append(this._name)
/* 1249 */       .append("'; ctors: ").append(this._ctorParameters)
/* 1250 */       .append(", field(s): ").append(this._fields)
/* 1251 */       .append(", getter(s): ").append(this._getters)
/* 1252 */       .append(", setter(s): ").append(this._setters);
/*      */     
/* 1254 */     sb.append("]");
/* 1255 */     return sb.toString();
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
/*      */   protected <T> T fromMemberAnnotations(WithMember<T> func) {
/* 1270 */     T result = null;
/* 1271 */     if (this._annotationIntrospector != null) {
/* 1272 */       if (this._forSerialization) {
/* 1273 */         if (this._getters != null) {
/* 1274 */           result = func.withMember((AnnotatedMember)this._getters.value);
/*      */         }
/*      */       } else {
/* 1277 */         if (this._ctorParameters != null) {
/* 1278 */           result = func.withMember((AnnotatedMember)this._ctorParameters.value);
/*      */         }
/* 1280 */         if (result == null && this._setters != null) {
/* 1281 */           result = func.withMember((AnnotatedMember)this._setters.value);
/*      */         }
/*      */       } 
/* 1284 */       if (result == null && this._fields != null) {
/* 1285 */         result = func.withMember((AnnotatedMember)this._fields.value);
/*      */       }
/*      */     } 
/* 1288 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   protected <T> T fromMemberAnnotationsExcept(WithMember<T> func, T defaultValue) {
/* 1293 */     if (this._annotationIntrospector == null) {
/* 1294 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1299 */     if (this._forSerialization) {
/* 1300 */       if (this._getters != null) {
/* 1301 */         T result = func.withMember((AnnotatedMember)this._getters.value);
/* 1302 */         if (result != null && result != defaultValue) {
/* 1303 */           return result;
/*      */         }
/*      */       } 
/* 1306 */       if (this._fields != null) {
/* 1307 */         T result = func.withMember((AnnotatedMember)this._fields.value);
/* 1308 */         if (result != null && result != defaultValue) {
/* 1309 */           return result;
/*      */         }
/*      */       } 
/* 1312 */       if (this._ctorParameters != null) {
/* 1313 */         T result = func.withMember((AnnotatedMember)this._ctorParameters.value);
/* 1314 */         if (result != null && result != defaultValue) {
/* 1315 */           return result;
/*      */         }
/*      */       } 
/* 1318 */       if (this._setters != null) {
/* 1319 */         T result = func.withMember((AnnotatedMember)this._setters.value);
/* 1320 */         if (result != null && result != defaultValue) {
/* 1321 */           return result;
/*      */         }
/*      */       } 
/* 1324 */       return null;
/*      */     } 
/* 1326 */     if (this._ctorParameters != null) {
/* 1327 */       T result = func.withMember((AnnotatedMember)this._ctorParameters.value);
/* 1328 */       if (result != null && result != defaultValue) {
/* 1329 */         return result;
/*      */       }
/*      */     } 
/* 1332 */     if (this._setters != null) {
/* 1333 */       T result = func.withMember((AnnotatedMember)this._setters.value);
/* 1334 */       if (result != null && result != defaultValue) {
/* 1335 */         return result;
/*      */       }
/*      */     } 
/* 1338 */     if (this._fields != null) {
/* 1339 */       T result = func.withMember((AnnotatedMember)this._fields.value);
/* 1340 */       if (result != null && result != defaultValue) {
/* 1341 */         return result;
/*      */       }
/*      */     } 
/* 1344 */     if (this._getters != null) {
/* 1345 */       T result = func.withMember((AnnotatedMember)this._getters.value);
/* 1346 */       if (result != null && result != defaultValue) {
/* 1347 */         return result;
/*      */       }
/*      */     } 
/* 1350 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Class<?> _rawTypeOf(AnnotatedMember m) {
/* 1360 */     if (m instanceof AnnotatedMethod) {
/* 1361 */       AnnotatedMethod meh = (AnnotatedMethod)m;
/* 1362 */       if (meh.getParameterCount() > 0)
/*      */       {
/*      */         
/* 1365 */         return meh.getParameterType(0).getRawClass();
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1370 */     return m.getType().getRawClass();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static class MemberIterator<T extends AnnotatedMember>
/*      */     implements Iterator<T>
/*      */   {
/*      */     private POJOPropertyBuilder.Linked<T> next;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public MemberIterator(POJOPropertyBuilder.Linked<T> first) {
/* 1392 */       this.next = first;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasNext() {
/* 1397 */       return (this.next != null);
/*      */     }
/*      */ 
/*      */     
/*      */     public T next() {
/* 1402 */       if (this.next == null) throw new NoSuchElementException(); 
/* 1403 */       AnnotatedMember annotatedMember = (AnnotatedMember)this.next.value;
/* 1404 */       this.next = this.next.next;
/* 1405 */       return (T)annotatedMember;
/*      */     }
/*      */ 
/*      */     
/*      */     public void remove() {
/* 1410 */       throw new UnsupportedOperationException();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static final class Linked<T>
/*      */   {
/*      */     public final T value;
/*      */     
/*      */     public final Linked<T> next;
/*      */     
/*      */     public final PropertyName name;
/*      */     
/*      */     public final boolean isNameExplicit;
/*      */     
/*      */     public final boolean isVisible;
/*      */     
/*      */     public final boolean isMarkedIgnored;
/*      */ 
/*      */     
/*      */     public Linked(T v, Linked<T> n, PropertyName name, boolean explName, boolean visible, boolean ignored) {
/* 1432 */       this.value = v;
/* 1433 */       this.next = n;
/*      */       
/* 1435 */       this.name = (name == null || name.isEmpty()) ? null : name;
/*      */       
/* 1437 */       if (explName) {
/* 1438 */         if (this.name == null) {
/* 1439 */           throw new IllegalArgumentException("Cannot pass true for 'explName' if name is null/empty");
/*      */         }
/*      */ 
/*      */         
/* 1443 */         if (!name.hasSimpleName()) {
/* 1444 */           explName = false;
/*      */         }
/*      */       } 
/*      */       
/* 1448 */       this.isNameExplicit = explName;
/* 1449 */       this.isVisible = visible;
/* 1450 */       this.isMarkedIgnored = ignored;
/*      */     }
/*      */     
/*      */     public Linked<T> withoutNext() {
/* 1454 */       if (this.next == null) {
/* 1455 */         return this;
/*      */       }
/* 1457 */       return new Linked(this.value, null, this.name, this.isNameExplicit, this.isVisible, this.isMarkedIgnored);
/*      */     }
/*      */     
/*      */     public Linked<T> withValue(T newValue) {
/* 1461 */       if (newValue == this.value) {
/* 1462 */         return this;
/*      */       }
/* 1464 */       return new Linked(newValue, this.next, this.name, this.isNameExplicit, this.isVisible, this.isMarkedIgnored);
/*      */     }
/*      */     
/*      */     public Linked<T> withNext(Linked<T> newNext) {
/* 1468 */       if (newNext == this.next) {
/* 1469 */         return this;
/*      */       }
/* 1471 */       return new Linked(this.value, newNext, this.name, this.isNameExplicit, this.isVisible, this.isMarkedIgnored);
/*      */     }
/*      */     
/*      */     public Linked<T> withoutIgnored() {
/* 1475 */       if (this.isMarkedIgnored) {
/* 1476 */         return (this.next == null) ? null : this.next.withoutIgnored();
/*      */       }
/* 1478 */       if (this.next != null) {
/* 1479 */         Linked<T> newNext = this.next.withoutIgnored();
/* 1480 */         if (newNext != this.next) {
/* 1481 */           return withNext(newNext);
/*      */         }
/*      */       } 
/* 1484 */       return this;
/*      */     }
/*      */     
/*      */     public Linked<T> withoutNonVisible() {
/* 1488 */       Linked<T> newNext = (this.next == null) ? null : this.next.withoutNonVisible();
/* 1489 */       return this.isVisible ? withNext(newNext) : newNext;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected Linked<T> append(Linked<T> appendable) {
/* 1497 */       if (this.next == null) {
/* 1498 */         return withNext(appendable);
/*      */       }
/* 1500 */       return withNext(this.next.append(appendable));
/*      */     }
/*      */     
/*      */     public Linked<T> trimByVisibility() {
/* 1504 */       if (this.next == null) {
/* 1505 */         return this;
/*      */       }
/* 1507 */       Linked<T> newNext = this.next.trimByVisibility();
/* 1508 */       if (this.name != null) {
/* 1509 */         if (newNext.name == null) {
/* 1510 */           return withNext(null);
/*      */         }
/*      */         
/* 1513 */         return withNext(newNext);
/*      */       } 
/* 1515 */       if (newNext.name != null) {
/* 1516 */         return newNext;
/*      */       }
/*      */       
/* 1519 */       if (this.isVisible == newNext.isVisible) {
/* 1520 */         return withNext(newNext);
/*      */       }
/* 1522 */       return this.isVisible ? withNext(null) : newNext;
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/* 1527 */       String msg = String.format("%s[visible=%b,ignore=%b,explicitName=%b]", new Object[] { this.value
/* 1528 */             .toString(), Boolean.valueOf(this.isVisible), Boolean.valueOf(this.isMarkedIgnored), Boolean.valueOf(this.isNameExplicit) });
/* 1529 */       if (this.next != null) {
/* 1530 */         msg = msg + ", " + this.next.toString();
/*      */       }
/* 1532 */       return msg;
/*      */     }
/*      */   }
/*      */   
/*      */   private static interface WithMember<T> {
/*      */     T withMember(AnnotatedMember param1AnnotatedMember);
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/POJOPropertyBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */