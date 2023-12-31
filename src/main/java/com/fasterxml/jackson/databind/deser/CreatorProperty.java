/*     */ package com.fasterxml.jackson.databind.deser;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JacksonInject;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.PropertyMetadata;
/*     */ import com.fasterxml.jackson.databind.PropertyName;
/*     */ import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.util.Annotations;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CreatorProperty
/*     */   extends SettableBeanProperty
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final AnnotatedParameter _annotated;
/*     */   protected final JacksonInject.Value _injectableValue;
/*     */   protected SettableBeanProperty _fallbackSetter;
/*     */   protected final int _creatorIndex;
/*     */   protected boolean _ignorable;
/*     */   
/*     */   protected CreatorProperty(PropertyName name, JavaType type, PropertyName wrapperName, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedParameter param, int index, JacksonInject.Value injectable, PropertyMetadata metadata) {
/*  87 */     super(name, type, wrapperName, typeDeser, contextAnnotations, metadata);
/*  88 */     this._annotated = param;
/*  89 */     this._creatorIndex = index;
/*  90 */     this._injectableValue = injectable;
/*  91 */     this._fallbackSetter = null;
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
/*     */   @Deprecated
/*     */   public CreatorProperty(PropertyName name, JavaType type, PropertyName wrapperName, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedParameter param, int index, Object injectableValueId, PropertyMetadata metadata) {
/* 104 */     this(name, type, wrapperName, typeDeser, contextAnnotations, param, index, (injectableValueId == null) ? null : 
/*     */         
/* 106 */         JacksonInject.Value.construct(injectableValueId, null), metadata);
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
/*     */   public static CreatorProperty construct(PropertyName name, JavaType type, PropertyName wrapperName, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedParameter param, int index, JacksonInject.Value injectable, PropertyMetadata metadata) {
/* 134 */     return new CreatorProperty(name, type, wrapperName, typeDeser, contextAnnotations, param, index, injectable, metadata);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CreatorProperty(CreatorProperty src, PropertyName newName) {
/* 142 */     super(src, newName);
/* 143 */     this._annotated = src._annotated;
/* 144 */     this._injectableValue = src._injectableValue;
/* 145 */     this._fallbackSetter = src._fallbackSetter;
/* 146 */     this._creatorIndex = src._creatorIndex;
/* 147 */     this._ignorable = src._ignorable;
/*     */   }
/*     */ 
/*     */   
/*     */   protected CreatorProperty(CreatorProperty src, JsonDeserializer<?> deser, NullValueProvider nva) {
/* 152 */     super(src, deser, nva);
/* 153 */     this._annotated = src._annotated;
/* 154 */     this._injectableValue = src._injectableValue;
/* 155 */     this._fallbackSetter = src._fallbackSetter;
/* 156 */     this._creatorIndex = src._creatorIndex;
/* 157 */     this._ignorable = src._ignorable;
/*     */   }
/*     */ 
/*     */   
/*     */   public SettableBeanProperty withName(PropertyName newName) {
/* 162 */     return new CreatorProperty(this, newName);
/*     */   }
/*     */ 
/*     */   
/*     */   public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
/* 167 */     if (this._valueDeserializer == deser) {
/* 168 */       return this;
/*     */     }
/*     */     
/* 171 */     NullValueProvider nvp = (this._valueDeserializer == this._nullProvider) ? (NullValueProvider)deser : this._nullProvider;
/* 172 */     return new CreatorProperty(this, deser, nvp);
/*     */   }
/*     */ 
/*     */   
/*     */   public SettableBeanProperty withNullProvider(NullValueProvider nva) {
/* 177 */     return new CreatorProperty(this, this._valueDeserializer, nva);
/*     */   }
/*     */ 
/*     */   
/*     */   public void fixAccess(DeserializationConfig config) {
/* 182 */     if (this._fallbackSetter != null) {
/* 183 */       this._fallbackSetter.fixAccess(config);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFallbackSetter(SettableBeanProperty fallbackSetter) {
/* 194 */     this._fallbackSetter = fallbackSetter;
/*     */   }
/*     */ 
/*     */   
/*     */   public void markAsIgnorable() {
/* 199 */     this._ignorable = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isIgnorable() {
/* 204 */     return this._ignorable;
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
/*     */   @Deprecated
/*     */   public Object findInjectableValue(DeserializationContext context, Object beanInstance) throws JsonMappingException {
/* 222 */     if (this._injectableValue == null)
/* 223 */       context.reportBadDefinition(ClassUtil.classOf(beanInstance), 
/* 224 */           String.format("Property %s (type %s) has no injectable value id configured", new Object[] {
/* 225 */               ClassUtil.name(getName()), ClassUtil.classNameOf(this)
/*     */             })); 
/* 227 */     return context.findInjectableValue(this._injectableValue.getId(), (BeanProperty)this, beanInstance);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void inject(DeserializationContext context, Object beanInstance) throws IOException {
/* 237 */     set(beanInstance, findInjectableValue(context, beanInstance));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <A extends java.lang.annotation.Annotation> A getAnnotation(Class<A> acls) {
/* 248 */     if (this._annotated == null) {
/* 249 */       return null;
/*     */     }
/* 251 */     return (A)this._annotated.getAnnotation(acls);
/*     */   }
/*     */   public AnnotatedMember getMember() {
/* 254 */     return (AnnotatedMember)this._annotated;
/*     */   }
/*     */   public int getCreatorIndex() {
/* 257 */     return this._creatorIndex;
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
/*     */   public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
/* 270 */     _verifySetter();
/* 271 */     this._fallbackSetter.set(instance, deserialize(p, ctxt));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
/* 278 */     _verifySetter();
/* 279 */     return this._fallbackSetter.setAndReturn(instance, deserialize(p, ctxt));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(Object instance, Object value) throws IOException {
/* 285 */     _verifySetter();
/* 286 */     this._fallbackSetter.set(instance, value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object setAndReturn(Object instance, Object value) throws IOException {
/* 292 */     _verifySetter();
/* 293 */     return this._fallbackSetter.setAndReturn(instance, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PropertyMetadata getMetadata() {
/* 303 */     PropertyMetadata md = super.getMetadata();
/* 304 */     if (this._fallbackSetter != null) {
/* 305 */       return md.withMergeInfo(this._fallbackSetter.getMetadata().getMergeInfo());
/*     */     }
/* 307 */     return md;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getInjectableValueId() {
/* 313 */     return (this._injectableValue == null) ? null : this._injectableValue.getId();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInjectionOnly() {
/* 318 */     return (this._injectableValue != null && !this._injectableValue.willUseInput(true));
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
/* 330 */     return "[creator property, name " + ClassUtil.name(getName()) + "; inject id '" + getInjectableValueId() + "']";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final void _verifySetter() throws IOException {
/* 340 */     if (this._fallbackSetter == null) {
/* 341 */       _reportMissingSetter((JsonParser)null, (DeserializationContext)null);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void _reportMissingSetter(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 348 */     String msg = "No fallback setter/field defined for creator property " + ClassUtil.name(getName());
/*     */ 
/*     */     
/* 351 */     if (ctxt != null) {
/* 352 */       ctxt.reportBadDefinition(getType(), msg);
/*     */     } else {
/* 354 */       throw InvalidDefinitionException.from(p, msg, getType());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/CreatorProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */