/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.PrettyPrinter;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MinimalPrettyPrinter
/*     */   implements PrettyPrinter, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected String _rootValueSeparator;
/*     */   protected Separators _separators;
/*     */   
/*     */   public MinimalPrettyPrinter() {
/*  44 */     this(DEFAULT_ROOT_VALUE_SEPARATOR.toString());
/*     */   }
/*     */   
/*     */   public MinimalPrettyPrinter(String rootValueSeparator) {
/*  48 */     this._rootValueSeparator = rootValueSeparator;
/*  49 */     this._separators = DEFAULT_SEPARATORS;
/*     */   }
/*     */   
/*     */   public void setRootValueSeparator(String sep) {
/*  53 */     this._rootValueSeparator = sep;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MinimalPrettyPrinter setSeparators(Separators separators) {
/*  64 */     this._separators = separators;
/*  65 */     return this;
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
/*     */   public void writeRootValueSeparator(JsonGenerator g) throws IOException {
/*  77 */     if (this._rootValueSeparator != null) {
/*  78 */       g.writeRaw(this._rootValueSeparator);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeStartObject(JsonGenerator g) throws IOException {
/*  85 */     g.writeRaw('{');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void beforeObjectEntries(JsonGenerator g) throws IOException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
/* 104 */     g.writeRaw(this._separators.getObjectFieldValueSeparator());
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
/*     */   public void writeObjectEntrySeparator(JsonGenerator g) throws IOException {
/* 117 */     g.writeRaw(this._separators.getObjectEntrySeparator());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
/* 123 */     g.writeRaw('}');
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeStartArray(JsonGenerator g) throws IOException {
/* 129 */     g.writeRaw('[');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void beforeArrayValues(JsonGenerator g) throws IOException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeArrayValueSeparator(JsonGenerator g) throws IOException {
/* 148 */     g.writeRaw(this._separators.getArrayValueSeparator());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
/* 154 */     g.writeRaw(']');
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/util/MinimalPrettyPrinter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */