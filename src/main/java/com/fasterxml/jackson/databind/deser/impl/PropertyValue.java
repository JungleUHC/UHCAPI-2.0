/*     */ package com.fasterxml.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.deser.SettableAnyProperty;
/*     */ import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
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
/*     */ public abstract class PropertyValue
/*     */ {
/*     */   public final PropertyValue next;
/*     */   public final Object value;
/*     */   
/*     */   protected PropertyValue(PropertyValue next, Object value) {
/*  23 */     this.next = next;
/*  24 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void assign(Object paramObject) throws IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final class Regular
/*     */     extends PropertyValue
/*     */   {
/*     */     final SettableBeanProperty _property;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Regular(PropertyValue next, Object value, SettableBeanProperty prop) {
/*  52 */       super(next, value);
/*  53 */       this._property = prop;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void assign(Object bean) throws IOException {
/*  60 */       this._property.set(bean, this.value);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final class Any
/*     */     extends PropertyValue
/*     */   {
/*     */     final SettableAnyProperty _property;
/*     */ 
/*     */ 
/*     */     
/*     */     final String _propertyName;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Any(PropertyValue next, Object value, SettableAnyProperty prop, String propName) {
/*  80 */       super(next, value);
/*  81 */       this._property = prop;
/*  82 */       this._propertyName = propName;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void assign(Object bean) throws IOException {
/*  89 */       this._property.set(bean, this._propertyName, this.value);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final class Map
/*     */     extends PropertyValue
/*     */   {
/*     */     final Object _key;
/*     */ 
/*     */ 
/*     */     
/*     */     public Map(PropertyValue next, Object value, Object key) {
/* 104 */       super(next, value);
/* 105 */       this._key = key;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void assign(Object bean) throws IOException {
/* 113 */       ((java.util.Map<Object, Object>)bean).put(this._key, this.value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/impl/PropertyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */