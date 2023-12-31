/*     */ package com.fasterxml.jackson.databind.cfg;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import java.io.Serializable;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CoercionConfigs
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  19 */   private static final int TARGET_TYPE_COUNT = (LogicalType.values()).length;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CoercionAction _defaultAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final MutableCoercionConfig _defaultCoercions;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MutableCoercionConfig[] _perTypeCoercions;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Map<Class<?>, MutableCoercionConfig> _perClassCoercions;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CoercionConfigs() {
/*  49 */     this(CoercionAction.TryConvert, new MutableCoercionConfig(), null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CoercionConfigs(CoercionAction defaultAction, MutableCoercionConfig defaultCoercions, MutableCoercionConfig[] perTypeCoercions, Map<Class<?>, MutableCoercionConfig> perClassCoercions) {
/*  58 */     this._defaultCoercions = defaultCoercions;
/*  59 */     this._defaultAction = defaultAction;
/*  60 */     this._perTypeCoercions = perTypeCoercions;
/*  61 */     this._perClassCoercions = perClassCoercions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CoercionConfigs copy() {
/*     */     MutableCoercionConfig[] newPerType;
/*     */     Map<Class<?>, MutableCoercionConfig> newPerClass;
/*  74 */     if (this._perTypeCoercions == null) {
/*  75 */       newPerType = null;
/*     */     } else {
/*  77 */       int size = this._perTypeCoercions.length;
/*  78 */       newPerType = new MutableCoercionConfig[size];
/*  79 */       for (int i = 0; i < size; i++) {
/*  80 */         newPerType[i] = _copy(this._perTypeCoercions[i]);
/*     */       }
/*     */     } 
/*     */     
/*  84 */     if (this._perClassCoercions == null) {
/*  85 */       newPerClass = null;
/*     */     } else {
/*  87 */       newPerClass = new HashMap<>();
/*  88 */       for (Map.Entry<Class<?>, MutableCoercionConfig> entry : this._perClassCoercions.entrySet()) {
/*  89 */         newPerClass.put(entry.getKey(), ((MutableCoercionConfig)entry.getValue()).copy());
/*     */       }
/*     */     } 
/*  92 */     return new CoercionConfigs(this._defaultAction, this._defaultCoercions.copy(), newPerType, newPerClass);
/*     */   }
/*     */ 
/*     */   
/*     */   private static MutableCoercionConfig _copy(MutableCoercionConfig src) {
/*  97 */     if (src == null) {
/*  98 */       return null;
/*     */     }
/* 100 */     return src.copy();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableCoercionConfig defaultCoercions() {
/* 110 */     return this._defaultCoercions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableCoercionConfig findOrCreateCoercion(LogicalType type) {
/* 120 */     if (this._perTypeCoercions == null) {
/* 121 */       this._perTypeCoercions = new MutableCoercionConfig[TARGET_TYPE_COUNT];
/*     */     }
/* 123 */     MutableCoercionConfig config = this._perTypeCoercions[type.ordinal()];
/* 124 */     if (config == null) {
/* 125 */       this._perTypeCoercions[type.ordinal()] = config = new MutableCoercionConfig();
/*     */     }
/* 127 */     return config;
/*     */   }
/*     */   
/*     */   public MutableCoercionConfig findOrCreateCoercion(Class<?> type) {
/* 131 */     if (this._perClassCoercions == null) {
/* 132 */       this._perClassCoercions = new HashMap<>();
/*     */     }
/* 134 */     MutableCoercionConfig config = this._perClassCoercions.get(type);
/* 135 */     if (config == null) {
/* 136 */       config = new MutableCoercionConfig();
/* 137 */       this._perClassCoercions.put(type, config);
/*     */     } 
/* 139 */     return config;
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
/*     */   public CoercionAction findCoercion(DeserializationConfig config, LogicalType targetType, Class<?> targetClass, CoercionInputShape inputShape) {
/* 166 */     if (this._perClassCoercions != null && targetClass != null) {
/* 167 */       MutableCoercionConfig cc = this._perClassCoercions.get(targetClass);
/* 168 */       if (cc != null) {
/* 169 */         CoercionAction coercionAction = cc.findAction(inputShape);
/* 170 */         if (coercionAction != null) {
/* 171 */           return coercionAction;
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 177 */     if (this._perTypeCoercions != null && targetType != null) {
/* 178 */       MutableCoercionConfig cc = this._perTypeCoercions[targetType.ordinal()];
/* 179 */       if (cc != null) {
/* 180 */         CoercionAction coercionAction = cc.findAction(inputShape);
/* 181 */         if (coercionAction != null) {
/* 182 */           return coercionAction;
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 188 */     CoercionAction act = this._defaultCoercions.findAction(inputShape);
/* 189 */     if (act != null) {
/* 190 */       return act;
/*     */     }
/*     */ 
/*     */     
/* 194 */     switch (inputShape) {
/*     */       
/*     */       case EmptyArray:
/* 197 */         return config.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT) ? CoercionAction.AsNull : CoercionAction.Fail;
/*     */       
/*     */       case Float:
/* 200 */         if (targetType == LogicalType.Integer)
/*     */         {
/* 202 */           return config.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT) ? CoercionAction.TryConvert : CoercionAction.Fail;
/*     */         }
/*     */         break;
/*     */       
/*     */       case Integer:
/* 207 */         if (targetType == LogicalType.Enum && 
/* 208 */           config.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)) {
/* 209 */           return CoercionAction.Fail;
/*     */         }
/*     */         break;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 218 */     boolean baseScalar = _isScalarType(targetType);
/*     */     
/* 220 */     if (baseScalar)
/*     */     {
/* 222 */       if (!config.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
/* 223 */         return CoercionAction.Fail;
/*     */       }
/*     */     }
/*     */     
/* 227 */     if (inputShape == CoercionInputShape.EmptyString) {
/*     */ 
/*     */       
/* 230 */       if (baseScalar || config
/*     */         
/* 232 */         .isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
/* 233 */         return CoercionAction.AsNull;
/*     */       }
/*     */ 
/*     */       
/* 237 */       if (targetType == LogicalType.OtherScalar) {
/* 238 */         return CoercionAction.TryConvert;
/*     */       }
/*     */       
/* 241 */       return CoercionAction.Fail;
/*     */     } 
/*     */ 
/*     */     
/* 245 */     return this._defaultAction;
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
/*     */   public CoercionAction findCoercionFromBlankString(DeserializationConfig config, LogicalType targetType, Class<?> targetClass, CoercionAction actionIfBlankNotAllowed) {
/* 268 */     Boolean acceptBlankAsEmpty = null;
/* 269 */     CoercionAction action = null;
/*     */ 
/*     */     
/* 272 */     if (this._perClassCoercions != null && targetClass != null) {
/* 273 */       MutableCoercionConfig cc = this._perClassCoercions.get(targetClass);
/* 274 */       if (cc != null) {
/* 275 */         acceptBlankAsEmpty = cc.getAcceptBlankAsEmpty();
/* 276 */         action = cc.findAction(CoercionInputShape.EmptyString);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 281 */     if (this._perTypeCoercions != null && targetType != null) {
/* 282 */       MutableCoercionConfig cc = this._perTypeCoercions[targetType.ordinal()];
/* 283 */       if (cc != null) {
/* 284 */         if (acceptBlankAsEmpty == null) {
/* 285 */           acceptBlankAsEmpty = cc.getAcceptBlankAsEmpty();
/*     */         }
/* 287 */         if (action == null) {
/* 288 */           action = cc.findAction(CoercionInputShape.EmptyString);
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 294 */     if (acceptBlankAsEmpty == null) {
/* 295 */       acceptBlankAsEmpty = this._defaultCoercions.getAcceptBlankAsEmpty();
/*     */     }
/* 297 */     if (action == null) {
/* 298 */       action = this._defaultCoercions.findAction(CoercionInputShape.EmptyString);
/*     */     }
/*     */ 
/*     */     
/* 302 */     if (Boolean.FALSE.equals(acceptBlankAsEmpty)) {
/* 303 */       return actionIfBlankNotAllowed;
/*     */     }
/*     */     
/* 306 */     if (action != null) {
/* 307 */       return action;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 312 */     if (_isScalarType(targetType)) {
/* 313 */       return CoercionAction.AsNull;
/*     */     }
/*     */ 
/*     */     
/* 317 */     if (config.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
/* 318 */       return CoercionAction.AsNull;
/*     */     }
/*     */ 
/*     */     
/* 322 */     return actionIfBlankNotAllowed;
/*     */   }
/*     */   
/*     */   protected boolean _isScalarType(LogicalType targetType) {
/* 326 */     return (targetType == LogicalType.Float || targetType == LogicalType.Integer || targetType == LogicalType.Boolean || targetType == LogicalType.DateTime);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/cfg/CoercionConfigs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */