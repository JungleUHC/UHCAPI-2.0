/*     */ package com.fasterxml.jackson.databind.introspect;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonAutoDetect;
/*     */ import com.fasterxml.jackson.annotation.PropertyAccessor;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface VisibilityChecker<T extends VisibilityChecker<T>>
/*     */ {
/*     */   T with(JsonAutoDetect paramJsonAutoDetect);
/*     */   
/*     */   T withOverrides(JsonAutoDetect.Value paramValue);
/*     */   
/*     */   T with(JsonAutoDetect.Visibility paramVisibility);
/*     */   
/*     */   T withVisibility(PropertyAccessor paramPropertyAccessor, JsonAutoDetect.Visibility paramVisibility);
/*     */   
/*     */   T withGetterVisibility(JsonAutoDetect.Visibility paramVisibility);
/*     */   
/*     */   T withIsGetterVisibility(JsonAutoDetect.Visibility paramVisibility);
/*     */   
/*     */   T withSetterVisibility(JsonAutoDetect.Visibility paramVisibility);
/*     */   
/*     */   T withCreatorVisibility(JsonAutoDetect.Visibility paramVisibility);
/*     */   
/*     */   T withFieldVisibility(JsonAutoDetect.Visibility paramVisibility);
/*     */   
/*     */   boolean isGetterVisible(Method paramMethod);
/*     */   
/*     */   boolean isGetterVisible(AnnotatedMethod paramAnnotatedMethod);
/*     */   
/*     */   boolean isIsGetterVisible(Method paramMethod);
/*     */   
/*     */   boolean isIsGetterVisible(AnnotatedMethod paramAnnotatedMethod);
/*     */   
/*     */   boolean isSetterVisible(Method paramMethod);
/*     */   
/*     */   boolean isSetterVisible(AnnotatedMethod paramAnnotatedMethod);
/*     */   
/*     */   boolean isCreatorVisible(Member paramMember);
/*     */   
/*     */   boolean isCreatorVisible(AnnotatedMember paramAnnotatedMember);
/*     */   
/*     */   boolean isFieldVisible(Field paramField);
/*     */   
/*     */   boolean isFieldVisible(AnnotatedField paramAnnotatedField);
/*     */   
/*     */   public static class Std
/*     */     implements VisibilityChecker<Std>, Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/* 164 */     protected static final Std DEFAULT = new Std(JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.ANY, JsonAutoDetect.Visibility.ANY, JsonAutoDetect.Visibility.PUBLIC_ONLY);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 178 */     protected static final Std ALL_PUBLIC = new Std(JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.PUBLIC_ONLY);
/*     */ 
/*     */     
/*     */     protected final JsonAutoDetect.Visibility _getterMinLevel;
/*     */ 
/*     */     
/*     */     protected final JsonAutoDetect.Visibility _isGetterMinLevel;
/*     */ 
/*     */     
/*     */     protected final JsonAutoDetect.Visibility _setterMinLevel;
/*     */ 
/*     */     
/*     */     protected final JsonAutoDetect.Visibility _creatorMinLevel;
/*     */     
/*     */     protected final JsonAutoDetect.Visibility _fieldMinLevel;
/*     */ 
/*     */     
/*     */     public static Std defaultInstance() {
/* 196 */       return DEFAULT;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Std allPublicInstance() {
/* 203 */       return ALL_PUBLIC;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Std(JsonAutoDetect ann) {
/* 214 */       this._getterMinLevel = ann.getterVisibility();
/* 215 */       this._isGetterMinLevel = ann.isGetterVisibility();
/* 216 */       this._setterMinLevel = ann.setterVisibility();
/* 217 */       this._creatorMinLevel = ann.creatorVisibility();
/* 218 */       this._fieldMinLevel = ann.fieldVisibility();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Std(JsonAutoDetect.Visibility getter, JsonAutoDetect.Visibility isGetter, JsonAutoDetect.Visibility setter, JsonAutoDetect.Visibility creator, JsonAutoDetect.Visibility field) {
/* 227 */       this._getterMinLevel = getter;
/* 228 */       this._isGetterMinLevel = isGetter;
/* 229 */       this._setterMinLevel = setter;
/* 230 */       this._creatorMinLevel = creator;
/* 231 */       this._fieldMinLevel = field;
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
/*     */     public Std(JsonAutoDetect.Visibility v) {
/* 243 */       if (v == JsonAutoDetect.Visibility.DEFAULT) {
/* 244 */         this._getterMinLevel = DEFAULT._getterMinLevel;
/* 245 */         this._isGetterMinLevel = DEFAULT._isGetterMinLevel;
/* 246 */         this._setterMinLevel = DEFAULT._setterMinLevel;
/* 247 */         this._creatorMinLevel = DEFAULT._creatorMinLevel;
/* 248 */         this._fieldMinLevel = DEFAULT._fieldMinLevel;
/*     */       } else {
/* 250 */         this._getterMinLevel = v;
/* 251 */         this._isGetterMinLevel = v;
/* 252 */         this._setterMinLevel = v;
/* 253 */         this._creatorMinLevel = v;
/* 254 */         this._fieldMinLevel = v;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Std construct(JsonAutoDetect.Value vis) {
/* 262 */       return DEFAULT.withOverrides(vis);
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
/*     */     protected Std _with(JsonAutoDetect.Visibility g, JsonAutoDetect.Visibility isG, JsonAutoDetect.Visibility s, JsonAutoDetect.Visibility cr, JsonAutoDetect.Visibility f) {
/* 274 */       if (g == this._getterMinLevel && isG == this._isGetterMinLevel && s == this._setterMinLevel && cr == this._creatorMinLevel && f == this._fieldMinLevel)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 280 */         return this;
/*     */       }
/* 282 */       return new Std(g, isG, s, cr, f);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Std with(JsonAutoDetect ann) {
/* 288 */       Std curr = this;
/* 289 */       if (ann != null) {
/* 290 */         return _with(
/* 291 */             _defaultOrOverride(this._getterMinLevel, ann.getterVisibility()), 
/* 292 */             _defaultOrOverride(this._isGetterMinLevel, ann.isGetterVisibility()), 
/* 293 */             _defaultOrOverride(this._setterMinLevel, ann.setterVisibility()), 
/* 294 */             _defaultOrOverride(this._creatorMinLevel, ann.creatorVisibility()), 
/* 295 */             _defaultOrOverride(this._fieldMinLevel, ann.fieldVisibility()));
/*     */       }
/*     */       
/* 298 */       return curr;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Std withOverrides(JsonAutoDetect.Value vis) {
/* 304 */       Std curr = this;
/* 305 */       if (vis != null) {
/* 306 */         return _with(
/* 307 */             _defaultOrOverride(this._getterMinLevel, vis.getGetterVisibility()), 
/* 308 */             _defaultOrOverride(this._isGetterMinLevel, vis.getIsGetterVisibility()), 
/* 309 */             _defaultOrOverride(this._setterMinLevel, vis.getSetterVisibility()), 
/* 310 */             _defaultOrOverride(this._creatorMinLevel, vis.getCreatorVisibility()), 
/* 311 */             _defaultOrOverride(this._fieldMinLevel, vis.getFieldVisibility()));
/*     */       }
/*     */       
/* 314 */       return curr;
/*     */     }
/*     */     
/*     */     private JsonAutoDetect.Visibility _defaultOrOverride(JsonAutoDetect.Visibility defaults, JsonAutoDetect.Visibility override) {
/* 318 */       if (override == JsonAutoDetect.Visibility.DEFAULT) {
/* 319 */         return defaults;
/*     */       }
/* 321 */       return override;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Std with(JsonAutoDetect.Visibility v) {
/* 327 */       if (v == JsonAutoDetect.Visibility.DEFAULT) {
/* 328 */         return DEFAULT;
/*     */       }
/* 330 */       return new Std(v);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Std withVisibility(PropertyAccessor method, JsonAutoDetect.Visibility v) {
/* 336 */       switch (method) {
/*     */         case GETTER:
/* 338 */           return withGetterVisibility(v);
/*     */         case SETTER:
/* 340 */           return withSetterVisibility(v);
/*     */         case CREATOR:
/* 342 */           return withCreatorVisibility(v);
/*     */         case FIELD:
/* 344 */           return withFieldVisibility(v);
/*     */         case IS_GETTER:
/* 346 */           return withIsGetterVisibility(v);
/*     */         case ALL:
/* 348 */           return with(v);
/*     */       } 
/*     */ 
/*     */       
/* 352 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Std withGetterVisibility(JsonAutoDetect.Visibility v) {
/* 358 */       if (v == JsonAutoDetect.Visibility.DEFAULT) v = DEFAULT._getterMinLevel; 
/* 359 */       if (this._getterMinLevel == v) return this; 
/* 360 */       return new Std(v, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel);
/*     */     }
/*     */ 
/*     */     
/*     */     public Std withIsGetterVisibility(JsonAutoDetect.Visibility v) {
/* 365 */       if (v == JsonAutoDetect.Visibility.DEFAULT) v = DEFAULT._isGetterMinLevel; 
/* 366 */       if (this._isGetterMinLevel == v) return this; 
/* 367 */       return new Std(this._getterMinLevel, v, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel);
/*     */     }
/*     */ 
/*     */     
/*     */     public Std withSetterVisibility(JsonAutoDetect.Visibility v) {
/* 372 */       if (v == JsonAutoDetect.Visibility.DEFAULT) v = DEFAULT._setterMinLevel; 
/* 373 */       if (this._setterMinLevel == v) return this; 
/* 374 */       return new Std(this._getterMinLevel, this._isGetterMinLevel, v, this._creatorMinLevel, this._fieldMinLevel);
/*     */     }
/*     */ 
/*     */     
/*     */     public Std withCreatorVisibility(JsonAutoDetect.Visibility v) {
/* 379 */       if (v == JsonAutoDetect.Visibility.DEFAULT) v = DEFAULT._creatorMinLevel; 
/* 380 */       if (this._creatorMinLevel == v) return this; 
/* 381 */       return new Std(this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, v, this._fieldMinLevel);
/*     */     }
/*     */ 
/*     */     
/*     */     public Std withFieldVisibility(JsonAutoDetect.Visibility v) {
/* 386 */       if (v == JsonAutoDetect.Visibility.DEFAULT) v = DEFAULT._fieldMinLevel; 
/* 387 */       if (this._fieldMinLevel == v) return this; 
/* 388 */       return new Std(this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, v);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isCreatorVisible(Member m) {
/* 399 */       return this._creatorMinLevel.isVisible(m);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isCreatorVisible(AnnotatedMember m) {
/* 404 */       return isCreatorVisible(m.getMember());
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isFieldVisible(Field f) {
/* 409 */       return this._fieldMinLevel.isVisible(f);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isFieldVisible(AnnotatedField f) {
/* 414 */       return isFieldVisible(f.getAnnotated());
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isGetterVisible(Method m) {
/* 419 */       return this._getterMinLevel.isVisible(m);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isGetterVisible(AnnotatedMethod m) {
/* 424 */       return isGetterVisible(m.getAnnotated());
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isIsGetterVisible(Method m) {
/* 429 */       return this._isGetterMinLevel.isVisible(m);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isIsGetterVisible(AnnotatedMethod m) {
/* 434 */       return isIsGetterVisible(m.getAnnotated());
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isSetterVisible(Method m) {
/* 439 */       return this._setterMinLevel.isVisible(m);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isSetterVisible(AnnotatedMethod m) {
/* 444 */       return isSetterVisible(m.getAnnotated());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/* 455 */       return String.format("[Visibility: getter=%s,isGetter=%s,setter=%s,creator=%s,field=%s]", new Object[] { this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel });
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/VisibilityChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */