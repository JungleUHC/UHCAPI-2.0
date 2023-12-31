/*     */ package com.fasterxml.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.fasterxml.jackson.databind.deser.ValueInstantiator;
/*     */ import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.lang.reflect.Member;
/*     */ import java.util.HashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CreatorCollector
/*     */ {
/*     */   protected static final int C_DEFAULT = 0;
/*     */   protected static final int C_STRING = 1;
/*     */   protected static final int C_INT = 2;
/*     */   protected static final int C_LONG = 3;
/*     */   protected static final int C_BIG_INTEGER = 4;
/*  32 */   protected static final String[] TYPE_DESCS = new String[] { "default", "from-String", "from-int", "from-long", "from-big-integer", "from-double", "from-big-decimal", "from-boolean", "delegate", "property-based", "array-delegate" };
/*     */ 
/*     */   
/*     */   protected static final int C_DOUBLE = 5;
/*     */ 
/*     */   
/*     */   protected static final int C_BIG_DECIMAL = 6;
/*     */   
/*     */   protected static final int C_BOOLEAN = 7;
/*     */   
/*     */   protected static final int C_DELEGATE = 8;
/*     */   
/*     */   protected static final int C_PROPS = 9;
/*     */   
/*     */   protected static final int C_ARRAY_DELEGATE = 10;
/*     */   
/*     */   protected final BeanDescription _beanDesc;
/*     */   
/*     */   protected final boolean _canFixAccess;
/*     */   
/*     */   protected final boolean _forceAccess;
/*     */   
/*  54 */   protected final AnnotatedWithParams[] _creators = new AnnotatedWithParams[11];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  63 */   protected int _explicitCreators = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _hasNonDefaultCreator = false;
/*     */ 
/*     */   
/*     */   protected SettableBeanProperty[] _delegateArgs;
/*     */ 
/*     */   
/*     */   protected SettableBeanProperty[] _arrayDelegateArgs;
/*     */ 
/*     */   
/*     */   protected SettableBeanProperty[] _propertyBasedArgs;
/*     */ 
/*     */ 
/*     */   
/*     */   public CreatorCollector(BeanDescription beanDesc, MapperConfig<?> config) {
/*  81 */     this._beanDesc = beanDesc;
/*  82 */     this._canFixAccess = config.canOverrideAccessModifiers();
/*  83 */     this
/*  84 */       ._forceAccess = config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ValueInstantiator constructValueInstantiator(DeserializationContext ctxt) throws JsonMappingException {
/*  90 */     DeserializationConfig config = ctxt.getConfig();
/*  91 */     JavaType delegateType = _computeDelegateType(ctxt, this._creators[8], this._delegateArgs);
/*     */     
/*  93 */     JavaType arrayDelegateType = _computeDelegateType(ctxt, this._creators[10], this._arrayDelegateArgs);
/*     */     
/*  95 */     JavaType type = this._beanDesc.getType();
/*     */     
/*  97 */     StdValueInstantiator inst = new StdValueInstantiator(config, type);
/*  98 */     inst.configureFromObjectSettings(this._creators[0], this._creators[8], delegateType, this._delegateArgs, this._creators[9], this._propertyBasedArgs);
/*     */ 
/*     */     
/* 101 */     inst.configureFromArraySettings(this._creators[10], arrayDelegateType, this._arrayDelegateArgs);
/*     */     
/* 103 */     inst.configureFromStringCreator(this._creators[1]);
/* 104 */     inst.configureFromIntCreator(this._creators[2]);
/* 105 */     inst.configureFromLongCreator(this._creators[3]);
/* 106 */     inst.configureFromBigIntegerCreator(this._creators[4]);
/* 107 */     inst.configureFromDoubleCreator(this._creators[5]);
/* 108 */     inst.configureFromBigDecimalCreator(this._creators[6]);
/* 109 */     inst.configureFromBooleanCreator(this._creators[7]);
/* 110 */     return (ValueInstantiator)inst;
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
/*     */   public void setDefaultCreator(AnnotatedWithParams creator) {
/* 130 */     this._creators[0] = _fixAccess(creator);
/*     */   }
/*     */   
/*     */   public void addStringCreator(AnnotatedWithParams creator, boolean explicit) {
/* 134 */     verifyNonDup(creator, 1, explicit);
/*     */   }
/*     */   
/*     */   public void addIntCreator(AnnotatedWithParams creator, boolean explicit) {
/* 138 */     verifyNonDup(creator, 2, explicit);
/*     */   }
/*     */   
/*     */   public void addLongCreator(AnnotatedWithParams creator, boolean explicit) {
/* 142 */     verifyNonDup(creator, 3, explicit);
/*     */   }
/*     */   
/*     */   public void addBigIntegerCreator(AnnotatedWithParams creator, boolean explicit) {
/* 146 */     verifyNonDup(creator, 4, explicit);
/*     */   }
/*     */   
/*     */   public void addDoubleCreator(AnnotatedWithParams creator, boolean explicit) {
/* 150 */     verifyNonDup(creator, 5, explicit);
/*     */   }
/*     */   
/*     */   public void addBigDecimalCreator(AnnotatedWithParams creator, boolean explicit) {
/* 154 */     verifyNonDup(creator, 6, explicit);
/*     */   }
/*     */   
/*     */   public void addBooleanCreator(AnnotatedWithParams creator, boolean explicit) {
/* 158 */     verifyNonDup(creator, 7, explicit);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addDelegatingCreator(AnnotatedWithParams creator, boolean explicit, SettableBeanProperty[] injectables, int delegateeIndex) {
/* 165 */     if (creator.getParameterType(delegateeIndex).isCollectionLikeType()) {
/* 166 */       if (verifyNonDup(creator, 10, explicit)) {
/* 167 */         this._arrayDelegateArgs = injectables;
/*     */       }
/*     */     }
/* 170 */     else if (verifyNonDup(creator, 8, explicit)) {
/* 171 */       this._delegateArgs = injectables;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addPropertyCreator(AnnotatedWithParams creator, boolean explicit, SettableBeanProperty[] properties) {
/* 179 */     if (verifyNonDup(creator, 9, explicit)) {
/*     */       
/* 181 */       if (properties.length > 1) {
/* 182 */         HashMap<String, Integer> names = new HashMap<>();
/* 183 */         for (int i = 0, len = properties.length; i < len; i++) {
/* 184 */           String name = properties[i].getName();
/*     */ 
/*     */           
/* 187 */           if (!name.isEmpty() || properties[i].getInjectableValueId() == null) {
/*     */ 
/*     */             
/* 190 */             Integer old = names.put(name, Integer.valueOf(i));
/* 191 */             if (old != null)
/* 192 */               throw new IllegalArgumentException(String.format("Duplicate creator property \"%s\" (index %s vs %d) for type %s ", new Object[] { name, old, 
/*     */                       
/* 194 */                       Integer.valueOf(i), ClassUtil.nameOf(this._beanDesc.getBeanClass()) })); 
/*     */           } 
/*     */         } 
/*     */       } 
/* 198 */       this._propertyBasedArgs = properties;
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
/*     */   public boolean hasDefaultCreator() {
/* 212 */     return (this._creators[0] != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasDelegatingCreator() {
/* 219 */     return (this._creators[8] != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasPropertyBasedCreator() {
/* 226 */     return (this._creators[9] != null);
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
/*     */   private JavaType _computeDelegateType(DeserializationContext ctxt, AnnotatedWithParams creator, SettableBeanProperty[] delegateArgs) throws JsonMappingException {
/* 239 */     if (!this._hasNonDefaultCreator || creator == null) {
/* 240 */       return null;
/*     */     }
/*     */     
/* 243 */     int ix = 0;
/* 244 */     if (delegateArgs != null) {
/* 245 */       for (int i = 0, len = delegateArgs.length; i < len; i++) {
/* 246 */         if (delegateArgs[i] == null) {
/* 247 */           ix = i;
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     }
/* 252 */     DeserializationConfig config = ctxt.getConfig();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 257 */     JavaType baseType = creator.getParameterType(ix);
/* 258 */     AnnotationIntrospector intr = config.getAnnotationIntrospector();
/* 259 */     if (intr != null) {
/* 260 */       AnnotatedParameter delegate = creator.getParameter(ix);
/*     */ 
/*     */       
/* 263 */       Object deserDef = intr.findDeserializer((Annotated)delegate);
/* 264 */       if (deserDef != null) {
/* 265 */         JsonDeserializer<Object> deser = ctxt.deserializerInstance((Annotated)delegate, deserDef);
/* 266 */         baseType = baseType.withValueHandler(deser);
/*     */       } else {
/*     */         
/* 269 */         baseType = intr.refineDeserializationType((MapperConfig)config, (Annotated)delegate, baseType);
/*     */       } 
/*     */     } 
/*     */     
/* 273 */     return baseType;
/*     */   }
/*     */   
/*     */   private <T extends com.fasterxml.jackson.databind.introspect.AnnotatedMember> T _fixAccess(T member) {
/* 277 */     if (member != null && this._canFixAccess) {
/* 278 */       ClassUtil.checkAndFixAccess((Member)member.getAnnotated(), this._forceAccess);
/*     */     }
/*     */     
/* 281 */     return member;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean verifyNonDup(AnnotatedWithParams newOne, int typeIndex, boolean explicit) {
/* 289 */     int mask = 1 << typeIndex;
/* 290 */     this._hasNonDefaultCreator = true;
/* 291 */     AnnotatedWithParams oldOne = this._creators[typeIndex];
/*     */     
/* 293 */     if (oldOne != null) {
/*     */       boolean verify;
/* 295 */       if ((this._explicitCreators & mask) != 0) {
/*     */         
/* 297 */         if (!explicit) {
/* 298 */           return false;
/*     */         }
/*     */         
/* 301 */         verify = true;
/*     */       } else {
/*     */         
/* 304 */         verify = !explicit;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 311 */       if (verify && oldOne.getClass() == newOne.getClass()) {
/*     */         
/* 313 */         Class<?> oldType = oldOne.getRawParameterType(0);
/* 314 */         Class<?> newType = newOne.getRawParameterType(0);
/*     */         
/* 316 */         if (oldType == newType) {
/*     */ 
/*     */ 
/*     */           
/* 320 */           if (_isEnumValueOf(newOne)) {
/* 321 */             return false;
/*     */           }
/* 323 */           if (!_isEnumValueOf(oldOne))
/*     */           {
/*     */             
/* 326 */             _reportDuplicateCreator(typeIndex, explicit, oldOne, newOne);
/*     */           }
/*     */         } else {
/*     */           
/* 330 */           if (newType.isAssignableFrom(oldType))
/*     */           {
/* 332 */             return false; } 
/* 333 */           if (!oldType.isAssignableFrom(newType))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 341 */             if (oldType.isPrimitive() != newType.isPrimitive()) {
/*     */               
/* 343 */               if (oldType.isPrimitive()) {
/* 344 */                 return false;
/*     */               
/*     */               }
/*     */             }
/*     */             else {
/*     */               
/* 350 */               _reportDuplicateCreator(typeIndex, explicit, oldOne, newOne);
/*     */             }  } 
/*     */         } 
/*     */       } 
/* 354 */     }  if (explicit) {
/* 355 */       this._explicitCreators |= mask;
/*     */     }
/* 357 */     this._creators[typeIndex] = _fixAccess(newOne);
/* 358 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _reportDuplicateCreator(int typeIndex, boolean explicit, AnnotatedWithParams oldOne, AnnotatedWithParams newOne) {
/* 364 */     throw new IllegalArgumentException(String.format("Conflicting %s creators: already had %s creator %s, encountered another: %s", new Object[] { TYPE_DESCS[typeIndex], explicit ? "explicitly marked" : "implicitly discovered", oldOne, newOne }));
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
/*     */   protected boolean _isEnumValueOf(AnnotatedWithParams creator) {
/* 378 */     return (ClassUtil.isEnumType(creator.getDeclaringClass()) && "valueOf"
/* 379 */       .equals(creator.getName()));
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/impl/CreatorCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */