/*     */ package com.fasterxml.jackson.databind.introspect;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.jdk14.JDK14Util;
/*     */ import java.io.Serializable;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultAccessorNamingStrategy
/*     */   extends AccessorNamingStrategy
/*     */ {
/*     */   protected final MapperConfig<?> _config;
/*     */   protected final AnnotatedClass _forClass;
/*     */   protected final BaseNameValidator _baseNameValidator;
/*     */   protected final boolean _stdBeanNaming;
/*     */   protected final String _getterPrefix;
/*     */   protected final String _isGetterPrefix;
/*     */   protected final String _mutatorPrefix;
/*     */   
/*     */   protected DefaultAccessorNamingStrategy(MapperConfig<?> config, AnnotatedClass forClass, String mutatorPrefix, String getterPrefix, String isGetterPrefix, BaseNameValidator baseNameValidator) {
/*  56 */     this._config = config;
/*  57 */     this._forClass = forClass;
/*     */     
/*  59 */     this._stdBeanNaming = config.isEnabled(MapperFeature.USE_STD_BEAN_NAMING);
/*  60 */     this._mutatorPrefix = mutatorPrefix;
/*  61 */     this._getterPrefix = getterPrefix;
/*  62 */     this._isGetterPrefix = isGetterPrefix;
/*  63 */     this._baseNameValidator = baseNameValidator;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String findNameForIsGetter(AnnotatedMethod am, String name) {
/*  69 */     if (this._isGetterPrefix != null) {
/*  70 */       Class<?> rt = am.getRawType();
/*  71 */       if ((rt == Boolean.class || rt == boolean.class) && 
/*  72 */         name.startsWith(this._isGetterPrefix)) {
/*  73 */         return this._stdBeanNaming ? 
/*  74 */           stdManglePropertyName(name, 2) : 
/*  75 */           legacyManglePropertyName(name, 2);
/*     */       }
/*     */     } 
/*     */     
/*  79 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String findNameForRegularGetter(AnnotatedMethod am, String name) {
/*  85 */     if (this._getterPrefix != null && name.startsWith(this._getterPrefix)) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  90 */       if ("getCallbacks".equals(name)) {
/*  91 */         if (_isCglibGetCallbacks(am)) {
/*  92 */           return null;
/*     */         }
/*  94 */       } else if ("getMetaClass".equals(name)) {
/*     */         
/*  96 */         if (_isGroovyMetaClassGetter(am)) {
/*  97 */           return null;
/*     */         }
/*     */       } 
/* 100 */       return this._stdBeanNaming ? 
/* 101 */         stdManglePropertyName(name, this._getterPrefix.length()) : 
/* 102 */         legacyManglePropertyName(name, this._getterPrefix.length());
/*     */     } 
/* 104 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String findNameForMutator(AnnotatedMethod am, String name) {
/* 110 */     if (this._mutatorPrefix != null && name.startsWith(this._mutatorPrefix)) {
/* 111 */       return this._stdBeanNaming ? 
/* 112 */         stdManglePropertyName(name, this._mutatorPrefix.length()) : 
/* 113 */         legacyManglePropertyName(name, this._mutatorPrefix.length());
/*     */     }
/* 115 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String modifyFieldName(AnnotatedField field, String name) {
/* 121 */     return name;
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
/*     */   protected String legacyManglePropertyName(String basename, int offset) {
/* 139 */     int end = basename.length();
/* 140 */     if (end == offset) {
/* 141 */       return null;
/*     */     }
/* 143 */     char c = basename.charAt(offset);
/*     */ 
/*     */     
/* 146 */     if (this._baseNameValidator != null && 
/* 147 */       !this._baseNameValidator.accept(c, basename, offset)) {
/* 148 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 153 */     char d = Character.toLowerCase(c);
/*     */     
/* 155 */     if (c == d) {
/* 156 */       return basename.substring(offset);
/*     */     }
/*     */     
/* 159 */     StringBuilder sb = new StringBuilder(end - offset);
/* 160 */     sb.append(d);
/* 161 */     int i = offset + 1;
/* 162 */     for (; i < end; i++) {
/* 163 */       c = basename.charAt(i);
/* 164 */       d = Character.toLowerCase(c);
/* 165 */       if (c == d) {
/* 166 */         sb.append(basename, i, end);
/*     */         break;
/*     */       } 
/* 169 */       sb.append(d);
/*     */     } 
/* 171 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   protected String stdManglePropertyName(String basename, int offset) {
/* 176 */     int end = basename.length();
/* 177 */     if (end == offset) {
/* 178 */       return null;
/*     */     }
/*     */     
/* 181 */     char c0 = basename.charAt(offset);
/*     */ 
/*     */     
/* 184 */     if (this._baseNameValidator != null && 
/* 185 */       !this._baseNameValidator.accept(c0, basename, offset)) {
/* 186 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 190 */     char c1 = Character.toLowerCase(c0);
/* 191 */     if (c0 == c1) {
/* 192 */       return basename.substring(offset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 197 */     if (offset + 1 < end && 
/* 198 */       Character.isUpperCase(basename.charAt(offset + 1))) {
/* 199 */       return basename.substring(offset);
/*     */     }
/*     */     
/* 202 */     StringBuilder sb = new StringBuilder(end - offset);
/* 203 */     sb.append(c1);
/* 204 */     sb.append(basename, offset + 1, end);
/* 205 */     return sb.toString();
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
/*     */   protected boolean _isCglibGetCallbacks(AnnotatedMethod am) {
/* 221 */     Class<?> rt = am.getRawType();
/*     */     
/* 223 */     if (rt.isArray()) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 228 */       Class<?> compType = rt.getComponentType();
/*     */       
/* 230 */       String className = compType.getName();
/* 231 */       if (className.contains(".cglib")) {
/* 232 */         return (className.startsWith("net.sf.cglib") || className
/*     */           
/* 234 */           .startsWith("org.hibernate.repackage.cglib") || className
/*     */           
/* 236 */           .startsWith("org.springframework.cglib"));
/*     */       }
/*     */     } 
/* 239 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean _isGroovyMetaClassGetter(AnnotatedMethod am) {
/* 244 */     return am.getRawType().getName().startsWith("groovy.lang");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface BaseNameValidator
/*     */   {
/*     */     boolean accept(char param1Char, String param1String, int param1Int);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Provider
/*     */     extends AccessorNamingStrategy.Provider
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */     
/*     */     protected final String _setterPrefix;
/*     */ 
/*     */ 
/*     */     
/*     */     protected final String _withPrefix;
/*     */ 
/*     */ 
/*     */     
/*     */     protected final String _getterPrefix;
/*     */ 
/*     */ 
/*     */     
/*     */     protected final String _isGetterPrefix;
/*     */ 
/*     */     
/*     */     protected final DefaultAccessorNamingStrategy.BaseNameValidator _baseNameValidator;
/*     */ 
/*     */ 
/*     */     
/*     */     public Provider() {
/* 286 */       this("set", "with", "get", "is", (DefaultAccessorNamingStrategy.BaseNameValidator)null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected Provider(Provider p, String setterPrefix, String withPrefix, String getterPrefix, String isGetterPrefix) {
/* 294 */       this(setterPrefix, withPrefix, getterPrefix, isGetterPrefix, p._baseNameValidator);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected Provider(Provider p, DefaultAccessorNamingStrategy.BaseNameValidator vld) {
/* 300 */       this(p._setterPrefix, p._withPrefix, p._getterPrefix, p._isGetterPrefix, vld);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected Provider(String setterPrefix, String withPrefix, String getterPrefix, String isGetterPrefix, DefaultAccessorNamingStrategy.BaseNameValidator vld) {
/* 308 */       this._setterPrefix = setterPrefix;
/* 309 */       this._withPrefix = withPrefix;
/* 310 */       this._getterPrefix = getterPrefix;
/* 311 */       this._isGetterPrefix = isGetterPrefix;
/* 312 */       this._baseNameValidator = vld;
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
/*     */     public Provider withSetterPrefix(String prefix) {
/* 327 */       return new Provider(this, prefix, this._withPrefix, this._getterPrefix, this._isGetterPrefix);
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
/*     */     public Provider withBuilderPrefix(String prefix) {
/* 343 */       return new Provider(this, this._setterPrefix, prefix, this._getterPrefix, this._isGetterPrefix);
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
/*     */     public Provider withGetterPrefix(String prefix) {
/* 359 */       return new Provider(this, this._setterPrefix, this._withPrefix, prefix, this._isGetterPrefix);
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
/*     */     public Provider withIsGetterPrefix(String prefix) {
/* 375 */       return new Provider(this, this._setterPrefix, this._withPrefix, this._getterPrefix, prefix);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Provider withFirstCharAcceptance(boolean allowLowerCaseFirstChar, boolean allowNonLetterFirstChar) {
/* 407 */       return withBaseNameValidator(
/* 408 */           DefaultAccessorNamingStrategy.FirstCharBasedValidator.forFirstNameRule(allowLowerCaseFirstChar, allowNonLetterFirstChar));
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
/*     */     public Provider withBaseNameValidator(DefaultAccessorNamingStrategy.BaseNameValidator vld) {
/* 422 */       return new Provider(this, vld);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public AccessorNamingStrategy forPOJO(MapperConfig<?> config, AnnotatedClass targetClass) {
/* 428 */       return new DefaultAccessorNamingStrategy(config, targetClass, this._setterPrefix, this._getterPrefix, this._isGetterPrefix, this._baseNameValidator);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public AccessorNamingStrategy forBuilder(MapperConfig<?> config, AnnotatedClass builderClass, BeanDescription valueTypeDesc) {
/* 437 */       AnnotationIntrospector ai = config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null;
/* 438 */       JsonPOJOBuilder.Value builderConfig = (ai == null) ? null : ai.findPOJOBuilderConfig(builderClass);
/* 439 */       String mutatorPrefix = (builderConfig == null) ? this._withPrefix : builderConfig.withPrefix;
/* 440 */       return new DefaultAccessorNamingStrategy(config, builderClass, mutatorPrefix, this._getterPrefix, this._isGetterPrefix, this._baseNameValidator);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public AccessorNamingStrategy forRecord(MapperConfig<?> config, AnnotatedClass recordClass) {
/* 448 */       return new DefaultAccessorNamingStrategy.RecordNaming(config, recordClass);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class FirstCharBasedValidator
/*     */     implements BaseNameValidator
/*     */   {
/*     */     private final boolean _allowLowerCaseFirstChar;
/*     */ 
/*     */ 
/*     */     
/*     */     private final boolean _allowNonLetterFirstChar;
/*     */ 
/*     */ 
/*     */     
/*     */     protected FirstCharBasedValidator(boolean allowLowerCaseFirstChar, boolean allowNonLetterFirstChar) {
/* 467 */       this._allowLowerCaseFirstChar = allowLowerCaseFirstChar;
/* 468 */       this._allowNonLetterFirstChar = allowNonLetterFirstChar;
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
/*     */     public static DefaultAccessorNamingStrategy.BaseNameValidator forFirstNameRule(boolean allowLowerCaseFirstChar, boolean allowNonLetterFirstChar) {
/* 487 */       if (!allowLowerCaseFirstChar && !allowNonLetterFirstChar) {
/* 488 */         return null;
/*     */       }
/* 490 */       return new FirstCharBasedValidator(allowLowerCaseFirstChar, allowNonLetterFirstChar);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean accept(char firstChar, String basename, int offset) {
/* 498 */       if (Character.isLetter(firstChar)) {
/* 499 */         return (this._allowLowerCaseFirstChar || !Character.isLowerCase(firstChar));
/*     */       }
/*     */       
/* 502 */       return this._allowNonLetterFirstChar;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class RecordNaming
/*     */     extends DefaultAccessorNamingStrategy
/*     */   {
/*     */     protected final Set<String> _fieldNames;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public RecordNaming(MapperConfig<?> config, AnnotatedClass forClass) {
/* 524 */       super(config, forClass, null, "get", "is", null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 530 */       this._fieldNames = new HashSet<>();
/* 531 */       for (String name : JDK14Util.getRecordFieldNames(forClass.getRawType())) {
/* 532 */         this._fieldNames.add(name);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String findNameForRegularGetter(AnnotatedMethod am, String name) {
/* 542 */       if (this._fieldNames.contains(name)) {
/* 543 */         return name;
/*     */       }
/*     */       
/* 546 */       return super.findNameForRegularGetter(am, name);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/DefaultAccessorNamingStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */