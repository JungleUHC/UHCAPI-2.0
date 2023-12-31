/*     */ package com.fasterxml.jackson.databind.ext;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.type.WritableTypeId;
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializationConfig;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.ContextualSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.Serializers;
/*     */ import com.fasterxml.jackson.databind.ser.std.CalendarSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.std.StdSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
/*     */ import java.io.IOException;
/*     */ import java.util.Calendar;
/*     */ import javax.xml.datatype.Duration;
/*     */ import javax.xml.datatype.XMLGregorianCalendar;
/*     */ import javax.xml.namespace.QName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CoreXMLSerializers
/*     */   extends Serializers.Base
/*     */ {
/*     */   public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
/*  36 */     Class<?> raw = type.getRawClass();
/*  37 */     if (Duration.class.isAssignableFrom(raw) || QName.class.isAssignableFrom(raw)) {
/*  38 */       return (JsonSerializer<?>)ToStringSerializer.instance;
/*     */     }
/*  40 */     if (XMLGregorianCalendar.class.isAssignableFrom(raw)) {
/*  41 */       return (JsonSerializer<?>)XMLGregorianCalendarSerializer.instance;
/*     */     }
/*  43 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public static class XMLGregorianCalendarSerializer
/*     */     extends StdSerializer<XMLGregorianCalendar>
/*     */     implements ContextualSerializer
/*     */   {
/*  51 */     static final XMLGregorianCalendarSerializer instance = new XMLGregorianCalendarSerializer();
/*     */     
/*     */     final JsonSerializer<Object> _delegate;
/*     */     
/*     */     public XMLGregorianCalendarSerializer() {
/*  56 */       this((JsonSerializer<?>)CalendarSerializer.instance);
/*     */     }
/*     */ 
/*     */     
/*     */     protected XMLGregorianCalendarSerializer(JsonSerializer<?> del) {
/*  61 */       super(XMLGregorianCalendar.class);
/*  62 */       this._delegate = (JsonSerializer)del;
/*     */     }
/*     */ 
/*     */     
/*     */     public JsonSerializer<?> getDelegatee() {
/*  67 */       return this._delegate;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty(SerializerProvider provider, XMLGregorianCalendar value) {
/*  72 */       return this._delegate.isEmpty(provider, _convert(value));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void serialize(XMLGregorianCalendar value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/*  78 */       this._delegate.serialize(_convert(value), gen, provider);
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
/*     */     public void serializeWithType(XMLGregorianCalendar value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/*  92 */       WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer
/*     */           
/*  94 */           .typeId(value, XMLGregorianCalendar.class, JsonToken.VALUE_STRING));
/*     */       
/*  96 */       serialize(value, g, provider);
/*  97 */       typeSer.writeTypeSuffix(g, typeIdDef);
/*     */     }
/*     */ 
/*     */     
/*     */     public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 102 */       this._delegate.acceptJsonFormatVisitor(visitor, null);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
/* 108 */       JsonSerializer<?> ser = prov.handlePrimaryContextualization(this._delegate, property);
/* 109 */       if (ser != this._delegate) {
/* 110 */         return (JsonSerializer<?>)new XMLGregorianCalendarSerializer(ser);
/*     */       }
/* 112 */       return (JsonSerializer<?>)this;
/*     */     }
/*     */     
/*     */     protected Calendar _convert(XMLGregorianCalendar input) {
/* 116 */       return (input == null) ? null : input.toGregorianCalendar();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ext/CoreXMLSerializers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */