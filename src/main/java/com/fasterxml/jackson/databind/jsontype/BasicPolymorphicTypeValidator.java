/*     */ package com.fasterxml.jackson.databind.jsontype;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BasicPolymorphicTypeValidator
/*     */   extends PolymorphicTypeValidator.Base
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final Set<Class<?>> _invalidBaseTypes;
/*     */   protected final TypeMatcher[] _baseTypeMatchers;
/*     */   protected final NameMatcher[] _subTypeNameMatchers;
/*     */   protected final TypeMatcher[] _subClassMatchers;
/*     */   
/*     */   public static abstract class TypeMatcher
/*     */   {
/*     */     public abstract boolean match(MapperConfig<?> param1MapperConfig, Class<?> param1Class);
/*     */   }
/*     */   
/*     */   public static abstract class NameMatcher
/*     */   {
/*     */     public abstract boolean match(MapperConfig<?> param1MapperConfig, String param1String);
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */   {
/*     */     protected Set<Class<?>> _invalidBaseTypes;
/*     */     protected List<BasicPolymorphicTypeValidator.TypeMatcher> _baseTypeMatchers;
/*     */     protected List<BasicPolymorphicTypeValidator.NameMatcher> _subTypeNameMatchers;
/*     */     protected List<BasicPolymorphicTypeValidator.TypeMatcher> _subTypeClassMatchers;
/*     */     
/*     */     public Builder allowIfBaseType(final Class<?> baseOfBase) {
/* 105 */       return _appendBaseMatcher(new BasicPolymorphicTypeValidator.TypeMatcher()
/*     */           {
/*     */             public boolean match(MapperConfig<?> config, Class<?> clazz) {
/* 108 */               return baseOfBase.isAssignableFrom(clazz);
/*     */             }
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder allowIfBaseType(final Pattern patternForBase) {
/* 131 */       return _appendBaseMatcher(new BasicPolymorphicTypeValidator.TypeMatcher()
/*     */           {
/*     */             public boolean match(MapperConfig<?> config, Class<?> clazz) {
/* 134 */               return patternForBase.matcher(clazz.getName()).matches();
/*     */             }
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder allowIfBaseType(final String prefixForBase) {
/* 151 */       return _appendBaseMatcher(new BasicPolymorphicTypeValidator.TypeMatcher()
/*     */           {
/*     */             public boolean match(MapperConfig<?> config, Class<?> clazz) {
/* 154 */               return clazz.getName().startsWith(prefixForBase);
/*     */             }
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder allowIfBaseType(BasicPolymorphicTypeValidator.TypeMatcher matcher) {
/* 171 */       return _appendBaseMatcher(matcher);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder denyForExactBaseType(Class<?> baseTypeToDeny) {
/* 187 */       if (this._invalidBaseTypes == null) {
/* 188 */         this._invalidBaseTypes = new HashSet<>();
/*     */       }
/* 190 */       this._invalidBaseTypes.add(baseTypeToDeny);
/* 191 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder allowIfSubType(final Class<?> subTypeBase) {
/* 208 */       return _appendSubClassMatcher(new BasicPolymorphicTypeValidator.TypeMatcher()
/*     */           {
/*     */             public boolean match(MapperConfig<?> config, Class<?> clazz) {
/* 211 */               return subTypeBase.isAssignableFrom(clazz);
/*     */             }
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder allowIfSubType(final Pattern patternForSubType) {
/* 233 */       return _appendSubNameMatcher(new BasicPolymorphicTypeValidator.NameMatcher()
/*     */           {
/*     */             public boolean match(MapperConfig<?> config, String clazzName) {
/* 236 */               return patternForSubType.matcher(clazzName).matches();
/*     */             }
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder allowIfSubType(final String prefixForSubType) {
/* 253 */       return _appendSubNameMatcher(new BasicPolymorphicTypeValidator.NameMatcher()
/*     */           {
/*     */             public boolean match(MapperConfig<?> config, String clazzName) {
/* 256 */               return clazzName.startsWith(prefixForSubType);
/*     */             }
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder allowIfSubType(BasicPolymorphicTypeValidator.TypeMatcher matcher) {
/* 273 */       return _appendSubClassMatcher(matcher);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder allowIfSubTypeIsArray() {
/* 291 */       return _appendSubClassMatcher(new BasicPolymorphicTypeValidator.TypeMatcher()
/*     */           {
/*     */             public boolean match(MapperConfig<?> config, Class<?> clazz) {
/* 294 */               return clazz.isArray();
/*     */             }
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public BasicPolymorphicTypeValidator build() {
/* 319 */       return new BasicPolymorphicTypeValidator(this._invalidBaseTypes, (this._baseTypeMatchers == null) ? null : this._baseTypeMatchers
/* 320 */           .<BasicPolymorphicTypeValidator.TypeMatcher>toArray(new BasicPolymorphicTypeValidator.TypeMatcher[0]), (this._subTypeNameMatchers == null) ? null : this._subTypeNameMatchers
/* 321 */           .<BasicPolymorphicTypeValidator.NameMatcher>toArray(new BasicPolymorphicTypeValidator.NameMatcher[0]), (this._subTypeClassMatchers == null) ? null : this._subTypeClassMatchers
/* 322 */           .<BasicPolymorphicTypeValidator.TypeMatcher>toArray(new BasicPolymorphicTypeValidator.TypeMatcher[0]));
/*     */     }
/*     */ 
/*     */     
/*     */     protected Builder _appendBaseMatcher(BasicPolymorphicTypeValidator.TypeMatcher matcher) {
/* 327 */       if (this._baseTypeMatchers == null) {
/* 328 */         this._baseTypeMatchers = new ArrayList<>();
/*     */       }
/* 330 */       this._baseTypeMatchers.add(matcher);
/* 331 */       return this;
/*     */     }
/*     */     
/*     */     protected Builder _appendSubNameMatcher(BasicPolymorphicTypeValidator.NameMatcher matcher) {
/* 335 */       if (this._subTypeNameMatchers == null) {
/* 336 */         this._subTypeNameMatchers = new ArrayList<>();
/*     */       }
/* 338 */       this._subTypeNameMatchers.add(matcher);
/* 339 */       return this;
/*     */     }
/*     */     
/*     */     protected Builder _appendSubClassMatcher(BasicPolymorphicTypeValidator.TypeMatcher matcher) {
/* 343 */       if (this._subTypeClassMatchers == null) {
/* 344 */         this._subTypeClassMatchers = new ArrayList<>();
/*     */       }
/* 346 */       this._subTypeClassMatchers.add(matcher);
/* 347 */       return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BasicPolymorphicTypeValidator(Set<Class<?>> invalidBaseTypes, TypeMatcher[] baseTypeMatchers, NameMatcher[] subTypeNameMatchers, TypeMatcher[] subClassMatchers) {
/* 385 */     this._invalidBaseTypes = invalidBaseTypes;
/* 386 */     this._baseTypeMatchers = baseTypeMatchers;
/* 387 */     this._subTypeNameMatchers = subTypeNameMatchers;
/* 388 */     this._subClassMatchers = subClassMatchers;
/*     */   }
/*     */   
/*     */   public static Builder builder() {
/* 392 */     return new Builder();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public PolymorphicTypeValidator.Validity validateBaseType(MapperConfig<?> ctxt, JavaType baseType) {
/* 398 */     Class<?> rawBase = baseType.getRawClass();
/* 399 */     if (this._invalidBaseTypes != null && 
/* 400 */       this._invalidBaseTypes.contains(rawBase)) {
/* 401 */       return PolymorphicTypeValidator.Validity.DENIED;
/*     */     }
/*     */     
/* 404 */     if (this._baseTypeMatchers != null) {
/* 405 */       for (TypeMatcher m : this._baseTypeMatchers) {
/* 406 */         if (m.match(ctxt, rawBase)) {
/* 407 */           return PolymorphicTypeValidator.Validity.ALLOWED;
/*     */         }
/*     */       } 
/*     */     }
/* 411 */     return PolymorphicTypeValidator.Validity.INDETERMINATE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PolymorphicTypeValidator.Validity validateSubClassName(MapperConfig<?> ctxt, JavaType baseType, String subClassName) throws JsonMappingException {
/* 420 */     if (this._subTypeNameMatchers != null) {
/* 421 */       for (NameMatcher m : this._subTypeNameMatchers) {
/* 422 */         if (m.match(ctxt, subClassName)) {
/* 423 */           return PolymorphicTypeValidator.Validity.ALLOWED;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 428 */     return PolymorphicTypeValidator.Validity.INDETERMINATE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PolymorphicTypeValidator.Validity validateSubType(MapperConfig<?> ctxt, JavaType baseType, JavaType subType) throws JsonMappingException {
/* 436 */     if (this._subClassMatchers != null) {
/* 437 */       Class<?> subClass = subType.getRawClass();
/* 438 */       for (TypeMatcher m : this._subClassMatchers) {
/* 439 */         if (m.match(ctxt, subClass)) {
/* 440 */           return PolymorphicTypeValidator.Validity.ALLOWED;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 445 */     return PolymorphicTypeValidator.Validity.INDETERMINATE;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jsontype/BasicPolymorphicTypeValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */